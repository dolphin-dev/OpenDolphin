/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.letter;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import open.dolphin.client.KartePane;

/**
 * 新規カルテの PDF メーカー。
 * @author Life Sciences Computing Corporation.
 */
public class KartePDFMaker2 extends AbstractLetterPDFMaker {
    public static final int PDF_TOP_MARGIN      = 30;   // PDF Top margin
    public static final int PDF_LEFT_MARGIN     = 30;   // PDF Left margin
    public static final int PDF_RIGHT_MARGIN    = 30;   // PDF Right margin
    public static final int PDF_BOTTOM_MARGIN   = 50;   // PDF Bottom margin
    public static final int KM_SOA              = 0;    // Karte SOA mode
    public static final int KM_PLAN             = 1;    // Karte Plan mode

    private String patID;                               // Patient ID
    private String patName;                             // Patient Name
    private String pdfTitle;                            // Title
    private Date saveDate;                              // Save Date
    private Document pdfDoc;                            // PDF Document
    private PdfWriter pdfWriter;                        // PDF Writer
    private int pdfTop;                                 // PDF Top
    private int pdfLeft;                                // PDF Left
    private int pdfRight;                               // PDF Right
    private int pdfBottom;                              // PDF Bottom
    private PdfPTable tableParent;                      // PDF Table
    private PdfPTable tableLeft;                        // PDF Left table
    private PdfPTable tableRight;                       // PDF Right table
    private int karteMode;                              // Karte mode
    private Paragraph pdfParagraph;                     // PDF Paragraph
    private int paragraphAlign;                         // Paragraph align
    private boolean bCreating;                          // Creating paragraph
    private boolean bInitialized;                       // Initialized PDF
    private String docID;                               // Karte ID

    /**
     * コンストラクタ
     * @param valPath フォルダパス
     * @param valPathID タイトル
     * @param valDate 保存時刻
     */
    public KartePDFMaker2(String valPatID, String valPatName, String valTitle, Date valDate, String docID) {
        // 患者ID
        patID = valPatID;
        // 患者氏名
        patName = valPatName;
        // タイトル
        pdfTitle = valTitle;
        // 保存日時
        saveDate = valDate;
        // フラグの初期化
        bCreating = false;
        bInitialized = false;
        // カルテID
        this.docID = docID;
    }
    
    /**
     * カルテモードの設定
     * @param mode モード
     */
    public void setKarteMode(int mode) {
        karteMode = mode;
    }
    
    /**
     * カルテモードの取得
     */
    public int getKarteMode() {
        return karteMode;
    }
    
    /**
     * カルテ用PDFの初期化
     */
    public void initKartePDF() {
        if(bCreating == true) addDataEnd();
        if(bInitialized == true) create();
        try {
            setMarginTop(PDF_TOP_MARGIN);
            setMarginLeft(PDF_LEFT_MARGIN);
            setMarginRight(PDF_RIGHT_MARGIN);
            setMarginBottom(PDF_BOTTOM_MARGIN);

            pdfDoc = new Document(
                    PageSize.A4,
                    getMarginLeft(),
                    getMarginRight(),
                    getMarginTop(),
                    getMarginBottom());

            StringBuilder sbPath = new StringBuilder();
            sbPath.append(getDocumentDir());
            if(getDocumentDir().endsWith(File.separator) == false) {
                sbPath.append(File.separator);
            }
//s.oh^ 2013/02/07 印刷対応
            //sbPath.append(patID).append("_");
            //sbPath.append(new SimpleDateFormat("yyyyMMddHHmmss").format(saveDate));
            //sbPath.append("_").append(docID);   // KarteIDの追加
            //sbPath.append(".PDF");
            if(docID != null) {
                sbPath.append(patID).append("_");
                sbPath.append(new SimpleDateFormat("yyyyMMddHHmmss").format(saveDate));
                sbPath.append("_").append(docID);   // KarteIDの追加
                sbPath.append(".PDF");
            }else{
                sbPath.append("Temp.pdf");
            }
//s.oh$
            setPathToPDF(sbPath.toString());         // 呼び出し側で取り出せるように保存する

            // Documentの作成
            pdfWriter = PdfWriter.getInstance(pdfDoc, new FileOutputStream(sbPath.toString()));

            // フォントの作成
            baseFont    = BaseFont.createFont(HEISEI_MIN_W3, UNIJIS_UCS2_HW_H, false);
            titleFont   = new Font(baseFont, getTitleFontSize(), 0, new Color(0,0,249));
            bodyFont    = new Font(baseFont, getBodyFontSize());
            
            // 不具合対応(番号:) カルテPDFのヘッダ出力
            // ヘッダーの設定をする
            HeaderFooter header = new HeaderFooter(new Phrase(pdfTitle, titleFont), false);
            header.setAlignment(Element.ALIGN_CENTER);
            pdfDoc.setHeader(header);
            
            // フッターの設定をする
//s.oh^ 2013/02/07 印刷対応
            //HeaderFooter footer = new HeaderFooter(new Phrase("--"), new Phrase("--"));
            StringBuilder sbFooter = new StringBuilder();
            sbFooter.append("【");
            sbFooter.append(patName);
            sbFooter.append(" 様】");
            sbFooter.append(" Page ");
            HeaderFooter footer = new HeaderFooter(new Phrase(sbFooter.toString(), bodyFont), true);
//s.oh$
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setBorder(Rectangle.NO_BORDER);
            pdfDoc.setFooter(footer);
            
            pdfDoc.open();

            // PDF Top/Left/Right/Bottom
            pdfTop     = (int)pdfWriter.getPageSize().getHeight() - getMarginTop();
            pdfLeft    = (int)pdfWriter.getPageSize().getLeft() + getMarginLeft();
            pdfRight   = (int)pdfWriter.getPageSize().getRight() - getMarginRight();
            pdfBottom  = (int)pdfWriter.getPageSize().getBottom() + getMarginBottom();

            // 親テーブルの作成
            tableParent = new PdfPTable(2);
            tableParent.setTotalWidth(pdfRight - pdfLeft);
            int width[]={(pdfRight - pdfLeft)/2, (pdfRight - pdfLeft)/2};
            tableParent.setWidths(width);
            tableParent.getDefaultCell().setBorder(Rectangle.BOX);

            // 左右テーブルの作成
            tableLeft = new PdfPTable(1);
            tableLeft.setTotalWidth((pdfRight - pdfLeft)/2);
            int widthLeftRight[]={(pdfRight - pdfLeft)/2};
            tableLeft.setWidths(widthLeftRight);
            tableLeft.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            tableRight = new PdfPTable(1);
            tableRight.setTotalWidth((pdfRight - pdfLeft)/2);
            tableRight.setWidths(widthLeftRight);
            tableRight.getDefaultCell().setBorder(Rectangle.NO_BORDER);

            // タイトルの追加
            Paragraph para = new Paragraph(pdfTitle, titleFont);
            //para.setAlignment(Element.ALIGN_CENTER);
            PdfPCell cell = new PdfPCell(para);
            cell.setFixedHeight(40f);
            cell.setColspan(2);
            cell.setPadding(4f);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(new Color(214,217,223));
            //tableParent.addCell(cell);
            
            bInitialized = true;
            //255,206,217
        } catch (IOException ex) {
            Logger.getLogger(KartePDFMaker2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DocumentException ex) {
            Logger.getLogger(KartePDFMaker2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * データ(行)の始まり
     * @param align 行揃え
     */
    public void addDataStart(int align) {
        if(bInitialized == false) return;
        if(bCreating == true) addDataEnd();
        pdfParagraph = new Paragraph();
        paragraphAlign = align;
        bCreating = true;
    }
    
    /**
     * データの追加
     * @param data データ
     * @param fontSize フォントサイズ
     * @param style スタイル
     * @param color カラー
     */
    public void addData(String data, int fontSize, int style, Color color) {
        if(bInitialized == false || bCreating == false) return;
        Font font = new Font(baseFont, (float)fontSize, style, color);
        Chunk chunk = new Chunk(data, font);
        //float subscript = -8.0f;
        //chunk.setTextRise(subscript);
        pdfParagraph.add(chunk);
    }
    
    /**
     * 画像の追加
     * @param buf JPEGのデータ
     */
    public void addImage(byte[] buf) {
        if(bInitialized == false || bCreating == false) return;
        try {
            Image img = Image.getInstance(buf);
            float width = img.getWidth();
            float height = img.getHeight();
            float maxsize = 0f;
            if(width > height) {
                maxsize = width / 2;
            }else{
                maxsize = height / 2;
            }
//s.oh^ 2013/02/07 印刷対応
            maxsize = 150;
//s.oh$
            if(width >= height) {
                height = height * (maxsize / width);
                width = maxsize;
            }else{
                width = width * (maxsize / height);
                height = maxsize;
            }
            img.scaleAbsolute(width, height);
            Chunk chunk = new Chunk(img, 0, 0);
            pdfParagraph.add(chunk);
        } catch (BadElementException ex) {
            Logger.getLogger(KartePDFMaker2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(KartePDFMaker2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(KartePDFMaker2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * スタンプの追加
     * @param data データ
     * @param fontSize フォントサイズ
     * @param style スタイル
     * @param color カラー
     */
    public void addStamp(String data, int fontSize, int style, Color color) {
        if(bInitialized == false || bCreating == false) return;
        Font font = new Font(baseFont, (float)fontSize, style, color);
        Chunk chunk = new Chunk(data, font);
        pdfParagraph.add(chunk);
    }
    
    /**
     * データ(行)の終わり
     */
    public void addDataEnd() {
        if(bInitialized == false) return;
        PdfPCell cell = new PdfPCell(pdfParagraph);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(4f);
        cell.setHorizontalAlignment(paragraphAlign);
        if(getKarteMode() == KM_SOA) {
            tableLeft.addCell(cell);
        }else{
            tableRight.addCell(cell);
        }
        bCreating = false;
    }

    /**
     * テーブルへデータの追加
     */
    public void addKartePDFData(String data, int fontSize, int style, Color color) {
        Font font = new Font(baseFont, (float)fontSize, style, color);
        PdfPCell cell = new PdfPCell(new Phrase(data, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(4f);
        cell.setHorizontalAlignment(paragraphAlign);
        if(getKarteMode() == KM_SOA) {
            tableLeft.addCell(cell);
        }else{
            tableRight.addCell(cell);
        }
    }
    
    /**
     * テーブルへ画像の追加
     */
    public void addKartePDFImage(byte[] buf, int align) {
        try {
            //Image img = Image.getInstance("E:\\Unitea画面イメージ.jpg");
            // addImage(Image, 横幅, 傾き, 傾き, 高さ, X座標, Y座標)
            //cb.addImage(jpg, jpg.getWidth(), 0, 0, jpg.getHeight(), 10, 500);
            //cb.addImage(jpg, 300, 0, 0, 100, 100, 500);
            // SetAbsolutePosition(X座標, Y座標);
            //img.setAbsolutePosition(
            //(PageSize.POSTCARD.getWidth() - img.getScaledWidth()) / 2,
            //(PageSize.POSTCARD.getHeight() - img.getScaledHeight()) / 2);
            //writer.getDirectContent().addImage(img, true);
            //PdfContentByte cb = PDF_WRITER.getDirectContent();
            //Image img = new Image.getInstance(buf);
            Image img = Image.getInstance(buf);
            float width = img.getWidth();
            float height = img.getHeight();
            float maxsize = 0f;
            if(width > height) {
                maxsize = width / 2;
            }else{
                maxsize = height / 2;
            }
            if(width >= height) {
                height = height * (maxsize / width);
                width = maxsize;
            }else{
                width = width * (maxsize / height);
                height = maxsize;
            }
            img.scaleAbsolute(width, height);
            PdfPCell cell = new PdfPCell(img);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setPadding(4f);
            cell.setHorizontalAlignment(align);
            if(getKarteMode() == KM_SOA) {
                tableLeft.addCell(cell);
            }else{
                tableRight.addCell(cell);
            }
        } catch (BadElementException ex) {
            Logger.getLogger(KartePDFMaker2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(KartePDFMaker2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(KartePDFMaker2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * カルテ用PDFの保存
     * @return String
     */
    @Override
    public String create() {
        try {
            // 各テーブルを追加
            PdfPCell cell = new PdfPCell(tableLeft);
            cell.setBorder(Rectangle.RIGHT);
            tableParent.addCell(cell);
            cell = new PdfPCell(tableRight);
            cell.setBorder(Rectangle.LEFT);
            tableParent.addCell(cell);
            // テーブルをドキュメントに追加
            //tableParent.writeSelectedRows(0, -1, pdfLeft, pdfTop, pdfWriter.getDirectContent());
            pdfDoc.add(tableParent);
            // ドキュメントの終了
            pdfDoc.close();
        } catch (DocumentException ex) {
            Logger.getLogger(KartePDFMaker2.class.getName()).log(Level.SEVERE, null, ex);
        }
        bInitialized = false;
        return getPathToPDF();
    }
}
