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
    private JProgressBar progressBar;
    private JLabel leftLabel;
    private JLabel rightLabel;
    private TaskMonitor taskMonitor;
    private JLabel timelabel;
    private boolean useTime = true;
    
    public StatusPanel() {
        this(true);
    }
    
    /**
     * Creates a new instance of StatusPanel
     */
    public StatusPanel(boolean useTime) {
        
        this.useTime = useTime;
        
        messageLable = new JLabel("");

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
        
        if (useTime) {
            timelabel = new JLabel("経過時間: 00 秒");
            timelabel.setFont(font);
            timelabel.setHorizontalAlignment(SwingConstants.CENTER);
        }
        
        JPanel info = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        info.add(progressBar);
        info.add(Box.createHorizontalStrut(3));
        info.add(leftLabel);
        info.add(new SeparatorPanel());
        info.add(rightLabel);
        if (useTime) {
            info.add(new SeparatorPanel());
            info.add(timelabel);
        }
        info.add(Box.createHorizontalStrut(11));
        this.setLayout(new BorderLayout());
        this.add(info, BorderLayout.CENTER);
        this.setPreferredSize(new Dimension(getWidth(), DEFAULT_HEIGHT));
    }
    
    @Override
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
        progressBar.setIndeterminate(true);
    }
    
    private void stop() {
        BlockGlass glass = getBlock();
        if (glass != null) {
            glass.unblock();
        }
        progressBar.setIndeterminate(false);
        progressBar.setValue(0);
    }
    
    @Override
    public void setRightInfo(String info) {
        rightLabel.setText(info);
    }
    
    @Override
    public void setLeftInfo(String info) {
        leftLabel.setText(info);
    }
    
    @Override
    public void setTimeInfo(long time) {
        if (useTime) {
            StringBuilder sb = new StringBuilder();
            sb.append("経過時間: ");
            sb.append(time);
            sb.append(" 秒");
            timelabel.setText(sb.toString());
        }
    }
    
    @Override
    public JProgressBar getProgressBar() {
        return progressBar;
    }
    
    public void ready(TaskMonitor taskMonitor) {
        taskMonitor.addPropertyChangeListener(this);
        this.taskMonitor = taskMonitor;
    }
        
    @Override
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

