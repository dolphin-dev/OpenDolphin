package open.dolphin.letter;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import open.dolphin.client.ClientContext;
import open.dolphin.infomodel.*;
import open.dolphin.project.Project;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * PDFKarteMaker.java
 * カルテをPDFにエクスポートする。KarteRenderer2から主なコード拝借
 *
 * @author masuda, Masuda Naika
 */
public class KartePDFMaker extends AbstractPDFMaker {

    // from KarteRenderer_2.java
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
    private static final String PROGRESS_COURSE_NAME = "kartePane";
    private static final String[] REPLACES = new String[]{"<", ">", "&", "'", "\""};
    private static final String[] MATCHES = new String[]{"&lt;", "&gt;", "&amp;", "&apos;", "&quot;"};

    private static final int KARTE_FONT_SIZE = 9;
    private static final int STAMP_FONT_SIZE = 8;
    private static final int PERCENTAGE_IMAGE_WIDTH = 25;   // ページ幅の1/4
    private static final Color STAMP_TITLE_BACKGROUND = new Color(200, 200, 200);    // グレー
    private static final String UNDER_TMP_SAVE = " - 仮保存中";
    
    private static final String DOC_TITLE = "カルテ";

    private List<DocumentModel> docList;
    private boolean ascending;
    private int bookmarkNumber;   // しおりの内部番号
    
    @Override
    protected final String getPatientName() {
        return (context != null) ? context.getPatient().getFullName() : null;
    }

    @Override
    protected final String getPatientId() {
        return (context != null) ? context.getPatient().getPatientId() : null;
    }
    
    private String getPatientBirthday() {
        return (context != null) ? context.getPatient().getBirthday() : null;
    }
    
    // 文書名を返す
    @Override
    protected String getTitle() {
        return DOC_TITLE.replace(" ", "").replace("　", "");
    }
    
    // PDFに出力する
    @Override
    public boolean makePDF(String filePath) {

        boolean result = false;
        marginLeft = 20;
        marginRight = 20;
        marginTop = 20;
        marginBottom = 30;
        titleFontSize = 10;        

        // 昇順・降順にソート
        if (ascending) {
            Collections.sort(docList);
        } else {
            Collections.sort(docList, Collections.reverseOrder());
        }

        // 用紙サイズを設定
        Document document = new Document(PageSize.A4, marginLeft, marginRight, marginTop, marginBottom);

        try {
            // Font
            baseFont = getGothicFont();
            Font font = new Font(baseFont, KARTE_FONT_SIZE);

            // PdfWriterの設定
//minagawa^ mac jdk7            
//            writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
            Path path = Paths.get(filePath);
            writer = PdfWriter.getInstance(document, Files.newOutputStream(path));
//minagawa$            
            writer.setStrictImageSequence(true);
            writer.setViewerPreferences(PdfWriter.PageModeUseOutlines);

            // フッターに名前とIDを入れる
            StringBuilder sb = new StringBuilder();
            sb.append(getPatientId()).append(" ");
            sb.append(getPatientName()).append(" 様 ");
            sb.append(FRMT_DATE_WITH_TIME.format(new Date()));
            sb.append("  Page ");
            HeaderFooter footer = new HeaderFooter(new Phrase(sb.toString(), font), true);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setBorder(Rectangle.NO_BORDER);
            document.setFooter(footer);

            // 製作者と文書タイトルを設定
            String author = Project.getUserModel().getFacilityModel().getFacilityName();
            document.addAuthor(author);
            document.addTitle(getPatientName() + " 様カルテ");

            document.open();

            for (DocumentModel docModel : docList) {

                // DocumentModelからschema, moduleを取り出す
                List<SchemaModel> schemas = docModel.getSchema();
                List<ModuleModel> modules = docModel.getModules();
                List<ModuleModel> soaModules = new ArrayList<ModuleModel>();
                List<ModuleModel> pModules = new ArrayList<ModuleModel>();
                String soaSpec = null;
                String pSpec = null;

                for (ModuleModel bean : modules) {
                    String role = bean.getModuleInfoBean().getStampRole();
                    switch (role) {
                        case IInfoModel.ROLE_SOA:
                            soaModules.add(bean);
                            break;
                        case IInfoModel.ROLE_SOA_SPEC:
                            soaSpec = ((ProgressCourse) bean.getModel()).getFreeText();
                            break;
                        case IInfoModel.ROLE_P:
                            pModules.add(bean);
                            break;
                        case IInfoModel.ROLE_P_SPEC:
                            pSpec = ((ProgressCourse) bean.getModel()).getFreeText();
                            break;
                    }
                }

                // 念のためソート
                Collections.sort(soaModules);
                Collections.sort(pModules);

                // テーブルを作成する
                KarteTable table;
                DocInfoModel docInfo = docModel.getDocInfoModel();

                if (docInfo != null && docInfo.getDocType().equals(IInfoModel.DOCTYPE_S_KARTE)) {
                    table = createTable(docModel, 1);
                    PdfPCell cell = new PdfCellRenderer().render(soaSpec, soaModules, schemas, table);
                    cell.setColspan(2);
                    table.addCell(cell);
                } else {
                    table = createTable(docModel, 2);
                    PdfPCell cell = new PdfCellRenderer().render(soaSpec, soaModules, schemas, table);
                    table.addCell(cell);
                    cell = new PdfCellRenderer().render(pSpec, pModules, schemas, table);
                    table.addCell(cell);
                }

                // PdfDocumentに追加する
                document.add(table);
            }

            result = true;

        } catch (IOException ex) {
            ClientContext.getBootLogger().warn(ex);
            throw new RuntimeException(ERROR_IO);
        } catch (DocumentException ex) {
            ClientContext.getBootLogger().warn(ex);
            throw new RuntimeException(ERROR_PDF);
        } finally {
            if (document.isOpen()) {
                document.close();
            }
            if (writer != null) {
                writer.close();
            }
        }
        return result;
    }

    public void setDocumentList(List<DocumentModel> docList) {
        this.docList = docList;
    }

    public void setAscending(boolean b) {
        this.ascending = b;
    }
    
    private KarteTable createTable(DocumentModel model, int col) {

        // タイトルのフォント
        Font font = new Font(baseFont, titleFontSize);
        String title = createTitle(model);
        // しおりのタイトルは日付とDocInfo.tilte
        StringBuilder sb = new StringBuilder();
        sb.append(FRMT_DATE_WITH_TIME.format(model.getDocInfoModel().getFirstConfirmDate()));
        sb.append("\n");
        sb.append(model.getDocInfoModel().getTitle());
        String bookmark = sb.toString();

        // タイトルにしおりを登録する
        String mark = String.valueOf(++bookmarkNumber);
        Chunk chunk = new Chunk(title, font).setLocalDestination(mark);
        PdfOutline root = writer.getDirectContent().getRootOutline();
        new PdfOutline(root, PdfAction.gotoLocalPage(mark, false), bookmark);

        // テーブルを作成する
        PdfPCell titleCell = new PdfPCell(new Paragraph(chunk));
        titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        KarteTable table = new KarteTable(col);
        titleCell.setColspan(col);

        table.addCell(titleCell);
        table.setWidthPercentage(100);
        table.setSpacingAfter(2);
        // ヘッダー行を指定
        //table.setHeaderRows(1);
        // 改頁で表の分割を許可
        table.setSplitLate(false);

        return table;
    }

    // カルテのタイトルを作成する
    private String createTitle(DocumentModel docModel) {

        StringBuilder sb = new StringBuilder();

        if (IInfoModel.STATUS_DELETE.equals(docModel.getDocInfoModel().getStatus())) {
            sb.append("削除済／");
        } else if (IInfoModel.STATUS_MODIFIED.equals(docModel.getDocInfoModel().getStatus())) {
            sb.append("修正:");
            sb.append(docModel.getDocInfoModel().getVersionNumber().replace(".0", ""));
            sb.append("／");
        }

        // 確定日を分かりやすい表現に変える
        sb.append(ModelUtils.getDateAsFormatString(
                docModel.getDocInfoModel().getFirstConfirmDate(),
                IInfoModel.KARTE_DATE_FORMAT));

        // 当時の年齢を表示する
        String mmlDate = ModelUtils.getDateAsString(docModel.getDocInfoModel().getFirstConfirmDate());
        if (getPatientBirthday() != null) {
            sb.append("[").append(ModelUtils.getAge2(getPatientBirthday(), mmlDate)).append("歳]");
        }

        // 仮保存
        if (docModel.getDocInfoModel().getStatus().equals(IInfoModel.STATUS_TMP)) {
            sb.append(UNDER_TMP_SAVE);
        }

        // 保険　公費が見えるのは気分良くないだろうから、表示しない
        // SPC区切りの保険者番号・保険者名称・公費のフォーマットである
        String ins = docModel.getDocInfoModel().getHealthInsuranceDesc().trim();
        if (ins != null && !ins.isEmpty()) {
            String items[] = docModel.getDocInfoModel().getHealthInsuranceDesc().split(" ");
            List<String> itemList = new ArrayList<String>();
            for (String item : items) {
                if (!item.isEmpty()) {
                    itemList.add(item);
                }
            }
            switch (itemList.size()) {
                case 1:
                    sb.append("／");
                    sb.append(ins);
                    break;
                case 2:
                case 3:
                    sb.append("／");
                    sb.append(itemList.get(1));
                    break;
            }
        }
        
        // KarteViewerで日付の右Dr名を表示する
        sb.append("／");
        sb.append(docModel.getUserModel().getCommonName());

        return sb.toString();
    }
    
    private String parseBundleNum(String str) {
        
        int len = str.length();
        int pos = str.indexOf("/");
        StringBuilder sb = new StringBuilder();
        sb.append("回数：");
        sb.append(str.substring(0, pos));
        sb.append(" 実施日：");
        sb.append(str.substring(pos + 1, len));
        sb.append("日");

        return sb.toString();
    }
    
    private class KarteTable extends PdfPTable {

        private int col;    // カラム数

        private KarteTable(int col) {
            super(col);
            this.col = col;
        }

        private int getColumnCount() {
            return col;
        }
    }

    private class PdfCellRenderer {

        private PdfPCell cell;
        private Paragraph theParagraph;
        private List<ModuleModel> modules;
        private List<SchemaModel> schemas;
        private KarteTable karteTable;

        private PdfPCell render(String xml, List<ModuleModel> modules, List<SchemaModel> schemas, KarteTable karteTable) {

            this.modules = modules;
            this.schemas = schemas;
            this.karteTable = karteTable;

            // SoaPane, Ppaneが収まるセル
            cell = new PdfPCell();

            SAXBuilder docBuilder = new SAXBuilder();

            try {
                StringReader sr = new StringReader(xml);
                org.jdom.Document doc = docBuilder.build(new BufferedReader(sr));
                org.jdom.Element root = doc.getRootElement();
                writeChildren(root);
            } catch (JDOMException e) {
                e.printStackTrace(System.err);
            } catch (IOException e) {
                e.printStackTrace(System.err);
            }
            return cell;
        }

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
                startIcon(current);

            } else if (eName.equals(PROGRESS_COURSE_NAME)) {
                eType = TT_PROGRESS_COURSE;
                startProgressCourse();

            } else if (eName.equals(SECTION_NAME)) {
                eType = TT_SECTION;
                startSection();
            }

            // 子を探索するのはパラグフとトップ要素のみ
            if (eType == TT_PARAGRAPH || eType == TT_PROGRESS_COURSE || eType == TT_SECTION) {

                List children = current.getChildren();
                Iterator iterator = children.iterator();

                while (iterator.hasNext()) {
                    org.jdom.Element child = (org.jdom.Element) iterator.next();
                    writeChildren(child);
                }
            }

            switch (eType) {

                case TT_PARAGRAPH:
                    endParagraph();
                    break;
                case TT_CONTENT:
                    endContent();
                    break;
                case TT_ICON:
                    endIcon();
                    break;
                case TT_COMPONENT:
                    endComponent();
                    break;
                case TT_PROGRESS_COURSE:
                    endProgressCourse();
                    break;
                case TT_SECTION:
                    endSection();
                    break;
            }
        }

        private void startParagraph(String alignStr) {

            theParagraph = createNewParagraph();
            cell.addElement(theParagraph);

            if (alignStr != null) {
                if (alignStr.equals("0")) {
                    theParagraph.setAlignment(Element.ALIGN_LEFT);
                } else if (alignStr.equals("1")) {
                    theParagraph.setAlignment(Element.ALIGN_CENTER);
                } else if (alignStr.equals("2")) {
                    theParagraph.setAlignment(Element.ALIGN_RIGHT);
                }
            }
        }

        private Paragraph createNewParagraph() {
            Paragraph p = new Paragraph();
            p.setFont(new Font(baseFont, KARTE_FONT_SIZE));
            //p.setLeading(KARTE_FONT_SIZE + 2);
            return p;
        }

        private void endParagraph() {
        }

        private void startContent(
                String foreground,
                String size,
                String bold,
                String italic,
                String underline,
                String text) {

            // 特殊文字を戻す
            for (int i = 0; i < REPLACES.length; i++) {
                text = text.replaceAll(MATCHES[i], REPLACES[i]);
            }

            Font font = theParagraph.getFont();
            // foreground 属性を設定する
            if (foreground != null) {
                StringTokenizer stk = new StringTokenizer(foreground, ",");
                if (stk.hasMoreTokens()) {
                    int r = Integer.parseInt(stk.nextToken());
                    int g = Integer.parseInt(stk.nextToken());
                    int b = Integer.parseInt(stk.nextToken());
                    theParagraph.getFont().setColor(new Color(r, g, b));
                }
            }

            // size 属性を設定する
            if (size != null) {
                font.setSize(Float.valueOf(size));
            }

            // bold 属性を設定する
            if (bold != null) {
                font.setStyle(Font.BOLD);
            }

            // italic 属性を設定する
            if (italic != null) {
                font.setStyle(Font.ITALIC);
            }

            // underline 属性を設定する
            if (underline != null) {
                font.setStyle(Font.UNDERLINE);
            }

            // テキストを挿入する
            if (!text.trim().equals("")) {  // スタンプで改行されないために
                theParagraph.add(new Chunk(text));
            }
        }

        private void endContent() {
        }

        // スタンプとシェーマを配置する
        private void startComponent(String name, String number) {

            int index = Integer.valueOf(number);
            PdfPTable pTable = null;

            if (name != null && name.equals(STAMP_HOLDER)) {
                ModuleModel stamp = modules.get(index);
                pTable = createStampTable(stamp);

            } else if (name != null && name.equals(SCHEMA_HOLDER)) {
                SchemaModel schema = schemas.get(index);
                pTable = createImageTable(schema);
            }

            // cellにスタンプを追加する
            if (pTable == null) {
                return;
            }
            cell.addElement(pTable);

            // スタンプを挿入した後はParagraphを作り直してcellに追加
            Paragraph p = createNewParagraph();
            // フォント・アライメントを引き継ぐ
            p.setFont(theParagraph.getFont());
            p.setAlignment(theParagraph.getAlignment());
            theParagraph = p;
            cell.addElement(theParagraph);
        }

        private void startSection() {
        }

        private void endSection() {
        }

        private void endComponent() {
        }

        private void startProgressCourse() {
        }

        private void endProgressCourse() {
        }

        private void startIcon(org.jdom.Element current) {
        }

        private void endIcon() {
        }

        // SchemaをImage入りのテーブルとして作成する
        private PdfPTable createImageTable(SchemaModel schema) {

            try {
                // Schemaはカラム数１のテーブル
                PdfPTable table = new PdfPTable(1);
                table.setSpacingBefore(1);
                //table.setSpacingAfter(1);
                table.setHorizontalAlignment(Element.ALIGN_LEFT);
                // イメージのパーセントを設定
                int percentage = Math.min(PERCENTAGE_IMAGE_WIDTH * karteTable.getColumnCount(), 100);
                table.setWidthPercentage(percentage);
                // SchemaModelからjpeg imageを取得
                Image image = Image.getInstance(schema.getJpegByte());
                // セルにimageを設定
                PdfPCell pcell = new PdfPCell(image, true);
                pcell.setBorder(Rectangle.NO_BORDER);
                // テーブルに追加
                table.addCell(pcell);

                return table;

            } catch (BadElementException ex) {
            } catch (MalformedURLException ex) {
            } catch (IOException ex) {
            }

            return null;
        }

        // スタンプをテーブルとして作成する
        private PdfPTable createStampTable(ModuleModel stamp) {

            try {
                // スタンプのテーブルを作成、カラム数３
                PdfPTable table = new PdfPTable(3);
                table.setWidthPercentage(100);
                table.setWidths(new int[]{7, 1, 2});    // てきとー
                table.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.setSpacingAfter(5);

                // スタンプの種類別に処理する
                String entity = stamp.getModuleInfoBean().getEntity();

                if (IInfoModel.ENTITY_LABO_TEST.equals(entity)) {
                    // 検体検査スタンプ
                    BundleDolphin model = (BundleDolphin) stamp.getModel();
                    // スタンプ名
                    StringBuilder sb = new StringBuilder();
                    sb.append(model.getOrderName());
                    sb.append("(");
                    sb.append(stamp.getModuleInfoBean().getStampName());
                    sb.append(")");
                    table.addCell(createStampCell(sb.toString(), 2, true));
                    // ClassCode
                    table.addCell(createStampCell(model.getClassCode(), 1, true));
                    // 項目
                    table.addCell(createStampCell(model.getItemNames(), 3, false));
                    // bundleNumber
                    String number = model.getBundleNumber();
                    if (number != null && number.startsWith("*")) {
                        String str = parseBundleNum(number);
                        table.addCell(createStampCell(str, 3, false));
                    } else if (number != null && !number.trim().isEmpty() && !"1".equals(number)) {
                        table.addCell(createStampCell("・回数", 1, false));
                        table.addCell(createStampCell(number, 1, false));
                        table.addCell(createStampCell(" 回", 1, false));
                    }
                    // メモ
                    String memo = model.getMemo();
                    if (memo != null && !memo.trim().isEmpty()) {
                        table.addCell(createStampCell(memo, 3, false));
                    }

                } else if (IInfoModel.ENTITY_MED_ORDER.equals(entity)) {
                    // 処方スタンプ
                    BundleMed model = (BundleMed) stamp.getModel();
                    // スタンプ名
                    StringBuilder sb = new StringBuilder();
                    sb.append("RP）");
                    sb.append(stamp.getModuleInfoBean().getStampName());
                    table.addCell(createStampCell(sb.toString(), 2, true));
                    // 院内・院外、ClassCode
                    String str = model.getMemo().replace("処方", "") + "/" + model.getClassCode();
                    table.addCell(createStampCell(str, 2, true));
                    // 薬剤項目
                    for (ClaimItem ci : model.getClaimItem()) {
                        // コメントコードでは・とｘは表示しない
                        if (!ci.getCode().matches(ClaimConst.REGEXP_COMMENT_MED)) {
                            table.addCell(createStampCell("・" + ci.getName(), 1, false));
                            table.addCell(createStampCell(" x " + ci.getNumber(), 1, false));
                            table.addCell(createStampCell(ci.getUnit(), 1, false));
                        } else {
                            table.addCell(createStampCell(ci.getName(), 1, false));
                            table.addCell(createStampCell(" ", 1, false));
                            table.addCell(createStampCell(" ", 1, false));
                        }
                    }
                    // 用法
                    table.addCell(createStampCell(model.getAdminDisplayString(), 3, false));

                } else {
                    // その他スタンプ
                    BundleDolphin model = (BundleDolphin) stamp.getModel();
                    // スタンプ名
                    StringBuilder sb = new StringBuilder();
                    sb.append(model.getOrderName());
                    sb.append("(");
                    sb.append(stamp.getModuleInfoBean().getStampName());
                    sb.append(")");
                    table.addCell(createStampCell(sb.toString(), 2, true));
                    // ClassCode
                    table.addCell(createStampCell(model.getClassCode(), 1, true));
                    // 項目
                    for (ClaimItem ci : model.getClaimItem()) {
                        if (ci.getNumber() != null) {
                            table.addCell(createStampCell("・" + ci.getName(), 1, false));
                            table.addCell(createStampCell(" x " + ci.getNumber(), 1, false));
                            table.addCell(createStampCell(ci.getUnit(), 1, false));
                        } else {
                            table.addCell(createStampCell("・" + ci.getName(), 1, false));
                            table.addCell(createStampCell(" ", 1, false));
                            table.addCell(createStampCell(" ", 1, false));
                        }
                    }
                    // bundleNumber
                    String number = model.getBundleNumber();
                    if (number != null && number.startsWith("*")) {
                        String str = parseBundleNum(number);
                        table.addCell(createStampCell(str, 3, false));
                    } else if (number != null && !number.trim().isEmpty() && !"1".equals(number)) {
                        table.addCell(createStampCell("・回数", 1, false));
                        table.addCell(createStampCell(number, 1, false));
                        table.addCell(createStampCell(" 回", 1, false));
                    }
                    // メモ
                    String memo = model.getMemo();
                    if (memo != null && !memo.trim().isEmpty()) {
                        table.addCell(createStampCell(memo, 3, false));
                    }
                }

                return table;

            } catch (DocumentException ex) {
            }

            return null;
        }

        // スタンプ表示に使うPdfPCellを作成する
        private PdfPCell createStampCell(String str, int colSpan, boolean setBackground) {

            if (str == null) {
                str = "";
            }
            PdfPCell pcell = new PdfPCell();
            pcell.setColspan(colSpan);
            pcell.setPadding(0);
            pcell.setBorder(Rectangle.NO_BORDER);
            if (setBackground) {
                pcell.setBackgroundColor(STAMP_TITLE_BACKGROUND);
            }
            pcell.addElement(new Chunk(str, new Font(baseFont, STAMP_FONT_SIZE)));
            return pcell;
        }
    }
}
