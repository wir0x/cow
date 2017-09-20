package ch.swissbytes.module.shared.notifications.mail.template.passwordrecovery;

import ch.swissbytes.module.buho.app.user.model.User;
import ch.swissbytes.module.shared.notifications.mail.MailObserver;
import ch.swissbytes.module.shared.notifications.mail.configuration.Mail;
import ch.swissbytes.module.shared.utils.AppConfiguration;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Calendar;

public class EmailSenderPasswordRecovery implements Serializable {

    @Inject
    private Logger log;

    @Inject
    private AppConfiguration labels;

    @Inject
    private MailObserver mailObserver;

    public String getResource() {
        String result = "";
        try {
            InputStream in = EmailSenderPasswordRecovery.class.getResourceAsStream("/email-template/forgotPassword.html");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "iso-8859-1"));
            String line = "";
            while ((line = bufferedReader.readLine()) != null)
                result += line;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    protected String rpl(String string, String emailKey, String msgKey) {
        if (string.indexOf(emailKey) > 0) {
            return string.replace("{" + emailKey + "}", msgKey);
        } else {
            return string;
        }
    }

    public void sendMail(String to, String content) {
        Mail mail = new Mail();
        mail.setToAddress(to);
        mail.setSubject("Reset Passsword");
        mail.setContent(content);
        mailObserver.sendMail(mail);
    }

    public String getYear() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        return String.valueOf(year);
    }

    public String createMimeMultipart(final String link, final User user) {
        String content = getResource();
        content = rpl(content, "userName", user.getName().toUpperCase());
        content = rpl(content, "rootURL", link);
        content = rpl(content, "CurrentDate", getYear());
        content = rpl(content, "imageLogo", "https://app.buho.bo/img/logo-login.png");
        return content;
    }
}
