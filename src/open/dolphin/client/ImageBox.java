package open.dolphin.client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.EventHandler;
import java.io.File;
import java.io.FileFilter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import open.dolphin.helper.ComponentMemory;
import org.apache.log4j.Logger;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.Task;
import org.jdesktop.application.TaskMonitor;

/**
 * ImageBox
 *
 * @author Minagawa,Kazushi
 */
public class ImageBox extends AbstractMainTool {
    
    private static final int DEFAULT_COLUMN_COUNT 	=   3;
    private static final int DEFAULT_IMAGE_WIDTH 	= 120;
    private static final int DEFAULT_IMAGE_HEIGHT 	= 120;
    private static final String[] DEFAULT_IMAGE_SUFFIX = {".jpg"};
    
    private String imageLocation  = ClientContext.getLocation("schema");
    private JTabbedPane tabbedPane;
    private JButton refreshBtn;
    private int columnCount = DEFAULT_COLUMN_COUNT;
    private int imageWidth = DEFAULT_IMAGE_WIDTH;
    private int imageHeight = DEFAULT_IMAGE_HEIGHT;
    private String[] suffix = DEFAULT_IMAGE_SUFFIX;
    private int defaultWidth = 406;
    private int defaultHeight = 587;
    private int defaultLocX = 537;
    private int defaultLocY = 22;
    
    //private JFrame frame;
    private JDialog frame;
    private String title = "シェーマボックス";
    private static final int TIMER_DELAY 	=  200;		// 200 msec 毎にチェック
    private static final int MAX_ESTIMATION 	= 5000;		// 全体の見積もり時間
    private static final String PROGRESS_NOTE = "画像をロードしています...";
    
    private Logger logger;
    
    public void start() {
        logger = ClientContext.getBootLogger();
        initComponent();
        connect();
        setImageLocation(imageLocation);
    }
    
    public void stop() {
        if (tabbedPane != null) {
            int cnt = tabbedPane.getTabCount();
            for (int i = 0; i < cnt; i++) {
                ImagePalette ip = (ImagePalette) tabbedPane.getComponentAt(i);
                if (ip != null) {
                    ip.dispose();
                }
            }
        }
        frame.setVisible(false);
        frame.dispose();
    }
    
    public JFrame getFrame() {
        //return frame;
        return null;
    }
    
    public void toFront() {
        if (frame != null) {
            if (!frame.isVisible()) {
                frame.setVisible(true);
            }
            frame.toFront();
        }
    }
    
    public String getImageLocation() {
        return imageLocation;
    }
    
    public void setImageLocation(String loc) {
        
        this.imageLocation = loc;
        
        ApplicationContext appCtx = ClientContext.getApplicationContext();
        Application app = appCtx.getApplication();
        
        Task task = new Task<Void, Void>(app) {

            @Override
            protected Void doInBackground() throws Exception {
                createImagePalettes();
                return null;
            }
            
            @Override
            protected void succeeded(Void result) {
                if (! frame.isVisible()) {
                    frame.setVisible(true);
                }
                logger.debug("Task succeeded");
            }
            
            @Override
            protected void cancelled() {
                logger.debug("Task cancelled");
            }
            
            @Override
            protected void failed(java.lang.Throwable cause) {
                logger.warn(cause.getMessage());
            }
            
            @Override
            protected void interrupted(java.lang.InterruptedException e) {
                logger.warn(e.getMessage());
            }
        };
        
        TaskMonitor taskMonitor = appCtx.getTaskMonitor();
        String message = "シェーマ画像";
        Component c = null;
        TaskTimerMonitor w = new TaskTimerMonitor(task, taskMonitor, c, message, PROGRESS_NOTE, TIMER_DELAY, MAX_ESTIMATION);
        taskMonitor.addPropertyChangeListener(w);
        
        appCtx.getTaskService().execute(task);
    }
    
    public void refresh() {
        
        final ImagePalette imageTable = (ImagePalette) tabbedPane.getSelectedComponent();
        
        ApplicationContext appCtx = ClientContext.getApplicationContext();
        Application app = appCtx.getApplication();
        
        Task task = new Task<Void, Void>(app) {

            @Override
            protected Void doInBackground() throws Exception {
                imageTable.refresh();
                return null;
            }
            
            @Override
            protected void succeeded(Void result) {
                if (! frame.isVisible()) {
                    frame.setVisible(true);
                }
                logger.debug("Task succeeded");
            }
            
            @Override
            protected void cancelled() {
                logger.debug("Task cancelled");
            }
            
            @Override
            protected void failed(java.lang.Throwable cause) {
                logger.warn(cause.getMessage());
            }
            
            @Override
            protected void interrupted(java.lang.InterruptedException e) {
                logger.warn(e.getMessage());
            }
        };
        
        TaskMonitor taskMonitor = appCtx.getTaskMonitor();
        String message = "シェーマ画像";
        Component c = SwingUtilities.getWindowAncestor(this.getFrame());
        TaskTimerMonitor w = new TaskTimerMonitor(task, taskMonitor, c, message, "画像リストを更新しています", TIMER_DELAY, MAX_ESTIMATION);
        taskMonitor.addPropertyChangeListener(w);
        
        appCtx.getTaskService().execute(task);
        
    }
    
    private void initComponent() {
        //
        // TabbedPane を生成する
        //
        tabbedPane = new JTabbedPane();
        
        //
        // 更新ボタンを生成する
        //
        refreshBtn = new JButton(ClientContext.getImageIcon("ref_24.gif"));
        refreshBtn.addActionListener((ActionListener) EventHandler.create(ActionListener.class, this, "refresh"));
        refreshBtn.setToolTipText("シェーマリストを更新します");
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(refreshBtn);
        
        //
        // 全体を配置する
        //
        JPanel p = new JPanel(new BorderLayout());
        p.add(btnPanel, BorderLayout.NORTH);
        p.add(tabbedPane, BorderLayout.CENTER);
        p.setBorder(BorderFactory.createEmptyBorder(12,12,11,11));
        
        //frame = new JFrame(title);
        frame = new JDialog((JFrame) null, title, false);
        //frame.setFocusableWindowState(false);
        ComponentMemory cm = new ComponentMemory(frame,
                new Point(defaultLocX,defaultLocY),
                new Dimension(defaultWidth, defaultHeight),
                this);
        cm.setToPreferenceBounds();
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                processWindowClosing();
            }
        });
        frame.getContentPane().add(p);
    }
    
    private void connect() {
    }
    
    public void createImagePalettes() {
        
        File baseDir = new File(imageLocation);
        if ( (! baseDir.exists()) || (! baseDir.isDirectory()) ) {
            return;
        }
        
        File[] directories = listDirectories(baseDir);
        if (directories == null || directories.length == 0) {
            return;
        }
        
        //Dimension imageSize = new Dimension(imageWidth, imageHeight);
        for (int i = 0; i < directories.length; i++) {
            String tabName = directories[i].getName();
            
            ImagePalette imageTable = new ImagePalette(null, columnCount, imageWidth, imageHeight);
            imageTable.setImageSuffix(suffix);
            imageTable.setImageDirectory(directories[i]);
            tabbedPane.addTab(tabName, imageTable);
        }
    }
    
    private File[] listDirectories(File dir) {
        DirectoryFilter filter = new DirectoryFilter();
        File[] directories = dir.listFiles(filter);
        return directories;
    }
    
    public void processWindowClosing() {
        stop();
    }
    
    /**
     * @param columnCount The columnCount to set.
     */
    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }
    
    /**
     * @return Returns the columnCount.
     */
    public int getColumnCount() {
        return columnCount;
    }
    
    /**
     * @param imageWidth The imageWidth to set.
     */
    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }
    
    /**
     * @return Returns the imageWidth.
     */
    public int getImageWidth() {
        return imageWidth;
    }
    
    /**
     * @param imageHeight The imageHeight to set.
     */
    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }
    
    /**
     * @return Returns the imageHeight.
     */
    public int getImageHeight() {
        return imageHeight;
    }
    
    /**
     * @param suffix The suffix to set.
     */
    public void setSuffix(String[] suffix) {
        this.suffix = suffix;
    }
    
    /**
     * @return Returns the suffix.
     */
    public String[] getSuffix() {
        return suffix;
    }
    
    class DirectoryFilter implements FileFilter {
        
        public boolean accept(File path) {
            return path.isDirectory();
        }
    }
}
