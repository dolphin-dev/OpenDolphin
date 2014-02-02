package open.dolphin.delegater;


import java.io.BufferedReader;
import java.util.List;
import javax.ws.rs.core.MediaType;
import open.dolphin.converter14.LetterModuleConverter;
import open.dolphin.infomodel.LetterModule;
import open.dolphin.infomodel.LetterModuleList;
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
        
        // JSON
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(conv);
        byte[] data = json.getBytes(UTF8);
        
        // PUT
        ClientRequest request = getRequest(PATH_FOR_LETTER);
        request.body(MediaType.APPLICATION_JSON, data);
        ClientResponse<String> response = request.put(String.class);
        
        // PK
        String entityStr = getString(response);
        return Long.parseLong(entityStr);
    }

    public LetterModule getLetter(long letterPk) throws Exception {

        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append(PATH_FOR_LETTER).append("/").append(letterPk);
        String path = sb.toString();
        
        // GET
        ClientRequest request = getRequest(path);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.get(String.class);
        
        // LetterModule
        BufferedReader br = getReader(response);
        ObjectMapper mapper = new ObjectMapper();
        LetterModule ret = mapper.readValue(br, LetterModule.class);
        
        return ret;
    }


    public List<LetterModule> getLetterList(long kartePk) throws Exception {

        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append(PATH_FOR_LETTER_LIST).append("/").append(kartePk);
        String path = sb.toString();
        
        // GET
        ClientRequest request = getRequest(path);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.get(String.class);
        
        // Wrapper
        BufferedReader br = getReader(response);
        ObjectMapper mapper = new ObjectMapper();
        LetterModuleList list = mapper.readValue(br, LetterModuleList.class);
        
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
        
        // DELETE
        ClientRequest request = getRequest(path);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.delete(String.class);

        // Check
        checkStatus(response);
    }
}
