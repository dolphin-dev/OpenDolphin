package open.dolphin.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import open.dolphin.project.*;


/**
 * SendClaimPlugin
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class SendClaimImpl implements ClaimMessageListener {
    
    // Socket constants
    private final int EOT                   	= 0x04;
    private final int ACK                   	= 0x06;
    private final int NAK                   	= 0x15;
    private final int DEFAULT_TRY_COUNT     	= 3;		// Socket 接続を試みる回数
    private final long DEFAULT_SLEEP_TIME   	= 20*1000L; 	// Socket 接続が得られなかった場合に次のトライまで待つ時間 msec
    private final int MAX_QUEU_SIZE 		= 10;
    
    // Alert constants
    private final int TT_QUEUE_SIZE         = 0;
    private final int TT_NAK_SIGNAL         = 1;
    private final int TT_SENDING_TROUBLE    = 2;
    private final int TT_CONNECTION_REJECT  = 3;
    
    // Strings
    private final String proceedString      = "継続";
    private final String dumpString         = "ログへ記録";
    
    // Properties
    private LinkedBlockingQueue queue;
    private String host;
    private int port;
    private String enc;
    private int tryCount 	= DEFAULT_TRY_COUNT;
    private long sleepTime 	= DEFAULT_SLEEP_TIME;
    private int alertQueueSize 	= MAX_QUEU_SIZE;
    
    private Logger logger;
    private ExecutorService sendService;
    
    private MainWindow context;
    private String name;
    
    /**
     * Creates new ClaimQue 
     */
    public SendClaimImpl() {
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public MainWindow getContext() {
        return context;
    }
    
    public void setContext(MainWindow context) {
        this.context = context;
    }
    
    private void setup() {
        logger = ClientContext.getPart11Logger();
        setHost(Project.getClaimAddress());
        setPort(Project.getClaimPort());
        setEncoding(Project.getClaimEncoding());
        
        if (queue == null) {
            queue = new LinkedBlockingQueue();
        }
    }
    
    /**
     * プログラムを開始する。
     */
    public void start() {
        
        setup();
        
        OrcaSocket orcaSocket = new OrcaSocket(getHost(), getPort(), sleepTime, tryCount);
        sendService = Executors.newSingleThreadExecutor();
        sendService.execute(new Consumer(orcaSocket));
        
        logger.info("SendClaim started with = host = " + getHost() + " port = " + getPort());
    }
    
    /**
     * プログラムを終了する。
     */
    public void stop() {
        
        try {
            if (sendService != null) {
                sendService.shutdownNow();
            }
            
            logDump();
            
            logger.info("SendClaim stopped");
            
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Exception while stopping the SendClaim");
            logger.warn(e.getMessage());
        }
    }
    
    public String getHost() {
        return host;
    }
    
    public void setHost(String host) {
        this.host = host;
    }
    
    public int getPort() {
        return port;
    }
    
    public void setPort(int port) {
        this.port = port;
    }
    
    public String getEncoding() {
        return enc;
    }
    
    public void setEncoding(String enc) {
        this.enc = enc;
    }
    
    public int getTryCount() {
        return tryCount;
    }
    
    public void setTryCount(int val) {
        tryCount = val;
    }
    
    public int getAlertQueueSize() {
        return alertQueueSize;
    }
    
    public void getAlertQueueSize(int val) {
        alertQueueSize = val;
    }
    
    /**
     * カルテで CLAIM データが生成されるとこの通知を受ける。
     */
    public void claimMessageEvent(ClaimMessageEvent e) {
        queue.offer(e);     
    }
    
    /**
     * Queue から取り出す。
     */
    public Object getCLAIM() throws InterruptedException {
        return queue.take();
    }
    
    public int getQueueSize() {
        return queue.size();
    }
    
    /**
     * Queue内の CLAIM message をログへ出力する。
     */
    public void logDump() {
        
        Iterator iter = queue.iterator();
        
        while (iter.hasNext()) {
            ClaimMessageEvent evt = (ClaimMessageEvent) iter.next();
            logger.warn(evt.getClaimInsutance());
        }
        
        queue.clear();
    }
    
    private int alertDialog(int code) {
        
        int option = -1;
        String title = "OpenDolphin: CLAIM 送信";
        StringBuffer buf = null;
        
        switch(code) {
            
            case TT_QUEUE_SIZE:
                buf = new StringBuffer();
                buf.append("未送信のCLAIM(レセプト)データが");
                buf.append(getQueueSize());
                buf.append(" 個あります。CLAIM サーバとの接続を確認してください。\n");
                buf.append("1. このまま処理を継続することもできます。\n");
                buf.append("2. 未送信データをログに記録することができます。\n");
                buf.append("   この場合、データは送信されず、診療報酬は手入力となります。");
                
                option = JOptionPane.showOptionDialog(
                        null,
                        buf.toString(),
                        title,
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.WARNING_MESSAGE,null,
                        new String[]{proceedString, dumpString},proceedString);
                break;
                
            case TT_NAK_SIGNAL:
                buf = new StringBuffer();
                buf.append("CLAIM(レセプト)データがサーバにより拒否されました。\n");
                buf.append("送信中のデータはログに記録します。診療報酬の自動入力はできません。");
                JOptionPane.showMessageDialog(
                        null,
                        buf.toString(),
                        title,
                        JOptionPane.ERROR_MESSAGE);
                break;
                
            case TT_SENDING_TROUBLE:
                buf = new StringBuffer();
                buf.append("CLAIM(レセプト)データの送信中にエラーがおきました。\n");
                buf.append("送信中のデータはログに記録します。診療報酬の自動入力はできません。");
                JOptionPane.showMessageDialog(
                        null,
                        buf.toString(),
                        title,
                        JOptionPane.ERROR_MESSAGE);
                break;
                
            case TT_CONNECTION_REJECT:
                buf = new StringBuffer();
                buf.append("CLAIM(レセプト)サーバ ");
                buf.append("Host=");
                buf.append(host);
                buf.append(" Port=");
                buf.append(port);
                buf.append(" が ");
                buf.append(tryCount*sleepTime);
                buf.append(" 秒以上応答しません。サーバの電源及び接続を確認してください。\n");
                buf.append("1. このまま接続を待つこともできます。\n");
                buf.append("2. データをログに記録することもできます。\n");
                buf.append("   この場合、データは送信されず、診療報酬は手入力となります。");
                
                option = JOptionPane.showOptionDialog(
                        null,
                        buf.toString(),
                        title,
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.WARNING_MESSAGE,null,
                        new String[]{proceedString, dumpString},proceedString);
                break;
        }
        
        return option;
    }
    
    private void warnLog(String result, ClaimMessageEvent evt) {
        logger.warn(getBasicInfo(result, evt));
        logger.warn(evt.getClaimInsutance());
    }
    
    private void log(String result, ClaimMessageEvent evt) {
        logger.info(getBasicInfo(result, evt));
    }
    
    private String getBasicInfo(String result, ClaimMessageEvent evt) {
        
        String id = evt.getPatientId();
        String nm = evt.getPatientName();
        String sex = evt.getPatientSex();
        String title = evt.getTitle();
        String timeStamp = evt.getConfirmDate();
        
        StringBuilder buf = new StringBuilder();
        buf.append(result);
        buf.append("[");
        buf.append(id);
        buf.append(" ");
        buf.append(nm);
        buf.append(" ");
        buf.append(sex);
        buf.append(" ");
        buf.append(title);
        buf.append(" ");
        buf.append(timeStamp);
        buf.append("]");
        
        return buf.toString();
    }
    
    /**
     * CLAIM 送信スレッド。
     */
    protected class Consumer implements Runnable {
        
        private OrcaSocket orcaSocket;
        
        public Consumer(OrcaSocket orcaSocket) {
            this.orcaSocket = orcaSocket;
        }
        
        public void run() {
            
            ClaimMessageEvent claimEvent = null;
            Socket socket = null;
            BufferedOutputStream writer = null;
            BufferedInputStream reader = null;

            String instance = null;

            while (true) {

                try {
                    // CLAIM Event を取得
                    claimEvent = (ClaimMessageEvent) getCLAIM();
                    instance = claimEvent.getClaimInsutance();

                    // Gets connection
                    socket = orcaSocket.getSocket();
                    if ( socket == null ) {
                        int option = alertDialog(TT_CONNECTION_REJECT);
                        if (option == 1) {
                            warnLog("CLAIM  Socket Error", claimEvent);
                            continue;
                        } else {
                            // push back to the queue
                            claimMessageEvent(claimEvent);
                            continue;
                        }
                    }

                    // Gets io stream
                    writer = new BufferedOutputStream(new DataOutputStream(socket.getOutputStream()));
                    reader = new BufferedInputStream(new DataInputStream(socket.getInputStream()));

                    // Writes UTF8 data
                    writer.write(instance.getBytes(enc));
                    writer.write(EOT);
                    writer.flush();

                    // Reads result
                    int c = reader.read();
                    if (c == ACK) {
                        log("CLAIM ACK", claimEvent);
                    } else if (c == NAK) {
                        warnLog("CLAIM NAK", claimEvent);
                    }
                    socket.close();

                } catch (IOException e) {
                    alertDialog(TT_SENDING_TROUBLE);
                    if (instance != null) {
                        warnLog("CLAIM IO Error", claimEvent);
                    }

                } catch (Exception e) {
                    logger.warn("Exception " + e.getMessage());
                    break;
                }
            }
        }
    }
}