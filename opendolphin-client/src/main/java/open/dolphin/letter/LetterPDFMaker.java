package open.dolphin.letter;

import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import open.dolphin.client.ClientContext;
import open.dolphin.helper.UserDocumentHelper;
import open.dolphin.project.Project;

/**
 * 紹介状の PDF メーカー。
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class LetterPDFMaker extends AbstractLetterPDFMaker {

    private static final String DOC_TITLE = "診療情報提供書";
    private static final String GREETINGS = "下記の患者さんを紹介致します。ご高診の程宜しくお願い申し上げます。";

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
                    DOC_TITLE,              // 文書名
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

            // 紹介先病院
            Paragraph para2 = new Paragraph(model.getConsultantHospital(), bodyFont);
            para2.setAlignment(Element.ALIGN_LEFT);
            document.add(para2);

            // 紹介先診療科
            para2 = new Paragraph(model.getConsultantDept(), bodyFont);
            para2.setAlignment(Element.ALIGN_LEFT);
            document.add(para2);

            // 紹介先医師
            StringBuilder sb = new StringBuilder();
            if (model.getConsultantDoctor()!= null) {
                sb.append(model.getConsultantDoctor());
                sb.append(" ");
            }
            sb.append("先生　");
            // title
            String title = Project.getString("letter.atesaki.title");
            if (title!=null && (!title.equals("無し"))) {
                sb.append(title);
            }
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
            sb.append(model.getClientDoctor());
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
            if (Project.getBoolean("letter.greetings.include")) {
                para2 = new Paragraph(GREETINGS, bodyFont);
                para2.setAlignment(Element.ALIGN_CENTER);
                document.add(para2);
            }

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
            String disease = model.getItemValue(LetterImpl.ITEM_DISEASE);
            String purpose = model.getItemValue(LetterImpl.ITEM_PURPOSE);
            String pastFamily = model.getTextValue(LetterImpl.TEXT_PAST_FAMILY);
            String clinicalCourse = model.getTextValue(LetterImpl.TEXT_CLINICAL_COURSE);
            String medication = model.getTextValue(LetterImpl.TEXT_MEDICATION);
            String remarks = model.getItemValue(LetterImpl.ITEM_REMARKS);

            Table lTable = new Table(2); //テーブル・オブジェクトの生成
            lTable.setPadding(2);
            width = new int[]{20, 80};
            lTable.setWidths(width); //各カラムの大きさを設定（パーセント）
            lTable.setWidth(100);

            lTable.addCell(new Phrase("傷病名", bodyFont));
            lTable.addCell(new Phrase(disease, bodyFont));

            lTable.addCell(new Phrase("紹介目的", bodyFont));
            lTable.addCell(new Phrase(purpose, bodyFont));

            sb = new StringBuilder();
            sb.append("既往歴").append("\n").append("家族歴");
            lTable.addCell(new Phrase(sb.toString(), bodyFont));
            cell = new Cell(new Phrase(pastFamily, bodyFont));
            lTable.addCell(cell);

            sb = new StringBuilder();
            sb.append("症状経過").append("\n").append("検査結果").append("\n").append("治療経過");
            lTable.addCell(new Phrase(sb.toString(), bodyFont));
            lTable.addCell(new Phrase(clinicalCourse, bodyFont));

            lTable.addCell(new Phrase("現在の処方", bodyFont));
            lTable.addCell(new Phrase(medication, bodyFont));

            lTable.addCell(new Phrase("備 考", bodyFont));
            lTable.addCell(new Phrase(remarks, bodyFont));

            document.add(lTable);

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

























