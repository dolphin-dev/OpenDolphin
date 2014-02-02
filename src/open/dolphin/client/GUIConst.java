package open.dolphin.client;

import java.awt.Dimension;

public class GUIConst {
    
    //
    // client package で使用する定数
    //
    // メニュー関連
    public static final String ACTION_WINDOW_CLOSING = "processWindowClosing";
    public static final String PRINTER_SETUP = "printerSetup";
    public static final String SHOW_ABOUT = "showAbout";
    public static final String EXIT = "exit";
    public static final String CHANGE_PASSWORD = "changePassword";
    public static final String ADD_USER = "addUser";
    public static final String UPDATE_SOFTWARE = "update1";
    public static final String BROWSE_DOLPHIN_SUPPORT = "browseDolphinSupport";
    public static final String BROWSE_DOLPHIN_PROJECT = "browseDolphinProject";
    public static final String BROWSE_MEDXML = "browseMedXml";
    public static final String SET_KARTE_ENV = "setKarteEnviroment";
    public static final String SHOW_STAMP_BOX = "showStampBox";
    public static final String SHOW_SCHEMA_BOX = "showSchemaBox";
    public static final String MENU_TEXT = "テキスト";
    public static final String MENU_SCHEMA = "シェーマ";
    public static final String MENU_STAMP = "スタンプ";
    public static final String MENU_INSERT = "挿 入";
    public static final String MENU_INSURANCE = "保険選択";
    public static final String ACTION_SIZE = "size";
    public static final String ACTION_STYLE = "style";
    public static final String ACTION_ALIGNMENT = "alignment";
    public static final String ACTION_COLOR = "color";
    public static final String ACTION_RESET_STYLE = "resetStyle";
    public static final String ACTION_RED = "redAction";
    public static final String ACTION_ORANGE = "orangeAction";
    public static final String ACTION_YELLOW = "yellowAction";
    public static final String ACTION_GREEN = "greenAction";
    public static final String ACTION_BLUE = "blueAction";
    public static final String ACTION_PURPLE = "purpleAction";
    public static final String ACTION_GRAY = "grayAction";
    public static final String ACTION_S9 = "s9Action";
    public static final String ACTION_S10 = "s10Action";
    public static final String ACTION_S12 = "s12Action";
    public static final String ACTION_S14 = "s14Action";
    public static final String ACTION_S18 = "s18Action";
    public static final String ACTION_S24 = "s24Action";
    public static final String ACTION_S36 = "s36Action";
    public static final String ACTION_BOLD = "boldAction";
    public static final String ACTION_ITALIC = "italicAction";
    public static final String ACTION_UNDERLINE = "underlineAction";
    public static final String ACTION_LEFT_ALIGN = "leftAlignmentAction";
    public static final String ACTION_CENTER_ALIGN = "centerAlignmentAction";
    public static final String ACTION_RIGHT_ALIGN = "rightAlignmentAction";
    public static final String ACTION_NEW_KARTE = "newKarte";
    public static final String ACTION_SAVE = "save";
    public static final String ACTION_DELETE_KARTE = "delete";
    public static final String ACTION_PRINT = "print";
    public static final String ACTION_CUT = "cut";
    public static final String ACTION_COPY = "copy";
    public static final String ACTION_PASTE = "paste";
    public static final String ACTION_UNDO = "undo";
    public static final String ACTION_REDO = "redo";
    public static final String ACTION_MODIFY_KARTE = "modifyKarte";
    public static final String ACTION_ASCENDING = "ascending";
    public static final String ACTION_DESCENDING = "descending";
    public static final String ACTION_SHOW_MODIFIED = "showModified";
    public static final String ACTION_INSERT_TEXT = "insertText";
    public static final String ACTION_INSERT_SCHEMA = "insertSchema";
    public static final String ACTION_INSERT_STAMP = "insertStamp";
    public static final String ACTION_SELECT_INSURANCE = "selectInsurance";
    public static final String TOOLTIPS_INSERT_TEXT = "テキストスタンプを挿入します。";
    public static final String TOOLTIPS_INSERT_SCHEMA = "シェーマを挿入します。";
    public static final String TOOLTIPS_INSERT_STAMP = "オーダスタンプを挿入します。";
    public static final String TOOLTIPS_SELECT_INSURANCE = "保険を選択します。";
//    public static final String MENU_PRINTER_SETUP = "ページ設定...";
//    public static final String MENU_ABOUT = "アバウト...";
//    public static final String MENU_EXIT = "終了";
//    public static final String MENU_CHANGE_PASSWORD = "パスワード変更...";
//    public static final String MENU_ADD_USER = "ユーザ登録...";
//    public static final String MENU_UPDATE_SOFTWARE = "アップデート確認...";
//    public static final String MENU_DOLPHIN_SUPPORT = "ドルフィンサポート";
//    public static final String MENU_DOLPHIN_PROJECT = "ドルフィンプロジェクト";
//    public static final String MENU_MEDXML = "MedXMLコンソーシアム";
//    public static final String MENU_SET_KARTE_ENV = "環境設定";
//    public static final String MENU_STAMP_BOX = "スタンプボックス";
//    public static final String MENU_SCHEMA_BOX = "シェーマボックス";
//    public static final String MENU_SIZE = "サイズ";
//    public static final String MENU_STYLE = "スタイル";
//    public static final String MENU_TEXT_ALIGN = "行揃え";
//    public static final String MENU_COLOR = "カラー";
//    public static final String MENU_RED = "レッド";
//    public static final String MENU_ORANGE = "オレンジ";
//    public static final String MENU_YELLOW = "イェロー";
//    public static final String MENU_GREEN = "グリーン";
//    public static final String MENU_BLUE = "ブルー";
//    public static final String MENU_PURPLE = "パープル";
//    public static final String MENU_GRAY = "グレー";
//    public static final String MENU_LEFT_ALIGN = "左揃え";
//    public static final String MENU_CENTER_ALIGN = "中央揃え";
//    public static final String MENU_RIGHT_ALIGN = "右揃え";

    
    // JNDI
    public static final String JNDI_STAMP_BOX = "mainWindow/stampBox";
    public static final String JNDI_SCHEMA_BOX = "mainWindow/schemaBox";
    public static final String JNDI_SEND_CLAIM = "karteEditor/sendClaim";
    public static final String JNDI_SEND_MML = "karteEditor/sendMml";
    public static final String JNDI_WATING_LIST = "mainWindow/comp/watingList";
    public static final String JNDI_CHART = "mainWindow/chart";
    public static final String JNDI_MENUBAR_BUILDER = "helper/menuBarBuilder";
    public static final String JNDI_CHANGE_PASSWORD = "mainWindow/menu/system/changePassword";
    public static final String JNDI_ADD_USER = "mainWindow/menu/system/addUser";
    public static final String JNDI_STAMP_PUBLISH = "mainWindow/menu/system/stampPublish";
    
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
