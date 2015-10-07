package open.dolphin.project;

import java.awt.Component;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextField;

/**
 *
 * @author kazushi Minagawa
 */
public class TextFieldSetter {

    private final SettingBean bean;
    private final PropertyDescriptor pd;
    private final PropertyEditor editor;
    private final Component c;

    public TextFieldSetter(SettingBean bean, PropertyDescriptor pd, PropertyEditor editor, Component c) {
        this.bean = bean;
        this.pd = pd;
        this.editor = editor;
        this.c = c;
    }

    public void setText(String text) {
        JTextField tf = (JTextField) c;
        tf.setText(text);
        editor.setAsText(tf.getText());
        Method setter = pd.getWriteMethod();
        try {
            setter.invoke(bean, new Object[]{editor.getValue()});
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(ConnectionPropertySheet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
