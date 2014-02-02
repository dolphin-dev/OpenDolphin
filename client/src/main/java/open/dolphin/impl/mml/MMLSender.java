package open.dolphin.impl.mml;

import java.io.*;
import open.dolphin.client.Chart;
import open.dolphin.client.ClientContext;
import open.dolphin.client.IKarteSender;
import open.dolphin.client.MmlMessageEvent;
import open.dolphin.client.MmlMessageListener;
import open.dolphin.infomodel.DocumentModel;
import open.dolphin.project.Project;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

/**
 *
 * @author Kazushi Minagawa. Digital Globe, Inc.
 */
public class MMLSender implements IKarteSender {
    
    private Chart context;
    
    private MmlMessageListener mmlListener;

    @Override
    public Chart getContext() {
        return context;
    }

    @Override
    public void setContext(Chart context) {
        this.context = context;
    }

    @Override
    public void prepare(DocumentModel data) {
        if (data.getDocInfoModel().isSendMml()) {
            mmlListener = context.getMMLListener();
        }
    }

    @Override
    public void send(DocumentModel model) {

        if ((!model.getDocInfoModel().isSendMml()) || mmlListener==null) {
            return;
        }

        // MML Message を生成する
        MMLHelper23 mb = new MMLHelper23();
        mb.setDocument(model);
        mb.setUserModel(Project.getUserModel());
        mb.setPatientId(context.getPatient().getPatientId());
        mb.buildText();
        
        // 患者情報
        PatientHelper ph = new PatientHelper();
        ph.setPatient(getContext().getPatient());
        ph.setFacility(Project.getUserModel().getFacilityModel().getFacilityName());

        try {
            VelocityContext vct = ClientContext.getVelocityContext();
            vct.put("mmlHelper", mb);
            vct.put("patientHelper", ph);

            // このスタンプのテンプレートファイルを得る
            String templateFile = "mml2.3Helper.vm";

            // Merge する
            StringWriter sw = new StringWriter();
            BufferedWriter bw = new BufferedWriter(sw);
            InputStream instream = ClientContext.getTemplateAsStream(templateFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(instream, "UTF-8"));
            Velocity.evaluate(vct, bw, "mml", reader);
            bw.flush();
            bw.close();
            reader.close();
            String mml = sw.toString();
            //System.err.println(mml);

            // debug出力を行う
            if (ClientContext.getMmlLogger() != null) {
                ClientContext.getMmlLogger().debug(mml);
            }

            MmlMessageEvent mevt = new MmlMessageEvent(this);
            mevt.setGroupId(mb.getDocId());
            mevt.setMmlInstance(mml);
            if (mb.getSchema() != null) {
                mevt.setSchema(mb.getSchema());
            }
            mmlListener.mmlMessageEvent(mevt);

            if (Project.getBoolean(Project.JOIN_AREA_NETWORK)) {
            // TODO
            }

        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}
