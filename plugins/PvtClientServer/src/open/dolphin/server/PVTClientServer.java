package open.dolphin.server;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import open.dolphin.client.MainWindow;
import org.apache.log4j.Logger;

import open.dolphin.client.ClientContext;

/**
 * 
 */
public class PVTClientServer implements open.dolphin.server.PVTServer,Runnable {
    
    public static final int EOT = 0x04;
    
    public static final int ACK = 0x06;
    
    public static final int NAK = 0x15;
    
    public static final String UTF8 = "UTF8";
    
    public static final String SJIS = "SHIFT_JIS";
    
    public static final String EUC = "EUC_JIS";
    
    //public static final String AUTO_DETECT = "JISAutoDetect";
    
    private static final int DEFAULT_PORT = 5002;
    
    private static final int BUFFER_SIZE = 8192*10; // 80K
    
    private static final int READ_BUFFER_SIZE = 8192;
    
    private int port = DEFAULT_PORT;
    
    private String encoding = UTF8;
    
    private Logger logger;
    
    private Selector selector;
    
    private ServerSocketChannel serverSocketChannel;
    
    //private ByteBuffer buffer;

    //private static Charset charset = Charset.forName("UTF-8");
    
    //private static CharsetDecoder decoder = charset.newDecoder();
    
    //private int length;
    
    //private byte[] dst;
    
    private ExecutorService service;
    
    private MainWindow context;
    
    private String name;
    
    
    /** Creates new ClaimServer */
    public PVTClientServer() {
        logger = ClientContext.getLogger("pvt");
    }
    
    public int getPort() {
        return port;
    }
    
    public void setPort(int port) {
        this.port = port;
    }
    
    public String getEncoding() {
        return encoding;
    }
    
    public void setEncoding(String enc) {
        encoding = enc;
    }
    
    private void setup() {
         
        try {
            selector = SelectorProvider.provider().openSelector();
            
            serverSocketChannel 
                = SelectorProvider.provider().openServerSocketChannel();
            
            serverSocketChannel.configureBlocking(false);

            InetSocketAddress address = new InetSocketAddress(InetAddress.getLocalHost(), port);
            
            serverSocketChannel.socket().bind(address);
            
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT, new AcceptHandler(logger));
            
            logger.info("PVT ServerSocket is binded " + address);
            
        } catch (IOException e) {
            e.printStackTrace();
            logger.warn(e.toString());
        }
    }
    
    /**
     * ソケットサーバを開始する。
     */
    public void startService() {
        
        try {
            setup();
            service = Executors.newSingleThreadExecutor();
            service.execute(this);
            
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn(e.toString());
        }
    }
    
    /**
     * ソケットサーバを終了する。
     */    
    public void stopService() {
        
        try {
            if (service != null) {
                service.shutdownNow();
                service = null;
            }
            
            if (serverSocketChannel != null) {
                serverSocketChannel.close();
                serverSocketChannel = null;
            }
            
            if (selector != null) {
                selector.close();
                selector = null;
            }
            
            logger.info("PVT Server is stoped.");
            
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("IOException while stopping the ServerSocket: "
                    + e.toString());
        }
    }
            
    /**
     * ソケットサーバを開始する。
     */
    public void run() {
        
        try {
            
            while (selector.select() > 0) {
                
                Set keys = selector.selectedKeys();
                
                for (Iterator it = keys.iterator(); it.hasNext();) {
                    
                    SelectionKey key = (SelectionKey) it.next();
                    it.remove();
                    
                    Handler handler = (Handler) key.attachment();
                    handler.handle(key);
                }
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            logger.warn(e.toString());
        }
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

    public void start() {
        startService();
    }

    public void stop() {
        stopService();
    }
}