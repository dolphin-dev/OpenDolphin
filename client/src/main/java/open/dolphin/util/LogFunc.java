package open.dolphin.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Calendar;
import java.util.logging.Level;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Log出力対応(G-01)
 * @author tani
 */
public class LogFunc {
    void log_fatal() {
        //logger.fatal("fatal");
    }
    //--------------------------------------------------------------------------
    // argv:
    //　String _logFilePath         ：チェック用ネットワークパス
    //  String localLogFileName     :Localパス
    //  String remoteLogFileName    :Remoteパス
    // return
    //--------------------------------------------------------------------------
     boolean  log_fileCheckPath(String _logFilePath,String localLogFileName,String remoteLogFileName) throws IOException{
         //Uniteaマシンが正常動作しているか否かのチェック
        // String localLogFileName  = localLogFilepath + CurrentDateTime("YYYYMMDD")+"_"+logClientName+".log";
        // String remoteLogFileName= logFilePath + CurrentDateTime("YYYYMMDD")+"_"+logClientName+".log";
         
         System.out.println("network mount check:Start");
         File chk = new File(_logFilePath);
         String str = null;
         boolean ret;
         ret = false;
        
         //if(true) {
         if(chk.exists()) {
             str = "OK";
              File localF = new File(localLogFileName);
              if(localF.exists()) {
                  File remoteF = new File(remoteLogFileName);
                  if(remoteF.exists()){
                     //ファイル追記
                    log_fileMarge(localLogFileName,remoteLogFileName);
                    //locaFile削除
                    localF.delete();
                  }else{
                      //ファイルコピー
                    log_copyTransfer(localLogFileName,remoteLogFileName);
                  }
              }
              ret = true;
         }else{
             str = "NG";
             ret = false;
         }
         return(ret);
         
     }
    //--------------------------------------------------------------------------
    //
    //
    //
    //--------------------------------------------------------------------------
     void log_fileMarge(String in_file,String out_file){
        System.out.println("---------------------->");
        System.out.println(in_file);
        System.out.println(out_file);
        
        
	String str=null;
        FileReader in_f = null;
        try {
            in_f = new FileReader(in_file);
        } catch (FileNotFoundException ex) {
            java.util.logging.Logger.getLogger(funcLogSet.class.getName()).log(Level.SEVERE, null, ex);
        }
        BufferedReader b = new BufferedReader(in_f);
        File ofile = new File(out_file);
        FileWriter filewriter=null;
        try {
            filewriter = new FileWriter(ofile,true);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(funcLogSet.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try{
            System.out.println("-Start-------------");
             while ((str = b.readLine()) != null) {
                   filewriter.write(str+System.getProperty("line.separator"));
                   System.out.println(str);
            }
            System.out.println("-End-----------------");
        }catch(IOException ex){
            
        }
        try {
            filewriter.flush();
            b.close();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(funcLogSet.class.getName()).log(Level.SEVERE, null, ex);
        }    
    }
     void log_copyTransfer(String srcPath, String destPath) 
        throws IOException {
    
         FileChannel srcChannel = new
         FileInputStream(srcPath).getChannel();
         FileChannel destChannel = new
         FileOutputStream(destPath).getChannel();
         try {
            srcChannel.transferTo(0, srcChannel.size(), destChannel);
        } finally {
            srcChannel.close();
             destChannel.close();
        }

    }
    String CurrentDateTime(String fmt){
        String a="";
        int hour=0;
        int minute=0;
        int second=0;
        Calendar cal1 = Calendar.getInstance();  //(1)オブジェクトの生成

        int year = cal1.get(Calendar.YEAR);        //(2)現在の年を取得
        int month = cal1.get(Calendar.MONTH) + 1;  //(3)現在の月を取得
        int day = cal1.get(Calendar.DATE);         //(4)現在の日を取得
        if(fmt.equals("YYYYMMDDHHMMSS")){
            hour = cal1.get(Calendar.HOUR_OF_DAY); //(5)現在の時を取得
            minute = cal1.get(Calendar.MINUTE);    //(6)現在の分を取得
            second = cal1.get(Calendar.SECOND);    //(7)現在の秒を取得
        }

        a += Integer.toString(year);
        if(fmt.equals("YYYY")){
            return(a);
        }
        if(month < 10) {
             a += "0";
        }
        a += Integer.toString(month);
        if(day < 10) {
             a += "0";
        }
        if(fmt.equals("YYYYMM")){
            return(a);
        }
        a += Integer.toString(day);
        if(fmt.equals("YYYYMMDDHHMMSS")){
            if(hour < 10) {
                a += "0";
            }
             a += Integer.toString(hour);
            if(minute < 10) {
                a += "0";
            }
            a += Integer.toString(minute);
            //if(fmt.equals("YYYYMMDDHHMMSS")){
                if(second < 10) {
                a += "0";
            }
                a += Integer.toString(second);
            //}
        }
  
        return(a);
    }
    
}
