package open.dolphin.impl.img;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.swing.*;
import open.dolphin.client.ClientContext;
import open.dolphin.client.GUIFactory;

/**
 * 他プロセス連携
 * @author Life Sciences Computing Corporation.
 */
public class DefaultSettingEx {

    private AbstractBrowser context;
    private Component parent;
    private DefaultConfigViewEx view;
    private JDialog dialog;
    private Properties properties;
   
    public DefaultSettingEx(AbstractBrowser context, Component parent) {
        this.context = context;
        this.parent = parent;
        this.properties = context.properties;
    }
    
    private ActionMap getAction(ResourceBundle resource) {

        ActionMap ret = new ActionMap();

        String text = resource.getString("setBaseDirectory.Action.text");
        AbstractAction setBaseDirectory = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                int returnVal = chooser.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    String dir = chooser.getSelectedFile().getPath();
                    view.getBaseDirFld().setText(dir);
                }
            }
        };
        ret.put("setBaseDirectory", setBaseDirectory);

        text = resource.getString("saveProperties.Action.text");
        AbstractAction saveProperties = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {

                // BaseDir
                setProperty(DefaultBrowserEx.PROP_BASE_DIR, view.getBaseDirFld());

                // Drop動作
                String dropAction = view.getCopyRadio().isSelected() ? "copy" : "move";
                properties.setProperty(AbstractBrowser.PROP_DROP_ACTION, dropAction);

                // カラム数
                String spiVal = view.getColumnSpinner().getValue().toString();
                properties.setProperty(AbstractBrowser.PROP_COLUMN_COUNT, spiVal);

                // ソート属性
                String sortAttr = view.getFilenameRadio().isSelected() ? "filename" : "lastModified";
                properties.setProperty(AbstractBrowser.PROP_SORT_ATTR, sortAttr);

                // ソート順
                String sortOrder = view.getAscRadio().isSelected() ? "asc" : "desc";
                properties.setProperty(AbstractBrowser.PROP_SORT_ORDER, sortOrder);

                dialog.setVisible(false);
                dialog.dispose();
            }
        };
        ret.put("saveProperties", saveProperties);

        text = resource.getString("cancel.Action.text");
        AbstractAction cancel = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                dialog.setVisible(false);
                dialog.dispose();
            }
        };
        ret.put("cancel", cancel);
        
        return ret;
    }

    public void start() {
        
        view = new DefaultConfigViewEx();

        // Base directory
        String baseDir = properties.getProperty(DefaultBrowserEx.PROP_BASE_DIR);
        view.getBaseDirFld().setText(baseDir);

        // Drop action
        boolean move = context.dropIsMove();
        view.getCopyRadio().setSelected(!move);
        view.getMoveRadio().setSelected(move);

        // カラム数
        int spiInt = context.columnCount();
        view.getColumnSpinner().setValue(new Integer(spiInt));

        // ソート属性
        boolean last = context.sortIsLastModified();
        view.getFilenameRadio().setSelected(!last);
        view.getLastRadio().setSelected(last);

        // ソート順
        boolean desc = context.sortIsDescending();
        view.getAscRadio().setSelected(!desc);
        view.getDescRadio().setSelected(desc);

        // Button group
        ButtonGroup actionRgp = new ButtonGroup();
        actionRgp.add(view.getCopyRadio());
        actionRgp.add(view.getMoveRadio());

        ButtonGroup attrRgp = new ButtonGroup();
        attrRgp.add(view.getFilenameRadio());
        attrRgp.add(view.getLastRadio());

        ButtonGroup orderRgp = new ButtonGroup();
        orderRgp.add(view.getAscRadio());
        orderRgp.add(view.getDescRadio());

        // Inject Actions
        ResourceBundle resource = ClientContext.getBundle(this.getClass());
        ActionMap map = getAction(resource);

        view.getBaseDirBtn().setAction(map.get("setBaseDirectory"));
        
        JButton saveBtn = new JButton();
        saveBtn.setAction(map.get("saveProperties"));
        
        JButton cancelBtn = new JButton();
        cancelBtn.setAction(map.get("cancel"));
        cancelBtn.setText(GUIFactory.getCancelButtonText());
        
        Object[] options = new Object[]{saveBtn, cancelBtn};
        
        JOptionPane jop = new JOptionPane(
                view,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                options,
                saveBtn);
        
        dialog = jop.createDialog(parent, ClientContext.getFrameTitle("イメージブラウザ設定"));
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dialog.setVisible(false);
                dialog.dispose();
            }
        });
        dialog.setVisible(true);
    }

    private void setProperty(String name, JTextField tf) {
        String value = tf.getText().trim();
        if (!value.equals("")) {
            properties.setProperty(name, value);
        } else if (properties.containsKey(name)) {
            properties.remove(name);
        }
    }
}
