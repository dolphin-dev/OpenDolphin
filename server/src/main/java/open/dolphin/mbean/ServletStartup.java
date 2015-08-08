package open.dolphin.mbean;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.Timer;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.inject.Inject;
import open.dolphin.session.ChartEventServiceBean;
import open.dolphin.session.SystemServiceBean;
import open.orca.rest.ORCAConnection;
//import open.dolphin.updater.Updater;

/**
 * スタートアップ時にUpdaterとStateServiceBeanを自動実行
 * @author masuda, Masuda Naika
 */
@Singleton
@Startup
public class ServletStartup {
    
private static final Logger logger = Logger.getLogger(ServletStartup.class.getSimpleName());

    @Inject
    private ChartEventServiceBean eventServiceBean;
    
//s.oh^ 2014/07/08 クラウド0対応
    @Inject
    private SystemServiceBean systemServiceBean;
//s.oh$
    
//    @Inject
//    private Updater updater;

    @PostConstruct
    public void init() {
//        updater.start();
        eventServiceBean.start();
    }

    @PreDestroy
    public void stop() {
    }

    // 日付が変わったらpvtListをクリアしクライアントに伝える
    @Schedule(hour="0", minute="0", persistent=false)
    public void dayChange() {
        Logger.getLogger("open.dolphin").info("Renew pvtlist.");
        eventServiceBean.renewPvtList();
    }
    @Timeout
    public void timeout(Timer timer) {
        logger.warning("ServletStartup: timeout occurred");
    }
    
//    @Schedule(dayOfWeek = "*", hour = "*", minute = "*", second = "*/5",year="2012", persistent = false)
//    public void backgroundProcessing() {
//        System.out.println("\n\n\t AutomaticSchedulerBean's backgroundProcessing() called....at: "+new Date());
//    }
    
//s.oh^ 2014/07/08 クラウド0対応
    /**
     * 毎月の１日 AM 5:10 に先月のアクティビティをメールで管理者へ送信する
     */
    @Schedule(dayOfMonth="1", hour="5", minute="0", persistent=false)
    //@Schedule(hour="15", minute="0", persistent=false)
    public void sendMonthlyActivities() {
        Logger.getLogger("open.dolphin").info("Send monthly Activities.");
     
//minagawa^ custom.properties          
//        Properties config = new Properties();
//        StringBuilder sb = new StringBuilder();
//        sb.append(System.getProperty("jboss.home.dir"));
//        sb.append(File.separator);
//        sb.append("custom.properties");
//        File f = new File(sb.toString());
//        try {
//            FileInputStream fin = new FileInputStream(f);
//            InputStreamReader r = new InputStreamReader(fin, "JISAutoDetect");
//            config.load(r);
//            r.close();
//        } catch (IOException ex) {
//            ex.printStackTrace(System.err);
//            throw new RuntimeException(ex.getMessage());
//        }
        Properties config = ORCAConnection.getInstance().getProperties();
//minagawa$        
        String zero = config.getProperty("cloud.zero");
        if(zero != null && zero.equals("true")) {
            // 先月の月と年
            GregorianCalendar gc = new GregorianCalendar();
            gc.add(Calendar.MONTH, -1);
            int year = gc.get(Calendar.YEAR);
            int month = gc.get(Calendar.MONTH);

            // レポートメール
            systemServiceBean.sendMonthlyActivities(year, month);
        }          
    }
//s.oh$
}
