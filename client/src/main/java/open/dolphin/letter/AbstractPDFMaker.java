package open.dolphin.letter;

import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Desktop;
import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import open.dolphin.client.Chart;
import open.dolphin.client.ClientContext;
import open.dolphin.helper.UserDocumentHelper;
import open.dolphin.infomodel.LetterModule;
import open.dolphin.infomodel.ModelUtils;
import open.dolphin.project.Project;
import org.apache.log4j.Logger;

/**
 * AbstractPDFMaker
 * @author Kazushi Minagawa. Digital Globe, Inc.
 * @author modified by masuda, Masuda Naika
 */
public abstract class AbstractPDFMaker {
    
    protected static final String EXT_PDF = ".pdf";
    protected static final String HEISEI_MIN_W3 = "HeiseiMin-W3";
    protected static final String UNIJIS_UCS2_HW_H = "UniJIS-UCS2-HW-H";
    
    protected static final String ERROR_IO = "ファイル IO エラー";
    protected static final String ERROR_PDF = "PDF 生成エラー";

    protected static final int TOP_MARGIN = 50;
    protected static final int LEFT_MARGIN = 50;
    protected static final int BOTTOM_MARGIN = 50;
    protected static final int RIGHT_MARGIN = 50;

    protected static final int TITLE_FONT_SIZE = 14;
    protected static final int BODY_FONT_SIZE = 10;
    
    protected static final float CELL_PADDING = 8.0f;

    protected String documentDir;
    protected String pathToPDF;
    protected LetterModule model;
    protected int marginLeft = LEFT_MARGIN;
    protected int marginRight = RIGHT_MARGIN;
    protected int marginTop = TOP_MARGIN;
    protected int marginBottom = BOTTOM_MARGIN;

    protected BaseFont baseFont;
    protected Font titleFont;
    protected Font bodyFont;
    protected int titleFontSize = TITLE_FONT_SIZE;
    protected int bodyFontSize = BODY_FONT_SIZE;

    protected PdfWriter writer;
    private static final String USER_GOTHIC_FONT = "msgothic.ttc,1";    // MS-PGothic
    private static final String USER_MINCHO_FONT = "msmincho.ttc,1";    // MS-PMicho
    private static final String HEISEI_GO_W5 = "HeiseiKakuGo-W5";
    private static final String DOC_FOOTER = "OpenDolphin, Japanese open source EHR. (c)Digital Globe, Inc.";
    protected static final SimpleDateFormat FRMT_DATE_WITH_TIME = new SimpleDateFormat("yyyy/MM/dd HH:mm");
    protected static final SimpleDateFormat FRMT_FILE_DATE = new SimpleDateFormat("yyyyMMdd");
    protected static final SimpleDateFormat FRMT_SIMPLE_DATE =  new SimpleDateFormat("yyyy-MM-dd");
    
    protected Chart context;
    
    public void setContext(Chart chart) {
        context = chart;
    }
    
    public abstract boolean makePDF(String filePath);

    protected abstract String getTitle();

    public final void create() {

        // 出力先を取得
        String title = getTitle();
        final String fileName = getFilePath(title);

        SwingWorker worker = new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {

                boolean b = makePDF(fileName);
                if (b) {
                    openDocumentFile(fileName);
                }
                return null;
            }
        };
        worker.execute();
    }

    protected String getPatientName() {
        return (model != null) ? model.getPatientName() : null;
    }

    protected String getPatientId() {
        return (model != null) ? model.getPatientId() : null;
    }
    
        
    protected String getDateString(Date d) {
        return ModelUtils.getDateAsFormatString(d, "yyyy年M月d日");
    }

    protected String getDateString(String date) {
        Date d = ModelUtils.getDateAsObject(date);
        return ModelUtils.getDateAsFormatString(d, "yyyy年M月d日");
    }
    
    protected PdfPCell createNoBorderCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, bodyFont));
        cell.setBorder(0);
        cell.setPadding(CELL_PADDING);
        return cell;
    }

    protected String getSexString(String sex) {
        //return ModelUtils.getGenderDesc(sex);
        return sex;
    }

    public LetterModule getModel() {
        return model;
    }

    public void setModel(LetterModule model) {
        this.model = model;
    }

    public int getMarginLeft() {
        return marginLeft;
    }

    public void setMarginLeft(int marginleft) {
        this.marginLeft = marginleft;
    }

    public int getMarginRight() {
        return marginRight;
    }

    public void setMarginRight(int marginRight) {
        this.marginRight = marginRight;
    }

    public int getMarginTop() {
        return marginTop;
    }

    public void setMarginTop(int marginTop) {
        this.marginTop = marginTop;
    }

    public int getMarginBottom() {
        return marginBottom;
    }

    public void setMarginBottom(int marginBottom) {
        this.marginBottom = marginBottom;
    }

    public int getTitleFontSize() {
        return titleFontSize;
    }

    public void setTitleFontSize(int titleFontSize) {
        this.titleFontSize = titleFontSize;
    }

    public int getBodyFontSize() {
        return bodyFontSize;
    }

    public void setBodyFontSize(int bodyFontSize) {
        this.bodyFontSize = bodyFontSize;
    }

    public String getDocumentDir() {
        return documentDir;
    }

    public void setDocumentDir(String documentDir) {
        this.documentDir = documentDir;
    }

    public String getPathToPDF() {
        return pathToPDF;
    }

    public void setPathToPDF(String fileName) {
        this.pathToPDF = fileName;
    }

//masdua^
    // この文書のファイル名を作成
    protected final String getFilePath(String title) {

        String ptName = getPatientName().replace(" ", "").replace("　", "");
        //StringBuilder sb = new StringBuilder();
        //sb.append(getPatientId()).append("_");
        //sb.append(ptName);
        //ptName = sb.toString();
//s.oh^ 不具合修正(2013/01/09)
        //String filePath = Project.getString(Project.LOCATION_PDF);
        //filePath = filePath + File.separator + getPatientId();
        String filePath = Project.getString(Project.KARTE_PDF_SEND_DIRECTORY);
        filePath = filePath + File.separator + getPatientId();
        File folder = new File(filePath);
        folder.mkdir();
//s.oh$
        setDocumentDir(filePath);
        
        Window parent = (context != null) ? context.getFrame() : null;
        boolean showDialog = false;
        String path;
        
        if (showDialog) {
            path = UserDocumentHelper.createPathToDocument(
            getDocumentDir(),   // PDF File を置く場所
            title,              // 文書名
            EXT_PDF,            // 拡張子 
            ptName,             // 患者氏名 
            new Date(),         // 日付
            parent);            // 親ウィンドウ
        
        } else {
            path = UserDocumentHelper.createPathToDocument(
            getDocumentDir(),   // PDF File を置く場所
            title,              // 文書名
            EXT_PDF,            // 拡張子 
            ptName,             // 患者氏名 
            new Date());        // 日付
        }
        
        setPathToPDF(path);// 呼び出し側で取り出せるように保存する
        return path;
    }
    
    protected final Logger getBootLogger() {
        return ClientContext.getBootLogger();
    }

    protected final HeaderFooter getDolphinFooter() {
        Font footerFont = new Font(Font.HELVETICA, 8, Font.ITALIC);
        HeaderFooter footer = new HeaderFooter(new Paragraph(DOC_FOOTER, footerFont), false);
        footer.setAlignment(Element.ALIGN_RIGHT);
        footer.setBorder(Rectangle.NO_BORDER);
        return footer;
    }

    protected final BaseFont getGothicFont() throws DocumentException, IOException {

        boolean win = ClientContext.isWin();

        if (win) {
            // Windowsの場合はMS-PGothicを使う。埋め込んじゃう
            String fontName = getWindowsFontPath(USER_GOTHIC_FONT);
            return BaseFont.createFont(fontName, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        } else {
            // HeiseiKakuGo-W5は埋め込めない。PDFRenderer.jarでは真っ白け
            return BaseFont.createFont(HEISEI_GO_W5, UNIJIS_UCS2_HW_H, false);
        }
    }

    protected final BaseFont getMinchoFont() throws DocumentException, IOException {

        boolean win = ClientContext.isWin();

        if (win) {
            // Windowsの場合はMS-PMinchoを使う。埋め込んじゃう
            String fontName = getWindowsFontPath(USER_MINCHO_FONT);
            return BaseFont.createFont(fontName, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        } else {
            return BaseFont.createFont(HEISEI_MIN_W3, UNIJIS_UCS2_HW_H, false);
        }
    }

    private String getWindowsFontPath(String fontName) {
        StringBuilder sb = new StringBuilder();
        sb.append(System.getenv("windir")).append(File.separator);
        sb.append("Fonts").append(File.separator);
        sb.append(fontName);
        return sb.toString();
    }

    // PDFビューアーで開く
    private void openDocumentFile(String filePath) throws IOException {

//s.oh^ 不具合修正(しなくても大丈夫そう…なぜだ…)
        File file = new File(filePath);
        if (file.exists()) {
            Desktop.getDesktop().open(file);
        }
        //String err = null;
        //try {
        //    URI uri = Paths.get(filePath).toUri();
        //    Desktop.getDesktop().browse(uri);
        //} catch (IOException ex) {
        //    err = "PDFファイルに関連づけされたアプリケーションを起動できません。";
        //} catch (Throwable ex) {
        //    err = ex.getMessage();
        //}
        //if (err!=null) {
        //    JOptionPane.showMessageDialog(null, err, ClientContext.getFrameTitle("PDF作成"), JOptionPane.WARNING_MESSAGE);
        //}
    }
//s.oh$
}
