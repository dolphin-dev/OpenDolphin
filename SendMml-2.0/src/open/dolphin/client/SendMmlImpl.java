package open.dolphin.client;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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
    
    private Logger getLogger() {
        return ClientContext.getMmlLogger();
    }
    
    @Override
    public void setCSGWPath(String val) {
        csgwPath = val;
        File directory = new File(csgwPath);
        if (! directory.exists()) {
            if (directory.mkdirs()) {
                getLogger().debug("MMLファイル出力先のディレクトリを作成しました");
            } else {
                getLogger().warn("MMLファイル出力先のディレクトリを作成できません");
            }
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
        encoding = Project.getMMLEncoding();
        
        // 送信スレッドを開始する
        kicker = new Kicker();
        sendThread = new Thread(kicker);
        sendThread.start();
        getLogger().info("Send MML statered with CSGW = " + getCSGWPath());
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
                getLogger().warn(evt.getMmlInstance());
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
            BufferedOutputStream writer = null;
            
            while (thisThread == sendThread) {
                
                try {
                    // MML パッケージを取得
                    MmlMessageEvent mevt = (MmlMessageEvent) getMML();
                    //getLogger().debug("MMLファイルをコンシュームしました");
                    String groupId = mevt.getGroupId();
                    String instance = mevt.getMmlInstance();
                    List<SchemaModel> schemas = mevt.getSchema();
                    
                    // ファイル名を生成する
                    String dest = getCSGWPathname(groupId, "xml");
                    String temp = getCSGWPathname(groupId, "xml.tmp");
                    File f = new File(temp);
                    
                    // インスタンスをUTF8で書き込む
                    writer = new BufferedOutputStream(new FileOutputStream(f));
                    byte[] bytes = instance.getBytes(encoding);
                    writer.write(bytes);
                    writer.flush();
                    writer.close();
                    
                    // 書き込み終了後にリネームする (.tmp -> .xml)
                    f.renameTo(new File(dest));
                    getLogger().debug("MMLファイルを書き込みました");
                    
                    // 画像を送信する
                    if (schemas != null) {
                        for (SchemaModel schema : schemas) {
                            dest = csgwPath + File.separator + schema.getExtRefModel().getHref();
                            temp = dest + ".tmp";
                            f = new File(temp);
                            writer = new BufferedOutputStream(new FileOutputStream(f));
                            writer.write(schema.getJpegByte());
                            writer.flush();
                            writer.close();
                            
                            // Renameする
                            f.renameTo(new File(dest));
                            getLogger().debug("画像ファイルを書き込みました");
                        }
                    }
                    
                } catch (IOException e) {
                    e.printStackTrace(System.err);
                    getLogger().warn(e.getMessage());
                    
                } catch (InterruptedException ie) {
                    getLogger().warn("Interrupted sending MML");

                } catch (Exception ee) {
                    ee.printStackTrace(System.err);
                    getLogger().warn(ee.getMessage());
                }
            }
        }
    }
}