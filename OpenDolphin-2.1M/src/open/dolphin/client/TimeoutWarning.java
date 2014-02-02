package open.dolphin.client;

import java.awt.Component;

import javax.swing.JOptionPane;

/**
 *
 * @author Kazushi Minagawa.
 */
public class TimeoutWarning {

    private Component parent;
    private String title;
    private String message;

    public TimeoutWarning(Component parent, String title, String message) {
        this.parent = parent;
        this.parent = parent;
        this.message = message;
    }

    public void start() {
        StringBuilder sb = new StringBuilder();
        if (message != null) {
            sb.append(message);
        }
        sb.append(ClientContext.getString("task.timeoutMsg1"));
        sb.append("\n");
        sb.append(ClientContext.getString("task.timeoutMsg2"));
        JOptionPane.showMessageDialog(parent,
                sb.toString(),
                ClientContext.getFrameTitle(title),
                JOptionPane.WARNING_MESSAGE);
    }
}
