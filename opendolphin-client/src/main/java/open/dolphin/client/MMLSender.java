package open.dolphin.client;

import java.io.*;
import open.dolphin.infomodel.DocumentModel;
import open.dolphin.message.MMLHelper;
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
        MMLHelper mb = new MMLHelper();
        mb.setDocument(model);
        mb.setUser(Project.getUserModel());
        mb.setPatientId(context.getPatient().getPatientId());
        mb.buildText();

        try {
            VelocityContext vct = ClientContext.getVelocityContext();
            vct.put("mmlHelper", mb);

            // このスタンプのテンプレートファイルを得る
            String templateFile = "mml2.3Helper.vm";

            // Merge する
            StringWriter sw = new StringWriter();
            BufferedWriter bw = new BufferedWriter(sw);
            InputStream instream = ClientContext.getTemplateAsStream(templateFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(instream, "SHIFT_JIS"));
            Velocity.evaluate(vct, bw, "mml", reader);
            bw.flush();
            bw.close();
            reader.close();
            String mml = sw.toString();
            //System.out.println(mml);

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
