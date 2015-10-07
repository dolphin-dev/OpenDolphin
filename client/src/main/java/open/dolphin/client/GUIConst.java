package open.dolphin.client;

import java.awt.Color;

/**
 *
 * @author Kazushi Minagawa.
 * 2015/04/22 Action 整理、並べ替え
 *            一部のColor集約
 *            未使用削除
 */
public class GUIConst {
    
    // メニュー名 MenuListenerでどのメニューが選択されたかを判別
    public static final String MENU_NAME_INSERT = "menuName.insert";
    public static final String MENU_NAME_TEXT = "menuName.text";
    
    // File Menu
    public static final String ACTION_NEW_KARTE = "newKarte";
    public static final String ACTION_NEW_DOCUMENT = "newDocument";
    public static final String ACTION_OPEN_KARTE = "openKarte";
    public static final String ACTION_CLOSE = "close";
    public static final String ACTION_SAVE = "save";
    public static final String ACTION_DELETE = "delete";
    public static final String ACTION_PRINTER_SETUP = "printerSetup";
    public static final String ACTION_PRINT = "print";
    public static final String ACTION_PROCESS_EXIT = "processExit";
   
    // Edit Menu
    public static final String ACTION_MODIFY_KARTE = "modifyKarte";
    public static final String ACTION_UNDO = "undo";
    public static final String ACTION_REDO = "redo";
    public static final String ACTION_CUT = "cut";
    public static final String ACTION_COPY = "copy";
    public static final String ACTION_PASTE = "paste";
    
    // Karte Menu
    public static final String ACTION_CHANGE_NUM_OF_DATES_ALL = "changeNumOfDatesAll";
    public static final String ACTION_SEND_CLAIM = "sendClaim";                         // pns^
    public static final String ACTION_CREATE_PRISCRIPTION = "createPrescription";       // Hiro Clinic^ 処方箋印刷
    public static final String ACTION_CHECK_INTERACTION = "checkInteraction";           //masuda^
    public static final String ACTION_ASCENDING = "ascending";
    public static final String ACTION_DESCENDING = "descending";
    public static final String ACTION_SHOW_MODIFIED = "showModified";
    public static final String ACTION_SET_KARTE_ENVIROMENT = "setKarteEnviroment";
    public static final String ACTION_SELECT_INSURANCE = "selectInsurance";  
    public static final String ACTION_NIMBUS_LOOK_AND_FEEL = "nimbusLookAndFeel";       //LookAndFeeL Win only
    public static final String ACTION_NATIVE_LOOK_AND_FEEL = "nativeLookAndFeel";       //LookAndFeeL Win only
    
    // Insert Menu
    public static final String ACTION_INSERT_DISEASE = "insertDisease";
    public static final String ACTION_INSERT_TEXT = "insertText";
    public static final String ACTION_INSERT_SCHEMA = "insertSchema";
    public static final String ACTION_ATTACHMENT = "attachment";
    public static final String ACTION_INSERT_STAMP = "insertStamp";
    
    // Text Menu
    public static final String ACTION_SIZE = "size";
    public static final String ACTION_FONT_LARGER = "fontLarger";
    public static final String ACTION_FONT_SMALLER = "fontSmaller";
    public static final String ACTION_FONT_STANDARD = "fontStandard";
    public static final String ACTION_STYLE = "style";
    public static final String ACTION_FONT_BOLD = "fontBold";
    public static final String ACTION_FONT_ITALIC = "fontItalic";
    public static final String ACTION_FONT_UNDERLINE = "fontUnderline";
    public static final String ACTION_JUSTIFY = "justify";
    public static final String ACTION_LEFT_JUSTIFY = "leftJustify";
    public static final String ACTION_CENTER_JUSTIFY = "centerJustify";
    public static final String ACTION_RIGHT_JUSTIFY = "rightJustify";
    public static final String ACTION_COLOR = "color";
    public static final String ACTION_FONT_RED = "fontRed";
    public static final String ACTION_FONT_ORANGE = "fontOrange";
    public static final String ACTION_FONT_YELLOW = "fontYellow";
    public static final String ACTION_FONT_GREEN = "fontGreen";
    public static final String ACTION_FONT_BLUE = "fontBlue";
    public static final String ACTION_FONT_PURPLE = "fontPurple";
    public static final String ACTION_FONT_GRAY = "fontGray";
    public static final String ACTION_FONT_BLACK = "fontBlack";
    
    // Tool Menu
    public static final String ACTION_SHOW_STAMPBOX = "showStampBox";
    public static final String ACTION_NEW_PATIENT = "addNewPatient";
    public static final String ACTION_SHOW_SCHEMABOX = "showSchemaBox";
    public static final String ACTION_CHANGE_PASSWORD = "changePassword";
    public static final String ACTION_EDIT_FACILITY_INFO = "editFacilityInfo";
    public static final String ACTION_ADD_USER = "addUser";
    public static final String ACTION_FETCH_ACTIVITIES = "fetchActivities";
    public static final String ACTION_FETCH_FACILITY_CODE = "fetchFacilityCode";
    public static final String ACTION_RECEIPT_BARCODE = "receiptBarcode";           //s.oh^ 2014/08/19 受付バーコード対応
    public static final String ACTION_OUTPUT_ALLKARTEPDF = "outputAllKartePdf";     //s.oh^ 2014/07/22 一括カルテPDF出力
    
    // Help
    public static final String ACTION_BROWS_DOLPHIN = "browseDolphinSupport";
    public static final String ACTION_BROWS_DOLPHIN_PROJECT = "browseDolphinProject";
    public static final String ACTION_BROWS_MEDXML = "browseMedXml";
    public static final String ACTION_SHOW_ABOUT = "showAbout"; 
    
//s.oh^ テキストの挿入 2013/08/12
    public static final String ACTION_SOAPANE_INSERTTEXT_DIR = "soapane.inserttext.dir";
    public static final String ACTION_PPANE_INSERTTEXT_DIR = "ppane.inserttext.dir";
//s.oh$
    public static final String ACTION_OTHERPROCESS_ICON = "otherprocessicon.link";  // ?
    
    // Role
    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_USER = "user";
    
    // カルテ背景 Dolphin's Green
    public static final Color KARTE_UNEDITABLE_BK_COLOR = new Color(227, 250, 207); // Lite Green
    
    // カルテ TimeStamp Label
    public static final Color KARTE_TIME_STAMP_FORE_COLOR = new Color(0,51,153); // Timestamp default (Dark blue)
    public static final Color TEMP_SAVE_KARTE_BK_COLOR = new Color(0,0,153);    // Drak blue
    public static final Color TEMP_SAVE_KARTE_FORE_COLOR = Color.white;         // white
    public static final Color SCHEDULE_KARTE_BK_COLOR = new Color(250, 191, 19);// 250 G：191 B：19
    public static final Color SCHEDULE_KARTE_FORE_COLOR = Color.black; 
    
    // StampHolder
    public static final Color STAMP_HOLDER_SELECTED_BORDER = new Color(255, 0, 153);    // Pink
    public static final Color STAMP_HOLDER_NON_SELECTED_BORDER = new Color(0, 0, 0, 0);  // 透明
    
    // 基礎情報用の性別カラー
    public static final Color BASIC_INFO_MALE_COLOR = new Color(118,166,212);
    public static final Color BASIC_INFO_FEMALE_COLOR = new Color(226,191,212);
    public static final Color BASIC_INFO_UNKNOW_COLOR = new Color(200,200,200);
    public static final Color BASIC_INFO_FOREGROUND = new Color(20,20,140);
    
    // JTableレンダラ用の性別カラー 
    public static final Color TABLE_MALE_COLOR = new Color(237,243,254);    // male
    public static final Color TABLE_FEMALE_COLOR = new Color(254,221,242);  // female
    
    // 奇数・偶数カラー
    public static final Color TABLE_ODD_COLOR = new Color(255,255,255);     // 奇数行ClientContext.getColor("color.odd"); 
    public static final Color TABLE_EVEN_COLOR = new Color(237,243,254);    // ClientContext.getColor("color.even");
    
    // 保険のカラーリング
    public static final Color SELF_INSURANCE_COLOR = new Color(255,255,102);        // 自費
    public static final Color JIBAISEKI_INSURANCE_COLOR = new Color(102,153,255);   // 自賠責
    public static final Color ROSAI_INSURANCE_COLOR = Color.orange;                 // 労災 new Color(255,102,204);
    
    
    // 環境設定後のサービス開始停止関連
    public static final String ACTION_CONFIRM_RUN = "confirmRun";
    public static final String PVT_SERVER_IS_RUNNING = "runtime.pvtServer";
    public static final String SEND_CLAIM_IS_RUNNING = "runtime.sendClaim";
    public static final String PVT_RELAY_IS_RUNNING = "runtime.pvt.relay";      // 受付リレー
    public static final String SEND_MML_IS_RUNNING = "runtime.sendMml";         // MML出力
    
}
