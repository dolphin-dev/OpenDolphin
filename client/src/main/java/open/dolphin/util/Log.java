/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.util;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
//import java.util.Date;


//これを使う
//処理ログ
//------------------------------------------------------------------------------
// yyyymmdd_CIS001.log
// yyyymmdd_CIS002.log
//
//------------------------------------------------------------------------------
/**
 * Log出力対応(G-01)
 * @author tani
 */

public class Log {
    
    private static operLogSet operlog;
    private static funcLogSet funclog;
    
    public static final int OPERATIONLOG_LEVEL_0 = 0;
    public static final int OPERATIONLOG_LEVEL_1 = 1;
    public static final int OPERATIONLOG_LEVEL_2 = 2;
    public static final int OPERATIONLOG_LEVEL_3 = 3;
    
    public static final int OPERATIONLOG_OP_OPERATION = 0;
    public static final int OPERATIONLOG_OP_DIALOGINFO = 1;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        System.out.println("LogStart!!");
        //
        //処理ログ
        //
        //arg1:".properties"ファイルパス
        //arg2:ローカル保存パス
        //arg3:ネットワークパス（マウントポイント)
        funcLogSet funclog = new funcLogSet("c:\\work\\","c:\\work\\log\\","c:\\net\\log\\");
        //Level0:
        funclog.log_info(1,"O","aaaa","bbb","ccc");
        funclog.log_info(0,"I","bbb","bbb");
        funclog.log_info(0,"E","ccc","ccc");

        //
        //操作ログ
        //
        //arg1:".properties"ファイルパス
        //arg2:ローカル保存パス
        //arg3:ネットワークパス（マウントポイント)
        String loginUser = "meisa";
        String patientID = "000000001";
        operLogSet operlog = new operLogSet(loginUser,"c:\\work\\","c:\\work\\log\\","c:\\net\\log\\");
        
        operlog.log_info(0,0,patientID,"1111","22222");
        operlog.log_info(1,0,"","1111","22222");
        
        
        //ディレクトリコピー
        DirCopy dircopy  = new DirCopy();
        String srcDir = "c:\\net\\log";
        String objDir = "c:\\back\\log";
        
        dircopy.copyDirectry(srcDir, objDir);
        
    }
    
    public static void createOperLogSet(String _loginUserID, String _logProFilePath, String _localLogFilepath, String _logFilePath) {
        operlog = new operLogSet(_loginUserID, _logProFilePath, _localLogFilepath, _logFilePath);
    }
    
    public static void outputOperLog(int op,int level,String _patientID, String... ms) {
        try {
            operlog.log_info(op, level, _patientID, ms);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void createFuncLogSet(String _logProFilePath, String _localLogFilepath, String _logFilePath) {
        funclog = new funcLogSet(_logProFilePath, _localLogFilepath, _logFilePath);
    }
    
    public static void outputFuncLog(int level, String... ms) {
        try {
            funclog.log_info(level, ms);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
class funcLogSet{
    private static Logger logger; // Loggerインスタンスはクラス変数で保持
    public static final String  FUNC_LOG_PROPERTIES_FILE = "func_log4j.properties";
    FileAppender  appender;
    FileAppender  flieLevel;
    String logFileName;
    String localLogFilepath;
    String logClientName;
    String logFilePath;
    String logProFilePath;
    String logProFile;// = "oper_log4j.properties";
    int logLevelSet = 0;
    Boolean _debug = true;
    //common
    LogFunc log;
    /*
     **************************************************************************
     * LOGを出力する.
     * Method funcLogSet.
     * @param .propertiesファイルパス名
     * @param LOGファイル保存パス名（ローカルパス)
     * @param LOGファイル保存パス名（ネットワークパス)
     * @return 
     **************************************************************************
    */
    public funcLogSet(String _logProFilePath,String _localLogFilepath,String _logFilePath) {
        log = new LogFunc();
        log_initSet(_logProFilePath,_localLogFilepath,_logFilePath);
         
        if(_debug){
            System.out.println(logFileName);
            System.out.println(logLevelSet);
        }
    }    
    /*
     **************************************************************************
     * LOGを出力する.
     * Method log_info.
     * @param level(出力レベル)0...4
     * @param 出力文字(ms[0......n]
     * @return 
     **************************************************************************
    */ 
    void log_info(int level,String... ms) throws IOException {
        if(level > logLevelSet){
            return;
        }
        String mes ="\t"+"<"+ms[0]+">"+"\t";
        for(int i=1;i<ms.length-1;i++){
            mes +=ms[i]+"\t";
        }
        mes +=ms[ms.length-1];
        System.out.println(logFileName);
        
        //ネットワークのチェック
        log_fileCheckPath();
        logger.info(mes);
  }
    /*
     **************************************************************************
     * ディレクトリをチェックする.
     * Method log_fileCheckPath.
     * @param 
     * @param 
     * @return 
     **************************************************************************
     */ 
     void log_fileCheckPath() throws IOException{
         //Uniteaマシンが正常動作しているか否かのチェック
         String localLogFileName  = localLogFilepath + log.CurrentDateTime("YYYYMMDD")+"_"+logClientName+".log";
         String remoteLogFileName= logFilePath + log.CurrentDateTime("YYYYMMDD")+"_"+logClientName+".log";
         
         boolean ret = log.log_fileCheckPath(logFilePath, localLogFileName, remoteLogFileName);
         if(ret){
             logFileName = remoteLogFileName;
         }else{
             logFileName = localLogFileName;
         }
         appender.setFile(logFileName);
         appender.activateOptions();  
     }
     /*
     **************************************************************************
     * パラメタをセットする.
     * Method log_initSet.
     * @param .propertiesファイルパス名
     * @param LOGファイル保存パス名（ローカルパス)
     * @param LOGファイル保存パス名（ネットワークパス)
     * @return 
     **************************************************************************
     */  
     private void log_initSet(String _logProFilePath,String _localLogFilepath,String _logFilePath){
        //読み込みファイル設定
        //PropertiesFile
        logProFilePath = _logProFilePath;//"c:\\work\\";
        logProFile = FUNC_LOG_PROPERTIES_FILE;//"func_log4j.properties";
        
        logProFile = logProFilePath+logProFile;         
         PropertyConfigurator.configure(logProFile);
        logger = Logger.getLogger("test");
        //出力ファイル名設定
        localLogFilepath = _localLogFilepath;//"c:\\work\\log\\";
        logFilePath = _logFilePath;//"c:\\net\\log\\";//"\\\\192.168.1.5\\UniteaTani\\log\\";//"c:\\work\\";
        //コンピューター名
        try {
            logClientName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            java.util.logging.Logger.getLogger(funcLogSet.class.getName()).log(Level.SEVERE, null, ex);
        }
        logFileName = logFilePath + log.CurrentDateTime("YYYYMMDD")+"_"+logClientName+".log";
        appender = (FileAppender)logger.getParent().getAppender("file");
        System.out.println("appender.setFile:Start");
        /*
        appender.setFile(logFileName);
        appender.activateOptions();
        */
        //Leveln
         flieLevel = (FileAppender)logger.getParent().getAppender("Level");
         logLevelSet = Integer.valueOf( flieLevel.getFile());
     }
     
}
//------------------------------------------------------------------------------
//操作ログ
// class operLogSet
// 
//
//------------------------------------------------------------------------------
class operLogSet{
    private static Logger logger; // Loggerインスタンスはクラス変数で保持
    public static final String  OPER_LOG_PROPERTIES_FILE = "oper_log4j.properties";
    FileAppender  appender;
    FileAppender  flieLevel;
    FileAppender  dateLevel;
    FileAppender  ColLevel;
    String logFileName;
    String localLogFilepath;
    String logClientName;
    String logFilePath;
    String logProFilePath;
    String logProFile;// = "oper_log4j.properties";
    int logLevelSet = 0;
    String dateLevelSet;
    String loginUserID; //ログインユーザーID
    String patientID;  //患者ID
    String patientIDmark="";
    Boolean _debug = true;
    int  idColumns = 10; //default
    //common
    LogFunc log;
    /*
     **************************************************************************
     * LOGを出力する.
     * Method operLogSet.
     * @param ログインユーザーID
     * @param 患者ID 
     * @param .propertiesファイルパス名
     * @param LOGファイル保存パス名（ローカルパス)
     * @param LOGファイル保存パス名（ネットワークパス)
     * @return 
     **************************************************************************
    */
    public operLogSet(String _loginUserID,String _logProFilePath,String _localLogFilepath,String _logFilePath) {
        loginUserID = _loginUserID;
        //patientID = _patientID;
        
        log = new LogFunc();
        log_initSet(_logProFilePath,_localLogFilepath,_logFilePath);
        
        if(_debug){
            System.out.println(logFileName);
            System.out.println(logLevelSet);
        }

    }
  /*
     **************************************************************************
     * LOGを出力する.
     * Method log_info.
     * @param 0:(OPERATION) 1:(DIALOGINFO) else:(OTHER)
     * @param level(出力レベル)0...4
     * @param 出力文字(ms[0......n]
     * @return 
     **************************************************************************
    */ 
    void log_info(int op,int level,String _patientID,String... ms) throws IOException {
        String mes;
        if(level > logLevelSet){
            return;
        }
         patientID = _patientID;
         if(_patientID == null){
             patientID = patientIDmark;
         }else if(_patientID.length() == 0){
             patientID = patientIDmark;
         }
         
        if(op == 0){
            mes ="\t"+loginUserID+"\t"+patientID+"\t"+"(OPERATION)"+"\t";
        }else if(op ==1){
            mes ="\t"+loginUserID+"\t"+patientID+"\t"+"(DIALOGINFO)"+"\t";
        }else {
            mes ="\t"+loginUserID+"\t"+patientID+"\t"+"(OTHER)"+"\t";
        }
        //String mes ="\t"+loginUserID+"\t"+patientID+"\t";
        for(int i=0;i<ms.length-1;i++){
            mes +=ms[i]+"\t";
        }
        mes +=ms[ms.length-1];
        log_fileCheckPath();
        logger.info(mes);
        //log_error();
    }
    /*
     void log_info(int level,String... ms) throws IOException {
        if(level > logLevelSet){
            return;
        }
        String mes ="\t"+loginUserID+"\t"+patientID+"\t";
        for(int i=0;i<ms.length-1;i++){
            mes +=ms[i]+"\t";
        }
        mes +=ms[ms.length-1];
        log_fileCheckPath();
        logger.info(mes);
        //log_error();
    }
    */
    /*
     **************************************************************************
     * パラメタをセットする.
     * Method log_initSet.
     * @param .propertiesファイルパス名
     * @param LOGファイル保存パス名（ローカルパス)
     * @param LOGファイル保存パス名（ネットワークパス)
     * @return 
     **************************************************************************
     */ 
     private void log_initSet(String _logProFilePath,String _localLogFilepath,String _logFilePath){
        //読み込みファイル設定
        boolean withDate = true;
        logProFilePath = _logProFilePath;//"c:\\work\\";
        logProFile = OPER_LOG_PROPERTIES_FILE;
        
        logProFile = logProFilePath+logProFile;         
         PropertyConfigurator.configure(logProFile);
        logger = Logger.getLogger("test");
        //出力ファイル名設定
        localLogFilepath = _localLogFilepath;
        logFilePath = _logFilePath;//"c:\\work\\";
        //コンピューター名
        try {
            logClientName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            java.util.logging.Logger.getLogger(funcLogSet.class.getName()).log(Level.SEVERE, null, ex);
        }
        ColLevel = (FileAppender)logger.getParent().getAppender("IDColumns");
        idColumns = Integer.valueOf( ColLevel.getFile());
        //
        for(int i=0;i<idColumns;i++){
            patientIDmark +="-";
        }
        
        
        flieLevel = (FileAppender)logger.getParent().getAppender("Level");
        logLevelSet = Integer.valueOf( flieLevel.getFile());
        dateLevel = (FileAppender)logger.getParent().getAppender("YMD");
        dateLevelSet =  dateLevel.getFile();
        if(dateLevelSet.equals("NODIVIDE")){
            withDate = false;
        }
        //logLevelSet = 0;
        //日付単位?
        if(withDate){
            logFileName = logFilePath + "HIPPA_"+ log.CurrentDateTime(dateLevelSet)+"_"+logClientName+".log";
        }else{
            logFileName = logFilePath + "HIPPA_"+logClientName+".log";
        }
        appender = (FileAppender)logger.getParent().getAppender("file");
      }
    /*
     **************************************************************************
     * ディレクトリをチェックする.
     * Method log_fileCheckPath.
     * @param 
     * @param 
     * @return 
     **************************************************************************
     */    
      void log_fileCheckPath() throws IOException{
         //Uniteaマシンが正常動作しているか否かのチェック
        String localLogFileName;
        String remoteLogFileName;
        boolean withDate = true;
        if(dateLevelSet.equals("NODIVIDE")){
            withDate = false;
        }
        if(withDate){
            remoteLogFileName = logFilePath + "HIPPA_"+ log.CurrentDateTime(dateLevelSet)+"_"+logClientName+".log";
            localLogFileName =  localLogFilepath + "HIPPA_"+ log.CurrentDateTime(dateLevelSet)+"_"+logClientName+".log";
        }else{
            remoteLogFileName = logFilePath + "HIPPA_"+logClientName+".log";
            localLogFileName  = localLogFilepath + "HIPPA_"+logClientName+".log";
        }
        // String localLogFileName  = localLogFilepath + log.CurrentDateTime(dateLevelSet)+"_"+logClientName+".log";
        // String remoteLogFileName= logFilePath + log.CurrentDateTime(dateLevelSet)+"_"+logClientName+".log";
         
         boolean ret = log.log_fileCheckPath(logFilePath, localLogFileName, remoteLogFileName);
         if(ret){
             logFileName = remoteLogFileName;
         }else{
             logFileName = localLogFileName;
         }
         appender.setFile(logFileName);
         appender.activateOptions();  
      }
}
//------------------------------------------------------------------------
// class DirCopy
//
//
//
//------------------------------------------------------------------------
class DirCopy {
    boolean copyFileBinaly(File sFile,File tFile) throws IOException{
	// ファイルの存在を確認
        if(!sFile.exists()) {
            return false;
        }
        byte[] buf = new byte[1024];
        int iSize;
        BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(sFile));
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(tFile));
        while((iSize=inputStream.read(buf,0,buf.length)) != -1){
            outputStream.write(buf, 0, iSize);
        }
        return true;
    }
	
    /*
     **************************************************************************
     * ディレクトリをコピーする.
     * Method copyDirectry.
     * @param sourceDirectry	コピー元のディレクトリ
     * @param targetDirectry	コピー先のディレクトリ
     * @return boolean 指定されたコピー元ディレクトリがディレクトリでなかったり存在しないときはfalseを返す。
     **************************************************************************
     */
    boolean copyDirectry(File sDirectry, File tDirectry) throws IOException{
        // コピー元がディレクトリでない場合はfalseを返す
        if(!sDirectry.exists() || !sDirectry.isDirectory()) {
            return false;
        }
        // ディレクトリを作成する
        tDirectry.mkdirs();
        // ディレクトリ内のファイルをすべて取得する
        File[] files = sDirectry.listFiles();

        // ディレクトリ内のファイルに対しコピー処理を行う
        for(int i = 0; files.length>i; i++){
            if(files[i].isDirectory()){
		// ディレクトリだった場合は再帰呼び出しを行う
		copyDirectry(
			new File(sDirectry.toString(), files[i].getName()), 
			new File(tDirectry.toString(), files[i].getName()));
            }else{
			// ファイルだった場合はファイルコピー処理を行う
		copyFileBinaly(
			new File(sDirectry.toString(), files[i].getName()), 
			new File(tDirectry.toString(), files[i].getName()));			
            }			
        }
        return true;
    }
    /*
     *************************************************************************** 
     * ディレクトリをコピーします.
     * Method copyDirectry.
     * @param sourceDirectry	コピー元のディレクトリ
     * @param targetDirectry	コピー先のディレクトリ
     * @return boolean 指定されたコピー元ディレクトリがディレクトリでなかったり存在しないときはfalseを返す。
     * **************************************************************************
     */
    boolean copyDirectry(String sDirectry, String tDirectry) throws IOException{
        return copyDirectry(new File(sDirectry), new File(tDirectry));
    }
}
