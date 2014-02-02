package open.dolphin.helper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import open.dolphin.client.ClientContext;

/**
 *
 * @author Kazushi Minagawa. Digital Globe, Inc.
 */
public class UserDocumentHelper {
    
    /**
     * PDF/OpenOffice差し込み文書Fileへのパスを生成する。
     * File name = 患者氏名_文書名_YYYY-MM-DD(N).pdf/odt
     */
    public static String createPathToDocument(String dirStr, String docName, String ext, String ptName, Date d) {
        
        // direcory をチェックする
        File dir;
        if (dirStr==null || dirStr.equals("")) {
            dir = new File(ClientContext.getPDFDirectory());
        } else {
            dir = new File(dirStr);
            if (!dir.exists()) {
                boolean ok = dir.mkdir();
                if (!ok) {
                    // dirStr!=null で dirが生成できない時
                    // PDF directory を使用する これは生成されている
                    dir = new File(ClientContext.getPDFDirectory());
                }
            }
        }
        
        // 拡張子チェック
        if (!ext.startsWith(".")) {
            ext = "." + ext;
        }
        
        // 患者氏名の空白を削除する
        ptName = ptName.replaceAll(" ", "");
        ptName = ptName.replaceAll("　", "");
        
        // 日付
        String dStr = new SimpleDateFormat("yyyy-MM-dd").format(d);
        
        // File 名を構成する
        StringBuilder sb = new StringBuilder();
        sb.append(ptName).append("_");
        sb.append(docName).append("_");
        sb.append(dStr);
        String fileName = sb.toString();
        sb.append(ext);
        String test = sb.toString();
        int cnt = 0;
        File ret = null;
        
        // 存在しなくなるまで (n) をつける
        while (true) {
            ret = new File(dir, test);
            if (!ret.exists()) {
                break;
            }
            cnt++;
            sb = new StringBuilder();
            sb.append(fileName);
            sb.append("(").append(cnt).append(")").append(ext);
            test = sb.toString();
        }
        
        return ret!=null ? ret.getPath() : null;
    }
}
