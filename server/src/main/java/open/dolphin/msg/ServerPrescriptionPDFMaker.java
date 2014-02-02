package open.dolphin.msg;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import open.dolphin.infomodel.BundleMed;
import open.dolphin.infomodel.ClaimConst;
import open.dolphin.infomodel.ClaimItem;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.ModelUtils;
import open.dolphin.infomodel.PVTPublicInsuranceItemModel;
import open.dolphin.infomodel.PriscriptionModel;

/**
 * 処方せんPDFを生成するクラス
 * 
 * @author Masato 新宿ヒロクリニック OpenDolphinToGo 
 * modified minagawa 2012-09-07 OpenDolphin/Pro へポート
 * 主な変更点
 *   処方リスト、適用保険、患者、医師、保険医療機関コードを外部から設定-> サーバー側でも利用
 *   公費２番目の配列へデータをセット
 *   output で生成したPDFへのパスをリターン
 *   日付、数値フォーマッターをキャッシュしない
 *   MML の classCodeSystem を無視
 */
public final class ServerPrescriptionPDFMaker {

    /** フォント-平成角ゴシック */
    private static final String FONT_HEISEI_KAKU5 = "HeiseiKakuGo-W5";
    
    /** フォント-平成明朝 */
    private static final String FONT_HEISEI_MIN3 = "HeiseiMin-W3";
    
    /** コード-UniJIS プロポーショナルフォント用 */
    private static final String CODE_UNIJIS_H = "UniJIS-UCS2-H";
    
    /** コード-UniJIS */
    private static final String CODE_UNIJIS_HWH = "UniJIS-UCS2-HW-H";
    
    /** ファイル名リテラル */
    private static final String FILE_NAME_PRE = "処方せん-";
    
    /** ファイル拡張子 */
    private static final String FILE_EXTENTION = ".pdf";
    
    /** タイトル */
    private static final String REPORT_TITLE = "処　　方　　せ　　ん";
    
    /** サブタイトル */
    private static final String REPORT_SUB_TITLE = "(この処方せんは、どの保険薬局でも有効です。)";
    
    /** ファイルプロパティ */
    private static final String PROPERTY_TITLE = "処方せん";
    
    /** ファイルプロパティサブタイトル */
    private static final String PROPERTY_SUB_TITLE = "";
    
    /** 備考欄に記載する文字列「在宅」 */
    private static final String NOTES_HOME_MEDICAL = "(在宅)";
    
    /** 改行を表すリテラル */
    private String STR_LF = "line.separator";
    
    /** 保存ディレクトリ名称 */
    private static final String DIR_NAME = "welcome-content";
    
    /** ファイル名称 */
    private String fileName;
    
    // Path to the created PDF
    private String pathToPDF;  
    
    // ベースフォント(ゴシック)
    BaseFont bfg;
    
    // ベースフォント(明朝)
    BaseFont bfm;
    
    // 明朝体 サイズ [6, 7, 8, 9, 10, 12, 14, 15]
    Font min_6, min_7, min_8, min_9, min_10, min_12, min_14, min_15;
    
    Font min_4; // @009

    // 線太さ
    private static final float LINE_WIDTH_0 = 0f;
    private static final float LINE_WIDTH_1 = 0.2f;
    private static final float LINE_WIDTH_2 = 1f;
    private static final float LINE_WIDTH_3 = 0.5f;
    // 高さ
    private static final float CELL_HIGHT_0 = 17f;
    private static final float CELL_HIGHT_1 = 12f;
    private static final float CELL_HIGHT_2 = 34f; // @010
    // @008 2010/06/18 処方せん2010年4月改定対応の為
    /** セル余白0：0f */
    private static final float CELL_PADDING_0 = 0f; // @008
    /** セル余白1：2.5f */
    private static final float CELL_PADDING_1 = 2.5f; // @008

    private boolean DEBUG;
    
     /** ドキュメント保存ディレクトリ */
    private String documentDir;
    
    /** 処方パッケージ */
    private PriscriptionModel pkg;
    
    /**
     * インスタンスを生成し、ブートロガー及びドキュメントモデルをフィールドに設定します。
     */
    public ServerPrescriptionPDFMaker() {
    }
    
    public ServerPrescriptionPDFMaker(PriscriptionModel pkg) {
        this();
        this.pkg = pkg;
    }

    /**
     * 処方せんを出力する。
     */
    public String output() {
        BufferedOutputStream bos;
        PdfWriter pw = null;
        Document document = null;

        try {    
            Date dateNow = new Date();
            
            // 患者ID
            String patientId = pkg.getPatientId();
            
            // 患者氏名
            String name = pkg.getPatientName();
            name = name.replaceAll(" ", "");
            name = name.replaceAll("　", "");
            
            String iNum;                    // 保険者番号
            String piNum = null;            // 公費負担者番号
            String rNum = null;             // 受給者番号
            
            String piNum2 = null;           // 公費負担者番号２
            String rNum2 = null;            // 受給者番号２
        
            String div = "";                // 本人家族区分
            String payRatio = "";           // 負担割合
            
            String mNum = "";               // 被保険者記号・番号
            
            char[] iNumC = new char[8];     // 保険者番号配列
            char[] piNumC = new char[8];    // 公費負担者番号配列
            char[] rNumC = new char[7];     // 受給者番号
            char[] piNumC2 = new char[8];   // 公費負担者番号２配列
            char[] rNumC2 = new char[7];    // 受給者番号２
            DecimalFormat df = new DecimalFormat("#0.#"); // 割合表示のフォーマット
            String paymentRatio = "";       // 公費割合
            String paymentRatio2 = "";      // 公費２割合

            if (pkg.getApplyedInsurance().getInsuranceNumber()!= null) {
                
                // 保険者番号
                iNum = pkg.getApplyedInsurance().getInsuranceNumber();
                
                // 自費は null にする
                if (iNum.toLowerCase().startsWith("z") ||
                        iNum.equals("9999")) {
                    iNum = null;
                }
                
                // 公費
                if (pkg.getApplyedInsurance().getPVTPublicInsuranceItem()!=null) {
                    PVTPublicInsuranceItemModel[] pubItems = pkg.getApplyedInsurance().getPVTPublicInsuranceItem();
                    for (int i = 0; i <pubItems.length; i++) {
                        PVTPublicInsuranceItemModel pm = pubItems[i];
                        if (i==0) {
                            // 負担者番号
                            piNum = pm.getProvider();
                            piNum = ("mikinyu".equals(piNum)) ? "" : piNum;
                            
                            // 受給者番号
                            rNum = pm.getRecipient();
                            rNum = ("mikinyu".equals(rNum)) ? "" : rNum;
                            
                            // 負担率または負担金
                            paymentRatio = pm.getPaymentRatio();
                        } else if (i==1) {
                            piNum2 = pm.getProvider();
                            piNum2 = ("mikinyu".equals(piNum2)) ? "" : piNum2;
                            
                            rNum2 = pm.getRecipient();
                            rNum2 = ("mikinyu".equals(rNum2)) ? "" : rNum2;
                            
                            paymentRatio2 = pm.getPaymentRatio();
                            break;
                        }
                    }
                }
                
                // 被保険者 記号・番号 文字列を構築する
                StringBuilder sb = new StringBuilder();
                
                // 被保険者 記号
                if (pkg.getApplyedInsurance().getClientGroup()!=null && !pkg.getApplyedInsurance().getClientGroup().equals("記載なし")) {
                    sb.append(pkg.getApplyedInsurance().getClientGroup()).append("・");
                }
                // 被保険者番号
                if (pkg.getApplyedInsurance().getClientNumber()!=null && !pkg.getApplyedInsurance().getClientNumber().equals("記載なし")) {
                    sb.append(pkg.getApplyedInsurance().getClientNumber());
                }
                mNum = sb.length()>0 ? sb.toString(): "";
                
                // 負担率
                if ("公費単独".equals(pkg.getApplyedInsurance().getInsuranceClass())) {
                    div = "";
                    payRatio = paymentRatio;
                } else {
                    // 本人家族区分
                    div = "true".equals(pkg.getApplyedInsurance().getFamilyClass()) ? "被保険者" : "被扶養者";
                    payRatio = pkg.getApplyedInsurance().getPayOutRatio();
                }
                if (payRatio != null && !("".equals(payRatio))) {
                    payRatio = df.format(Double.valueOf(payRatio) * 10);
                }
                
                if (DEBUG) {
                    System.err.println("iNum="+iNum);
                    System.err.println("piNum="+piNum);
                    System.err.println("rNum="+rNum);
                    System.err.println("piNum2="+piNum2);
                    System.err.println("rNum2="+rNum2);
                    System.err.println("mNum="+mNum);
                    System.err.println("本人家族区分="+div);
                    System.err.println("負担率="+payRatio);
                }
                
                // 配列へ分解する
                iNumC = partitionPadRL(iNum, 8, "R");       // 保険者番号
                piNumC = partitionPadRL(piNum, 8, "L");     // 公費負担者番号
                rNumC = partitionPadRL(rNum, 7, "L");       // 受給者番号
                piNumC2 = partitionPadRL(piNum2, 8, "L");   // 公費負担者番号2
                rNumC2 = partitionPadRL(rNum2, 7, "L");     // 受給者番号2
            }
            /***** ↑患者情報↑ *****/

            document = new Document(PageSize.A5, 10, 10, 2, 2);
            
            // 処方せんPDF保存ディレクトリwelcome-content
            if (getDocumentDir() == null) {
                StringBuilder sb = new StringBuilder();
                sb.append(System.getProperty("jboss.home.dir"));
                sb.append(File.separator);
                sb.append(DIR_NAME);
                setDocumentDir(sb.toString());
            }
            File dir = new File(getDocumentDir());
            dir.mkdir();
            
            // ファイル名(患者ID_日付.pdf)
            StringBuilder sb = new StringBuilder();
            sb.append(patientId).append("_");
            sb.append(new SimpleDateFormat("yyyyMMddHHmmss").format(dateNow));
            sb.append(FILE_EXTENTION);
            setFileName(sb.toString());

            sb = new StringBuilder();
            sb.append(getDocumentDir());
            sb.append(File.separator);
            sb.append(getFileName());
            pathToPDF = sb.toString();

//minagawa^ 評価の場合、water markを書く必要があるので、まず byte[]へ書き込む            
            ByteArrayOutputStream byteo = new ByteArrayOutputStream();
            bos = new BufferedOutputStream(byteo);
//minagawa$             
            pw = PdfWriter.getInstance(document, bos);

            // font setting
            bfm = BaseFont.createFont(FONT_HEISEI_MIN3, CODE_UNIJIS_H, BaseFont.NOT_EMBEDDED);
            bfg = BaseFont.createFont(FONT_HEISEI_KAKU5, CODE_UNIJIS_H, BaseFont.NOT_EMBEDDED);
            min_6 = new Font(bfm, 6);
            min_7 = new Font(bfm, 7);
            min_8 = new Font(bfm, 8);
            min_9 = new Font(bfm, 9);
            min_10 = new Font(bfm, 10);
            min_12 = new Font(bfm, 12);
            min_14 = new Font(bfm, 14);
            min_15 = new Font(bfm, 15);
            min_4 = new Font(bfm, 4); // @009

            // ドキュメントプロパティ設定
            document.open();
            document.addAuthor(pkg.getPhysicianName());
            document.addTitle(PROPERTY_TITLE);
            document.addSubject(PROPERTY_SUB_TITLE);

            // 処方せん情報のテーブルを生成
            List<PdfPTable> list = createPrescriptionTbl2();
            Iterator<PdfPTable> ite = list.iterator();
            
            // 頁数表示追加
            int pageNo = 0;
            int totalPageNo = list.size();
 
            // 処方がなかった場合は空の処方せんを出力
            do {
                PdfPTable ptbl = new PdfPTable(1);
                ptbl.setWidthPercentage(100f);
                ptbl.getDefaultCell().setPadding(0f);
                PdfPCell pcell = new PdfPCell(new Paragraph(REPORT_TITLE, min_15));
                pcell.setBorder(Table.NO_BORDER);
                setAlignCenter(pcell);
                ptbl.addCell(pcell);
                pcell = new PdfPCell(new Paragraph(REPORT_SUB_TITLE, min_7));
                pcell.setBorder(Table.NO_BORDER);
                setAlignCenter(pcell);
                ptbl.addCell(pcell);
                document.add(ptbl);

                ptbl = new PdfPTable(3);
                ptbl.setWidthPercentage(100f);
                ptbl.getDefaultCell().setPadding(0f);
                ptbl.getDefaultCell().setBorder(Table.NO_BORDER);
                float[] widths = {43.5f, 2f, 54.5f};
                ptbl.setWidths(widths);
                // 患者番号
                pcell = new PdfPCell(new Paragraph(patientId, min_9));
                pcell.setBorder(Table.NO_BORDER);
                pcell.setColspan(10);
                ptbl.addCell(pcell);

                PdfPTable ptblL = new PdfPTable(9);
                ptblL.setSpacingBefore(10f);
                ptblL.setWidthPercentage(100f);
                float[] widthsL = {33, 8, 8, 8, 8, 8, 8, 8, 8};
                ptblL.setWidths(widthsL);
                ptblL.getDefaultCell().setPadding(0f);
                pcell = new PdfPCell(new Paragraph("公費負担者番号", min_7));
                pcell.setMinimumHeight(CELL_HIGHT_0);
                setAlignJustifiedAll(pcell);
                setAlignMiddle(pcell);
                pcell.setBorderWidth(LINE_WIDTH_1);
                ptblL.addCell(pcell);
                pcell = new PdfPCell(new Paragraph(String.valueOf(piNumC[0]), min_14));
                pcell.setBorderWidth(LINE_WIDTH_1);
                pcell.setPadding(0f);
                setAlignCenterMiddle(pcell);
                ptblL.addCell(pcell);
                pcell = new PdfPCell(new Paragraph(String.valueOf(piNumC[1]), min_14));
                pcell.setBorderWidth(LINE_WIDTH_1);
                pcell.setBorderWidthRight(LINE_WIDTH_3);
                pcell.setPadding(0f);
                setAlignCenterMiddle(pcell);
                ptblL.addCell(pcell);
                pcell = new PdfPCell(new Paragraph(String.valueOf(piNumC[2]), min_14));
                pcell.setBorderWidth(LINE_WIDTH_1);
                pcell.setBorderWidthLeft(LINE_WIDTH_3);
                pcell.setPadding(0f);
                setAlignCenterMiddle(pcell);
                ptblL.addCell(pcell);
                pcell = new PdfPCell(new Paragraph(String.valueOf(piNumC[3]), min_14));
                pcell.setBorderWidth(LINE_WIDTH_1);
                pcell.setBorderWidthRight(LINE_WIDTH_3);
                pcell.setPadding(0f);
                setAlignCenterMiddle(pcell);
                ptblL.addCell(pcell);
                pcell = new PdfPCell(new Paragraph(String.valueOf(piNumC[4]), min_14));
                pcell.setBorderWidth(LINE_WIDTH_2);
                pcell.setBorderWidthRight(LINE_WIDTH_1);
                pcell.setBorderWidthLeft(LINE_WIDTH_3);
                pcell.setPadding(0f);
                setAlignCenterMiddle(pcell);
                ptblL.addCell(pcell);
                pcell = new PdfPCell(new Paragraph(String.valueOf(piNumC[5]), min_14));
                pcell.setBorderWidth(LINE_WIDTH_1);
                pcell.setBorderWidthTop(LINE_WIDTH_2);
                pcell.setBorderWidthBottom(LINE_WIDTH_2);
                pcell.setPadding(0f);
                setAlignCenterMiddle(pcell);
                ptblL.addCell(pcell);
                pcell = new PdfPCell(new Paragraph(String.valueOf(piNumC[6]), min_14));
                pcell.setBorderWidth(LINE_WIDTH_2);
                pcell.setBorderWidthRight(LINE_WIDTH_3);
                pcell.setBorderWidthLeft(LINE_WIDTH_1);
                pcell.setPadding(0f);
                setAlignCenterMiddle(pcell);
                ptblL.addCell(pcell);
                pcell = new PdfPCell(new Paragraph(String.valueOf(piNumC[7]), min_14));
                pcell.setBorderWidth(LINE_WIDTH_1);
                pcell.setBorderWidthLeft(LINE_WIDTH_3);
                pcell.setPadding(0f);
                setAlignCenterMiddle(pcell);
                ptblL.addCell(pcell);
                pcell = new PdfPCell(new Paragraph("公費負担医療の受給者番号", min_7));
                pcell.setPaddingTop(0.3f);
                setAlignJustifiedAll(pcell);
                pcell.setBorderWidth(LINE_WIDTH_1);
                ptblL.addCell(pcell);
                pcell = new PdfPCell(new Paragraph(String.valueOf(rNumC[0]), min_14));
                pcell.setBorderWidth(LINE_WIDTH_1);
                pcell.setPadding(0f);
                setAlignCenterMiddle(pcell);
                ptblL.addCell(pcell);
                pcell = new PdfPCell(new Paragraph(String.valueOf(rNumC[1]), min_14));
                pcell.setBorderWidth(LINE_WIDTH_1);
                pcell.setPadding(0f);
                setAlignCenterMiddle(pcell);
                ptblL.addCell(pcell);
                pcell = new PdfPCell(new Paragraph(String.valueOf(rNumC[2]), min_14));
                pcell.setBorderWidth(LINE_WIDTH_1);
                pcell.setBorderWidthRight(LINE_WIDTH_3);
                pcell.setPadding(0f);
                setAlignCenterMiddle(pcell);
                ptblL.addCell(pcell);
                pcell = new PdfPCell(new Paragraph(String.valueOf(rNumC[3]), min_14));
                pcell.setBorderWidth(LINE_WIDTH_1);
                pcell.setBorderWidthLeft(LINE_WIDTH_3);
                pcell.setPadding(0f);
                setAlignCenterMiddle(pcell);
                ptblL.addCell(pcell);
                pcell = new PdfPCell(new Paragraph(String.valueOf(rNumC[4]), min_14));
                pcell.setBorderWidth(LINE_WIDTH_1);
                pcell.setPadding(0f);
                setAlignCenterMiddle(pcell);
                ptblL.addCell(pcell);
                pcell = new PdfPCell(new Paragraph(String.valueOf(rNumC[5]), min_14));
                pcell.setBorderWidth(LINE_WIDTH_1);
                pcell.setBorderWidthRight(LINE_WIDTH_3);
                pcell.setPadding(0f);
                setAlignCenterMiddle(pcell);
                ptblL.addCell(pcell);
                pcell = new PdfPCell(new Paragraph(String.valueOf(rNumC[6]), min_14));
                pcell.setBorderWidth(LINE_WIDTH_1);
                pcell.setBorderWidthLeft(LINE_WIDTH_3);
                pcell.setPadding(0f);
                setAlignCenterMiddle(pcell);
                ptblL.addCell(pcell);
                pcell = new PdfPCell();
                pcell.setBorderWidth(LINE_WIDTH_0);
                ptblL.addCell(pcell);

                PdfPTable patientTbl = new PdfPTable(2);
                patientTbl.setWidthPercentage(100f);
                float[] widthsPa = {7.8f, 92.2f};
                patientTbl.setWidths(widthsPa);
                patientTbl.getDefaultCell().setPadding(0f);
                patientTbl.getDefaultCell().setBorder(Table.NO_BORDER);
                PdfPCell pcellP = new PdfPCell(new Paragraph("患　　　者", min_7));
                pcellP.setBorderWidth(LINE_WIDTH_1);
                setAlignCenterMiddle(pcellP);
                patientTbl.addCell(pcellP);
                // 患者詳細情報
                PdfPTable desc = new PdfPTable(5);
                desc.setWidthPercentage(100f);
                float[] widthsD = {28.5f, 41.5f, 7, 16, 7};
                desc.setWidths(widthsD);
                // 氏名(フリガナ、フルネーム)
                PdfPCell patientInfo = new PdfPCell(new Paragraph("氏名", min_7));
                patientInfo.setBorderWidth(LINE_WIDTH_1);
                setAlignJustifiedAll(patientInfo);
                setAlignMiddle(patientInfo);
                desc.addCell(patientInfo);
                PdfPTable nameTbl = new PdfPTable(1);
                nameTbl.setWidthPercentage(100f);
                nameTbl.setSpacingAfter(3f);
                PdfPCell nameCell = new PdfPCell(new Paragraph(pkg.getPatientKana(), min_7));
                nameCell.setBorderWidth(LINE_WIDTH_0);
                nameTbl.addCell(nameCell);
                nameCell = new PdfPCell(new Paragraph(pkg.getPatientName(), min_9));
                nameCell.setBorderWidth(LINE_WIDTH_0);
                nameTbl.addCell(nameCell);
                patientInfo = new PdfPCell(nameTbl);
                patientInfo.setColspan(4);
                patientInfo.setBorderWidth(LINE_WIDTH_1);
                desc.addCell(patientInfo);
                // 生年月日
                patientInfo = new PdfPCell(new Paragraph("生年月日", min_7));
                patientInfo.setBorderWidth(LINE_WIDTH_1);
                setAlignJustifiedAll(patientInfo);
                desc.addCell(patientInfo);
                String birthDay = ModelUtils.convertToGengo(pkg.getPatientBirthday());
                patientInfo = new PdfPCell(new Paragraph(birthDay, min_9));
                patientInfo.setBorderWidth(LINE_WIDTH_1);
                patientInfo.setColspan(3);
                patientInfo.setPaddingTop(0.5f);
                setAlignMiddle(patientInfo);
                desc.addCell(patientInfo);
                patientInfo = new PdfPCell(new Paragraph(pkg.getPatientSex(), min_8));
                patientInfo.setBorderWidth(LINE_WIDTH_1);
                setAlignCenter(patientInfo);
                desc.addCell(patientInfo);
                // 区分
                patientInfo = new PdfPCell(new Paragraph("区分", min_7));
                patientInfo.setBorderWidth(LINE_WIDTH_1);
                setAlignJustifiedAll(patientInfo);
                setAlignMiddle(patientInfo);
                desc.addCell(patientInfo);
                patientInfo = new PdfPCell(new Paragraph(div, min_8));
                patientInfo.setBorderWidth(LINE_WIDTH_1);
                setAlignMiddle(patientInfo);
                desc.addCell(patientInfo);
                patientInfo = new PdfPCell(new Paragraph("割合", min_7));
                patientInfo.setBorderWidth(LINE_WIDTH_1);
                setAlignMiddle(patientInfo);
                desc.addCell(patientInfo);
                patientInfo = new PdfPCell(new Paragraph(payRatio, min_9));
                setAlignRightMiddle(patientInfo);
                patientInfo.setBorderWidth(LINE_WIDTH_0);
                desc.addCell(patientInfo);
                patientInfo = new PdfPCell(new Paragraph("割", min_7));
                patientInfo.setBorderWidth(LINE_WIDTH_0);
                patientInfo.setVerticalAlignment(Element.ALIGN_BOTTOM);
                setAlignRight(patientInfo);
                desc.addCell(patientInfo);
                patientTbl.addCell(desc);
                pcell = new PdfPCell(patientTbl);
                pcell.setColspan(9);
                pcell.setBorderWidth(LINE_WIDTH_1);
                ptblL.addCell(pcell);
                // 交付年月日
                pcell = new PdfPCell(new Paragraph("交付年月日", min_7));
                pcell.setBorderWidth(LINE_WIDTH_1);
                setAlignJustifiedAll(pcell);
                setAlignMiddle(pcell);
                ptblL.addCell(pcell);
                // @003 2010/02/15 仕様変更：交付年月日はカルテを作成した日でないといけないので、対応
                String issueDate = ModelUtils.convertToGengo(ModelUtils.getDateAsFormatString(pkg.getIssuanceDate(), IInfoModel.DATE_WITHOUT_TIME));
                pcell = new PdfPCell(new Paragraph(issueDate, min_9));
                pcell.setBorderWidth(LINE_WIDTH_1);
                pcell.setPaddingTop(0.5f);
                setAlignMiddle(pcell);
                pcell.setColspan(8);
                ptblL.addCell(pcell);

                ptbl.addCell(ptblL);
                ptbl.addCell("");

                PdfPTable ptblR = new PdfPTable(10);
                ptblR.setSpacingBefore(10f);
                ptblR.setWidthPercentage(100f);
                float[] widthsR = {30, 7, 7, 7, 7, 7, 7, 7, 7, 14};
                ptblR.setWidths(widthsR);
                pcell = new PdfPCell(new Paragraph("保険者番号", min_7));
                pcell.setMinimumHeight(CELL_HIGHT_0);
                pcell.setBorderWidth(LINE_WIDTH_1);
                setAlignJustifiedAll(pcell);
                setAlignMiddle(pcell);
                ptblR.addCell(pcell);
                pcell = new PdfPCell(new Paragraph(String.valueOf(iNumC[0]), min_14));
                pcell.setBorderWidth(LINE_WIDTH_1);
                pcell.setPadding(CELL_PADDING_0);
                setAlignCenterMiddle(pcell);
                ptblR.addCell(pcell);
                pcell = new PdfPCell(new Paragraph(String.valueOf(iNumC[1]), min_14));
                pcell.setBorderWidth(LINE_WIDTH_1);
                pcell.setBorderWidthRight(LINE_WIDTH_2);
                pcell.setPadding(CELL_PADDING_0);
                setAlignCenterMiddle(pcell);
                ptblR.addCell(pcell);
                pcell = new PdfPCell(new Paragraph(String.valueOf(iNumC[2]), min_14));
                pcell.setBorderWidth(LINE_WIDTH_1);
                pcell.setPadding(CELL_PADDING_0);
                setAlignCenterMiddle(pcell);
                ptblR.addCell(pcell);
                pcell = new PdfPCell(new Paragraph(String.valueOf(iNumC[3]), min_14));
                pcell.setBorderWidth(LINE_WIDTH_1);
                pcell.setBorderWidthRight(LINE_WIDTH_2);
                pcell.setPadding(CELL_PADDING_0);
                setAlignCenterMiddle(pcell);
                ptblR.addCell(pcell);
                pcell = new PdfPCell(new Paragraph(String.valueOf(iNumC[4]), min_14));
                pcell.setBorderWidth(LINE_WIDTH_1);
                pcell.setBorderWidthTop(LINE_WIDTH_2);
                pcell.setBorderWidthBottom(LINE_WIDTH_2);
                pcell.setPadding(CELL_PADDING_0);
                setAlignCenterMiddle(pcell);
                ptblR.addCell(pcell);
                pcell = new PdfPCell(new Paragraph(String.valueOf(iNumC[5]), min_14));
                pcell.setBorderWidth(LINE_WIDTH_1);
                pcell.setBorderWidthTop(LINE_WIDTH_2);
                pcell.setBorderWidthBottom(LINE_WIDTH_2);
                pcell.setPadding(CELL_PADDING_0);
                setAlignCenterMiddle(pcell);
                ptblR.addCell(pcell);
                pcell = new PdfPCell(new Paragraph(String.valueOf(iNumC[6]), min_14));
                pcell.setBorderWidth(LINE_WIDTH_2);
                pcell.setBorderWidthLeft(LINE_WIDTH_1);
                pcell.setPadding(CELL_PADDING_0);
                setAlignCenterMiddle(pcell);
                ptblR.addCell(pcell);
                pcell = new PdfPCell(new Paragraph(String.valueOf(iNumC[7]), min_14));
                pcell.setBorderWidth(LINE_WIDTH_1);
                pcell.setPadding(CELL_PADDING_0);
                setAlignCenterMiddle(pcell);
                ptblR.addCell(pcell);
                pcell = new PdfPCell();
                pcell.setBorderWidth(LINE_WIDTH_0);
                ptblR.addCell(pcell);
                pcell = new PdfPCell(new Paragraph("被保険者証･被保険者手帳の記号･番号", min_7));
                pcell.setPaddingTop(0.3f);
                pcell.setBorderWidth(LINE_WIDTH_1);
                setAlignJustifiedAll(pcell);
                setAlignMiddle(pcell);
                ptblR.addCell(pcell);
                pcell = new PdfPCell(new Paragraph(mNum, min_9));
                pcell.setBorderWidth(LINE_WIDTH_1);
                pcell.setColspan(9);
                setAlignMiddle(pcell);
                ptblR.addCell(pcell);

                //FacilityModel facility = getPhysician().getFacilityModel();
                String facilityName = pkg.getInstitutionName(); // 医療機関名
                //String facilityZipCode = facility.getZipCode(); // 郵便番号
                String facilityAddress = pkg.getInstitutionAddress(); // 住所
                String facilityTelNo = pkg.getInstitutionTelephone(); // 電話番号
//minagawa^ 診察カルテはないので                 
                String drName = pkg.getPhysicianName();
//minagawa$                    
                if (pkg.isChkUseDrugInfo()) {
                    // 麻薬施用者記載の場合
                    drName = pkg.getPhysicianName();
                }
            // ********** @008 2010/06/18 ↓↓ **********
                // 2010年4月診療報酬改定による対応
                String prefNo = "  ";           // 都道府県番号 2桁
                String grade = " ";             // 点数表番号 1桁
                String institution = "       "; // 医療機関コード 7桁

                if ((pkg.getInstitutionNumber() != null) && (pkg.getInstitutionNumber().length() > 9)) {
                    prefNo = pkg.getInstitutionNumber().substring(0, 2);
                    grade = pkg.getInstitutionNumber().substring(2, 3);
                    institution = pkg.getInstitutionNumber().substring(3, 10);
                }
            // ********** @008 2010/06/18 ↑↑ **********

                PdfPTable medOrgTbl = new PdfPTable(3);
                medOrgTbl.setWidthPercentage(100f);
                float[] widthsM = {30, 55, 15};
                medOrgTbl.setWidths(widthsM);
                PdfPCell medOrgCell = new PdfPCell(new Paragraph("保険医療機関の\n所在地", min_7));
                medOrgCell.setBorderWidth(LINE_WIDTH_0);
                setAlignJustifiedAll(medOrgCell);
                medOrgTbl.addCell(medOrgCell);
                medOrgCell = new PdfPCell(new Paragraph(facilityAddress, min_8));
                medOrgCell.setBorderWidth(LINE_WIDTH_0);
                medOrgCell.setColspan(2);
                setAlignMiddle(medOrgCell);
                medOrgTbl.addCell(medOrgCell);
                medOrgCell = new PdfPCell(new Paragraph("及び名称", min_7));
                medOrgCell.setBorderWidth(LINE_WIDTH_0);
                setAlignJustifiedAll(medOrgCell);
                medOrgCell.setPaddingTop(CELL_PADDING_0); // @008
                medOrgCell.setPaddingBottom(CELL_PADDING_0); // @008
                medOrgTbl.addCell(medOrgCell);
                medOrgCell = new PdfPCell(new Paragraph(facilityName, min_8));
                medOrgCell.setBorderWidth(LINE_WIDTH_0);
                medOrgCell.setColspan(2);
                medOrgCell.setPaddingTop(CELL_PADDING_0); // @008
                medOrgCell.setPaddingBottom(CELL_PADDING_0); // @008
                medOrgTbl.addCell(medOrgCell);
                medOrgCell = new PdfPCell();
                medOrgCell.setBorder(Table.NO_BORDER);
                medOrgCell.setColspan(3);
                medOrgCell.setPaddingTop(CELL_PADDING_0); // @008
                medOrgCell.setPaddingBottom(CELL_PADDING_0); // @008
                medOrgTbl.addCell(medOrgCell);
                medOrgCell = new PdfPCell(new Paragraph("電話番号", min_7));
                medOrgCell.setBorderWidth(LINE_WIDTH_0);
                setAlignJustifiedAll(medOrgCell);
                setAlignMiddle(medOrgCell);
                medOrgCell.setPaddingTop(CELL_PADDING_0); // @008
                medOrgTbl.addCell(medOrgCell);
                medOrgCell = new PdfPCell(new Paragraph(facilityTelNo, min_9));
                medOrgCell.setBorderWidth(LINE_WIDTH_0);
                setAlignMiddle(medOrgCell);
                medOrgCell.setColspan(2);
                medOrgCell.setPaddingTop(CELL_PADDING_0); // @008
                medOrgTbl.addCell(medOrgCell);
                medOrgCell = new PdfPCell(new Paragraph("保険医氏名", min_7));
                medOrgCell.setBorderWidth(LINE_WIDTH_0);
                setAlignJustifiedAll(medOrgCell);
                setAlignMiddle(medOrgCell);
                medOrgCell.setPaddingTop(CELL_PADDING_0); // @008
                medOrgTbl.addCell(medOrgCell);
                medOrgCell = new PdfPCell(new Paragraph(drName, min_10));
                medOrgCell.setBorderWidth(LINE_WIDTH_0);
                setAlignMiddle(medOrgCell);
                medOrgCell.setPaddingTop(CELL_PADDING_0); // @008
                medOrgTbl.addCell(medOrgCell);
                medOrgCell = new PdfPCell(new Paragraph("印", min_8));
                medOrgCell.setBorderWidth(LINE_WIDTH_0);
                setAlignMiddle(medOrgCell);
                medOrgCell.setPaddingTop(CELL_PADDING_0); // @008
                medOrgTbl.addCell(medOrgCell);
                pcell = new PdfPCell(medOrgTbl);
                pcell.setBorder(Table.NO_BORDER);
                pcell.setColspan(10);
                pcell.setPaddingBottom(CELL_PADDING_1);
                ptblR.addCell(pcell);
            // ********** @008 2010/06/18 ↓↓ **********
                // 2010年4月診療報酬改定対応による 仕様変更
                PdfPTable medCodeTbl = new PdfPTable(13);
                medCodeTbl.setWidthPercentage(100f);
                float[] widthsCode = {17, 8, 8, 15, 8, 17, 8, 8, 8, 8, 8, 8, 8};
                medCodeTbl.setWidths(widthsCode);
                // 都道府県番号
                PdfPCell medCodeCell = new PdfPCell(new Paragraph("都道府県\n番号", min_6));
                medCodeCell.setBorderWidth(LINE_WIDTH_1);
                setAlignCenterMiddle(medCodeCell);
                medCodeCell.setPaddingTop(CELL_PADDING_0);
                medCodeTbl.addCell(medCodeCell);
                medCodeCell = new PdfPCell(new Paragraph(String.valueOf(prefNo.charAt(0)), min_14));
                medCodeCell.setBorderWidth(LINE_WIDTH_1);
                setAlignCenterMiddle(medCodeCell);
                medCodeCell.setPaddingTop(CELL_PADDING_0);
                medCodeTbl.addCell(medCodeCell);
                medCodeCell = new PdfPCell(new Paragraph(String.valueOf(prefNo.charAt(1)), min_14));
                medCodeCell.setBorderWidth(LINE_WIDTH_1);
                setAlignCenterMiddle(medCodeCell);
                medCodeCell.setPaddingTop(CELL_PADDING_0);
                medCodeTbl.addCell(medCodeCell);
                medCodeCell = new PdfPCell(new Paragraph("点数表\n番号", min_6));
                medCodeCell.setBorderWidth(LINE_WIDTH_1);
                setAlignCenterMiddle(medCodeCell);
                medCodeCell.setPaddingTop(CELL_PADDING_0);
                medCodeTbl.addCell(medCodeCell);
                medCodeCell = new PdfPCell(new Paragraph(String.valueOf(grade.charAt(0)), min_14));
                medCodeCell.setBorderWidth(LINE_WIDTH_1);
                setAlignCenterMiddle(medCodeCell);
                medCodeCell.setPaddingTop(CELL_PADDING_0);
                medCodeTbl.addCell(medCodeCell);
                medCodeCell = new PdfPCell(new Paragraph("医療機関コード", min_6));
                medCodeCell.setBorderWidth(LINE_WIDTH_1);
                setAlignCenterMiddle(medCodeCell);
                medCodeCell.setPaddingTop(CELL_PADDING_0);
                medCodeTbl.addCell(medCodeCell);
                medCodeCell = new PdfPCell(new Paragraph(String.valueOf(institution.charAt(0)), min_14));
                medCodeCell.setBorderWidth(LINE_WIDTH_1);
                setAlignCenterMiddle(medCodeCell);
                medCodeCell.setPaddingTop(CELL_PADDING_0);
                medCodeTbl.addCell(medCodeCell);
                medCodeCell = new PdfPCell(new Paragraph(String.valueOf(institution.charAt(1)), min_14));
                medCodeCell.setBorderWidth(LINE_WIDTH_1);
                setAlignCenterMiddle(medCodeCell);
                medCodeCell.setPaddingTop(CELL_PADDING_0);
                medCodeTbl.addCell(medCodeCell);
                medCodeCell = new PdfPCell(new Paragraph(String.valueOf(institution.charAt(2)), min_14));
                medCodeCell.setBorderWidth(LINE_WIDTH_1);
                setAlignCenterMiddle(medCodeCell);
                medCodeCell.setPaddingTop(CELL_PADDING_0);
                medCodeTbl.addCell(medCodeCell);
                medCodeCell = new PdfPCell(new Paragraph(String.valueOf(institution.charAt(3)), min_14));
                medCodeCell.setBorderWidth(LINE_WIDTH_1);
                setAlignCenterMiddle(medCodeCell);
                medCodeCell.setPaddingTop(CELL_PADDING_0);
                medCodeTbl.addCell(medCodeCell);
                medCodeCell = new PdfPCell(new Paragraph(String.valueOf(institution.charAt(4)), min_14));
                medCodeCell.setBorderWidth(LINE_WIDTH_1);
                setAlignCenterMiddle(medCodeCell);
                medCodeCell.setPaddingTop(CELL_PADDING_0);
                medCodeTbl.addCell(medCodeCell);
                medCodeCell = new PdfPCell(new Paragraph(String.valueOf(institution.charAt(5)), min_14));
                medCodeCell.setBorderWidth(LINE_WIDTH_1);
                setAlignCenterMiddle(medCodeCell);
                medCodeCell.setPaddingTop(CELL_PADDING_0);
                medCodeTbl.addCell(medCodeCell);
                medCodeCell = new PdfPCell(new Paragraph(String.valueOf(institution.charAt(6)), min_14));
                medCodeCell.setBorderWidth(LINE_WIDTH_1);
                setAlignCenterMiddle(medCodeCell);
                medCodeCell.setPaddingTop(CELL_PADDING_0);
                medCodeTbl.addCell(medCodeCell);
                pcell = new PdfPCell(medCodeTbl);
                pcell.setBorder(Table.NO_BORDER);
                pcell.setColspan(10);
                pcell.setPaddingBottom(CELL_PADDING_1);
                ptblR.addCell(pcell);
            // ********** @008 2010/06/18 ↑↑ **********

                ptbl.addCell(ptblR);

                // 処方せんの使用期間
                PdfPTable termTbl = new PdfPTable(3);
                termTbl.setWidthPercentage(100f);
                float[] widthsT = {14.8f, 26, 59.2f};
                termTbl.setWidths(widthsT);
                termTbl.getDefaultCell().setPadding(0f);
                PdfPCell termCell = new PdfPCell(new Paragraph("処方せんの\n使用期間", min_7));
                termCell.setBorderWidth(LINE_WIDTH_1);
                termCell.setPaddingTop(0.3f);
                setAlignJustifiedAll(termCell);
                termTbl.addCell(termCell);
            // ********* @009 2010/07/01 ↓↓ *********
                String periodDate = "平成　　年　　月　　日";
                if (pkg.getPeriod() != null) {
                    periodDate = ModelUtils.convertToGengo(ModelUtils.getDateAsFormatString(pkg.getPeriod(), IInfoModel.DATE_WITHOUT_TIME));
                }
                termCell = new PdfPCell(new Paragraph(periodDate, min_8));
            // ********* @009 2010/07/01 ↑↑ *********
                termCell.setBorderWidth(LINE_WIDTH_1);
                termCell.setBorderWidthRight(LINE_WIDTH_0);
                setAlignMiddle(termCell);
                termTbl.addCell(termCell);
                termCell = new PdfPCell(new Paragraph("特に記載のある場合を除き、交付の日を含めて４日以内に保険薬局に提出すること。", min_6));
                termCell.setBorderWidth(LINE_WIDTH_1);
                termCell.setBorderWidthLeft(LINE_WIDTH_0);
                setAlignMiddle(termCell);
                termTbl.addCell(termCell);
                pcell = new PdfPCell(termTbl);
                pcell.setBorder(Table.NO_BORDER);
                pcell.setColspan(3);
                ptbl.addCell(pcell);

                document.add(ptbl);

                // 処方
                ptbl = new PdfPTable(2);
                ptbl.setWidthPercentage(100f);
                ptbl.getDefaultCell().setPadding(0f);
                ptbl.getDefaultCell().setBorder(Table.NO_BORDER);
                float[] widthsPre = {3.5f, 96.5f};
                ptbl.setWidths(widthsPre);
                pcell = new PdfPCell(new Paragraph("処　　　　　　　　　　　　　　　　　　方", min_7));
                pcell.setBorderWidth(LINE_WIDTH_1);
                setAlignCenterMiddle(pcell);
                ptbl.addCell(pcell);
                // @005 2010/02/26 追加 ↓↓
                // 頁数表示追加による修正
                // 処方詳細大枠テーブル
                PdfPTable outLineTbl = new PdfPTable(1);
                PdfPCell outLineCell; // 処方詳細大枠内のセル
                // @005 2010/02/26 追加 ↑↑
                // 処方詳細
                PdfPTable prescriptionTbl; // 処方詳細テーブル
                if (ite.hasNext()) {
                    prescriptionTbl = ite.next();
                } else {
                    prescriptionTbl = new PdfPTable(1);
                }
                // @005 2010/02/26 追加 ↓↓
                // 頁数表示追加による修正
                outLineCell = new PdfPCell(prescriptionTbl);
                outLineCell.setFixedHeight(200f);
                outLineCell.setBorderWidth(LINE_WIDTH_0);
                outLineTbl.addCell(outLineCell);
                if (totalPageNo > 1) {
                    pageNo++;
                    outLineCell = new PdfPCell(new Paragraph((String.valueOf(pageNo) + "／" + String.valueOf(totalPageNo)), min_10));
                    setAlignRight(outLineCell);
                    outLineCell.setFixedHeight(12f); // @010
                    outLineCell.setBorderWidth(LINE_WIDTH_1); // @010
                    outLineTbl.addCell(outLineCell);
                }
                // @005 2010/02/26 追加 ↑↑
                PdfPCell prescriptionCell = new PdfPCell(outLineTbl);
                prescriptionCell.setFixedHeight(215f);
                prescriptionCell.setBorderWidth(LINE_WIDTH_1);
                ptbl.addCell(prescriptionCell);

                // 備考
                pcell = new PdfPCell(new Paragraph("備　　　　　　考", min_7));
                pcell.setBorderWidth(LINE_WIDTH_1);
                setAlignCenterMiddle(pcell);
                ptbl.addCell(pcell);
                // 備考詳細
                PdfPTable noteTbl = new PdfPTable(5); // @010
                noteTbl.setWidthPercentage(100f);
                float[] widthsN = {11, 4, 34, 4, 47}; // @010
                noteTbl.setWidths(widthsN);
                noteTbl.getDefaultCell().setPadding(0f);
                noteTbl.getDefaultCell().setBorder(Table.NO_BORDER);
                String address = (pkg.getPatientAddress() == null) ? "" : pkg.getPatientAddress();
                String patientName = pkg.getPatientName();
                String addressName = "住所：" + address + "\n氏名：" + patientName;
                String useDrugInfo = "麻薬施用者免許証番号：" + pkg.getDrugLicenseNumber() + "(" + pkg.getPhysicianName() + ")";

                StringBuilder postInfo = new StringBuilder();
                // 備考欄転記情報
                if (pkg.isChkHomeMedical()) {
                    postInfo.append(NOTES_HOME_MEDICAL + "\n");
                }
                if (pkg.isChkPatientInfo()) {
                    // 患者住所、氏名
                    postInfo.append(addressName);
                }
                if (postInfo.length() > 0) {
                    // 改行コード追加
                    postInfo.append("\n");
                }
                if (pkg.isChkUseDrugInfo()) {
                    // 麻薬施用者免許証番号
                    postInfo.append(useDrugInfo);
                }
                // @010 2012年4月診療報酬改定対応 -->
                PdfPCell noteCell = new PdfPCell(new Paragraph("保険医署名", min_7));
                noteCell.setBorderWidth(LINE_WIDTH_0);
                noteCell.setBorderWidthBottom(LINE_WIDTH_1);
                noteCell.setMinimumHeight(CELL_HIGHT_2);
                setAlignTop(noteCell);
                noteTbl.addCell(noteCell);

                noteCell = new PdfPCell(new Paragraph("〔", min_15));//min_15
                noteCell.setBorderWidth(LINE_WIDTH_0);
                noteCell.setBorderWidthBottom(LINE_WIDTH_1);
                noteCell.setPadding(0f);
                setAlignRight(noteCell);
                noteTbl.addCell(noteCell);

                noteCell = new PdfPCell(new Paragraph("「変更不可」欄に「レ」又は「×」を記載した\n場合は、署名又は記名・押印すること。", min_6));
                noteCell.setBorderWidth(LINE_WIDTH_0);
                noteCell.setBorderWidthBottom(LINE_WIDTH_1);
                noteTbl.addCell(noteCell);

                noteCell = new PdfPCell(new Paragraph("〕", min_15));//min_15
                noteCell.setBorderWidth(LINE_WIDTH_0);
                noteCell.setBorderWidthBottom(LINE_WIDTH_1);
                noteCell.setBorderWidthRight(LINE_WIDTH_1);
                noteCell.setPadding(0f);
                setAlignLeft(noteCell);
                noteTbl.addCell(noteCell);

//minagawa^ ここは後の欄  47                   
                noteCell = new PdfPCell();
                noteCell.setBorderWidth(LINE_WIDTH_0);
                noteTbl.addCell(noteCell);
//minagawa                    
                noteCell = new PdfPCell(new Paragraph(postInfo.toString(), min_7)); // 住所、氏名などの情報
                noteCell.setColspan(widthsN.length);
                noteCell.setMinimumHeight(40f);
                noteCell.setBorderWidth(LINE_WIDTH_0);
                noteTbl.addCell(noteCell);
                // <-- 2012年4月診療報酬改定対応 @010

                pcell = new PdfPCell(noteTbl);
                pcell.setBorderWidth(LINE_WIDTH_1);
                ptbl.addCell(pcell);

                document.add(ptbl);

                // 以降、薬局関係情報
                ptbl = new PdfPTable(2);
                ptbl.setWidthPercentage(100f);
                float[] widthsOther = {58, 42};
                ptbl.setWidths(widthsOther);
                ptbl.getDefaultCell().setPadding(0f);
                ptbl.getDefaultCell().setBorder(Table.NO_BORDER);
                // 調剤
                ptblL = new PdfPTable(3);
                ptblL.setWidthPercentage(100f);
                float[] widthsPh = {28, 65, 7};
                ptblL.setWidths(widthsPh);
                ptblL.getDefaultCell().setPadding(0f);
                ptblL.getDefaultCell().setBorder(Table.NO_BORDER);
                PdfPCell pcellL = new PdfPCell(new Paragraph("調剤済月日", min_7));
                pcellL.setMinimumHeight(CELL_HIGHT_0);
                pcellL.setBorderWidth(LINE_WIDTH_1);
                setAlignJustifiedAll(pcellL);
                setAlignMiddle(pcellL);
                ptblL.addCell(pcellL);
                pcellL = new PdfPCell(new Paragraph("平成　　年　　月　　日", min_8));
                pcellL.setBorderWidth(LINE_WIDTH_1);
                setAlignMiddle(pcellL);
                pcellL.setColspan(2);
                ptblL.addCell(pcellL);
                pcellL = new PdfPCell(new Paragraph("保険薬局の\n所在地及び\n名称", min_7));
                pcellL.setPaddingTop(0.2f);
                pcellL.setBorderWidth(LINE_WIDTH_1);
                pcellL.setBorderWidthBottom(LINE_WIDTH_0);
                setAlignJustifiedAll(pcellL);
                ptblL.addCell(pcellL);
                pcellL = new PdfPCell();
                pcellL.setBorderWidth(LINE_WIDTH_0);
                pcellL.setBorderWidthRight(LINE_WIDTH_1);
                pcellL.setColspan(2);
                ptblL.addCell(pcellL);
                pcellL = new PdfPCell(new Paragraph("保険薬剤師\n氏名", min_7));
                pcellL.setPaddingTop(0.2f);
                pcellL.setBorderWidth(LINE_WIDTH_1);
                pcellL.setBorderWidthTop(LINE_WIDTH_0);
                setAlignJustifiedAll(pcellL);
                ptblL.addCell(pcellL);
                pcellL = new PdfPCell();
                pcellL.setBorderWidth(LINE_WIDTH_0);
                pcellL.setBorderWidthBottom(LINE_WIDTH_1);
                ptblL.addCell(pcellL);
                pcellL = new PdfPCell(new Paragraph("印", min_8));
                pcellL.setBorderWidth(LINE_WIDTH_1);
                pcellL.setBorderWidthTop(LINE_WIDTH_0);
                pcellL.setBorderWidthLeft(LINE_WIDTH_0);
                setAlignJustifiedAll(pcellL);
                setAlignMiddle(pcellL);
                ptblL.addCell(pcellL);
                ptbl.addCell(ptblL);

                ptblR = new PdfPTable(9);
                ptblR.setWidthPercentage(100f);
                float[] widthsPu = {33, 8, 8, 8, 8, 8, 8, 8, 8};
                ptblR.setWidths(widthsPu);
                ptblR.getDefaultCell().setPadding(0f);
                PdfPCell pcellR = new PdfPCell(new Paragraph("公費負担者番号", min_7));
                pcellR.setMinimumHeight(CELL_HIGHT_0);
                setAlignJustifiedAll(pcellR);
                setAlignMiddle(pcellR);
                pcellR.setBorderWidth(LINE_WIDTH_1);
                ptblR.addCell(pcellR);
                pcellR = new PdfPCell(new Paragraph(String.valueOf(piNumC2[0]), min_14)); // @006
                pcellR.setBorderWidth(LINE_WIDTH_1);
                pcellR.setPadding(0f);
                setAlignCenterMiddle(pcellR);
                ptblR.addCell(pcellR);
                pcellR = new PdfPCell(new Paragraph(String.valueOf(piNumC2[1]), min_14)); // @006
                pcellR.setBorderWidth(LINE_WIDTH_1);
                pcellR.setBorderWidthRight(LINE_WIDTH_3);
                pcellR.setPadding(0f);
                setAlignCenterMiddle(pcellR);
                ptblR.addCell(pcellR);
                pcellR = new PdfPCell(new Paragraph(String.valueOf(piNumC2[2]), min_14)); // @006
                pcellR.setBorderWidth(LINE_WIDTH_1);
                pcellR.setBorderWidthLeft(LINE_WIDTH_3);
                pcellR.setPadding(0f);
                setAlignCenterMiddle(pcellR);
                ptblR.addCell(pcellR);
                pcellR = new PdfPCell(new Paragraph(String.valueOf(piNumC2[3]), min_14)); // @006
                pcellR.setBorderWidth(LINE_WIDTH_1);
                pcellR.setBorderWidthRight(LINE_WIDTH_3);
                pcellR.setPadding(0f);
                setAlignCenterMiddle(pcellR);
                ptblR.addCell(pcellR);
                pcellR = new PdfPCell(new Paragraph(String.valueOf(piNumC2[4]), min_14)); // @006
                pcellR.setBorderWidth(LINE_WIDTH_2);
                pcellR.setBorderWidthRight(LINE_WIDTH_1);
                pcellR.setBorderWidthLeft(LINE_WIDTH_3);
                pcellR.setPadding(0f);
                setAlignCenterMiddle(pcellR);
                ptblR.addCell(pcellR);
                pcellR = new PdfPCell(new Paragraph(String.valueOf(piNumC2[5]), min_14)); // @006
                pcellR.setBorderWidth(LINE_WIDTH_1);
                pcellR.setBorderWidthTop(LINE_WIDTH_2);
                pcellR.setBorderWidthBottom(LINE_WIDTH_2);
                pcellR.setPadding(0f);
                setAlignCenterMiddle(pcellR);
                ptblR.addCell(pcellR);
                pcellR = new PdfPCell(new Paragraph(String.valueOf(piNumC2[6]), min_14)); // @006
                pcellR.setBorderWidth(LINE_WIDTH_2);
                pcellR.setBorderWidthRight(LINE_WIDTH_3);
                pcellR.setBorderWidthLeft(LINE_WIDTH_1);
                pcellR.setPadding(0f);
                setAlignCenterMiddle(pcellR);
                ptblR.addCell(pcellR);
                pcellR = new PdfPCell(new Paragraph(String.valueOf(piNumC2[7]), min_14)); // @006
                pcellR.setBorderWidth(LINE_WIDTH_1);
                pcellR.setBorderWidthLeft(LINE_WIDTH_3);
                pcellR.setPadding(0f);
                setAlignCenterMiddle(pcellR);
                ptblR.addCell(pcellR);
                pcellR = new PdfPCell(new Paragraph("公費負担医療の受給者番号", min_7));
                pcellR.setPaddingTop(0.3f);
                setAlignJustifiedAll(pcellR);
                pcellR.setBorderWidth(LINE_WIDTH_1);
                ptblR.addCell(pcellR);
                pcellR = new PdfPCell(new Paragraph(String.valueOf(rNumC2[0]), min_14)); // @006
                pcellR.setBorderWidth(LINE_WIDTH_1);
                pcellR.setPadding(0f);
                setAlignCenterMiddle(pcellR);
                ptblR.addCell(pcellR);
                pcellR = new PdfPCell(new Paragraph(String.valueOf(rNumC2[1]), min_14)); // @006
                pcellR.setBorderWidth(LINE_WIDTH_1);
                pcellR.setPadding(0f);
                setAlignCenterMiddle(pcellR);
                ptblR.addCell(pcellR);
                pcellR = new PdfPCell(new Paragraph(String.valueOf(rNumC2[2]), min_14)); // @006
                pcellR.setBorderWidth(LINE_WIDTH_1);
                pcellR.setBorderWidthRight(LINE_WIDTH_3);
                pcellR.setPadding(0f);
                setAlignCenterMiddle(pcellR);
                ptblR.addCell(pcellR);
                pcellR = new PdfPCell(new Paragraph(String.valueOf(rNumC2[3]), min_14)); // @006
                pcellR.setBorderWidth(LINE_WIDTH_1);
                pcellR.setBorderWidthLeft(LINE_WIDTH_3);
                pcellR.setPadding(0f);
                setAlignCenterMiddle(pcellR);
                ptblR.addCell(pcellR);
                pcellR = new PdfPCell(new Paragraph(String.valueOf(rNumC2[4]), min_14)); // @006
                pcellR.setBorderWidth(LINE_WIDTH_1);
                pcellR.setPadding(0f);
                setAlignCenterMiddle(pcellR);
                ptblR.addCell(pcellR);
                pcellR = new PdfPCell(new Paragraph(String.valueOf(rNumC2[5]), min_14)); // @006
                pcellR.setBorderWidth(LINE_WIDTH_1);
                pcellR.setBorderWidthRight(LINE_WIDTH_3);
                pcellR.setPadding(0f);
                setAlignCenterMiddle(pcellR);
                ptblR.addCell(pcellR);
                pcellR = new PdfPCell(new Paragraph(String.valueOf(rNumC2[6]), min_14)); // @006
                pcellR.setBorderWidth(LINE_WIDTH_1);
                pcellR.setBorderWidthLeft(LINE_WIDTH_3);
                pcellR.setPadding(0f);
                setAlignCenterMiddle(pcellR);
                ptblR.addCell(pcellR);
                pcellR = new PdfPCell();
                pcellR.setBorderWidth(LINE_WIDTH_0);
                ptblR.addCell(pcellR);
                pcellR = new PdfPCell();
                pcellR.setBorderWidth(LINE_WIDTH_0);
                pcellR.setColspan(9);
                ptblR.addCell(pcellR);

                ptbl.addCell(ptblR);

                document.add(ptbl);

                ptbl = new PdfPTable(2);
                ptbl.setWidthPercentage(100f);
                float[] widthsMed = {3.5f, 96.5f};
                ptbl.setWidths(widthsMed);
                ptbl.setSpacingBefore(3f);
                ptbl.getDefaultCell().setPadding(0f);
                ptbl.getDefaultCell().setBorder(Table.NO_BORDER);
                pcell = new PdfPCell(new Paragraph("薬局で記載のこと", min_7));
                pcell.setBorderWidth(LINE_WIDTH_1);
                setAlignCenter(pcell);
                ptbl.addCell(pcell);

                ptblR = new PdfPTable(3);
                ptblR.setWidthPercentage(100f);
                float[] widthsPm = {60, 20, 20};
                ptblR.setWidths(widthsPm);
                ptblR.getDefaultCell().setPadding(0f);
                ptblR.getDefaultCell().setBorder(Table.NO_BORDER);
                // 型、調剤料、薬剤料、調剤数量、薬剤料計、合計、加算
                PdfPTable pointTbl = new PdfPTable(7);
                pointTbl.setWidthPercentage(100f);
                float[] widthsPo = {7, 15.5f, 15.5f, 15.5f, 15.5f, 15.5f, 15.5f};
                pointTbl.setWidths(widthsPo);
                pointTbl.getDefaultCell().setPadding(0f);
                pointTbl.getDefaultCell().setBorder(Table.NO_BORDER);
                PdfPCell pointCell = new PdfPCell(new Paragraph("型", min_7));
                pointCell.setBorderWidth(LINE_WIDTH_1);
                setAlignCenterMiddle(pointCell);
                pointTbl.addCell(pointCell);
                pointCell = new PdfPCell(new Paragraph("調剤料", min_7));
                pointCell.setBorderWidth(LINE_WIDTH_1);
                setAlignCenterMiddle(pointCell);
                pointTbl.addCell(pointCell);
                pointCell = new PdfPCell(new Paragraph("薬剤料", min_7));
                pointCell.setBorderWidth(LINE_WIDTH_1);
                setAlignCenterMiddle(pointCell);
                pointTbl.addCell(pointCell);
                pointCell = new PdfPCell(new Paragraph("調剤数量", min_7));
                pointCell.setBorderWidth(LINE_WIDTH_1);
                setAlignCenterMiddle(pointCell);
                pointTbl.addCell(pointCell);
                pointCell = new PdfPCell(new Paragraph("薬剤料計", min_7));
                pointCell.setBorderWidth(LINE_WIDTH_1);
                setAlignCenterMiddle(pointCell);
                pointTbl.addCell(pointCell);
                pointCell = new PdfPCell(new Paragraph("合　計", min_7));
                pointCell.setBorderWidth(LINE_WIDTH_1);
                setAlignCenterMiddle(pointCell);
                pointTbl.addCell(pointCell);
                pointCell = new PdfPCell(new Paragraph("加　算", min_7));
                pointCell.setBorderWidth(LINE_WIDTH_1);
                setAlignCenterMiddle(pointCell);
                pointTbl.addCell(pointCell);
                // 上記項目の入力欄
                PdfPCell blankCell = new PdfPCell();
                blankCell.setBorderWidth(LINE_WIDTH_1);
                pointTbl.addCell(blankCell);
                pointTbl.addCell(blankCell);
                pointTbl.addCell(blankCell);
                pointTbl.addCell(blankCell);
                pointTbl.addCell(blankCell);
                pointTbl.addCell(blankCell);
                pointTbl.addCell(blankCell);
                ptblR.addCell(pointTbl);
                // 調剤基本料、薬剤指導料
                PdfPTable feeTbl = new PdfPTable(2);
                feeTbl.setWidthPercentage(100f);
                float[] widthsF = {50, 50};
                feeTbl.setWidths(widthsF);
                feeTbl.getDefaultCell().setPadding(0f);
                feeTbl.getDefaultCell().setBorder(Table.NO_BORDER);
                PdfPCell feeCell = new PdfPCell(new Paragraph("調 剤 基 本 料", min_7));
                feeCell.setBorderWidth(LINE_WIDTH_1);
                setAlignCenter(feeCell);
                feeTbl.addCell(feeCell);
                feeCell = new PdfPCell(new Paragraph("薬 剤 指 導 料", min_7));
                feeCell.setBorderWidth(LINE_WIDTH_1);
                setAlignCenter(feeCell);
                feeTbl.addCell(feeCell);
                feeCell = new PdfPCell();
                feeCell.setBorderWidth(LINE_WIDTH_1);
                feeCell.setMinimumHeight(CELL_HIGHT_1);
                feeTbl.addCell(feeCell);
                feeTbl.addCell(feeCell);
                // 保険内点数etc..
                PdfPTable feeTblSub = new PdfPTable(4);
                feeTblSub.setWidthPercentage(100f);
                float[] widthsSub = {28, 16, 28, 28};
                feeTblSub.setWidths(widthsSub);
                feeTblSub.getDefaultCell().setPadding(0f);
                feeTblSub.getDefaultCell().setBorder(Table.NO_BORDER);
                PdfPCell feeCellSub = new PdfPCell(new Paragraph("保険内点数", min_7));
                feeCellSub.setBorderWidth(LINE_WIDTH_1);
                setAlignCenter(feeCellSub);
                feeTblSub.addCell(feeCellSub);
                feeCellSub = new PdfPCell(new Paragraph("負担", min_7));
                feeCellSub.setBorderWidth(LINE_WIDTH_1);
                setAlignCenter(feeCellSub);
                feeTblSub.addCell(feeCellSub);
                feeCellSub = new PdfPCell(new Paragraph("負 担 金", min_7));
                feeCellSub.setBorderWidth(LINE_WIDTH_1);
                setAlignCenter(feeCellSub);
                feeTblSub.addCell(feeCellSub);
                feeCellSub = new PdfPCell(new Paragraph("保 険 外", min_7));
                feeCellSub.setBorderWidth(LINE_WIDTH_1);
                setAlignCenter(feeCellSub);
                feeTblSub.addCell(feeCellSub);
                // 上記項目の入力欄
                feeCellSub = new PdfPCell();
                feeCellSub.setBorderWidth(LINE_WIDTH_1);
                feeCellSub.setMinimumHeight(CELL_HIGHT_1);
                feeTblSub.addCell(feeCellSub);
                feeTblSub.addCell(feeCellSub);
                feeTblSub.addCell(feeCellSub);
                feeTblSub.addCell(feeCellSub);
                feeCell = new PdfPCell(feeTblSub);
                feeCell.setBorder(Table.NO_BORDER);
                feeCell.setColspan(2);
                feeTbl.addCell(feeCell);
                // 調剤基本料etc..テーブルのセット
                pcellR = new PdfPCell(feeTbl);
                pcellR.setPadding(0f);
                pcellR.setColspan(2);
                pcellR.setBorder(Table.NO_BORDER);
                ptblR.addCell(pcellR);
                // 備考欄
                noteTbl = new PdfPTable(2);
                noteTbl.setWidthPercentage(100f);
                float[] widthsNote = {5.3f, 94.7f};
                noteTbl.setWidths(widthsNote);
                noteTbl.getDefaultCell().setPadding(0f);
                noteTbl.getDefaultCell().setBorder(Table.NO_BORDER);
                noteCell = new PdfPCell(new Paragraph("備考", min_7));
                noteCell.setBorderWidth(LINE_WIDTH_1);
                setAlignCenterMiddle(noteCell);
                noteTbl.addCell(noteCell);
                noteCell = new PdfPCell();
                noteCell.setBorderWidth(LINE_WIDTH_1);
                noteTbl.addCell(noteCell);
                pcell = new PdfPCell(noteTbl);
                pcell.setBorderWidth(LINE_WIDTH_0);
                pcell.setPadding(0f);
                pcell.setColspan(2);
                ptblR.addCell(pcell);
                // 患者請求金額
                PdfPTable sumTbl = new PdfPTable(1);
                sumTbl.setWidthPercentage(100f);
                sumTbl.getDefaultCell().setPadding(0f);
                sumTbl.getDefaultCell().setBorder(Table.NO_BORDER);
                PdfPCell sumCell = new PdfPCell(new Paragraph("患者請求金額", min_7));
                sumCell.setBorderWidth(LINE_WIDTH_1);
                setAlignCenter(sumCell);
                sumTbl.addCell(sumCell);
                sumCell = new PdfPCell();
                sumCell.setBorderWidth(LINE_WIDTH_1);
                sumCell.setMinimumHeight(CELL_HIGHT_1);
                sumTbl.addCell(sumCell);
                ptblR.addCell(sumTbl);

                pcell = new PdfPCell(ptblR);
                pcell.setBorderWidth(LINE_WIDTH_0);
                pcell.setPadding(0f);
                ptbl.addCell(pcell);

                document.add(ptbl);
                // 改ページ
                if (ite.hasNext()) {
                    document.newPage();
                }

            } while (ite.hasNext());

            document.close();
            bos.close();
            
            // pdf content bytes
            byte[] pdfbytes = byteo.toByteArray();
            
            // 評価でない場合は Fileへ書き込んでリターン
            //if (!ClientContext.is5mTest()) {
                FileOutputStream fout = new FileOutputStream(pathToPDF);
                FileChannel channel = fout.getChannel();
                ByteBuffer bytebuff = ByteBuffer.wrap(pdfbytes);

                while(bytebuff.hasRemaining()) {
                    channel.write(bytebuff);
                }
                channel.close();
                //return pathToPDF;
            //}
            
//            // 評価の場合は water Mark を書く
//            PdfReader pdfReader = new PdfReader(pdfbytes);
//            PdfStamper pdfStamper = new PdfStamper(pdfReader,new FileOutputStream(pathToPDF));
//
//            Image image = Image.getInstance(ClientContext.getImageResource("water-mark.png"));
//
//            for(int i=1; i<= pdfReader.getNumberOfPages(); i++){
//
//                PdfContentByte content = pdfStamper.getUnderContent(i);
//
//                image.scaleAbsolute(PageSize.A5.getWidth(), PageSize.A5.getHeight());
//                image.setAbsolutePosition(0.0f, 0.0f);
//                content.addImage(image);
//            }
//
//            pdfStamper.close();
            
            return getFileName();   //http://ip:8080/filename.pdf
            
        } catch (DocumentException e) {
            e.printStackTrace(System.err);
            throw new RuntimeException(e.getMessage());
            
        } catch (IOException e) {
            e.printStackTrace(System.err);
            throw new RuntimeException(e.getMessage());
            
        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw new RuntimeException(e.getMessage());
            
        } finally {
            if (document != null && document.isOpen()) {
                document.close();
            }
        }
    }

    // @010 2012年4月診療報酬改定対応
    private List<PdfPTable> createPrescriptionTbl2() {
        ClaimItem[] items;
        PdfPCell pcell, blank, numCell, amountCell;
        int num, cnt;
        double sum; // @009 外用薬の場合の総量(数量*日数)
        List<PdfPTable> preTblList = null;
        String admin, number; // @009
        String classCode, amount, medicine; // @009
        
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("個々の処方薬について、後発医薬品(ジェネリック医薬品)への変更に差し支えがあると判断した場合には、\n");
            sb.append("「変更不可」欄に「レ」又は「×」を記載し、「保険医署名」欄に署名又は記名・押印すること。");
            PdfPTable ptbl = new PdfPTable(9); // @009
            ptbl.setWidthPercentage(100f);
            // @009 変更不可チェック欄, 番号, 名称, 剤数量, 単位, 用法, "(", 日数, "日分)"
            float[] widthsP = {14, 7, 52, 8, 15, 32, 2, 5, 9};
            ptbl.setWidths(widthsP);
            ptbl.getDefaultCell().setPadding(0f);
            preTblList = new ArrayList<PdfPTable>();

            num = 1;
            //admin = "";  // @009 用法
            //number = ""; // @009 日数
            //amount = ""; // @009 数量
            numCell = new PdfPCell(new Paragraph("", min_8)); // @009
            numCell.setBorderWidth(LINE_WIDTH_0); // @009

            PdfPCell notes = new PdfPCell(new Paragraph("変更不可", min_8));
            notes.setBorderWidth(LINE_WIDTH_1);
            notes.setBorderWidthBottom(LINE_WIDTH_0);
            PdfPCell bracket = new PdfPCell(new Paragraph("〔", min_15));
            bracket.setBorderWidth(LINE_WIDTH_0);
            bracket.setHorizontalAlignment(Element.ALIGN_RIGHT);
            bracket.setPadding(0f);
            PdfPCell notesDetail = new PdfPCell(new Paragraph(sb.toString(), min_6));
            notesDetail.setBorderWidth(LINE_WIDTH_0);
            notesDetail.setColspan(5);
            PdfPCell bracketC = new PdfPCell(new Paragraph("〕", min_15));
            bracketC.setBorderWidth(LINE_WIDTH_0);
            bracketC.setHorizontalAlignment(Element.ALIGN_LEFT);
            bracketC.setPadding(0f);
            bracketC.setColspan(2);

            for (Iterator<BundleMed> ite = pkg.getPriscriptionList().iterator(); ite.hasNext();) {    
                BundleMed prescription = ite.next();
                admin = prescription.getAdmin();                    // @009  用法
                number = prescription.getBundleNumber();            // @009  日数、回数、外用の場合=1 bundeNumber
                classCode = prescription.getClassCode();            // @009  診療行為コード 221 etc 
                
                items = prescription.getClaimItem();
//minagawa^                
                boolean genericIsOk = true;
                
                if (items!=null) {
                    for (ClaimItem i : items) {
                        if (i.getCode().equals("099209903")) {
                            genericIsOk=false;
                            break;
                        }
                    }
                }
                genericIsOk = true;
//minagawa4                

                if (ptbl.getRows().isEmpty()) {
                    ptbl.addCell(notes);
                    ptbl.addCell(bracket);
                    ptbl.addCell(notesDetail);
                    ptbl.addCell(bracketC);
                }

                // @001 2009/11/17 バグ修正
                // itemsがnullの場合がある。処方スタンプエディタでスタンプ作成時、用法がリストの一番上にある状態でカルテに展開をすると
                // 用法のみのスタンプができる。その際、items が null になる。スタンプエディタ部分を修正する必要がある？
                // 下記if条件を加えることで、処方欄に何も表示されないバグ(他に正しい処方スタンプがある場合は表示されるように)対応。
                if (items != null) {
                    boolean clearFlg = true; // @004 2010/02/26 追加 処方薬番号記載有無フラグ
                // ********** @009 ↓↓ **********
                    //sum = 0; // 外用薬の場合の剤総量
                    Font adminF = min_8; // 用法欄フォント
                    boolean wrap = true;
                    if (admin != null) {
                        if (admin.length() > 17) {
                            // 用法の文字数が17より多い場合、フォントサイズを4に設定する。
                            adminF = min_4;
                            wrap = false;
                        } else if (admin.length() > 11) {
                            // 用法の文字数が11より多い場合、フォントサイズを6に設定する。
                            adminF = min_6;
                        }
                    }
                    Font medicineF; // 薬剤名フォント
                // ********** @009 ↑↑ **********
                    
                    // ここで ClaimItem をiterate
                    for (cnt = 0; cnt < items.length; cnt++) {
                        //System.out.println("row size:" + ptbl.getRows().size());
                        
                        // 変更不可欄
                        if (!genericIsOk) {
                            blank = new PdfPCell(new Paragraph("レ", min_8));
                            blank.setBorderWidth(LINE_WIDTH_0);
                            blank.setBorderWidthRight(LINE_WIDTH_1);
                            ptbl.addCell(blank);
                            genericIsOk = true;
                            
                        } else {
                            blank = new PdfPCell();
                            blank.setBorderWidth(LINE_WIDTH_0);
                            blank.setBorderWidthRight(LINE_WIDTH_1);
                            ptbl.addCell(blank);
                        }

                        if (ptbl.getRows().size() > 13) {
                            // 行数が13より大きい場合、改ページの文言を表示し、改ページ後のテーブルを作成する。
                            blank = new PdfPCell();
                            blank.setBorderWidth(LINE_WIDTH_0);
                            blank.setColspan(4);
                            ptbl.addCell(blank);
                            pcell = new PdfPCell(new Paragraph("次ページへ続く", min_8));
                            pcell.setBorderWidth(LINE_WIDTH_0);
                            pcell.setColspan(5);
                            ptbl.addCell(pcell);
                            preTblList.add(ptbl);
                            ptbl = new PdfPTable(9);
                            ptbl.setWidthPercentage(100f);
                            ptbl.setWidths(widthsP);
                            ptbl.getDefaultCell().setPadding(0f);
                            // 変更不可欄
                            ptbl.addCell(notes);
                            ptbl.addCell(bracket);
                            ptbl.addCell(notesDetail);
                            ptbl.addCell(bracketC);
                            blank = new PdfPCell();
                            blank.setBorderWidth(LINE_WIDTH_0);
                            blank.setBorderWidthRight(LINE_WIDTH_1);
                            ptbl.addCell(blank);
                        }

                        if (cnt > 0) {
                            // スタンプの1つめ以外の場合、項目番号表示セルは空白で埋める。
                            blank = new PdfPCell();
                            blank.setBorderWidth(LINE_WIDTH_0);
                            ptbl.addCell(blank);
                        } else {
                            // スタンプの1つめは、項目番号セルの値を設定
                            numCell = new PdfPCell(new Paragraph(num++ + ")", min_8));
                            numCell.setBorderWidth(LINE_WIDTH_0);
                            setAlignRight(numCell);
                            ptbl.addCell(numCell);
                            // @004 2010/02/26 追加 ↓↓
                            // コメントコード以外が1つでもあれば、処方薬番号記載有無フラグを false にする。
                            if (!ClaimConst.COMMENT_CODE_0.equals(items[cnt].getCode())) {
                                clearFlg = false;
                            }
                            // @004 2010/02/26 追加 ↑↑
                        }

                    // ********** @009 ↓↓ **********
                        amount = items[cnt].getNumber();
//                        System.out.println("剤名：" + items[cnt].getName());
//                        System.out.println("剤数量：" + amount);
                        //medicine = "";
                        medicine = items[cnt].getName();
//                        System.out.println("薬剤名の長さ：" + medicine.length());
                        if (medicine.length() > 19) {
                            // 薬剤名の文字数が19より多い場合、フォントサイズを7に設定する。
                            medicineF = min_7;
                        } else {
                            // 薬剤名の文字数が19以下の場合、フォントサイズを8に再設定する。
                            medicineF = min_8;
                        }
                    // ********** @009 ↑↑ **********
                        
                        if ((ClaimConst.SUBCLASS_CODE_ID.equals(items[cnt].getClassCodeSystem()))
                                && ((String.valueOf(ClaimConst.YAKUZAI).equals(items[cnt].getClassCode()))
                                || (String.valueOf(ClaimConst.ZAIRYO).equals(items[cnt].getClassCode())))) {
                            
                            pcell = new PdfPCell(new Paragraph(convertNVL(items[cnt].getName()), medicineF)); // 薬剤名 または 材料名
                            pcell.setBorderWidth(LINE_WIDTH_0);
                            ptbl.addCell(pcell);
                            amountCell = new PdfPCell(new Paragraph(convertNVL(amount), min_8)); // 数量
                            setAlignRight(amountCell);
                            amountCell.setBorderWidth(LINE_WIDTH_0);
                            ptbl.addCell(amountCell);
                            
                            if (!(String.valueOf(ClaimConst.ZAIRYO).equals(items[cnt].getClassCode()))) {
                                // アイテムが材料以外の場合
                                pcell = new PdfPCell(new Paragraph(convertNVL(items[cnt].getUnit()), min_8)); // 単位
                                pcell.setBorderWidth(LINE_WIDTH_0);
                                ptbl.addCell(pcell);
                        // ********** @009 ↓↓ **********
                                pcell = new PdfPCell(new Paragraph(admin, adminF)); // 用法
                                pcell.setBorderWidth(LINE_WIDTH_0);
                                pcell.setNoWrap(wrap);
                                ptbl.addCell(pcell);
                            } else {
                                blank = new PdfPCell();
                                blank.setColspan(2);
                                blank.setBorderWidth(LINE_WIDTH_0);
                                ptbl.addCell(blank);
                            }
                            
                            if (ClaimConst.RECEIPT_CODE_GAIYO.equals(classCode)) {    
                                pcell = new PdfPCell(new Paragraph("", min_8));
                                pcell.setBorderWidth(LINE_WIDTH_0);
                                pcell.setColspan(3);
                                ptbl.addCell(pcell);
                                sum = Double.parseDouble(amount) * Double.parseDouble(number);
                                DecimalFormat f = new DecimalFormat("####0.###");
//                                System.out.println("総量：" + sum);
                                amountCell.getPhrase().clear();
                                amountCell.getPhrase().add(new Paragraph(String.valueOf(f.format(sum)), min_8)); // 数量
                            } else {
                                // 日数
                                if ((number != null) && !("".equals(number))) {
                                    pcell = new PdfPCell(new Paragraph("(", min_8));
                                    pcell.setBorderWidth(LINE_WIDTH_0);
                                    ptbl.addCell(pcell);
                                    pcell = new PdfPCell(new Paragraph(number, min_8));
                                    pcell.setBorderWidth(LINE_WIDTH_0);
                                    setAlignRight(pcell);
                                    ptbl.addCell(pcell);
                                    
//minagawa^          
                                    if (classCode.startsWith("22")) {
                                        // 頓用
                                        pcell = new PdfPCell(new Paragraph("回分)", min_8));
                                    } else if (classCode.startsWith("21")) {
                                        // 内容
                                        pcell = new PdfPCell(new Paragraph("日分)", min_8));
                                        // 外用
                                    } else if (classCode.startsWith("23")) {
                                        pcell = new PdfPCell(new Paragraph("", min_8));
                                    }
//minagawa$     
                                    pcell.setBorderWidth(LINE_WIDTH_0);
                                    setAlignRight(pcell);
                                    ptbl.addCell(pcell);
                                } else {
                                    blank = new PdfPCell();
                                    blank.setBorderWidth(LINE_WIDTH_0);
                                    blank.setColspan(3);
                                    ptbl.addCell(blank);
                                }
                            }
                        // ********** @009 ↑↑ **********
                        } else {
                            pcell = new PdfPCell(new Paragraph(convertNVL(items[cnt].getName()), min_8));
                            pcell.setBorderWidth(LINE_WIDTH_0);
                            pcell.setColspan(7);
                            ptbl.addCell(pcell);
                        }
                    }
                    // @004 2010/02/26 追加 ↓↓
                    // 処方薬番号記載有無フラグが true の場合ナンバーセルをクリアする。
                    if (clearFlg && (admin == null)) {
                        numCell.getPhrase().clear();
                        num--;
                    }
                    // @004 2010/02/26 追加 ↑↑

                } else {
                    String stampName = prescription.getOrderName();
                    System.err.println("処方スタンプ名称：「" + stampName + "」から処方情報が取得できませんでした。");
                    StringBuilder b = new StringBuilder();
                    b.append("処方スタンプ名称：「").append(stampName).append("」を確認してください。").append(System.getProperty(STR_LF));
                    System.err.println(b.toString());
                }

                if (!ite.hasNext()) {
                    blank = new PdfPCell();
                    blank.setBorderWidth(LINE_WIDTH_0);
                    ptbl.addCell(blank);
                    pcell = new PdfPCell(new Paragraph("以下余白", min_8));
                    pcell.setBorderWidth(LINE_WIDTH_0);
                    pcell.setBorderWidthLeft(LINE_WIDTH_1);
                    pcell.setPaddingLeft(30f);
                    pcell.setColspan(8);
                    ptbl.addCell(pcell);
                    preTblList.add(ptbl);
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return preTblList;
    }

    /**
     * 文字列を指定した桁数のchar配列に 右詰 or 左詰 で分割する
     * @param str
     * @param num 配列長さ
     * @param pad 右詰(R)or左詰(L)
     * @return char[] 配列
     */
    private char[] partitionPadRL(String str, int num, String pad) {
        char[] ret;
        if ("R".equals(pad)) {
            ret = partitionPadR(str, num);
        } else {
            ret = partitionPadL(str, num);
        }
        return ret;
    }

    /**
     * 文字列を指定した文字数分のchar配列に分割し、右詰め(不足分は空白文字)する。
     * @param str 分割する文字列
     * @param num 桁数
     * @return char[]
     */
    private char[] partitionPadR(String str, int num) {
        char[] ret = null;
        StringBuilder sb;
        int cnt;
        try {
            if (str != null) {
                int strNum = str.toCharArray().length;
                if (num < strNum) {
                    /*
                     * 文字数より、分割数の方が大きく指定された場合
                     * 保険者番号(8)、公費負担者番号(8)、公費負担医療の受給者番号(7)はMAX桁数が決まっている。()内はMAX桁数
                     * ORCAで登録された上記番号の桁数がオーバーしていればここへ入る。
                     * エラーにしたほうがよい？？
                     */
                    ret = new char[strNum];
                    ret = str.toCharArray();
                } else {
                    ret = new char[num];
                    sb = new StringBuilder(num);
                    cnt = num - strNum;
                    sb.append(str);
                    for (int i = 0; i < cnt ; i++) {
                        sb.insert(0, " ");
                    }
                    ret = sb.toString().toCharArray();
                }
            } else {
                ret = new char[num];
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return ret;
    }

    /**
     * 文字列を指定した文字数分のchar配列に分割し、左詰め(不足分は空白文字)する。
     * 
     * @param str 分割する文字列
     * @param num 桁数
     * @return char[]
     */
    private char[] partitionPadL(String str, int num) {
        char[] ret;
        ret = null;
        StringBuilder sb;
        int cnt;
        try {
            if (str != null) {
                int strNum = str.toCharArray().length;
                if (num < strNum) {
                    /*
                     * 文字数より、分割数の方が大きく指定された場合
                     * 保険者番号(8)、公費負担者番号(8)、公費負担医療の受給者番号(7)はMAX桁数が決まっている。()内はMAX桁数
                     * ORCAで登録された上記番号の桁数がオーバーしていればここへ入る。
                     * エラーにしたほうがよい？？
                     */
                    ret = new char[strNum];
                    ret = str.toCharArray();
                } else {
                    ret = new char[num];
                    sb = new StringBuilder(num);
                    cnt = num - strNum;
                    sb.append(str);
                    for (int i = 0; i < cnt ; i++) {
                        sb.append(" ");
                    }
                    ret = sb.toString().toCharArray();
                }
            } else {
                ret = new char[num];
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return ret;
    }

    /**
     * nullを空の文字列に変換します。
     * 
     * @param str
     * @return
     */
    private String convertNVL(String str) {
        return str==null ? "" : str;
    }

    /**
     * セルの文字列配置設定
     * 　横：右揃え　縦：中央
     * @param pcell
     */
    private void setAlignRightMiddle(PdfPCell pcell) {
        setAlignRight(pcell);
        setAlignMiddle(pcell);
    }

    /**
     * セルの文字列配置設定
     * 　横：真ん中　縦：中央
     * @param pcell
     */
    private void setAlignCenterMiddle(PdfPCell pcell) {
        setAlignCenter(pcell);
        setAlignMiddle(pcell);
    }

    /**
     * セルの文字列配置設定
     * 　横：右揃え
     * @param pcell
     */
    private void setAlignRight(PdfPCell pcell) {
        pcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
    }

    /**
     * セルの文字列配置設定
     * 　横：左揃え
     * @param pcell
     */
    private void setAlignLeft(PdfPCell pcell) {
        pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
    }

    /**
     * セルの文字列配置設定
     * 　横：真ん中
     * @param pcell
     */
    private void setAlignCenter(PdfPCell pcell) {
        pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
    }

    /**
     * セルの文字列配置設定
     * 　縦：中央
     * @param pcell
     */
    private void setAlignMiddle(PdfPCell pcell) {
        pcell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    }

    /**
     * セルの文字列配置設定
     * 　縦：上
     * @param pcell
     */
    private void setAlignTop(PdfPCell pcell) {
        pcell.setVerticalAlignment(Element.ALIGN_TOP);
    }

    /**
     * セルの文字列配置設定
     * 　横：均等
     * @param pcell
     */
    private void setAlignJustifiedAll(PdfPCell pcell) {
        pcell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED_ALL);
    }

// @002 2009/11/17 追加
    /**
     * ドキュメント保存ディレクトリを返します。
     * 
     * @return ドキュメント保存ディレクトリ
     */
    public String getDocumentDir() {
        return documentDir;
    }

    /**
     * ドキュメント保存ディレクトリを設定します。
     * 
     * @param documentDir String
     */
    public void setDocumentDir(String documentDir) {
        this.documentDir = documentDir;
    }

    /**
     * ファイル名を返します。
     * 
     * @return ファイル名
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * ファイル名を設定します。
     * 
     * @param fileName String
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
// @002 ↑ここまで
}
