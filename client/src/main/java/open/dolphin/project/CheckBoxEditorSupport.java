package open.dolphin.project;

import java.beans.PropertyEditorSupport;

/**
 *
 * @author Kazushi Minagawa.
 */
public final class CheckBoxEditorSupport extends PropertyEditorSupport {
    
    private boolean on;
    
    @Override
    public String[] getTags() {
        return new String[]{"any"};
    }
    
    @Override
    public Object getValue() {
        return on;
    }
    
    @Override
    public void setValue(Object o) {
        on = (Boolean)o;
    }
    
    @Override
    public String getAsText() {
        return String.valueOf(on);
    }
    
    @Override
    public void setAsText(String text) {
        on = Boolean.parseBoolean(text);
    }
}
