package open.dolphin.server;

import java.util.*;
import java.io.*;
import java.net.*;
import java.text.*;


import open.dolphin.client.ClientContext;
import open.dolphin.client.MainWindow;
import open.dolphin.infomodel.UserModel;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * PVT socket server<br>
 * <br>
 * PVTServer() listen to ORCA for MML file through socket<br>
 * <br>
 * Creates 'Connection' thread on getting MML file from ORCA<br>
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 *
 * Modified by Mirror-i corp for writing into postgreSQL
 *
 */
public final class PVTClientServer implements Runnable,open.dolphin.server.PVTServer {

    public static final int EOT = 0x04;
    public static final int ACK = 0x06;
    public static final int NAK = 0x15;
    public static final String UTF8 = "UTF8";
    public static final String SJIS = "SHIFT_JIS";
    public static final String EUC = "EUC_JIS";
    
    private static final int DEFAULT_PORT = 5002;
    private UserModel user;
    private int port = DEFAULT_PORT;
    private String bindAddress;
    private ServerSocket listenSocket;
    private String encoding = UTF8;
    private Thread serverThread;
    private PVTSender sender;
    
    private MainWindow context;
    private String name;

    private boolean DEBUG;
    private Logger logger;

    /** Creates new ClaimServer */
    public PVTClientServer() {
       logger = ClientContext.getLogger("pvt");
       DEBUG = logger.getLevel() == Level.DEBUG ? true : false;
    }
    
    @Override
    public String getBindAddress() {
        return bindAddress;
    }

    @Override
    public void setBindAddress(String bindAddress) {
        this.bindAddress = bindAddress;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
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
        return encoding;
    }

    @Override
    public void setEncoding(String enc) {
        encoding = enc;
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
    public void start() {
        startService();
    }

    @Override
    public void stop() {
        stopService();
    }

    /**
     * 受付受信サーバを開始する。
     */
    public void startService() {

        try {
            sender = new PVTSender();
            sender.startService();
            
            InetSocketAddress address = null;
            String test = getBindAddress();
            
            if (test !=null && (!test.equals("")) ) {
                if (DEBUG) {
                    logger.debug("PVT ServerSocket bind address = " + getBindAddress());
                }
                try {
                    InetAddress addr = InetAddress.getByName(test);
                    address = new InetSocketAddress(addr, port);
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
            }
            
            if (address == null) {
                address = new InetSocketAddress(InetAddress.getLocalHost(), port);
            }
            
            listenSocket = new ServerSocket();
            listenSocket.bind(address);
            logger.info("PVT Server is binded " + address + " with encoding: " + encoding);

            serverThread = new Thread(this);
            serverThread.setPriority(Thread.NORM_PRIORITY);
            serverThread.start();

        } catch (IOException e) {
            e.printStackTrace(System.err);
            logger.warn("IOException while creating the ServerSocket: " + e.toString());
        }
    }

    /**
     * 受付受信サーバをストップする。
     */
    public void stopService() {

        if (serverThread != null) {
            serverThread = null;
        }

        if (listenSocket != null) {
            try {
                listenSocket.close();
                listenSocket = null;
            } catch (IOException e) {
                e.printStackTrace(System.err);
                logger.warn(e);
            }
        }

        if (sender != null) {
            sender.stopService();
        }
    }

    /**
     *
     * run(), This method is called from startServer()<br>
     * Listens to ORCA server for MML file and creats new 'Connection' thread
     * <br>
     *
     */
    @Override
    public void run() {

        Thread thisThread = Thread.currentThread();

        while (thisThread==serverThread) {
            try {
                Socket clientSocket = listenSocket.accept();
                Connection con = new Connection(clientSocket);
                Thread t = new Thread(con);
                t.setPriority(Thread.NORM_PRIORITY);
                t.start();
            } catch (IOException e) {
                if (thisThread!=serverThread) {
                    logger.info("PVTServer stopped");
                } else {
                    e.printStackTrace(System.err);
                    logger.warn("Exception while listening for connections:" + e);
                }
            }
        }
    }

    /**
     * 'Connection' thread<br>
     *
     * 'Connection' thread created from 'PVTServer' thread<br>
     * Converts the socket stream in to string and creates 'PatientRegister'
     * object<br>
     * Calls 'regist' method of 'PatientRegister' with received MML info<br>
     * Receives 'PVTPostgres' object by calling 'getPVT' method of
     * 'PatientRegister' object<br>
     * Creates new 'PVTPostgresConnection' object to get Postgres conenction<br>
     * Calls 'addWork' method of 'PVTPostgresConnection' to add/update patient
     * various info in Postgres database<br>
     * Overall transaction result sent to ORCA (ACK/NAK)<br>
     *
     */
    protected final class Connection implements Runnable {

        private Socket client;

        public Connection(Socket clientSocket) {
            this.client = clientSocket;
        }
        
        private void printInfo() {
            String addr = this.client.getInetAddress().getHostAddress();
            String time = DateFormat.getDateTimeInstance().format(new Date());
            logger.info("Connected from " + addr + " at " + time);
        }

        
        @Override
        public void run() {

            BufferedInputStream reader = null;
            BufferedOutputStream writer = null;
            
            try {
                printInfo();
                
                reader = new BufferedInputStream(new DataInputStream(this.client.getInputStream()));
                writer = new BufferedOutputStream(new DataOutputStream(this.client.getOutputStream()));
                
                ByteArrayOutputStream bo = new ByteArrayOutputStream();
                BufferedOutputStream buf = new BufferedOutputStream(bo);
                String recieved = null;

                byte[] buffer = new byte[16384];
                int readLen = 0;

                while (true) {

                    readLen = reader.read(buffer);

                    if (readLen == -1) {
                        if (DEBUG) {
                            logger.debug("EOF");
                        }
                        break;
                    }

                    if (buffer[readLen-1] == EOT) {
                        buf.write(buffer, 0, readLen-1);
                        buf.flush();
                        recieved = bo.toString(encoding);
                        int len = recieved.length();
                        bo.close();
                        buf.close();
                        if (DEBUG) {
                            logger.info("Recieved EOT length = " + len + " bytes");
                            logger.debug(recieved);
                        }

                        // add queue
                        sender.processPvt(recieved);

                        // Reply ACK
                        if (DEBUG) {
                            logger.debug("return ACK");
                        }
                        writer.write(ACK);
                        writer.flush();

                    } else {
                        buf.write(buffer, 0, readLen);
                    }
                }

                reader.close();
                writer.close();
                client.close();
                client = null;
                
            } catch (IOException e) {
                logger.warn("IOException while reading streams");
                logger.warn("Exception details:" + e);

            } finally {
                if (client != null) {
                    try {
                        client.close();
                        client = null;
                    } catch (IOException e2) {
                        logger.warn("Exception while closing socket conenction after reading streams");
                        logger.warn("Exception details:" + e2);
                    }
                }
            }
        }
    }
}