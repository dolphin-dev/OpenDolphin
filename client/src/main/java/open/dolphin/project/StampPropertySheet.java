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
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import open.dolphin.client.ClientContext;

/**
 * StampPropertySheet
 *
 * @author Minagawa,Kazushi
 */
public class StampPropertySheet extends AbstractPropertySheet {
    
    private SettingBean bean;

    public StampPropertySheet() {
        super();
        String ID = "stampSetting";
        String TITLE = ClientContext.getMyBundle(StampPropertySheet.class).getString("title.stamp");
        String ICON = "icon_stamp_settings_small";
        this.setId(ID);
        this.setTitle(TITLE);
        this.setIcon(ICON);
    }

    @Override
    public void start() {
        
        bean = new StampSettingBean();
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
                        } else if (bean.isDecimalProperty(pd.getName())) {
                            pd.setPropertyEditorClass(DecimalEditorSupport.class);
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
            
            getUI().setLayout(new SpringLayout());
            int numRows = 0;
            
            for (PropertyDescriptor pd : descriptors) {
                
                PropertyEditor editor = getEditor(bean, pd);
                
                if (editor!=null) {
                    
                    numRows++;
                    
                    if (editor instanceof TagEditorSupport) {
                        TagEditorSupport tagEditor = (TagEditorSupport)editor;
                        tagEditor.setTags(bean.getTags(pd.getName()));
                    }
                    
                    // Display name and it's component
                    String name = pd.getDisplayName();
                    Component c = this.getEditorComponent(bean, editor, pd);
                    
                    if (c instanceof JCheckBox) {
                        getUI().add(new JLabel("", SwingConstants.RIGHT));
                    } else {
                        getUI().add(new JLabel(name, SwingConstants.RIGHT));
                    }
                    int height = (int) c.getPreferredSize().getHeight();
                    c.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
                    getUI().add(c);
                }
            }
            
            // Form layout
            open.dolphin.helper.SpringUtilities.makeCompactGrid(getUI(), 
                    numRows, 2, 6, 6, 6, 6);
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
