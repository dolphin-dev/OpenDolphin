package open.dolphin.img;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import open.dolphin.client.AbstractMainTool;
import open.dolphin.client.ClientContext;
import open.dolphin.client.ImageEntry;
import open.dolphin.helper.ImageHelper;
import open.dolphin.helper.TaskProgressMonitor;
import org.apache.log4j.Logger;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.LocalStorage;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.Task;
import org.jdesktop.application.TaskMonitor;

/**
 *
 * @author kazm
 */
public class ImageBrowser extends AbstractMainTool {

    private static final String TITLE = "イメージブラウザ";
    private static final int MAX_IMAGE_SIZE = 120;
    private static final int CELL_WIDTH_MARGIN = 20;
    private static final int CELL_HEIGHT_MARGIN = 20;
    private ImageTableModel tableModel;
    private int columnCount;
    private boolean showFileName;
    private int imageSize = MAX_IMAGE_SIZE;
    private int cellWidth = MAX_IMAGE_SIZE + CELL_WIDTH_MARGIN;
    private int cellHeight = MAX_IMAGE_SIZE + CELL_HEIGHT_MARGIN;
    private Properties properties;
    private ImageBrowserView view;
    private JFrame frame;
    private ApplicationContext appCtx;
    private Application app;
    private TaskMonitor taskMonitor;
    private Logger logger;
    
    private String imageLocation;

    
    public ImageBrowser() {
        setName(TITLE);
        logger = ClientContext.getBootLogger();
        appCtx = ClientContext.getApplicationContext();
        taskMonitor = appCtx.getTaskMonitor();
        app = appCtx.getApplication();
    }
    
    private ImageIcon getIcon(String res) {
        return new ImageIcon(this.getClass().getResource(res));
    }
    
    private ImageIcon getPdfIcon() {
        return getIcon("/open/dolphin/img/resources/pdf_icon40px.gif");
    }
    
    public void setImageLocation(String loc) {
        
        this.imageLocation = loc;
        view.getLocationFld().setText(this.imageLocation);
        scan();
    }
    
    private String getSuffix(String path) {
        
        String test = path.toLowerCase();
        int index = test.lastIndexOf('.');
        if (index > 0) {
            return test.substring(index + 1);
        }
        return null;
    }
    
    private void scan() {
        
        File imageDirectory = new File(imageLocation);
        if ( (!imageDirectory.exists()) || (!imageDirectory.isDirectory())) {
            return;
        }
        
        //ImageFileFilter filter = new ImageFileFilter(suffix);
        final File[] imageFiles = imageDirectory.listFiles();
            
        if (imageFiles == null || imageFiles.length == 0) {
            return;
        }
        
        Task task = new Task<ArrayList<ImageEntry>, Integer>(app) {

            @Override
            protected ArrayList<ImageEntry> doInBackground() throws Exception {
                
                ArrayList imageList = new ArrayList();
                          
                    int cnt = 0;
                    int total = imageFiles.length;
                    
                    for (File file : imageFiles) {
                        
                        cnt++;
                        
                        String path = file.getPath();
                        String fileName = file.getName();
                        
                        logger.debug("File = " + path);
                        StringBuilder sb = new StringBuilder();
                        sb.append(fileName);
                        sb.append("[");
                        sb.append(cnt);
                        sb.append("/");
                        sb.append(total);
                        sb.append("]を処理しています...");
                        setMessage(sb.toString());
                        
                        String suffix = getSuffix(path);
                        if (suffix == null) {
                            logger.debug("No suffix: continue");
                            setProgress(new Integer(cnt));
                            continue;
                        }
                        
                        Iterator readers = ImageIO.getImageReadersBySuffix(suffix);

                        if (!readers.hasNext()) {
                            logger.debug("No available reader: continue");
                            if (suffix.equals("pdf")) {
                                ImageEntry entry = new ImageEntry();
                                URL url = file.toURL();
                                entry.setUrl(url.toString());
                                entry.setPath(path);
                                entry.setFileName(fileName);
                                entry.setImageIcon(getPdfIcon());
                                imageList.add(entry);
                            }
                            setProgress(new Integer(cnt));
                            continue;
                        }
                        
                        ImageReader reader = (ImageReader) readers.next();
                        logger.debug("reader = " + reader.getClass().getName());
                        
                        try {
                            reader.setInput(new FileImageInputStream(file), true);
                            
                        } catch (Exception e) {
                            logger.warn(e.getMessage());
                            continue;
                        }
            
                        int numImages = 1;
                        if (suffix.equals("dcm")) {
                            try {
                                numImages = reader.getNumImages(true);
                                if (numImages > 1) {
                                    sb = new StringBuilder();
                                    sb.append(fileName);
                                    sb.append(" M");
                                    sb.append(numImages);
                                    fileName = sb.toString();
                                }
                            } catch (Exception e) {
                                logger.debug(e.getMessage());
                            }
                        }
                        logger.debug("numImages = " + numImages);
                        
                        try {
                            int width = reader.getWidth(0);
                            int height = reader.getHeight(0);
                            logger.debug("width = " + width);
                            logger.debug("height = " + width);
                        
                            BufferedImage image = reader.read(0);
                            //if (width > imageSize || height > imageSize) {
                                image = ImageHelper.getFirstScaledInstance(image, imageSize);
                            //}
                            ImageIcon icon = new ImageIcon(image);
                            
                            ImageEntry entry = new ImageEntry();
                            URL url = file.toURL();
                            entry.setUrl(url.toString());
                            entry.setPath(path);
                            entry.setFileName(fileName);
                            entry.setImageIcon(icon);
                            entry.setNumImages(numImages);
                            entry.setWidth(width);
                            entry.setHeight(height);
                            
                            imageList.add(entry);
                            
                        } catch (Exception e) {
                            logger.warn(e.getMessage());
                        }
                        
                        setProgress(new Integer(cnt));
                    }

                return imageList;
            }
            
            @Override
            protected void succeeded(ArrayList<ImageEntry> result) {
                tableModel.setImageList(result);
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
        
        Component c = frame;
        String message = "イメージブラウザ";
        String note = imageLocation + "ディレクトリをスキャンしています...";
        int min = 0;
        int max = imageFiles.length;
        new TaskProgressMonitor(task, taskMonitor, c, message, note, min, max);
        appCtx.getTaskService().execute(task);
    }

    @Action
    public void chooseDirectory() {

        try {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

            String baseDir = properties.getProperty("baseDir");
            if (baseDir != null && (!baseDir.equals(""))) {
                File f = new File(baseDir);
                chooser.setSelectedFile(f);
            }
            int returnVal = chooser.showOpenDialog(frame);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                setImageLocation(chooser.getSelectedFile().getPath());
            }

        } catch (Exception ex) {
            logger.warn(ex);
        }
    }

    @Action
    public void refresh() {
        scan();
    }
    
    @Action
    public void doSetting() {
        BrowserSetting setting = new BrowserSetting(frame, properties);
        setting.start();
        int oldCount = columnCount;
        boolean oldShow = showFileName;
        columnCount = Integer.parseInt(properties.getProperty("columnCount"));
        showFileName = Boolean.parseBoolean(properties.getProperty("showFileName"));
        if (oldCount != columnCount || oldShow != showFileName) {
            List list = tableModel.getImageList();
            tableModel = new ImageTableModel(null, columnCount);
            view.getTable().setModel(tableModel);
            TableColumn column = null;
            for (int i = 0; i < columnCount; i++) {
                column = view.getTable().getColumnModel().getColumn(i);
                column.setPreferredWidth(cellWidth);
            }
            view.getTable().setRowHeight(cellHeight);
            tableModel.setImageList(list);
        }
    }

    class ImageRenderer extends DefaultTableCellRenderer {
        
        public ImageRenderer() {
            setVerticalTextPosition(JLabel.BOTTOM);
            setHorizontalTextPosition(JLabel.CENTER);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value,
                boolean isSelected,
                boolean isFocused,
                int row, int col) {
            Component compo = super.getTableCellRendererComponent(table,
                    value,
                    isSelected,
                    isFocused,
                    row, col);
            JLabel l = (JLabel)compo;
            
            if (value != null) {
                
                ImageEntry entry = (ImageEntry)value;
                l.setIcon(entry.getImageIcon());
                
                if (showFileName) {
                    
                    l.setText(entry.getFileName());
                    
                } else {
                    l.setText(null);
                }
                
            } else {
                l.setIcon(null);
                l.setText(null);
            }
            return compo;
        }
    }
    
    private void openImage(ImageEntry entry) {
        
        try {
            String path = entry.getPath();
            String ext = path.toLowerCase();
            
            String appli = null;
                
            if (ext.endsWith(".pdf")) {
                appli = properties.getProperty("pdfView");
                
            } else if (ext.endsWith(".dcm")) {
                appli = properties.getProperty("dicomView");
                
            } else {
                appli = properties.getProperty("jpegView");
            }
            
            if (appli == null || appli.equals("")) {
                return;
            }
            if (ClientContext.isMac()) {
                new ProcessBuilder("open", "-a", appli, path).start();
            } else {
                new ProcessBuilder(appli, path).start();
            }
                    
        } catch (IOException ex) {
            logger.warn(ex);
        }
    }
    
    private void initComponents() {
        
        ActionMap map = appCtx.getActionMap(ImageBrowser.this);
        ResourceMap resource = appCtx.getResourceMap(ImageBrowser.class);

        try {
            LocalStorage ls = appCtx.getLocalStorage();
            properties = (Properties) ls.load("imageBrowserProp.xml");

        } catch (Exception e) {
        }

        if (properties == null) {
            properties = new Properties();
            properties.setProperty("columnCount", "5");
            properties.setProperty("showFileName", "true");
            properties.setProperty("baseDir", "");
            if (ClientContext.isMac()) {
                properties.setProperty("jpegView", resource.getString("jpegView.mac"));
                properties.setProperty("pdfView", resource.getString("pdfView.mac"));
            } else if (ClientContext.isWin()) {
                properties.setProperty("jpegView", resource.getString("jpegView.win"));
                properties.setProperty("pdfView", resource.getString("pdfView.win"));
            } else {
                properties.setProperty("jpegView", "");
                properties.setProperty("pdfView", "");
            }
            properties.setProperty("dicomView", "");
        }

        view = new ImageBrowserView();
        
        columnCount = Integer.parseInt(properties.getProperty("columnCount"));
        showFileName = Boolean.parseBoolean(properties.getProperty("showFileName"));
        tableModel = new ImageTableModel(null, columnCount);
        view.getTable().setModel(tableModel);
        view.getTable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        view.getTable().setCellSelectionEnabled(true);
        view.getTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        TableColumn column = null;
        for (int i = 0; i < columnCount; i++) {
            column = view.getTable().getColumnModel().getColumn(i);
            column.setPreferredWidth(cellWidth);
        }
        view.getTable().setRowHeight(cellHeight);

        ImageRenderer imageRenderer = new ImageRenderer();
        imageRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        view.getTable().setDefaultRenderer(java.lang.Object.class, imageRenderer);

        view.getTable().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = view.getTable().getSelectedRow();
                    int col = view.getTable().getSelectedColumn();
                    if (row != -1 && col != -1) {
                        ImageEntry entry = (ImageEntry) tableModel.getValueAt(row, col);
                        if (entry != null) {
                            openImage(entry);
                        }
                    }
                }
            }
        });

        view.getTable().setTransferHandler(new ImageTableTransferHandler());
        
        view.getTable().addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent e) {
                int ctrlMask = InputEvent.CTRL_DOWN_MASK;
                int action = ((e.getModifiersEx() & ctrlMask) == ctrlMask)
                    ? TransferHandler.COPY
                    : TransferHandler.MOVE;
                JComponent c = (JComponent) e.getSource();
                TransferHandler handler = c.getTransferHandler();
                handler.exportAsDrag(c, e, action);
            }
            
            public void mouseMoved(MouseEvent e) {
            }
        });
        
        view.getChooserBtn().setAction(map.get("chooseDirectory"));
        view.getUpdateBtn().setAction(map.get("refresh"));
        view.getConfigBtn().setAction(map.get("doSetting"));

        frame = new JFrame(getName());
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                stop();
            }
        });

        frame.getContentPane().add(view);
        
        Rectangle bounds = null;
        try {
            bounds = (Rectangle) appCtx.getLocalStorage().load("imageBrowserBounds.xml");
        } catch (IOException ex) {
            logger.warn(ex);
        }
        
        if (bounds == null) {
            int defaultWidth = 750;
            int defaultHeight = 500;
            int defaultLocX = 300;
            int defaultLocY = 200;
            bounds = new Rectangle(defaultLocX, defaultLocY, defaultWidth, defaultHeight);
        }
        frame.setBounds(bounds);
        frame.setVisible(true);
    }

    @Override
    public void start() {
        initComponents();
        view.getChooserBtn().doClick();
    }

    @Override
    public void stop() {
        try {
            LocalStorage ls = appCtx.getLocalStorage();
            ls.save(properties, "imageBrowserProp.xml");
            ls.save(frame.getBounds(), "imageBrowserBounds.xml");
        } catch (IOException e) {
            logger.warn(e);
        }
        frame.setVisible(false);
        frame.dispose();
    }
}
