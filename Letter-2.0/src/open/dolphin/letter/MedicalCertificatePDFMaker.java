package open.dolphin.letter;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import open.dolphin.client.ClientContext;
import open.dolphin.infomodel.LetterModule;
import open.dolphin.infomodel.ModelUtils;
import open.dolphin.infomodel.UserModel;
import open.dolphin.project.Project;

/**
 * 紹介状の PDF メーカー。
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

            document.add(new Paragraph("　"));
            document.add(new Paragraph("　"));

            // 患者氏名
            sb = new StringBuilder();
            sb.append("　氏　名　").append(model.getPatientName());
            para = new Paragraph(sb.toString(), bodyFont);
            para.setAlignment(Element.ALIGN_LEFT);
            document.add(para);

            // 生年月日
            sb = new StringBuilder();
            sb.append("生年月日　").append(getDateString(model.getPatientBirthday()));
            para = new Paragraph(sb.toString(), bodyFont);
            para.setAlignment(Element.ALIGN_LEFT);
            document.add(para);

            // 住所
            sb = new StringBuilder();
            sb.append("　住　所　").append(model.getPatientAddress());
            para = new Paragraph(sb.toString(), bodyFont);
            para.setAlignment(Element.ALIGN_LEFT);
            document.add(para);

            document.add(new Paragraph("　"));

            // 傷病名
            String disease = model.getItemValue(MedicalCertificateImpl.ITEM_DISEASE);
            sb = new StringBuilder();
            sb.append("　傷病名　").append(disease);
            Paragraph para2 = new Paragraph(sb.toString(), bodyFont);
            para2.setAlignment(Element.ALIGN_LEFT);
            document.add(para2);

            document.add(new Paragraph("　"));

            // コンテント
            String informed = model.getTextValue(MedicalCertificateImpl.TEXT_INFORMED_CONTENT);
            para2 = new Paragraph(informed, bodyFont);
            para2.setAlignment(Element.ALIGN_LEFT);
            document.add(para2);

            document.add(new Paragraph("　"));
            document.add(new Paragraph("　"));

            // 日付
            String dateStr = getDateString(model.getConfirmed());
            para = new Paragraph(dateStr, bodyFont);
            para.setAlignment(Element.ALIGN_RIGHT);
            document.add(para);

            // 住所
            UserModel user = Project.getUserModel();
            String zipCode = user.getFacilityModel().getZipCode();
            String address = user.getFacilityModel().getAddress();
            sb = new StringBuilder();
            sb.append(zipCode);
            sb.append(" ");
            sb.append(address);
            para2 = new Paragraph(sb.toString(), bodyFont);
            para2.setAlignment(Element.ALIGN_RIGHT);
            document.add(para2);

            // 病院名
            para2 = new Paragraph(model.getConsultantHospital(), bodyFont);
            para2.setAlignment(Element.ALIGN_RIGHT);
            document.add(para2);

            // 医師
            sb = new StringBuilder();
            sb.append(model.getConsultantDoctor());
            sb.append(" 印");
            para2 = new Paragraph(sb.toString(), bodyFont);
            para2.setAlignment(Element.ALIGN_RIGHT);
            document.add(para2);

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

























