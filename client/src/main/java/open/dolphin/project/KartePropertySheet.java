package open.dolphin.project;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import open.dolphin.client.ClientContext;

/**
 * KartePropertySheet
 *
 * @author Minagawa,Kazushi
 */
public class KartePropertySheet extends AbstractPropertySheet {
    
    private KarteSettingBean bean;

    public KartePropertySheet() {
        super();
        String ID = "karteSetting";
        String TITLE = ClientContext.getMyBundle(KartePropertySheet.class).getString("title.karte");
        String ICON = "icon_karte_settings_small";
        this.setId(ID);
        this.setTitle(TITLE);
        this.setIcon(ICON);
    }
    
    @Override
    public void start() {

        bean = new KarteSettingBean();
        bean.populate();
        
        // Introspection
        BeanInfo info;
        try {
            info = Introspector.getBeanInfo(bean.getClass());
            PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
            
            String[] propertyOrder = bean.propertyOrder();
            String[] categoryAnchor = bean.categoryAnchor();
            String[] categoryNames = bean.categoryNames();
            java.util.ResourceBundle bundle = ClientContext.getMyBundle(bean.getClass());
            
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
                               
                        } else if (bean.isSpinnerProperty(pd.getName())) {
                            pd.setPropertyEditorClass(SpinnerEditorSupport.class);
                               
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
            
            getUI().setLayout(new BorderLayout());
            JTabbedPane tabbedPane = new JTabbedPane();
            getUI().add(tabbedPane);
            
            JPanel panel = new JPanel(new SpringLayout());
            int category = 0;
            int numRows = 0;
            
            for (PropertyDescriptor pd : descriptors) {
                
                PropertyEditor editor = getEditor(bean, pd);
                
                if (editor!=null) {
                    
                    if (editor instanceof TagEditorSupport) {
                        TagEditorSupport tagEditor = (TagEditorSupport)editor;
                        String[] tagValue = bean.getTags(pd.getName());
                        if (tagValue!=null) {
                            tagEditor.setTags(tagValue);
                        }
                    } else if (editor instanceof SpinnerEditorSupport) {
                        SpinnerEditorSupport spinnerEditor = (SpinnerEditorSupport)editor;
                        int[] spec = bean.getSpinnerSpec(pd.getName());
                        spinnerEditor.setMinValue(spec[0]);
                        spinnerEditor.setMaxValue(spec[1]);
                        spinnerEditor.setStepValue(spec[2]);
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
                    
                    if (pd.getName().equals(categoryAnchor[category])) {
                        open.dolphin.helper.SpringUtilities.makeCompactGrid(panel, numRows, 2, 6, 6, 6, 6);
                        tabbedPane.add(categoryNames[category], panel);
                        category++;
                        numRows=0;
                        panel = new JPanel(new SpringLayout());
                    }
                }
            }
        
        } catch (IntrospectionException ex) {
            Logger.getLogger(ConnectionPropertySheet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void save() {
        bean.store();
    }
}
