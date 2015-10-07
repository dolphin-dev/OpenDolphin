package open.dolphin.project;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import open.dolphin.client.ClientContext;

/**
 * ClaimSettingBean
 *
 * @author Kazushi Minagawa Digital Globe, Inc.
 *
 */
public final class ClaimSettingBean extends AbstractSettingBean {

    private String sendClaim;
    private String claimHost;
    private String jmariCode;
    private String claimAddress;
    private String claimPort;
    private boolean useAsPvtServer;
    private String bindAddress;
    private boolean claim01;
    private String connectionMode;
    private boolean pvtTimerCheck;
    
    private final Map<String, String[]> tagMap = new HashMap<>(5, 0.75f);
    
    public ClaimSettingBean() {
        ResourceBundle bundle = ClientContext.getMyBundle(this.getClass());
        tagMap.put("sendClaim", new String[]{bundle.getString("send"), bundle.getString("noSend")});
        tagMap.put("connectionMode", new String[]{bundle.getString("client"), bundle.getString("server")});
    }
    
    @Override
    public String[] propertyOrder() {
        return new String[]{
            "sendClaim", "claimHost", "claim01", "jmariCode", "connectionMode",
            "claimAddress", "claimPort", "useAsPvtServer", "bindAddress", "pvtTimerCheck"
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
        return property.equals("claimPort");
    }
    
    @Override
    public boolean isValidBean() {
        
        boolean jmariOk = isJmariCode(jmariCode);
        boolean claimAddrOk = true;
        boolean claimPortOk = true;
        boolean bindAdrOk = true;
        
        int index = findIndex(getConnectionMode(), getTags("connectionMode"));

        if (index==0) {
            claimAddrOk = claimAddrOk && isIPAddress(claimAddress);
            claimPortOk = claimPortOk && isPort(claimPort);
        }

        return (jmariOk && claimAddrOk && claimPortOk && bindAdrOk);
    }

    @Override
    public void populate() {
        
        // 診療行為送信
        boolean send = Project.getBoolean(Project.SEND_CLAIM);
        String value = arrayValueFromBoolean(send, getTags("sendClaim"));
        setSendClaim(value);

        // JMARI code
        String code = Project.getString(Project.JMARI_CODE);
        if (code!=null && code.startsWith("JPN")) {
            code = code.substring(3);
        }
        setJmariCode(code);

        // Connection mode to accounting computer(ORCA). Store client|server as value
        String[] tag = getTags("connectionMode");
        String test = Project.getString(Project.CLAIM_SENDER);
        String mode = (test!=null && !"".equals(test) && test.equals("client")) ? tag[0] : tag[1];
        setConnectionMode(mode);

        // CLAIM ホストのIPアドレス
        setClaimAddress(Project.getString(Project.CLAIM_ADDRESS));

        // CLAIM ホストのポート番号
        setClaimPort(Project.getString(Project.CLAIM_PORT));

        // ホスト名
        setClaimHost(Project.getString(Project.CLAIM_HOST_NAME));

        // 受付受信
        setUseAsPvtServer(Project.getBoolean(Project.USE_AS_PVT_SERVER));

        // バインドアドレス
        setBindAddress(Project.getString(Project.CLAIM_BIND_ADDRESS));

        // 01 小児科等
        setClaim01(Project.getBoolean(Project.CLAIM_01));
            
        // Check patient visits periodically
        setPvtTimerCheck(Project.getBoolean(Project.PVT_TIMER_CHECK, true));          
    }

    @Override
    public void store() {

        // 診療行為送信
        int index = findIndex(getSendClaim(), getTags("sendClaim"));
        Project.setBoolean(Project.SEND_CLAIM, index==0);

        // JMARI
        String code = getJmariCode();
        if (code!=null && !"".equals(code) && code.length()==12) {
            code = "JPN" + code;
            Project.setString(Project.JMARI_CODE, code);
        }

        // Connection mode, Store clientserver as the property value
        String[] tag = getTags("connectionMode");
        String value = getConnectionMode().equals(tag[0]) ? "client" : "server";
        Project.setString(Project.CLAIM_SENDER, value);

        // CLAIM ホストのIPアドレス
        Project.setString(Project.CLAIM_ADDRESS, getClaimAddress());

        // CLAIM ホストのポート番号
        Project.setString(Project.CLAIM_PORT, getClaimPort());

        // ホスト名
        Project.setString(Project.CLAIM_HOST_NAME, getClaimHost());

        // 受付受信
        Project.setBoolean(Project.USE_AS_PVT_SERVER, isUseAsPvtServer());

        // バインドアドレス
        Project.setString(Project.CLAIM_BIND_ADDRESS, getBindAddress());

        // 01 小児科
        Project.setBoolean(Project.CLAIM_01, isClaim01());

        // Check pvt 
        Project.setBoolean(Project.PVT_TIMER_CHECK, isPvtTimerCheck());             
    }

    public String getSendClaim() {
        return sendClaim;
    }

    public void setSendClaim(String sendClaim) {
        this.sendClaim = sendClaim;
    }

    public boolean isUseAsPvtServer() {
        return useAsPvtServer;
    }

    public void setUseAsPvtServer(boolean useAsPvtServer) {
        this.useAsPvtServer = useAsPvtServer;
    }

    public String getClaimHost() {
        return claimHost;
    }

    public void setClaimHost(String claimHostName) {
        this.claimHost = claimHostName;
    }

    public String getConnectionMode() {
        return connectionMode;
    }

    public void setConnectionMode(String b) {
        connectionMode = b;
    }

    public String getClaimAddress() {
        return claimAddress;
    }

    public void setClaimAddress(String claimAddress) {
        this.claimAddress = claimAddress;
    }

    public String getBindAddress() {
        return bindAddress;
    }

    public void setBindAddress(String bindAddress) {
        this.bindAddress = bindAddress;
    }

    public String getClaimPort() {
        return claimPort;
    }

    public void setClaimPort(String claimPort) {
        this.claimPort = claimPort;
    }

    public String getJmariCode() {
        return jmariCode;
    }

    public void setJmariCode(String jmariCode) {
        this.jmariCode = jmariCode;
    }

    public boolean isClaim01() {
        return claim01;
    }

    public void setClaim01(boolean b) {
        this.claim01 = b;
    }

    public boolean isPvtTimerCheck() {
        return pvtTimerCheck;
    }

    public void setPvtTimerCheck(boolean b) {
        this.pvtTimerCheck = b;
    }
    
    private boolean isJmariCode(String test) {
        return test!=null && !"".equals(test) && test.length()==12;
    }

    private boolean isIPAddress(String test) {
        return (test != null && !test.equals(""));
    }

    private boolean isPort(String test) {

        boolean ret = false;

        if (test != null) {
            try {
                int port = Integer.parseInt(test);
                ret = port >= 0 && port <= 65535;
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }

        return ret;
    }
}
