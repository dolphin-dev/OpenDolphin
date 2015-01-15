package open.dolphin.msg;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import open.dolphin.infomodel.ActivityModel;
import open.dolphin.session.AccountSummary;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

public class OidSender {
    
    private static final String GMAIL_ACCOUNT = "kazushi.minagawa@gmail.com";
    private static final String GMAIL_PASSWD = "hanagui++";
    private static final String MAIL_ENC = "ISO-2022-JP";
//s.oh^ 2014/07/08 クラウド0対応
    //private static final String DOLPHIN_EMAIL_ADDRESS = "dolphin@digital-globe.co.jp";
    //private static final String ACCOUNT_MAKING_RESULT = "OpenDolphinアカウント作成のお知らせ";
    private static final String DG_DOLPHIN_EMAIL_ADDRESS = "dolphin@digital-globe.co.jp";
    private static final String ACTIVITY_RESULT = "【使用量レポート】";
//s.oh$
    private static final String DOLPHIN_EMAIL_ADDRESS = "dolphin@lscc.co.jp";
//s.oh^ 2014/07/08 クラウド0対応
    //private static final String ACCOUNT_MAKING_RESULT = "DolphinProアカウント作成のお知らせ";
    private static final String ACCOUNT_MAKING_RESULT = "OpenDolphinアカウント作成のお知らせ";
//s.oh$
    private static final String MEMBER_TEMPLATE = "member-mail.vm";
    private static final String TESTER_TEMPLATE = "account-mail.vm";
    private static final String TEMPLATE_ENC = "SHIFT_JIS";
    private static final String OBJECT_NAME = "account";
    private static final String ASP_TESTER = "ASP_TESTER";
    private static final String ASP_MEMBER = "ASP_MEMBER";


    public void send(AccountSummary account) {

        try {

            VelocityContext context = VelocityHelper.getContext();
            context.put(OBJECT_NAME, account);
            StringWriter sw = new StringWriter();
//s.oh^ 2014/07/08 クラウド0対応
            //BufferedWriter bw = new BufferedWriter(sw);
            //
            //// ライセンスのタイプでテンプレートを選択する
            //if (account.getMemberType().equals(ASP_TESTER)) {
            //    Velocity.mergeTemplate(TESTER_TEMPLATE, TEMPLATE_ENC, context, bw);
            //} else if (account.getMemberType().equals(ASP_MEMBER)) {
            //    Velocity.mergeTemplate(MEMBER_TEMPLATE, TEMPLATE_ENC, context, bw);
            //}
            //
            //bw.flush();
            //bw.close();
            //String body = sw.toString();
            //
            //Properties props = new Properties();
            //// gmail -------------------------------
            ///*props.put("mail.smtp.host", "smtp.gmail.com");
            //props.put("mail.smtp.socketFactory.port", "465");
            //props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
            //props.put("mail.smtp.auth", "true");
            //props.put("mail.smtp.port", "465");*/
            //
            //props.put("mail.smtp.host", "mails.lscc.co.jp");
            ////props.put("mail.smtp.socketFactory.port", "465");
            ////props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
            //props.put("mail.smtp.auth", "true");
            //props.put("mail.smtp.port", "25");
            //// id: dolphin
            //// pass: dolphin
            ////--------------------------------------
            try (BufferedWriter bw = new BufferedWriter(sw)) {
                // ライセンスのタイプでテンプレートを選択する
                if (account.getMemberType().equals(ASP_TESTER)) {
                    Velocity.mergeTemplate(TESTER_TEMPLATE, TEMPLATE_ENC, context, bw);
                } else if (account.getMemberType().equals(ASP_MEMBER)) {
                    Velocity.mergeTemplate(MEMBER_TEMPLATE, TEMPLATE_ENC, context, bw);
                }
                
                bw.flush();
            }
            String body = sw.toString();

            Properties props = this.getMailProperties();
//s.oh$

            Session session = Session.getDefaultInstance(props,
                    new javax.mail.Authenticator() {

                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            //return new PasswordAuthentication(GMAIL_ACCOUNT,GMAIL_PASSWD);
                            return new PasswordAuthentication("dolphin","dolphin");
                        }
                    });


            javax.mail.internet.MimeMessage mimeMessage = new javax.mail.internet.MimeMessage(session);

            mimeMessage.setFrom(new InternetAddress(DOLPHIN_EMAIL_ADDRESS));
            mimeMessage.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(account.getUserEmail()));
            mimeMessage.addRecipients(RecipientType.BCC, DOLPHIN_EMAIL_ADDRESS);

            mimeMessage.setSubject(ACCOUNT_MAKING_RESULT, MAIL_ENC);
            mimeMessage.setText(body, MAIL_ENC);

            Transport.send(mimeMessage);
           

        } catch (Exception e) {
            e.printStackTrace(System.err);
            Logger.getLogger("open.dolphin").warning(e.getMessage());
        }
    }
    
//s.oh^ 2014/07/08 クラウド0対応
    public void sendActivity(ActivityModel ams[]) {
        
        ActivityModel am = ams[0];
        ActivityModel total = ams[1];
        
        Properties config = new Properties();
        StringBuilder path = new StringBuilder();
        path.append(System.getProperty("jboss.home.dir"));
        path.append(File.separator);
        path.append("custom.properties");
        File f = new File(path.toString());
        try {
            FileInputStream fin = new FileInputStream(f);
            InputStreamReader r = new InputStreamReader(fin, "JISAutoDetect");
            config.load(r);
            r.close();
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
            throw new RuntimeException(ex.getMessage());
        }
        String mailFrom = config.getProperty("cloud.zero.mail.from");
        if(mailFrom == null || mailFrom.length() <= 0) {
            mailFrom = DOLPHIN_EMAIL_ADDRESS;
        }
        String mailTo = config.getProperty("cloud.zero.mail.to");
        if(mailTo == null || mailTo.length() <= 0) {
            mailTo = DOLPHIN_EMAIL_ADDRESS;
        }
        final String account = config.getProperty("cloud.zero.mail.account");
        final String password = config.getProperty("cloud.zero.mail.password");

        try {
            // 本文
            StringBuilder sb = new StringBuilder();
            sb.append("集計期間=").append(targetDateFromDate(am.getFromDate())).append("~").append(targetDateFromDate(am.getToDate())).append("\n");
            sb.append("------------------------------------").append("\n");
            sb.append("医療機関ID=").append(total.getFacilityId()).append("\n");
            sb.append("医療機関名=").append(total.getFacilityName()).append("\n");
            sb.append("郵便番号=").append(total.getFacilityZip()).append("\n");
            sb.append("住所=").append(total.getFacilityAddress()).append("\n");
            sb.append("電話=").append(total.getFacilityTelephone()).append("\n");
            sb.append("FAX=").append(total.getFacilityFacimile()).append("\n");
            sb.append("利用者数=").append(total.getNumOfUsers()).append("\n");
            sb.append("************************************").append("\n");
            sb.append("患者登録数= ").append(formatNumber(am.getNumOfPatients())).append(" / ").append(formatNumber(total.getNumOfPatients())).append("\n");
            sb.append("来院数= ").append(formatNumber(am.getNumOfPatientVisits())).append(" / ").append(formatNumber(total.getNumOfPatientVisits())).append("\n");
            sb.append("病名数= ").append(formatNumber(am.getNumOfDiagnosis())).append(" / ").append(formatNumber(total.getNumOfDiagnosis())).append("\n");
            sb.append("カルテ枚数= ").append(formatNumber(am.getNumOfKarte())).append(" / ").append(formatNumber(total.getNumOfKarte())).append("\n");
            sb.append("画像数= ").append(formatNumber(am.getNumOfImages())).append(" / ").append(formatNumber(total.getNumOfImages())).append("\n");
            sb.append("添付文書数= ").append(formatNumber(am.getNumOfAttachments())).append(" / ").append(formatNumber(total.getNumOfAttachments())).append("\n");
            sb.append("紹介状数= ").append(formatNumber(am.getNumOfLetters())).append(" / ").append(formatNumber(total.getNumOfLetters())).append("\n");
            sb.append("検査数= ").append(formatNumber(am.getNumOfLabTests())).append(" / ").append(formatNumber(total.getNumOfLabTests())).append("\n");
            sb.append("************************************").append("\n");
            sb.append("データベース容量= ").append(total.getDbSize()).append("\n");
            sb.append("IPアドレス= ").append(total.getBindAddress()).append("\n");
            sb.append("\n");
            sb.append("*** 集計期間数/総数 を表示").append("\n");
            String body = sb.toString();

            // Session
            //Properties props = this.getGmailProperties();
            Properties props = new Properties();
            props.put("mail.smtp.host", config.getProperty("cloud.zero.mail.host"));
            props.put("mail.smtp.auth", config.getProperty("cloud.zero.mail.auth"));
            props.put("mail.smtp.port", config.getProperty("cloud.zero.mail.port"));
            Session session = Session.getDefaultInstance(props,
                    new javax.mail.Authenticator() {

                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            //return new PasswordAuthentication(GMAIL_ACCOUNT,GMAIL_PASSWD);
                            return new PasswordAuthentication(account, password);
                            //return new PasswordAuthentication("dolphin","dolphin");
                        }
                    });
            
            // Message
            javax.mail.internet.MimeMessage mimeMessage = new javax.mail.internet.MimeMessage(session);

            // 差出人
            mimeMessage.setFrom(new InternetAddress(mailFrom));
            
            // 宛先
            InternetAddress[] addressTo = new InternetAddress[1];
            addressTo[0] = new InternetAddress(mailTo);
            mimeMessage.setRecipients(javax.mail.Message.RecipientType.TO, addressTo);
            
            // BCC
            mimeMessage.addRecipients(RecipientType.BCC, DG_DOLPHIN_EMAIL_ADDRESS);

            // Title
            sb = new StringBuilder();
            //sb.append(ACTIVITY_RESULT).append(this.reportDateFromDate(am.getFromDate())).append("-").append(am.getFacilityName());
            sb.append(ACTIVITY_RESULT).append(this.reportDateFromDate(am.getFromDate())).append("-").append(total.getFacilityName());
            mimeMessage.setSubject(sb.toString(), MAIL_ENC);
            
            mimeMessage.setText(body, MAIL_ENC);

            Transport.send(mimeMessage);
           
        } catch (ResourceNotFoundException | ParseErrorException | MethodInvocationException | MessagingException e) {
            e.printStackTrace(System.err);
            Logger.getLogger("open.dolphin").warning(e.getMessage());
        }
    }
    
    private Properties getGmailProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        return props;
    }
    
    private Properties getMailProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.host", "mails.lscc.co.jp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "25");
        return props;
    }
    
    private String targetDateFromDate(Date d) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy'年'MM'月'dd'日'");
        return sdf.format(d);
    }
    
    private String reportDateFromDate(Date d) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy'年'MM'月'");
        return sdf.format(d);
    }
    
    private String formatNumber(long num) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        return nf.format(num);
    }
//s.oh$
}
