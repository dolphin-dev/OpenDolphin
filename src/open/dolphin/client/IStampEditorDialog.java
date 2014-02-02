package open.dolphin.client;

import javax.swing.JButton;


public interface IStampEditorDialog {
    
    public static final String EDITOR_VALUE_PROP = "editorValueProp";
    
    public JButton getOkButton();
    
    public void close();
    
}
