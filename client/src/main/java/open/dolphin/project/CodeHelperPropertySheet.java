package open.dolphin.project;

import java.awt.Component;
import java.awt.Dimension;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import open.dolphin.client.ClientContext;

/**
 * CodeHelperPropertySheet
 *
 * @author Kazushi Minagawa
 */
public class CodeHelperPropertySheet extends AbstractPropertySheet {
    
    // Target bean
    private SettingBean bean;
    
    /** 
     * Creates a new instance of CodeHelperPropertySheet 
     */
    public CodeHelperPropertySheet() {
        super();
        String ID = "codeHelperSetting";
        String TITLE = ClientContext.getMyBundle(CodeHelperPropertySheet.class).getString("title.codeHelper");
        String ICON = "icon_code_helper_settings_small";
        this.setId(ID);
        this.setTitle(TITLE);
        this.setIcon(ICON);
    }
    
    @Override
    public void start() {

        bean = new CodeHelperSettingBean();
        bean.populate();
        
        // Introspection
        BeanInfo info;
        try {
            info = Introspector.getBeanInfo(bean.getClass());
            PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
            
            String[] propertyOrder = bean.propertyOrder();
            ResourceBundle bundle = ClientContext.getMyBundle(bean.getClass());
            
            for (PropertyDescriptor pd : descriptors) {
                
                for (int i=0; i < propertyOrder.length; i++) {
                    
                    if (pd.getName().equals(propertyOrder[i])) {
                        String pad = i < 10 ? "0" : "";
                        pd.setShortDescription(pad+String.valueOf(i));
                        pd.setDisplayName(bundle.getString(pd.getName()));
                        if (pd.getPropertyType().equals(boolean.class)) {
                            pd.setPropertyEditorClass(CheckBoxEditorSupport.class);
                        } else if (bean.isTagProperty(pd.getName())) {
                            pd.setPropertyEditorClass(TagEditorSupport.class);
                        }
                    }
                }
            }

            // Sort on display order
            Arrays.sort(descriptors, (Object o1, Object o2) -> {
                PropertyDescriptor pd1 = (PropertyDescriptor)o1;
                PropertyDescriptor pd2 = (PropertyDescriptor)o2;
                return pd1.getShortDescription().compareTo(pd2.getShortDescription());
            });
            
            BoxLayout box = new BoxLayout(getUI(), BoxLayout.Y_AXIS);
            getUI().setLayout(box);
            
            JPanel panel = new JPanel(new SpringLayout());
            int numRows = 0;
            
            for (PropertyDescriptor pd : descriptors) {
                
                PropertyEditor editor = getEditor(bean, pd);
                
                if (editor!=null) {
                    
                    if (editor instanceof TagEditorSupport) {
                        TagEditorSupport tagEditor = (TagEditorSupport)editor;
                        tagEditor.setTags(bean.getTags(pd.getName()));
                    }
                    
                    // Display name and it's component
                    String name = pd.getDisplayName();
                    Component c = this.getEditorComponent(bean, editor, pd);
                    
                    if (c instanceof JCheckBox) {
                        panel.add(new JLabel("", SwingConstants.RIGHT));
                    } else {
                        panel.add(new JLabel(name, SwingConstants.RIGHT));
                    }
                    int height = (int) c.getPreferredSize().getHeight();
                    c.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
                    panel.add(c);
                    numRows++;
                    
                    if (pd.getName().equals("modifierKey")) {
                        open.dolphin.helper.SpringUtilities.makeCompactGrid(panel, numRows, 2, 6, 6, 6, 6);
                        getUI().add(panel);
                        numRows=0;
                        panel = new JPanel(new SpringLayout());
                    } else if (pd.getName().equals("orca")) {
                        panel.add(new JLabel(""));
                        panel.add(new JLabel(""));
                        numRows++;
                        numRows /= 2;  // 4columns
                        open.dolphin.helper.SpringUtilities.makeCompactGrid(panel, numRows, 4, 6, 6, 6, 6);
                        getUI().add(panel);
                    }
                }
            }
            
            check(bean);
        
        } catch (IntrospectionException ex) {
            Logger.getLogger(ConnectionPropertySheet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void save() {
        bean.store();
    }
}












