package open.dolphin.order;

import open.dolphin.infomodel.ClaimConst;
import java.awt.Window;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import open.dolphin.client.ClientContext;
import open.dolphin.dao.SqlDaoFactory;
import open.dolphin.dao.SqlMasterDao;
import open.dolphin.infomodel.ClaimItem;
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
    public static final String EDIT_END_PROP = "editEnd";

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

    // ORCA 有効期限用のDF
    protected static SimpleDateFormat effectiveFormat = new SimpleDateFormat("yyyyMMdd");

    // ドルフィンのオーダ履歴用の名前
    protected String orderName;

    // ClaimBundle に設定する 診療行為区分 400,500,600 .. etc
    protected String classCode;

    // 診療行為区分定義のテーブルID == Claim007
    protected String classCodeId    = "Claim007";

    // ClaimItem (項目) の種別を定義しているテーブルID = Claim003
    protected String subclassCodeId = "Claom003";

    // このエディタのエンティティ
    protected String entity;
    
    // このエディタで組合わせが可能な点数マスタ項目の正規表現
    protected String passRegExp;

    // このエディタの診区正規表現
    protected String shinkuRegExp;

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
    public static Hashtable<String, String> getEditorSpec(String entity) {

        Hashtable<String, String> ht = new Hashtable<String, String>(10, 0.75f);
        String orderName = null;
        String passRegExp = null;
        String shinkuRegExp = null;
        String implied007 = null;
        String info = null;

        if (entity.equals("baseChargeOrder")) {

            orderName = "診断料";
            passRegExp = "[手そ]";
            shinkuRegExp = "^(11|12)";
            info = "診断料（診区=110-120）";


        } else if (entity.equals("instractionChargeOrder")) {

            orderName = "指導・在宅";
            passRegExp = "[手そ]";
            shinkuRegExp = "^(13|14)";
            info = "指導・在宅（診区=130-140）";


        } else if (entity.equals("medOrder")) {

            orderName = "処 方";
            passRegExp = "[薬用材そ]";              // 薬剤、用法、材料、その他(保険適用外医薬品）
            info = "処 方";

        } else if (entity.equals("injectionOrder")) {

            orderName = "注 射";
            passRegExp = "[手そ注材]";              // 手技、その他、注射薬、材料
            shinkuRegExp = "^3";
            info = "注 射（診区=300）";


        } else if (entity.equals("treatmentOrder")) {

            orderName = "処 置";
            passRegExp = "[手そ薬材]";              // 手技、その他、薬剤、材料
            shinkuRegExp = "^4";
            implied007 = "400";
            info = "処 置（診区=400）";


        } else if (entity.equals("surgeryOrder")) {

            orderName = "手 術";
            passRegExp = "[手そ薬材]";              // 手技、その他、薬剤、材料
            shinkuRegExp = "^5";
            info = "手 術（診区=500）";


        } else if (entity.equals("bacteriaOrder")) {

            orderName = "細菌検査";
            passRegExp = "[手そ薬材]";              // 手技、その他、薬剤、材料
            shinkuRegExp = "^6";
            implied007 = "600";
            info = "細菌検査（診区=600）";

        } else if (entity.equals("physiologyOrder")) {

            orderName = "生理・内視鏡検査";
            passRegExp = "[手そ薬材]";              // 手技、その他、薬剤、材料
            shinkuRegExp = "^6";
            implied007 = "600";
            info = "生理・内視鏡検査（診区=600）";


        } else if (entity.equals("testOrder")) {

            orderName = "検体検査";
            passRegExp = "[手そ薬材]";              // 手技、その他、薬剤、材料
            shinkuRegExp = "^6";
            implied007 = "600";
            info = "検体検査（診区=600）";


        } else if (entity.equals("radiologyOrder")) {

            orderName = "放射線";
            passRegExp = "[手そ薬材部]";            // 手技、その他、薬剤、材料、部位
            shinkuRegExp = "^7";
            implied007 = "700";
            info = "放射線（診区=700）";


        }   else if (entity.equals("otherOrder")) {

            orderName = "その他";
            passRegExp = "[手そ薬材]";              // 手技、その他、薬剤、材料
            shinkuRegExp = "^8";
            implied007 = "800";
            info = "その他（診区=800）";


        } else if (entity.equals("generalOrder")) {

            orderName = "汎 用";
            passRegExp = "[手そ薬材用部]";        // 手技、その他、薬剤、材料、用法、部位
            shinkuRegExp = "\\d";
            info = "汎 用（診区=100-999）";

        } else if (entity.equals("diagnosis")) {

            orderName = "傷病名";
            passRegExp = "[手そ薬材用部]";
        }

        ht.put("orderName", orderName);

        if (passRegExp!=null) {
            ht.put("passRegExp", passRegExp);
        }
        
        if (shinkuRegExp!=null) {
            ht.put("shinkuRegExp", shinkuRegExp);
        }

        if (info!=null) {
            ht.put("info", info);
        }

        if (implied007!=null) {
            ht.put("implied007", implied007);
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

    protected void checkValidation() {
        
        if (boundSupport != null) {
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

        // 単位
        String unit = trimToNullIfEmpty(masterItem.getUnit());
        if (unit != null) {
            ret.setUnit(unit);
        }

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

            String inputNum = DEFAULT_NUMBER;

            if (ret.getUnit().equals(ClaimConst.UNIT_T)) {
                inputNum = Project.getPreferences().get("defaultZyozaiNum", "3");

            } else if (ret.getUnit().equals(ClaimConst.UNIT_G)) {
                inputNum = Project.getPreferences().get("defaultSanyakuNum", "1.0");

            } else if (ret.getUnit().equals(ClaimConst.UNIT_ML)) {
                inputNum = Project.getPreferences().get("defaultMizuyakuNum", "1");

            } else if (ret.getUnit().equals(ClaimConst.UNIT_CAPSULE)) {
                inputNum = Project.getPreferences().get("defaultKapuselNum", "1");
            }

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
            ret.setBundleNumber(Project.getPreferences().get("defaultRpNum", "1"));

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
        String address = Project.getClaimAddress();
        if (address == null || address.equals("")) {
            alertIpAddress();
            return false;
        }

        return true;
    }

    protected abstract void search(final String text);

    protected abstract void clear();

    protected abstract void initComponents();

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
    public String getPassRegExp() {
        return passRegExp;
    }

    /**
     * @param passRegExp the passRegExp to set
     */
    public void setPassRegExp(String passRegExp) {
        this.passRegExp = passRegExp;
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
    public void setShinkuRegExp(String shinkuRegExp) {
        this.shinkuRegExp = shinkuRegExp;
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
    public void setOrderName(String orderName) {
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
    public void setEntity(String entity) {
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
    public void setInfo(String info) {
        this.info = info;
    }

    public String getImplied007() {
        return implied007;
    }

    public void setImplied007(String default007) {
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
    public void setFromStampEditor(Boolean fromStampEditor) {
        this.fromStampEditor = fromStampEditor;
    }

    public AbstractStampEditor() {
        initComponents();
    }

    public AbstractStampEditor(String entity) {
        this(entity,true);
    }

    public AbstractStampEditor(String entity, boolean mode) {

        Hashtable<String, String> ht = AbstractStampEditor.getEditorSpec(entity);

        this.setEntity(entity);
        this.setOrderName(ht.get("orderName"));

        if (ht.get("passRegExp")!=null) {
            this.setPassRegExp(ht.get("passRegExp"));
        }
        
        if (ht.get("shinkuRegExp")!=null) {
            this.setShinkuRegExp(ht.get("shinkuRegExp"));
        }

        if (ht.get("info")!=null) {
            this.setInfo(ht.get("info"));
        }

        if (ht.get("implied007")!=null) {
            this.setImplied007(ht.get("implied007"));
        }

        setFromStampEditor(mode);

        initComponents();
    }
}
