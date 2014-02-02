package open.dolphin.client;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.EventHandler;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import open.dolphin.helper.ComponentMemory;

/**
 * ImageBox
 *
 * @author Minagawa,Kazushi
 */
public class ImageBox extends AbstractMainTool {

    private static final String DEFAULT_TAB = "基本セット";
    private static final int DEFAULT_COLUMN_COUNT 	=   3;
    private static final int DEFAULT_IMAGE_WIDTH 	= 120;
    private static final int DEFAULT_IMAGE_HEIGHT 	= 120;
    private static final String[] DEFAULT_IMAGE_SUFFIX = {".jpg"};
    
    private String imageLocation;
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
    
    private JDialog frame;
    private String title = "シェーマボックス";

    private int systemSchemaIndex;
    //private boolean systemSchemaLoaded;
    
    @Override
    public void start() {
        initComponent();
        connect();
        if (! frame.isVisible()) {
            frame.setVisible(true);
        }
        setImageLocation(ClientContext.getSchemaDirectory());
    }
    
    @Override
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

        SwingWorker worker = new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                createImagePalettes();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    tabbedPane.addChangeListener(new ChangeListener() {
                        @Override
                        public void stateChanged(ChangeEvent ce) {
                            Component cmp = tabbedPane.getSelectedComponent();
                            if (cmp==null) {
                                ImagePalette sysTable = new ImagePalette(null, columnCount, imageWidth, imageHeight);
                                sysTable.setupDefaultSchema();
                                tabbedPane.setComponentAt(systemSchemaIndex, sysTable);
                            }
                        }
                    });
                } catch (InterruptedException ex) {
                    ex.printStackTrace(System.err);
                } catch (ExecutionException ex) {
                    ex.printStackTrace(System.err);
                }
            }
        };

        worker.execute();
    }
    
    public void refresh() {
        
        final ImagePalette imageTable = (ImagePalette) tabbedPane.getSelectedComponent();
        
        SwingWorker worker = new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                imageTable.refresh();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    if (! frame.isVisible()) {
                        frame.setVisible(true);
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace(System.err);
                } catch (ExecutionException ex) {
                    ex.printStackTrace(System.err);
                }
            }
        };

        worker.execute();
    }
    
    private void initComponent() {

        // TabbedPane を生成する
        tabbedPane = new JTabbedPane();
        
        // 更新ボタンを生成する
//minagawa^ Icon Server        
        //refreshBtn = new JButton(ClientContext.getImageIcon("ref_24.gif"));
        refreshBtn = new JButton(ClientContext.getImageIconArias("icon_refresh"));
//minagawa$        
        refreshBtn.addActionListener((ActionListener) EventHandler.create(ActionListener.class, this, "refresh"));
        refreshBtn.setToolTipText("シェーマリストを更新します");
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(refreshBtn);
        
        // 全体を配置する
        JPanel p = new JPanel(new BorderLayout());
        p.add(btnPanel, BorderLayout.NORTH);
        p.add(tabbedPane, BorderLayout.CENTER);
        p.setBorder(BorderFactory.createEmptyBorder(12,12,11,11));
        
        frame = new JDialog((JFrame) null, title, false);
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
        
        Path imageDir = Paths.get(imageLocation);
        if ( (!Files.exists(imageDir)) || (!Files.isDirectory(imageDir)) ) {
            return;
        }
        
        final java.util.List<Path> paths = new ArrayList<>();
        try {
            DirectoryStream<Path> ds = Files.newDirectoryStream(imageDir);
            for (Path p : ds) {
                if (Files.isDirectory(p)) {
                    paths.add(p);
                }
            }  
        } catch (Exception e) { 
        }
        
        if (!paths.isEmpty()) {
            for (Path path : paths) {
                String tabName = path.getFileName().toString();
                ImagePalette imageTable = new ImagePalette(null, columnCount, imageWidth, imageHeight);
                imageTable.setImageSuffix(suffix);
                imageTable.setImageDirectory(path);   // refresh
                tabbedPane.addTab(tabName, imageTable);
            }
            systemSchemaIndex = paths.size();
            tabbedPane.addTab(DEFAULT_TAB, null);
        }
        if (systemSchemaIndex==0) {
            ImagePalette sysTable = new ImagePalette(null, columnCount, imageWidth, imageHeight);
            sysTable.setupDefaultSchema();
            tabbedPane.addTab(DEFAULT_TAB, sysTable);
        }
//        
//        File baseDir = new File(imageLocation);
//        if (baseDir.exists() && baseDir.isDirectory()) {
//            
//            File[] directories = listDirectories(baseDir);
//            if (directories != null && directories.length > 0) {
//                for (int i = 0; i < directories.length; i++) {
//                    String tabName = directories[i].getName();
//                    ImagePalette imageTable = new ImagePalette(null, columnCount, imageWidth, imageHeight);
//                    imageTable.setImageSuffix(suffix);
//                    imageTable.setImageDirectory(directories[i]);   // refresh
//                    tabbedPane.addTab(tabName, imageTable);
//                }
//                systemSchemaIndex = directories.length;
//                tabbedPane.addTab(DEFAULT_TAB, null);
//            } 
//        }
//
//        if (systemSchemaIndex==0) {
//            ImagePalette sysTable = new ImagePalette(null, columnCount, imageWidth, imageHeight);
//            sysTable.setupDefaultSchema();
//            tabbedPane.addTab(DEFAULT_TAB, sysTable);
//        }
    }
    
//    private File[] listDirectories(File dir) {
//        DirectoryFilter filter = new DirectoryFilter();
//        File[] directories = dir.listFiles(filter);
//        return directories;
//    }
    
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
    
//    class DirectoryFilter implements FileFilter {
//        
//        @Override
//        public boolean accept(File path) {
//            return path.isDirectory();
//        }
//    }
}
