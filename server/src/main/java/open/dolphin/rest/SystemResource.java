package open.dolphin.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import open.dolphin.infomodel.ActivityModel;
import open.dolphin.infomodel.RoleModel;
import open.dolphin.infomodel.UserModel;
import open.dolphin.session.SystemServiceBean;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * REST Web Service
 *
 * @author kazushi
 */
@Path("/dolphin")
public class SystemResource extends AbstractResource {
    
    @Inject
    private SystemServiceBean systemServiceBean;

    /** Creates a new instance of SystemResource */
    public SystemResource() {
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hellowDolphin() {
        return "Hellow, Dolphin";
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void addFacilityAdmin(String json) throws IOException {
        
        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        UserModel user = mapper.readValue(json, UserModel.class);

        // 関係を構築する
        List<RoleModel> roles = user.getRoles();
        for (RoleModel role : roles) {
            role.setUserModel(user);
        }

        systemServiceBean.addFacilityAdmin(user);
    }
    
    @GET
    @Path("/activity/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ActivityModel> getActivities(@Context HttpServletRequest servletReq, @PathParam("param") String param) {
        
        // Parameters
        String[] params = param.split(CAMMA);
        int year = Integer.parseInt(params[0]);     // 集計起点年
        int month = Integer.parseInt(params[1]);    // 集計起点月
        int count = Integer.parseInt(params[2]);    // 過去何ヶ月
        
        String fid = getRemoteFacility(servletReq.getRemoteUser());
        
        ActivityModel[] array = new ActivityModel[count+1]; // +1=total
        
        // ex month=5,past=-3 -> 3,4,5
        GregorianCalendar gcFirst = new GregorianCalendar(year, month, 1);
        int numDays = gcFirst.getActualMaximum(Calendar.DAY_OF_MONTH);
        
        int index = array.length-2;
        while (true) {
            GregorianCalendar gcLast = new GregorianCalendar(year, month, numDays, 23,59,59);
            ActivityModel am = systemServiceBean.countActivities(fid, gcFirst.getTime(), gcLast.getTime());
            array[index]=am;
            
            index--;
            if (index < 0) {
                break;
            }
            gcFirst.add(Calendar.MONTH, -1);
            year = gcFirst.get(Calendar.YEAR);
            month = gcFirst.get(Calendar.MONTH);
            numDays = gcFirst.getActualMaximum(Calendar.DAY_OF_MONTH);
        }
        
        // 総数
        ActivityModel am = systemServiceBean.countTotalActivities(fid);
        array[array.length-1] =am;
        
        return Arrays.asList(array);
    }
    
//s.oh^ 2014/07/08 クラウド0対応
    @POST
    @Path("/license")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String checkLicense(String uid) throws IOException {
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Properties config = new Properties();
        StringBuilder sb = new StringBuilder();
        sb.append(System.getProperty("jboss.home.dir"));
        sb.append(File.separator);
        sb.append("license.properties");
        File f = new File(sb.toString());
        try {
            FileInputStream fin = new FileInputStream(f);
            InputStreamReader isr = new InputStreamReader(fin, "UTF-8");
            config.load(isr);
            isr.close();
            fin.close();
        } catch (IOException ex) {
            Logger.getLogger("open.dolphin").warning("ライセンスファイル読込エラー");
            ex.printStackTrace(System.err);
            return "2";
        }
        
        String val = config.getProperty("license.max", "3");
        int max = Integer.parseInt(val);
        for(int i = 0; i < max; i++) {
            sb = new StringBuilder();
            sb.append("license.uid");
            sb.append(String.valueOf(i+1));
            val = config.getProperty(sb.toString());
            if(val == null) {
                config.setProperty(sb.toString(), uid);
                try {
                    FileOutputStream fon = new FileOutputStream(f);
                    config.store(fon, "OpenDolphinZero License");
                    fon.close();
                } catch (IOException ex) {
                    Logger.getLogger("open.dolphin").warning("ライセンスファイル保存エラー");
                    ex.printStackTrace(System.err);
                    return "3";
                }
                Logger.getLogger("open.dolphin").info("ライセンス新規登録");
                return "0";
            }else{
                if(val.equals(uid)) {
                    Logger.getLogger("open.dolphin").info("ライセンス登録済");
                    return "0";
                }
            }
        }
        
        Logger.getLogger("open.dolphin").warning("ライセンス認証の制限数を超えました");
        return "4";
    }
//s.oh$
    
//s.oh^ 2014/07/08 クラウド0対応
    @GET
    @Path("/cloudzero/sendmail")
    public void sendCloudZeroMail() {
        Logger.getLogger("open.dolphin").info("Send CloudZero mail.");
        
        GregorianCalendar gc = new GregorianCalendar();
        gc.add(Calendar.MONTH, -1);
        int year = gc.get(Calendar.YEAR);
        int month = gc.get(Calendar.MONTH);
        systemServiceBean.sendMonthlyActivities(year, month);
    }
//s.oh$
    
    /**
     * 
     * ResteasyClient client = new ResteasyClientBuilder().build();
     * 
    ResteasyWebTarget target = client.target("http://.../upload");

    MultipartFormDataOutput mdo = new MultipartFormDataOutput();
    mdo.addFormData("file", new FileInputStream(new File("....thermo.wav")),    MediaType.APPLICATION_OCTET_STREAM_TYPE);
    GenericEntity<MultipartFormDataOutput> entity = new GenericEntity<MultipartFormDataOutput>(mdo) {};

    Response r = target.request().post( Entity.entity(entity, MediaType.MULTIPART_FORM_DATA_TYPE));
     */
}
