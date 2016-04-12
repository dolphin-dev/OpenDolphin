package open.dolphin.system;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import open.dolphin.converter.UserModelConverter;
import open.dolphin.delegater.BusinessDelegater;
import open.dolphin.infomodel.UserModel;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class SystemDelegater extends BusinessDelegater {

    private final String PATH = "/dolphin";
    private final String BASE_URI = "https://i18n.opendolphin.com:443/openDolphin/resources";
    private final String USER_ID = "1.3.6.1.4.1.9414.10.1:dolphin";
    private final String USER_PASSWORD = "dolphin";

    public SystemDelegater() {
    }

    /**
     * 通信テストを行う。
     * @return hellow
     * @throws Exception
     */
    public String hellow() throws Exception {
        ResteasyWebTarget target = getWebTarget(BASE_URI, PATH, USER_ID, USER_PASSWORD);
        String entityStr = target.request(MediaType.TEXT_PLAIN).get(String.class);   
        return entityStr;
    }

    /**
     * 施設ユーザーアカウントを登録する。
     * @param user 登録するユーザー
     * @throws Exception
     */
    public String addFacilityUser(UserModel user) throws Exception {
        
        // Converter
        UserModelConverter conv = new UserModelConverter();
        conv.setModel(user);
        ObjectMapper mapper = this.getSerializeMapper();
        byte[] data = mapper.writeValueAsBytes(conv);
        ResteasyWebTarget target = getWebTarget(BASE_URI, PATH, USER_ID, USER_PASSWORD);
        String res = target.request().post(Entity.json(data), String.class);
        
        return res;
    }
    
//s.oh^ 2014/07/08 クラウド0対応
    public void sendCloudZeroMail() throws Exception {
        String path = PATH + "/cloudzero/sendmail";
        ResteasyWebTarget target = getWebTarget(path);
        String entityStr = target.request(MediaType.APPLICATION_JSON).get(String.class);        
    }
//s.oh$
}
