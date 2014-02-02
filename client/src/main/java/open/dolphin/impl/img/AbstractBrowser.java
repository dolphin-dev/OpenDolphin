package open.dolphin.impl.img;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import open.dolphin.client.AbstractChartDocument;
import open.dolphin.client.Chart;
import open.dolphin.client.ChartImpl;
import open.dolphin.client.ClientContext;
import open.dolphin.client.ImageEntry;
import open.dolphin.helper.ImageHelper;
import open.dolphin.infomodel.PatientModel;
import org.apache.log4j.Level;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public abstract class AbstractBrowser extends AbstractChartDocument {

    protected static final int MAX_IMAGE_SIZE       = 120;
    protected static final int CELL_WIDTH_MARGIN    = 20;
    protected static final int CELL_HEIGHT_MARGIN   = 20;

    protected static final String PROP_BASE_DIR         = "baseDir";
    protected static final String PROP_DROP_ACTION      = "dropAction";
    protected static final String PROP_COLUMN_COUNT     = "columnCount";
    protected static final String PROP_SHOW_FILE_NAME   = "showFileName";
    protected static final String PROP_DISPLAY_ATTR     = "displayAttr";
    protected static final String PROP_SORT_ATTR        = "sortAttr";
    protected static final String PROP_SORT_ORDER       = "sortOrder";

    //protected static final String[] ACCEPT_IMAGE_TYPES = {"dcm","jpg", "png", "bmp", "gif", "tif"};
    protected static final String[] ACCEPT_IMAGE_TYPES = {"jpg", "png", "bmp", "gif", "tif"};
    protected static final String[] ACCEPT_DOC_TYPES = {"pdf", "doc","docx", "xls", "xlsx", "ppt","pptx"};
//minagawa^    
    //protected static final String[] ACCEPT_DOC_ICONS = 
        //{"pdf_icon40px.gif", "Word-32-d.gif","Word-32-d.gif", "Excel-32-d.gif", "Excel-32-d.gif", "PowerPoint-32-d.gif", "PowerPoint-32-d.gif"};
    protected static final String[] ACCEPT_DOC_ICONS = 
        {"icon_pdf", "icon_word","icon_word", "icon_excel", "icon_excel", "icon_power_point", "icon_power_point"};
//minagawa$    

//    protected static final String[] OTHER_DOC_TYPES =
//        {"pdf", "txt", "rtf", "htm","html", "doc","docx", "xls", "xlsx", "ppt","pptx","pages", "numbers", "key"};
//minagawa^ Icon Server    
    //protected static final String DEFAULT_DOC_ICON = "docs_32.gif";
    //protected static final String FOLDER_ICON = "foldr_32.gif";
    //protected static final String ICON_HAS_IMAGE = "/open/dolphin/resources/images/play_16.gif";
//minagawa$ 

//    protected static SimpleDateFormat SDF = new SimpleDateFormat("yyyy年MM月dd日");
    protected static final String DATE_FORMAT = "yyyy年MM月dd日";

    protected ImageTableModel tableModel;
    protected JTable table;

    protected Desktop desktop;

    protected boolean DEBUG;

    protected String imageBase;
    protected Properties properties;
    
    protected ProgressMonitor progressMonitor;
    
    protected boolean imageOrPDFIsExist;
    
    public AbstractBrowser() {
        DEBUG = (ClientContext.getBootLogger().getLevel()==Level.DEBUG);
        if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
        } else {
            ClientContext.getBootLogger().warn("Desktop is not supported");
        }
    }
    
    public boolean isImageOrPDFIsExist() {
        return imageOrPDFIsExist;
    }
    
    public void setImageOrPDFIsExist(boolean b) {
        boolean old = imageOrPDFIsExist;
        imageOrPDFIsExist = b;
        if (old!=imageOrPDFIsExist) {
//minagawa^ Icon Server            
            //ImageIcon icon =  imageOrPDFIsExist ? ClientContext.getImageIcon(ICON_HAS_IMAGE) : null;
            ImageIcon icon =  imageOrPDFIsExist ? ClientContext.getImageIconArias("icon_indicate_has_iamges_or_pdfs") : null;
//minagawa$            
            ChartImpl c = (ChartImpl)this.getContext();
            c.setChartDocumentIconAt(icon, this.getUI());
        }
    }

    /**
     * ブラウザ表示設定の規定値を返す。
     * @return Properties
     */
    protected Properties getProperties() {
        Properties defaults = new Properties();
        defaults.setProperty(PROP_DROP_ACTION, "copy");
        defaults.setProperty(PROP_COLUMN_COUNT, "5");
        defaults.setProperty(PROP_SHOW_FILE_NAME, "true");
        defaults.setProperty(PROP_DISPLAY_ATTR, "filename");
        defaults.setProperty(PROP_SORT_ATTR, "lastModified");
        defaults.setProperty(PROP_SORT_ORDER, "desc");
        properties = new Properties(defaults);
        return properties;
    }

    protected boolean dropIsMove() {
        return (!properties.getProperty(PROP_DROP_ACTION).equals("copy"));
    }
    
    protected int columnCount() {
        return Integer.parseInt(properties.getProperty(PROP_COLUMN_COUNT));
    }
    
    protected boolean showFilename() {
        return Boolean.parseBoolean(properties.getProperty(PROP_SHOW_FILE_NAME));
    }
    
    protected boolean displayIsFilename() {
        return (properties.getProperty(PROP_DISPLAY_ATTR).equals("filename"));
    }

    protected boolean sortIsLastModified() {
        return (properties.getProperty(PROP_SORT_ATTR).equals("lastModified"));
    }

    protected boolean sortIsDescending() {
        return (properties.getProperty(PROP_SORT_ORDER).equals("desc"));
    }

    protected String getSuffix(String path) {
        int index = path!=null ? path.lastIndexOf('.') : -1;
        return index>=0 ? path.substring(index+1).toLowerCase(): null;
    }

    protected boolean isImage(String ext) {
        boolean ret = false;
        if (ext!=null) {
            for (String str : AbstractBrowser.ACCEPT_IMAGE_TYPES) {
                if (str.equals(ext)) {
                    ret = true;
                    break;
                }
            }
        }
        return ret;
    }

    protected int isDocument(String ext) {
        int ret = -1;
        if (ext!=null) {
            for (int i=0; i < AbstractBrowser.ACCEPT_DOC_TYPES.length; i++) {
                if (AbstractBrowser.ACCEPT_DOC_TYPES[i].equals(ext)) {
                    ret = i;
                    break;
                }
            }
        }
        return ret;
    }

    protected ImageEntry getEntryAt(int row, int col) {
        if (row != -1 && col != -1) {
            ImageEntry entry = (ImageEntry) tableModel.getValueAt(row, col);
            return entry;
        }
        return null;
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
//minagawa^ LSC Test        
//        if (hasImageOrPDF(pid)) {
//            icon = ClientContext.getImageIcon(ICON_HAS_IMAGE);
//        }
        imageOrPDFIsExist = hasImageOrPDF(pid);
//minagawa^ Icon Server        
        //icon =  imageOrPDFIsExist ? ClientContext.getImageIcon(ICON_HAS_IMAGE) : null;
        icon =  imageOrPDFIsExist ? ClientContext.getImageIconArias("icon_indicate_has_iamges_or_pdfs") : null;
//minagawa$        
//minagawa$        
        return icon;
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
//minagawa^ LSC Test                  
//            File imageDirectory = new File(sb.toString());
//            if ( imageDirectory.exists() && imageDirectory.isDirectory() ) {
//
//                File[] imageFiles = imageDirectory.listFiles();
//
//                if (imageFiles != null || imageFiles.length> 0) {
//                    ret = true;
//                }
//            }  
            String imgLoc = sb.toString();
            Path imageDir = Paths.get(imgLoc);
            if (!Files.exists(imageDir) || !Files.isDirectory(imageDir)) {
                return false;
            } 
            try {
                DirectoryStream<Path> ds = Files.newDirectoryStream(imageDir);
                for (Path p : ds) {
                    String test = p.getFileName().toString();
                    if (!test.startsWith(".") && !test.startsWith("__")) {
                        ret = true;
                        break;
                    }
                }  
            } catch (Exception e) { 
            }
        }
//minagawa$
        return ret;
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
        if (!this.imageBase.equals(old)) {
            scan(imageBase);
        }
    }
    
    private void debug(URI uri, URL url, String path, String fileName) {
        if (DEBUG) {
            System.err.println("-------------------------------------------");
            System.err.println("URI = " + uri.toString());
            System.err.println("URL = " + url.toString());
            System.err.println("PATH = " + path);
            System.err.println("File Name = " + fileName);
        }
    }

    /**
     * 患者フォルダをスキャンする。
     */
    protected void scan(String imgLoc) {

        //String imgLoc = getImgLocation();

        if (valueIsNullOrEmpty(imgLoc)) {
            tableModel.clear();
            this.setImageOrPDFIsExist(false);
            return;
        }

//minagawa^ jdk7
//        final File imageDirectory = new File(imgLoc);
//        if ( (!imageDirectory.exists()) || (!imageDirectory.isDirectory())) {
//            tableModel.clear();
//            return;
//        }
        Path imageDir = Paths.get(imgLoc);
        if (!Files.exists(imageDir) || !Files.isDirectory(imageDir)) {
            tableModel.clear();
            this.setImageOrPDFIsExist(false);
            return;
        }  
////        List<File> allFiles = new ArrayList<File>();
////        addAllFiles(imageDirectory, allFiles);
////        if (allFiles.isEmpty()) {
////            tableModel.clear();
////            return;
////        }
//        final File[] imageFiles = new File[allFiles.size()];
//        allFiles.toArray(imageFiles);
//        final File[] imageFiles = imageDirectory.listFiles();
//        if (imageFiles==null || imageFiles.length==0) {
//            tableModel.clear();
//            return;
//        }     
        final List<Path> paths = new ArrayList<>();
        try {
            DirectoryStream<Path> ds = Files.newDirectoryStream(imageDir);
            for (Path p : ds) {
                String test = p.getFileName().toString();
                if (!test.startsWith(".") && !test.startsWith("__")) {
                    paths.add(p);
                }
            }  
        } catch (Exception e) { 
        }
        
        if (paths.isEmpty()) {
            tableModel.clear();
            this.setImageOrPDFIsExist(false);
            return;
        }
        setImageOrPDFIsExist(true);
//minagawa$         
        final int total = paths.size();
       
        SwingWorker worker = new SwingWorker<ArrayList<ImageEntry>, Void>() {

            @Override
            protected ArrayList<ImageEntry> doInBackground() throws Exception {
                
                ArrayList<ImageEntry> imageList = new ArrayList<ImageEntry>();

                // Sort
                if (sortIsLastModified()) {
                    // 最終更新日でソート
                    if (sortIsDescending()) {
                        Collections.sort(paths, new Comparator() {
                            @Override
                            public int compare(final Object o1, final Object o2) {
                                try {
                                    FileTime l1 = Files.getLastModifiedTime((Path)o1);
                                    FileTime l2 = Files.getLastModifiedTime((Path)o2);
                                    return l2.compareTo(l1);
                                } catch (Exception e) {
                                }
                                return 0;
                            }
                        });

                    } else {
                         Collections.sort(paths, new Comparator() {
                            @Override
                            public int compare(final Object o1, final Object o2) {
                                try {
                                    FileTime l1 = Files.getLastModifiedTime((Path)o1);
                                    FileTime l2 = Files.getLastModifiedTime((Path)o2);
                                    return l1.compareTo(l2);
                                } catch (Exception e) {
                                }
                                return 0;
                            }
                        });
                    }
                } else {
                    // filename でソート
                    if (sortIsDescending()) {
                        Collections.sort(paths, new Comparator() {
                            @Override
                            public int compare(final Object o1, final Object o2) {
                                String n1 = ((Path)o1).getFileName().toString();
                                String n2 = ((Path)o2).getFileName().toString();
                                return n2.compareTo(n1);
                            }
                        });

                    } else {
                         Collections.sort(paths, new Comparator() {
                            @Override
                            public int compare(final Object o1, final Object o2) {
                                String n1 = ((Path)o1).getFileName().toString();
                                String n2 = ((Path)o2).getFileName().toString();
                                return n1.compareTo(n2);
                            }
                        });
                    }
                }

                int cnt = 0;

                for (Path path : paths) {
                    //for (File file : imageFiles) {
//minagawa^ lsctest
                    setProgress(100*(++cnt/total));
//minagawa$                    
                    URI uri = path.toUri();
                    URL url = uri.toURL();
                    String pathStr = path.toAbsolutePath().toString();
                    String fileName = path.getFileName().toString();
                    //File f = path.toFile();
                    long last = Files.getLastModifiedTime(path).toMillis();

                    debug(uri, url, pathStr, fileName);
                    
                    if (fileName.startsWith(".")||fileName.startsWith("__M")) {
                        continue;
                    }

                    // ディレクトリの場合
                    if (Files.isDirectory(path)) {
                        ImageEntry entry = new ImageEntry();
                        entry.setUrl(url.toString());
                        entry.setPath(pathStr);
                        entry.setFileName(fileName);
                        entry.setLastModified(last);
//minagawa^ Icon Server                        
                        //entry.setImageIcon(ClientContext.getImageIcon(FOLDER_ICON));
                        entry.setImageIcon(ClientContext.getImageIconArias("icon_foldr"));
//minagawa$                        
                        entry.setDirectrory(true);  // directory
                        imageList.add(entry);
                        continue;
                    }
//                    
//                    if (f.length()==0 || fileName.startsWith(".")) {
//                        continue;
//                    }

                    // 拡張子のないファイル ToDo
                    String suffix = getSuffix(fileName);
                    if (suffix == null) {
                        continue;
                    }

                    boolean found = false;

                    // 画像ファイル: Thumbnailを生成しアイコンへセットする
                    for (int i = 0; i < ACCEPT_IMAGE_TYPES.length; i++) {
                        if (ACCEPT_IMAGE_TYPES[i].equals(suffix)) {
//minagawa^ jdk7                           
                            BufferedImage image =  ImageIO.read(Files.newInputStream(path));
//s.oh^ 2013/03/15 不具合修正(表示できない画像対応)
                            //image = ImageHelper.getFirstScaledInstance(image, MAX_IMAGE_SIZE);
                            //ImageIcon icon = new ImageIcon(image);
                            ImageIcon icon = null;
                            if(image == null) {
//minagawa^ Icon Server                                
                                //icon = ClientContext.getImageIcon(AbstractBrowser.DEFAULT_DOC_ICON);
                                icon = ClientContext.getImageIconArias("icon_default_document");
//minagawa$                                
                            }else{
                                image = ImageHelper.getFirstScaledInstance(image, MAX_IMAGE_SIZE);
                                icon = new ImageIcon(image);
                            }
//s.oh$

                            ImageEntry entry = new ImageEntry();
                            entry.setUrl(url.toString());
                            entry.setPath(pathStr);
                            entry.setFileName(fileName);
                            entry.setLastModified(last);
                            entry.setImageIcon(icon);
                            imageList.add(entry);
                            found = true;
                            
//                            Iterator readers = ImageIO.getImageReadersBySuffix(suffix);                         
//                            if (readers.hasNext()) {
//                                
//                                ImageReader reader = (ImageReader)readers.next();
//                                reader.setInput(new FileImageInputStream(new File(pathStr)), true);
//                                BufferedImage image = reader.read(0);
//                                image = ImageHelper.getFirstScaledInstance(image, MAX_IMAGE_SIZE);
//                                ImageIcon icon = new ImageIcon(image);
//                                
//                                ImageEntry entry = new ImageEntry();
//                                entry.setUrl(url.toString());
//                                entry.setPath(pathStr);
//                                entry.setFileName(fileName);
//                                entry.setLastModified(last);
//                                entry.setImageIcon(icon);
//                                imageList.add(entry);
//                                found = true;
//                            }
//minagawa$                            
                            break;
                        }
                    }

                    if (found) {
                        continue;
                    }

                    // 文書: Doc Icon
                    for (int i = 0; i < ACCEPT_DOC_TYPES.length; i++) {
                        if (ACCEPT_DOC_TYPES[i].equals(suffix)) {
                            ImageEntry entry = new ImageEntry();
                            entry.setUrl(url.toString());
                            entry.setPath(pathStr);
                            entry.setFileName(fileName);
                            entry.setLastModified(last);
//minagawa^ Icon Server                            
                            //ImageIcon icon = ClientContext.getImageIcon(ACCEPT_DOC_ICONS[i]);
                            ImageIcon icon = ClientContext.getImageIconArias(ACCEPT_DOC_ICONS[i]);
//minagawa$                            
                            entry.setImageIcon(icon);
                            imageList.add(entry);
                            found = true;
                            break;
                        }
                    }

                    if (found) {
                        continue;
                    }

                    // Default Icon
                    ImageEntry entry = new ImageEntry();
                    entry.setUrl(url.toString());
                    entry.setPath(pathStr);
                    entry.setFileName(fileName);
                    entry.setLastModified(last);
//minagawa^ Icon Server                    
                    //ImageIcon icon = ClientContext.getImageIcon(DEFAULT_DOC_ICON);
                    ImageIcon icon = ClientContext.getImageIconArias("icon_default_document");
//minagawa$                    
                    entry.setImageIcon(icon);
                    imageList.add(entry);
                }

                return imageList;
            }

            @Override
            protected void done() {
                try {
                    tableModel.setImageList(get());
                } catch (InterruptedException | ExecutionException ex) {
                    ex.printStackTrace(System.err);
                }
            }
        };
        
        worker.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("progress".equals(evt.getPropertyName())) {
                    int progress = (Integer)evt.getNewValue();
                    progressMonitor.setProgress(progress);
                    String message = String.format("%d/%d 件を処理しています...", progress, total);
                    progressMonitor.setNote(message);
                }
            }
        });
//minagawa^ lsctest
        //progressMonitor = new ProgressMonitor(getUI(),"画像・PDFスキャン", "", 0, total-1);
        progressMonitor = new ProgressMonitor(getUI(),"画像・PDFスキャン", "", 0, 100);
//minagawa$        
        progressMonitor.setProgress(0);
    
        worker.execute();
    }

    protected abstract String getImgLocation();

    protected abstract void initComponents();

    protected void openImage(ImageEntry entry) {

        if (desktop==null) {
            return;
        }
//minagawa^ jdk7       
        //File f = new File(entry.getPath());
        try {
            //desktop.open(f);
            Path path = Paths.get(entry.getPath());
            desktop.browse(path.toUri());
//minagawa$            
        } catch (Exception ex) {
            ClientContext.getBootLogger().warn(ex);
        }
    }
    
    @Override
    public void start() {
        initComponents();
        scan(getImgLocation());
    }

    @Override
    public void stop() {
    }

    protected boolean valueIsNullOrEmpty(String test) {
        return (test==null || test.equals("")) ? true : false;
    }

    protected boolean valueIsNotNullNorEmpty(String test) {
        return !valueIsNullOrEmpty(test);
    }

    public JTable getTable() {
        return table;
    }
}
