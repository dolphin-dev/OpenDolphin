package open.dolphin.client;

import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Kazushi Minagawa. Digital Globe, Inc.
 */
public final class ChangeNumDatesDialog {

    private final JButton chagneBtn;
    private final JButton cancelBtn;
    private ChangeNumDatesView view;
    private final JDialog dialog;
    private final PropertyChangeSupport boundSupport;

    public ChangeNumDatesDialog(JFrame parent, PropertyChangeListener pcl) {

        // view
        view = new ChangeNumDatesView();
        String pattern = "^[1-9][0-9]*$";
        RegexConstrainedDocument numReg = new RegexConstrainedDocument(pattern);
        view.getNumDatesFld().setDocument(numReg);
//s.oh^ 不具合修正
        view.getNumDatesFld().enableInputMethods(false);
//s.oh$
        
        java.util.ResourceBundle bundle = ClientContext.getMyBundle(ChangeNumDatesDialog.class);
        
        // OK button
        String actionText = bundle.getString("actionText.change");
        chagneBtn = new JButton(actionText);
        chagneBtn.addActionListener((ActionListener) EventHandler.create(ActionListener.class, ChangeNumDatesDialog.this, "doOk"));
        chagneBtn.setEnabled(false);

        // Cancel Button
//minagawa^ mac jdk7        
//        String buttonText =  (String)UIManager.get("OptionPane.cancelButtonText");
        String buttonText =  GUIFactory.getCancelButtonText();
//minagawa$        
        cancelBtn = new JButton(buttonText);
        cancelBtn.addActionListener((ActionListener) EventHandler.create(ActionListener.class, ChangeNumDatesDialog.this, "doCancel"));

        // Listener
        view.getNumDatesFld().getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent de) {
                checkInput();
            }

            @Override
            public void removeUpdate(DocumentEvent de) {
                checkInput();
            }

            @Override
            public void changedUpdate(DocumentEvent de) {
            }
        });

        Object[] options = new Object[]{chagneBtn, cancelBtn};

        JOptionPane jop = new JOptionPane(
                view,
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                options,
                chagneBtn);

        String title = bundle.getString("title.dialog.changeRpNumDays");
        dialog = jop.createDialog(parent, ClientContext.getFrameTitle(title));
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                view.getNumDatesFld().requestFocus();
            }
            @Override
            public void windowClosing(WindowEvent e) {
                doCancel();
            }
        });

        boundSupport = new PropertyChangeSupport(this);
        boundSupport.addPropertyChangeListener(pcl);
    }

    public void show() {
        dialog.setVisible(true);
    }

    public void doOk() {
        try {
            int number = Integer.parseInt(view.getNumDatesFld().getText().trim());
            boundSupport.firePropertyChange("newNumDates", -1, number);
            close();
        } catch (Throwable e) {
            e.printStackTrace(System.err);
        }
    }

    public void doCancel() {
        boundSupport.firePropertyChange("newNumDates", -1, 0);
        close();
    }

    private void close() {
        dialog.setVisible(false);
        dialog.dispose();
    }

    private void checkInput() {
        String test = view.getNumDatesFld().getText().trim();
        boolean ok = true;
        ok = ok && (!test.equals(""));
        chagneBtn.setEnabled(ok);
    }
}
