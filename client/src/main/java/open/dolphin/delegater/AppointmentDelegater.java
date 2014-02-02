package open.dolphin.delegater;

import java.util.List;
import javax.ws.rs.core.MediaType;
import open.dolphin.converter14.AppoListConverter;
import open.dolphin.infomodel.AppoList;
import open.dolphin.infomodel.AppointmentModel;
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
        
        // Wrapper
        AppoList wrapper = new AppoList();
        wrapper.setList(list);
        
        // Converter
        AppoListConverter conv = new AppoListConverter();
        conv.setModel(wrapper);
        
        // JSON
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(conv);
        byte[] data = json.getBytes(UTF8);
        
        // PUT
        ClientRequest request = getRequest(path);
        request.body(MediaType.APPLICATION_JSON, data);
        ClientResponse<String> response = request.put(String.class);

        // Count
        String entityStr = getString(response);
        return Integer.parseInt(entityStr);
    }
}
