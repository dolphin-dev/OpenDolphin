package open.dolphin.delegater;

import com.sun.jersey.api.client.ClientResponse;
import java.util.List;
import javax.ws.rs.core.MediaType;
import open.dolphin.converter.PlistConverter;
import open.dolphin.infomodel.AppointmentModel;



/**
 * AppointmentDelegater
 * 
 * @author Kazushi Minagawa. Digital Globe, Inc.
 */
public final class AppointmentDelegater extends BusinessDelegater {

    public int putAppointments(List<AppointmentModel> list) {

        PlistConverter con = new PlistConverter();
        String xml = con.convert(list);

        String path = "appo/";

        ClientResponse response = getResource(path)
                .type(MediaType.APPLICATION_XML_TYPE)
                .put(ClientResponse.class, xml);
        
        String entityStr = response.getEntity(String.class);

        if (DEBUG) {
            logger.debug("status = " + response.getStatus());
            logger.debug(entityStr);
        }

        return Integer.parseInt(entityStr);
    }
}
