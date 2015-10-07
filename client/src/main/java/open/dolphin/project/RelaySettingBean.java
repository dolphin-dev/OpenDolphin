package open.dolphin.project;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kazushi Minagawa Digital Globe, Inc.
 * @author s.oh
 */
public final class RelaySettingBean extends AbstractSettingBean {
    
    private boolean sendMml;
    private String mmlVersion;
    private String mmlDirectory;
    
    private boolean sendKartePdf;
    private String kartePdfDirectory;
    
    private boolean pvtRelay;
    private String pvtRelayDirectory;
    private String pvtRelayEncoding;
    
    private final Map<String, String[]> tagMap = new HashMap<>(5, 0.75f);
    
    public RelaySettingBean() {
        tagMap.put("pvtRelayEncoding", new String[]{"UTF-8", "SHIFT_JIS", "EUC_JP"});
    }
    
    @Override
    public String[] propertyOrder() {
        return new String[]{
            "sendMml", "mmlVersion", "mmlDirectory",
            "sendKartePdf", "kartePdfDirectory",
            "pvtRelay", "pvtRelayDirectory", "pvtRelayEncoding"
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
    public boolean isDirectoryProperty(String property) {
        return (property.equals("mmlDirectory") ||
                property.equals("kartePdfDirectory") ||
                property.equals("pvtRelayDirectory"));
    }
    
    @Override
    public boolean isValidBean() {
        boolean mmlOk = isSendMml() ? notEmpty(getMmlDirectory()) : true;
        boolean pdfOk = isSendKartePdf() ? notEmpty(getKartePdfDirectory()) : true;
        boolean relayOk = isPvtRelay() ? notEmpty(getPvtRelayDirectory()) : true;
        return mmlOk && pdfOk && relayOk;
    }
    
    @Override
    public void populate() {
        
        ProjectStub stub = Project.getProjectStub();

        // MML送信
        boolean send = stub.getBoolean(Project.SEND_MML);
        setSendMml(send);

        // version3 -> disabled
        setMmlVersion("230");

        // 送信ディレクトリ
        String val = stub.getString(Project.SEND_MML_DIRECTORY);
        if (notEmpty(val)) {
            setMmlDirectory(val);
        }

        //----------------------------------------------
        // Karte PDF 送信
        boolean sendKartePDF = stub.getBoolean(Project.KARTE_PDF_SEND_AT_SAVE);
        setSendKartePdf(sendKartePDF);

        // 送信ディレクトリ
        val = stub.getString(Project.KARTE_PDF_SEND_DIRECTORY);
        if (notEmpty(val)) {
            setKartePdfDirectory(val);
        }

        //----------------------------------------------
        // Relay
        boolean relay = stub.getBoolean(Project.PVT_RELAY);
        setPvtRelay(relay);

        // Relayディレクトリ
        val = stub.getString(Project.PVT_RELAY_DIRECTORY);
        if (notEmpty(val)) {
            setPvtRelayDirectory(val);
        }

        // デフォルト値で UTF-8が与えられている
        val = stub.getString(Project.PVT_RELAY_ENCODING, "utf8").toLowerCase();
        val = val.replaceAll("-", "");
        val = val.replaceAll("_", "");
        String[] tag = getTags("pvtRelayEncoding");
        if (val.equals("utf8")) {
            setPvtRelayEncoding(tag[0]);
        } else if (val.equals("shiftjis") || val.equals("Shift_JIS")) {
            setPvtRelayEncoding(tag[1]);
        } else if (val.equals("eucjp")) {
            setPvtRelayEncoding(tag[2]);
        } 
    }

    @Override
    public void store() {
        
        ProjectStub stub = Project.getProjectStub();

        // MML送信
        stub.setBoolean(Project.SEND_MML, isSendMml());

        // MML バージョン
        stub.setString(Project.MML_VERSION, "230");

        // 送信先ディレクトリ null値は propertiesに設定できない
        String value = getMmlDirectory();
        if (notEmpty(value)) {
            stub.setString(Project.SEND_MML_DIRECTORY, value);
        }

        //----------------------------------------------
        // Karte PDF 送信
        stub.setBoolean(Project.KARTE_PDF_SEND_AT_SAVE, isSendKartePdf());

        // 送信先ディレクトリ null値は propertiesに設定できない
        value = getKartePdfDirectory();
        if (notEmpty(value)) {
            stub.setString(Project.KARTE_PDF_SEND_DIRECTORY, value);
        }

        //----------------------------------------------
        // Relay
        stub.setBoolean(Project.PVT_RELAY, isPvtRelay());

        // Relayディレクトリ null値は propertiesに設定できない
        value = getPvtRelayDirectory();
        if (notEmpty(value)) {
            stub.setString(Project.PVT_RELAY_DIRECTORY, value);
        }
        
        String val = getPvtRelayEncoding().toLowerCase();
        val = val.replaceAll("-", "");
        val = val.replaceAll("_", "");
        stub.setString(Project.PVT_RELAY_ENCODING, val);
    }

    public boolean isSendMml() {
        return sendMml;
    }

    public void setSendMml(boolean sendMml) {
        this.sendMml = sendMml;
    }

    public String getMmlVersion() {
        return mmlVersion;
    }

    public void setMmlVersion(String mmlVersion) {
        this.mmlVersion = mmlVersion;
    }

    public String getMmlDirectory() {
        return mmlDirectory;
    }

    public void setMmlDirectory(String mmlDirectory) {
        this.mmlDirectory = mmlDirectory;
    }

    public boolean isSendKartePdf() {
        return sendKartePdf;
    }

    public void setSendKartePdf(boolean sendKartePdf) {
        this.sendKartePdf = sendKartePdf;
    }

    public String getKartePdfDirectory() {
        return kartePdfDirectory;
    }

    public void setKartePdfDirectory(String kartePdfDirectory) {
        this.kartePdfDirectory = kartePdfDirectory;
    }

    public boolean isPvtRelay() {
        return pvtRelay;
    }

    public void setPvtRelay(boolean pvtRelay) {
        this.pvtRelay = pvtRelay;
    }

    public String getPvtRelayDirectory() {
        return pvtRelayDirectory;
    }

    public void setPvtRelayDirectory(String pvtRelayDirectory) {
        this.pvtRelayDirectory = pvtRelayDirectory;
    }

    public String getPvtRelayEncoding() {
        return pvtRelayEncoding;
    }

    public void setPvtRelayEncoding(String pvtRelayEncoding) {
        this.pvtRelayEncoding = pvtRelayEncoding;
    }
}
