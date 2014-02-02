/*
 * Created on 2005/09/13
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package open.dolphin.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
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
import javax.swing.ProgressMonitor;
import javax.swing.WindowConstants;

import open.dolphin.plugin.helper.CallBacksWorker;
import open.dolphin.plugin.helper.ComponentMemory;


/**
 * ImageBox
 *
 * @author Minagawa,Kazushi
 */
public class ImageBox extends DefaultMainWindowPlugin {
    
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
    // Task Timer 関連
    private javax.swing.Timer taskTimer;
    private static final int TIMER_DELAY 	=  200;		// 200 msec 毎にチェック
    private static final int MAX_ESTIMATION 	= 5000;		// 全体の見積もり時間
    private static final int DECIDE_TO_POPUP 	=  300;		// 300 msec 後にポップアップの判断をする
    private static final int MILIS_TO_POPUP  	=  500;		// その時　Taskが 500msec 以上かかるようであればポップアップする
    private static final String PROGRESS_NOTE = "画像をロードしています...";
    
    public void start() {
        initComponent();
        connect();
        setImageLocation(imageLocation);
        super.start();
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
        super.stop();
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
        
        final CallBacksWorker worker = new CallBacksWorker(this, "createImagePalettes", null, null);
        final ProgressMonitor monitor = new ProgressMonitor(null, null, PROGRESS_NOTE, 0, MAX_ESTIMATION/TIMER_DELAY);
        taskTimer = new javax.swing.Timer(TIMER_DELAY, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
                if (worker.isDone()) {
                    taskTimer.stop();
                    monitor.close();
                    if (! frame.isVisible()) {
                        frame.setVisible(true);
                    }
                } else {
                    monitor.setProgress(worker.getCurrent());
                }
            }
        });
        monitor.setProgress(0);
        monitor.setMillisToDecideToPopup(DECIDE_TO_POPUP);
        monitor.setMillisToPopup(MILIS_TO_POPUP);
        worker.start();
        taskTimer.start();
    }
    
    public void refresh() {
        
        ImagePalette imageTable = (ImagePalette)tabbedPane.getSelectedComponent();
        
        final CallBacksWorker worker = new CallBacksWorker(imageTable, "refresh", null, null);
        final ProgressMonitor monitor = new ProgressMonitor(imageTable, null, "画像リストを更新しています", 0, MAX_ESTIMATION/TIMER_DELAY);
        taskTimer = new javax.swing.Timer(TIMER_DELAY, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
                if (worker.isDone()) {
                    taskTimer.stop();
                    monitor.close();
                    if (! frame.isVisible()) {
                        frame.setVisible(true);
                    }
                } else {
                    monitor.setProgress(worker.getCurrent());
                }
            }
        });
        monitor.setProgress(0);
        monitor.setMillisToDecideToPopup(DECIDE_TO_POPUP);
        monitor.setMillisToPopup(MILIS_TO_POPUP);
        worker.start();
        taskTimer.start();
        
        //imageTable.refresh();
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
