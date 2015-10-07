package open.dolphin.project;

import java.beans.PropertyEditorSupport;

/**
 *
 * @author Kazushi Minagawa
 */
public class DecimalEditorSupport extends PropertyEditorSupport {
    
    private String value;
    
    @Override
    public Object getValue() {
        return value;
    }
    
    @Override
    public void setValue(Object o) {
        value = (String)o;
    }
    
    @Override
    public String getAsText() {
        return value;
    }
    
    @Override
    public void setAsText(String text) {
        value = text;
    }
}
