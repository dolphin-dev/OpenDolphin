/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package open.dolphin.delegater;

import java.io.BufferedReader;
import javax.ws.rs.core.MediaType;
import open.dolphin.util.Log;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

/**
 *
 * @author S.Oh@Life Sciences Computing Corporation.
 */
public final class  ServerInfoDelegater extends BusinessDelegater {
    
    private static final String BASE_RESOURCE = "/serverinfo/";
    
    public boolean isCloudZero() throws Exception {
        
        // PATH
        String path = BASE_RESOURCE + "cloud/zero";
        Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_INFORMATION, path);
        
        // GET
        ClientRequest request = getRequest(path);
        request.accept(MediaType.TEXT_PLAIN);
        ClientResponse<String> response = request.get(String.class);
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","REQ",request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","PRM",MediaType.APPLICATION_JSON);
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","RES",String.valueOf(response.getStatus()), response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5,"I","ENT",getString(response));
        
        String ret = getString(response);
        
        return (ret.equals("true")) ? true : false;
    }
    
    public String getJamri() throws Exception {
        
        // PATH
        String path = BASE_RESOURCE + "jamri";
        Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_INFORMATION, path);
        
        // GET
        ClientRequest request = getRequest(path);
        request.accept(MediaType.TEXT_PLAIN);
        ClientResponse<String> response = request.get(String.class);
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","REQ",request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","PRM",MediaType.APPLICATION_JSON);
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","RES",String.valueOf(response.getStatus()), response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5,"I","ENT",getString(response));
        
        String ret = getString(response);
        
        return ret;
    }
}
