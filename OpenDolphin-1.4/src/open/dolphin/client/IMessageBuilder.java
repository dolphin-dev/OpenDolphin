package open.dolphin.client;

/**
 * @author Kazushi Minagawa, Digital Globe, Inc.
 *
 */
public interface IMessageBuilder {
    
    public String getTemplateFile();
    
    public void setTemplateFile(String fileName);
    
    public String getEncoding();
    
    public void setEncoding(String encoding);
    
    public String build(String dml);
    
}
