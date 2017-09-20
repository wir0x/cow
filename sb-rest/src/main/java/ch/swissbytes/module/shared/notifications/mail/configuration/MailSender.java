package ch.swissbytes.module.shared.notifications.mail.configuration;

import ch.swissbytes.domain.enumerator.PaymentStatusEnum;
import ch.swissbytes.module.buho.app.notification.model.Notification;
import ch.swissbytes.module.buho.service.configuration.ConfigurationKey;
import ch.swissbytes.module.buho.service.configuration.KeyAppConfiguration;
import ch.swissbytes.module.shared.exception.InvalidInputException;
import ch.swissbytes.module.shared.utils.StringUtil;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.hibernate.annotations.common.util.impl.LoggerFactory;
import org.jboss.logging.Logger;

import javax.ejb.Stateful;
import javax.mail.internet.InternetAddress;
import java.util.Date;

@Stateful
public class MailSender extends HtmlEmail {

    private static final Logger log = LoggerFactory.logger(MailSender.class);

    private Mail emailFields;

    public MailSender(Mail fields) {
        this.emailFields = fields;

        final String hostName = KeyAppConfiguration.getString(ConfigurationKey.SMTP_SERVER);
        final String loginName = KeyAppConfiguration.getString(ConfigurationKey.SMTP_USERNAME);
        final String loginPW = KeyAppConfiguration.getString(ConfigurationKey.SMTP_PASSWORD);
        final String sender = KeyAppConfiguration.getString(ConfigurationKey.SMTP_SENDER_ADDRESS);
        final String senderName = KeyAppConfiguration.getString(ConfigurationKey.SMTP_SENDER_NAME);
        final int port = KeyAppConfiguration.getInt(ConfigurationKey.SMTP_PORT);
        final boolean ssl = KeyAppConfiguration.getBoolean(ConfigurationKey.SMTP_SSL);

        //load email config
        setHostName(hostName);
        setAuthentication(loginName, loginPW);
        setSmtpPort(port);
        setSSL(ssl);

        emailFields.setFromAddress(sender);
        emailFields.setFromName(senderName);
    }

    public MailSender() {

        String hostName = KeyAppConfiguration.getString(ConfigurationKey.SMTP_SERVER);
        String loginName = KeyAppConfiguration.getString(ConfigurationKey.SMTP_USERNAME);
        String loginPW = KeyAppConfiguration.getString(ConfigurationKey.SMTP_PASSWORD);
        String sender = KeyAppConfiguration.getString(ConfigurationKey.SMTP_SENDER_ADDRESS);
        String senderName = KeyAppConfiguration.getString(ConfigurationKey.SMTP_SENDER_NAME);
        int port = KeyAppConfiguration.getInt(ConfigurationKey.SMTP_PORT);
        boolean ssl = KeyAppConfiguration.getBoolean(ConfigurationKey.SMTP_SSL);

        //load email config
        setHostName(hostName);
        setAuthentication(loginName, loginPW);
        setSmtpPort(port);
        setSSL(ssl);

        emailFields = new Mail();

        emailFields.setFromAddress(sender);
        emailFields.setFromName(senderName);
    }

    public void sendEmail() {
        log.info("Start processing send Email");
        try {
            setFrom(emailFields.getFromAddress(), emailFields.getFromName());
            setToAddresses(emailFields.getToAddress());
            setSubject(emailFields.getSubject());
            setHtmlMsg(emailFields.getContent());
            executeSend();
            try {
                Thread.sleep(250);// 3 s
            } catch (Exception e) {
            }
        } catch (EmailException e) {
            log.error(e.getMessage(), e);
        }
        log.info("End processing send Email");
    }

    public Notification sendEmail(Notification notification) {
        log.info("Start processing send Email");
        try {
            if (StringUtil.isEmpty(notification.getToAddress())) {
                throw new InvalidInputException("there is no to email address");
            }

            if (StringUtil.isNotEmpty(notification.getFromName())) {
                emailFields.setFromName(notification.getFromName());
            }

            setFrom(emailFields.getFromAddress(), emailFields.getFromName());

            setToAddresses(notification.getToAddress());
            setSubject(notification.getSubject());
            setHtmlMsg(notification.getContent());
            executeSend();
            try {
                Thread.sleep(150);// 3 s
            } catch (Exception e) {
            }

            notification.setStatus(PaymentStatusEnum.PROCESSED);
        } catch (InvalidInputException | EmailException e) {
            log.error(e.getMessage(), e);
            notification.setStatus(PaymentStatusEnum.ERROR);
            notification.setErrorDescription(e.getMessage());
        }
        log.info("End processing send Email");
        notification.setSentDate(new Date());
        return notification;
    }

    private void executeSend() {
        try {
            Long start = System.currentTimeMillis();
            log.info("----------- SendMail  ------------");
            log.info("From: " + getFromAddress());
            log.info("To: " + toList);
            log.info("Subject: " + getSubject());
            log.info("getHostName: " + getHostName());
            log.info("getSmtpPort: " + getSmtpPort());
            log.info("isSSL: " + isSSL());
            setCharset("utf-8");
            send();
            Long end = System.currentTimeMillis();
            log.info("----------- SendMail  ------------");
            log.info("From: " + getFromAddress());
            log.info("To: " + toList);
            log.info("Start at: " + start);
            log.info("End at: " + end);
            log.info("time sec: " + (end - start) / 1000);
            log.info("----------------------------------");
        } catch (EmailException e) {
            log.error("Mail not sent", e);
        }
    }

    private void setToAddresses(String recipients) {
        String[] split = recipients.split("(;)|(\\,)|(\\r?\\n)");
        for (int i = 0; i < split.length; i++) {
            try {
                new InternetAddress(split[i]);
                addTo(split[i]);
            } catch (Exception e) {
                log.warn("Error in address" + split[i], e);
            }
        }
    }
}
