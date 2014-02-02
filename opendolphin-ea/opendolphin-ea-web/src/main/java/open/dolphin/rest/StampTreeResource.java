package open.dolphin.rest;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import open.dolphin.converter.PlistConverter;
import open.dolphin.converter.PlistParser;
import open.dolphin.infomodel.IStampTreeModel;
import open.dolphin.infomodel.PublishedTreeModel;
import open.dolphin.infomodel.StampTreeModel;
import open.dolphin.infomodel.SubscribedTreeModel;

/**
 * REST Web Service
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */

@Path("stampTree")
public final class StampTreeResource extends AbstractResource {

    private boolean DEBUG = false;

    /** Creates a new instance of SampResource */
    public StampTreeResource() {
    }

    @GET
    @Path("{userPK}/")
    @Produces(MediaType.APPLICATION_XML)
    public String getStampTree(@PathParam("userPK") String userPK) {

        List<IStampTreeModel> result = EJBLocator.getStampServiceBean().getTrees(Long.parseLong(userPK));

        PlistConverter con = new PlistConverter();
        String xml = con.convert(result);
        debug(xml);

        return xml;
    }


    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public String putTree(String repXml) {

        PlistParser parser = new PlistParser();
        StampTreeModel model = (StampTreeModel) parser.parse(repXml);

        long pk = EJBLocator.getStampServiceBean().putTree(model);
        String pkStr = String.valueOf(pk);
        debug(pkStr);

        return pkStr;
    }


    @POST
    @Path("published/")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public String postPublishedTree(String repXml) {

        PlistParser parser = new PlistParser();
        List<IStampTreeModel> list = (List<IStampTreeModel>) parser.parse(repXml);

        long pk = EJBLocator.getStampServiceBean().saveAndPublishTree(list);
        String pkStr = String.valueOf(pk);
        debug(pkStr);

        return pkStr;
    }


    @PUT
    @Path("published/")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public String putPublishedTree(String repXml) {

        PlistParser parser = new PlistParser();
        List<IStampTreeModel> list = (List<IStampTreeModel>) parser.parse(repXml);

        int cnt = EJBLocator.getStampServiceBean().updatePublishedTree(list);
        String cntStr = String.valueOf(cnt);
        debug(cntStr);

        return cntStr;
    }


    @PUT
    @Path("published/cancel/")
    @Consumes(MediaType.APPLICATION_XML)
    public void cancelPublishedTree(String repXml) {

        PlistParser parser = new PlistParser();
        StampTreeModel model = (StampTreeModel) parser.parse(repXml);
        
        int cnt = EJBLocator.getStampServiceBean().cancelPublishedTree(model);

        String cntStr = String.valueOf(cnt);
        debug(cntStr);
    }


    @GET
    @Path("published/")
    @Produces(MediaType.APPLICATION_XML)
    public String getPublishedTrees(@Context HttpServletRequest servletReq) {

        String fid = getRemoteFacility(servletReq.getRemoteUser());
        List<PublishedTreeModel> list = EJBLocator.getStampServiceBean().getPublishedTrees(fid);

        PlistConverter con = new PlistConverter();
        String xml = con.convert(list);
        debug(xml);

        return xml;
    }


    @PUT
    @Path("subscribed/")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public String subscribeTrees(String repXml) {
        
        PlistParser parser = new PlistParser();
        List<SubscribedTreeModel> list = (List<SubscribedTreeModel>) parser.parse(repXml);

        List<Long> result = EJBLocator.getStampServiceBean().subscribeTrees(list);

        StringBuilder sb = new StringBuilder();
        for (Long l : result) {
            sb.append(String.valueOf(l));
            sb.append(CAMMA);
        }
        String pks = sb.substring(0, sb.length()-1);
        debug(pks);

        return pks;
    }


    @DELETE
    @Path("subscribed/{idPks}")
    @Consumes(MediaType.APPLICATION_XML)
    public void unsubscribeTrees(@PathParam("idPks") String idPks) {

        String[] params = idPks.split(CAMMA);
        List<Long> list = new ArrayList<Long>();
        for (String s : params) {
            list.add(Long.parseLong(s));
        }

        int cnt = EJBLocator.getStampServiceBean().unsubscribeTrees(list);
        
        String cntStr = String.valueOf(cnt);
        debug(cntStr);
    }


    @Override
    protected void debug(String msg) {
        if (DEBUG) {
            super.debug(msg);
        }
    }
}
