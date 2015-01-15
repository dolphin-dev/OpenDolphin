/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.util;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
//import java.util.Properties;
import java.util.logging.Level;
import open.dolphin.client.Chart;
import open.dolphin.client.ChartImpl;
import open.dolphin.client.ClientContext;
import open.dolphin.project.Project;
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
    
    //private static operLogSet operlog;
    private static funcLogSet funclog;
    private static funcLogSet logSet;
    
    public static final int LOG_LEVEL_0 = 0;    // 通常
    public static final int LOG_LEVEL_1 = 1;    // 
    public static final int LOG_LEVEL_2 = 2;    // 
    public static final int LOG_LEVEL_3 = 3;    // 詳細
    public static final int LOG_LEVEL_4 = 4;    // 
    public static final int LOG_LEVEL_5 = 5;    // 送受信Entity情報
    
    public static final int OPERATIONLOG_OP_OPERATION = 0;
    public static final int OPERATIONLOG_OP_DIALOGINFO = 1;
    
    public static final String FUNCTIONLOG_KIND_INFORMATION = "I";
    public static final String FUNCTIONLOG_KIND_ERROR = "E";
    public static final String FUNCTIONLOG_KIND_WARNING = "W";
    public static final String FUNCTIONLOG_KIND_OTHER = "O";
    
    public static final String FUNCT4OPER_MESNAME = "[OperLog]";
    
    private static boolean initLog;
    
    

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
        String loginUser = "meisa";
        String patientID = "000000001";
        //operLogSet operlog = new operLogSet(loginUser,"c:\\work\\","c:\\work\\log\\","c:\\net\\log\\");
        funcLogSet funclog = new funcLogSet("c:\\work\\","c:\\work\\log\\","c:\\net\\log\\");
        //Level0:
        /*
        funclog.log_info(1,"O","aaaa","bbb","ccc");
        funclog.log_info(0,"I","bbb","bbb");
        funclog.log_info(0,"E","ccc","ccc");
        */
        //
        //操作ログ
        //
        //arg1:".properties"ファイルパス
        //arg2:ローカル保存パス
        //arg3:ネットワークパス（マウントポイント)
        /*
        String loginUser = "meisa";
        String patientID = "000000001";
        operLogSet operlog = new operLogSet(loginUser,"c:\\work\\","c:\\work\\log\\","c:\\net\\log\\");
        */
        funclog.log_info(0,"O","aaaa","bbb","ccc");
        funclog.log_oreinfo(0,0,patientID,"1111","22222");
         funclog.log_info(0,"O","aaaa","bbb","ccc");
        funclog.log_oreinfo(0,0,patientID,"1111","22222");
         funclog.log_info(0,"O","aaaa","bbb","ccc");
        funclog.log_oreinfo(0,0,patientID,"1111","22222");
         funclog.log_info(0,"O","aaaa","bbb","ccc");
        funclog.log_oreinfo(0,0,patientID,"1111","22222");
        //operlog.log_info(1,0,"","1111","22222");
        
        
        
        //Level0:
        //funclog.log_info(0,"O","aaaa","bbb","ccc");
        //funclog.log_info(0,"I","bbb","bbb");
        //funclog.log_info(0,"E","ccc","ccc");

        /*
        DirCopy dircopy  = new DirCopy();
        boolean bk = true;
        if(bk){
            //バックアップ
            dircopy.logBackup();
        }else{
            String userID =null;// "meisa";
            dircopy.logRestore(userID);
        }
        */

    }
    
    public static void createLogSet(String _logProFilePath, String _localLogFilepath, String _logFilePath) {
        if(_logProFilePath == null || _localLogFilepath == null || _logFilePath == null) {
            return;
        }
        logSet = new funcLogSet(_logProFilePath, _localLogFilepath, _logFilePath);
        if(logSet != null) {
            initLog = true;
            setUserID("SYSTEM");
        }
    }
    
    //public static void createOperLogSet(String _loginUserID, String _logProFilePath, String _localLogFilepath, String _logFilePath) {
    //    if(_logProFilePath == null || _localLogFilepath == null || _logFilePath == null) {
    //        return;
    //    }
    //    initOperLog = true;
    //    operlog = new operLogSet(_loginUserID, _logProFilePath, _localLogFilepath, _logFilePath);
    //}
    
    public static void setUserID(String _loginUserID) {
        if(!initLog) return;
        logSet.setUserID(_loginUserID);
    }
    
    public static void setOtherName(String name) {
        if(!initLog) return;
        logSet.setOtherName(name);
    }
    
    private static void outputOperLog(Object obj, int op,int level, String... ms) {
        if(!initLog) return;
        String pid = null;
        if(obj != null) {
            if(obj instanceof ChartImpl) {
                if(((ChartImpl)obj).getPatient() != null) {
                    pid = ((ChartImpl)obj).getPatient().getPatientId();
                }else{
                	pid = "SYSTEM";
                }
            }else if(obj instanceof Chart) {
                if(((Chart)obj).getPatient() != null) {
                    pid = ((Chart)obj).getPatient().getPatientId();
                }else{
                	pid = "SYSTEM";
                }
            }else{
            	pid = "SYSTEM";
            }
        }else{
            pid = "SYSTEM";
        }
        try {
            logSet.log_oreinfo(op, level, pid, ms);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
        }
        StackTraceElement e= new Exception().getStackTrace()[1];
        String mes = e.getMethodName();
        outputFuncLog4Oper(level, mes, pid, ms);
    }
    
    public static void outputOperLogOper(Object obj, int level, String... ms) {
        outputOperLog(obj, OPERATIONLOG_OP_OPERATION, level, ms);
    }
    
    public static void outputOperLogDlg(Object obj, int level, String... ms) {
        outputOperLog(obj, OPERATIONLOG_OP_DIALOGINFO, level, ms);
    }
    
    public static void outputFuncLog(int level, String... ms) {
        if(!initLog) return;
        try {
            StackTraceElement e= new Exception().getStackTrace()[1];
            //String mes = e.getClassName() + "," + e.getMethodName();
            String mes = e.getMethodName();
            
            logSet.log_info(level, mes,ms);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void outputFuncLog4Oper(int level, String mes, String pid, String... ms) {
        if(!initLog) return;
        try {
            logSet.log_info4Oper(level, Log.FUNCTIONLOG_KIND_OTHER, mes, pid, ms);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
class funcLogSet{
    private static Logger logger; // Loggerインスタンスはクラス変数で保持
    private static Logger logger2; // Loggerインスタンスはクラス変数で保持
    public static final String  FUNC_LOG_PROPERTIES_FILE = "func_log4j.properties";
    private static FileAppender  appender;
    private static FileAppender  appender2;
    FileAppender  flieLevel;
    FileAppender  op_flieLevel;
    FileAppender  fn_flieLevel;
    FileAppender  dateLevel;
    FileAppender  ColLevel;
    FileAppender  debugLevel;
    String logFileName;
    String localLogFilepath;
    String logClientName;
    String logFilePath;
    String logProFilePath;
    String logProFile;
    //20130301
    String logSystemName;
    int logLevelSet = 0;
    int op_logLevelSet = 0;
    int fn_logLevelSet = 0;
    int _debug_level = 0;
    Boolean _debug = true;
    //common
    LogFunc log;
    
    String loginUserID; //ログインユーザーID
    String otherName; //付加する名前
    String patientID;  //患者ID
    String patientIDmark="";
    String dateLevelSet;
    String opFanameSet;
    //20130301
    String SystemName;
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
     * ユーザーIDをセットする.
     * Method setUserID.
     * @param ログインユーザーID
     * @return 
     **************************************************************************
    */
    public void setUserID(String _loginUserID) {
        loginUserID = _loginUserID;
    }
    /*
     **************************************************************************
     * ファイルに付加する名前をセットする.
     * Method setUserID.
     * @param 付加する名前
     * @return 
     **************************************************************************
    */
    public void setOtherName(String name) {
        otherName = name;
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
    void log_info(int level,String cl,String... ms) throws IOException {
        if(level > fn_logLevelSet || ms == null || ms.length <= 0){
            return;
        }
        String mes = "";
        if(_debug_level == 1){
            mes = "\t"+String.valueOf(level);
        }
        //StackTraceElement e= new Exception().getStackTrace()[1];
        mes +="\t"+"<"+ms[0]+">"+"\t";
        mes += cl+"\t";
        for(int i=1;i<ms.length-1;i++){
            mes +=ms[i]+"\t";
        }
        mes +=ms[ms.length-1];
        //System.out.println(logFileName);
        
        if(Project.getProjectStub() != null && Project.getBoolean("log.output.memory", false)) {
            try{
                DecimalFormat f1 = new DecimalFormat("#,###KB");
                DecimalFormat f2 = new DecimalFormat("##.#");
                long free = Runtime.getRuntime().freeMemory() / 1024;
                long total = Runtime.getRuntime().totalMemory() / 1024;
                long max = Runtime.getRuntime().maxMemory() / 1024;
                long used = total - free;
                String info = "Memory:Total=" + f1.format(total) + ", Used=" + f1.format(used) + ", Max=" + f1.format(max);
                mes += "\t" + info;
            }catch(Exception ex) {
            }
        }
        
        //ネットワークのチェック
        log_fileCheckPath();
        logger.info(mes);
  }
    void log_info4Oper(int level,String kind,String cl,String pid,String... ms) throws IOException {
        if(level > fn_logLevelSet || ms == null || ms.length <= 0){
            return;
        }
        String mes = "";
        if(_debug_level == 1){
            mes = "\t"+String.valueOf(level);
        }
        mes +="\t"+"<"+kind+">"+"\t";
        mes += cl+"\t";
        mes += pid+"\t";
        mes += Log.FUNCT4OPER_MESNAME+"\t";
        for(int i=0;i<ms.length-1;i++){
            mes +=ms[i]+"\t";
        }
        mes +=ms[ms.length-1];
        
        log_fileCheckPath();
        logger.info(mes);
  }
     void log_oreinfo(int op,int level,String _patientID,String... ms) throws IOException {
        String mes;
        if(level > op_logLevelSet || ms == null || ms.length <= 0){
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
        
        if(Project.getProjectStub() != null && Project.getBoolean("log.output.memory", false)) {
            try{
                DecimalFormat f1 = new DecimalFormat("#,###KB");
                DecimalFormat f2 = new DecimalFormat("##.#");
                long free = Runtime.getRuntime().freeMemory() / 1024;
                long total = Runtime.getRuntime().totalMemory() / 1024;
                long max = Runtime.getRuntime().maxMemory() / 1024;
                long used = total - free;
                String info = "Memory:Total=" + f1.format(total) + ", Used=" + f1.format(used) + ", Max=" + f1.format(max);
                mes += "\t" + info;
            }catch(Exception ex) {
            }
        }
        
        log_fileCheckPathOper();
        logger2.info(mes);
        //log_error();
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
         //String localLogFileName  = localLogFilepath + log.CurrentDateTime("YYYYMMDD")+"_"+logClientName+".log";
         //String remoteLogFileName= logFilePath + log.CurrentDateTime("YYYYMMDD")+"_"+logClientName+".log";
         String localLogFileName = null;
         String remoteLogFileName = null;
         if(otherName == null || otherName.length() <= 0) {
            localLogFileName = localLogFilepath + log.CurrentDateTime("YYYYMMDD")+"_"+logClientName+".log";
            remoteLogFileName = logFilePath + log.CurrentDateTime("YYYYMMDD")+"_"+logClientName+".log";
         }else{
            localLogFileName = localLogFilepath + log.CurrentDateTime("YYYYMMDD")+"_"+logClientName+"_"+otherName+".log";
            remoteLogFileName = logFilePath + log.CurrentDateTime("YYYYMMDD")+"_"+logClientName+"_"+otherName+".log";
         }
         
         boolean ret = log.log_fileCheckPath(logFilePath, localLogFileName, remoteLogFileName);
         if(ret){
             logFileName = remoteLogFileName;
         }else{
             logFileName = localLogFileName;
         }
        
        appender = (FileAppender)logger.getAppender("B");
        //System.out.println("--------FUNC---------->"+logFileName);
        appender.setFile(logFileName);
        appender.activateOptions();
     }
     //
     //
     //
     //
     //
      void log_fileCheckPathOper() throws IOException{
         //Uniteaマシンが正常動作しているか否かのチェック
        String localLogFileName;
        String remoteLogFileName;
        boolean withDate = true;
        //dateLevelSet = "YYYYMMDD";
        if(dateLevelSet.equals("NODIVIDE")){
            withDate = false;
        }
        /*-- 20130305 update
        if(withDate){
            remoteLogFileName = logFilePath + opFanameSet+"_"+ log.CurrentDateTime(dateLevelSet)+"_"+logClientName+".log";
            localLogFileName =  localLogFilepath + opFanameSet+"_"+ log.CurrentDateTime(dateLevelSet)+"_"+logClientName+".log";
        }else{
            remoteLogFileName = logFilePath + opFanameSet+"_"+logClientName+".log";
            localLogFileName  = localLogFilepath + opFanameSet+"_"+logClientName+".log";
        }
        */
        //if(withDate){
        //    remoteLogFileName = logFilePath + log.CurrentDateTime(dateLevelSet)+"_"+opFanameSet+"_"+logClientName+".log";
        //    localLogFileName =  localLogFilepath + log.CurrentDateTime(dateLevelSet)+"_"+opFanameSet+"_"+logClientName+".log";
        //}else{
        //    remoteLogFileName = logFilePath + opFanameSet+"_"+logClientName+".log";
        //    localLogFileName  = localLogFilepath + opFanameSet+"_"+logClientName+".log";
        //}
        if(otherName == null || otherName.length() <= 0) {
            if(withDate){
                remoteLogFileName = logFilePath + log.CurrentDateTime(dateLevelSet)+"_"+opFanameSet+"_"+logClientName+".log";
                localLogFileName =  localLogFilepath + log.CurrentDateTime(dateLevelSet)+"_"+opFanameSet+"_"+logClientName+".log";
            }else{
                remoteLogFileName = logFilePath + opFanameSet+"_"+logClientName+".log";
                localLogFileName  = localLogFilepath + opFanameSet+"_"+logClientName+".log";
            }
        }else{
            if(withDate){
                remoteLogFileName = logFilePath + log.CurrentDateTime(dateLevelSet)+"_"+opFanameSet+"_"+logClientName+"_"+otherName+".log";
                localLogFileName =  localLogFilepath + log.CurrentDateTime(dateLevelSet)+"_"+opFanameSet+"_"+logClientName+"_"+otherName+".log";
            }else{
                remoteLogFileName = logFilePath + opFanameSet+"_"+logClientName+"_"+otherName+".log";
                localLogFileName  = localLogFilepath + opFanameSet+"_"+logClientName+"_"+otherName+".log";
            }
        }
        // String localLogFileName  = localLogFilepath + log.CurrentDateTime(dateLevelSet)+"_"+logClientName+".log";
        // String remoteLogFileName= logFilePath + log.CurrentDateTime(dateLevelSet)+"_"+logClientName+".log";
         
         boolean ret = log.log_fileCheckPath(logFilePath, localLogFileName, remoteLogFileName);
         if(ret){
             logFileName = remoteLogFileName;
         }else{
             logFileName = localLogFileName;
         }
         appender2 = (FileAppender)logger2.getAppender("A");
         //System.out.println("----OPRE------->"+logFileName);
         appender2.setFile(logFileName);
         appender2.activateOptions();  
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
        File func_log4jProp = new File(logProFile);
        if(func_log4jProp.exists()) {
            PropertyConfigurator.configure(logProFile);
        }else{
             try {
                Properties prop = new Properties();
                BufferedInputStream in = new BufferedInputStream(ClientContext.getResourceAsStream("func_log4j.properties"));
                prop.load(in);
                in.close();
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(func_log4jProp));
                prop.store(out, ClientContext.getVersion());
                out.close();
                PropertyConfigurator.configure(prop);
             } catch (IOException ex) {
                 java.util.logging.Logger.getLogger(funcLogSet.class.getName()).log(Level.SEVERE, null, ex);
             }
        }
        logger = Logger.getLogger("FUNC");  //処理ログ
        logger2 = Logger.getLogger("OPRE"); //操作ログ
        /*
        //操作ログファイル名
        dateLevel = (FileAppender)logger2.getAppender("YMD");
        //dateLevelSet =  dateLevel.getFile();
        String fnameStr =  dateLevel.getFile();
        String[] fnameAry;
        fnameAry = fnameStr.split(",");
        opFanameSet = fnameAry[0];
        dateLevelSet =  fnameAry[1];
       
        //dateLevelSet = "YYYYMMDD";
        //操作ログファイル名
        op_flieLevel = (FileAppender)logger2.getAppender("OPLEVEL");
        op_logLevelSet =  Integer.valueOf( op_flieLevel.getFile());
        //操作ログファイル名
        fn_flieLevel = (FileAppender)logger.getAppender("FNLEVEL");
        fn_logLevelSet =  Integer.valueOf( fn_flieLevel.getFile());
        //DEBUG用
        debugLevel = (FileAppender)logger.getAppender("DLEVEL");
        if(debugLevel != null){
            _debug_level = Integer.valueOf( debugLevel.getFile());
        }
        */
                /*
       appender2.setFile(null);
       appender2.activateOptions();
       appender.setFile(null);
       appender.activateOptions();  
       */
        //出力ファイル名設定
        localLogFilepath = _localLogFilepath;//"c:\\work\\log\\";
        logFilePath = _logFilePath;//"c:\\net\\log\\";//"\\\\192.168.1.5\\UniteaTani\\log\\";//"c:\\work\\";
        //コンピューター名
        try {
            logClientName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            java.util.logging.Logger.getLogger(funcLogSet.class.getName()).log(Level.SEVERE, null, ex);
            // Mavericks対応
            logClientName = "localhost";
        }
        //20130301
        opFanameSet ="";
        SystemName  ="";
        dateLevelSet="NODIVIDE";
        op_logLevelSet = 0;
        _debug_level = 0;
        
        try{
             File file = new File(logProFile);
            BufferedReader br = new BufferedReader(new FileReader(file));

            String str = br.readLine();
            String w;
            while(str != null){
                 if(str.startsWith("//")){
                    System.out.println(str);
                    str = str.replaceAll("\t","");
                    str = str.replaceAll(" ","");
                    String[] strAry = str.split("=");
                    for (int i=0; i<strAry.length; i++) {
                        if(strAry[i].toString().indexOf("FILE_NAME") != -1){
                            opFanameSet = strAry[++i].toString();
                            break;
                        }
                        if(strAry[i].toString().indexOf("SYSTEM") != -1){
                            SystemName = strAry[++i].toString();
                            break;
                        }
                        if(strAry[i].toString().indexOf("YMD") != -1){
                            dateLevelSet = strAry[++i].toString();
                            break;
                        }
                        if(strAry[i].toString().indexOf("OPLEVEL") != -1){
                            op_logLevelSet = Integer.valueOf(strAry[++i].toString());
                            break;
                        }
                        if(strAry[i].toString().indexOf("FNLEVEL") != -1){
                            fn_logLevelSet = Integer.valueOf(strAry[++i].toString());
                            break;
                        }
                        if(strAry[i].toString().indexOf("DLEVEL") != -1){
                            _debug_level = Integer.valueOf(strAry[++i].toString());
                            break;
                        }
                        System.out.println(strAry[i]);
                    }
                }
                str = br.readLine();
            }

            br.close();
            }catch(FileNotFoundException e){
                System.out.println(e);
            }catch(IOException e){
                System.out.println(e);
            }
        
        logClientName = SystemName+"_";
        try {
            logClientName += InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            java.util.logging.Logger.getLogger(funcLogSet.class.getName()).log(Level.SEVERE, null, ex);
            // Mavericks対応
            logClientName += "localhost";
        }
        //DEBUG
        if(_debug){
            //op_flieLevel.
            //System.out.println("---argv1:------->"+op_flieLevel.toString());
            //System.out.println("---argv1:------->"+op_flieLevel.getName());
            System.out.println("---argv1:------->"+_logProFilePath);
            System.out.println("---argv2:------->"+_localLogFilepath);
            System.out.println("---argv3:------->"+logProFile);
            System.out.println("---OPLEVEL------->"+op_logLevelSet);
            System.out.println("---FNLEVEL------->"+fn_logLevelSet);
            System.out.println("---YMD--0----->"+opFanameSet);
            System.out.println("---YMD--1----->"+dateLevelSet);
        }
        
        
        
        
     }
     
}

