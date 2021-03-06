package open.dolphin.letter;

import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import open.dolphin.client.ClientContext;
import open.dolphin.helper.UserDocumentHelper;
import open.dolphin.project.Project;

/**
 * 診断書の PDF メーカー。
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class MedicalCertificatePDFMaker extends AbstractLetterPDFMaker {

    //private static final String DOC_TITLE = "診 断 書";

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
                    ClientContext.getMyBundle(MedicalCertificatePDFMaker.class).getString("title.medicalCertificate"),                // 文書名
                    EXT_PDF,                // 拡張子 
                    model.getPatientName(), // 患者氏名 
                    new Date());            // 日付 
            
            Path pathObj = Paths.get(path);
            setPathToPDF(pathObj.toAbsolutePath().toString());         // 呼び出し側で取り出せるように保存する             
            // Open Document
            ByteArrayOutputStream byteo = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, byteo);
            document.open();
            
            // Font
            baseFont = BaseFont.createFont(HEISEI_MIN_W3, UNIJIS_UCS2_HW_H, false);
            if (Project.getString(Project.SHINDANSYO_FONT_SIZE).equals("small")) {
                titleFont = new Font(baseFont, getTitleFontSize());
                bodyFont = new Font(baseFont, getBodyFontSize());
            } else {
                titleFont = new Font(baseFont, 18);
                bodyFont = new Font(baseFont, 14);
            }

            //----------------------------------------
            // タイトル
            //----------------------------------------
            Paragraph para = new Paragraph(ClientContext.getMyBundle(MedicalCertificatePDFMaker.class).getString("paragrap.medicalCertificate"), titleFont);
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
            PdfPCell cell;
            pTable.addCell(createNoBorderCell(ClientContext.getMyBundle(MedicalCertificatePDFMaker.class).getString("cell.name")));
            cell = createNoBorderCell(model.getPatientName());
            cell.setColspan(3);
            pTable.addCell(cell);

            // 生年月日 性別
            pTable.addCell(createNoBorderCell(ClientContext.getMyBundle(MedicalCertificatePDFMaker.class).getString("cell.birthdate")));
            pTable.addCell(createNoBorderCell(getDateString(model.getPatientBirthday())));
            pTable.addCell(createNoBorderCell(ClientContext.getMyBundle(MedicalCertificatePDFMaker.class).getString("cell.gender")));
            pTable.addCell(createNoBorderCell(model.getPatientGender()));

            // 住所
            pTable.addCell(createNoBorderCell(ClientContext.getMyBundle(MedicalCertificatePDFMaker.class).getString("cell.address")));
            cell = createNoBorderCell(model.getPatientAddress());
            cell.setColspan(3);
            pTable.addCell(cell);

            // 傷病名
            String disease = model.getItemValue(MedicalCertificateImpl.ITEM_DISEASE);
            pTable.addCell(createNoBorderCell(ClientContext.getMyBundle(MedicalCertificatePDFMaker.class).getString("cell.dicease")));
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
            if (Project.getString("sindansyo.font.size").equals("small")) {
                cell.setFixedHeight(250.0f);            // Cell 高
            } else {
                cell.setFixedHeight(225.0f);            // Cell 高
            }   
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
            pTable.addCell(createNoBorderCell(ClientContext.getMyBundle(MedicalCertificatePDFMaker.class).getString("cell.certificate")));
            String dateStr = getDateString(model.getStarted());  
            pTable.addCell(createNoBorderCell(dateStr));
            
            // 住所 BaseFont.getWidthPoint
            String zipCode = model.getConsultantZipCode();
            String address = model.getConsultantAddress();

            StringBuilder sb = new StringBuilder();
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
            sb.append(ClientContext.getMyBundle(MedicalCertificatePDFMaker.class).getString("cell.doctor")).append(" ").append(model.getConsultantDoctor()).append("").append(ClientContext.getMyBundle(MedicalCertificatePDFMaker.class).getString("cell.seal"));
            cell = createNoBorderCell(sb.toString());
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            pTable.addCell(cell);
            document.add(pTable);

            document.close();
            
            // // pdf content bytes
            byte[] pdfbytes = byteo.toByteArray();
            
            // 評価でない場合は Fileへ書き込んでリターン
            //if (!ClientContext.is5mTest()) {  
            if (!Project.isTester()) {      
                Files.write(pathObj, pdfbytes);            
                return getPathToPDF();
            }
            
            // 評価の場合は water Mark を書く
            PdfReader pdfReader = new PdfReader(pdfbytes);
            PdfStamper pdfStamper = new PdfStamper(pdfReader,Files.newOutputStream(pathObj));
            Image image = Image.getInstance(ClientContext.getImageResource("water-mark.png"));

            for(int i=1; i<= pdfReader.getNumberOfPages(); i++){

                PdfContentByte content = pdfStamper.getUnderContent(i);
                image.scaleAbsolute(PageSize.A4.getWidth(), PageSize.A4.getHeight());
                image.setAbsolutePosition(0.0f, 0.0f);

                content.addImage(image);
            }

            pdfStamper.close();
            
            return getPathToPDF();

        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(this.getClass().getName()).warning(ex.getMessage());
            throw new RuntimeException(ERROR_IO);
        } catch (DocumentException ex) {
            java.util.logging.Logger.getLogger(this.getClass().getName()).warning(ex.getMessage());
            throw new RuntimeException(ERROR_PDF);
        }
    }
}