/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.utilities.utility;

import java.io.IOException;
import java.net.UnknownHostException;
import open.dolphin.utilities.common.SOCKET;

/**
 * SOCKETライブラリクラス
 * @author Life Sciences Computing Corporation.
 */
public class SocketConnect extends SOCKET {
    /**
     * コンストラクタ
     */
    public SocketConnect() {
        super();
    }
    
    /**
     * ソケットの送受信
     * @param host ホスト名
     * @param port ポート番号
     * @param data 送信するデータ
     * @param encode 受信データのエンコード
     * @return 受信したデータ
     * @throws UnknownHostException
     * @throws IOException 
     */
    public String sendRecvSocket(String host, int port, String data, String encode) throws UnknownHostException, IOException {
        String ret = null;
        if(createClientSocket(host, port) != null) {
            sendClientData(data);
            ret = recvClientData(encode);
            closeClientSocket();
        }
        return ret;
    }
}
