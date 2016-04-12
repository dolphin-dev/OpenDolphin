package open.dolphin.project;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import open.dolphin.client.ClientContext;
import open.dolphin.client.ServerInfo;
import open.dolphin.system.AddFacilityDialog;

/**
 * ConnectionPropertySheet
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class ConnectionPropertySheet extends AbstractPropertySheet {
    
    // Target
    private SettingBean bean;
    
    // For account make
    private TextFieldSetter facilityIdSetter;
    private TextFieldSetter userIdSetter;
    
    public ConnectionPropertySheet() {
        super();
        String ID = "hostSetting";
        String TITLE = ClientContext.getMyBundle(ConnectionPropertySheet.class).getString("title.baseURISetting");
        String ICON = "icon_server_settings_small"; 
        this.setId(ID);
        this.setTitle(TITLE);
        this.setIcon(ICON);
    }
    
    @Override
    public void start() {
        
        // Creates a connection bean and populate
        bean = new ConnectionSettingBean();
        bean.populate();
        
        // Introspection
        BeanInfo info;
        try {
            info = Introspector.getBeanInfo(bean.getClass());
            PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
            
            // Property as ordered array
            String[] propertyOrder = bean.propertyOrder();
            ResourceBundle bundle = ClientContext.getMyBundle(bean.getClass());
            
            for (PropertyDescriptor pd : descriptors) {
                
                for (int i=0; i < propertyOrder.length; i++) {
                    
                    if (pd.getName().equals(propertyOrder[i])) {
                        
                        // To sort
                        String pad = i < 10 ? "0" : "";
                        pd.setShortDescription(pad+String.valueOf(i));
                        
                        // Display name from resource
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
                    
                    // RegisterComponent(c)
                    if (pd.getName().equals("facilityId")) {
                        facilityIdSetter = new TextFieldSetter(bean, pd, editor, c);
                    } else if (pd.getName().equals("userId")) {
                        userIdSetter = new TextFieldSetter(bean, pd, editor, c);
                    }
                }
            }
            
//minagawa^ 評価アカウント作成
            // Add button to create test account
            if (ClientContext.isI18N()) {
                String text = ClientContext.getMyBundle(ConnectionPropertySheet.class).getString("buttonText.create.testAccount");
                JButton button = new JButton(text);
                button.addActionListener((ActionEvent e) -> {
                    make5TestAccount();
                });
                JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT,5,0));
                p.add(button);
                getUI().add(new JLabel(""));
                getUI().add(p);
                numRows++;
            }
//minagawa$            
            
            open.dolphin.helper.SpringUtilities.makeCompactGrid(getUI(), numRows, 2, 6, 6, 6, 6);
            
            // Do check if the initial state is valid
            check(bean);
        
        } catch (IntrospectionException ex) {
            Logger.getLogger(ConnectionPropertySheet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * 5分間評価用のアカウントを作成する。
     */
    public void make5TestAccount() {
        
        AddFacilityDialog af = new AddFacilityDialog();
        PropertyChangeListener pl = (PropertyChangeEvent evt) -> {
            newAccount((ServerInfo) evt.getNewValue());
        };
        af.addPropertyChangeListener(AddFacilityDialog.ACCOUNT_INFO, pl);
        Thread t = new Thread(af);
        t.setPriority(Thread.NORM_PRIORITY);
        t.start();
    }
    
    /**
     * 管理者登録ダイアログの結果を受け取り情報を表示する。
     * @param info
     */
    public void newAccount(ServerInfo info) {
        if (info!=null && facilityIdSetter!=null && userIdSetter!=null) {
            facilityIdSetter.setText(info.getFacilityId());
            userIdSetter.setText(info.getAdminId());
        }
    }
    
    @Override
    public void save() {
        bean.store();
    }
}
