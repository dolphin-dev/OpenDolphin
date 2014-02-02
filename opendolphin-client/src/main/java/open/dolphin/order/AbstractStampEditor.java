package open.dolphin.order;

import java.awt.Window;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import open.dolphin.client.ClientContext;
import open.dolphin.dao.SqlDaoFactory;
import open.dolphin.dao.SqlMasterDao;
import open.dolphin.infomodel.ClaimConst;
import open.dolphin.infomodel.ClaimItem;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.TensuMaster;
import open.dolphin.project.Project;
import open.dolphin.util.ZenkakuUtils;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public abstract class AbstractStampEditor {

    public static final String VALUE_PROP = "value";
    public static final String VALIDA_DATA_PROP = "validData";
    public static final String EMPTY_DATA_PROP = "emptyData";
    public static final String EDIT_END_PROP = "editEnd";
    public static final String CURRENT_SHINKU_PROP = "currentShinkuProp";

    protected static final String DEFAULT_NUMBER = "1";

    protected static final String DEFAULT_STAMP_NAME     = "新規スタンプ";
    protected static final String FROM_EDITOR_STAMP_NAME = "エディタから";

    protected static final String[] MED_COST_FLGAS = {"廃","金","都","","","","","減","不"};
    protected static final String[] TOOL_COST_FLGAS = {"廃","金","都","","","%加","","","","乗"};
    protected static final String[] TREAT_COST_FLGAS = {"廃","金","","+点","都","%加","%減","減","-点"};
    protected static final String[] IN_OUT_FLAGS = {"入外","入","外"};
    protected static final String[] HOSPITAL_CLINIC_FLAGS = {"病診","病","診"};
    protected static final String[] OLD_FLAGS = {"社老","社","老"};

    protected static final String ADMIN_MARK = "[用法] ";
    protected static final String REG_ADMIN_MARK = "\\[用法\\] ";

    protected static final int START_NUM_ROWS = 20;
    
    // 組み合わせができるマスター項目
    protected static final String REG_BASE_CHARGE           = "[手そ]";
    protected static final String REG_INSTRACTION_CHARGE    = "[手そ薬材]";     // 在宅で薬剤、材料を追加
    protected static final String REG_MED_ORDER             = "[薬用材そ]";     // 保険適用外の医薬品等追加
    protected static final String REG_INJECTION_ORDER       = "[手そ注材]";
    protected static final String REG_TREATMENT             = "[手そ薬材]";
    protected static final String REG_SURGERY_ORDER         = "[手そ薬材]";
    protected static final String REG_BACTERIA_ORDER        = "[手そ薬材]";
    protected static final String REG_PHYSIOLOGY_ORDER      = "[手そ薬材]";
    protected static final String REG_LABO_TEST             = "[手そ薬材]";
    protected static final String REG_RADIOLOGY_ORDER       = "[手そ薬材部]";
    protected static final String REG_OTHER_ORDER           = "[手そ薬材]";
    protected static final String REG_GENERAL_ORDER         = "[手そ薬材用部]";

    // セットできる診療行為区分
    protected static final String SHIN_BASE_CHARGE           = "^(11|12)";
    protected static final String SHIN_INSTRACTION_CHARGE    = "^(13|14)";
    protected static final String SHIN_MED_ORDER             = "";              // 210|220|230
    protected static final String SHIN_INJECTION_ORDER       = "^3";            // 310|320|330
    protected static final String SHIN_TREATMENT             = "^4";
    protected static final String SHIN_SURGERY_ORDER         = "^5";
    protected static final String SHIN_BACTERIA_ORDER        = "^6";
    protected static final String SHIN_PHYSIOLOGY_ORDER      = "^6";
    protected static final String SHIN_LABO_TEST             = "^6";
    protected static final String SHIN_RADIOLOGY_ORDER       = "^7";
    protected static final String SHIN_OTHER_ORDER           = "^8";
    protected static final String SHIN_GENERAL_ORDER         = "\\d";

    // エディタに表示する名前
    protected static final String NAME_BASE_CHARGE           = "診断料";
    protected static final String NAME_INSTRACTION_CHARGE    = "管理料 ";       // 指導・在宅
    protected static final String NAME_MED_ORDER             = "処 方";
    protected static final String NAME_INJECTION_ORDER       = "注 射";
    protected static final String NAME_TREATMENT             = "処 置";
    protected static final String NAME_SURGERY_ORDER         = "手 術";
    protected static final String NAME_BACTERIA_ORDER        = "細菌検査";
    protected static final String NAME_PHYSIOLOGY_ORDER      = "生理・内視鏡検査";
    protected static final String NAME_LABO_TEST             = "検体検査";
    protected static final String NAME_RADIOLOGY_ORDER       = "放射線";
    protected static final String NAME_OTHER_ORDER           = "その他";
    protected static final String NAME_GENERAL_ORDER         = "汎 用";

    // 暗黙の診療行為区分
    protected static final String IMPLIED_BASE_CHARGE           = "";
    protected static final String IMPLIED_INSTRACTION_CHARGE    = "";
    protected static final String IMPLIED_MED_ORDER             = "";
    protected static final String IMPLIED_INJECTION_ORDER       = "";
    protected static final String IMPLIED_TREATMENT             = "400";
    protected static final String IMPLIED_SURGERY_ORDER         = "";
    protected static final String IMPLIED_BACTERIA_ORDER        = "600";
    protected static final String IMPLIED_PHYSIOLOGY_ORDER      = "600";
    protected static final String IMPLIED_LABO_TEST             = "600";
    protected static final String IMPLIED_RADIOLOGY_ORDER       = "700";
    protected static final String IMPLIED_OTHER_ORDER           = "800";
    protected static final String IMPLIED_GENERAL_ORDER         = "";

    // 情報
    protected static final String INFO_BASE_CHARGE           = "診断料（診区=110-120）";
    protected static final String INFO_INSTRACTION_CHARGE    = "管理料（診区=130-140）";
    protected static final String INFO_MED_ORDER             = "処 方";
    protected static final String INFO_INJECTION_ORDER       = "注 射（診区=300）";
    protected static final String INFO_TREATMENT             = "処 置（診区=400）";
    protected static final String INFO_SURGERY_ORDER         = "手 術（診区=500）";
    protected static final String INFO_BACTERIA_ORDER        = "細菌検査（診区=600）";
    protected static final String INFO_PHYSIOLOGY_ORDER      = "生理・内視鏡検査（診区=600）";
    protected static final String INFO_LABO_TEST             = "検体検査（診区=600）";
    protected static final String INFO_RADIOLOGY_ORDER       = "放射線（診区=700）";
    protected static final String INFO_OTHER_ORDER           = "その他（診区=800）";
    protected static final String INFO_GENERAL_ORDER         = "汎 用（診区=100-999）";

    // 病名
    protected static final String NAME_DIAGNOSIS             = "傷病名";
    protected static final String REG_DIAGNOSIS              = "[手そ薬材用部]";

    // 辞書のキー
    protected static final String KEY_ORDER_NAME    = "orderName";
    protected static final String KEY_PASS_REGEXP   = "passRegExp";
    protected static final String KEY_SHIN_REGEXP   = "shinkuRegExp";
    protected static final String KEY_INFO          = "info";
    protected static final String KEY_IMPLIED       = "implied007";

    // 編集可能コメント
    protected static final String EDITABLE_COMMENT_81 = "81";;   //"810000001";
    protected static final String EDITABLE_COMMENT_0081 = "0081";
    protected static final String EDITABLE_COMMENT_83 = "83";
    protected static final String EDITABLE_COMMENT_0083 = "0083";
    protected static final String EDITABLE_COMMENT_84 = "84";
    protected static final String EDITABLE_COMMENT_0084 = "0084";
    protected static final String EDITABLE_COMMENT_85 = "85";
    protected static final String EDITABLE_COMMENT_0085 = "0085";  //"008500000";

    // 検索特殊記号文字
    protected static final String ASTERISK_HALF = "*";
    protected static final String ASTERISK_FULL = "＊";
    protected static final String TENSU_SEARCH_HALF = "///";
    protected static final String TENSU_SEARCH_FULL = "／／／";
    protected static final String COMMENT_SEARCH_HALF = "8";
    protected static final String COMMENT_SEARCH_FULL = "８";
    protected static final String COMMENT_85_SEARCH_HALF = "85";
    protected static final String COMMENT_85_SEARCH_FULL = "８５";

    // 検索タイプ
    protected static final int TT_INVALID       = -1;
    protected static final int TT_LIST_TECH     = 0;
    protected static final int TT_TENSU_SEARCH  = 1;
    protected static final int TT_85_SEARCH     = 2;
    protected static final int TT_CODE_SEARCH   = 3;
    protected static final int TT_LETTER_SEARCH = 4;
    protected static final int TT_SHINKU_SERACH = 5;
    
    // Editor button
    protected static final String STAMP_EDITOR_BUTTON_TYPE = "stamp.editor.buttonType";
    protected static final String BUTTON_TYPE_IS_ICON = "icon";
    protected static final String BUTTON_TYPE_IS_ITEXT = "text";

    // ORCA 有効期限用のDF
    protected static SimpleDateFormat effectiveFormat = new SimpleDateFormat("yyyyMMdd");

    // ドルフィンのオーダ履歴用の名前
    protected String orderName;

    // ClaimBundle に設定する 診療行為区分 400,500,600 .. etc
    protected String classCode;

    // 診療行為区分定義のテーブルID == Claim007
    protected String classCodeId    = "Claim007";

    // ClaimItem (項目) の種別を定義しているテーブルID = Claim003
    protected String subclassCodeId = "Claim003";

    // このエディタのエンティティ
    protected String entity;
    
    // このエディタで組合わせが可能な点数マスタ項目の正規表現
    protected Pattern passPattern;

    // このエディタの診区正規表現パターン
    protected String shinkuRegExp;
    protected Pattern shinkuPattern;

    // このエディタの情報
    private String info;

    protected String implied007;

    protected JTextField searchTextField;

    protected JTextField countField;

    // 通知用の束縛サポート
    protected PropertyChangeSupport boundSupport;

    // マスタ検索用の便利オブジェクト
    protected SqlMasterDao dao;
    protected String now;

    // セットの有効性を制御する便利フラグ
    protected Boolean setIsEmpty;
    protected Boolean setIsValid;

    // StampEditor から起動された時 true
    // StampMaker から起動された時は false
    private Boolean fromStampEditor;


    /**
     * Entity からマスタ検索に必要な正規表現を生成する。
     * @param entity エンティティ
     * @return 正規表現を格納した Hashtable
     */
    public static HashMap<String, String> getEditorSpec(String entity) {

        HashMap<String, String> ht = new HashMap<String, String>(10, 0.75f);
        String orderName = null;
        String passRegExp = null;
        String shinkuRegExp = null;
        String implied007 = null;
        String info = null;

        if (entity.equals(IInfoModel.ENTITY_BASE_CHARGE_ORDER)) {

            orderName = NAME_BASE_CHARGE;
            passRegExp = REG_BASE_CHARGE;
            shinkuRegExp = SHIN_BASE_CHARGE;
            info = INFO_BASE_CHARGE;


        } else if (entity.equals(IInfoModel.ENTITY_INSTRACTION_CHARGE_ORDER)) {

            orderName = NAME_INSTRACTION_CHARGE;
            passRegExp = REG_INSTRACTION_CHARGE;
            shinkuRegExp = SHIN_INSTRACTION_CHARGE;
            info = INFO_INSTRACTION_CHARGE;


        } else if (entity.equals(IInfoModel.ENTITY_MED_ORDER)) {

            orderName = NAME_MED_ORDER;
            passRegExp = REG_MED_ORDER;                     // 薬剤、用法、材料、その他(保険適用外医薬品）
            info = INFO_MED_ORDER;

        } else if (entity.equals(IInfoModel.ENTITY_INJECTION_ORDER)) {

            orderName = NAME_INJECTION_ORDER;
            passRegExp = REG_INJECTION_ORDER;               // 手技、その他、注射薬、材料
            shinkuRegExp = SHIN_INJECTION_ORDER;
            info = INFO_INJECTION_ORDER;


        } else if (entity.equals(IInfoModel.ENTITY_TREATMENT)) {

            orderName = NAME_TREATMENT;
            passRegExp = REG_TREATMENT;                     // 手技、その他、薬剤、材料
            shinkuRegExp = SHIN_TREATMENT;
            implied007 = IMPLIED_TREATMENT;
            info = INFO_TREATMENT;


        } else if (entity.equals(IInfoModel.ENTITY_SURGERY_ORDER)) {

            orderName = NAME_SURGERY_ORDER;
            passRegExp = REG_SURGERY_ORDER;                 // 手技、その他、薬剤、材料
            shinkuRegExp = SHIN_SURGERY_ORDER;
            info = INFO_SURGERY_ORDER;


        } else if (entity.equals(IInfoModel.ENTITY_BACTERIA_ORDER)) {

            orderName = NAME_BACTERIA_ORDER;
            passRegExp = REG_BACTERIA_ORDER;                // 手技、その他、薬剤、材料
            shinkuRegExp = SHIN_BACTERIA_ORDER;
            implied007 = IMPLIED_BACTERIA_ORDER;
            info = INFO_BACTERIA_ORDER;

        } else if (entity.equals(IInfoModel.ENTITY_PHYSIOLOGY_ORDER)) {

            orderName = NAME_PHYSIOLOGY_ORDER;
            passRegExp = REG_PHYSIOLOGY_ORDER;              // 手技、その他、薬剤、材料
            shinkuRegExp = SHIN_PHYSIOLOGY_ORDER;
            implied007 = IMPLIED_PHYSIOLOGY_ORDER;
            info = INFO_PHYSIOLOGY_ORDER;


        } else if (entity.equals(IInfoModel.ENTITY_LABO_TEST)) {

            orderName = NAME_LABO_TEST;
            passRegExp = REG_LABO_TEST;                     // 手技、その他、薬剤、材料
            shinkuRegExp = SHIN_LABO_TEST;
            implied007 = IMPLIED_LABO_TEST;
            info = INFO_LABO_TEST;


        } else if (entity.equals(IInfoModel.ENTITY_RADIOLOGY_ORDER)) {

            orderName = NAME_RADIOLOGY_ORDER;
            passRegExp = REG_RADIOLOGY_ORDER;               // 手技、その他、薬剤、材料、部位
            shinkuRegExp = SHIN_RADIOLOGY_ORDER;
            implied007 = IMPLIED_RADIOLOGY_ORDER;
            info = INFO_RADIOLOGY_ORDER;


        }   else if (entity.equals(IInfoModel.ENTITY_OTHER_ORDER)) {

            orderName = NAME_OTHER_ORDER;
            passRegExp = REG_OTHER_ORDER;                   // 手技、その他、薬剤、材料
            shinkuRegExp = SHIN_OTHER_ORDER;
            implied007 = IMPLIED_OTHER_ORDER;
            info = INFO_OTHER_ORDER;


        } else if (entity.equals(IInfoModel.ENTITY_GENERAL_ORDER)) {

            orderName = NAME_GENERAL_ORDER;
            passRegExp = REG_GENERAL_ORDER;                 // 手技、その他、薬剤、材料、用法、部位
            shinkuRegExp = SHIN_GENERAL_ORDER;
            info = INFO_GENERAL_ORDER;

        } else if (entity.equals(IInfoModel.ENTITY_DIAGNOSIS)) {

            orderName = NAME_DIAGNOSIS;
            passRegExp = REG_DIAGNOSIS;
        }

        ht.put(KEY_ORDER_NAME, orderName);

        if (passRegExp!=null) {
            ht.put(KEY_PASS_REGEXP, passRegExp);
        }
        
        if (shinkuRegExp!=null) {
            ht.put(KEY_SHIN_REGEXP, shinkuRegExp);
        }

        if (info!=null) {
            ht.put(KEY_INFO, info);
        }

        if (implied007!=null) {
            ht.put(KEY_IMPLIED, implied007);
        }

        return ht;
    }

    protected static boolean isCode(String text) {
        boolean maybe = true;
        if (text != null) {
            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                int type = Character.getType(c);
                if (type == Character.DECIMAL_DIGIT_NUMBER) {
                    continue;
                } else {
                    maybe = false;
                    break;
                }
            }
            return maybe;
        }
        return false;
    }

    //-----------------------------------------
    // 点数で検索する場合の入力 =  ///11 etc
    //-----------------------------------------
    protected static boolean isTensuSearch(String text) {
        boolean maybe = (text != null && text.length() > 3 && (text.startsWith(TENSU_SEARCH_HALF) || text.startsWith(TENSU_SEARCH_FULL)))
                      ? true
                      : false;
        return maybe;
    }

    //---------------------------------------
    // 内容を編集できるコメントコード
    //---------------------------------------
    protected static boolean isNameEditableComment(String code) {

        boolean ans = true;
        ans = ans && (code!=null);

        if (!ans) {
            return ans;
        }

        boolean ed = (code.startsWith(EDITABLE_COMMENT_81) ||
                      code.startsWith(EDITABLE_COMMENT_0081) ||
                      code.startsWith(EDITABLE_COMMENT_83) ||
                      code.startsWith(EDITABLE_COMMENT_0083) ||
                      code.startsWith(EDITABLE_COMMENT_85) ||
                      code.startsWith(EDITABLE_COMMENT_0085));

        return ed;
    }

    //---------------------------------------
    protected static boolean isNumberEditableComment(String code) {

        boolean ans = true;
        ans = ans && (code!=null);

        if (!ans) {
            return ans;
        }

        boolean ed = (code.startsWith(EDITABLE_COMMENT_84) ||
                      code.startsWith(EDITABLE_COMMENT_0084));

        return ed;
    }

    protected static boolean is82Comment(String code) {
        if (code==null) {
            return false;
        }
        boolean ans = true;
        ans = ans && (code.startsWith("82") ||
                      code.startsWith("0082") ||
                      code.startsWith("８２") ||
                      code.startsWith("００８２"));
        return ans;
    }

    //-----------------------------------------
    // 入力されたテキストから検索タイプを返す
    //-----------------------------------------
    protected static int getSearchType(String test, boolean hitReturn) {

        if (test == null || test.equals("")) {
            return TT_INVALID;
        }

        // *
        if (test.equals(ASTERISK_HALF) || test.equals(ASTERISK_FULL)) {
            return TT_LIST_TECH;
        }

        //  ///12
        if (test.startsWith(TENSU_SEARCH_HALF) || test.startsWith(TENSU_SEARCH_FULL)) {

            if (isTensuSearch(test) && hitReturn) {
                return TT_TENSU_SEARCH;
            } else {
                return TT_INVALID;
            }
        }

        // 81,82,83,84,85
        if ( (test.startsWith(COMMENT_SEARCH_HALF) || test.startsWith(COMMENT_SEARCH_FULL)) && test.length() >1 ) {
//            if (test.startsWith(COMMENT_85_SEARCH_HALF) || test.startsWith(COMMENT_85_SEARCH_FULL)) {
//                return TT_85_SEARCH;
//            } else {
//                return TT_CODE_SEARCH;
//            }
            return TT_CODE_SEARCH;
        }

        // .140 診療行為区分検索
        if ((test.startsWith(".") || test.startsWith("．")) && test.length()==4) {
            return TT_SHINKU_SERACH;
        }

        boolean textIsCode = isCode(test);

        // 6桁以上のコード
        if (textIsCode && test.length() > 5) {
            return TT_CODE_SEARCH;
        }

        // ２文字以上
        if ((!textIsCode) && test.length() > 1) {
            return TT_LETTER_SEARCH;
        }

        // １文字でreturn確定
        if ((!textIsCode) && hitReturn) {
            return TT_LETTER_SEARCH;
        }

        return TT_INVALID;
    }

    // 注射診区コード
    protected static boolean isInjection(String code) {
        return (code != null &&
                (code.startsWith("31") || code.startsWith("32") || code.startsWith("33"))) ? true : false;
    }
    
    // Editor Button Type
    protected boolean editorButtonTypeIsIcon() {
        String prop = Project.getString(STAMP_EDITOR_BUTTON_TYPE);
        return prop.equals(BUTTON_TYPE_IS_ICON) ? true : false;
    }

    public abstract JPanel getView();

    public abstract Object getValue();

    public abstract void setValue(Object theStamp);

    public void dispose() {

        if (searchTextField != null) {
            searchTextField.setText("");
        }

        if (countField != null) {
            countField.setText("");
        }
    }

    /**
     * validDataProp と emptyDataPropの通知を行う。
     */
    protected void checkValidation() {
        if (boundSupport != null) {
            boundSupport.firePropertyChange(EMPTY_DATA_PROP, new Boolean(!setIsEmpty), new Boolean(setIsEmpty));
            boundSupport.firePropertyChange(VALIDA_DATA_PROP, new Boolean(!setIsValid), new Boolean(setIsValid));
        }
    }
    
    /**
     * セットテーブルのMasterItemからClaimItemを生成する。
     * @param masterItem セットテーブルの行オブジェクト
     * @return ClaimItem
     */
    protected ClaimItem masterToClaimItem(MasterItem masterItem) {

        ClaimItem ret = new ClaimItem();

        // コード
        ret.setCode(masterItem.getCode());

        // 名称
        ret.setName(masterItem.getName());

        // subclassCode(手技|薬剤|材料|部位|用法|その他)
        ret.setClassCode(String.valueOf(masterItem.getClassCode()));

        // Claim003
        ret.setClassCodeSystem(ClaimConst.SUBCLASS_CODE_ID);

        // 数量
        String number = trimToNullIfEmpty(masterItem.getNumber());
        if (number != null) {
            number = ZenkakuUtils.toHalfNumber(number);
            ret.setNumber(number);
            ret.setNumberCode(getNumberCode(masterItem.getClassCode()));
            ret.setNumberCodeSystem(ClaimConst.NUMBER_CODE_ID);
        }
        //System.err.println(number);

        // 単位
        String unit = trimToNullIfEmpty(masterItem.getUnit());
        if (unit != null) {
            ret.setUnit(unit);
        }

        // YKZ knb
        ret.setYkzKbn(masterItem.getYkzKbn());

        return ret;
    }

    /**
     * ClaimItemをセットテーブルの行MasterItemへ変換する。
     * @param claimItem ClaimItem
     * @return  MasterItem
     */
    protected MasterItem claimToMasterItem(ClaimItem claimItem) {

        MasterItem ret = new MasterItem();

        // Code
        ret.setCode(claimItem.getCode());

        // Name
        ret.setName(claimItem.getName());

        // 手技・材料・薬品のフラグ
        String test = trimToNullIfEmpty(claimItem.getClassCode());
        if (test != null ) {
            ret.setClassCode(Integer.parseInt(test));
        }

        // 数量
        test = trimToNullIfEmpty(claimItem.getNumber());
        if (test != null) {
            test = ZenkakuUtils.toHalfNumber(test.trim());
            ret.setNumber(test);
        }

        // 単位
        test = trimToNullIfEmpty(claimItem.getUnit());
        if (test != null) {
            ret.setUnit(test.trim());
        }

        // YKZ kbn
        ret.setYkzKbn(claimItem.getYkzKbn());
        
        return ret;
    }

    /**
     * 点数マスタからMasterItemを生成する。
     * @param tm 点数マスタ
     * @return MasterItem
     */
    protected MasterItem tensuToMasterItem(TensuMaster tm) {

        MasterItem ret = new MasterItem();

        // code
        ret.setCode(tm.getSrycd());

        // name
        ret.setName(tm.getName());

        // unit
        ret.setUnit(trimToNullIfEmpty(tm.getTaniname()));


        // ClaimInterface の　手技、薬剤、器材の別
        // 及び診療行為区分（診区）を設定する
        // 0: 手技  1: 材料  2: 薬剤 3: 用法 4:部位 5:その他
        String test = tm.getSlot();

        if (test.equals(ClaimConst.SLOT_SYUGI)) {

            // 手技
            ret.setClassCode(ClaimConst.SYUGI);

            // 診療行為区分 手技で設定している
            ret.setClaimClassCode(tm.getSrysyukbn());

            // もしかして数量があるかも...
            if (ret.getUnit()!=null) {
                ret.setNumber(DEFAULT_NUMBER);
            }

        } else if (Pattern.compile(ClaimConst.SLOT_MEDICINE).matcher(test).find()) {

            // 薬剤
            ret.setClassCode(ClaimConst.YAKUZAI);

            ret.setYkzKbn(tm.getYkzkbn());
            //System.out.println("剤型区分=" + ret.getYkzKbn());

            String inputNum = DEFAULT_NUMBER;

            if (ret.getUnit()!= null && ret.getUnit().equals(ClaimConst.UNIT_T)) {
                //inputNum = Project.getString("defaultZyozaiNum", "3");
                inputNum = Project.getString("defaultZyozaiNum");

            } else if (ret.getUnit()!= null && ret.getUnit().equals(ClaimConst.UNIT_CAPSULE)) {
                //inputNum = Project.getString("defaultCapsuleNum", "1");   // ?
                //inputNum = Project.getString("defaultZyozaiNum", "3");
                //inputNum = Project.getString("defaultZyozaiNum");
                inputNum = Project.getString("defaultCapsuleNum");

            } else if (ret.getUnit()!= null && ret.getUnit().equals(ClaimConst.UNIT_G)) {
                //inputNum = Project.getString("defaultSanyakuNum", "1.0");
                inputNum = Project.getString("defaultSanyakuNum");

            } else if (ret.getUnit()!= null && ret.getUnit().equals(ClaimConst.UNIT_ML)) {
                //inputNum = Project.getString("defaultMizuyakuNum", "1");
                inputNum = Project.getString("defaultMizuyakuNum");

            } //else if (ret.getUnit().equals(ClaimConst.UNIT_CAPSULE)) {
                //inputNum = Project.getString("defaultKapuselNum", "1");
            //}

            // zero -> null 
            inputNum = (inputNum==null || inputNum.equals("") || inputNum.equals("0")) ? null : inputNum;
            ret.setNumber(inputNum);


        } else if (test.equals(ClaimConst.SLOT_ZAIRYO)) {
            // 材料
            ret.setClassCode(ClaimConst.ZAIRYO);
            ret.setNumber(DEFAULT_NUMBER);

        } else if (test.equals(ClaimConst.SLOT_YOHO)) {
            // 用法
            ret.setClassCode(ClaimConst.ADMIN);
            ret.setName(ADMIN_MARK + tm.getName());
            ret.setDummy("X");
            ret.setBundleNumber(Project.getString("defaultRpNum", "1"));

        } else if (test.equals(ClaimConst.SLOT_BUI)) {
            // 部位
            ret.setClassCode(ClaimConst.BUI);

        } else if (test.equals(ClaimConst.SLOT_OTHER)) {
            // その他
            ret.setClassCode(ClaimConst.OTHER);
            if (ret.getUnit()!=null) {
                ret.setNumber(DEFAULT_NUMBER);
            }
        }

        return ret;
    }

    protected String trimToNullIfEmpty(String test) {

        if (test == null) {
            return null;
        }

        test = test.trim();

        return test.equals("") ? null : test;
    }

    protected String getClaim007Code(String code) {

        if (code == null) {
            return null;
        }

        if (code.equals(ClaimConst.INJECTION_311)) {
            return ClaimConst.INJECTION_310;

        } else if (code.equals(ClaimConst.INJECTION_321)) {
            return ClaimConst.INJECTION_320;

        } else if (code.equals(ClaimConst.INJECTION_331)) {
            return ClaimConst.INJECTION_330;

        } else {
            // 注射以外のケース
            return code;
        }
    }

    /**
     * マスター検索で選択された点数オブジェクトをセットテーブルへ追加する。
     * @param tm 点数マスタ
     */
    protected abstract void addSelectedTensu(TensuMaster tm);

    /**
     * Returns Claim004 Number Code 21 材料個数 when subclassCode = 1 11
     * 薬剤投与量（１回）when subclassCode = 2
     */
    protected String getNumberCode(int subclassCode) {
        return (subclassCode == 1) ? ClaimConst.ZAIRYO_KOSU : ClaimConst.YAKUZAI_TOYORYO; // 材料個数 : 薬剤投与量１回
        // 2010 ORAC の実装
        //return ClaimConst.YAKUZAI_TOYORYO;
    }

    protected void alertIpAddress() {

        String msg0 = "レセコンのIPアドレスが設定されていないため、マスターを検索できません。";
        String msg1 = "環境設定メニューからレセコンのIPアドレスを設定してください。";
        Object message = new String[]{msg0, msg1};
        Window parent = SwingUtilities.getWindowAncestor(getView());
        String title = ClientContext.getFrameTitle("マスタ検索");
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.WARNING_MESSAGE);
    }

    protected void alertSearchError(String err) {

        String msg0 = "マスターを検索できません。アクセスが許可されているかご確認ください。";
        String msg1 = err;

        Object message = new String[]{msg0, msg1};
        Window parent = SwingUtilities.getWindowAncestor(getView());
        String title = ClientContext.getFrameTitle("マスタ検索");
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.WARNING_MESSAGE);
    }

    protected SqlMasterDao getDao() {

        if (dao == null) {
            dao = (SqlMasterDao) SqlDaoFactory.create("dao.master");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            now = sdf.format(new Date());
        }

        return dao;
    }

    protected Boolean ipOk() {

        // CLAIM(Master) Address が設定されていない場合に警告する
        String address = Project.getString(Project.CLAIM_ADDRESS);
        if (address == null || address.equals("")) {
            alertIpAddress();
            return false;
        }

        return true;
    }

    protected abstract void search(final String text, boolean hitRet);

    protected abstract void clear();

    protected abstract void initComponents();

    public JTextField getSearchTextField() {
        return searchTextField;
    }

    public void addPropertyChangeListener(String prop, PropertyChangeListener listener) {
        if (boundSupport == null) {
            boundSupport = new PropertyChangeSupport(this);
        }
        boundSupport.addPropertyChangeListener(prop, listener);
    }


    public void remopvePropertyChangeListener(String prop, PropertyChangeListener listener) {
        boundSupport.removePropertyChangeListener(prop, listener);
    }

    /**
     * @return the passRegExp
     */
    public Pattern getPassPattern() {
        return passPattern;
    }

    /**
     * @param passRegExp the passRegExp to set
     */
    public final void setPassPattern(Pattern passPattern) {
        this.passPattern = passPattern;
    }

    /**
     * @return the shinkuRegExp
     */
    public String getShinkuRegExp() {
        return shinkuRegExp;
    }

    /**
     * @param shinkuRegExp the shinkuRegExp to set
     */
    public final void setShinkuRegExp(String shinkuRegExp) {
        this.shinkuRegExp = shinkuRegExp;
    }

    /**
     * @return the shinkuRegExp
     */
    public Pattern getShinkuPattern() {
        return shinkuPattern;
    }

    /**
     * @param shinkuRegExp the shinkuRegExp to set
     */
    public final void setShinkuPattern(Pattern shinkuRegExp) {
        this.shinkuPattern = shinkuRegExp;
    }

    /**
     * @return the orderName
     */
    public String getOrderName() {
        return orderName;
    }

    /**
     * @param orderName the orderName to set
     */
    public final void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    /**
     * @return the classCode
     */
    public String getClassCode() {
        return classCode;
    }

    /**
     * @param classCode the classCode to set
     */
    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    /**
     * @return the classCodeId
     */
    public String getClassCodeId() {
        return classCodeId;
    }

    /**
     * @param classCodeId the classCodeId to set
     */
    public void setClassCodeId(String classCodeId) {
        this.classCodeId = classCodeId;
    }

    /**
     * @return the subclassCodeId
     */
    public String getSubclassCodeId() {
        return subclassCodeId;
    }

    /**
     * @param subclassCodeId the subclassCodeId to set
     */
    public void setSubclassCodeId(String subclassCodeId) {
        this.subclassCodeId = subclassCodeId;
    }

    /**
     * @return the entity
     */
    public String getEntity() {
        return entity;
    }

    /**
     * @param entity the entity to set
     */
    public final void setEntity(String entity) {
        this.entity = entity;
    }

    /**
     * @return the info
     */
    public String getInfo() {
        return info;
    }

    /**
     * @param info the info to set
     */
    public final void setInfo(String info) {
        this.info = info;
    }

    public String getImplied007() {
        return implied007;
    }

    public final void setImplied007(String default007) {
        this.implied007 = default007;
    }

    /**
     * @return the fromStampEditor
     */
    public Boolean getFromStampEditor() {
        return fromStampEditor;
    }

    /**
     * @param fromStampEditor the fromStampEditor to set
     */
    public final void setFromStampEditor(Boolean fromStampEditor) {
        this.fromStampEditor = fromStampEditor;
    }

    public AbstractStampEditor() {
        this.initComponents();
    }

    public AbstractStampEditor(String entity) {
        this(entity,true);
    }

    public AbstractStampEditor(String entity, boolean mode) {

        HashMap<String, String> ht = AbstractStampEditor.getEditorSpec(entity);

        this.setEntity(entity);
        this.setOrderName(ht.get(KEY_ORDER_NAME));

        if (ht.get(KEY_PASS_REGEXP)!=null) {
            this.setPassPattern(Pattern.compile(ht.get(KEY_PASS_REGEXP)));
        }
        
        if (ht.get(KEY_SHIN_REGEXP)!=null) {
            this.setShinkuRegExp(ht.get(KEY_SHIN_REGEXP));
            this.setShinkuPattern(Pattern.compile(ht.get(KEY_SHIN_REGEXP)));
        }

        if (ht.get(KEY_INFO)!=null) {
            this.setInfo(ht.get(KEY_INFO));
        }

        if (ht.get(KEY_IMPLIED)!=null) {
            this.setImplied007(ht.get(KEY_IMPLIED));
        }

        setFromStampEditor(mode);

        initComponents();
    }
}
