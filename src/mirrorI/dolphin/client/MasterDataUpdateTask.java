/*
 * MasterDataUpdateTask.java
 *
 * Created on 2003/02/12
 *
 * Last updated on 2003/03/06
 *
 */

package mirrorI.dolphin.client;

import swingworker.*;
import java.util.*;
import java.io.*;
import java.net.*;

/**
 *
 * This class communicate with local server's 'ClientRequestHandler' program<br>
 * for executing master update program which is exist in local server<br>
 *
 * This object is created when when '更新' button is clicked from 'マスタデータ更新' GUI<br>
 *
 * @author  Prasahnth Kumar, Mirror-I Corp.
 *
 */
public class MasterDataUpdateTask{

    //For Master Data Update program returns
    //error
    private static final int OPERATION_FAIL						= -1;
    //No error and data updates available
    private static final int UPDATE_DATA_EXST_SUC		= 1;
	//No error and data updates not available
    private static final int UPDATE_DATA_NOT_EXST_SUC	= 3;
    //No error and file updates available
    private static final int UPDATE_FILE_EXST_SUC			= 5;
    //No error and file updates not available
    private static final int UPDATE_FILE_NOT_EXST_SUC	= 6;

    private static final int MASTER_DATA_UPDATE	= 0x11;

    //private static Logger logger ;
    private Properties masterUpdateParameter;
    private boolean updateOver=false;
    private String statMessage=null;

    /** Creates new MasterDataUpdateTask */
    public MasterDataUpdateTask (Properties masterUpdateParameter) {
		this.masterUpdateParameter = masterUpdateParameter;
		//this.logger = logger;
	}

	/**
	 *
	 * go(), cretes new object of  'ActualTask' class to send request for master data update<br>
	 * <br>
	 * This method is called from MasterDataUpdateService.exeUpdate()<br>
	 *
 	 */
    void go() {
		statMessage="ローカルサーバーへ接続中. . . . . . .";
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                return new ActualTask();
            }
        };
        worker.start();
    }
	/**
	 *
	 * done(), returns true when update task is completed and else returns false<br>
	 * <br>
	 * This method is called from MasterDataUpdateService.exeUpdate()<br>
	 *
 	 */
     boolean done() {
    	return updateOver;
    }

 	/**
	 *
	 * getMessage(), returns the present message to MasterDataUpdateService to display<br>
	 * <br>
	 * This method is called from MasterDataUpdateService.exeUpdate()<br>
	 *
 	 */
    String getMessage() {
        return statMessage;
    }

 	/**
	 *
 	 * This class communicate with local server's 'ClientRequestHandler' program<br>
 	 * for executing master update program which is exist in local server<br>
 	 *
 	 * Returns updated result to MasterDataUpdateService for displaying message<br>
 	 *
	 * This class is initiated from go()<br>
	 *
 	 */
    private class ActualTask {

 		 /** Creates new ActualTask */
 		ActualTask() {
			//send request to execute
			sendRequest();
  		}

		/**
		 *
		 * This method communicate with local server's 'ClientRequestHandler' program<br>
		 * for executing master update program which is exist in local server<br>
		 *
		 * Returns updated result to MasterDataUpdateService for displaying message<br>
		 *
		 * This method is called from ActualTask's Constructor<br>
		 *
		 */
		private boolean sendRequest() {
			//logger.finer("Method Start");
			Socket localSocket = null;
			ObjectOutputStream writer=null;
			ObjectInputStream reader = null;
			boolean sendRequestRet = false;
			int resultRet=0;
			updateOver=false;
			String clientReqIP = null;
			int clientReqPort = 0;

			try {
				//Get ClientRequestHandler's IP Address and port to communicate
				if (masterUpdateParameter != null && masterUpdateParameter.size() > 0 &&
																 masterUpdateParameter.containsKey("ClientReqHandlerIP") &&
																 masterUpdateParameter.getProperty("ClientReqHandlerIP") != null ) {
					clientReqIP=masterUpdateParameter.getProperty("ClientReqHandlerIP");
				}
				if (masterUpdateParameter != null && masterUpdateParameter.size() > 0 &&
																 masterUpdateParameter.containsKey("ClientReqHandlerPort") &&
																 new Integer(masterUpdateParameter.getProperty("ClientReqHandlerPort")).intValue() >0) {
					clientReqPort = (new Integer(masterUpdateParameter.getProperty("ClientReqHandlerPort")).intValue());
				}

				if( (clientReqIP != null) && (clientReqPort>0) ){

					//Conenct to Server
					localSocket = new Socket(clientReqIP, clientReqPort );

					if(localSocket.isConnected()){
						//logger.finer("connected to remote server");
						//Initialize output stream writer
						writer = new ObjectOutputStream(new BufferedOutputStream (localSocket.getOutputStream()));
						//Send local server Identification
						writer.write(MASTER_DATA_UPDATE);
						writer.flush();
						//Initialize input stream reader
						reader = new ObjectInputStream(new BufferedInputStream(localSocket.getInputStream()));
						//Read requested program name
						resultRet =reader.read();

						if(resultRet ==MASTER_DATA_UPDATE){
							statMessage ="マスタデータ更新中. . . . . . .";
							//logger.finer("Client system requested to execute 'Master Update' program");
							//read and Display return result
							resultRet =reader.readInt();
							switch(resultRet) {
								case (OPERATION_FAIL):
									statMessage ="異常終了：マスタデータ更新エラー。";
									//logger.info("異常終了：マスタデータ更新エラー。");
									break;

								case (UPDATE_DATA_EXST_SUC + UPDATE_FILE_EXST_SUC):
									statMessage="正常終了：データとファイルが更新されました。";
									//logger.info("正常終了：データとファイルが更新されました。");
									break;

								case (UPDATE_DATA_EXST_SUC + UPDATE_FILE_NOT_EXST_SUC):
									statMessage="正常終了：データが更新されました。";
									//logger.info("正常終了：データが更新されました。");
									break;

								case (UPDATE_DATA_NOT_EXST_SUC + UPDATE_FILE_EXST_SUC):
									statMessage="正常終了：ファイルが更新されました。";
									//logger.info("正常終了：ファイルが更新されました。");
									break;

								case (UPDATE_DATA_NOT_EXST_SUC + UPDATE_FILE_NOT_EXST_SUC):
									statMessage="正常終了：更新すべきデータとファイルがありません。";
									//logger.info("正常終了：更新すべきデータとファイルがありません。");
									break;

								default:
									statMessage="異常終了：不明なエラー";
									//logger.info("異常終了：不明なエラー");
									break;
							}
						}
						sendRequestRet=true;
					}
					else{
						statMessage="異常終了：ローカルサーバー接続に失敗しました。";
						//logger.warning("Not able to connect to Client Request handler ");
					}
				}
				//Not able to get IP and port of Client Request handler
				else{
					statMessage="異常終了：ローカルサーバー接続に失敗しました。";
					//logger.warning("Could not get IP address or/and port of Client Request handler from INI file ");
				}
			}
			catch (IOException ioe) {
				//logger.warning("IOException while communicating with Client Request handler");
				//logger.warning( "Exception details:"  + ioe );
				statMessage="異常終了：ローカルサーバー接続に失敗しました。";
				sendRequestRet=false;
			}
			catch (Exception e) {
				//logger.warning("Exception while communicating with Client Request handler");
				//logger.warning( "Exception details:"  + e );
				statMessage="異常終了：ローカルサーバー接続に失敗しました。";
				sendRequestRet=false;
			}
			finally {
				try {
					if (reader != null) {
						reader.close();
					}
					if (writer != null) {
						writer.close();
					}
					if (localSocket != null) {
						localSocket.close();
					}
				}
				catch (IOException e2) {
					//logger.warning("Exception while closing socket or writer or reader");
					//logger.warning( "Exception details:"  + e2 );
					sendRequestRet=false;
				}
			}
			updateOver = true;
			//logger.finer("Method Start");
			return sendRequestRet;
		}
	}
}