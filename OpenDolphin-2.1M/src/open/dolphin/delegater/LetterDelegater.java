package open.dolphin.delegater;

import com.sun.jersey.api.client.ClientResponse;
import java.util.List;
import javax.ws.rs.core.MediaType;
import open.dolphin.converter.PlistConverter;
import open.dolphin.converter.PlistParser;
import open.dolphin.infomodel.LetterModel;
import open.dolphin.infomodel.LetterModule;
import open.dolphin.infomodel.TouTouLetter;
import open.dolphin.infomodel.TouTouReply;
import open.dolphin.util.BeanUtils;

/**
 * 紹介状用のデリゲータークラス。
 * @author Kazushi Minagawa.
 */
public class LetterDelegater extends BusinessDelegater {

    private static final String PATH_FOR_LETTER = "odletter/letter/";
    private static final String PATH_FOR_LETTER_LIST = "odletter/list/";
    
    public LetterDelegater() {
    }
    
    public long saveOrUpdateLetter(LetterModule model) throws Exception {
        
        String repXml = null;

        PlistConverter con = new PlistConverter();
        repXml = con.convert(model);

        if (repXml == null) {
            return 0L;
        }

        ClientResponse response = getResource(PATH_FOR_LETTER)
                    .type(MediaType.APPLICATION_XML_TYPE)
                    .put(ClientResponse.class, repXml);

        int status = response.getStatus();
        String entityStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, entityStr);
        }

        long pk = Long.parseLong(entityStr);
        return pk;
    }

    public LetterModule getLetter(long letterPk) {

        LetterModule ret = null;

        StringBuilder sb = new StringBuilder();
        sb.append(PATH_FOR_LETTER);
        sb.append(letterPk);
        String path = sb.toString();

        ClientResponse response = getResource(path)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(ClientResponse.class);

        int status = response.getStatus();
        String entityStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, entityStr);
        }

        PlistParser con = new PlistParser();
        ret = (LetterModule) con.parse(entityStr);
        return ret;
    }


    public List<LetterModule> getLetterList(long kartePk) {

        List<LetterModule> ret = null;

        StringBuilder sb = new StringBuilder();
        sb.append(PATH_FOR_LETTER_LIST);
        sb.append(kartePk);
        String path = sb.toString();

        ClientResponse response = getResource(path)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(ClientResponse.class);

        int status = response.getStatus();
        String entityStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, entityStr);
        }

        PlistParser con = new PlistParser();
        ret = (List<LetterModule>) con.parse(entityStr);
        return ret;
    }


    public void delete(long pk) {
        StringBuilder sb = new StringBuilder();
        sb.append(PATH_FOR_LETTER);
        sb.append(pk);
        String path = sb.toString();

        ClientResponse response = getResource(path)
                .accept(MediaType.TEXT_PLAIN)
                .delete(ClientResponse.class);

        int status = response.getStatus();
        if (DEBUG) {
            debug(status, "delete response");
        }
    }


    public void convert() {
        ClientResponse response = getResource(PATH_FOR_LETTER)
                    .type(MediaType.APPLICATION_XML_TYPE)
                    .post(ClientResponse.class);
    }


    public String getOldLetterPks() {

        StringBuilder sb = new StringBuilder();
        sb.append("odletter/oldletter/pk/");
        String path = sb.toString();

        ClientResponse response = getResource(path)
                .accept(MediaType.TEXT_PLAIN)
                .get(ClientResponse.class);

        int status = response.getStatus();
        String entityStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, entityStr);
        }

        return entityStr;
    }

        
    public String getOldReplyPks() {

        StringBuilder sb = new StringBuilder();
        sb.append("odletter/oldreply/pk/");
        String path = sb.toString();

        ClientResponse response = getResource(path)
                .accept(MediaType.TEXT_PLAIN)
                .get(ClientResponse.class);

        int status = response.getStatus();
        String entityStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, entityStr);
        }

        return entityStr;
    }


    public LetterModel getOldLetter(long letterPk) {

        LetterModel ret = null;

        StringBuilder sb = new StringBuilder();
        sb.append("karte/letter/");
        sb.append(letterPk);
        String path = sb.toString();

        ClientResponse response = getResource(path)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(ClientResponse.class);

        int status = response.getStatus();
        String entityStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, entityStr);
        }

        PlistParser con = new PlistParser();
        TouTouLetter result = (TouTouLetter) con.parse(entityStr);
        byte[] bytes = result.getBeanBytes();
        ret = (LetterModel) BeanUtils.xmlDecode(bytes);
        ret.setId(result.getId());
        ret.setBeanBytes(null);
        return ret;
    }


    public LetterModel getOldLetterReply(long letterPk) {

        LetterModel ret = null;

        StringBuilder sb = new StringBuilder();
        sb.append("karte/reply/");
        sb.append(letterPk);
        String path = sb.toString();

        ClientResponse response = getResource(path)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(ClientResponse.class);

        int status = response.getStatus();
        String entityStr = response.getEntity(String.class);

        if (DEBUG) {
            debug(status, entityStr);
        }

        PlistParser con = new PlistParser();
        TouTouReply result = (TouTouReply) con.parse(entityStr);
        byte[] bytes = result.getBeanBytes();
        ret = (LetterModel) BeanUtils.xmlDecode(bytes);
        ret.setId(result.getId());
        ret.setBeanBytes(null);
        return ret;
    }
}
