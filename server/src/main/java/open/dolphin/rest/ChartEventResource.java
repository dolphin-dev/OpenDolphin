package open.dolphin.rest;

import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import open.dolphin.converter.ChartEventModelConverter;
import open.dolphin.infomodel.ChartEventModel;
import open.dolphin.mbean.ServletContextHolder;
import open.dolphin.session.ChartEventServiceBean;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * ChartEventResource
 * @author masuda, Masuda Naika
 * 
 * minagawa^ OpenDolphin/Pro のパスに合うように変更点
 * @Path, DISPATCH_URL
 */
@Path("/chartEvent")
public class ChartEventResource extends AbstractResource {
    
    private static final boolean debug = false;
    
    private static final int asyncTimeout = 60 * 1000 * 60 *24; // 60 minutes*24
    
    public static final String CLIENT_UUID = "clientUUID";
    public static final String FID = "fid";
    public static final String DISPATCH_URL = "/resources/chartEvent/dispatch";
    public static final String KEY_NAME = "chartEvent";
    
    @Inject
    private ChartEventServiceBean eventServiceBean;
    
    @Inject
    private ServletContextHolder contextHolder;
    
    @Context
    private HttpServletRequest servletReq;
    
    @GET
    @Path("/subscribe")
    public void subscribe() {

        String fid = getRemoteFacility(servletReq.getRemoteUser());
        String clientUUID = servletReq.getHeader(CLIENT_UUID);
//minagawa^        
        if (debug) {
            StringBuilder sb = new StringBuilder();
            sb.append(fid).append(":").append(clientUUID);
            sb.append(" did request subscribe");
            debug(sb.toString());
        }
//minagawa$        
        
        final AsyncContext ac = servletReq.startAsync();
        // timeoutを設定
        ac.setTimeout(asyncTimeout);
        // requestにfid, clientUUIDを記録しておく
        ac.getRequest().setAttribute(FID, fid);
        ac.getRequest().setAttribute(CLIENT_UUID, clientUUID);
        contextHolder.addAsyncContext(ac);
        
//minagawa^
        int subscribers = contextHolder.getAsyncContextList().size();
        debug("subscribers count = " + subscribers);
//minagawa$        
        
        ac.addListener(new AsyncListener() {

            private void remove() {
                // JBOSS終了時にぬるぽ？
                try {
                    contextHolder.removeAsyncContext(ac);
                } catch (NullPointerException ex) {
                }
            }

            @Override
            public void onComplete(AsyncEvent event) throws IOException {
            }

            @Override
            public void onTimeout(AsyncEvent event) throws IOException {
                remove();
                //System.out.println("ON TIMEOUT");
                //event.getThrowable().printStackTrace(System.out);
            }

            @Override
            public void onError(AsyncEvent event) throws IOException {
                remove();
                //System.out.println("ON ERROR");
                //event.getThrowable().printStackTrace(System.out);
            }

            @Override
            public void onStartAsync(AsyncEvent event) throws IOException {
            }
        });
    }
    
    @PUT
    @Path("/event")
    @Consumes()
    @Produces(MediaType.APPLICATION_JSON)
    public String putChartEvent(String json) throws IOException {
        
//minagawa^ resteasyを使用
//        ChartEventModel msg = (ChartEventModel)
//                getConverter().fromJson(json, ChartEventModel.class);
//        int cnt = eventServiceBean.processChartEvent(msg);
//        return String.valueOf(cnt);
        debug("putChartEvent did call");
        ObjectMapper mapper = new ObjectMapper();
        ChartEventModel msg = mapper.readValue(json, ChartEventModel.class);
        int cnt = eventServiceBean.processChartEvent(msg);
        return String.valueOf(cnt);
//minagawa$        
    }
    
    // 参：きしだのはてな もっとJavaEE6っぽくcometチャットを実装する
    // http://d.hatena.ne.jp/nowokay/20110416/1302978207
    @GET
    @Path("/dispatch")
    @Produces(MediaType.APPLICATION_JSON)
    public ChartEventModelConverter deliverChartEvent() {
        
//minagawa^ resteasyを使用
//        ChartEventModel msg = (ChartEventModel)servletReq.getAttribute(KEY_NAME);
//        String json = getConverter().toJson(msg);
//        return json;
        debug("deliverChartEvent did call");
        ChartEventModel msg = (ChartEventModel)servletReq.getAttribute(KEY_NAME);
        ChartEventModelConverter conv = new ChartEventModelConverter();
        conv.setModel(msg);
        return conv;
//minagawa$          
    }

    @Override
    protected void debug(String msg) {
        if (debug || DEBUG) {
            super.debug(msg);
        }
    }
}
