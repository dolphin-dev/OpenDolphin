/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.letter;

import com.lowagie.text.Element;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
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
import javax.swing.ImageIcon;
import open.dolphin.client.ClientContext;
import open.dolphin.impl.lbtest.LaboTestOutputPDF;
import open.dolphin.infomodel.AttachmentModel;
import open.dolphin.infomodel.BundleDolphin;
import open.dolphin.infomodel.DocumentModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.ModuleModel;
import open.dolphin.infomodel.ProgressCourse;
import open.dolphin.project.Project;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author S.Oh@Life Sciences Computing Corporation.
 */
public class KartePDFImpl {
    private DocumentModel model;
    private ArrayList<ModuleModel> stamps;
    private KartePDFMaker2 pdfMarker;   // PDF Marker

    private static final String COMPONENT_ELEMENT_NAME = "component";
    private static final String STAMP_HOLDER = "stampHolder";
    private static final String SCHEMA_HOLDER = "schemaHolder";
    private static final String ATTACHMENT_HOLDER = "attachmentHolder"; // Attachment追加
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
//    private static final String LOGICAL_STYLE_NAME = "logicalStyle";
    private static final String PROGRESS_COURSE_NAME = "kartePane";
    //private static final String[] REPLACES = new String[] { "<", ">", "&", "'" ,"\""};
    private static final String[] REPLACES = new String[] { "&", "<", ">", "'" ,"\""};
    //private static final String[] MATCHES = new String[] { "&lt;", "&gt;", "&amp;", "&apos;", "&quot;" };
    private static final String[] MATCHES = new String[] { "&amp;", "&lt;", "&gt;", "&apos;", "&quot;" };
//    private static final String NAME_STAMP_HOLDER = "name=\"stampHolder\"";

    /**
     * コンストラクタ
     * @param valPath フォルダパス
     * @param valPathID タイトル
     * @param valDate 保存時刻
     * @param valSOA SOA
     * @param valPlan Plan
     */
    public KartePDFImpl(String valPath, String valDocID, String valPatID, String valPatName, String valTitle, Date valDate, DocumentModel model, String docNo) {
        // SOA
        // Plan
        this.model = model;
        // PDF Marker
        pdfMarker = new KartePDFMaker2(valPatID, valPatName, valTitle, valDate, valDocID, docNo);
        pdfMarker.setDocumentDir(valPath);
    }
    
    /**
     * SOAカルテ用PDFの作成
     */
    public void createKarteSOAToPDF() {
        Collection<ModuleModel> modules = model.getModules();
        stamps = new ArrayList<ModuleModel>();
        String soaSpec = null;
        String pSpec = null;
        for (ModuleModel bean : modules) {
            String role = bean.getModuleInfoBean().getStampRole();
            if (role.equals(IInfoModel.ROLE_SOA)) {
            } else if (role.equals(IInfoModel.ROLE_SOA_SPEC)) {
                soaSpec = ((ProgressCourse) bean.getModel()).getFreeText();
            } else if (role.equals(IInfoModel.ROLE_P)) {
                stamps.add(bean);
            } else if (role.equals(IInfoModel.ROLE_P_SPEC)) {
                pSpec = ((ProgressCourse) bean.getModel()).getFreeText();
            }
            else if (bean.getModel() instanceof ProgressCourse) {
                if (soaSpec==null) {
                    soaSpec = ((ProgressCourse) bean.getModel()).getFreeText();
                } else if (pSpec==null) {
                    pSpec = ((ProgressCourse) bean.getModel()).getFreeText();
                }
            }else {
                stamps.add(bean);
            }
        }
        Collections.sort(stamps);
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
//s.oh^ 2013/09/12 PDF印刷文字サイズ
        //int fontSize = 12;
        String textSize = Project.getString(Project.KARTE_PRINT_PDF_TEXTSIZE);
        int fontSize = 12;
        if(textSize != null && textSize.length() >= 1 && !textSize.startsWith("0")) {
            fontSize = Integer.parseInt(textSize);
        }
//s.oh$
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
//s.oh^ 2013/09/12 PDF印刷文字サイズ
            //fontSize = Integer.parseInt(size);
            int diffSize = Integer.parseInt(size) - 12;
            fontSize = fontSize + diffSize;
            if(fontSize <= 0) {
                fontSize = 1;
            }
//s.oh$
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
                int idx = Integer.parseInt(number);
                if(stamps != null && stamps.size() > idx && stamps.get(idx).getModel() instanceof BundleDolphin) {
                    BundleDolphin bd = (BundleDolphin)stamps.get(idx).getModel();
                    //PDF_MARKER.addKartePDFData(bd.toString(), align, PDF_MARKER.getBodyFontSize(), com.lowagie.text.Font.NORMAL, new Color(0,0,0));
//s.oh^ 2013/09/12 PDF印刷文字サイズ
                    //pdfMarker.addData(bd.toString(), pdfMarker.getBodyFontSize(), com.lowagie.text.Font.NORMAL, new Color(0,0,0));
                    String textSize = Project.getString(Project.KARTE_PRINT_PDF_TEXTSIZE);
                    int fontSize = pdfMarker.getBodyFontSize();
                    if(textSize != null && textSize.length() >= 1 && !textSize.startsWith("0")) {
                        fontSize = Integer.parseInt(textSize);
                    }
                    pdfMarker.addData(bd.toString(), fontSize, com.lowagie.text.Font.NORMAL, new Color(0,0,0));
//s.oh$
                }
            } else if (name != null && name.equals(SCHEMA_HOLDER)) {
                int idx = Integer.parseInt(number);
                if(model.getSchema() != null && model.getSchema().size() > idx) {
                    //Image img = new Image.getInstance(schema[idx].getJpegByte());
                    //PDF_MARKER.addKartePDFImage(schema[idx].getJpegByte(), align);
//s.oh^ 2013/03/27 不具合修正(保存前のシェーマが作成されない)
                    //pdfMarker.addImage(schema[idx].getJpegByte());
                    if(model.getSchema(idx).getJpegByte() != null) {
                        pdfMarker.addImage(model.getSchema(idx).getJpegByte());
                    }else{
                        int maxImageWidth = ClientContext.getInt("image.max.width");
                        int maxImageHeight = ClientContext.getInt("image.max.height");
                        Dimension maxSImageSize = new Dimension(maxImageWidth, maxImageHeight);
                        ImageIcon icon = model.getSchema(idx).getIcon();
                        icon = adjustImageSize(icon, maxSImageSize);
                        byte[] jpegByte = getJPEGByte(icon.getImage());
                        pdfMarker.addImage(jpegByte);
                    }
//s.oh$
                }
            // Attachment追加
            } else if (name != null && name.equals(ATTACHMENT_HOLDER)) {
                int idx = Integer.parseInt(number);
                AttachmentModel attachment = (model.getAttachment() != null && model.getAttachment().size() > idx) ? model.getAttachment(idx) : null;
                if(attachment != null) {
                    startContent(null, null, null, null, null, "添付：" + attachment.getTitle() + "(" + attachment.getContentType() + ")");
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
        pdfMarker.initKartePDF();
        createKarteSOAToPDF();
        createKartePlanToPDF();
        path = pdfMarker.create();

        return path;
    }
    
//s.oh^ 2013/02/07 印刷対応
    public void printPDF(ArrayList<String> pdfFileNames) {
        if(pdfFileNames == null || pdfFileNames.size() <= 0) return;
//s.oh^ 2013/06/24 印刷対応
        if(Project.getBoolean(Project.KARTE_PRINT_SHOWPDF)) {
            for(String pdfFileName : pdfFileNames) {
                File file = new File(pdfFileName);
                URI uri = file.toURI();
                try {
                    Desktop.getDesktop().browse(uri);
                } catch (IOException ex) {
                    Logger.getLogger(KartePDFImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
//s.oh$
        }else if(Project.getBoolean(Project.KARTE_PRINT_DIRECT)) {
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
            for(String pdfFileName : pdfFileNames) {
                DocPrintJob job = (defaultService == null) ? null : defaultService.createPrintJob();
                if(job == null) {
                    if(printService.length > 0) {
                        job = printService[0].createPrintJob();
                    }
                }
                if(job == null) continue;
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
                try{
                    Thread.sleep(100);
                }catch(InterruptedException ex) {}
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
                    for(String pdfFileName : pdfFileNames) {
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
                        try{
                            Thread.sleep(100);
                        }catch(InterruptedException ex) {}
                    }
                }
            }
        }
    }
//s.oh$
    
    private ImageIcon adjustImageSize(ImageIcon icon, Dimension dim) {

        if ((icon.getIconHeight() > dim.height) ||
                (icon.getIconWidth() > dim.width)) {
            Image img = icon.getImage();
            float hRatio = (float) icon.getIconHeight() / dim.height;
            float wRatio = (float) icon.getIconWidth() / dim.width;
            int h,w;
            if (hRatio > wRatio) {
                h = dim.height;
                w = (int) (icon.getIconWidth() / hRatio);
            } else {
                w = dim.width;
                h = (int) (icon.getIconHeight() / wRatio);
            }
            img = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } else {
            return icon;
        }
    }
    
    private byte[] getJPEGByte(Image image) {

        byte[] ret = null;

        try {
            Dimension d = new Dimension(image.getWidth(null), image.getHeight(null));
            BufferedImage bf = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
            Graphics g = bf.getGraphics();
            g.setColor(Color.white);
            g.drawImage(image, 0, 0, d.width, d.height, null);

            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ImageIO.write(bf, "jpeg", bo);
            ret = bo.toByteArray();

        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
        return ret;
    }
}
