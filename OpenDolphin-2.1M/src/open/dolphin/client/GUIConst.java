package open.dolphin.client;

import java.awt.Dimension;

/**
 *
 * @author Kazushi Minagawa.
 */
public class GUIConst {
    
    //
    // client package で使用する定数
    //
    // メニュー関連
    public static final String SHOW_SCHEMA_BOX = "showSchemaBox";
    public static final String MENU_KARTE = "カルテ";
    public static final String MENU_TEXT = "テキスト";
    public static final String MENU_SCHEMA = "シェーマ";
    public static final String MENU_STAMP = "スタンプ";
    public static final String MENU_INSERT = "挿 入";
    public static final String MENU_INSURANCE = "保険選択";

    public static final String ACTION_NEW_KARTE = "newKarte";
    public static final String ACTION_NEW_DOCUMENT = "newDocument";
    public static final String ACTION_OPEN_KARTE = "openKarte";
    public static final String ACTION_CLOSE = "close";
    public static final String ACTION_SAVE = "save";
    public static final String ACTION_DELETE = "delete";
    public static final String ACTION_PRINTER_SETUP = "printerSetup";
    public static final String ACTION_PRINT = "print";
    public static final String ACTION_PROCESS_EXIT = "processExit";
   
    public static final String ACTION_MODIFY_KARTE = "modifyKarte";
    public static final String ACTION_UNDO = "undo";
    public static final String ACTION_REDO = "redo";
    public static final String ACTION_CUT = "cut";
    public static final String ACTION_COPY = "copy";
    public static final String ACTION_PASTE = "paste";

    // 元町皮ふ科
    public static final String ACTION_SEND_CLAIM = "sendClaim";

    public static final String ACTION_ASCENDING = "ascending";
    public static final String ACTION_DESCENDING = "descending";
    public static final String ACTION_SHOW_MODIFIED = "showModified";
    public static final String ACTION_SET_KARTE_ENVIROMENT = "setKarteEnviroment";
    
    public static final String ACTION_INSERT_DISEASE = "insertDisease";
    public static final String ACTION_INSERT_TEXT = "insertText";
    public static final String ACTION_INSERT_SCHEMA = "insertSchema";
    public static final String ACTION_INSERT_STAMP = "insertStamp";
    public static final String ACTION_SELECT_INSURANCE = "selectInsurance";
    public static final String ACTION_CHANGE_NUM_OF_DATES_ALL = "changeNumOfDatesAll";
    
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
    
    public static final String ACTION_RESET_STYLE = "resetStyle";
    
    public static final String ACTION_SHOW_STAMPBOX = "showStampBox";
    public static final String ACTION_NEW_PATIENT = "addNewPatient";
    public static final String ACTION_SHOW_SCHEMABOX = "showSchemaBox";
    public static final String ACTION_CHANGE_PASSWORD = "changePassword";
    public static final String ACTION_ADD_USER = "addUser";
    public static final String ACTION_CONFIRM_RUN = "confirmRun";
    public static final String ACTION_SOFTWARE_UPDATE = "update1";

    public static final String ACTION_BROWS_DOLPHIN = "browseDolphinSupport";
    public static final String ACTION_BROWS_DOLPHIN_PROJECT = "browseDolphinProject";
    public static final String ACTION_BROWS_MEDXML = "browseMedXml";
    public static final String ACTION_SHOW_ABOUT = "showAbout";
    
    // Role
    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_USER = "user";
    
    // 環境設定後のサービス開始停止関連
    public static final String KEY_PVT_SERVER = "pvtServer";
    public static final String KEY_SEND_CLAIM = "sendClaim";
    public static final String KEY_SEND_MML = "sendMml";
    public static final String SERVICE_RUNNING = "running";
    public static final String SERVICE_NOT_RUNNING = "notRunning";
    public static final String ADDRESS_CLAIM = "claimAddress";
    public static final String CSGW_PATH = "csgwPath";

    //
    // order package で使用する定数
    //
    public static final int DEFAULT_CMP_V_SPACE = 11;
    
    public static final int DEFAULT_STAMP_EDITOR_WIDTH  = 700;
    public static final int DEFAULT_STAMP_EDITOR_HEIGHT = 690;
    public static final Dimension DEFAULT_STAMP_EDITOR_SIZE = new Dimension(DEFAULT_STAMP_EDITOR_WIDTH, DEFAULT_STAMP_EDITOR_HEIGHT);
    
    public static final int DEFAULT_EDITOR_WIDTH 	= 680;  //724
    public static final int DEFAULT_EDITOR_HEIGHT 	= 230;    
    
}
