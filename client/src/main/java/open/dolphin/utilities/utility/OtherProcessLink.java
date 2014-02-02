/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.utilities.utility;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 他プロセス連携
 * @author Life Sciences Computing Corporation.
 */
public class OtherProcessLink {
    
    /**
     * コンストラクタ
     */
    public OtherProcessLink() {}
    
    /**
     * URL連携
     * @param url URL
     * @return 
     */
    public boolean linkURL(String url) {
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.browse(new URI(url));
        } catch (URISyntaxException ex) {
            Logger.getLogger(OtherProcessLink.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OtherProcessLink.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return true;
    }
    
    /**
     * ファイル連携
     * @param command コマンド
     * @return 
     */
    public boolean linkFile(String command) {
        Runtime run = Runtime.getRuntime();
        try {
            Process process = run.exec(command);
        } catch (IOException ex) {
            Logger.getLogger(OtherProcessLink.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return true;
    }
    
    /**
     * TCP/IP:Exe連携
     * @param data データ
     * @param host ホスト名
     * @param int ポート番号
     * @return 
     */
    public boolean linkTCPToExe(String data, String host, int port) {
        KickerConnect kc = new KickerConnect(host,port);
        try {
            
            boolean b = kc.exeStart(kc.createQueryString(data));
            
            if(b){
                System.out.print("OK");
            }
            else{
                String err = kc.getErrorMessage();
                System.out.print(err);
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return true;
    }
    
    /**
     * TCP/IP:Exe連携
     * @param data データ
     * @param host ホスト名
     * @param int ポート番号
     * @param file ファイルパス
     * @return 
     */
    public boolean linkTCPToFile(String data, String host, int port, String file) {
        KickerConnect kc = new KickerConnect(host,port);
        try {
            
            boolean b = kc.saveFile(file, data); // ファイル保存
            
            if(b){
                System.out.print("OK");
            }
            else{
                String err = kc.getErrorMessage();
                System.out.print(err);
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return true;
    }
}
