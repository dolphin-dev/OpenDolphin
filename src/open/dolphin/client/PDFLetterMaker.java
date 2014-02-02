package open.dolphin.client;

import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import open.dolphin.infomodel.LetterModel;
import open.dolphin.infomodel.ModelUtils;
import open.dolphin.infomodel.TouTouLetter;

/**
 * 紹介状の PDF メーカー。
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class PDFLetterMaker {
    
    private static final String DOC_TITLE = "診療情報提供書";
    private static final String GREETINGS = "下記の患者さんを紹介致します。ご高診の程宜しくお願い申し上げます。";
    private static final String HEISEI_MIN_W3 = "HeiseiMin-W3";
    private static final String UNIJIS_UCS2_HW_H = "UniJIS-UCS2-HW-H";
    
    private static final int TOP_MARGIN = 75;
    private static final int LEFT_MARGIN = 75;
    private static final int BOTTOM_MARGIN = 75;
    private static final int RIGHT_MARGIN = 75;
    
    private static final int TITLE_FONT_SIZE = 14;
    private static final int BODY_FONT_SIZE = 12;
    
    private String documentDir;
    private String fileName;
    private TouTouLetter model;
    private int marginLeft = LEFT_MARGIN;
    private int marginRight = RIGHT_MARGIN;
    private int marginTop = TOP_MARGIN;
    private int marginBottom = BOTTOM_MARGIN;
    
    private BaseFont baseFont;
    private Font titleFont;
    private Font bodyFont;
    private int titleFontSize = TITLE_FONT_SIZE;
    private int bodyFontSize = BODY_FONT_SIZE;
    
    
    public boolean create() {
        
        boolean result = true;
        
        try {

            Document document = new Document(
                    PageSize.A4,
                    getMarginLeft(),
                    getMarginRight(),
                    getMarginTop(),
                    getMarginBottom());
            
            if (documentDir == null) {
                StringBuilder sb = new StringBuilder();
                sb.append(System.getProperty("user.dir"));
                sb.append(File.separator);
                sb.append("pdf");
                setDocumentDir(sb.toString());
            }
            File dir = new File(getDocumentDir());
            dir.mkdir();
            
            String name = model.getPatientName();
            name = name.replaceAll(" ", "");
            name = name.replaceAll("　", "");
            StringBuilder sb = new StringBuilder();
            sb.append("紹介状-");
            sb.append(name);
            sb.append("様-");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sb.append(sdf.format(new Date()));
            sb.append(".pdf");
            setFileName(sb.toString());
            
            sb = new StringBuilder();
            if (getDocumentDir() != null) {
                sb.append(getDocumentDir());
                sb.append(File.separator);
            }
            sb.append(getFileName());
            
            PdfWriter.getInstance(document, new FileOutputStream(sb.toString()));
            document.open();
            
            // Font
            baseFont = BaseFont.createFont(HEISEI_MIN_W3, UNIJIS_UCS2_HW_H, false);
            titleFont = new Font(baseFont, getTitleFontSize());
            bodyFont = new Font(baseFont, getBodyFontSize());
            
            // タイトル
            Paragraph para = new Paragraph(DOC_TITLE, titleFont);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);
            
            // 日付
            String dateStr = getDateString(model.getConfirmed());
            para = new Paragraph(dateStr, bodyFont);
            para.setAlignment(Element.ALIGN_RIGHT);
            document.add(para);
            
            document.add(new Paragraph("　"));
            
            // 紹介先病院
            Paragraph para2 = new Paragraph(model.getConsultantHospital(), bodyFont);
            para2.setAlignment(Element.ALIGN_LEFT);
            document.add(para2);
            
            // 紹介先診療科
            para2 = new Paragraph(model.getConsultantDept(), bodyFont);
            para2.setAlignment(Element.ALIGN_LEFT);
            document.add(para2);
            
            // 紹介先医師
            sb = new StringBuilder();
            sb.append(model.getConsultantDoctor());
            sb.append(" 先生");
            para2 = new Paragraph(sb.toString(), bodyFont);
            para2.setAlignment(Element.ALIGN_LEFT);
            document.add(para2);
            
            // 紹介元病院
            para2 = new Paragraph(model.getClientHospital(), bodyFont);
            para2.setAlignment(Element.ALIGN_RIGHT);
            document.add(para2);
            
//            // 紹介元診療科
//            para2 = new Paragraph(model.getCl, bodyFont);
//            para2.setAlignment(Element.ALIGN_RIGHT);
//            document.add(para2);
            
            // 紹介元医師
            sb = new StringBuilder();
            sb.append(model.getClientName());
            sb.append(" 印");
            para2 = new Paragraph(sb.toString(), bodyFont);
            para2.setAlignment(Element.ALIGN_RIGHT);
            document.add(para2);
            
            // 紹介元住所
            para2 = new Paragraph(model.getClientAddress(), bodyFont);
            para2.setAlignment(Element.ALIGN_RIGHT);
            document.add(para2);
            
            // 紹介元電話番号
            sb = new StringBuilder();
            sb.append("電話 ");
            sb.append(model.getClientTelephone());
            para2 = new Paragraph(sb.toString(), bodyFont);
            para2.setAlignment(Element.ALIGN_RIGHT);
            document.add(para2);
            
            document.add(new Paragraph("　"));
            
            // 紹介挨拶
            para2 = new Paragraph(GREETINGS, bodyFont);
            para2.setAlignment(Element.ALIGN_CENTER);
            document.add(para2);
            
            // 患者
            Table pTable = new Table(4);
            pTable.setPadding(2);
            int width[] = new int[]{20, 60, 10, 10};
            pTable.setWidths(width);
            pTable.setWidth(100);     
            
            String birthday = getDateString(model.getPatientBirthday());
            String sexStr = getSexString(model.getPatientGender());
            pTable.addCell(new Phrase("患者氏名", bodyFont));
            pTable.addCell(new Phrase(model.getPatientName(), bodyFont));
            pTable.addCell(new Phrase("性別", bodyFont));
            pTable.addCell(new Phrase(sexStr, bodyFont));
            pTable.addCell(new Phrase("生年月日", bodyFont));
            sb = new StringBuilder();
            sb.append(birthday);
            sb.append(" (");
            sb.append(model.getPatientAge());
            sb.append(" 歳)");
            Cell cell = new Cell(new Phrase(sb.toString(), bodyFont));
            cell.setColspan(3);
            pTable.addCell(cell);
            document.add(pTable);
            
            // 紹介状内容
            Table lTable = new Table(2); //テーブル・オブジェクトの生成
            lTable.setPadding(2);
            width = new int[]{20, 80};
            lTable.setWidths(width); //各カラムの大きさを設定（パーセント）
            lTable.setWidth(100);
            
            lTable.addCell(new Phrase("傷病名", bodyFont));
            lTable.addCell(new Phrase(model.getDisease(), bodyFont));
            
            lTable.addCell(new Phrase("紹介目的", bodyFont));
            lTable.addCell(new Phrase(model.getPurpose(), bodyFont));
            
            lTable.addCell(new Phrase("既往歴\n家族歴", bodyFont));
            cell = new Cell(new Phrase(model.getPastFamily(), bodyFont));
            lTable.addCell(cell);
            
            lTable.addCell(new Phrase("症状経過\n検査結果\n治療経過", bodyFont));
            lTable.addCell(new Phrase(model.getClinicalCourse(), bodyFont));
            
            lTable.addCell(new Phrase("現在の処方", bodyFont));
            lTable.addCell(new Phrase(model.getMedication(), bodyFont));
            
            lTable.addCell(new Phrase("備 考", bodyFont));
            lTable.addCell(new Phrase(model.getRemarks(), bodyFont));
            
            document.add(lTable);
            
            document.close();
            
        } catch (IOException ex) {
            ClientContext.getBootLogger().warn(ex);
            result = false;
        } catch (DocumentException ex) {
            ClientContext.getBootLogger().warn(ex);
            result = false;
        }
        
        return result;
    }
    
    private String getDateString(Date d) {
        return ModelUtils.getDateAsFormatString(d, "yyyy年M月d日");      
    }
    
    private String getDateString(String date) {
        Date d = ModelUtils.getDateAsObject(date);
        return ModelUtils.getDateAsFormatString(d, "yyyy年M月d日");
    }
    
    private String getSexString(String sex) {
        //return ModelUtils.getGenderDesc(sex);
        return sex;
    }

    public LetterModel getModel() {
        return model;
    }

    public void setModel(TouTouLetter model) {
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}

























