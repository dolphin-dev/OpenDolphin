package open.dolphin.adm20;

import com.plivo.helper.api.client.RestAPI;
import com.plivo.helper.api.response.message.MessageResponse;
import com.plivo.helper.exception.PlivoException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kazushi Minagawa.
 */
public class PlivoSender {
    
    private static final String AUTH_ID = "MAOWU0NWU1MZC4NJK5MT";
    private static final String AUTH_TOKEN = "MmM4NzYwYmVhYjZhN2I4MTJhNGVmN2NmMzkyNTc1";
    private static final String SRC_NUMBER = "14159681855";
    
    private String authId = AUTH_ID;
    private String authToken = AUTH_TOKEN;
    private String srcNumber = SRC_NUMBER;
    
    public PlivoSender() {
    }
    
    public void send(List<String> list, String message) throws SMSException {
        
        // 送信先
        StringBuilder sb = new StringBuilder();
        for (String number : list) {
            
            String[] params = number.split("\\s*-\\s*");
            
            if (params.length==4) {
                // +81-090-4667-6797 -> 819046676797
                String test = params[0];
                test = (test.startsWith("+")) ? test.substring(1) : test;
                String test2 = params[1];
                test2 = (test2.startsWith("0")) ? test2.substring(1) : test2;
                sb.append(test).append(test2).append(params[2]).append(params[3]).append("<");
            }
            else if (params.length==3)
            {
                // 090-4667-6797 -> 819046676797
                String test = params[0];
                test = (test.startsWith("0")) ? test.substring(1) : test;
                sb.append("81").append(test).append(params[1]).append(params[2]).append("<");
            }
        }
        // Trim last < 
        int len = sb.length();
        sb.setLength(len-1);
        String dest = sb.toString();
        
        // API
        RestAPI api = new RestAPI(getAuthId(), getAuthToken(), "v1");
        
        LinkedHashMap<String, String> parameters = new LinkedHashMap();
        parameters.put("src", getSrcNumber());
        parameters.put("dst", dest);
        parameters.put("text", message);
        
        try {
            MessageResponse msgResponse = api.sendMessage(parameters);
            info(msgResponse.apiId);
            if (msgResponse.serverCode == 202) {
                info("SMS success " + msgResponse.messageUuids.get(0));
            } else {
                warn("SMS error " + msgResponse.error);
                throw new SMSException(msgResponse.error);
            }
        } catch (PlivoException e) {
            warn(e.getLocalizedMessage());
            throw new SMSException(e.getLocalizedMessage());
        }
    }
    
    private void info(String msg) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, msg);
    }
    
    private void warn(String msg) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, msg);
    }

    public String getAuthId() {
        return authId;
    }

    public void setAuthId(String authId) {
        this.authId = authId;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getSrcNumber() {
        return srcNumber;
    }

    public void setSrcNumber(String srcNumber) {
        this.srcNumber = srcNumber;
    }
}
