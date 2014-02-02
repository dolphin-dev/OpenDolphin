package open.dolphin.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import open.dolphin.converter.PlistConverter;
import open.dolphin.converter.PlistParser;
import open.dolphin.infomodel.NLaboItem;
import open.dolphin.infomodel.NLaboModule;
import open.dolphin.infomodel.PatientLiteModel;
import open.dolphin.infomodel.PatientModel;

/**
 * REST Web Service
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
@Path("lab")
public final class NLabResource extends AbstractResource {

    private boolean DEBUG;

    public NLabResource() {
    }

    @GET
    @Path("module/{param}/")
    @Produces(MediaType.APPLICATION_XML)
    public String getLaboTest(@Context HttpServletRequest servletReq, @PathParam("param") String param) {

        debug(param);
        String[] params = param.split(CAMMA);
        String pid = params[0];
        int firstResult = Integer.parseInt(params[1]);
        int maxResult = Integer.parseInt(params[2]);

        String fidPid = getFidPid(servletReq.getRemoteUser(), pid);

        List<NLaboModule> list = EJBLocator.getNLabServiceBean().getLaboTest(fidPid, firstResult, maxResult);

        PlistConverter con = new PlistConverter();
        String xml = con.convert(list);
        debug(xml);
        
        return xml;
    }

    @GET
    @Path("item/{param}/")
    @Produces(MediaType.APPLICATION_XML)
    public String getLaboTestItem(@Context HttpServletRequest servletReq, @PathParam("param") String param) {

        debug(param);
        String[] params = param.split(CAMMA);
        String pid = params[0];
        int firstResult = Integer.parseInt(params[1]);
        int maxResult = Integer.parseInt(params[2]);
        String itemCode = params[3];

        String fidPid = getFidPid(servletReq.getRemoteUser(), pid);

        List<NLaboItem> list = EJBLocator.getNLabServiceBean().getLaboTestItem(fidPid, firstResult, maxResult, itemCode);

        PlistConverter con = new PlistConverter();
        String xml = con.convert(list);
        debug(xml);
        
        return xml;
    }

    @GET
    @Path("patient/{param}")
    @Produces(MediaType.APPLICATION_XML)
    public String getConstrainedPatients(@Context HttpServletRequest servletReq, @PathParam("param") String param) {

        String fid = getRemoteFacility(servletReq.getRemoteUser());

        debug(param);
        String[] params = param.split(CAMMA);
        List<String> idList = new ArrayList<String>(params.length);
        idList.addAll(Arrays.asList(params));

        List<PatientLiteModel> list = EJBLocator.getNLabServiceBean().getConstrainedPatients(fid, idList);

        PlistConverter con = new PlistConverter();
        String xml = con.convert(list);
        debug(xml);

        return xml;
    }

    @POST
    @Path("module/")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public String postNLaboTest(@Context HttpServletRequest servletReq, String repXml) {

        String fid = getRemoteFacility(servletReq.getRemoteUser());

        PlistParser parser = new PlistParser();
        NLaboModule module = (NLaboModule) parser.parse(repXml);
        List<NLaboItem> items = module.getItems();
        // 関係を構築する
        if (items!=null && items.size()>0) {
            for (NLaboItem item : items) {
                item.setLaboModule(module);
            }
        }
        
        PatientModel patient = EJBLocator.getNLabServiceBean().create(fid, module);

        PlistConverter con = new PlistConverter();
        String xml = con.convert(patient);
        debug(xml);

        return xml;
    }

    @Override
    protected void debug(String msg) {
        if (DEBUG) {
            super.debug(msg);
        }
    }
}
