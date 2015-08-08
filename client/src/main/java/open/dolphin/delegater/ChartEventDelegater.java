package open.dolphin.delegater;

import java.util.concurrent.Future;
import javax.ws.rs.core.MediaType;
import open.dolphin.converter.ChartEventModelConverter;
import open.dolphin.infomodel.ChartEventModel;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

/**
 * State変化関連のデレゲータ
 * @author masuda, Masuda Naika
 * 
 * オリジナル: Git Hub Masuda-Naika OpenDolphin-2.3.8m
 * 上記の劣化版コピー: minagawa
 *  JSON のハンドリングを reasteasyのjacksonで行う
 *  resteasyクライントが非同期通信をサポートしていないのでAsyncHttpClientを使用する  
 */
public class ChartEventDelegater extends BusinessDelegater {
    
    private static final String RES_CE = "/chartEvent";
    private static final String SUBSCRIBE_PATH = RES_CE + "/subscribe";
    private static final String PUT_EVENT_PATH = RES_CE + "/event";
    
    private static final ChartEventDelegater instance = new ChartEventDelegater();
    
    private ChartEventDelegater() {
    }
    
    public static ChartEventDelegater getInstance() {
        return instance;
    }
    
    public int putChartEvent(ChartEventModel evt) throws Exception {
        
        // Convert
        ChartEventModelConverter conv = new ChartEventModelConverter();
        conv.setModel(evt);
        
        // JSON
        ObjectMapper mapper = this.getSerializeMapper();
        byte[] data = mapper.writeValueAsBytes(conv);
        
        // PUT
        String countStr = putEasyJson(PUT_EVENT_PATH, data, String.class);
        
        // cnt
        return Integer.parseInt(countStr);      
    }
    
    public Future<ChartEventModel> subscribe() throws Exception {
        ResteasyWebTarget target = getWebTargetSubscribe(SUBSCRIBE_PATH);
        return target.request(MediaType.APPLICATION_JSON).async().get(ChartEventModel.class);
    }
}
