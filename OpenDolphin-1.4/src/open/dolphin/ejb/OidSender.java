package open.dolphin.ejb;

import java.io.BufferedWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

@MessageDriven(activationConfig =
{
    @ActivationConfigProperty(propertyName="destinationType", propertyValue="javax.jms.Queue"),
    @ActivationConfigProperty(propertyName="destination", propertyValue="queue/openDolphin/account")
})
public class OidSender implements MessageListener {
    
    static {
        try {
            Velocity.init();
        } catch (Exception e) {
        }
    }
    
    private static final String JAVAMAIL_SESSION = "java:/Mail";
    private static final String DOLPHIN_EMAIL_ADDRESS = "dolphin@digital-globe.co.jp";
    private static final String ACCOUNT_MAKING_RESULT = "Opendolphinアカウント作成のお知らせ";
    private static final String MEMBER_TEMPLATE = "member-mail.vm";
    private static final String TESTER_TEMPLATE = "account-mail.vm";
    private static final String TEMPLATE_ENC = "SHIFT_JIS";
    private static final String OBJECT_NAME = "account";
    private static final String ASP_TESTER = "ASP_TESTER";
    private static final String ASP_MEMBER = "ASP_MEMBER";
    
    public static javax.mail.Session getJavaMailSession(String javaMailSessionJndiName) throws ServiceLocatorException {
        
        javax.mail.Session javaMailSession = null;
        
        try {
            Context ctx = new InitialContext();
            javaMailSession = (javax.mail.Session) ctx.lookup(javaMailSessionJndiName);
            
        } catch (ClassCastException cce) {
            throw new ServiceLocatorException(cce);
            
        } catch (NamingException ne) {
            throw new ServiceLocatorException(ne);
        }
        return javaMailSession;
    }
    
    
    @SuppressWarnings("unchecked")
    public void onMessage(Message message) {
        
        javax.mail.Session javaMailSession = null;
        
        try {
            if (message instanceof ObjectMessage) {
                
                ObjectMessage objMessage = (ObjectMessage)message;
                Object obj = objMessage.getObject();
                
                if (obj instanceof AccountSummary) {
                    
                    AccountSummary account = (AccountSummary) obj;
                    
                    VelocityContext context = new VelocityContext();
                    context.put(OBJECT_NAME, account);
                    StringWriter sw = new StringWriter();
                    BufferedWriter bw = new BufferedWriter(sw);
                    
                    // ライセンスのタイプでテンプレートを選択する
                    if (account.getMemberType().equals(ASP_TESTER)) {
                        Velocity.mergeTemplate(TESTER_TEMPLATE, TEMPLATE_ENC, context, bw);
                    } else if (account.getMemberType().equals(ASP_MEMBER)) {
                        Velocity.mergeTemplate(MEMBER_TEMPLATE, TEMPLATE_ENC, context, bw);
                    }
                    
                    bw.flush();
                    bw.close();
                    String body = sw.toString();
                    
                    javaMailSession = getJavaMailSession(JAVAMAIL_SESSION);
                    TextEmail email = new TextEmail(javaMailSession);
                    email.setSender(DOLPHIN_EMAIL_ADDRESS);
                    email.setSubject(ACCOUNT_MAKING_RESULT);
                    email.addRecipient(account.getUserEmail());
                    ArrayList list = new ArrayList(1);
                    list.add(DOLPHIN_EMAIL_ADDRESS);
                    email.setBccRecipients(list);
                    email.setBody(body);
                    email.send();
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
