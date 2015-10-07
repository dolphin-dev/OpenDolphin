package open.dolphin.project;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentListener;
import open.dolphin.client.ClientContext;

/**
 * AbstractPropertySheet
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 * @author  s.oh^
 */
public abstract class AbstractPropertySheet {
    
    public static final String STATE_PROP   = "stateProp";
    public enum State {NONE_STATE,VALID_STATE,INVALID_STATE};
    
    private static final int UI_WIDTH_MIN = 10;
    private static final int UI_HEIGHT_MIN = 10;
    
    private ProjectSettingDialog context;
    private ProjectStub projectStub;
    private PropertyChangeSupport boundSupport;
    protected AbstractPropertySheet.State state = AbstractPropertySheet.State.NONE_STATE;
    private JPanel ui;
    private boolean loginState;
    private String title;
    private String icon;
    private String id;
    
    /** 
     * Creates a new instance of AbstractPropertySheet 
     */
    public AbstractPropertySheet() {
        ui = new JPanel();
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getIcon() {
        return icon;
    }
    
    public void setIcon(String icon) {
        this.icon = icon;
    }
    
    public ProjectSettingDialog getContext() {
        return context;
    }
    
    public void setContext(ProjectSettingDialog context) {
        this.context = context;
        this.addPropertyChangeListener(STATE_PROP, context);
        this.setLogInState(context.getLoginState());
    }
    
    public boolean isLoginState() {
        return loginState;
    }
    
    public void setLogInState(boolean login) {
        loginState = login;
    }
    
    public JPanel getUI() {
        return ui;
    }
    
    public void setUI(JPanel p) {
        ui = p;
        if(ui != null) {
            ui.setMinimumSize(new Dimension(UI_WIDTH_MIN, UI_HEIGHT_MIN));
        }
    }
    
    public abstract void start();
    
    public abstract void save();
    
    public void addPropertyChangeListener(String prop, PropertyChangeListener l) {
        if (boundSupport == null) {
            boundSupport = new PropertyChangeSupport(this);
        }
        boundSupport.addPropertyChangeListener(prop, l);
    }
    
    public void removePropertyChangeListener(String prop, PropertyChangeListener l) {       
        if (boundSupport == null) {
            boundSupport = new PropertyChangeSupport(this);
        }
        boundSupport.removePropertyChangeListener(prop, l);
    }
    
    public ProjectStub getProjectStub() {
        return projectStub;
    }
    
    public void setProjectStub(ProjectStub projectStub) {
        this.projectStub = projectStub;
    }
    
    protected void setState(AbstractPropertySheet.State state) {
        AbstractPropertySheet.State old = this.state;
        this.state = state;
        if (this.state!=old) {
            boundSupport.firePropertyChange(STATE_PROP, null, this.state);
        }
    }
    
    protected AbstractPropertySheet.State getState() {
        return state;
    }
    
    /**
     * courtesy of
     * http://www.sable.mcgill.ca/~hendren/303/BookSlides/Ch7/code/propedit/PropertySheet.java.html
     * Gets the property editor for a given property,
     * and wires it so that it updates the given object.
     * @param bean the object whose properties are being edited
     * @param descriptor the descriptor of the property to
     * be edited
     * @return a property editor that edits the property
     * with the given descriptor and updates the given object
     */
    public PropertyEditor getEditor(final SettingBean bean, PropertyDescriptor descriptor) {
        try {
            // Getter
            Method getter = descriptor.getReadMethod();
            if (getter == null) {
                return null;
            }
            
            // Setter
            final Method setter = descriptor.getWriteMethod();
            if (setter == null) {
                return null;
            }
            
            final PropertyEditor editor;
            Class editorClass = descriptor.getPropertyEditorClass();
            
            if (editorClass != null) {           
                editor = (PropertyEditor) editorClass.newInstance();
            }
            else {
                editor = PropertyEditorManager.findEditor(descriptor.getPropertyType());
            }
            
            if (editor == null) {
                return null;
            }

            // Set editor beans' value
            Object value = getter.invoke(bean, new Object[] {});
            editor.setValue(value);
            
            return editor;
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException exception) {
            return null;
        }
    }
    
    /**
     * courtesy of
     * http://www.sable.mcgill.ca/~hendren/303/BookSlides/Ch7/code/propedit/PropertySheet.java.html
     * Wraps a property editor into a component.
     * @param editor the editor to wrap
     * @return a button (if there is a custom editor), 
     * combo box (if the editor has tags), or text field (otherwise)
     */   
    public Component getEditorComponent(final SettingBean bean, final PropertyEditor editor, PropertyDescriptor pd) {
        
        String[] tags = editor.getTags();
        String text = editor.getAsText();
        Method setter = pd.getWriteMethod();
        
//minagawa^        
        if (editor instanceof DecimalEditorSupport) {
            
            final JTextField textField = new JTextField(text, 3);
            textField.setHorizontalAlignment(SwingConstants.RIGHT);
            textField.getDocument().addDocumentListener(new DocumentListener() {

                @Override
                public void insertUpdate(javax.swing.event.DocumentEvent e) {
                    try {
                        editor.setAsText(textField.getText());
                        setBeanValue(bean, setter, editor.getValue());
                        check(bean);
                    }
                    catch (IllegalArgumentException exception) {
                    }
                }

                @Override
                public void removeUpdate(javax.swing.event.DocumentEvent e) {
                    try {
                        editor.setAsText(textField.getText());
                        setBeanValue(bean, setter, editor.getValue());
                        check(bean);
                    }
                    catch (IllegalArgumentException exception) {
                    }
                }

                @Override
                public void changedUpdate(javax.swing.event.DocumentEvent e) {
                }
            });
            
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panel.add(textField);
            return panel;
        }
        
        else if (editor instanceof SpinnerEditorSupport) {
            
            SpinnerEditorSupport spinnerEditor = (SpinnerEditorSupport)editor;
            SpinnerModel fetchModel = new SpinnerNumberModel((Integer)editor.getValue(), 
                    new Integer(spinnerEditor.getMinValue()), 
                    new Integer(spinnerEditor.getMaxValue()), 
                    new Integer(spinnerEditor.getStepValue()));
            JSpinner spinner = new JSpinner(fetchModel);
            spinner.setEditor(new JSpinner.NumberEditor(spinner, "#"));
            spinner.addChangeListener((ChangeEvent e) -> {
                JSpinner spn = (JSpinner)e.getSource();
                editor.setAsText(spn.getValue().toString());
                setBeanValue(bean, setter, editor.getValue());
            });
            
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panel.add(spinner);
            return panel;
        }
        
        else if (editor instanceof DirectoryEditorSupport) {
            
            final JTextField textField = new JTextField(text, 15);
            textField.getDocument().addDocumentListener(new DocumentListener() {

                @Override
                public void insertUpdate(javax.swing.event.DocumentEvent e) {
                    try {
                        editor.setAsText(textField.getText());
                        setBeanValue(bean, setter, editor.getValue());
                        check(bean);
                    }
                    catch (IllegalArgumentException exception) {
                    }
                }

                @Override
                public void removeUpdate(javax.swing.event.DocumentEvent e) {
                    try {
                        editor.setAsText(textField.getText());
                        setBeanValue(bean, setter, editor.getValue());
                        check(bean);
                    }
                    catch (IllegalArgumentException exception) {
                    }
                }

                @Override
                public void changedUpdate(javax.swing.event.DocumentEvent e) {
                }
            });
            java.util.ResourceBundle bundle = ClientContext.getMyBundle(AbstractPropertySheet.class);
            JButton choose = new JButton(bundle.getString("ChooseButton"));
            choose.addActionListener((ActionEvent e) -> {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                int returnVal = chooser.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    String dir = chooser.getSelectedFile().getPath();
                    textField.setText(dir);
                    editor.setAsText(textField.getText());
                    setBeanValue(bean, setter, editor.getValue());
                    check(bean);
                }
            });
            
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panel.add(textField);
            panel.add(choose);
            return panel;
            
        }
//minagawa$        
        else if (tags!=null) {
            
            if (tags.length==1) {
                // make checkbox that shows a tag
                final JCheckBox chk = new JCheckBox(pd.getDisplayName());
                chk.setSelected(Boolean.parseBoolean(text));
                chk.addActionListener((ActionEvent e) -> {
                    JCheckBox box = (JCheckBox)e.getSource();
                    editor.setAsText(String.valueOf(box.isSelected()));
                    setBeanValue(bean, setter, editor.getValue());
                    check(bean);
                });
                return chk;
                
            } else if (tags.length<=3) {
                // make radio buttons that shows all tag
                final ButtonGroup group = new ButtonGroup();
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                JRadioButton toCheck=null;
                
                for (String name : tags) {
                    JRadioButton rd = new JRadioButton(name);
                    group.add(rd);
                    panel.add(rd);
                    
                    if (name.equals(text)) {
                        toCheck = rd;
                    }
                    
                    rd.addActionListener((ActionEvent e) -> {
                        JRadioButton r = (JRadioButton)e.getSource();
                        editor.setAsText(r.getText());
                        setBeanValue(bean, setter, editor.getValue());
                        check(bean);
                    });
                }
                if (toCheck!=null) {
                    toCheck.setSelected(true);
                }
                return panel;
                
            } else {
                // make a combo box that shows all tags
                final JComboBox comboBox = new JComboBox(tags);
                comboBox.setSelectedItem(text);
                comboBox.addItemListener((ItemEvent event) -> {
                    if (event.getStateChange() == ItemEvent.SELECTED) {
                        editor.setAsText((String) comboBox.getSelectedItem());
                        setBeanValue(bean, setter, editor.getValue());
                        check(bean);
                    }
                });
                JPanel ret = new JPanel(new FlowLayout(FlowLayout.LEFT));
                ret.add(comboBox);
                return ret;
            }
            
        } else {
            final JTextField textField = new JTextField(text, 10);
            textField.getDocument().addDocumentListener(new DocumentListener() {

                @Override
                public void insertUpdate(javax.swing.event.DocumentEvent e) {
                    try {
                        editor.setAsText(textField.getText());
                        setBeanValue(bean, setter, editor.getValue());
                        check(bean);
                    }
                    catch (IllegalArgumentException exception) {
                    }
                }

                @Override
                public void removeUpdate(javax.swing.event.DocumentEvent e) {
                    try {
                        editor.setAsText(textField.getText());
                        setBeanValue(bean, setter, editor.getValue());
                        check(bean);
                    }
                    catch (IllegalArgumentException exception) {
                    }
                }

                @Override
                public void changedUpdate(javax.swing.event.DocumentEvent e) {
                }
            });
            return textField;
        }
    }
    
    private void setBeanValue(final Object bean, Method setter, Object value) {
        try {
            setter.invoke(bean, new Object[]{value});
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(ConnectionPropertySheet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    protected void check(SettingBean bean) {
        AbstractPropertySheet.State newState = bean.isValidBean()
                ? AbstractPropertySheet.State.VALID_STATE 
                : AbstractPropertySheet.State.INVALID_STATE;
        setState(newState);
    }
}
