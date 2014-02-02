package open.dolphin.ejb;

import javax.mail.*;
import javax.mail.internet.*;

import java.util.*;

/**
 * TextEmail defines utility methods for the JavaMail API, which provides
 * a platform independent and protocol independent framework to build Java
 * technology-based mail and messaging applications.
 *
 * @author Tom Marrs
 *
 */
public class TextEmail {
    private static final String SMTP = "smtp";
    private static final String MAIL_TRANSPORT_PROTOCOL = "mail.transport.protocol";
    private static final String MAIL_SMTP_HOST = "mail.smtp.host";
    private static final String MAIL_SMTP_PORT = "mail.smtp.port";
    private static final String MAIL_DEBUG = "mail.debug";
    
    private static final String INVALID_ADDRESS_MSG = "Email Address does not conform to RFC 822 standard";
    
    private Session session;
    private InternetAddress sender = new InternetAddress();
    private String subject = new String();
    private StringBuffer body = new StringBuffer();
    private List recipients = new ArrayList();
    private List ccRecipients = new ArrayList();
    private List bccRecipients = new ArrayList();
    private Date sentDate;
    
    public TextEmail() throws EmailException {
        setSession(getDefaultSession());
    }
    
    public TextEmail(Session session) throws EmailException {
        setSession(session);
    }
    
    public TextEmail(String sender, String subject, String body, List recipients) throws EmailException {
        this();
        setSender(sender);
        setSubject(subject);
        setBody(body);
        setRecipients(recipients);
    }
    
    public TextEmail(String sender, String subject, String body,
            List recipients, List ccRecipients) throws EmailException {
        this(sender, subject, body, recipients);
        setCcRecipients(ccRecipients);
    }
    
    public String getSender() {
        return sender.toString();
    }
    
    public void setSender(String sender) throws EmailException {
        this.sender = TextEmail.makeInternetAddress(sender);
    }
    
    private static InternetAddress makeInternetAddress(String emailAddress) throws EmailException {
        InternetAddress internetAddress = null;
        
        try {
            internetAddress = new InternetAddress(emailAddress);
            internetAddress.validate();
        } catch (AddressException ae) {
            throw new EmailException(TextEmail.INVALID_ADDRESS_MSG + " " + emailAddress + ":\n" + ae);
        }
        
        return internetAddress;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public String getBody() {
        return body.toString();
    }
    
    public void setBody(String body) throws IllegalArgumentException {
        this.body = new StringBuffer(body);
    }
    
    public List getRecipients() {
        return TextEmail.internetAddressesToStrings(recipients);
    }
    
    /**
     * @return
     */
    private static List internetAddressesToStrings(List internetAddressRecipients) {
        List stringRecipients = new ArrayList();
        Iterator internetAddressRecipientsIter = internetAddressRecipients.iterator();
        
        while (internetAddressRecipientsIter.hasNext()) {
            InternetAddress internetAddress = (InternetAddress) internetAddressRecipientsIter.next();
            
            stringRecipients.add(internetAddress.toString());
        }
        
        return stringRecipients;
    }
    
    public void addRecipient(String recipient) throws EmailException {
        recipients.add(TextEmail.makeInternetAddress(recipient));
    }
    
    public void setRecipients(List recipients) throws EmailException {
        TextEmail.setRecipients(this.recipients, recipients);
    }
    
    private static void setRecipients(List dest, List src) throws EmailException {
        dest.clear();
        
        dest.addAll(TextEmail.stringsToInternetAddresses(src));
    }
    
    /**
     * @return
     * @throws EmailException
     */
    private static List stringsToInternetAddresses(List stringRecipients) throws EmailException {
        List internetAddressRecipients = new ArrayList();
        Iterator stringRecipientsIter = stringRecipients.iterator();
        
        while (stringRecipientsIter.hasNext()) {
            String address = (String) stringRecipientsIter.next();
            
            internetAddressRecipients.add(TextEmail.makeInternetAddress(address));
        }
        
        return internetAddressRecipients;
    }
    
    public List getCcRecipients() {
        return TextEmail.internetAddressesToStrings(ccRecipients);
    }
    
    public void addCcRecipient(String recipient) throws EmailException {
        ccRecipients.add(TextEmail.makeInternetAddress(recipient));
    }
    
    public void setCcRecipients(List ccRecipients) throws EmailException {
        TextEmail.setRecipients(this.ccRecipients, ccRecipients);
    }
    
    public boolean hasCcRecipients() {
        return (ccRecipients != null && !ccRecipients.isEmpty());
    }
    public List getBccRecipients() {
        return TextEmail.internetAddressesToStrings(bccRecipients);
    }
    
    public void addBccRecipient(String recipient) throws EmailException {
        bccRecipients.add(TextEmail.makeInternetAddress(recipient));
    }
    
    public void setBccRecipients(List bccRecipients) throws EmailException {
        TextEmail.setRecipients(this.bccRecipients, bccRecipients);
    }
    
    public boolean hasBccRecipients() {
        return (bccRecipients != null && !bccRecipients.isEmpty());
    }
    
    public void setSentDate(Date sentDate) throws IllegalArgumentException {
        this.sentDate = sentDate;
    }
    
    public Date getSentDate() {
        if (sentDate == null) {
            sentDate = new Date();
        }
        
        return sentDate;
    }
    
    private Session getDefaultSession() throws EmailException {
        Properties props = new Properties(),
                sysProps = System.getProperties();
        
        String mailSmtpHost = sysProps.getProperty(TextEmail.MAIL_SMTP_HOST),
                mailSmtpPort = sysProps.getProperty(TextEmail.MAIL_SMTP_PORT),
                mailDebug = sysProps.getProperty(TextEmail.MAIL_DEBUG, "false");
        
        props.setProperty(TextEmail.MAIL_TRANSPORT_PROTOCOL, SMTP);
        props.setProperty(TextEmail.MAIL_SMTP_HOST, mailSmtpPort);
        
        props.setProperty(TextEmail.MAIL_SMTP_HOST, mailSmtpHost);
        props.setProperty(TextEmail.MAIL_DEBUG, mailDebug);
        
        // Get the default Session using Properties.
        Session session = Session.getDefaultInstance(props);
        
        return session;
    }
    
    /**
     * @return Returns the session.
     */
    public Session getSession() {
        return session;
    }
    
    /**
     * @param session The session to set.
     */
    public void setSession(Session session) {
        this.session = session;
    }
    
    /**
     * This method sends an email message using the JavaMail API.
     *
     * @param mailMessage The email message to send.
     */
    public void send() throws EmailException {
        
        try {
            InternetAddress[] recipientsArr = (InternetAddress[]) recipients.toArray(new InternetAddress[0]);
            
            // Create a New message.
            
            MimeMessage msg = new MimeMessage(session);
            
            // Set the "From" address.
            msg.setFrom(sender);
            
            // Set the "To recipients" addresses.
            msg.setRecipients(Message.RecipientType.TO, recipientsArr);
            
            if (hasCcRecipients()) {
                InternetAddress[] ccRecipientsArr = (InternetAddress[]) ccRecipients.toArray(new InternetAddress[0]);
                
                // Cc Recipients are optional.
                // Set the "Cc recipients" addresses.
                msg.setRecipients(Message.RecipientType.CC, ccRecipientsArr);
            }
            
            if (hasBccRecipients()) {
                InternetAddress[] bccRecipientsArr = (InternetAddress[])
                bccRecipients.toArray(new InternetAddress[0]);
                
                // Bcc Recipients are optional.
                // Set the "Bcc recipients" addresses.
                msg.setRecipients(Message.RecipientType.BCC, bccRecipientsArr);
            }
            
            // Set the Subject.
            msg.setSubject(subject);
            
            // Set the Text.
            msg.setText(body.toString());
            
            // Set the sent date.
            msg.setSentDate(getSentDate());
            
            // Send the message.
            Transport.send(msg);
        } catch (MessagingException me) {
            throw new EmailException(me);
        }
    }
    
}
