package open.dolphin.client;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


/**
 * About dialog
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class AboutDialog extends JDialog {
    
    /** Creates new AboutDialog */
    public AboutDialog(Frame f, String title, String imageFile) {
        
        super(f, title, true);
        
        StringBuilder buf = new StringBuilder();
        buf.append(ClientContext.getString("productString"));
        buf.append("  Ver.");
        buf.append(ClientContext.getString("version"));
        String version = buf.toString();
        
        String[] copyright = ClientContext.getStringArray("copyrightString");
        
        Object[] message = new Object[] {
            ClientContext.getImageIcon(imageFile),
            version,
            copyright[0],
            copyright[1],
        };
        String[] options = {"閉じる"};
        JOptionPane optionPane = new JOptionPane(message,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                options,
                options[0]);
        optionPane.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getPropertyName().equals(JOptionPane.VALUE_PROPERTY)) {
                    close();
                }
            }
        });
        JPanel content = new JPanel(new BorderLayout());
        content.add(optionPane);
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
        content.setOpaque(true);
        this.setContentPane(content);
        this.pack();
        Point loc = GUIFactory.getCenterLoc(this.getWidth(), this.getHeight());
        this.setLocation(loc);
        this.setVisible(true);
    }
    
    private void close() {
        this.setVisible(false);
        this.dispose();
    }
}