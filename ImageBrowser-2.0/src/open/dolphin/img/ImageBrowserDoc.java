package open.dolphin.img;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import open.dolphin.client.AbstractChartDocument;
import open.dolphin.client.Chart;
import open.dolphin.client.ClientContext;
import open.dolphin.client.ImageEntry;
import open.dolphin.helper.ImageHelper;
import open.dolphin.helper.TaskProgressMonitor;
import open.dolphin.infomodel.PatientModel;
import org.apache.log4j.Level;
import org.jdesktop.application.Action;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.LocalStorage;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.Task;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class ImageBrowserDoc extends AbstractChartDocument {

    private static final String TITLE = "PDF・画像";
    private static final int MAX_IMAGE_SIZE = 120;
    private static final int CELL_WIDTH_MARGIN = 20;
    private static final int CELL_HEIGHT_MARGIN = 20;
    private static final String SETTING_FILE_NAME = "imageBrowserProp2.xml";

    protected static final String PROP_BASE_DIR = "baseDir";
    protected static final String PROP_COLUMN_COUNT = "columnCount";
    protected static final String PROP_SHOW_FILE_NAME = "showFileName";
    protected static final String PROP_PDF_VIEWER = "pdfView";
    protected static final String PROP_JPEG_VIEWER = "jpegView";
    protected static final String PROP_DICOME_VIEWER = "dicomView";
    protected static final String[] ACCEPT_FILE_TYPES = {".pdf", ".dcm", ".jpg", ".png", ".bmp", ".gif", ".tif"};

    private static final String ICON_HAS_IMAGE = "/open/dolphin/resources/images/play_16.gif";
    private static final String ICON_PDF = "/open/dolphin/img/resources/pdf_icon40px.gif";
    private static final String SUFFIX_PDF = "pdf";
    private static final String SUFFIX_DICOM = "dcm";
    
    private ImageTableModel tableModel;
    private int columnCount;
    private boolean showFileName;
    private int imageSize = MAX_IMAGE_SIZE;
    private int cellWidth = MAX_IMAGE_SIZE + CELL_WIDTH_MARGIN;
    private int cellHeight = MAX_IMAGE_SIZE + CELL_HEIGHT_MARGIN;
    private Properties properties;
    private String imageBase;
    private JTable table;
    private JScrollPane jScrollPane1;
    private JLabel dirLabel;
    private JButton settingBtn;
    private JButton refreshBtn;

    private boolean DEBUG;
  
    public ImageBrowserDoc() {
        
        setTitle(TITLE);

        DEBUG = logger.getLevel() == Level.DEBUG ? true : false;
        
        //------------------------------------------------------
        // このプラグインでは properties を取得し、imageBase を設定する。
        // タブへ追加された時、画像及びPDF文書があるかどうかを示すため
        //------------------------------------------------------
        properties = loadProperties();
        if (properties != null) {
            String dir = properties.getProperty(PROP_BASE_DIR);
            if (dir != null && (!dir.equals(""))) {
                this.imageBase = dir;
            }
        }
    }
    
    /**
     * Chart がプラグインをタブへ追加する場合にコールする。
     * 患者ディレクトリにファイルがあれば アイコンを返す。
     */
    @Override
    public ImageIcon getIconInfo(Chart ctx) {
        ImageIcon icon = null;
        PatientModel pm = ctx.getPatient();
        String pid = pm.getPatientId();
        if (hasImageOrPDF(pid)) {
            icon = getIcon(ICON_HAS_IMAGE);
        }
        return icon;
    }
    
    private ImageIcon getIcon(String res) {
        return new ImageIcon(this.getClass().getResource(res));
    }
    
    private ImageIcon getPdfIcon() {
        return getIcon(ICON_PDF);
    }
    
    /**
     * PDFや画像が保管されているベース（共有）ディレクトリを返す。
     * @return ベースディレクトリ名
     */
    public String getImageBase() {
        return this.imageBase;
    }
    
    /**
     * PDFや画像が保管されているベース（共有）ディレクトリを設定する。
     * @param base ベースディレクトリ名
     */
    public void setImageBase(String base) {
        String old = this.imageBase;
        this.imageBase = base;
        scan();
    }
    
    
    private String getImgLocation() {
        
        if (getContext() == null) {
            dirLabel.setText("");
            return null;
        }
        
        if (getImageBase() == null || getImageBase().equals("")) {
            dirLabel.setText("画像・PDFディレクトリが指定されていません。");
            return null;
        }
        
        String pid = getContext().getPatient().getPatientId();
        StringBuilder sb = new StringBuilder();
        sb.append(getImageBase());
        if (!getImageBase().endsWith(File.separator)) {
            sb.append(File.separator);
        }
        
        sb.append(pid);
        String loc = sb.toString();
        dirLabel.setText("ディレクトリ=" + loc);
        
        return loc;
    }
    
    private String getSuffix(String path) {
        String test = path.toLowerCase();
        int index = test.lastIndexOf('.');
        if (index > 0) {
            return test.substring(index + 1);
        }
        return null;
    }
    
    /**
     * 指定した患者のディレクトリにファイルが存在する場合は true を返す。
     */
    private boolean hasImageOrPDF(String patientId) {
        
        boolean ret = false;
        
        if (getImageBase() != null && patientId!= null) {
            
            StringBuilder sb = new StringBuilder();
            sb.append(getImageBase());
            if (! getImageBase().endsWith(File.separator)) {
                sb.append(File.separator);
            }
            sb.append(patientId);
            
            File imageDirectory = new File(sb.toString());
            if ( imageDirectory.exists() && imageDirectory.isDirectory() ) {
                
                File[] imageFiles = imageDirectory.listFiles();
            
                if (imageFiles != null || imageFiles.length> 0) {
                    ret = true;
                }
            }
        }
        
        return ret;
    }

    private void debug(URI uri, URL url, String path, String fileName) {
        if (DEBUG) {
            logger.debug("-------------------------------------------");
            logger.debug("URI = " + uri.toString());
            logger.debug("URL = " + url.toString());
            logger.debug("PATH = " + path);
            logger.debug("File Name = " + fileName);
        }
    }

    private void debug(String msg) {
        if (DEBUG) {
            logger.debug(msg);
        }
    }

    
    /**
     * 患者フォルダをスキャンする。
     */
    private void scan() {

        String imgLoc = getImgLocation();

        if (imgLoc == null) {
            tableModel.clear();
            return;
        }

        File imageDirectory = new File(imgLoc);
        if ( (!imageDirectory.exists()) || (!imageDirectory.isDirectory())) {
            tableModel.clear();
            return;
        }

        final File[] imageFiles = imageDirectory.listFiles();

        if (imageFiles == null || imageFiles.length == 0) {
            tableModel.clear();
            return;
        }

        ApplicationContext appCtx = ClientContext.getApplicationContext();

        Task task = new Task<ArrayList<ImageEntry>, Integer>(appCtx.getApplication()) {

            @Override
            protected ArrayList<ImageEntry> doInBackground() throws Exception {

                ArrayList imageList = new ArrayList();

                int cnt = 0;
                int total = imageFiles.length;

                for (File file : imageFiles) {

                    cnt++;

                    URI uri = file.toURI();
                    URL url = uri.toURL();
                    String path = file.getPath();
                    String fileName = file.getName();

                    debug(uri, url, path, fileName);

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
                        setProgress(new Integer(cnt));
                        continue;
                    }

                    for (int i = 0; i < ACCEPT_FILE_TYPES.length; i++) {
                        if (ACCEPT_FILE_TYPES[i].endsWith(suffix)) {
                            ImageEntry entry = new ImageEntry();
                            entry.setUrl(url.toString());
                            entry.setPath(path);
                            entry.setFileName(fileName);
                            imageList.add(entry);
                            break;
                        }
                    }
                    setProgress(new Integer(cnt));
                }

                return imageList;
            }

            @Override
            protected void succeeded(ArrayList<ImageEntry> result) {
                tableModel.setImageList(result);
                debug("Task succeeded");
            }

            @Override
            protected void cancelled() {
                debug("Task cancelled");
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

        Component c = getUI();
        String message = "イメージブラウザ";
        String note = imgLoc + "ディレクトリをスキャンしています...";
        int min = 0;
        int max = imageFiles.length;
        TaskProgressMonitor tp = new TaskProgressMonitor(task, appCtx.getTaskMonitor(), c, message, note, min, max);
        appCtx.getTaskService().execute(task);
    }

    @Action
    public void chooseDirectory() {

        try {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

            String baseDir = properties.getProperty(PROP_BASE_DIR);
            if (baseDir != null && (!baseDir.equals(""))) {
                File f = new File(baseDir);
                chooser.setSelectedFile(f);
            }
            int returnVal = chooser.showOpenDialog(getUI());
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                //setImageLocation(chooser.getSelectedFile().getPath());
            }

        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

    @Action
    public void refresh() {
        scan();
    }
    
    @Action
    public void doSetting() {
        
        // Setting dialog を開始する
        BrowserSetting setting = new BrowserSetting(getUI(), properties);
        setting.start();
        
        // 結果は properties にセットされて返ってくるので save する
        saveProperties(properties);
        
        // カラム数の変更があった場合はテーブルを再構築する
        int oldCount = columnCount;
        boolean oldShow = showFileName;
        columnCount = Integer.parseInt(properties.getProperty(PROP_COLUMN_COUNT));
        showFileName = Boolean.parseBoolean(properties.getProperty(PROP_SHOW_FILE_NAME));
        if (oldCount != columnCount || oldShow != showFileName) {
            List list = tableModel.getImageList();
            tableModel = new ImageTableModel(null, columnCount);
            table.setModel(tableModel);
            TableColumn column = null;
            for (int i = 0; i < columnCount; i++) {
                column = table.getColumnModel().getColumn(i);
                column.setPreferredWidth(cellWidth);
            }
            table.setRowHeight(cellHeight);
            tableModel.setImageList(list);
        }
        
        // Base directory の変更があった場合は再スキャンする
        String newBase = properties.getProperty(PROP_BASE_DIR);
        setImageBase(newBase);
    }
    
    private void saveProperties(Properties propToave) {
        if (propToave != null) {
            try {
                LocalStorage ls = ClientContext.getApplicationContext().getLocalStorage();
                ls.save(propToave, SETTING_FILE_NAME);
            } catch (IOException e) {
                logger.warn(e.getMessage());
            }
        }
    }
    
    private Properties loadProperties() {
        Properties ret = null;
        try {
            LocalStorage ls = ClientContext.getApplicationContext().getLocalStorage();
            ret = (Properties) ls.load(SETTING_FILE_NAME);

        } catch (IOException e) {
            logger.warn(e.getMessage());
        }
        return ret;
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
            ImageIcon icon = null;
            String fileName = null;
            
            if (value != null) {
                
                ImageEntry entry = (ImageEntry)value;
                fileName = entry.getFileName();

                if (fileName.endsWith(SUFFIX_PDF)) {
                    icon = getPdfIcon();
                } else {
                    try {
                        Iterator readers = ImageIO.getImageReadersBySuffix(getSuffix(fileName));
                        ImageReader reader = (ImageReader) readers.next();
                        File f = new File(entry.getPath());
                        FileImageInputStream fin = new FileImageInputStream(f);
                        reader.setInput(fin, true);
                        BufferedImage image = reader.read(0);
                        image = ImageHelper.getFirstScaledInstance(image, imageSize);
                        icon = new ImageIcon(image);
                        fin.close();
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace(System.err);
                    } catch (IOException ex) {
                        ex.printStackTrace(System.err);
                    } catch (Exception e) {
                        e.printStackTrace(System.err);
                    }
                }
            }
            l.setIcon(icon);
            String text = showFileName ? fileName : null;
            l.setText(text);
            return compo;
        }
    }
    
    private void openImage(ImageEntry entry) {
        
        try {
            String path = entry.getPath();
            String ext = getSuffix(path);
            
            String appli = null;
                
            if (ext.equals(SUFFIX_PDF)) {
                appli = properties.getProperty(PROP_PDF_VIEWER);
                
            } else if (ext.equals(SUFFIX_DICOM)) {
                appli = properties.getProperty(PROP_DICOME_VIEWER);
                
            } else {
                appli = properties.getProperty(PROP_JPEG_VIEWER);
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
            ex.printStackTrace(System.err);
        }
    }
    
    /**
     * 患者フォルダをスキャンしイメージテーブルにサムネールを表示する。
     */
    private void initComponents() {
        
        ActionMap map = ClientContext.getApplicationContext().getActionMap(ImageBrowserDoc.this);
        ResourceMap resource = ClientContext.getApplicationContext().getResourceMap(ImageBrowserDoc.class);

        if (properties == null) {
            properties = new Properties();
            properties.setProperty(PROP_COLUMN_COUNT, "5");
            properties.setProperty(PROP_SHOW_FILE_NAME, "true");
            properties.setProperty(PROP_BASE_DIR, "");
            
            if (ClientContext.isMac()) {
                properties.setProperty(PROP_JPEG_VIEWER, resource.getString("jpegView.mac"));
                properties.setProperty(PROP_PDF_VIEWER, resource.getString("pdfView.mac"));
            } else if (ClientContext.isWin()) {
                properties.setProperty(PROP_JPEG_VIEWER, resource.getString("jpegView.win"));
                properties.setProperty(PROP_PDF_VIEWER, resource.getString("pdfView.win"));
            } else {
                properties.setProperty(PROP_JPEG_VIEWER, "");
                properties.setProperty(PROP_PDF_VIEWER, "");
            }
            properties.setProperty(PROP_DICOME_VIEWER, "");
        }

        columnCount = Integer.parseInt(properties.getProperty(PROP_COLUMN_COUNT));
        showFileName = Boolean.parseBoolean(properties.getProperty(PROP_SHOW_FILE_NAME));
        tableModel = new ImageTableModel(null, columnCount);
        
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setCellSelectionEnabled(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setTransferHandler(new ImageTableTransferHandler(this));

        TableColumn column = null;
        for (int i = 0; i < columnCount; i++) {
            column = table.getColumnModel().getColumn(i);
            column.setPreferredWidth(cellWidth);
        }
        table.setRowHeight(cellHeight);

        ImageRenderer imageRenderer = new ImageRenderer();
        imageRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.setDefaultRenderer(java.lang.Object.class, imageRenderer);

        table.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    int col = table.getSelectedColumn();
                    if (row != -1 && col != -1) {
                        ImageEntry entry = (ImageEntry) tableModel.getValueAt(row, col);
                        if (entry != null) {
                            openImage(entry);
                        }
                    }
                }
            }
        });
        
        table.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int ctrlMask = InputEvent.CTRL_DOWN_MASK;
                int action = ((e.getModifiersEx() & ctrlMask) == ctrlMask)
                    ? TransferHandler.COPY
                    : TransferHandler.MOVE;
                JComponent c = (JComponent) e.getSource();
                TransferHandler handler = c.getTransferHandler();
                handler.exportAsDrag(c, e, action);
            }
            
            @Override
            public void mouseMoved(MouseEvent e) {
            }
        });
        
        settingBtn = new JButton();
        settingBtn.setAction(map.get("doSetting"));
        refreshBtn = new JButton();
        refreshBtn.setAction(map.get("refresh"));

        dirLabel = new JLabel();
        JPanel north = new JPanel();
        north.add(refreshBtn);
        north.add(dirLabel);
        north.add(Box.createHorizontalGlue());
        north.add(settingBtn);

        JPanel aho = new JPanel(new BorderLayout());
        aho.add(table, BorderLayout.CENTER);
        jScrollPane1 = new JScrollPane(aho);
        
        getUI().setLayout(new BorderLayout());
        getUI().add(north, BorderLayout.NORTH);
        getUI().add(jScrollPane1, BorderLayout.CENTER);
    }

    @Override
    public void start() {
        initComponents();
        scan();
    }

    @Override
    public void stop() {
    }
}
