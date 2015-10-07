package open.dolphin.impl.mml;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import open.dolphin.client.ClientContext;
import open.dolphin.client.MainWindow;
import open.dolphin.client.MmlMessageEvent;
import open.dolphin.client.MmlMessageListener;
import open.dolphin.infomodel.SchemaModel;
import open.dolphin.project.Project;
import org.apache.log4j.Logger;

/**
 * MML 送信サービス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class SendMmlImpl implements MmlMessageListener {
    
    // CSGW への書き込みパス
    // CSGW = Client Side Gateway
    private String csgwPath;
    
    // MML Encoding
    private String encoding;
    
    // Work Queue
    private final LinkedList queue = new LinkedList();
    
    private Kicker kicker;
    
    private Thread sendThread;
    
    private MainWindow context;
    
    private String name;
    
    /** Creates new SendMmlService */
    public SendMmlImpl() {
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
    
    @Override
    public String getCSGWPath() {
        return csgwPath;
    }
    
    @Override
    public void setCSGWPath(String val) {
        csgwPath = val;     
        try {
            Path path = Paths.get(csgwPath);
            Files.createDirectories(path);
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(this.getClass().getName()).warning("MMLファイル出力先のディレクトリを作成できません。");
        }      
    }
    
    @Override
    public void stop() {
        if (sendThread!=null) {
            Thread moribund = sendThread;
            sendThread = null;
            moribund.interrupt();
        }
        logDump();
    }
    
    @Override
    public void start() {
        
        // CSGW 書き込みパスを設定する
        setCSGWPath(Project.getCSGWPath());
        encoding = "UTF-8";  //Project.getString(Project.MML_ENCODING);
        
        // 送信スレッドを開始する
        kicker = new Kicker();
        sendThread = new Thread(kicker);
        sendThread.start();
        java.util.logging.Logger.getLogger(this.getClass().getName()).info("Send MML statered with CSGW = " + getCSGWPath());
    }
    
    @Override
    public void mmlMessageEvent(MmlMessageEvent e) {
        synchronized (queue) {
            queue.add(e);
            queue.notify();
        }
    }
    
    private Object getMML() throws InterruptedException {
        synchronized(queue) {
            while (queue.isEmpty()) {
                queue.wait();
            }
        }
       return queue.remove(0);
    }
    
    public void logDump() {

        synchronized(queue) {

            Iterator iter = queue.iterator();

            while (iter.hasNext()) {
                MmlMessageEvent evt = (MmlMessageEvent) iter.next();
                java.util.logging.Logger.getLogger(this.getClass().getName()).warning(evt.getMmlInstance());
            }

            queue.clear();
        }
    }
    
    protected String getCSGWPathname(String fileName, String ext) {
        StringBuilder buf = new StringBuilder();
        buf.append(csgwPath);
        buf.append(File.separator);
        buf.append(fileName);
        buf.append(".");
        buf.append(ext);
        return buf.toString();
    }
    
    protected class Kicker implements Runnable {
        
        @Override
        public void run() {
            
            Thread thisThread = Thread.currentThread();
            //BufferedOutputStream writer;
            
            while (thisThread == sendThread) {
                
                try {
                    // MML パッケージを取得
                    MmlMessageEvent mevt = (MmlMessageEvent) getMML();
                    //getLogger().debug("MMLファイルをコンシュームしました");
                    String groupId = mevt.getGroupId();
                    String instance = mevt.getMmlInstance();
                    List<SchemaModel> schemas = mevt.getSchema();
                    String filename = groupId+".xml.tmp";
                    Path dest = Paths.get(getCSGWPath(),filename);
                    Files.write(dest, instance.getBytes(encoding));
                    Files.move(dest, dest.resolveSibling(groupId+".xml"));
                    
                    if (schemas != null) {
                        for (SchemaModel schema : schemas) {
                            filename = schema.getExtRefModel().getHref()+".tmp";
                            dest = Paths.get(getCSGWPath(),filename);
                            Files.write(dest, schema.getJpegByte());
                            Files.move(dest, dest.resolveSibling(schema.getExtRefModel().getHref()));
                        }
                    }                 
                } catch (IOException e) {
                    e.printStackTrace(System.err);
                    java.util.logging.Logger.getLogger(this.getClass().getName()).warning(e.getMessage());
                    
                } catch (InterruptedException ie) {
                    java.util.logging.Logger.getLogger(this.getClass().getName()).warning("Interrupted sending MML");

                }
            }
        }
    }
}