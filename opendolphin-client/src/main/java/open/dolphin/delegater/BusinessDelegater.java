package open.dolphin.delegater;

import com.sun.jersey.api.client.WebResource;
import open.dolphin.client.ClientContext;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;


/**
 * Bsiness Delegater のルートクラス。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class BusinessDelegater {

    protected static final String CAMMA = ",";

    protected static final String DATE_TIME_FORMAT_REST = "yyyy-MM-dd HH:mm:ss";

    protected Logger logger;

    protected boolean DEBUG;

    public BusinessDelegater() {
        logger = ClientContext.getDelegaterLogger();
        DEBUG = logger.getLevel() == Level.DEBUG ? true : false;
    }

    protected WebResource.Builder getResource(String path) {
        return JerseyClient.getInstance().getResource(path);
    }

    protected void debug(int status, String entity) {
        logger.debug("---------------------------------------");
        logger.debug("status = " + status);
        logger.debug(entity);
    }
}
