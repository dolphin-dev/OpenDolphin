package open.dolphin.letter;

import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import open.dolphin.client.ClientContext;
import open.dolphin.helper.UserDocumentHelper;
import open.dolphin.infomodel.UserModel;
import open.dolphin.project.Project;

/**
 * 紹介状の PDF メーカー。
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class Reply1PDFMaker extends AbstractLetterPDFMaker {

    private static final String DOC_TITLE = "紹介患者経過報告書";

    @Override
    public String create() {

        try {

            Document document = new Document(
                    PageSize.A4,
                    getMarginLeft(),
                    getMarginRight(),
                    getMarginTop(),
                    getMarginBottom());
            
            String path = UserDocumentHelper.createPathToDocument(
                    getDocumentDir(),       // PDF File を置く場所
                    DOC_TITLE,                // 文書名
                    EXT_PDF,                // 拡張子 
                    model.getPatientName(), // 患者氏名 
                    new Date());            // 日付
            setPathToPDF(path);         // 呼び出し側で取り出せるように保存する
            
            // Open Document
            PdfWriter.getInstance(document, new FileOutputStream(pathToPDF));
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

            // 紹介元病院
            Paragraph para2 = new Paragraph(model.getClientHospital(), bodyFont);
            para2.setAlignment(Element.ALIGN_LEFT);
            document.add(para2);

            // 紹介元診療科
            String dept = model.getClientDept();
            if (dept == null || (dept.equals(""))) {
                para2 = new Paragraph("　", bodyFont);
                para2.setAlignment(Element.ALIGN_LEFT);
                document.add(para2);
            } else {
                if (!dept.endsWith("科")) {
                    dept = dept + "科";
                }
                para2 = new Paragraph(dept, bodyFont);
                para2.setAlignment(Element.ALIGN_LEFT);
                document.add(para2);
            }

            // 紹介元医師
            StringBuilder sb = new StringBuilder();
            if (model.getClientDoctor()!=null) {
                sb.append(model.getClientDoctor());
                sb.append(" ");
            }
            sb.append("先生");
            // title
            String title = Project.getString("letter.atesaki.title");
            if (title!=null && (!title.equals("無し"))) {
                sb.append(title);
            }
            para2 = new Paragraph(sb.toString(), bodyFont);
            para2.setAlignment(Element.ALIGN_LEFT);
            document.add(para2);

//            // 紹介先病院
//            para2 = new Paragraph(model.getConsultantHospital(), bodyFont);
//            para2.setAlignment(Element.ALIGN_RIGHT);
//            document.add(para2);
//            para2 = new Paragraph(model.getConsultantDoctor(), bodyFont);
//            para2.setAlignment(Element.ALIGN_RIGHT);
//            document.add(para2);

            document.add(new Paragraph("　"));

            // 挨拶
            String visitedDate = model.getItemValue(Reply1Impl.ITEM_VISITED_DATE);
            String informed = model.getTextValue(Reply1Impl.TEXT_INFORMED_CONTENT);
            sb = new StringBuilder();
            sb.append("ご紹介いただきました ");
            sb.append(model.getPatientName());
            sb.append(" 殿(生年月日: ");
            sb.append(getDateString(model.getPatientBirthday()));
            sb.append(" ").append(model.getPatientAge()).append("歳").append(")、");
            sb.append(visitedDate);
            sb.append(" 受診され、");
            sb.append("拝見し、下記のとおり説明いたしました。");
            para2 = new Paragraph(sb.toString(), bodyFont);
            para2.setAlignment(Element.ALIGN_LEFT);
            document.add(para2);

            document.add(new Paragraph("　"));

            para2 = new Paragraph(informed, bodyFont);
            para2.setAlignment(Element.ALIGN_LEFT);
            document.add(para2);

            document.add(new Paragraph("　"));
            document.add(new Paragraph("　"));

            sb = new StringBuilder();
            sb.append("ご紹介いただき、ありがとうございました。取り急ぎ返信まで。");
            para2 = new Paragraph(sb.toString(), bodyFont);
            para2.setAlignment(Element.ALIGN_LEFT);
            document.add(para2);

//            sb = new StringBuilder();
//            sb.append("取り急ぎ返信まで。");
//            para2 = new Paragraph(sb.toString(), bodyFont);
//            para2.setAlignment(Element.ALIGN_LEFT);
//            document.add(para2);

            document.add(new Paragraph("　"));

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

            // 電話
            sb = new StringBuilder();
            sb.append("電話　");
            sb.append(user.getFacilityModel().getTelephone());
            para2 = new Paragraph(sb.toString(), bodyFont);
            para2.setAlignment(Element.ALIGN_RIGHT);
            document.add(para2);

            // 差出人病院名
            para2 = new Paragraph(model.getConsultantHospital(), bodyFont);
            para2.setAlignment(Element.ALIGN_RIGHT);
            document.add(para2);

            // 差出人医師
            sb = new StringBuilder();
            sb.append(model.getConsultantDoctor());
            sb.append(" 印");
            para2 = new Paragraph(sb.toString(), bodyFont);
            para2.setAlignment(Element.ALIGN_RIGHT);
            document.add(para2);

            document.close();
            
            return getPathToPDF();

        } catch (IOException ex) {
            ClientContext.getBootLogger().warn(ex);
            throw new RuntimeException(ERROR_IO);
        } catch (DocumentException ex) {
            ClientContext.getBootLogger().warn(ex);
            throw new RuntimeException(ERROR_PDF);
        }
    }
}

























