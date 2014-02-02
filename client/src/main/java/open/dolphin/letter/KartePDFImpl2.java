/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.letter;

import com.lowagie.text.Element;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.ServiceUI;
import javax.print.SimpleDoc;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaSizeName;
import open.dolphin.client.KartePaneDumper_2;
import open.dolphin.impl.lbtest.LaboTestOutputPDF;
import open.dolphin.infomodel.BundleDolphin;
import open.dolphin.infomodel.ModuleModel;
import open.dolphin.infomodel.SchemaModel;
import open.dolphin.project.Project;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author Life Sciences Computing Corporation.
 */
public class KartePDFImpl2 {
    private KartePaneDumper_2 pdfSOA;  // PDF SOA
    private KartePaneDumper_2 pdfPlan; // PDF Plan
    private KartePDFMaker2 pdfMarker;   // PDF Marker

    private static final String COMPONENT_ELEMENT_NAME = "component";
    private static final String STAMP_HOLDER = "stampHolder";
    private static final String SCHEMA_HOLDER = "schemaHolder";
    private static final int TT_SECTION = 0;
    private static final int TT_PARAGRAPH = 1;
    private static final int TT_CONTENT = 2;
    private static final int TT_ICON = 3;
    private static final int TT_COMPONENT = 4;
    private static final int TT_PROGRESS_COURSE = 5;
    private static final String SECTION_NAME = "section";
    private static final String PARAGRAPH_NAME = "paragraph";
    private static final String CONTENT_NAME = "content";
    private static final String COMPONENT_NAME = "component";
    private static final String ICON_NAME = "icon";
    private static final String ALIGNMENT_NAME = "Alignment";
    private static final String FOREGROUND_NAME = "foreground";
    private static final String SIZE_NAME = "size";
    private static final String BOLD_NAME = "bold";
    private static final String ITALIC_NAME = "italic";
    private static final String UNDERLINE_NAME = "underline";
    private static final String TEXT_NAME = "text";
    private static final String NAME_NAME = "name";
    private static final String LOGICAL_STYLE_NAME = "logicalStyle";
    private static final String PROGRESS_COURSE_NAME = "kartePane";
    private static final String[] REPLACES = new String[] { "<", ">", "&", "'" ,"\""};
    private static final String[] MATCHES = new String[] { "&lt;", "&gt;", "&amp;", "&apos;", "&quot;" };
    private static final String NAME_STAMP_HOLDER = "name=\"stampHolder\"";

    /**
     * コンストラクタ
     * @param valPath フォルダパス
     * @param valPathID タイトル
     * @param valDate 保存時刻
     * @param valSOA SOA
     * @param valPlan Plan
     */
    public KartePDFImpl2(String valPath, String valDocID, String valPatID, String valPatName, String valTitle, Date valDate, KartePaneDumper_2 valSOA, KartePaneDumper_2 valPlan) {
        // SOA
        pdfSOA = valSOA;
        // Plan
        pdfPlan = valPlan;
        // PDF Marker
        pdfMarker = new KartePDFMaker2(valPatID, valPatName, valTitle, valDate, valDocID);
        pdfMarker.setDocumentDir(valPath);
    }
    
    /**
     * SOAカルテ用PDFの作成
     */
    public void createKarteSOAToPDF() {
        /*
        ModuleModel[] soaOrgModules = PDF_SOA.getModule();
        ModuleModel[] pOrgModules = PDF_PLAN.getModule();
        // SOA と P のモジュールをわける
        // また各々の Pane の spec を取得する
        ArrayList<ModuleModel> soaModules = new ArrayList<ModuleModel>();
        ArrayList<ModuleModel> pModules = new ArrayList<ModuleModel>();
        String soaSpec = null;
        String pSpec = null;
        
        for(int i = 0; i < soaOrgModules.length; i++) {
            ModuleModel bean = soaOrgModules[i];
            String role = bean.getModuleInfoBean().getStampRole();
            if (role.equals(IInfoModel.ROLE_SOA)) {
                soaModules.add(bean);
                
            } else if (role.equals(IInfoModel.ROLE_SOA_SPEC)) {
                soaSpec = ((ProgressCourse) bean.getModel()).getFreeText();
                
            } else if (role.equals(IInfoModel.ROLE_P)) {
                pModules.add(bean);
                
            } else if (role.equals(IInfoModel.ROLE_P_SPEC)) {
                pSpec = ((ProgressCourse) bean.getModel()).getFreeText();
            }
        }
        
        if (soaSpec != null && pSpec != null) {
            
            int index = soaSpec.indexOf(NAME_STAMP_HOLDER);
            if (index > 0) {
                String sTmp = soaSpec;
                String pTmp = pSpec;
                soaSpec = pTmp;
                pSpec = sTmp;
            }
        }

        // SOA Pane をレンダリングする
        if (soaSpec == null || soaSpec.equals("")) {
            //for (ModuleModel mm : soaModules) {
            //    soaPane.stamp(mm);
            //    soaPane.makeParagraph();
            //}
        } else {
            //debug("Render SOA Pane");
            //debug("Module count = " + soaModules.size());
            //bSoaPane = true;
            //thePane = soaPane;
            renderPane(soaSpec);
        }
        
        // P Pane をレンダリングする
        if (pSpec == null || pSpec.equals("")) {
            //// 前回処方適用のようにモジュールのみの場合
            //for (ModuleModel mm : pModules) {
            //    //pPane.stamp(mm);
            //    pPane.flowStamp(mm);
            //    pPane.makeParagraph();
            //    pPane.makeParagraph();
            //}
        } else {
            //bSoaPane = false;
            //thePane = pPane;
            renderPane(pSpec);
        }
        * 
        */
        String soaSpec = pdfSOA.getSpec();
        String pSpec = pdfPlan.getSpec();
        if (soaSpec != null && pSpec != null) {
            int index = soaSpec.indexOf(NAME_STAMP_HOLDER);
            if (index > 0) {
                String sTmp = soaSpec;
                String pTmp = pSpec;
                soaSpec = pTmp;
                pSpec = sTmp;
            }
        }
        pdfMarker.setKarteMode(KartePDFMaker2.KM_SOA);
        renderPDF(soaSpec);
        pdfMarker.setKarteMode(KartePDFMaker2.KM_PLAN);
        renderPDF(pSpec);
    }
    
    /**
     * TextPane Dump の XML を解析する。
     * @param xml 作成したカルテのXML情報
     */
    private void renderPDF(String xml) {
        SAXBuilder docBuilder = new SAXBuilder();
        
        try {
            StringReader sr = new StringReader(xml);
            org.jdom.Document doc = docBuilder.build(new BufferedReader(sr));
            org.jdom.Element root = (org.jdom.Element) doc.getRootElement();
            
            writeChildren(root);
        }
        // indicates a well-formedness error
        catch (JDOMException e) {
            e.printStackTrace(System.err);
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }
    
   /**
     * 子要素をパースする。
     * @param current 要素
     */
    private void writeChildren(org.jdom.Element current) {
        
        int eType = -1;
        String eName = current.getName();
        
        if (eName.equals(PARAGRAPH_NAME)) {
            eType = TT_PARAGRAPH;
            startParagraph(current.getAttributeValue(ALIGNMENT_NAME));
        } else if (eName.equals(CONTENT_NAME) && (current.getChild(TEXT_NAME) != null)) {
            eType = TT_CONTENT;
            startContent(current.getAttributeValue(FOREGROUND_NAME), 
                    current.getAttributeValue(SIZE_NAME), 
                    current.getAttributeValue(BOLD_NAME), 
                    current.getAttributeValue(ITALIC_NAME), 
                    current.getAttributeValue(UNDERLINE_NAME), 
                    current.getChildText(TEXT_NAME));
        } else if (eName.equals(COMPONENT_NAME)) {
            eType = TT_COMPONENT;
            startComponent(current.getAttributeValue(NAME_NAME), // compoenet=number
                    current.getAttributeValue(COMPONENT_ELEMENT_NAME));
        } else if (eName.equals(ICON_NAME)) {
            eType = TT_ICON;
        } else if (eName.equals(PROGRESS_COURSE_NAME)) {
            eType = TT_PROGRESS_COURSE;
        } else if (eName.equals(SECTION_NAME)) {
            eType = TT_SECTION;
        }
        
        // 子を探索するのはパラグフとトップ要素のみ
        if (eType == TT_PARAGRAPH || eType == TT_PROGRESS_COURSE
                || eType == TT_SECTION) {
            
            java.util.List children = (java.util.List) current.getChildren();
            Iterator iterator = children.iterator();
            
            while (iterator.hasNext()) {
                org.jdom.Element child = (org.jdom.Element) iterator.next();
                writeChildren(child);
            }
            if(eType == TT_PARAGRAPH) {
                pdfMarker.addDataEnd();
            }
        }
    }

    /**
     * Paragraph内の要素の解析
     * @param alignStr 行揃え情報
     */
    private void startParagraph(String alignStr) {
        int align = Element.ALIGN_LEFT;
        if (alignStr != null) {
            if (alignStr.equals("0")) {
                align = Element.ALIGN_LEFT;
            } else if (alignStr.equals("1")) {
                align = Element.ALIGN_CENTER;
            } else if (alignStr.equals("2")) {
                align = Element.ALIGN_RIGHT;
            }
        }
        pdfMarker.addDataStart(align);
    }

    /**
     * Contentの解析
     * @param foreground foreground属性
     * @param size サイズ
     * @param bold ボールド
     * @param italic イタリック
     * @param underline 下線
     * @param text テキスト
     */
    private void startContent(String foreground, String size, String bold,
            String italic, String underline, String text) {
        int r = 0;
        int g = 0;
        int b = 0;
        int fontSize = 12;
        int style = style = 0;
        
        // 特殊文字を戻す
        for (int i = 0; i < REPLACES.length; i++) {
            text = text.replaceAll(MATCHES[i], REPLACES[i]);
        }
        
        // このコンテントに設定する AttributeSet
        //MutableAttributeSet atts = new SimpleAttributeSet();
        
        // foreground 属性を設定する
        if (foreground != null) {
            StringTokenizer stk = new StringTokenizer(foreground, ",");
            if (stk.hasMoreTokens()) {
                r = Integer.parseInt(stk.nextToken());
                g = Integer.parseInt(stk.nextToken());
                b = Integer.parseInt(stk.nextToken());
                //StyleConstants.setForeground(atts, new Color(r, g, b));
            }
        }
        
        // size 属性を設定する
        if (size != null) {
            fontSize = Integer.parseInt(size);
            //StyleConstants.setFontSize(atts, Integer.parseInt(size));
        }
        
        // bold 属性を設定する
        if (bold != null) {
            style = com.lowagie.text.Font.BOLD;
            //StyleConstants.setBold(atts, Boolean.valueOf(bold).booleanValue());
        }
        
        // italic 属性を設定する
        if (italic != null) {
            style = com.lowagie.text.Font.ITALIC;
            //StyleConstants.setItalic(atts, Boolean.valueOf(italic)
            //.booleanValue());
        }
        
        // underline 属性を設定する
        if (underline != null) {
            style = com.lowagie.text.Font.UNDERLINE;
            //StyleConstants.setUnderline(atts, Boolean.valueOf(underline)
            //.booleanValue());
        }
        
        // テキストを挿入する
        //thePane.insertFreeString(text, atts);
        if(text != null && text.length() > 0) {
            //PDF_MARKER.addKartePDFData(text, align, fontSize, style, new Color(r,g,b));
            pdfMarker.addData(text, fontSize, style, new Color(r,g,b));
        }
    }

    /**
     * Componentの開始
     * @param name 名前
     * @param number 番号
     */
    private void startComponent(String name, String number) {
        try {
            if (name != null && name.equals(STAMP_HOLDER)) {
                //int index = Integer.parseInt(number);
                //ModuleModel stamp = bSoaPane
                //        ? (ModuleModel) soaModules.get(index)
                //        : (ModuleModel) pModules.get(index);
                //thePane.flowStamp(stamp);
                int idx = Integer.parseInt(number);
                ModuleModel[] stamp = pdfPlan.getModule();
                if(stamp != null && stamp[idx].getModel() instanceof BundleDolphin) {
                    BundleDolphin bd = (BundleDolphin)stamp[idx].getModel();
                    //PDF_MARKER.addKartePDFData(bd.toString(), align, PDF_MARKER.getBodyFontSize(), com.lowagie.text.Font.NORMAL, new Color(0,0,0));
                    pdfMarker.addData(bd.toString(), pdfMarker.getBodyFontSize(), com.lowagie.text.Font.NORMAL, new Color(0,0,0));
                }
            } else if (name != null && name.equals(SCHEMA_HOLDER)) {
                int idx = Integer.parseInt(number);
                SchemaModel[] schema = pdfSOA.getSchema();
                if(schema != null) {
                    //Image img = new Image.getInstance(schema[idx].getJpegByte());
                    //PDF_MARKER.addKartePDFImage(schema[idx].getJpegByte(), align);
                    pdfMarker.addImage(schema[idx].getJpegByte());
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * Planカルテ用PDFの作成
     */
    public void createKartePlanToPDF() {
        
    }

    /**
     * 作成
     * @return String
     */
    public String create() {
        String path;
//        try {
            pdfMarker.initKartePDF();
            createKarteSOAToPDF();
            createKartePlanToPDF();
            path = pdfMarker.create();
            /*
            Document document = new Document(
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
            sbPath.append("000001").append("_");
            sbPath.append(new SimpleDateFormat("yyyyMMddHHmmss").format(PDF_SAVEDATE));
            sbPath.append(EXT_PDF);
            setPathToPDF(sbPath.toString());         // 呼び出し側で取り出せるように保存する
            
            // Open Document
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(sbPath.toString()));
            document.open();

            // PDFの有効領域の取得
            int pdfTop = (int)writer.getPageSize().getHeight() - getMarginTop();
            int pdfLeft = (int)writer.getPageSize().getLeft() + getMarginLeft();
            int pdfRight = (int)writer.getPageSize().getRight() - getMarginRight();
            int pdfBottom = (int)writer.getPageSize().getBottom() + getMarginBottom();

            // Font
            baseFont = BaseFont.createFont(HEISEI_MIN_W3, UNIJIS_UCS2_HW_H, false);
            titleFont = new Font(baseFont, getTitleFontSize());
            bodyFont = new Font(baseFont, getBodyFontSize());

            // タイトル
            Paragraph para = new Paragraph(TITLE, titleFont);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);

            // ↓↓↓↓↓ これ下を変更 ↓↓↓↓↓
            PdfContentByte cb = writer.getDirectContent();
            BaseFont bf = BaseFont.createFont("HeiseiKakuGo-W5", "UniJIS-UCS2-H", BaseFont.NOT_EMBEDDED);
            
            cb.beginText();
            cb.setFontAndSize(bf, 10);
            cb.showTextAligned(PdfContentByte.ALIGN_CENTER, "Hello People!", (pdfRight - pdfLeft) / 2, pdfTop, 0);
            cb.endText();
            
            cb.saveState();
            cb.beginText();
            cb.moveText(pdfLeft, pdfTop);
            cb.showText("TEST TEST TEST");
            cb.endText();
            cb.restoreState();
            
            //テーブルの作成
            PdfPTable table = new PdfPTable(3);
            //テーブルの幅を指定
            table.setTotalWidth(300);
            int width[]={50,30,20};
            table.setWidths(width);
            //罫線の設定
            table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            //フォントの設定
            Font font=new Font(bf,12);
            PdfPCell cell;
//            cell = new PdfPCell(new Phrase("セル1",font)); //追加するセルを作成
Paragraph para = new Paragraph("TESTOK", titleFont);
para.setAlignment(Element.ALIGN_CENTER);
cell = new PdfPCell(para);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell); //セルをテーブルに追加
            cell = new PdfPCell(new Phrase("セル2",font));
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("セル3",font));
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("セル4",font));
            cell.setColspan(2);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("セル5",font));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
        //テーブルの作成
        PdfPTable table2 = new PdfPTable(2);
        //テーブルの幅を指定
        table2.setTotalWidth(pdfRight - pdfLeft);
        int width2[]={(pdfRight - pdfLeft) / 2, (pdfRight - pdfLeft) / 2};
        table2.setWidths(width2);
        //罫線の設定
        table2.getDefaultCell().setBorder(Rectangle.BOX);
        //フォントの設定
        cell = new PdfPCell(new Phrase("セル1",font)); //追加するセルを作成
        cell.setColspan(2);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table2.addCell(cell); //セルをテーブルに追加
        //cell = new PdfPCell(new Phrase("セル2",font));
        //table2.addCell(cell);
        //cell = new PdfPCell(new Phrase("セル3",font));
        //table2.addCell(cell);
        //cell = new PdfPCell(new Phrase("セル4",font));
        //table2.addCell(cell);
        table2.addCell(table);
        table2.addCell(table);
            //テーブルをドキュメントに追加
            table2.writeSelectedRows(0, -1, pdfLeft, pdfTop, writer.getDirectContent());

            cb.beginText();
            cb.setFontAndSize(bf, 10);
            cb.moveText(0, rect.getTop());
            cb.showText("００００００００００");
            cb.endText();
            
            cb.setLineWidth(0f);
            cb.moveTo(pdfLeft, pdfTop);
            cb.lineTo(pdfLeft, pdfBottom);
            cb.moveTo(pdfLeft, pdfTop);
            cb.lineTo(pdfRight, pdfTop);
            cb.moveTo(pdfRight, pdfTop);
            cb.lineTo(pdfRight, pdfBottom);
            cb.moveTo(pdfLeft, pdfBottom);
            cb.lineTo(pdfRight, pdfBottom);
            cb.moveTo(0, 0);
            cb.lineTo(PageSize.A4.getWidth(), PageSize.A4.getHeight());
            cb.moveTo(0, PageSize.A4.getHeight());
            cb.lineTo(PageSize.A4.getWidth(), 0);
            cb.moveTo(PageSize.A4.getWidth() / 2, 0);
            cb.lineTo(PageSize.A4.getWidth() / 2, PageSize.A4.getHeight());
            cb.moveTo(0, PageSize.A4.getHeight() / 2);
            cb.lineTo(PageSize.A4.getWidth(), PageSize.A4.getHeight() / 2);
            cb.stroke();
            // draw text
            String text = "AWAY again ";
            cb.beginText();
            cb.setFontAndSize(bf, 12);
            cb.setTextMatrix(50, 800);
            cb.showText(text);
            cb.showTextAligned(PdfContentByte.ALIGN_CENTER, text + " Center", 150, 760, 0);
            cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, text + " Right", 150, 700, 0);
            cb.showTextAligned(PdfContentByte.ALIGN_LEFT, text + " Left", 150, 640, 0);
            cb.showTextAlignedKerned(PdfContentByte.ALIGN_LEFT, text + " Left", 150, 628, 0);
            cb.setTextMatrix(0, 1, -1, 0, 300, 600);
            cb.showText("Position 300,600, rotated 90 degrees.");
            for (int i = 0; i < 360; i += 30) {
                cb.showTextAligned(PdfContentByte.ALIGN_LEFT, text, 400, 700, i);
            }
            cb.endText();


            // 日付
            String dateStr = new SimpleDateFormat("yyyy/MM/dd").format(getDate());
            para = new Paragraph(dateStr, bodyFont);
            para.setAlignment(Element.ALIGN_RIGHT);
            document.add(para);

            document.add(new Paragraph("　"));
            
            // 文字列１
            Paragraph para2 = new Paragraph("テスト", bodyFont);
            para2.setAlignment(Element.ALIGN_LEFT);
            document.add(para2);
            
            document.add(new Paragraph("  "));
            
            // テーブル１
            Table pTable = new Table(4);
            pTable.setPadding(2);
            int width[] = new int[]{20, 60, 10, 10};
            pTable.setWidths(width);
            pTable.setWidth(100);
            pTable.setBorderWidth(Table.NO_BORDER);
            pTable.addCell(new Phrase("患者氏名", bodyFont));
            pTable.addCell(new Phrase("OH", bodyFont));
            pTable.addCell(new Phrase("性別", bodyFont));
            pTable.addCell(new Phrase("M", bodyFont));
            pTable.addCell(new Phrase("生年月日", bodyFont));
            Cell cell = new Cell(new Phrase("1981/03/26(30歳)", bodyFont));
            cell.setColspan(3);
            pTable.addCell(cell);
            document.add(pTable);

            // テーブル２
            Table lTable = new Table(2); //テーブル・オブジェクトの生成
            lTable.setPadding(2);
            width = new int[]{20, 80};
            lTable.setWidths(width); //各カラムの大きさを設定（パーセント）
            lTable.setWidth(100);
            lTable.addCell(new Phrase("傷病名", bodyFont));
            lTable.addCell(new Phrase("捻挫", bodyFont));
            lTable.addCell(new Phrase("紹介目的", bodyFont));
            lTable.addCell(new Phrase("なんとなく", bodyFont));
            lTable.addCell(new Phrase("既往歴\n家族歴", bodyFont));
            cell = new Cell(new Phrase("ない\nある", bodyFont));
            lTable.addCell(cell);
            lTable.addCell(new Phrase("症状経過\n検査結果\n治療経過", bodyFont));
            lTable.addCell(new Phrase("１\n２\n３", bodyFont));
            lTable.addCell(new Phrase("現在の処方", bodyFont));
            lTable.addCell(new Phrase("もうダメだ", bodyFont));
            lTable.addCell(new Phrase("備 考", bodyFont));
            lTable.addCell(new Phrase("。。。", bodyFont));
            document.add(lTable);
            
            // 文字列２
            cb = writer.getDirectContent();
            cb.beginText();
            bf = BaseFont.createFont("HeiseiKakuGo-W5", "UniJIS-UCS2-H", BaseFont.NOT_EMBEDDED);
            cb.setFontAndSize(bf, 12);
            cb.moveText(10, 800);
            cb.showText("てすと   mytest 123");
            cb.endText();
            
            // 図形
            cb.setRGBColorFill(255, 0, 0);
            cb.rectangle(100, 700, 50, 50);
            cb.fill();
            cb.setRGBColorFill(0, 0, 0);
            cb.ellipse(200, 700, 250, 650);
            cb.stroke();

            // CODE39のバーコード
            Barcode39 code39 = new Barcode39();
            code39.setCode("WIKIPEDIA");
            Image image39 = code39.createImageWithBarcode(cb, null, null);
            cb.addImage(image39, image39.getWidth(), 0,0,image39.getHeight(), 10, 600);

            // 画像出力(JPEGファイル読み込み）
            Image img = Image.getInstance("E:\\Unitea画面イメージ.jpg");
            // addImage(Image, 横幅, 傾き, 傾き, 高さ, X座標, Y座標)
            //cb.addImage(jpg, jpg.getWidth(), 0, 0, jpg.getHeight(), 10, 500);
            //cb.addImage(jpg, 300, 0, 0, 100, 100, 500);
            // SetAbsolutePosition(X座標, Y座標);
            //img.setAbsolutePosition(
            //(PageSize.POSTCARD.getWidth() - img.getScaledWidth()) / 2,
            //(PageSize.POSTCARD.getHeight() - img.getScaledHeight()) / 2);
            //writer.getDirectContent().addImage(img, true);
            
            document.newPage();

            document.close();
            
            return getPathToPDF();
            * 
            */
//        } catch (IOException ex) {
//            ClientContext.getBootLogger().warn(ex);
//            throw new RuntimeException(ERROR_IO);
//        } catch (DocumentException ex) {
//            ClientContext.getBootLogger().warn(ex);
//            throw new RuntimeException(ERROR_PDF);
//        }

        return path;
    }
    
//s.oh^ 2013/02/07 印刷対応
    public static void printPDF(String pdfFileName) {
        if(pdfFileName == null) return;
        if(Project.getBoolean(Project.KARTE_PRINT_DIRECT)) {
            // ダイアログ非表示
            //// docフレーバを設定
            //DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
            //// 印刷要求属性を設定
            //PrintRequestAttributeSet requestAttributes = new HashPrintRequestAttributeSet();
            //requestAttributes.add(new Copies(3));
            //requestAttributes.add(MediaSizeName.ISO_A4);
            //// 印刷サービスを検出
            //PrintService service = PrintServiceLookup.lookupDefaultPrintService();
            //// 印刷ジョブを作成
            //DocPrintJob job = service.createPrintJob();
            PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
            DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
            PrintService printService[] = PrintServiceLookup.lookupPrintServices(flavor, pras);
            PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();
            DocPrintJob job = (defaultService == null) ? null : defaultService.createPrintJob();
            if(job == null) {
                if(printService.length > 0) {
                    job = printService[0].createPrintJob();
                }
            }
            if(job == null) return;
            try {
                // docオブジェクトを生成
                FileInputStream data = new FileInputStream(pdfFileName);
                DocAttributeSet docAttributes = new HashDocAttributeSet();
                Doc doc = new SimpleDoc(data, flavor, docAttributes);
                // 印刷
                job.print(doc, pras);
            }catch (IOException e) {
                e.printStackTrace();
            }catch (PrintException e) {
                e.printStackTrace();
            }
        }else{
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
        }
    }
//s.oh$
}
