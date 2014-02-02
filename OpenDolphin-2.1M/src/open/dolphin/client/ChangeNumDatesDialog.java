package open.dolphin.client;

import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Kazushi Minagawa. Digital Globe, Inc.
 */
public final class ChangeNumDatesDialog {

    private JButton chagneBtn;
    private JButton cancelBtn;
    private ChangeNumDatesView view;
    private JDialog dialog;
    private PropertyChangeSupport boundSupport;

    public ChangeNumDatesDialog(JFrame parent, PropertyChangeListener pcl) {

        // view
        view = new ChangeNumDatesView();
        String pattern = "^[1-9][0-9]*$";
        RegexConstrainedDocument numReg = new RegexConstrainedDocument(pattern);
        view.getNumDatesFld().setDocument(numReg);

        // OK button
        chagneBtn = new JButton("変更");
        chagneBtn.addActionListener((ActionListener) EventHandler.create(ActionListener.class, ChangeNumDatesDialog.this, "doOk"));
        chagneBtn.setEnabled(false);

        // Cancel Button
        String buttonText =  (String)UIManager.get("OptionPane.cancelButtonText");
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

        dialog = jop.createDialog(parent, ClientContext.getFrameTitle("処方日数変更"));
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
