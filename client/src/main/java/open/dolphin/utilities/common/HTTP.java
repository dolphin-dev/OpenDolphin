/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.utilities.common;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * HTTPクラス
 * @author S.Oh@Life Sciences Computing Corporation.
 */
public class HTTP extends AbstractCommonFunc {
    private HttpURLConnection http;
    private String contentType;
    private String charset;
    
    /**
     * コンストラクタ
     */
    protected HTTP() {
        super();
        Init();
    }
    
    /**
     * 初期化
     */
    void Init() {
        http = null;
        contentType = "";
        charset = "";
    }
    
    /**
     * HTTP接続
     * @param target 送信するURLとパラメータ
     * @param request GET/POST
     * @param proxy プロキシの有無
     * @param proxyHost ホスト名
     * @param proxyPort ポート番号
     * @throws MalformedURLException
     * @throws IOException 
     */
    protected void connectHttp(String target, String request, boolean proxy, String proxyHost, int proxyPort) throws MalformedURLException, IOException {
        URL url;
        if(proxy) {
            url = new URL(PROTOCOL_HTTP, proxyHost, proxyPort, target);
        }else{
            url = new URL(target);
        }
        // 接続オブジェクトの取得
        http = (HttpURLConnection)url.openConnection();
        // HTTPのメソッドを設定(GETは省略可能)
        if(request.equals(REQUESTMETHOD_GET)) {
            http.setRequestMethod(REQUESTMETHOD_GET);
            http.setDoOutput(false);
        }else if(request.equals(REQUESTMETHOD_POST)) {
            http.setRequestMethod(REQUESTMETHOD_POST);
            http.setDoOutput(true);
        }
        // 接続
        if(getDebug()) {
            System.out.println("【接続】...");
            System.out.println("Protocel: " + url.getProtocol());
            System.out.println("Host: " + url.getHost());
            System.out.println("File: " + url.getFile());
            //System.out.println("Authority: " + url.getAuthority());
            //System.out.println("UserInfo: " + url.getUserInfo());
            //System.out.println("Query: " + url.getQuery());
            System.out.println("Request: " + http.getRequestMethod());
            System.out.print("ポート番号: ");
            if( url.getPort() == -1 ) System.out.println( url.getDefaultPort() );
            else System.out.println( url.getPort() );
        }
        http.connect();

        // ヘッダ情報の出力
        if(getDebug()) {
            System.out.println("【ヘッダ情報】...");
            Map headers = http.getHeaderFields();
            for(Object key : headers.keySet()) {
                System.out.println(key + ": " + headers.get(key));
            }
        }

        // ContentTypeの取得
        String type = http.getContentType();
        int idx;
        idx = type.indexOf(";");
        if(idx > 0) {
            contentType = type.substring(0, idx);
            idx = type.indexOf("=");
            charset = type.substring(idx+1, type.length());
            charset = charset.toUpperCase();
        }else{
            contentType = type;
            charset = DEFAULT_CHARSET;
        }
    }
    
    /**
     * POSTデータのセット
     * @param data データ
     * @throws IOException 
     */
    protected void setPostData(String data) throws IOException {
        OutputStreamWriter osw = new OutputStreamWriter(http.getOutputStream());
        osw.write(data + System.getProperty("line.separator"));
        osw.flush();
        osw.close();
    }
    
    /**
     * HTTP受信
     * @param line 1行出力
     * @return 受信したデータ
     * @throws UnsupportedEncodingException
     * @throws IOException 
     */
    protected String recvHttp(boolean line) throws UnsupportedEncodingException, IOException {
        if(getDebug()) System.out.println("【受信情報】...");
        String data;
        
        getLastResponseCode();
        getLastResponseMessage();
        
        if(line) {
            BufferedReader reader = null;
            if(getLastResponseCode() >= HttpURLConnection.HTTP_BAD_REQUEST) {
                reader = new BufferedReader(new InputStreamReader(http.getErrorStream(), charset));
            }else{
                reader = new BufferedReader(new InputStreamReader(http.getInputStream(), charset));
            }
            StringBuilder sb = new StringBuilder();  
            String buffer;
            while ((buffer = reader.readLine()) != null) {  
                sb.append(buffer);  
                if(getDebug()) System.out.println();
            }  
            reader.close(); // いる？
            data = sb.toString();
        }else{
            BufferedInputStream bis = null;
            if(getLastResponseCode() >= HttpURLConnection.HTTP_BAD_REQUEST) {
                bis = new BufferedInputStream(http.getErrorStream());
            }else{
                bis = new BufferedInputStream(http.getInputStream());
            }
            //ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int bs;
            while( (bs = bis.read()) != -1 ) {
                //System.out.write(bs);
                baos.write(bs);
            }
            data = new String(baos.toByteArray());
        }

        // 応答コードとメッセージ
        if(getDebug()) System.out.println("応答コード: " + getLastResponseCode() + ", " + getLastResponseMessage() );
        
        return data;
    }
    
    /**
     * レスポンスコードの取得
     * @return レスポンスコード
     * @throws IOException 
     */
    protected int getLastResponseCode() throws IOException {
        return http.getResponseCode();
    }
    
    /**
     * レスポンスメッセージの取得
     * @return レスポンスメッセージ
     * @throws IOException 
     */
    protected String getLastResponseMessage() throws IOException{
        return http.getResponseMessage();
    }
    
    /**
     * コンテンツタイプの取得
     * @return コンテンツタイプ
     */
    protected String getContentType() {
        return contentType;
    }
    
    /**
     * Charsetの取得
     * @return Charaset
     */
    protected String getCharset() {
        return charset;
    }
}
