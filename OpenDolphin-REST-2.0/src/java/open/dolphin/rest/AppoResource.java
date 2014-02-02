package open.dolphin.rest;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import open.dolphin.converter.PlistParser;
import open.dolphin.infomodel.AppointmentModel;

/**
 * REST Web Service
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */

@Path("appo")
public final class AppoResource extends AbstractResource {

    private boolean DEBUG;

    /** Creates a new instance of AppoResource */
    public AppoResource() {
    }

    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public String putXml(String repXml) {

        PlistParser parser = new PlistParser();
        List<AppointmentModel> list = (List<AppointmentModel>) parser.parse(repXml);

        int count = EJBLocator.getAppoServiceBean().putAppointments(list);
        String cntStr = String.valueOf(count);
        debug(cntStr);

        return cntStr;
    }


    @Override
    protected void debug(String msg) {
        if (DEBUG) {
            super.debug(msg);
        }
    }
}
