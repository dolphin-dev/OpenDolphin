package open.dolphin.utilities.utility;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author funabashi
 */
public class KickerConnect {

    private String responseStr;
    private String errorMessage;
    private String host;
    private int port;

    private int timeout = 10000;    // タイムアウト値（デフォルト：10秒）
   
    private static final String OK_RESPONSE = "OK";
    
    private static final String EXE_CONNECT_STR = "EXE|";
    private static final String URL_CONNECT_STR = "URL|";
    private static final String BROWSER_CONNECT_STR = "BRW|";
    private static final String FILESAVE_CONNECT_STR = "FSV|";
    

    
    /**
     * ラストエラーメッセージを格納する
     * @return エラーメッセージ
     */
    public String getErrorMessage(){
        return errorMessage;
    }
    
    /**
     * タイムアウト値を取得する
     * @return タイムアウト値（ミリ秒）
     */
    public int getTimeout(){
        return timeout;
    }
    /**
     * タイムアウト値を設定する
     * @param val タイムアウト値（ミリ秒）
     */
    public void setTimeout(int val){
        this.timeout = val;
    }
    
    /**
     * コンストラクタ
     * @param host ホスト
     * @param port ポート番号
     */
    public KickerConnect(String host, int port){
        this.responseStr = null;
        this.errorMessage = "";
        this.host = host;
        this.port = port;
        
    }
    public KickerConnect(String host, int port, int timeout){
        this.responseStr = null;
        this.errorMessage = "";
        this.host = host;
        this.port = port;
        this.timeout = timeout;
        
    }
    public boolean browserStart(String queryString){
        return appStart(BROWSER_CONNECT_STR, queryString);
    }
    
    public boolean urlStart(String queryString){
        return appStart(URL_CONNECT_STR, queryString);
    }
    
    public boolean exeStart(String queryString){
        return appStart(EXE_CONNECT_STR, queryString);
    }
    
    public boolean saveFile(String fname, String statement){
        String buf = fname + "|" + statement;
        return appStart(FILESAVE_CONNECT_STR, buf);
    }
    
    private boolean appStart(String command, String queryString){

        responseStr = "";
        Socket socket = null;
        //InputStreamReader reader = null;
        BufferedReader reader = null;
        BufferedWriter writer = null;
        InputStream is = null;
        boolean bRet = false;
        
        try {            
            socket = new Socket(host, port);
            socket.setSoTimeout(this.timeout);
            System.out.println("接続しました:" + socket.getRemoteSocketAddress() + " - timeout:" + socket.getSoTimeout());
            
            
            is = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));            
            //
            writer.write(command);
            writer.write(queryString);
            writer.flush();
            
            int time = 0;
            String totaldata = "";
            while(is.available() >= 0){
                if(is.available() == 0){
                    if(totaldata.length()>0){
                        break;  // data取得済み
                    }

                    //System.err.print((is.available()) + ":・・・");
                    if(time++>this.timeout/100){
                        errorMessage = "タイムアウトが発生しました";
                        System.err.println("timeout - move()");
                        break;
                    }
                    
                    try{
                        Thread.sleep(100);
                    }
                    catch(InterruptedException ignor){}
                    continue;
                }
                char[] data = new char[is.available()];
                reader.read(data, 0, is.available());
                System.out.println("戻り値：");
                System.out.print(data);
                totaldata += new String(data);
            }
            if(totaldata.length()>0){
                String res = totaldata;
                if(res.equals(OK_RESPONSE)){
                    responseStr = res;
                    bRet = true;
                }
                else{
                    errorMessage = res;
                }
            }
        }
        catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + host);
            System.err.println(e);
            errorMessage = e.getMessage();
            e.printStackTrace();
        }
        catch (IOException e) {
            System.err.println(e);
            errorMessage = e.getMessage();
            e.printStackTrace();
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
                if (reader != null) {
                    reader.close();
                }
                if (writer != null) {
                    writer.close();
                }
                if(is != null){
                    is.close();
                }
            }
            catch (IOException e) {}
        }
        //System.out.println("切断されました " + socket.getRemoteSocketAddress());
        
        return bRet;
     }
    
    
    public String createQueryString(String patientID){
        StringBuilder sb = new StringBuilder();
        
        // 患者ID
        if(patientID != null){
            sb.append("pid=");
            sb.append(patientID);
        }
        
        return sb.toString();
        
    }
    
    public static void main(String[] args){
        
        int port = 2101;    // 接続先ポート番号
        String host = "172.31.200.193"; // 接続先IPアドレス
        
        KickerConnect con = new KickerConnect(host,port);
        con.timeout = 10000;    // タイムアウト値
        try {
            
            boolean b = con.exeStart(con.createQueryString("1234567")); // EXE起動（パラメタのみ送信）
            //boolean b = con.browserStart(con.createQueryString("12345")); // ブラウザ起動（パラメタのみ送信）
            //boolean b = con.urlStart("http://lscc.co.jp/?id=\"test\""); // ブラウザ起動（URLごと送信）
            //boolean b = con.saveFile("\\\\172.31.10.140\\a\\dddddd.txt", "1,2,3,4,5,6,7,8,9,,10" ); // ファイル保存
            
            if(b){
                System.out.print("OK------!");
            }
            else{
                String err = con.getErrorMessage();
                System.out.print(err);
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        
 
    }

}
