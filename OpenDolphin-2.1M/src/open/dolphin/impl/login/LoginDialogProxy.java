package open.dolphin.impl.login;

import java.beans.PropertyChangeListener;
import open.dolphin.client.ILoginDialog;
import open.dolphin.project.Project;

/**
 *
 * @author Kazushi Minagawa. Digital Globe, Inc.
 */
public class LoginDialogProxy implements ILoginDialog {

    private AbstractLoginDialog dialog;

    public LoginDialogProxy() {
        String test = Project.getString("login.set");
        if (test!=null && test.equals("multi-user")) {
            dialog = new MultiUserLoginDialog();
        } else if (test!=null && test.equals("multi-facility")) {
            dialog = new MultiFacilityLoginDialog();
        } else {
            dialog = new LoginDialog();
        }
    }

    @Override
    public void addPropertyChangeListener(String prop, PropertyChangeListener listener) {
        dialog.addPropertyChangeListener(prop, listener);
    }

    @Override
    public void removePropertyChangeListener(String prop, PropertyChangeListener listener) {
        dialog.removePropertyChangeListener(prop, listener);
    }

    @Override
    public void start() {
        dialog.start();
    }

    @Override
    public void close() {
        dialog.close();
    }

    @Override
    public void doSetting() {
        dialog.doSetting();
    }
}
