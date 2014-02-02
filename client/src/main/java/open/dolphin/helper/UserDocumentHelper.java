package open.dolphin.helper;

import java.awt.Window;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import open.dolphin.client.ClientContext;

/**
 *
 * @author Kazushi Minagawa. Digital Globe, Inc.
 */
public class UserDocumentHelper {
    
    private static final String[] IMAGE_TYPES = {"dcm","jpg", "png", "bmp", "gif", "tif"};
    private static final String[] DOCS_HAS_ICON = {"pdf", "doc","docx", "xls", "xlsx", "ppt","pptx"};
//minagawa^ Icon Server    
    //private static final String[] DOC_ICONS = 
        //{"pdf_icon40px.gif", "Word-32-d.gif","Word-32-d.gif", "Excel-32-d.gif", "Excel-32-d.gif", "PowerPoint-32-d.gif", "PowerPoint-32-d.gif"};
    private static final String[] DOC_ICONS = 
        {"icon_pdf", "icon_word","icon_word", "icon_excel", "icon_excel", "icon_power_point", "icon_power_point"};
//minagawa$    
    
    private static final String DEFAULT_DOC_ICON = "docs_32.gif";
    
    /**
     * PDF/OpenOffice差し込み文書Fileへのパスを生成する。
     * File name = 患者氏名_文書名_YYYY-MM-DD(N).pdf/odt
     */
    public static String createPathToDocument(String dirStr, String docName, String ext, String ptName, Date d) {
//minagawa^ mac jdk7        
//        // direcory をチェックする
//        File dir;
//        if (dirStr==null || dirStr.equals("")) {
//            dir = new File(ClientContext.getPDFDirectory());
//        } else {
//            dir = new File(dirStr);
//            if (!dir.exists()) {
//                boolean ok = dir.mkdir();
//                if (!ok) {
//                    // dirStr!=null で dirが生成できない時
//                    // PDF directory を使用する これは生成されている
//                    dir = new File(ClientContext.getPDFDirectory());
//                }
//            }
//        }
//        // 拡張子チェック
//        if (!ext.startsWith(".")) {
//            ext = "." + ext;
//        }
//        // 患者氏名の空白を削除する
//        ptName = ptName.replaceAll(" ", "");
//        ptName = ptName.replaceAll("　", "");
//        
//        // 日付
//        String dStr = new SimpleDateFormat("yyyy-MM-dd").format(d);
//        
//        // File 名を構成する
//        StringBuilder sb = new StringBuilder();
//        sb.append(ptName).append("_");
//        sb.append(docName).append("_");
//        sb.append(dStr);
//        String fileName = sb.toString();
//        sb.append(ext);
//        String test = sb.toString();
//        int cnt = 0;
//        File ret = null;
//        
//        // 存在しなくなるまで (n) をつける
//        while (true) {
//            ret = new File(dir, test);
//            if (!ret.exists()) {
//                break;
//            }
//            cnt++;
//            sb = new StringBuilder();
//            sb.append(fileName);
//            sb.append("(").append(cnt).append(")").append(ext);
//            test = sb.toString();
//        }
//        
//        return ret!=null ? ret.getPath() : null;
        
        String ret = null;
        try {
            dirStr = (dirStr!=null && (!dirStr.equals(""))) ? dirStr : ClientContext.getPDFDirectory();
            
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
            
            // 存在しなくなるまで (n) をつける
            while (true) {
                Path testPath = Paths.get(dirStr, test);
                if (!Files.exists(testPath)) {
                    ret = testPath.toAbsolutePath().toString();
                    break;
                }
                cnt++;
                sb = new StringBuilder();
                sb.append(fileName);
                sb.append("(").append(cnt).append(")").append(ext);
                test = sb.toString();
            }

        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return ret;
 //minagawa$       
    }
    
    public static ImageIcon getIcon(String ext) {
        
        ImageIcon ret = null;
        
        for (int i=0; i < DOCS_HAS_ICON.length; i++) {
            if (DOCS_HAS_ICON[i].equals(ext)) {
//minagawa^ Icon Server                
                //ret = ClientContext.getImageIcon(DOC_ICONS[i]);
                ret = ClientContext.getImageIconArias(DOC_ICONS[i]);
//minagawa$                
                break;
            }
        }
//minagawa^ Icon Server         
        //return ret!=null ? ret : ClientContext.getImageIcon(DEFAULT_DOC_ICON);
        return ret!=null ? ret : ClientContext.getImageIconArias("icon_default_document");
//minagawa$        
    }
    
    public static boolean isImage(String ext) {
        
        if (ext==null) {
            return false;
        }
        
        boolean ret = false;
        
        for (int i=0; i < IMAGE_TYPES.length; i++) {
            if (ext.equals(IMAGE_TYPES[i])) {
                ret = true;
                break;
            }
        }
        
        return ret;
    }
    
    /**
     * PDF/OpenOffice差し込み文書Fileへのパスを生成する。 File name =
     * 患者氏名_文書名_YYYY-MM-DD(N).pdf/odt
     */
    public static String createPathToDocument(String dirStr, String docName, String ext, String ptName, Date d, Window parent) {

        // direcory をチェックする
        File dir;
        if (dirStr == null || dirStr.equals("")) {
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
        ptName = ptName.replace(" ", "");
        ptName = ptName.replace("　", "");

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
        
        // FileChooserを表示
        JFileChooser fileChooser = new JFileChooser(dirStr);
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooser.setDialogTitle("PDF出力");
        File current = fileChooser.getCurrentDirectory();
        fileChooser.setSelectedFile(new File(current.getPath(), test));
        int selected = fileChooser.showSaveDialog(parent);
        if (selected != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        File ret = fileChooser.getSelectedFile();

        if (ret.exists()) {
            String title = "上書き確認";
            String message = "既存のファイル " + ret.toString() + "\n" + "を上書きしようとしています。続けますか？";
            selected = showConfirmDialogCancelDefault(title, message, parent);
            
            switch (selected) {
                case 0:
                    return null;
                case 1:
                    // 存在しなくなるまで (n) をつける
                    int cnt = 0;
                    do {
                        cnt++;
                        sb = new StringBuilder();
                        sb.append(fileName);
                        sb.append("(").append(cnt).append(")").append(ext);
                        test = sb.toString();
                        ret = new File(dir, test);
                    } while (ret.exists());
            }
        }

        return ret.getPath();
    }
    
    private static int showConfirmDialogCancelDefault(String title, String message, Window parent) {

        String[] options = {"いいえ", "番号振り", "はい"};
        int selected = JOptionPane.showOptionDialog(parent, message, title,
                JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
        return selected;
    }
}
