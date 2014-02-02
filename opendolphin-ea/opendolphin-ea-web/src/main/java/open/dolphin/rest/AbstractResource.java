package open.dolphin.rest;

import java.text.SimpleDateFormat;
import java.util.Date;
import open.dolphin.infomodel.IInfoModel;
import org.apache.log4j.Logger;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class AbstractResource {

    protected static final String CAMMA = ",";
    protected static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
    protected static SimpleDateFormat ISO_DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    protected static SimpleDateFormat MML_Df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    protected static Logger logger = Logger.getLogger("org.jboss.logging.util.OnlyOnceErrorHandler");

    protected static Date parseDate(String source) {
        try {
            return ISO_DF.parse(source);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return null;
    }

    protected void debug(String msg) {
        logger.info(msg);
    }

    protected static String getRemoteFacility(String remoteUser) {
        int index = remoteUser.indexOf(IInfoModel.COMPOSITE_KEY_MAKER);
        return remoteUser.substring(0, index);
    }

    protected static String getFidPid(String remoteUser, String pid) {
        StringBuilder sb = new StringBuilder();
        sb.append(getRemoteFacility(remoteUser));
        sb.append(IInfoModel.COMPOSITE_KEY_MAKER);
        sb.append(pid);
        return sb.toString();
    }
}
