package open.dolphin.project;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import open.dolphin.client.ClientContext;
import open.dolphin.infomodel.IInfoModel;

/**
 * CodeHelperSettingBean
 *
 * @author Kazushi Minagawa
 */
public final class CodeHelperSettingBean extends AbstractSettingBean {

    private String modifierKey;

    private String text;

    private String path;

    private String general;

    private String other;

    private String treatment;

    private String surgery;

    private String radiology;

    private String labo;

    private String physiology;

    private String bacteria;

    private String injection;

    private String rp;

    private String baseCharge;

    private String instraction;

    private String orca;
    
    private final Map<String, String[]> tagMap = new HashMap<>(5, 0.75f);
    
    public CodeHelperSettingBean() {
        ResourceBundle bundle = ClientContext.getMyBundle(this.getClass());
        String controle = bundle.getString("ctrl");
        String meta = bundle.getString("meta");
        tagMap.put("modifierKey", new String[]{controle, meta});
    }
    
    @Override
    public String[] propertyOrder() {
        return new String[]{
            "modifierKey", "text", "path", "general", "other",
            "treatment", "surgery", "radiology", "labo", "physiology",
            "bacteria", "injection", "rp", "baseCharge", "instraction", "orca"
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
    public boolean isValidBean() {
        boolean ok = notEmpty(this.getText());
        ok = ok && notEmpty(this.getPath());
        ok = ok && notEmpty(this.getGeneral());
        ok = ok && notEmpty(this.getOther());
        ok = ok && notEmpty(this.getTreatment());
        ok = ok && notEmpty(this.getSurgery());
        ok = ok && notEmpty(this.getRadiology());
        ok = ok && notEmpty(this.getLabo());
        ok = ok && notEmpty(this.getPhysiology());
        ok = ok && notEmpty(this.getBacteria());
        ok = ok && notEmpty(this.getInjection());
        ok = ok && notEmpty(this.getRp());
        ok = ok && notEmpty(this.getBaseCharge());
        ok = ok && notEmpty(this.getInstraction());
        ok = ok && notEmpty(this.getOrca());
        return ok;
    }

    /**
     * 設定した値をプレファレンスに保存する。
     */
    @Override
    public void store() {

        String test = getModifierKey().trim();
        String value = test.equals(getTags("modifierKey")[1]) ? "meta" : "ctrl";
        Project.setString("modifier", value);

        Project.setString(IInfoModel.ENTITY_TEXT, getText().trim());

        Project.setString(IInfoModel.ENTITY_PATH, getPath().trim());

        Project.setString(IInfoModel.ENTITY_GENERAL_ORDER, getGeneral().trim());

        Project.setString(IInfoModel.ENTITY_OTHER_ORDER, getOther().trim());

        Project.setString(IInfoModel.ENTITY_TREATMENT, getTreatment().trim());

        Project.setString(IInfoModel.ENTITY_SURGERY_ORDER, getSurgery().trim());

        Project.setString(IInfoModel.ENTITY_RADIOLOGY_ORDER, getRadiology().trim());

        Project.setString(IInfoModel.ENTITY_LABO_TEST, getLabo().trim());

        Project.setString(IInfoModel.ENTITY_PHYSIOLOGY_ORDER, getPhysiology().trim());

        Project.setString(IInfoModel.ENTITY_BACTERIA_ORDER, getBacteria().trim());

        Project.setString(IInfoModel.ENTITY_INJECTION_ORDER, getInjection().trim());

        Project.setString(IInfoModel.ENTITY_MED_ORDER, getRp().trim());

        Project.setString(IInfoModel.ENTITY_BASE_CHARGE_ORDER, getBaseCharge().trim());

        Project.setString(IInfoModel.ENTITY_INSTRACTION_CHARGE_ORDER, getInstraction().trim());

        Project.setString(IInfoModel.ENTITY_ORCA, getOrca().trim());
    }

    /**
     * プレファレンスから値をGUIにセットする。
     */
    @Override
    public void populate() {

        String mask = ClientContext.isMac() ? "meta" : "ctrl";
        String test = Project.getString("modifier", mask);
        String value = test.equals("meta") ? getTags("modifierKey")[1] : getTags("modifierKey")[0];
        setModifierKey(value);

        setText(Project.getString(IInfoModel.ENTITY_TEXT, "tx").trim());

        setPath(Project.getString(IInfoModel.ENTITY_PATH, "pat").trim());

        setGeneral(Project.getString(IInfoModel.ENTITY_GENERAL_ORDER, "gen").trim());

        setOther(Project.getString(IInfoModel.ENTITY_OTHER_ORDER, "oth").trim());

        setTreatment(Project.getString(IInfoModel.ENTITY_TREATMENT, "tr").trim());

        setSurgery(Project.getString(IInfoModel.ENTITY_SURGERY_ORDER, "sur").trim());

        setRadiology(Project.getString(IInfoModel.ENTITY_RADIOLOGY_ORDER, "rad").trim());

        setLabo(Project.getString(IInfoModel.ENTITY_LABO_TEST, "lab").trim());

        setPhysiology(Project.getString(IInfoModel.ENTITY_PHYSIOLOGY_ORDER, "phy").trim());

        setBacteria(Project.getString(IInfoModel.ENTITY_BACTERIA_ORDER, "bac").trim());

        setInjection(Project.getString(IInfoModel.ENTITY_INJECTION_ORDER, "inj").trim());

        setRp(Project.getString(IInfoModel.ENTITY_MED_ORDER, "rp").trim());

        setBaseCharge(Project.getString(IInfoModel.ENTITY_BASE_CHARGE_ORDER, "base").trim());

        setInstraction(Project.getString(IInfoModel.ENTITY_INSTRACTION_CHARGE_ORDER, "ins").trim());

        setOrca(Project.getString(IInfoModel.ENTITY_ORCA, "orca").trim());
    }

    public String getModifierKey() {
        return modifierKey;
    }

    public void setModifierKey(String modifierKey) {
        this.modifierKey = modifierKey;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getGeneral() {
        return general;
    }

    public void setGeneral(String general) {
        this.general = general;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public String getSurgery() {
        return surgery;
    }

    public void setSurgery(String surgery) {
        this.surgery = surgery;
    }

    public String getRadiology() {
        return radiology;
    }

    public void setRadiology(String radiology) {
        this.radiology = radiology;
    }

    public String getLabo() {
        return labo;
    }

    public void setLabo(String labo) {
        this.labo = labo;
    }

    public String getPhysiology() {
        return physiology;
    }

    public void setPhysiology(String physiology) {
        this.physiology = physiology;
    }

    public String getBacteria() {
        return bacteria;
    }

    public void setBacteria(String bacteria) {
        this.bacteria = bacteria;
    }

    public String getInjection() {
        return injection;
    }

    public void setInjection(String injection) {
        this.injection = injection;
    }

    public String getRp() {
        return rp;
    }

    public void setRp(String rp) {
        this.rp = rp;
    }

    public String getBaseCharge() {
        return baseCharge;
    }

    public void setBaseCharge(String baseCharge) {
        this.baseCharge = baseCharge;
    }

    public String getInstraction() {
        return instraction;
    }

    public void setInstraction(String instraction) {
        this.instraction = instraction;
    }

    public String getOrca() {
        return orca;
    }

    public void setOrca(String orca) {
        this.orca = orca;
    }
}
