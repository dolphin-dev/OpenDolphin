package open.dolphin.client;

import java.beans.PropertyChangeListener;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public interface ILoginDialog {

    public static final String LOGIN_PROP = "LOGIN_PROP";
    public enum LoginStatus {AUTHENTICATED, NOT_AUTHENTICATED, CANCELD};

    public void addPropertyChangeListener(String prop, PropertyChangeListener listener);

    public void removePropertyChangeListener(String prop, PropertyChangeListener listener);

    public void start();

    public void close();

    public void doSetting();
}
