package open.dolphin.impl.login;

import java.beans.PropertyChangeListener;
import open.dolphin.client.ILoginDialog;
import open.dolphin.project.Project;

/**
 *
 * @author Kazushi Minagawa. Digital Globe, Inc.
 */
public class LoginDialogProxy implements ILoginDialog {

    private ILoginDialog dialog;

    public LoginDialogProxy() {
    }

    @Override
    public void addPropertyChangeListener(String prop, PropertyChangeListener listener) {
        getDialog().addPropertyChangeListener(prop, listener);
    }

    @Override
    public void removePropertyChangeListener(String prop, PropertyChangeListener listener) {
        getDialog().removePropertyChangeListener(prop, listener);
    }

    @Override
    public void start() {
        getDialog().start();
    }

    @Override
    public void close() {
        getDialog().close();
    }

    @Override
    public void doSetting() {
        getDialog().doSetting();
    }
    
    private ILoginDialog getDialog() {
        if (dialog==null) {
           String test = Project.getString("login.set");
            if (test!=null && test.equals("multi-user")) {
                dialog = (ILoginDialog)create("open.dolphin.impl.login.MultiUserLoginDialog");
            } else if (test!=null && test.equals("multi-facility")) {
                dialog = (ILoginDialog)create("open.dolphin.impl.login.MultiFacilityLoginDialog");
            } else {
                dialog = (ILoginDialog)create("open.dolphin.impl.login.LoginDialog");
            } 
        }
        return dialog;
    }
    
    private Object create(String clsName) {
        try {
            return Class.forName(clsName).newInstance();
        } catch (InstantiationException ex) {
            ex.printStackTrace(System.err);
        } catch (IllegalAccessException ex) {
            ex.printStackTrace(System.err);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }
}
