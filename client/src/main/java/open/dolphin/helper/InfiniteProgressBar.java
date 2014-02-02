package open.dolphin.helper;

import java.awt.*;
import javax.swing.*;
import open.dolphin.client.ClientContext;

/**
 *
 * @author Kazushi Minagawa.
 */
public class InfiniteProgressBar {
    
    private InfiniteProgrressBarView view;
    private JDialog dialog;
    
    public InfiniteProgressBar(String title, String msg, Component cmp) {
        view = new InfiniteProgrressBarView();
        view.getMsgLbl().setText(msg);
        view.getCancelBtn().setText((String)UIManager.get("OptionPane.cancelButtonText"));
        view.getCancelBtn().setEnabled(false);
        view.setOpaque(true);
        
        Frame frame = null;
        if (cmp!=null) {
            Window w = SwingUtilities.getWindowAncestor(cmp);
            frame = (Frame)w;
            dialog = new JDialog(frame, ClientContext.getFrameTitle(title), false);
        } else {
            dialog = new JDialog(new JFrame(), ClientContext.getFrameTitle(title), false);
        }
        dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        dialog.setAlwaysOnTop(true);
        dialog.setResizable(false);
        dialog.setContentPane(view);
        dialog.pack();
        
        int x,y;
        if (frame!=null) {
            x = (frame.getSize().width - dialog.getSize().width) / 2;
            y = (frame.getSize().height - dialog.getSize().height)/ 2;
            x+=frame.getLocation().x;
            y+=frame.getLocation().y;
        } else {
            int n = ClientContext.isMac() ? 3 : 2;
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            x = (screenSize.width - dialog.getPreferredSize().width) / 2;
            y = (screenSize.height - dialog.getPreferredSize().height)/ n;
        }
        dialog.setLocation(x, y);
    }
    
    public void start() {
        view.getProgressBar().setIndeterminate(true);
        dialog.setVisible(true);
    }
    
    public void stop() {
        view.getProgressBar().setIndeterminate(false);
        view.getProgressBar().setValue(0);
        dialog.setVisible(false);
        dialog.dispose();
    }
}
