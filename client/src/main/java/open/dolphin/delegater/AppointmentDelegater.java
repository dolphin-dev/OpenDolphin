package open.dolphin.delegater;

import java.util.List;
import open.dolphin.converter.AppoListConverter;
import open.dolphin.infomodel.AppoList;
import open.dolphin.infomodel.AppointmentModel;
import org.codehaus.jackson.map.ObjectMapper;


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
        ObjectMapper mapper = this.getSerializeMapper();
        byte[] data = mapper.writeValueAsBytes(conv);
        
        // PUT
        String countStr = putEasyJson(path, data, String.class);
        
        // Count
        return Integer.parseInt(countStr);
    }
}
