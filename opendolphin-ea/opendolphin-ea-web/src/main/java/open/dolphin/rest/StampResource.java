package open.dolphin.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import open.dolphin.converter.PlistConverter;
import open.dolphin.converter.PlistParser;
import open.dolphin.infomodel.StampModel;

/**
 * REST Web Service
 *
 * @author kazushi Minagawa, Digital Globe, Inc.
 */

@Path("stamp")
public final class StampResource extends AbstractResource {

    private boolean DEBUG = false;

    /** Creates a new instance of StampResource */
    public StampResource() {
    }

    @GET
    @Path("id/{param}/")
    @Produces(MediaType.APPLICATION_XML)
    public String getStamp(@PathParam("param") String param) {
        StampModel stamp = EJBLocator.getStampServiceBean().getStamp(param);
        PlistConverter con = new PlistConverter();
        String xml = con.convert(stamp);
        debug(xml);
        return xml;
    }

    
    @GET
    @Path("list/{param}/")
    @Produces(MediaType.APPLICATION_XML)
    public String getStamps(@PathParam("param") String param) {
        
        String[] params = param.split(CAMMA);
        List<String> list = new ArrayList<String>();
        list.addAll(Arrays.asList(params));

        List<StampModel> result = EJBLocator.getStampServiceBean().getStamp(list);

        PlistConverter con = new PlistConverter();
        String xml = con.convert(result);
        debug(xml);

        return xml;
    }


    @PUT
    @Path("id/")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public String putStamp(String repXml) {

        PlistParser parser = new PlistParser();
        StampModel model = (StampModel) parser.parse(repXml);

        String ret = EJBLocator.getStampServiceBean().putStamp(model);
        debug(ret);

        return ret;
    }


    @PUT
    @Path("list/")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public String putStamps(String repXml) {

        PlistParser parser = new PlistParser();
        List<StampModel> list = (List<StampModel>)parser.parse(repXml);

        List<String> ret = EJBLocator.getStampServiceBean().putStamp(list);

        StringBuilder sb = new StringBuilder();
        for (String str : ret) {
            sb.append(str);
            sb.append(",");
        }

        String retText = sb.substring(0, sb.length()-1);
        debug(retText);

        return retText;
    }


    @DELETE
    @Path("id/{param}/")
    public void deleteStamp(@PathParam("param") String param) {

        int cnt = EJBLocator.getStampServiceBean().removeStamp(param);

        debug(String.valueOf(cnt));
    }
    

    @DELETE
    @Path("list/{param}/")
    public void deleteStamps(@PathParam("param") String param) {

        String[] params = param.split(CAMMA);
        List<String> list = new ArrayList<String>();
        list.addAll(Arrays.asList(params));

        int cnt = EJBLocator.getStampServiceBean().removeStamp(list);

        debug(String.valueOf(cnt));
    }

    @Override
    protected void debug(String msg) {
        if (DEBUG) {
            super.debug(msg);
        }
    }
}
