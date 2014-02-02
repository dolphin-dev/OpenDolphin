/*
 * PVTServer.java
 *
 * Created on 2001/10/17, 14:01
 *
 * Last updated on 2002/12/31
 *
 */
package mirrorI.dolphin.server;

import java.util.*;
import java.io.*;
import java.net.*;
import java.text.*;
import java.util.logging.*;

/**
 * PVT socket server<br>
 * <br>
 * PVTServer() listen to ORCA for MML file through socket<br>
 * <br>
 * Creates 'Connection' thread on getting MML file from ORCA<br>
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 *
 * Modified by Mirror-i corp for writing into postgreSQL
 *
 */
public class PVTServer extends Thread {

    public static final int EOT             		= 0x04;
    public static final int ACK             		= 0x06;
    public static final int NAK             		= 0x15;
    public static final String UTF8         		= "UTF8";
    public static final String SJIS         		= "SHIFT_JIS";
    public static final String EUC          		= "EUC_JIS";
    public static final String AUTO_DETECT  		= "JISAutoDetect";

    // Postgres Connection Parameter
    public static final String PostgresDriver 	= "org.postgresql.Driver";
    public static final String PostgresHost 	= "localhost";
    public static final String PostgresPort 	= "5432";
    public static final String PostgresDBName	= "dolphin";
    public static final String PostgresDBUser	= "dolphin";
    public static final String PostgresDBPasswd	= "";
    //Logger location
    public static final String loggerLocation = "usr.local.dolphin.mirrorI.dolphin.server";

    private Hashtable postgresConParameter;

    private static final int DEFAULT_PORT   = 5002;

    private int port = DEFAULT_PORT;

    private ServerSocket listenSocket;

    private String encoding = UTF8;

    private boolean forceStop;

    private static Logger logger = Logger.getLogger(loggerLocation);

    /** Creates new ClaimServer */
    //public PVTServer()
    public PVTServer() throws java.io.IOException {
        super();

        // Setting postgres connection parameter into hashtable
        postgresConParameter  =     new Hashtable(7, 0.75f);
        postgresConParameter.put("driver",PostgresDriver);
        postgresConParameter.put("host",PostgresHost);
        postgresConParameter.put("port",new Integer(PostgresPort));
        postgresConParameter.put("database",PostgresDBName);
        postgresConParameter.put("user",PostgresDBUser);
        postgresConParameter.put("passwd",PostgresDBPasswd);
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

    /**
     *
     * startServer(), This method is called from main()<br>
     * Creates new ServerSocket and calls run() of this thread<br>
     *
     */
    public void startServer() throws IOException {
        forceStop = false;
        listenSocket = new ServerSocket(port);
        this.start();
        logger.info("PVT Server is listening on port: " + port + " with encoding: " + encoding);
    }

    public void stopServer() throws IOException {
        if (listenSocket != null) {
            forceStop = true;
            listenSocket.close();
            listenSocket = null;
            logger.info("PVT Server is stoped.");
        }
    }

    public void restartServer() throws IOException {
        stopServer();
        startServer();
    }

    /**
     *
     * run(), This method is called from startServer()<br>
     * Listens to ORCA server for MML file and creats new 'Connection' thread <br>
     *
     */
    public void run() {

        try {
            while (true) {
                Socket clientSocket = listenSocket.accept();
                Connection c = new Connection(clientSocket);
            }
        
        } catch (IOException e) {
            if ( ! forceStop )  {
                logger.warning("Exception while listening for connections");
                logger.warning( "Exception details:"  + e );
            }
        }
    }

    /**
      * 'Connection' thread<br>
      *
      * 'Connection' thread created from 'PVTServer' thread<br>
      * Converts the socket stream in to string and creates 'PatientRegister' object<br>
      * Calls 'regist' method of 'PatientRegister' with received MML info<br>
      * Receives 'PVTPostgres' object by calling 'getPVT' method of 'PatientRegister' object<br>
      * Creates new 'PVTPostgresConnection' object to get Postgres conenction<br>
      * Calls 'addWork' method of 'PVTPostgresConnection' to add/update patient various info
      * in Postgres database<br>
      * Overall transaction result sent to ORCA (ACK/NAK)<br>
      *
      */
    protected final class Connection extends Thread {

        private BufferedInputStream reader;
        private BufferedOutputStream writer;
        private Socket client;

        public Connection(Socket clientSocket) {
            
            super();

            client = clientSocket;

            String addr = client.getInetAddress().getHostAddress();
            String time = DateFormat.getDateTimeInstance().format(new Date());
            logger.finer("Connected from " + addr + " at " + time);

            try {

		reader = new BufferedInputStream(new DataInputStream(client.getInputStream()));
                writer = new BufferedOutputStream(new DataOutputStream(client.getOutputStream()));

                this.start();
            
            } catch (IOException e) {
                logger.warning("Exception while getting socket streams");
                logger.warning( "Exception details:"  + e );
                try {
                    client.close();
                }
                catch (IOException e2) {
                    logger.warning("Exception while closing socket conenction on error");
                    logger.warning( "Exception details:"  + e2 );
                }
            }
        }

        public void run() {

            int c;
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            String recieved = null;
            try {
                while (true) {
                    if ( (c = reader.read()) == -1 ) {
                        throw new IOException("Exception while reading streams");
                    
                    } else if (c == EOT) {
                        // EncodingToUnicode
                        recieved = buf.toString(encoding);
                        int len = recieved.length();
                        logger.finer("recieved EOT length = " + len + " bytes");
                        break;
                   
                    } else {
                        buf.write(c);
                    }
                }

		// For parsing xml file, storing parsed information in postgres database
                mirrorI.dolphin.server.PatientRegister register = new PatientRegister();
                boolean result = register.regist(recieved);
                PVTPostgres pvt = register.getPVT();
                
                PVTPostgresConnection con = new PVTPostgresConnection(postgresConParameter);
                //To insert/update in the database
                result=con.addWork(pvt);

                // For getting PID for logger message
                PVTPatient pvtPatient;
                pvtPatient=pvt.getPVTPatient();

                // Returns result code
                if (result) {
                    writer.write(ACK);
                    logger.info("--------- Transaction succeed: Reply ACK ---------PID: " + pvtPatient.getPatientId());
                
                } else {
                    writer.write(NAK);
                    logger.warning("--------- Transaction failed: Reply NAK ------------PID: " + pvtPatient.getPatientId());
                }
                writer.flush();

                // Close socket
                client.close();
                client = null;
            
            } catch (IOException e) {
                logger.warning("IOException while reading streams");
                logger.warning( "Exception details:"  + e);
            
            } finally {
                if (client != null) {
                    try {
                        client.close();
                    }
                    catch (IOException e2) {
						logger.warning("Exception while closing socket conenction after reading streams");
                    	logger.warning( "Exception details:"  + e2 );
                    }
                }
            }
        }
    }

    /**
     *
     * main(), PVTServer entry point<br>
     * Accepts arguments for port and encode<br>
     * Default port=5002 and encode UTF8 if not specified<br>
     * <br>
     * Creates PVTServer() thread and calls its startServer() metthod<br>
     *
     */
    public static void main(String[] args) {

        // Opening log handler
        try {
            logger.addHandler(new FileHandler());
        
        } catch(IOException e) {
            System.out.println("File handler could not be found");
            System.out.println( "Exception details:"  + e );
        }

        // Default settings
        int port = 5002;
        String enc = "UTF8";

        try {
            // Retrive params
            if (args.length == 1) {
                port = Integer.parseInt(args[0]);
            
            } else if (args.length == 2) {
                port = Integer.parseInt(args[0]);
                enc = args[1];
            }

            // Create and set sever
            PVTServer server = new PVTServer();
            server.setPort(port);
            server.setEncoding(enc);

            // Start service
            server.startServer();
        
        } catch (Exception e) {
            System.out.println(e);
            logger.warning("Exception while retriving parameters");
            logger.warning( "Exception details:"  + e );
            System.exit(1);
        }
    }
}