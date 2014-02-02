package open.dolphin.letter;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import open.dolphin.client.ClientContext;
import open.dolphin.infomodel.LetterModule;
import open.dolphin.infomodel.ModelUtils;

/**
 * 診断書の PDF メーカー。
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class MedicalCertificatePDFMaker {

    private static final String DOC_TITLE = "診 断 書";
    private static final String HEISEI_MIN_W3 = "HeiseiMin-W3";
    private static final String UNIJIS_UCS2_HW_H = "UniJIS-UCS2-HW-H";

    private static final int TOP_MARGIN = 75;
    private static final int LEFT_MARGIN = 75;
    private static final int BOTTOM_MARGIN = 75;
    private static final int RIGHT_MARGIN = 75;

    private static final int TITLE_FONT_SIZE = 18;
    private static final int BODY_FONT_SIZE = 12;

    private static final float CELL_PADDING = 8.0f;

    private String documentDir;
    private String fileName;
    private LetterModule model;
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

            File dir = null;

            if (getDocumentDir()!=null) {
                dir = new File(getDocumentDir());
                dir.mkdir();
            }

            if (dir == null || (!dir.exists())) {
                StringBuilder sb = new StringBuilder();
                sb.append(System.getProperty("user.dir"));
                sb.append(File.separator);
                sb.append("pdf");
                setDocumentDir(sb.toString());
                dir = new File(getDocumentDir());
                dir.mkdir();
            }

            String name = model.getPatientName();
            name = name.replaceAll(" ", "");
            name = name.replaceAll("　", "");
            StringBuilder sb = new StringBuilder();
            sb.append("診断書-");
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

            // Document Open
            PdfWriter.getInstance(document, new FileOutputStream(sb.toString()));
            document.open();

            // Font
            baseFont = BaseFont.createFont(HEISEI_MIN_W3, UNIJIS_UCS2_HW_H, false);
            titleFont = new Font(baseFont, getTitleFontSize());
            bodyFont = new Font(baseFont, getBodyFontSize());

            //----------------------------------------
            // タイトル
            //----------------------------------------
            Paragraph para = new Paragraph(DOC_TITLE, titleFont);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);
            document.add(new Paragraph("　"));
            document.add(new Paragraph("　"));
            document.add(new Paragraph("　"));

            //----------------------------------------
            // 患者情報テーブル
            //----------------------------------------
            PdfPTable pTable = new PdfPTable(new float[]{20.0f, 60.0f, 10.0f, 10.0f});
            pTable.setWidthPercentage(100.0f);

            // 患者氏名
            PdfPCell cell = null;
            pTable.addCell(createNoBorderCell("氏　　名"));
            cell = createNoBorderCell(model.getPatientName());
            cell.setColspan(3);
            pTable.addCell(cell);

            // 生年月日 性別
            pTable.addCell(createNoBorderCell("生年月日"));
            pTable.addCell(createNoBorderCell(getDateString(model.getPatientBirthday())));
            pTable.addCell(createNoBorderCell("性別"));
            pTable.addCell(createNoBorderCell(model.getPatientGender()));

            // 住所
            pTable.addCell(createNoBorderCell("住　　所"));
            cell = createNoBorderCell(model.getPatientAddress());
            cell.setColspan(3);
            pTable.addCell(cell);

            // 傷病名
            String disease = model.getItemValue(MedicalCertificateImpl.ITEM_DISEASE);
            pTable.addCell(createNoBorderCell("傷 病 名"));
            cell = createNoBorderCell(disease);
            cell.setColspan(3);
            pTable.addCell(cell);

            document.add(pTable);
            document.add(new Paragraph("　"));

            //------------------------------------------
            // コンテントテーブル
            //------------------------------------------
            pTable = new PdfPTable(new float[]{1.0f});
            pTable.setWidthPercentage(100.0f);
            String informed = model.getTextValue(MedicalCertificateImpl.TEXT_INFORMED_CONTENT);
            cell = createNoBorderCell(informed);
            cell.setFixedHeight(250.0f);            // Cell 高
            cell.setLeading(0f, 1.5f);              // x 1.5 font height
            pTable.addCell(cell);
            document.add(pTable);
            document.add(new Paragraph("　"));

            //------------------------------------------
            // 署名テーブル
            //------------------------------------------
            // 日付
            pTable = new PdfPTable(new float[]{1.0f});
            pTable.setWidthPercentage(100.0f);

            // 上記の通り診断する
            pTable.addCell(createNoBorderCell("上記の通り診断する。"));
            String dateStr = getDateString(model.getConfirmed());
            pTable.addCell(createNoBorderCell(dateStr));
            
            // 住所 BaseFont.getWidthPoint
            String zipCode = model.getConsultantZipCode();
            String address = model.getConsultantAddress();
//            float zipLen = baseFont.getWidthPoint(zipCode, 12.0f);
//            float addressLen = baseFont.getWidthPoint(address, 12.0f);
//            float padlen = addressLen-zipLen;
//            sb = new StringBuilder();
//            while (true) {
//                sb.append("　");
//                if (baseFont.getWidthPoint(sb.toString(), 12.0f)>=padlen) {
//                    break;
//                }
//            }
//            String space = sb.toString();
            sb = new StringBuilder();
            sb.append(zipCode);
            cell = createNoBorderCell(sb.toString());
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            pTable.addCell(cell);

            sb = new StringBuilder();
            sb.append(address);
            cell = createNoBorderCell(sb.toString());
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            pTable.addCell(cell);

            // 病院名
            cell = createNoBorderCell(model.getConsultantHospital());
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            pTable.addCell(cell);

            // 電話番号
            cell = createNoBorderCell(model.getConsultantTelephone());
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            pTable.addCell(cell);

            // 医師
            sb = new StringBuilder();
            sb.append("医 師　").append(model.getConsultantDoctor()).append(" 印");
            cell = createNoBorderCell(sb.toString());
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            pTable.addCell(cell);
            document.add(pTable);

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

    private PdfPCell createNoBorderCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, bodyFont));
        cell.setBorder(0);
        cell.setPadding(CELL_PADDING);
        return cell;
    }

    private String getDateString(Date d) {
        return ModelUtils.getDateAsFormatString(d, "yyyy年M月d日");
    }

    private String getDateString(String date) {
        Date d = ModelUtils.getDateAsObject(date);
        return ModelUtils.getDateAsFormatString(d, "yyyy年M月d日");
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}






















