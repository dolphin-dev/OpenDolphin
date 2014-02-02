/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.impl.lbtest;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.*;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaSizeName;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import open.dolphin.client.ClientContext;
import open.dolphin.infomodel.LabTestRowObject;
import open.dolphin.infomodel.LabTestValueObject;
import open.dolphin.letter.AbstractLetterPDFMaker;
import open.dolphin.table.ListTableModel;
import org.jfree.chart.JFreeChart;

/**
 * ラボデータのPDF出力
 * @author Life Sciences Computing Corporation.
 */
public class LaboTestOutputPDF extends AbstractLetterPDFMaker {
    public static final int PDF_TOP_MARGIN          = 30;   // PDF Top margin
    public static final int PDF_LEFT_MARGIN         = 10;   // PDF Left margin
    public static final int PDF_RIGHT_MARGIN        = 10;   // PDF Right margin
    public static final int PDF_BOTTOM_MARGIN       = 50;   // PDF Bottom margin
    public static final int CHART_HEIGHT            = 250;
    public static final int TABLE_FONTSIZE          = 11;
    public static final int HEADER_FONTSIZE         = 15;
    private static final int GRAPHSPACE_ROWCOUNT    = 30;
    private static final Color COLOR_COLUMN         = new Color(222,222,222);
    private static final Color COLOR_CELL1          = new Color(255,255,255);
    private static final Color COLOR_CELL2          = new Color(237,243,254);
    private static final String MSG_CREATEPDF_ERR   = "PDFの作成に失敗しました。";
    
    private String patID;                                   // Patient ID
    private String patName;
    private Date saveDate;                                  // Save Date
    private JTable laboTable;
    private JFreeChart freeChart;
    
    public LaboTestOutputPDF(String id, String name, Date date, String path, JTable table, JFreeChart chart) {
        patID = id;
        patName = name;
        saveDate = date;
        this.setDocumentDir(path);
        laboTable = table;
        freeChart = chart;
    }
    
    public String createPDF() throws DocumentException, FileNotFoundException, IOException {
        setMarginTop(PDF_TOP_MARGIN);
        setMarginLeft(PDF_LEFT_MARGIN);
        setMarginRight(PDF_RIGHT_MARGIN);
        setMarginBottom(PDF_BOTTOM_MARGIN);

        org.jfree.text.TextUtilities.setUseDrawRotatedStringWorkaround(false);
        Document pdfDoc = new Document(
                PageSize.A4,
                getMarginLeft(),
                getMarginRight(),
                getMarginTop(),
                getMarginBottom());
        //Document pdfDoc = new Document(new Rectangle(500, 250));

        StringBuilder sbPath = new StringBuilder();
        sbPath.append(getDocumentDir());
        if(getDocumentDir().endsWith(File.separator) == false) {
            sbPath.append(File.separator);
        }
        if(saveDate != null) {
            sbPath.append(patID).append("_");
            sbPath.append(new SimpleDateFormat("yyyyMMddHHmmss").format(saveDate));
            sbPath.append(EXT_PDF);
        }else{
            sbPath.append("Temp.pdf");
        }
        setPathToPDF(sbPath.toString());         // 呼び出し側で取り出せるように保存する

        // Documentの作成
        PdfWriter pdfWriter = PdfWriter.getInstance(pdfDoc, new FileOutputStream(sbPath.toString()));
        
        // ヘッダーの設定をする
        Font headerFont = new Font(BaseFont.createFont("HeiseiKakuGo-W5", "UniJIS-UCS2-H", BaseFont.NOT_EMBEDDED), HEADER_FONTSIZE, Font.BOLD);
        HeaderFooter header = new HeaderFooter(new Phrase(patName + " 様", headerFont), false);
        header.setAlignment(Element.ALIGN_CENTER);
        header.setBorder(Rectangle.NO_BORDER);
        pdfDoc.setHeader(header);

        // フッターの設定をする
        HeaderFooter footer = new HeaderFooter(new Phrase("--"), new Phrase("--"));
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setBorder(Rectangle.NO_BORDER);
        pdfDoc.setFooter(footer);

        pdfDoc.open();

        // PDF Top/Left/Right/Bottom
        int pdfTop     = (int)pdfWriter.getPageSize().getHeight() - getMarginTop();
        int pdfLeft    = (int)pdfWriter.getPageSize().getLeft() + getMarginLeft();
        int pdfRight   = (int)pdfWriter.getPageSize().getRight() - getMarginRight();
        int pdfBottom  = (int)pdfWriter.getPageSize().getBottom() + getMarginBottom();

        // フォントの作成
        baseFont    = BaseFont.createFont(HEISEI_MIN_W3, UNIJIS_UCS2_HW_H, false);
        titleFont   = new Font(baseFont, getTitleFontSize(), 0, new Color(0,0,249));
        bodyFont    = new Font(baseFont, getBodyFontSize());
        
        // ラボデータテーブルの作成
        PdfPTable labo = new PdfPTable(laboTable.getColumnCount());
        labo.setTotalWidth(pdfRight - pdfLeft);
        int cellWidth = (pdfRight - pdfLeft) / (laboTable.getColumnCount() + 1);
        int width[] = {cellWidth * 2, cellWidth, cellWidth, cellWidth, cellWidth, cellWidth, cellWidth};
        labo.setWidths(width);
        labo.getDefaultCell().setBorder(Rectangle.BOX);
        
        // グラフの追加
        Font cellFont = new Font(baseFont, (float)TABLE_FONTSIZE, com.lowagie.text.Font.NORMAL, new Color(0,0,0));
        // グラフ出力のマスク
        //if(freeChart != null && laboTable.getSelectedRowCount() > 0) {
        //    PdfContentByte cb = pdfWriter.getDirectContent();
        //    PdfTemplate tp = cb.createTemplate((int)pdfWriter.getPageSize().getWidth() - PDF_LEFT_MARGIN - PDF_RIGHT_MARGIN, CHART_HEIGHT);
        //    //Graphics2D g2d = tp.createGraphics((int)pdfWriter.getPageSize().getWidth(), CHART_HEIGHT, new DefaultFontMapper());
        //    // 日本語表示
        //    Graphics2D g2d = tp.createGraphics((int)pdfWriter.getPageSize().getWidth() - PDF_LEFT_MARGIN - PDF_RIGHT_MARGIN, CHART_HEIGHT, new AsianFontMapper(AsianFontMapper.JapaneseFont_Go, AsianFontMapper.JapaneseEncoding_H));
        //    Rectangle2D r2d = new Rectangle2D.Double(PDF_LEFT_MARGIN, 0, (int)pdfWriter.getPageSize().getWidth() - PDF_LEFT_MARGIN - PDF_RIGHT_MARGIN, CHART_HEIGHT);
        //    freeChart.draw(g2d, r2d);
        //    g2d.dispose();
        //    cb.addTemplate(tp, 0, (int)pdfWriter.getPageSize().getHeight() - CHART_HEIGHT);
        //    
        //    // グラフのスペースを空ける
        //    Paragraph paragraph = new Paragraph();
        //    Chunk chunk = new Chunk("", cellFont);
        //    paragraph.add(chunk);
        //    PdfPCell cell = new PdfPCell(paragraph);
        //    cell.setVerticalAlignment(Element.ALIGN_CENTER);
        //    cell.setBorder(Rectangle.NO_BORDER);
        //    cell.setPadding(4f);
        //    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        //    for(int i = 0; i < GRAPHSPACE_ROWCOUNT; i++) {
        //        for(int j = 0; j < 7; j++) {
        //            labo.addCell(cell);
        //        }
        //    }
        //}
        
        ListTableModel<LabTestRowObject> tableModel = (ListTableModel<LabTestRowObject>)laboTable.getModel();
        // カラム
        for(int col = 0; col < laboTable.getColumnCount(); col++) {
            String sampleTime = tableModel.getColumnName(col);
            if(sampleTime == null) sampleTime = "";
            addData(labo, cellFont, sampleTime, COLOR_COLUMN);
        }
        // データ
        for(int cnt = 0; cnt < laboTable.getRowCount(); cnt++) {
            java.util.List<LabTestRowObject> dataProvider = tableModel.getDataProvider();
            LabTestRowObject rowObj = dataProvider.get(cnt);
            java.util.List<LabTestValueObject> values = rowObj.getValues();
            Color cellColor = (cnt % 2 == 0) ? COLOR_CELL1 : COLOR_CELL2;
            for(int col = 0; col < laboTable.getColumnCount(); col++) {
                Font font = cellFont;
                if(col > 0) {
                    // 文字色の変更
                    LabTestValueObject valueObj = rowObj.getLabTestValueObjectAt(col -1);
                    String text = valueObj != null ? valueObj.getValue() : "";
                    String flag = valueObj != null ? valueObj.getOut() : null;
                    String toolTip = valueObj != null ? valueObj.concatComment() : "";
                    if (flag != null && flag.startsWith("H")) {
                        font = new Font(baseFont, (float)TABLE_FONTSIZE, com.lowagie.text.Font.NORMAL, Color.RED);
                    } else if (flag != null && flag.startsWith("L")) {
                        font = new Font(baseFont, (float)TABLE_FONTSIZE, com.lowagie.text.Font.NORMAL, Color.BLUE);
                    } else if (toolTip!= null && (!toolTip.equals(""))) {
                        font = new Font(baseFont, (float)TABLE_FONTSIZE, com.lowagie.text.Font.NORMAL, Color.MAGENTA);
                    } else {
                        font = new Font(baseFont, (float)TABLE_FONTSIZE, com.lowagie.text.Font.NORMAL, Color.black);
                    }
                }
                if(col == 0) {
                    if(rowObj != null) {
                        addData(labo, font, rowObj.getItemName(), cellColor);
                    }else{
                        addData(labo, font, "", cellColor);
                    }
                }else{
                    LabTestValueObject value = values.get(col -1);
                    if(value != null) {
                        addData(labo, font, value.getValue(), cellColor);
                    }else{
                        addData(labo, font, "", cellColor);
                    }
                }
            }
        }
        
        // テーブルをドキュメントに追加
        //tableParent.writeSelectedRows(0, -1, pdfLeft, pdfTop, pdfWriter.getDirectContent());
        pdfDoc.add(labo);
        
        // ドキュメントの終了
        pdfDoc.close();
        
        return getPathToPDF();
    }
    
    private void addData(PdfPTable labo, Font font, String data, Color bgColor) {
        String val = (data == null) ? "" : data;
        Paragraph paragraph = new Paragraph();
        Chunk chunk = new Chunk(val, font);
        paragraph.add(chunk);
        PdfPCell cell = new PdfPCell(paragraph);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(Rectangle.BOX);
        cell.setPadding(4f);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBackgroundColor(bgColor);
        labo.addCell(cell);
    }

    @Override
    public String create() {
        String ret = null;
        try {
            ret = createPDF();
        } catch (DocumentException ex) {
            Logger.getLogger(LaboTestOutputPDF.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, MSG_CREATEPDF_ERR, ClientContext.getString("productString"), JOptionPane.WARNING_MESSAGE);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LaboTestOutputPDF.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, MSG_CREATEPDF_ERR, ClientContext.getString("productString"), JOptionPane.WARNING_MESSAGE);
        } catch (IOException ex) {
            Logger.getLogger(LaboTestOutputPDF.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, MSG_CREATEPDF_ERR, ClientContext.getString("productString"), JOptionPane.WARNING_MESSAGE);
        }
        return ret;
    }
    
    /**
     * ラボデータPDFの印刷
     * @param pdfFileName 
     */
    public static void printPDF(String pdfFileName) {
        if(pdfFileName == null) return;
        // ダイアログ表示(プロパティ等は選択できない)
        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
        DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
        PrintService printService[] = PrintServiceLookup.lookupPrintServices(flavor, pras);
        PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();
        if(printService.length > 0) {
            PrintService service = ServiceUI.printDialog(null, 200, 200, printService, defaultService, flavor, pras);
            if(service != null) {
                DocPrintJob job = service.createPrintJob();
                //DocPrintJob job = defaultService.createPrintJob();
                //FileOutputStream fis;
                FileInputStream fis;
                try {
                    fis = new FileInputStream(pdfFileName);
                    DocAttributeSet das = new HashDocAttributeSet();
                    Doc doc = new SimpleDoc(fis, flavor, das);
                    //Doc doc = new SimpleDoc(fis, flavor, null);
                    job.print(doc, pras);
                    //job.print(doc, null);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(LaboTestOutputPDF.class.getName()).log(Level.SEVERE, null, ex);
                } catch (PrintException ex) {
                    Logger.getLogger(LaboTestOutputPDF.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        // ダイアログ非表示
        /*
        // docフレーバを設定
        DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
        // 印刷要求属性を設定
        PrintRequestAttributeSet requestAttributes =
        new HashPrintRequestAttributeSet();
        requestAttributes.add(new Copies(3));
        requestAttributes.add(MediaSizeName.ISO_A4);
        // 印刷サービスを検出
        PrintService services =
        PrintServiceLookup.lookupDefaultPrintService();
        // 印刷ジョブを作成
        DocPrintJob job = services.createPrintJob();
        try {
            // docオブジェクトを生成
            FileInputStream data = new FileInputStream("E:\\00001.pdf");
            DocAttributeSet docAttributes = new HashDocAttributeSet();
            Doc doc = new SimpleDoc(data, flavor, docAttributes);
            // 印刷
            job.print(doc, requestAttributes);
        }catch (IOException e) {
            e.printStackTrace();
        }catch (PrintException e) {
            e.printStackTrace();
        }
        */
    }
}
