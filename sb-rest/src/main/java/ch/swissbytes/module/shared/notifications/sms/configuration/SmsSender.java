package ch.swissbytes.module.shared.notifications.sms.configuration;

import ch.swissbytes.domain.dao.SmsControlDao;
import ch.swissbytes.domain.entities.SmsControl;
import ch.swissbytes.domain.enumerator.PaymentStatusEnum;
import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.buho.app.notification.model.Notification;
import ch.swissbytes.module.buho.app.position.repository.PositionRepository;
import ch.swissbytes.module.buho.app.subscription.model.Subscription;
import ch.swissbytes.module.buho.app.subscription.repository.SubscriptionRepository;
import ch.swissbytes.module.buho.service.SmsControlService;
import ch.swissbytes.module.buho.service.SmsLimitService;
import ch.swissbytes.module.buho.service.configuration.ConfigurationKey;
import ch.swissbytes.module.buho.service.configuration.KeyAppConfiguration;
import ch.swissbytes.module.buho.service.notifications.NotificationService;
import ch.swissbytes.module.shared.exception.InvalidInputException;
import ch.swissbytes.module.shared.persistence.Optional;
import ch.swissbytes.module.shared.utils.DateUtil;
import ch.swissbytes.module.shared.utils.StringUtil;
import org.jboss.logging.Logger;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.UUID;

@Stateless
public class SmsSender implements Serializable {

    @Inject
    private Logger log;
    @Inject
    private PositionRepository positionRepository;
    @Inject
    private SmsControlDao smsControlDao;
    @Inject
    private SmsControlService smsControlService;
    @Inject
    private SubscriptionRepository subscriptionRepository;
    @Inject
    private SmsLimitService smsLimitService;
    @Inject
    private NotificationService notificationService;

    private static String createXml(UUID productToken, String sender, String recipient, String message) {
        try {

            ByteArrayOutputStream xml = new ByteArrayOutputStream();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);

            // Get the DocumentBuilder
            DocumentBuilder docBuilder = factory.newDocumentBuilder();

            // Create blank DOM Document
            DOMImplementation impl = docBuilder.getDOMImplementation();
            Document doc = impl.createDocument(null, "MESSAGES", null);

            // create the root element
            Element root = doc.getDocumentElement();
            Element authenticationElement = doc.createElement("AUTHENTICATION");
            Element productTokenElement = doc.createElement("PRODUCTTOKEN");
            authenticationElement.appendChild(productTokenElement);
            Text productTokenValue = doc.createTextNode("" + productToken);
            productTokenElement.appendChild(productTokenValue);
            root.appendChild(authenticationElement);

            Element msgElement = doc.createElement("MSG");
            root.appendChild(msgElement);

            Element fromElement = doc.createElement("FROM");
            Text fromValue = doc.createTextNode(sender);
            fromElement.appendChild(fromValue);
            msgElement.appendChild(fromElement);

            Element bodyElement = doc.createElement("BODY");
            Text bodyValue = doc.createTextNode(message);
            bodyElement.appendChild(bodyValue);
            msgElement.appendChild(bodyElement);

            Element toElement = doc.createElement("TO");
            Text toValue = doc.createTextNode(recipient);
            toElement.appendChild(toValue);
            msgElement.appendChild(toElement);

            TransformerFactory tranFactory = TransformerFactory.newInstance();
            Transformer aTransformer = tranFactory.newTransformer();
            aTransformer.setOutputProperty(OutputKeys.INDENT, "yes");

            Source src = new DOMSource(doc);
            Result dest = new StreamResult(xml);
            aTransformer.transform(src, dest);

            return xml.toString();

        } catch (TransformerException ex) {
            System.err.println(ex);
            return ex.toString();
        } catch (ParserConfigurationException p) {
            System.err.println(p);
            return p.toString();
        }
    }

    private static String doHttpPost(String urlString, String requestString) {
        try {
            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(requestString);
            wr.flush();
            // Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            String response = "";
            while ((line = rd.readLine()) != null) {
                response += line;
            }
            wr.close();
            rd.close();

            return response;
        } catch (IOException ex) {
            System.err.println(ex);
            return ex.toString();
        }
    }

    private static String validateMobile(String phoneNumber) throws InvalidInputException {
        //only numbers
        String mobile = phoneNumber.replaceAll("[^\\d]", "");

        if (mobile.startsWith("591")) {
            mobile = mobile.replace("591", "");
        }

        mobile = "00591" + mobile;

        if (mobile.length() != 13) {
            throw new InvalidInputException("número incorrecto " + phoneNumber);
        }

        return mobile.toString();
    }

    public Notification sendSms(Notification notification) {
        String smsToken = KeyAppConfiguration.getString(ConfigurationKey.SMS_TOKEN);
        String smsSender = KeyAppConfiguration.getString(ConfigurationKey.SMS_SENDER);
        String smsUrl = KeyAppConfiguration.getString(ConfigurationKey.SMS_URL);
        try {
            if (StringUtil.isEmpty(notification.getToAddress())) {
                throw new InvalidInputException("there is no to number");
            }

            Long testCompanyId = KeyAppConfiguration.getLong(ConfigurationKey.COMPANY_TEST_ID);
            if (notification.getAccountId().equals(testCompanyId)) {
                throw new InvalidInputException("Testing account can't send sms");
            }

            validationSmsCredit(notification);

            Long start = System.currentTimeMillis();
            log.info("----------- SendSms  ------------");
            log.info("From: " + smsSender);
            log.info("To: " + notification.getToAddress());
            log.info("Message: " + notification.getContent());
            String mobile = validateMobile(notification.getToAddress());
            UUID productToken = UUID.fromString(smsToken);
            String xml = createXml(productToken, smsSender, mobile, notification.getContent());
            String response = doHttpPost(smsUrl, xml);
            Long end = System.currentTimeMillis();
            log.info("----------------------------------" + (end - start));
            if (!response.isEmpty()) {
                log.error(" invalid input");
                throw new InvalidInputException(response);
            }

            try {
                Thread.sleep(250);// 3 s
            } catch (Exception e) {
            }
            notification.setStatus(PaymentStatusEnum.PROCESSED);
        } catch (RuntimeException e) {
            log.warn(e);
            notification.setStatus(PaymentStatusEnum.ERROR);
            notification.setErrorDescription(e.getMessage());
        }

        notification.setSentDate(new Date());
        return notification;

    }

    private void validationSmsCredit(Notification notification) throws RuntimeException {
        //TODO validate if exist devices

        Optional<SmsControl> smsControlOptional = smsControlDao.getCurrentByDeviceId(notification.getDeviceId());

        SmsControl smsControl;

        if (smsControlOptional.isAbsent()) {
            smsControl = SmsControl.createNew();
            smsControl.setDevice(new Device(notification.getDeviceId()));
            smsControl.setMonthYear(DateUtil.getFirstDayOfMonth());

            Optional<Subscription> subscriptionOptional = subscriptionRepository.getActiveByDeviceId(notification.getDeviceId());

            if (subscriptionOptional.isPresent()) {
                smsControl.setMaxSms(subscriptionOptional.get().getMaxSms());
            } else {
                smsControl.setMaxSms(KeyAppConfiguration.getInt(ConfigurationKey.SMS_MAX_LIMIT));
            }
            smsControl = smsControlDao.save(smsControl);
        } else {
            smsControl = smsControlOptional.get();
        }

        if (smsControl.getUsedSms() + 1 > smsControl.getMaxSms()) {
            throw new InvalidInputException("The device company has no credit: " + notification.getDeviceId());
        }

        smsControl.setUsedSms(smsControl.getUsedSms() + 1);

        if (smsLimitService.createSmsLimitNotification(smsControl, notification)) {
            smsControl.setSentMail(true);
        }

        smsControlService.store(smsControl);
    }
}
