/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import open.dolphin.project.Project;

/**
 * データバンクデータ復元(D-02)
 * @author Life Sciences Computing Corporation.
 */
public class DirCopy {
    private static final String BACKUP_FILE_NAME = "BackupDate.txt";
    
    boolean copyFileBinaly(File sFile,File tFile) throws IOException{
	// ファイルの存在を確認
        if(!sFile.exists()) {
            return false;
        }
        byte[] buf = new byte[1024];
        int iSize;
        BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(sFile));
        try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(tFile))) {
            while((iSize=inputStream.read(buf,0,buf.length)) != -1){
                outputStream.write(buf, 0, iSize);
            }
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
    /*
    boolean copyDirectry(String sDirectry, String tDirectry) throws IOException{
        
        
        
        return copyDirectry(new File(sDirectry), new File(tDirectry));
    }
    */
    /*
     *************************************************************************** 
     * ディレクトリをコピーします.
     * Method logBackup
     * @return Normal:0  コピー失敗:1   日付ファイル作成失敗:2
     * **************************************************************************
     */
    public int logBackup() throws IOException{
 
        // Mavericks対応
        //String pcName = InetAddress.getLocalHost().getHostName();
        String pcName;
        try {
            pcName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            java.util.logging.Logger.getLogger(DirCopy.class.getName()).log(Level.SEVERE, null, ex);
            pcName = "localhost";
        }
        //for Windows.
        String user_name = System.getProperty("user.name"); 
        //System.out.println(user_name);
        //.propatiesから取得
        //
        String pDir1 =Project.getString("unitea.back.dir");
        String pDir2 =Project.getString("unitea.back.filedir");
        String sDir=System.getProperty("user.home");
        String tDir=Project.getString("unitea.back.backdir");
        //
        
        String pDir[] = new String[2];
        pDir[0] = pDir1;
        pDir[1] = pDir2;
        //String[] fDir;
        //String fDir[] = new String[100];
        boolean ret;
        String cDir;
        String dDir;
        
        if(pDir1 == null || pDir2 == null || sDir == null || tDir == null) {
            return 3;
        }
        
        String[] dirAry;
        for(int j=0;j<2;j++){
             dirAry = pDir[j].split(",");
              for(int i=0;i<dirAry.length;i++){
                cDir =sDir+"\\"+dirAry[i];
                dDir =tDir+"\\"+pcName+"\\"+"["+user_name+"]"+"\\"+dirAry[i];
                System.out.println(cDir);
                System.out.println(dDir);
                ret = copyDirectry(new File(cDir), new File(dDir));
            }
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String timeStamp = sdf.format(new Date());
        
        //日付ファイル
        String bfileName = tDir+"\\"+BACKUP_FILE_NAME;
        try{
            File file = new File(bfileName);
            try (FileWriter filewriter = new FileWriter(file)) {
                filewriter.write(timeStamp);
            }
        }catch(IOException e){
            System.out.println(e);
            return 2;
        }
        return 0;
     }
    /*
     *************************************************************************** 
     * ディレクトリをコピーします.
     * Method logRestore
     * @param userID
     * memo:userID == null ALL restore
     * @return Normal:0  コピー失敗:1   
     * **************************************************************************
     */
    public int logRestore(String userID) throws IOException{
        // Mavericks対応
        //String pcName = InetAddress.getLocalHost().getHostName();
        String pcName;
        try {
            pcName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            java.util.logging.Logger.getLogger(DirCopy.class.getName()).log(Level.SEVERE, null, ex);
            pcName = "localhost";
        }
        //for Windows.
        String user_name = System.getProperty("user.name"); 
        boolean allCopy;
        if(userID == null){
            allCopy = true;
        }else if(userID.length() == 0){
            allCopy = true;
        }else{
             allCopy = false;
        }
 
        //.propatiesから取得
        //
        String pDir1 =Project.getString("unitea.restore.dir");
        String pDir2 =Project.getString("unitea.restore.filedir");
        String sDir=System.getProperty("user.home");
        String tDir=Project.getString("unitea.restore.backdir");
        //
        
        String pDir[] = new String[2];
        pDir[0] = pDir1;
        pDir[1] = pDir2;
        //String[] fDir;
        //String fDir[] = new String[100];
        boolean ret;
        String cDir;
        String dDir;
        
        if(pDir1 == null || pDir2 == null || sDir == null || tDir == null) {
            return 3;
        }
        
        String[] dirAry;
        int cnt = 2;
        if(allCopy == false){
            cnt = 1;
            //cDir = sDir+"\\"+pDir2+"\\";
            
           //dDir = tDir+"\\"+pcName+"\\"+"["+user_name+"]"+"\\"+pDir2+"\\"+userID+"*";
           dDir = tDir+"\\"+pcName+"\\"+"["+user_name+"]"+"\\"+pDir2;
           System.out.println(dDir);
           File dir = new File(dDir);
           File[] files = dir.listFiles();

            System.out.println(userID);
            String findStr =  "\\"+userID+"_";
            System.out.println(findStr);
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if(file.toString().contains(findStr)){
                    //System.out.println((i + 1) + "A    " + file);
                    cDir = sDir+"\\"+pDir2+"\\";
                    cDir += getFileName(file.toString());
                    System.out.println(file.toString()+"->"+cDir);
                    boolean bRet = copyFileBinaly(new File(file.toString()),new File(cDir));
                }
            }
        }
        
        for(int j=0;j<cnt;j++){
             dirAry = pDir[j].split(",");
              for(int i=0;i<dirAry.length;i++){
                cDir =sDir+"\\"+dirAry[i];
                dDir =tDir+"\\"+pcName+"\\"+"["+user_name+"]"+"\\"+dirAry[i];
                System.out.println(cDir);
                System.out.println(dDir);
                ret = copyDirectry(new File(dDir), new File(cDir));
                if(ret){
                    
                    //return 1;
                }else{
                    
                }
            }
        }
        return 0;
     }
     /*
     *************************************************************************** 
     * ファイル名のとりだし.
     * Method getFileName
     * @param fname(fullpath)
     * @return fname only.   
     * **************************************************************************
     */
    String getFileName(String fname){
        String retFname = "";
        char[] c=fname.toCharArray();
        for (int i = 0; i < c.length; i++) {
            retFname += c[i];
            if (c[i]=='\\') {
                retFname="";
            }
        }
        return(retFname);
    }
    
    public String getBackupDate() {
        String date = null;
        StringBuilder sb = new StringBuilder();
        sb.append(Project.getString("unitea.restore.backdir"));
        sb.append(File.separator);
        sb.append(BACKUP_FILE_NAME);
        File file = new File(sb.toString());
        if(!file.exists()) return null;
        FileInputStream input;
        try {
            input = new FileInputStream(file);
            try (InputStreamReader reader = new InputStreamReader(input)) {
                BufferedReader buf = new BufferedReader(reader);
                date = buf.readLine();
                buf.close();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DirCopy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DirCopy.class.getName()).log(Level.SEVERE, null, ex);
        }
        return date;
    }
}
