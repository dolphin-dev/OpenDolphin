package open.dolphin.impl.claim;

import java.io.*;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import open.dolphin.client.ClaimMessageEvent;
import open.dolphin.client.ClaimMessageListener;
import open.dolphin.client.ClientContext;
import open.dolphin.client.MainWindow;
import open.dolphin.project.Project;
import org.apache.log4j.Logger;


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
    
    // Properties
    private String host;
    private int port;
    private String enc;
    private int tryCount 	= DEFAULT_TRY_COUNT;
    private final long sleepTime 	= DEFAULT_SLEEP_TIME;
    private int alertQueueSize 	= MAX_QUEU_SIZE;
    
    private Thread sendThread;
    private final List queue = new LinkedList();
    private OrcaSocket orcaSocket;
    
    
    private MainWindow context;
    private String name;
    
    /**
     * Creates new ClaimQue 
     */
    public SendClaimImpl() {
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public MainWindow getContext() {
        return context;
    }
    
    @Override
    public void setContext(MainWindow context) {
        this.context = context;
    }
    
    private void setup() {
        setHost(Project.getString(Project.CLAIM_ADDRESS));
        setPort(Project.getInt(Project.CLAIM_PORT));
        setEncoding(Project.getString(Project.CLAIM_ENCODING));
        
        if (orcaSocket == null) {
            orcaSocket = new OrcaSocket(getHost(), getPort(), sleepTime, tryCount);
        }
    }
    
    /**
     * プログラムを開始する。
     */
    @Override
    public void start() {
        
        setup();
        
        sendThread = new Thread(new Consumer(orcaSocket));
        sendThread.start();
        
        java.util.logging.Logger.getLogger(this.getClass().getName()).log(Level.INFO, "SendClaim started with = host = {0} port = {1}", new Object[]{getHost(), getPort()});
    }
    
    /**
     * プログラムを終了する。
     */
    @Override
    public void stop() {
        
        if (sendThread != null) {
            Thread moribund = sendThread;
            sendThread = null;
            moribund.interrupt();
        }

        logDump();
    }
    
    @Override
    public String getHost() {
        return host;
    }
    
    @Override
    public void setHost(String host) {
        this.host = host;
    }
    
    @Override
    public int getPort() {
        return port;
    }
    
    @Override
    public void setPort(int port) {
        this.port = port;
    }
    
    @Override
    public String getEncoding() {
        return enc;
    }
    
    @Override
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
     * @param e
     */
    @Override
    public void claimMessageEvent(ClaimMessageEvent e) {
        synchronized (queue) {
            queue.add(e);
            queue.notify();
        }     
    }
    
    /**
     * Queue から取り出す。
     */
    private Object getCLAIM() throws InterruptedException {
        synchronized(queue) {
            while (queue.isEmpty()) {
                queue.wait();
            }
        }
        return queue.remove(0);
    }
    
    public synchronized int getQueueSize() {
        return queue.size();
    }
    
    /**
     * Queue内の CLAIM message をログへ出力する。
     */
    public void logDump() {
        
        synchronized(queue) {
            
            Iterator iter = queue.iterator();

            while (iter.hasNext()) {
                ClaimMessageEvent evt = (ClaimMessageEvent) iter.next();
                java.util.logging.Logger.getLogger(this.getClass().getName()).warning(evt.getClaimInsutance());
            }

            queue.clear();
        }
    }
    
    private int alertDialog(int code) {
        
        java.util.ResourceBundle bundle = ClientContext.getMyBundle(SendClaimImpl.class);
        String proceedString = bundle.getString("optionText.continue");
        String dumpString = bundle.getString("optionText.outputLog");
        String title = bundle.getString("title.sendClaim");
        
        int option = -1;
        StringBuffer buf;
        
        switch(code) {
            
            case TT_QUEUE_SIZE:
                buf = new StringBuffer();
                buf.append(bundle.getString("messageCompo_0"));
                buf.append(getQueueSize());
                buf.append(bundle.getString("messageCompo_1")).append("\n");
                buf.append(bundle.getString("messageCompo_2")).append("\n");;
                buf.append(bundle.getString("messageCompo_3")).append("\n   ");
                buf.append(bundle.getString("messageCompo_4"));
                
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
                buf.append(bundle.getString("messageCompo_5")).append("\n");
                buf.append(bundle.getString("messageCompo_6"));
                JOptionPane.showMessageDialog(
                        null,
                        buf.toString(),
                        title,
                        JOptionPane.ERROR_MESSAGE);
                break;
                
            case TT_SENDING_TROUBLE:
                buf = new StringBuffer();
                buf.append(bundle.getString("messageCompo_7")).append("\n");
                buf.append(bundle.getString("messageCompo_8"));
                JOptionPane.showMessageDialog(
                        null,
                        buf.toString(),
                        title,
                        JOptionPane.ERROR_MESSAGE);
                break;
                
            case TT_CONNECTION_REJECT:
                buf = new StringBuffer();
                String fmt = bundle.getString("messageFormat.noResponse");
                buf.append(new MessageFormat(fmt).format(new Object[]{host,port})).append("\n");
                buf.append(bundle.getString("messageCompo_9")).append("\n");
                buf.append(bundle.getString("messageCompo_10")).append("\n");
                buf.append(bundle.getString("messageCompo_11")).append("\n   ");
                buf.append(bundle.getString("messageCompo_12"));
                
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
        java.util.logging.Logger.getLogger(this.getClass().getName()).warning(getBasicInfo(result, evt));
        java.util.logging.Logger.getLogger(this.getClass().getName()).warning(evt.getClaimInsutance());
    }
    
    private void log(String result, ClaimMessageEvent evt) {
        java.util.logging.Logger.getLogger(this.getClass().getName()).info(getBasicInfo(result, evt));
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
        
        private final OrcaSocket orcaSocket;
        
        public Consumer(OrcaSocket orcaSocket) {
            this.orcaSocket = orcaSocket;
        }
        
        @Override
        public void run() {
            
            Thread thisThread = Thread.currentThread();

            while (thisThread == sendThread) {

                try {
                    // CLAIM Event を取得
                    ClaimMessageEvent claimEvent = (ClaimMessageEvent) getCLAIM();
                    String instance = claimEvent.getClaimInsutance();

                    try ( // Gets connection
                            Socket socket = orcaSocket.getSocket()) {
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
                        OutputStream out = socket.getOutputStream();
                        DataOutputStream dout = new DataOutputStream(out);
                        BufferedInputStream reader;
                        try (BufferedOutputStream writer = new BufferedOutputStream(dout)) {
                            InputStream in = socket.getInputStream();
                            DataInputStream din = new DataInputStream(in);
                            reader = new BufferedInputStream(din);
                            // Writes UTF8 data
                            writer.write(instance.getBytes(enc));
                            writer.write(EOT);
                            writer.flush();
                            // Reads result
                            int c = reader.read();
                            if (c == ACK) {
                                log("CLAIM ACK", claimEvent);
                            } else if (c == NAK) {
                                warnLog("received NAK", claimEvent);
                            }
                        }
                        reader.close();
                    }

                } catch (IOException e) {
                    e.printStackTrace(System.err);
                    java.util.logging.Logger.getLogger(this.getClass().getName()).warning(e.getMessage());
                    alertDialog(TT_SENDING_TROUBLE);

                } catch (InterruptedException e) {
                    java.util.logging.Logger.getLogger(this.getClass().getName()).warning("Interrupted sending CLAIM");
                }
            }
        }
    }
}