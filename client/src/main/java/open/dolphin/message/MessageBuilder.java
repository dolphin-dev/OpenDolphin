package open.dolphin.message;

import java.io.*;
import open.dolphin.client.ClientContext;
import open.dolphin.project.Project;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

/**
 * DML を 任意のMessage に翻訳するクラス。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 *
 */
public final class MessageBuilder {
    //public class MessageBuilder implements IMessageBuilder {
    
    private static final String ENCODING = "SHIFT_JIS";
    
    /** テンプレートファイル */
    private String templateFile;
    
    /** テンプレートファイルのエンコーディング */
    private String encoding = ENCODING;
    
    
    public MessageBuilder() {
        java.util.logging.Logger.getLogger(this.getClass().getName()).fine("MessageBuilder constracted");
    }
    
    public String getTemplateFile() {
        return templateFile;
    }
    
    public void setTemplateFile(String templateFile) {
        this.templateFile = templateFile;
    }
    
    public String getEncoding() {
        return encoding;
    }
    
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
    
    public String build(Object helper) {
        
        java.util.logging.Logger.getLogger(this.getClass().getName()).fine("MessageBuilder build");
        
        String ret = null;
        String name = helper.getClass().getName();
        int index = name.lastIndexOf('.');
        name = name.substring(index+1);
        StringBuilder sb = new StringBuilder();
        sb.append(name.substring(0,1).toLowerCase());
        sb.append(name.substring(1));
        name = sb.toString();
        
        try {
            java.util.logging.Logger.getLogger(this.getClass().getName()).fine("MessageBuilder try");
            VelocityContext context = ClientContext.getVelocityContext();
            context.put(name, helper);
            
            // このスタンプのテンプレートファイルを得る
            String tFile;
//s.oh^ 2014/03/13 傷病名削除診療科対応
            if (Project.getBoolean(Project.CLAIM_01)) {
                tFile = name + "_01.vm";
            } else {
                tFile = name + ".vm";
            }
//            tFile = name + "_02.vm";
//s.oh$
            java.util.logging.Logger.getLogger(this.getClass().getName()).fine("template file = " + tFile);
            
            // Merge する
            StringWriter sw = new StringWriter();
            BufferedWriter bw = new BufferedWriter(sw);
            InputStream instream = ClientContext.getTemplateAsStream(tFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(instream, encoding));
            Velocity.evaluate(context, bw, name, reader);
            java.util.logging.Logger.getLogger(this.getClass().getName()).fine("Velocity.evaluated");
            bw.flush();
            bw.close();
            reader.close();
            
            ret = sw.toString();
            
        } catch (ParseErrorException | MethodInvocationException | ResourceNotFoundException | IOException e) {
            java.util.logging.Logger.getLogger(this.getClass().getName()).warning(e.getMessage());
        }
        
        return ret;
    }
}
