package open.dolphin.msg;

import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.List;
import java.util.logging.Logger;
import open.dolphin.infomodel.DocumentModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.ModuleModel;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

/**
 *
 * @author kazushi
 */
public class MMLSender {
    
    private static final String OBJECT_NAME = "mmlHelper";
    private static final String TEMPLATE_NAME = "mmlHelper.vm";
    private static final String TEMPLATE_ENC = "SHIFT_JIS";
    
    private boolean DEBUG;
    
    public MMLSender() {
        DEBUG = Logger.getLogger("open.dolphin").getLevel().equals(java.util.logging.Level.FINE);
    }
    
    public void send(DocumentModel dm) throws Exception {
        
        if (DEBUG) {
            log("patientId = " + dm.getKarteBean().getPatientModel().getPatientId());
            log("patientName = " + dm.getKarteBean().getPatientModel().getFullName());
            log("userId = " + dm.getUserModel().getUserId());
            log("userName = " + dm.getUserModel().getCommonName());
        }

        // decode
        List<ModuleModel> modules = dm.getModules();
        for (ModuleModel mm : modules) {
            mm.setModel((IInfoModel)this.xmlDecode(mm.getBeanBytes()));
        }

        MMLHelper helper = new MMLHelper();
        helper.setDocument(dm);
        helper.buildText();

        VelocityContext context = new VelocityContext();
        context.put(OBJECT_NAME, helper);
        StringWriter sw = new StringWriter();
        BufferedWriter bw = new BufferedWriter(sw);
        Velocity.mergeTemplate(TEMPLATE_NAME, TEMPLATE_ENC, context, bw);
        bw.flush();
        bw.close();
        String mml = sw.toString();
        if (DEBUG) {
            log(mml);
        }
    }

    private void log(String msg) {
        Logger.getLogger("open.dolphin").info(msg);
    }
    
    private void warning(String msg) {
        Logger.getLogger("open.dolphin").warning(msg);
    }
    
    private Object xmlDecode(byte[] bytes)  {

        XMLDecoder d = new XMLDecoder(
                new BufferedInputStream(
                new ByteArrayInputStream(bytes)));

        return d.readObject();
    }
}
