package open.dolphin.order;

import java.awt.Window;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import open.dolphin.client.ClientContext;
import open.dolphin.client.StampHolder;
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

    // 検索タイプ
    protected static final int TT_INVALID       = -1;
    protected static final int TT_LIST_TECH     = 0;
    protected static final int TT_TENSU_SEARCH  = 1;
    protected static final int TT_CODE_SEARCH   = 2;
    protected static final int TT_LETTER_SEARCH = 3;
    protected static final int TT_SHINKU_SERACH = 4;
    
    // Editor button
    protected static final String STAMP_EDITOR_BUTTON_TYPE = "stamp.editor.buttonType";
    protected static final String BUTTON_TYPE_IS_ICON = "icon";
    protected static final String BUTTON_TYPE_IS_ITEXT = "text";

    // ドルフィンのオーダ履歴用の名前
    protected String orderName;

    // ClaimBundle に設定する 診療行為区分 400,500,600 .. etc
    protected String classCode;

    // 診療行為区分定義のテーブルID == Claim007
    protected String classCodeId    = ClaimConst.CLASS_CODE_ID;

    // ClaimItem (項目) の種別を定義しているテーブルID = Claim003
    protected String subclassCodeId = ClaimConst.SUBCLASS_CODE_ID;

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

    // セットの有効性を制御する便利フラグ
    protected Boolean setIsEmpty;
    protected Boolean setIsValid;

    // StampEditor から起動された時 true
    // StampMaker から起動された時は false
    private Boolean fromStampEditor;
        
    protected boolean modifyFromStampHolder;

    /**
     * Entity からマスタ検索に必要な正規表現を生成する。
     * @param entity エンティティ
     * @return 正規表現を格納した Hashtable
     */
    public final HashMap<String, String> getEditorSpec(String entity) {

        HashMap<String, String> ht = new HashMap<>(10, 0.75f);
        String orderName_ = null;
        String passRegExp_ = null;
        String shinkuRegExp_ = null;
        String implied007_ = null;
        String info_ = null;
        
        ResourceBundle bdl = ClientContext.getClaimBundle();

        if (entity.equals(IInfoModel.ENTITY_BASE_CHARGE_ORDER)) {
            // 診断料　初診・
            orderName_ = bdl.getString("NAME_BASE_CHARGE");
            passRegExp_ = bdl.getString("REG_BASE_CHARGE");
            shinkuRegExp_ = bdl.getString("SHIN_BASE_CHARGE");
            info_ = bdl.getString("INFO_BASE_CHARGE");

        } else if (entity.equals(IInfoModel.ENTITY_INSTRACTION_CHARGE_ORDER)) {
            // 指導・在宅
            orderName_ = bdl.getString("NAME_INSTRACTION_CHARGE");
            passRegExp_ = bdl.getString("REG_INSTRACTION_CHARGE");
            shinkuRegExp_ = bdl.getString("SHIN_INSTRACTION_CHARGE");
            info_ = bdl.getString("INFO_INSTRACTION_CHARGE");
            
        } else if (entity.equals(IInfoModel.ENTITY_MED_ORDER)) {
            // 処方: 薬剤、用法、材料、その他(保険適用外医薬品）
            orderName_ = bdl.getString("NAME_MED_ORDER");
            passRegExp_ = bdl.getString("REG_MED_ORDER");
            info_ = bdl.getString("INFO_MED_ORDER");

        } else if (entity.equals(IInfoModel.ENTITY_INJECTION_ORDER)) {
            // 注射: 手技、その他、注射薬、材料
            orderName_ = bdl.getString("NAME_INJECTION_ORDER");
            passRegExp_ = bdl.getString("REG_INJECTION_ORDER");
            shinkuRegExp_ = bdl.getString("SHIN_INJECTION_ORDER");
            info_ = bdl.getString("INFO_INJECTION_ORDER");

        } else if (entity.equals(IInfoModel.ENTITY_TREATMENT)) {
            // 処置: 手技、その他、薬剤、材料
            orderName_ = bdl.getString("NAME_TREATMENT");
            passRegExp_ = bdl.getString("REG_TREATMENT");
            shinkuRegExp_ = bdl.getString("SHIN_TREATMENT");
            implied007_ = bdl.getString("IMPLIED_TREATMENT");
            info_ = bdl.getString("INFO_TREATMENT");

        } else if (entity.equals(IInfoModel.ENTITY_SURGERY_ORDER)) {
            // 手術: 手技、その他、薬剤、材料
            orderName_ = bdl.getString("NAME_SURGERY_ORDER");
            passRegExp_ = bdl.getString("REG_SURGERY_ORDER");
            shinkuRegExp_ = bdl.getString("SHIN_SURGERY_ORDER");
            info_ = bdl.getString("INFO_SURGERY_ORDER");

        } else if (entity.equals(IInfoModel.ENTITY_BACTERIA_ORDER)) {
            // 細菌検査: 手技、その他、薬剤、材料
            orderName_ = bdl.getString("NAME_BACTERIA_ORDER");
            passRegExp_ = bdl.getString("REG_BACTERIA_ORDER");
            shinkuRegExp_ = bdl.getString("SHIN_BACTERIA_ORDER");
            implied007_ = bdl.getString("IMPLIED_BACTERIA_ORDER");
            info_ = bdl.getString("INFO_BACTERIA_ORDER");

        } else if (entity.equals(IInfoModel.ENTITY_PHYSIOLOGY_ORDER)) {
            // 生体検査: 手技、その他、薬剤、材料
            orderName_ = bdl.getString("NAME_PHYSIOLOGY_ORDER");
            passRegExp_ = bdl.getString("REG_PHYSIOLOGY_ORDER");
            shinkuRegExp_ = bdl.getString("SHIN_PHYSIOLOGY_ORDER");
            implied007_ = bdl.getString("IMPLIED_PHYSIOLOGY_ORDER");
            info_ = bdl.getString("INFO_PHYSIOLOGY_ORDER");

        } else if (entity.equals(IInfoModel.ENTITY_LABO_TEST)) {
            // 検体検査: 手技、その他、薬剤、材料
            orderName_ = bdl.getString("NAME_LABO_TEST");
            passRegExp_ = bdl.getString("REG_LABO_TEST");
            shinkuRegExp_ = bdl.getString("SHIN_LABO_TEST");
            implied007_ = bdl.getString("IMPLIED_LABO_TEST");
            info_ = bdl.getString("INFO_LABO_TEST");

        } else if (entity.equals(IInfoModel.ENTITY_RADIOLOGY_ORDER)) {
            // 画像: 手技、その他、薬剤、材料、部位
            orderName_ = bdl.getString("NAME_RADIOLOGY_ORDER");
            passRegExp_ = bdl.getString("REG_RADIOLOGY_ORDER");
            shinkuRegExp_ = bdl.getString("SHIN_RADIOLOGY_ORDER");
            implied007_ = bdl.getString("IMPLIED_RADIOLOGY_ORDER");
            info_ = bdl.getString("INFO_RADIOLOGY_ORDER");

        }   else if (entity.equals(IInfoModel.ENTITY_OTHER_ORDER)) {
            // その他: 手技、その他、薬剤、材料
            orderName_ = bdl.getString("NAME_OTHER_ORDER");
            passRegExp_ = bdl.getString("REG_OTHER_ORDER");
            shinkuRegExp_ = bdl.getString("SHIN_OTHER_ORDER");
            implied007_ = bdl.getString("IMPLIED_OTHER_ORDER");
            info_ = bdl.getString("INFO_OTHER_ORDER");

        } else if (entity.equals(IInfoModel.ENTITY_GENERAL_ORDER)) {
            // 汎用: 手技、その他、薬剤、材料、用法、部位
            orderName_ = bdl.getString("NAME_GENERAL_ORDER");
            passRegExp_ = bdl.getString("REG_GENERAL_ORDER");
            shinkuRegExp_ = bdl.getString("SHIN_GENERAL_ORDER");
            info_ = bdl.getString("INFO_GENERAL_ORDER");

        } else if (entity.equals(IInfoModel.ENTITY_DIAGNOSIS)) {
            // 傷病名
            orderName_ = bdl.getString("NAME_DIAGNOSIS");
            passRegExp_ = bdl.getString("EG_DIAGNOSIS");
        }

        ht.put(bdl.getString("KEY_ORDER_NAME"), orderName_);

        if (passRegExp_!=null) {
            ht.put(bdl.getString("KEY_PASS_REGEXP"), passRegExp_);
        }
        
        if (shinkuRegExp_!=null) {
            ht.put(bdl.getString("KEY_SHIN_REGEXP"), shinkuRegExp_);
        }

        if (info_!=null) {
            ht.put(bdl.getString("KEY_INFO"), info_);
        }

        if (implied007_!=null) {
            ht.put(bdl.getString("KEY_IMPLIED"), implied007_);
        }

        return ht;
    }

    protected boolean isCode(String text) {
        boolean maybe = true;
        if (text != null) {
            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                int type = Character.getType(c);
                if (type == Character.DECIMAL_DIGIT_NUMBER) {
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
    protected boolean isTensuSearch(String text) {
        ResourceBundle bdl = ClientContext.getClaimBundle();
        String half = bdl.getString("TENSU_SEARCH_HALF");
        String full = bdl.getString("TENSU_SEARCH_FULL");
        boolean maybe = (text != null && 
                         text.length() > 3 && 
                         (text.startsWith(half) || text.startsWith(full)));
        return maybe;
    }

    //---------------------------------------
    // 内容を編集できるコメントコード 81 83 85 86
    //---------------------------------------
    protected boolean isNameEditableComment(String code) {
        if (code==null) {
            return false;
        }
        ResourceBundle bdl = ClientContext.getClaimBundle();
        String[] targets = (String[])bdl.getObject("NAME_EDITABLE_COMMENT");

        boolean ans = false;
        for (String str : targets) {
            if (code.startsWith(str)) {
                ans = true;
                break;
            }
        }
        return ans;
    }

    //---------------------------------------
    // 数量を編集できるコメントコード 84
    //---------------------------------------
    protected boolean isNumberEditableComment(String code) {
        if (code==null) {
            return false;
        }
        ResourceBundle bdl = ClientContext.getClaimBundle();
        String[] targets = (String[])bdl.getObject("NUMBER_EDITABLE_COMMENT");
        
        boolean ans = false;
        for (String str : targets) {
            if (code.startsWith(str)) {
                ans = true;
                break;
            }
        }
        return ans;
    }

    //---------------------------------------
    // 固定コメントコード 82
    //---------------------------------------
    protected boolean is82Comment(String code) {
        if (code==null) {
            return false;
        }
        ResourceBundle bdl = ClientContext.getClaimBundle();
        String[] targets = (String[])bdl.getObject("COMMENT_82");
        
        boolean ans = false;
        for (String str : targets) {
            if (code.startsWith(str)) {
                ans = true;
                break;
            }
        }
        return ans;
    }
    
    //---------------------------------------
    // 注射診区コード  31 32 33
    //---------------------------------------
    protected boolean isInjection(String code) {
        if (code==null) {
            return false;
        }
        ResourceBundle bdl = ClientContext.getClaimBundle();
        String[] targets = (String[])bdl.getObject("INJECTION_CLASS_CODE");
        
        boolean ans = false;
        for (String str : targets) {
            if (code.startsWith(str)) {
                ans = true;
                break;
            }
        }
        return ans;
    }

    //-----------------------------------------
    // 入力されたテキストから検索タイプを返す
    //-----------------------------------------
    protected int getSearchType(String test, boolean hitReturn) {

        if (test == null || test.equals("")) {
            return TT_INVALID;
        }
        
        ResourceBundle bdl = ClientContext.getClaimBundle();

        // *
        if (test.equals(bdl.getString("ASTERISK_HALF")) || 
            test.equals(bdl.getString("ASTERISK_FULL"))) {
            return TT_LIST_TECH;
        }
       
        if (test.startsWith("/") || test.startsWith("／")) {
            if (isTensuSearch(test) && hitReturn) {
                return TT_TENSU_SEARCH;
            } else {
                return TT_INVALID;
            }
        }

        // 81,82,83,84,85,86
        if ( (test.startsWith(bdl.getString("COMMENT_SEARCH_HALF")) || 
              test.startsWith(bdl.getString("COMMENT_SEARCH_FULL"))) && 
              test.length() >1 ) {
            
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
    
    // Editor Button Type
    protected boolean editorButtonTypeIsIcon() {
        String prop = Project.getString(STAMP_EDITOR_BUTTON_TYPE);
        return prop.equals(BUTTON_TYPE_IS_ICON);
    }
    
    protected String getDefaultStampName() {
        return ClientContext.getClaimBundle().getString("DEFAULT_STAMP_NAME");
    }
    
    protected String getStampNameFromEditor() {
        return ClientContext.getClaimBundle().getString("FROM_EDITOR_STAMP_NAME");
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
        
//s.oh^ 2014/08/08 スタンプ編集制御
        ret.setSrysyukbn(tm.getSrysyukbn());
//s.oh$
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
        
        ResourceBundle clb = ClientContext.getClaimBundle();

        if (test.equals(clb.getString("SLOT_SYUGI"))) {

            // 手技
            ret.setClassCode(ClaimConst.SYUGI);

            // 診療行為区分 手技で設定している
            ret.setClaimClassCode(tm.getSrysyukbn());

            // もしかして数量があるかも...
            if (ret.getUnit()!=null) {
                ret.setNumber(ClientContext.getClaimBundle().getString("DEFAULT_NUMBER"));
            }

        } else if (Pattern.compile(clb.getString("SLOT_MEDICINE")).matcher(test).find()) {

            // 薬剤
            ret.setClassCode(ClaimConst.YAKUZAI);

            ret.setYkzKbn(tm.getYkzkbn());
            //System.out.println("剤型区分=" + ret.getYkzKbn());

            String inputNum = ClientContext.getClaimBundle().getString("DEFAULT_NUMBER");

            if (ret.getUnit()!= null && ret.getUnit().equals(clb.getString("UNIT_T"))) {
                inputNum = Project.getString("defaultZyozaiNum");

            } else if (ret.getUnit()!= null && ret.getUnit().equals(clb.getString("UNIT_CAPSULE"))) {
                inputNum = Project.getString("defaultCapsuleNum");

            } else if (ret.getUnit()!= null && ret.getUnit().equals(clb.getString("UNIT_G"))) {
                inputNum = Project.getString("defaultSanyakuNum");

            } else if (ret.getUnit()!= null && ret.getUnit().equals(clb.getString("UNIT_ML"))) {
                inputNum = Project.getString("defaultMizuyakuNum");
            }

            // zero -> null 
//s.oh^ 2014/06/19 薬剤の単位対応
            //inputNum = (inputNum==null || inputNum.equals("") || inputNum.equals("0")) ? null : inputNum;
            inputNum = (inputNum==null || inputNum.equals("") || inputNum.equals("0") || inputNum.equals("0.0")) ? null : inputNum;
//s.oh$
            ret.setNumber(inputNum);


        } else if (test.equals(clb.getString("SLOT_ZAIRYO"))) {
            // 材料
            ret.setClassCode(ClaimConst.ZAIRYO);
            ret.setNumber(ClientContext.getClaimBundle().getString("DEFAULT_NUMBER"));

        } else if (test.equals(clb.getString("SLOT_YOHO"))) {
            // 用法
            ret.setClassCode(ClaimConst.ADMIN);
            ret.setName(ClientContext.getClaimBundle().getString("ADMIN_MARK") + tm.getName());
            ret.setDummy("X");
            ret.setBundleNumber(Project.getString("defaultRpNum", "1"));

        } else if (test.equals(clb.getString("SLOT_BUI"))) {
            // 部位
            ret.setClassCode(ClaimConst.BUI);

        } else if (test.equals(clb.getString("OTHER"))) {
            // その他
//minagawa^ LSC Test 初診、再診(DUMMY)
            String tenName = tm.getName();
            int index = tenName.indexOf("（ＤＵＭＭＹ）");
            if (index>0 && tm.getSrysyukbn()!=null && !tm.getSrysyukbn().equals("")) {
                ret.setClassCode(ClaimConst.SYUGI);
                ret.setClaimClassCode(tm.getSrysyukbn());
            } else {
                ret.setClassCode(ClaimConst.OTHER);
            }
//minagawa$            
            if (ret.getUnit()!=null) {
                ret.setNumber(ClientContext.getClaimBundle().getString("DEFAULT_NUMBER"));
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
     * @param subclassCode
     * @return 
     */
    protected String getNumberCode(int subclassCode) {
        return (subclassCode == 1) ? ClaimConst.ZAIRYO_KOSU : ClaimConst.YAKUZAI_TOYORYO; // 材料個数 : 薬剤投与量１回
        // 2010 ORAC の実装
        //return ClaimConst.YAKUZAI_TOYORYO;
    }

    protected void alertIpAddress() {
        java.util.ResourceBundle bundle = ClientContext.getMyBundle(AbstractStampEditor.class);
        String msg0 = bundle.getString("warning.noIpAddress");
        String msg1 = bundle.getString("message.setIpAddress");
        Object message = new String[]{msg0, msg1};
        Window parent = SwingUtilities.getWindowAncestor(getView());
        String title = ClientContext.getFrameTitle(bundle.getString("title.optionPane.masterSearch"));
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.WARNING_MESSAGE);
    }

    protected void alertSearchError(String err) {
        java.util.ResourceBundle bundle = ClientContext.getMyBundle(AbstractStampEditor.class);
        String msg0 = bundle.getString("warning.noPrevilaggeToSearch");
        String msg1 = err;

        Object message = new String[]{msg0, msg1};
        Window parent = SwingUtilities.getWindowAncestor(getView());
        String title = ClientContext.getFrameTitle(bundle.getString("title.optionPane.masterSearch"));
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.WARNING_MESSAGE);
    }

    protected Boolean ipOk() {
        return Project.canSearchMaster();
    }

    protected abstract void search(final String text, boolean hitRet);

    protected abstract void clear();

    public JTextField getSearchTextField() {
        return searchTextField;
    }

    public void addPropertyChangeListener(String prop, PropertyChangeListener listener) {
        if (boundSupport == null) {
            boundSupport = new PropertyChangeSupport(this);
        }
        boundSupport.addPropertyChangeListener(prop, listener);        
        if (listener instanceof StampHolder) {
            modifyFromStampHolder = true;
        }       
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
     * @param passPattern
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
    }

//    public AbstractStampEditor(String entity) {
//        this(entity,true);
//    }

    public AbstractStampEditor(String entity, boolean mode) {

        HashMap<String, String> ht = getEditorSpec(entity);
        
        this.setEntity(entity);
        
        ResourceBundle bdl = ClientContext.getClaimBundle();

        this.setOrderName(ht.get(bdl.getString("KEY_ORDER_NAME")));

        if (ht.get(bdl.getString("KEY_PASS_REGEXP"))!=null) {
            this.setPassPattern(Pattern.compile(ht.get(bdl.getString("KEY_PASS_REGEXP"))));
        }
        
        if (ht.get(bdl.getString("KEY_SHIN_REGEXP"))!=null) {
            this.setShinkuRegExp(ht.get(bdl.getString("KEY_SHIN_REGEXP")));
            this.setShinkuPattern(Pattern.compile(ht.get(bdl.getString("KEY_SHIN_REGEXP"))));
        }

        if (ht.get(bdl.getString("KEY_INFO"))!=null) {
            this.setInfo(ht.get(bdl.getString("KEY_INFO")));
        }

        if (ht.get(bdl.getString("KEY_IMPLIED"))!=null) {
            this.setImplied007(ht.get(bdl.getString("KEY_IMPLIED")));
        }

        setFromStampEditor(mode);
    }
}
