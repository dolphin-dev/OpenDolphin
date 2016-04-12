package open.dolphin.adm20.mbean;

import java.io.File;

/**
 *
 * @author Kazushi Minagawa
 */
public class LayerConfig {
    
    private final static String LAER_APP_ID = "layer:///apps/staging/3a031e94-5c3b-11e5-b0e1-e9979f007fc5";
    private final static String LAYER_KEY_ID = "layer:///keys/0a992148-7334-11e5-b1e8-74ba16004993";
    private final static String LAYER_PROVIDER_ID = "layer:///providers/3a025bb2-5c3b-11e5-ab16-e9979f007fc5";
    //private final static String LAYER_RSA_KEY_PATH = "/Users/kazushi/Dropbox/LayerKey/phrchat.pk8";

    public String getAppId() {
        return LAER_APP_ID;
    }
    
    public String getProviderId() {
        return LAYER_PROVIDER_ID;
    }

    public String getLayerKeyId() {
        return LAYER_KEY_ID;
    }

    public String getRsaKeyPath() {
        //return LAYER_RSA_KEY_PATH;
        StringBuilder sb = new StringBuilder();
        sb.append(System.getProperty("jboss.home.dir"));
        sb.append(File.separator);
        sb.append("phrchat.pk8");
        return sb.toString();
    }
}
