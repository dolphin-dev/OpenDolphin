/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.utilities.utility;

import java.io.IOException;
import java.net.MalformedURLException;
import open.dolphin.utilities.common.HTTP;

/**
 * HTTPライブラリクラス
 * @author S.Oh@Life Sciences Computing Corporation.
 */
public class HttpConnect extends HTTP {
    /**
     * コンストラクタ
     */
    public HttpConnect() {
        super();
    }
    
    /**
     * GET
     * @param target 接続URLとパラメータ
     * @param request GET/POST
     * @return
     * @throws MalformedURLException
     * @throws IOException 
     */
    public String httpGET(String target, String request) throws MalformedURLException, IOException {
        connectHttp(target, request, false, "", 0);
        //if(getLastResponseCode() != HttpURLConnection.HTTP_OK) return "";
        return recvHttp(true);
    }
    
    /**
     * デバッグ情報の有無設定
     * @param dbg デバッグ情報の有無
     */
    public void debug(boolean dbg) {
        setDebug(dbg);
    }
    
    /**
     * Charsetの取得
     * @return Charaset
     */
    public String getCharName() {
        return getCharset();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    }
}
