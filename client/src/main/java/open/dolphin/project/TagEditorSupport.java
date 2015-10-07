package open.dolphin.project;

import java.beans.PropertyEditorSupport;

/**
 *
 * @author Kazushi Minagawa
 */
public class TagEditorSupport extends PropertyEditorSupport {
    
    private String value;
    
    private String[] tags;
    
    @Override
    public String[] getTags() {
        return tags;
    }
    
    public void setTags(String[] tags) {
        this.tags = tags;
    }
    
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
