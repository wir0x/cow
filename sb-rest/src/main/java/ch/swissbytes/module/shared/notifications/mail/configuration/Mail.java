package ch.swissbytes.module.shared.notifications.mail.configuration;

import java.io.Serializable;

public class Mail implements Serializable {

    private String fromName;

    private String fromAddress;

    private String toAddress;

    private String subject;

    private String content;

    public Mail() {
    }

    /**
     * @param fromName
     * @param fromAddress
     * @param toAddress
     * @param subject
     * @param content
     * @author jorge
     */
    public Mail(String fromName, String fromAddress, String toAddress, String subject, String content) {
        super();
        this.fromName = fromName;
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.subject = subject;
        this.content = content;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }
}
