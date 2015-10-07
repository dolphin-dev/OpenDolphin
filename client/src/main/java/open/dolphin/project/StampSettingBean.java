package open.dolphin.project;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import open.dolphin.client.ClientContext;
import open.dolphin.util.ZenkakuUtils;

/**
 * KarteSettingPanel
 *
 * @author Minagawa,Kazushi
 */
public final class StampSettingBean extends AbstractSettingBean {

    // スタンプ動作
    private String replaceStamp;
    private boolean stampSpace;
    private boolean laboFold;
    //---------------------------------
    private String defaultZyozaiNum;
    private String defaultMizuyakuNum;
    private String defaultSanyakuNum;
    private String defaultCapsuleNum;
    private String defaultRpNum;
    //----------------------------------
    private boolean itemColoring;
    private String editorButtonType;
    private boolean mergeWithSameAdmin;
    private boolean showStampName;
    
    private final Map<String, String[]> tagMap = new HashMap<>(5, 0.75f);
    
    public StampSettingBean() {
        ResourceBundle bundle = ClientContext.getMyBundle(this.getClass());
        tagMap.put("replaceStamp", new String[]{bundle.getString("replace"), bundle.getString("alert")});
        tagMap.put("editorButtonType", new String[]{bundle.getString("text"), bundle.getString("icon")});
    }
    
    @Override
    public String[] propertyOrder() {
        return new String[]{
            "replaceStamp", "stampSpace", "laboFold", "defaultZyozaiNum", "defaultMizuyakuNum",
            "defaultSanyakuNum", "defaultCapsuleNum", "defaultRpNum", "itemColoring",
            "editorButtonType", "mergeWithSameAdmin", "showStampName"
        };
    }
    
    @Override
    public boolean isTagProperty(String property) {
        return tagMap.get(property)!=null;
    }
    
    @Override
    public String[] getTags(String property) {
        String[] ret = tagMap.get(property);
        return ret;
    }
    
    @Override
    public boolean isDecimalProperty(String property) {
        return (property.equals("defaultZyozaiNum") ||
                property.equals("defaultMizuyakuNum") ||
                property.equals("defaultSanyakuNum") ||
                property.equals("defaultCapsuleNum") ||
                property.equals("defaultRpNum"));
    }
    
    /**
     * ProjectStub から populate する。
     */
    @Override
    public void populate() {

        // スタンプの上にスタンプを DnD した場合に置き換えるかどうか
        boolean replace = Project.getBoolean(Project.STAMP_REPLACE);
        String text = this.arrayValueFromBoolean(replace, getTags("replaceStamp"));
        setReplaceStamp(text);

        // スタンプのDnDで間隔を空けるかどうか
        setStampSpace(Project.getBoolean(Project.STAMP_SPACE));

        // 検体検査スタンプを折りたたみ表示するかどうか
        setLaboFold(Project.getBoolean(Project.LABTEST_FOLD));

        // 錠剤のデフォルト数量
        String test = Project.getString(Project.DEFAULT_ZYOZAI_NUM);
        setDefaultZyozaiNum(decimalStringByCheck(test));

        // 水薬のデフォルト数量
        test = Project.getString(Project.DEFAULT_MIZUYAKU_NUM);
        setDefaultMizuyakuNum(decimalStringByCheck(test));

        // 散薬のデフォルト数量
        test = Project.getString(Project.DEFAULT_SANYAKU_NUM);
        setDefaultSanyakuNum(decimalStringByCheck(test));

        // カプセルのデフォルト数量
        test = Project.getString(Project.DEFAULT_CAPSULE_NUM);
        setDefaultCapsuleNum(decimalStringByCheck(test));

        // 処方日数のデフォルト
        test = Project.getString(Project.DEFAULT_RP_NUM);
        setDefaultRpNum(decimalStringByCheck(test));

        // 同じ用法をまとめる
        setMergeWithSameAdmin(Project.getBoolean(Project.KARTE_MERGE_RP_WITH_SAME_ADMIN));

        // マスタ項目をカラーリングする
        setItemColoring(Project.getBoolean(Project.MASTER_SEARCH_ITEM_COLORING));

        // スタンプエディタのボタンタイプ
        test = Project.getString(Project.STAMP_EDITOR_BUTTON_TYPE);
        int index = this.findIndex(test, new String[]{"text", "icon"});
        String value = getTags("editorButtonType")[index];
        setEditorButtonType(value);
        
        setShowStampName(Project.getBoolean("karte.show.stampName"));
    }

    /**
     * ProjectStubへ保存する。
     */
    @Override
    public void store() {

        String[] tag = getTags("replaceStamp");
        boolean b = getReplaceStamp().equals(tag[0]);
        Project.setBoolean(Project.STAMP_REPLACE, b);

        Project.setBoolean(Project.STAMP_SPACE, isStampSpace());

        Project.setBoolean(Project.LABTEST_FOLD, isLaboFold());

        //-------------------------------
        Project.setString(Project.DEFAULT_ZYOZAI_NUM, 
                ZenkakuUtils.toHalfNumber(getDefaultZyozaiNum()));
        Project.setString(Project.DEFAULT_MIZUYAKU_NUM, 
                ZenkakuUtils.toHalfNumber(getDefaultMizuyakuNum()));
        Project.setString(Project.DEFAULT_SANYAKU_NUM, 
                ZenkakuUtils.toHalfNumber(getDefaultSanyakuNum()));
        Project.setString(Project.DEFAULT_CAPSULE_NUM, 
                ZenkakuUtils.toHalfNumber(getDefaultCapsuleNum()));
        Project.setString(Project.DEFAULT_RP_NUM, 
                ZenkakuUtils.toHalfNumber(getDefaultRpNum()));
        //-------------------------------

        Project.setBoolean(Project.MASTER_SEARCH_ITEM_COLORING, isItemColoring());

        tag = getTags("editorButtonType");
        String value = getEditorButtonType().equals(tag[0]) ? "text" : "icon";
        Project.setString(Project.STAMP_EDITOR_BUTTON_TYPE, value);

        Project.setBoolean(Project.KARTE_MERGE_RP_WITH_SAME_ADMIN, isMergeWithSameAdmin());

        Project.setBoolean("karte.show.stampName", isShowStampName());
    }
    
    @Override
    public boolean isValidBean() {
        boolean ok = true;
        ok = ok && testNumber(defaultZyozaiNum);
        ok = ok && testNumber(defaultMizuyakuNum);
        ok = ok && testNumber(defaultSanyakuNum);
        ok = ok && testNumber(defaultCapsuleNum);
        ok = ok && testNumber(defaultRpNum);
        return ok;
    }
    
    private boolean testNumber(String test) {
        try {
            float value = Float.parseFloat(test);
            return (value>=0.0);
        } catch (RuntimeException e) {
        }
        return false;
    }

    public String getReplaceStamp() {
        return replaceStamp;
    }

    public void setReplaceStamp(String replaceStamp) {
        this.replaceStamp = replaceStamp;
    }

    public boolean isStampSpace() {
        return stampSpace;
    }

    public void setStampSpace(boolean stampSpace) {
        this.stampSpace = stampSpace;
    }

    public boolean isLaboFold() {
        return laboFold;
    }

    public void setLaboFold(boolean laboFold) {
        this.laboFold = laboFold;
    }

    public String getDefaultZyozaiNum() {
        return defaultZyozaiNum;
    }

    public void setDefaultZyozaiNum(String defaultZyozaiNum) {
        this.defaultZyozaiNum = defaultZyozaiNum;
    }

    public String getDefaultMizuyakuNum() {
        return defaultMizuyakuNum;
    }

    public void setDefaultMizuyakuNum(String defaultMizuyakuNum) {
        this.defaultMizuyakuNum = defaultMizuyakuNum;
    }

    public String getDefaultSanyakuNum() {
        return defaultSanyakuNum;
    }

    public void setDefaultSanyakuNum(String defaultSanyakuNum) {
        this.defaultSanyakuNum = defaultSanyakuNum;
    }

    public String getDefaultCapsuleNum() {
        return defaultCapsuleNum;
    }

    public void setDefaultCapsuleNum(String defaultCapsuleNum) {
        this.defaultCapsuleNum = defaultCapsuleNum;
    }

    public String getDefaultRpNum() {
        return defaultRpNum;
    }

    public void setDefaultRpNum(String defaultRpNum) {
        this.defaultRpNum = defaultRpNum;
    }

    public boolean isItemColoring() {
        return itemColoring;
    }

    public void setItemColoring(boolean itemColoring) {
        this.itemColoring = itemColoring;
    }

    public String getEditorButtonType() {
        return editorButtonType;
    }

    public void setEditorButtonType(String b) {
        this.editorButtonType = b;
    }

    public boolean isMergeWithSameAdmin() {
        return mergeWithSameAdmin;
    }

    public void setMergeWithSameAdmin(boolean b) {
        mergeWithSameAdmin = b;
    }        

    public boolean isShowStampName() {
        return showStampName;
    }

    public void setShowStampName(boolean b) {
        showStampName = b;
    }
}
