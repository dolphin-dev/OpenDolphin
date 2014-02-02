package open.dolphin.delegater;


import java.io.BufferedReader;
import java.util.List;
import javax.ws.rs.core.MediaType;
import open.dolphin.converter.LetterModuleConverter;
import open.dolphin.infomodel.LetterModule;
import open.dolphin.infomodel.LetterModuleList;
import open.dolphin.util.Log;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

/**
 * 紹介状用のデリゲータークラス。
 * @author Kazushi Minagawa.
 */
public final class LetterDelegater extends BusinessDelegater {

    private static final String PATH_FOR_LETTER = "/odletter/letter";
    private static final String PATH_FOR_LETTER_LIST = "/odletter/list";
    
    public LetterDelegater() {
    }
    
    public long saveOrUpdateLetter(LetterModule model) throws Exception {
        
        // Converter
        LetterModuleConverter conv = new LetterModuleConverter();
        conv.setModel(model);
        Log.outputFuncLog(Log.LOG_LEVEL_0,"I",model.getPatientId());
        // JSON
        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String json = mapper.writeValueAsString(conv);
        byte[] data = json.getBytes(UTF8);
        
        // PUT
        ClientRequest request = getRequest(PATH_FOR_LETTER);
        request.body(MediaType.APPLICATION_JSON, data);
        ClientResponse<String> response = request.put(String.class);
        
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","REQ",request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","PRM",MediaType.APPLICATION_JSON,json);
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","RES",response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5,"I","ENT",getString(response));
        
        // PK
        String entityStr = getString(response);
        return Long.parseLong(entityStr);
    }

    public LetterModule getLetter(long letterPk) throws Exception {

        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append(PATH_FOR_LETTER).append("/").append(letterPk);
        String path = sb.toString();
        Log.outputFuncLog(Log.LOG_LEVEL_0,"I",path);
        // GET
        ClientRequest request = getRequest(path);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.get(String.class);
        
        // LetterModule
        BufferedReader br = getReader(response);
        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        LetterModule ret = mapper.readValue(br, LetterModule.class);
        
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","REQ",request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","PRM",MediaType.APPLICATION_JSON);
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","RES",response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5,"I","ENT",getString(response));
        
        return ret;
    }


    public List<LetterModule> getLetterList(long kartePk) throws Exception {

        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append(PATH_FOR_LETTER_LIST).append("/").append(kartePk);
        String path = sb.toString();
        Log.outputFuncLog(Log.LOG_LEVEL_0,"I",path);
        // GET
        ClientRequest request = getRequest(path);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.get(String.class);
        
        // Wrapper
        BufferedReader br = getReader(response);
        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        LetterModuleList list = mapper.readValue(br, LetterModuleList.class);
        
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","REQ",request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","PRM",MediaType.APPLICATION_JSON);
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","RES",response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5,"I","ENT",getString(response));
        // List
        return list.getList();
    }


    public void delete(long pk) throws Exception {
        
        // PATH
        StringBuilder sb = new StringBuilder();
//s.oh^ 不具合修正
        //sb.append(PATH_FOR_LETTER);
        sb.append(PATH_FOR_LETTER).append("/");
//s.oh$
        sb.append(pk);
        String path = sb.toString();
        Log.outputFuncLog(Log.LOG_LEVEL_0,"I",path);
        // DELETE
        ClientRequest request = getRequest(path);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.delete(String.class);

        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","REQ",request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","PRM",MediaType.APPLICATION_JSON);
        Log.outputFuncLog(Log.LOG_LEVEL_3,"I","RES",response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5,"I","ENT",getString(response));
        // Check
        checkStatus(response);
    }
}
