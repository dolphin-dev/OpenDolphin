package open.dolphin.rest;

import java.util.List;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import open.dolphin.converter.PlistConverter;
import open.dolphin.converter.PlistParser;
import open.dolphin.infomodel.LetterModule;

/**
 * REST Web Service
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
@Path("odletter")
public final class LetterResource extends AbstractResource {

    private boolean DEBUG = false;

    /** Creates a new instance of KarteResource */
    public LetterResource() {
    }

    @PUT
    @Path("letter/")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public String putLetter(String repXml) {

        PlistParser parser = new PlistParser();
        LetterModule model = (LetterModule) parser.parse(repXml);

        Long pk = EJBLocator.getLetterServiceBean().saveOrUpdateLetter(model);

        String pkStr = String.valueOf(pk);
        debug(pkStr);

        return pkStr;
    }

    @GET
    @Path("list/{param}/")
    @Produces(MediaType.APPLICATION_XML)
    public String getLetterList(@PathParam("param") String param) {

        debug(param);
        String[] params = param.split(CAMMA);
        long karteId = Long.parseLong(params[0]);

        List<LetterModule> list = EJBLocator.getLetterServiceBean().getLetterList(karteId);

        String xml = null;
        PlistConverter con = new PlistConverter();
        xml = con.convert(list);
        debug(xml);

        return xml;
    }

    @GET
    @Path("letter/{param}/")
    @Produces(MediaType.APPLICATION_XML)
    public String getLetter(@PathParam("param") String param) {

        long pk = Long.parseLong(param);

        LetterModule result = (LetterModule) EJBLocator.getLetterServiceBean().getLetter(pk);
        PlistConverter con = new PlistConverter();
        String xml = con.convert(result);
        debug(xml);

        return xml;
    }

    @DELETE
    @Path("letter/{param}/")
    public void delete(@PathParam("param") String param) {

        long pk = Long.parseLong(param);
        EJBLocator.getLetterServiceBean().delete(pk);
    }


    @Override
    protected void debug(String msg) {
        if (DEBUG) {
            super.debug(msg);
        }
    }
}
