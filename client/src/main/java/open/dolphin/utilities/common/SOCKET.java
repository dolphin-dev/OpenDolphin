/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.utilities.common;

import java.io.*;
import java.net.*;

/**
 * SOCKETクラス
 * @author Life Sciences Computing Corporation.
 */
public class SOCKET extends AbstractCommonFunc {
    protected Socket clientSocket;
    protected OutputStreamWriter clientOsw;
    protected BufferedWriter clientBw;
    protected InputStream clientIs;
    protected InputStreamReader clientIsr;
    protected BufferedReader clientBr;
    protected DataOutputStream clientDos;
    
    /**
     * コンストラクタ
     */
    protected SOCKET() {
        super();
        Init();
    }
    
    /**
     * 初期化
     */
    @Override
    void Init() {
    }
    
    /**
     * ソケット接続
     * @param host ホスト名 or IPアドレス
     * @param port ポート番号
     * @return
     * @throws UnknownHostException
     * @throws IOException 
     */
    protected Socket createClientSocket(String host, int port) throws UnknownHostException, IOException {
        // ソケットの生成
        clientSocket = new Socket(host, port);
        return clientSocket;
    }
    
    /**
     * 送信
     * @param data 送信するデータ
     * @throws IOException 
     */
    protected void sendClientData(String data) throws IOException {
        if(clientSocket == null || data == null) return;
        // 送信する
        //clientOsw = new OutputStreamWriter(clientSocket.getOutputStream());
        //clientBw = new BufferedWriter(clientOsw);
        //clientBw.write(data);
        //clientBw.flush();
        clientDos = new DataOutputStream(clientSocket.getOutputStream());
        clientBr = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        clientDos.writeBytes(data);
    }
    
    /**
     * 受信
     * @param encode エンコード
     * @return 受信したデータ
     * @throws IOException 
     */
    protected String recvClientData(String encode) throws IOException {
        // 受信する
        //clientIs = clientSocket.getInputStream();
        //clientIsr = new InputStreamReader(clientIs, encode);
        //clientBr = new BufferedReader(clientIsr);
        //// 受信できるまで待機
        //while(clientIs.available() == 0);
        //// 受信した内容を出力
        //char[] ret = new char[clientIs.available()];
        //clientBr.read(ret);
        if(clientBr == null) return null;
        StringBuilder ret = new StringBuilder();
        String tmp;
        while((tmp = clientBr.readLine()) != null) {
            ret.append(tmp);
        }
        
        return ret.toString();
    }
    
    protected String recv() throws IOException {
        StringBuilder ret = new StringBuilder();
        String tmp;
        while((tmp = clientBr.readLine()) != null) {
            ret.append(tmp);
        }
        clientDos.close();
        clientBr.close();
        clientSocket.close();
        return ret.toString();
    }
    
    /**
     * 終了
     * @throws IOException 
     */
    protected void closeClientSocket() throws IOException {
        //clientBw.close();
        //clientOsw.close();
        //clientBr.close();
        //clientIsr.close();
        //clientSocket.close();
        clientDos.close();
        clientBr.close();
        clientSocket.close();
    }
    
    protected void createServerSocket(int port) throws IOException {
        // ソケットの生成
        ServerSocket serverSocket = new ServerSocket(port);
        boolean endFlag = false;
        Socket socket = null;
        while(!endFlag) {
            // クライアントからの接続を待機(接続時に新たなSocketを返す)
            // 接続があるまで処理はブロックされるため、複数のクライアントからの接続を受付する
            // ためにはスレッドを使用する。
            socket = serverSocket.accept();
            // データの受信
            InputStreamReader isr = new InputStreamReader(socket.getInputStream());
            BufferedReader br = new BufferedReader(isr);
            String ret = br.readLine();
            br.close();
            socket.close();
            if(ret.startsWith("[end]")) {
                // データの返信
                //OutputStream os = socket.getOutputStream();
                //PrintStream ps = new PrintStream(os);
                //ps.println("END");
                serverSocket.close();
                endFlag = true;
            }
        }
    }
}
