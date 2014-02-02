package open.dolphin.delegater;

import java.util.List;
import javax.ws.rs.core.MediaType;
import open.dolphin.converter.AppoListConverter;
import open.dolphin.infomodel.AppoList;
import open.dolphin.infomodel.AppointmentModel;
import open.dolphin.util.Log;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;


/**
 * AppointmentDelegater
 * 
 * @author Kazushi Minagawa. Digital Globe, Inc.
 */
public final class AppointmentDelegater extends BusinessDelegater {

    public int putAppointments(List<AppointmentModel> list) throws Exception {
        
        // PATH
        String path = "/appo";
        Log.outputFuncLog(Log.LOG_LEVEL_0,"I",path);
        // Wrapper
        AppoList wrapper = new AppoList();
        wrapper.setList(list);
        
        // Converter
        AppoListConverter conv = new AppoListConverter();
        conv.setModel(wrapper);
        
        // JSON
        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String json = mapper.writeValueAsString(conv);
        byte[] data = json.getBytes(UTF8);
        
        // PUT
        ClientRequest request = getRequest(path);
        request.body(MediaType.APPLICATION_JSON, data);
        ClientResponse<String> response = request.put(String.class);

        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","REQ",request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","PRM",MediaType.APPLICATION_JSON,json);
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","RES",response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5,"I","ENT",getString(response));
        // Count
        String entityStr = getString(response);
        return Integer.parseInt(entityStr);
    }
}
