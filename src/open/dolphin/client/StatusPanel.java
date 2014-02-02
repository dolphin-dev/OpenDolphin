package open.dolphin.client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;

import java.beans.PropertyChangeEvent;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import org.jdesktop.application.TaskMonitor;

/**
 * Chart plugin で共通に利用するステータスパネル。
 *
 * @author  Kazushi Minagawa
 */
public class StatusPanel extends JPanel implements IStatusPanel {
    
    private static final int DEFAULT_HEIGHT = 23;
    
    private JLabel messageLable;
    //private UltraSonicProgressLabel ultraSonic;
    private JProgressBar progressBar;
    private JLabel leftLabel;
    private JLabel rightLabel;
    private TaskMonitor taskMonitor;
    
    /**
     * Creates a new instance of StatusPanel
     */
    public StatusPanel() {
        
        messageLable = new JLabel("");
        //ultraSonic = new UltraSonicProgressLabel();
        progressBar = new JProgressBar();
        progressBar.setPreferredSize(new Dimension(100, 14));
        progressBar.setMinimumSize(new Dimension(100, 14));
        progressBar.setMaximumSize(new Dimension(100, 14));
        leftLabel = new JLabel("");
        rightLabel = new JLabel("");
        Font font = GUIFactory.createSmallFont();
        leftLabel.setFont(font);
        rightLabel.setFont(font);
        leftLabel.setHorizontalAlignment(SwingConstants.CENTER);
        rightLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel info = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 0));
        info.add(progressBar);
        info.add(Box.createHorizontalStrut(3));
        info.add(leftLabel);
        info.add(new SeparatorPanel());
        info.add(rightLabel);
        info.add(Box.createHorizontalStrut(11));
        this.setLayout(new BorderLayout());
        this.add(info, BorderLayout.CENTER);
        this.setPreferredSize(new Dimension(getWidth(), DEFAULT_HEIGHT));
    }
    
    public void setMessage(String msg) {
        messageLable.setText(msg);
    }
    
    private BlockGlass getBlock() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null && window instanceof JFrame) {
            JFrame frame = (JFrame) window;
            Component cmp = frame.getGlassPane();
            if (cmp != null && cmp instanceof BlockGlass) {
                return (BlockGlass) cmp;
            }
        }
        return null;
    }
    
    private void start() {
        BlockGlass glass = getBlock();
        if (glass != null) {
            glass.block();
        }
        //ultraSonic.start();
        progressBar.setIndeterminate(true);
    }
    
//    public void start(String startMsg) {
//        setMessage(startMsg);
//        start();
//    }
    
    private void stop() {
        BlockGlass glass = getBlock();
        if (glass != null) {
            glass.unblock();
        }
        //ultraSonic.stop();
        progressBar.setIndeterminate(false);
        progressBar.setValue(0);
    }
    
//    public void stop(String stopMsg) {
//        setMessage(stopMsg);
//        stop();
//    }
    
    public void setRightInfo(String info) {
        rightLabel.setText(info);
    }
    
    public void setLeftInfo(String info) {
        leftLabel.setText(info);
    }
    
    public JProgressBar getProgressBar() {
        return progressBar;
    }
    
    public void ready(TaskMonitor taskMonitor) {
        taskMonitor.addPropertyChangeListener(this);
        this.taskMonitor = taskMonitor;
    }
        
    public void propertyChange(PropertyChangeEvent e) {
                
        String propertyName = e.getPropertyName();

        if ("started".equals(propertyName)) {
            this.start();

        } else if ("done".equals(propertyName)) {
            this.stop();
            this.taskMonitor.removePropertyChangeListener(this); // 重要
        }
    }
}

