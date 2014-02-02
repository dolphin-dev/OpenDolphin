/*
 * ClientRequestHandler.java
 *
 * Created on 2003/02/10
 *
 * Last updated on 2003/03/06
 *
 */
 package mirrorI.dolphin.server;

import java.io.*;
import java.util.*;
import java.net.*;
import java.util.logging.*;
import java.text.*;

/**
 *
 * This class listen to dolphin client request for 'Master Data Update' and 'Database Backup'<br>
 * After identifying the type of request, appropriate local server programs are called<br>
 * Returns the result back to requested client program<br>
 *
 * @author  Prashanth Kumar Mirror-I Corp
 */
public class ClientRequestHandler extends Thread {

	//List of client requests
	private static final int MASTER_DATA_UPDATE	= 0x11;
    private static final int DATA_BACKUP				= 0x12;

   	private static final int DEFAULT_PORT   			= 6002;

    private int port = DEFAULT_PORT;
    private ServerSocket listenSocket;

	//Properties to store master update various parameter
	private static Properties clientRequestParameter;

	//Logger for logging messages
	public static Logger logger;

	//INI file read object
	private  static mirrorI.dolphin.server.IniFileRead iniFileRead;


   	/** Creates new ClientRequestHandler */
   	public ClientRequestHandler( ) throws java.io.IOException {
   		super();
    }


    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

 	/**
	 *
	 * startServer(), Creates new ServerSocket and calls run() of this thread<br>
	 *
	 * This method is called from main()<br>
	 *
     */
    public boolean startServer() throws IOException {
		logger.finer("Method Entry");
        listenSocket = new ServerSocket(port);
        this.start();
        logger.info("Client Request handler is listening on port: " + port);
        logger.finer("Method Exit");
        return true;
    }

 	/**
	 *
	 *  run(), Listens to client request and creates new 'Connection' thread on request<br>
	 *
	 * This method is called from startServer()<br>
	 *
     */
    public void run() {
		logger.finer("Method Entry");
        try {
            while (true) {
               Socket clientSocket = listenSocket.accept();
                if(clientSocket.isConnected()){
                	Connection c = new Connection(clientSocket);
				}
            }
        }
        catch (IOException e) {
			logger.warning("Exception while listening for connections");
			logger.warning( "Exception details:"  + e );
        }
        logger.finer("Method Exit");
    }

	/**
	  * 'Connection' thread<br>
	  *
	  * 'Connection' thread created from 'ClientRequestHandler' thread<br>
	  * Calls run() method to handle client request<br>
	  *<br>
	  *
	  */
 	protected final class Connection extends Thread {
        private Socket client;
		private ObjectOutputStream writer;
		private ObjectInputStream reader;

		public Connection(Socket clientSocket) {
            super();

            client = clientSocket;

			String addr = client.getInetAddress().getHostAddress();
			String time = DateFormat.getDateTimeInstance().format(new java.util.Date());
			logger.info("Connected from " + addr + " at " + time);

            try {
				reader = new ObjectInputStream(new BufferedInputStream (client.getInputStream()));
				writer = new ObjectOutputStream(new BufferedOutputStream (client.getOutputStream()));
                this.start();
            }
            catch (IOException e) {
                logger.warning("Exception while getting socket streams");
                logger.warning("Exception details:"  + e );
                try {
                    if ((reader != null) && (!client.isClosed()) ) {
						reader.close();
					}
                    if ( (writer != null) && (!client.isClosed())  ) {
						writer.close();
					}
                    if (client != null) {
						client.close();
					}
                }
                catch (IOException e2) {
					logger.warning("Exception while closing socket or writer or reader on exception");
                    logger.warning( "Exception details:"  + e2 );
                }
            }
        }

		/**
		 *
		 * Accepts type of program need to be executed in local server,<br>
		 * calls appropriate local server program and sends back the result to requested client program<br>
		 *
		 * This method is called from Connection thread<br>
		 *
		 */
        public void run() {
			logger.finer("Method Entry");

			int resultReturn = 0;

			 try {
				//Read type of program to be excuted
				int programToBeExecuted = reader.read();

				switch (programToBeExecuted) {
					//Is is it for master data update
					case (MASTER_DATA_UPDATE):
						logger.info("Client requested to execute 'Master Data update' program");
						//write confirmation message
						writer.write(MASTER_DATA_UPDATE);
						writer.flush();
						//execute requested program and write back the result
						MasterUpdateLocal masterUpdLoc = new MasterUpdateLocal(clientRequestParameter,logger);
						resultReturn=masterUpdLoc.getUpdate();
						writer.writeInt(resultReturn);
						writer.flush();
						break;

					//Is is it for data backup
					case (DATA_BACKUP):
						logger.info("Client requested to execute 'Data backup' program");
						int selectedMedia=0;
						int selectedHdd=0;
						String backupFilePath=null;
						//read  selected Media To Backup
						selectedMedia=reader.readInt();
						//read  selected HDD
						selectedHdd=reader.readInt();
						//read path
						backupFilePath=(String)reader.readObject();
						//write confirmation message
						writer.write(DATA_BACKUP);
						writer.flush();
						//execute requested program and write back the result
						mirrorI.dolphin.data.DataBackup dataBackup = new mirrorI.dolphin.data.DataBackup(clientRequestParameter,logger);
						resultReturn=dataBackup.startDatabackup(selectedMedia,selectedHdd,backupFilePath);
						logger.info("Parameter sent to DataBackup program, Media Sel: "+selectedMedia + " HDD Sel:  " +selectedHdd + " Path: " +backupFilePath );
						writer.writeInt(resultReturn);
						writer.flush();
						break;

					//default
					default:
					logger.warning("Unhandled request from client: " +programToBeExecuted);
					break;
				}
			}
			catch (IOException ioe) {
				logger.warning("IO Exception while getting while receiving/sending request from/to client system");
                logger.warning( "Exception details:"  + ioe);
         	}
   			catch (ClassNotFoundException cnf) {
				logger.warning("Class not found Exception while getting while receiving/sending request from/to client system");
                logger.warning( "Exception details:"  + cnf);
         	}
   			catch (Exception e) {
				logger.warning("Exception while getting while receiving/sending request from/to client system");
                logger.warning( "Exception details:"  + e);
         	}
            finally {
                try {
                    if ((reader != null) && (!client.isClosed()) ) {
						reader.close();
					}
                    if ( (writer != null) && (!client.isClosed())  ) {
						writer.close();
					}
                    if (client != null) {
						client.close();
					}
                }
                catch (IOException e2) {
					logger.warning("Exception while closing socket or writer or reader after receiving/sending request from/to client system");
                    logger.warning( "Exception details:"  + e2 );
                }
            }
            logger.finer("Method Exit");
        }

	}//class 'Connection' end


	/**
	 *
	 * main(), ClientRequestHandler entry point<br>
	 *   	Reads INI file for DB connection parameter, remote host ID etc <br>
	 *		Opens log handler for logging messages<br>
	 * 	Calls ClientRequestHandler<br>
	 *
	 */
    public static void main(String args[]) {

		//To store Remote Server information like host ID, postgres connection etc etc
		boolean mainReturn=false;
		clientRequestParameter=null;
		int port = 6002;

		//Read INI file and store info in 'masterUpdateParameter'
		iniFileRead = new IniFileRead();
		clientRequestParameter = iniFileRead.readIniFile();
		if ( clientRequestParameter == null){
			  System.out.println("Could not get INI file " );
			  System.exit(1);
		}
		//set IniFileRead object as null
		iniFileRead = null;

		// Opening log handler
		if (clientRequestParameter != null && clientRequestParameter.size() > 0 &&
		        										     clientRequestParameter.containsKey("ClientRequestLoggerLocation") &&
		    												 clientRequestParameter.getProperty("ClientRequestLoggerLocation") != null ) {

			logger = Logger.getLogger(clientRequestParameter.getProperty("ClientRequestLoggerLocation"));
			//Copy to LoggerLocation for PostgresConnection Object
			clientRequestParameter.put("LoggerLocation",clientRequestParameter.getProperty("ClientRequestLoggerLocation"));
		}
		//To avoid run time exception error (when logger info not found in INI file)
		else{
			logger = Logger.getLogger("Dummy");
			clientRequestParameter.put("LoggerLocation","Dummy");
		}

		try {
			logger.addHandler(new FileHandler());
		}
		catch(IOException ioe) {
			System.out.println("File handler could not be found");
			System.out.println( "Exception details:"  + ioe );
		}
		catch(Exception e) {
			System.out.println("Exception while opening logger handler");
			System.out.println( "Exception details:"  + e );
		}
		logger.finer("Method Entry");

        try{
			//Create clientRequestHandler
			ClientRequestHandler clientRequestHandler = new ClientRequestHandler();
			clientRequestHandler.setPort(port);

			//Call clientRequestHandler startServer() method to start watching for client request
			mainReturn=clientRequestHandler.startServer();
		}
		catch (Exception e){
			logger.warning("Exception while creating 'clientRequestHandler' object");
			logger.warning( "Exception details:"  + e);
		}
		if(!mainReturn){
			logger.warning("Error in startServer() of ClientRequestHandler");
			logger.warning("Abnormol Exit");
			System.exit(1);
		}
		logger.finer("Method Exit");
	}//Main() end

}//Class 'ClientRequestHandler' end

