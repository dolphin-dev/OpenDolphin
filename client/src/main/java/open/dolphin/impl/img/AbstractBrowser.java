package open.dolphin.impl.img;

import java.awt.Desktop;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
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

    protected static final String[] ACCEPT_IMAGE_TYPES = {"jpg", "png", "bmp", "gif", "tif"};
    protected static final String[] ACCEPT_DOC_TYPES = {"pdf", "doc","docx", "xls", "xlsx", "ppt","pptx"};

    protected static final String[] ACCEPT_DOC_ICONS = 
        {"icon_pdf", "icon_word","icon_word", "icon_excel", "icon_excel", "icon_power_point", "icon_power_point"};   

    protected final String DATE_FORMAT;

    protected ImageTableModel tableModel;
    protected JTable table;

    protected Desktop desktop;

    //protected boolean DEBUG;

    protected String imageBase;
    protected Properties properties;
    
    protected ProgressMonitor progressMonitor;
    
    protected boolean imageOrPDFIsExist;
    
    protected int imgCounter;
    
    protected String nowLocation;
    
//s.oh^ 2014/05/07 PDF・画像タブの改善
    protected boolean scanning;
//s.oh$
    
    public AbstractBrowser() {
        DATE_FORMAT = ClientContext.getMyBundle(AbstractBrowser.class).getString("dateFormat.imageBrowser");
        if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
        } else {
            java.util.logging.Logger.getLogger(this.getClass().getName()).warning("Desktop is not supported");
        }
    }
    
    public boolean isImageOrPDFIsExist() {
        return imageOrPDFIsExist;
    }
    
    public void setImageOrPDFIsExist(boolean b) {
        boolean old = imageOrPDFIsExist;
        imageOrPDFIsExist = b;
        if (old!=imageOrPDFIsExist) {
            ImageIcon icon =  imageOrPDFIsExist ? ClientContext.getImageIconArias("icon_indicate_has_iamges_or_pdfs") : null;            
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
     * @param ctx
     * @return 
     */
    @Override
    public ImageIcon getIconInfo(Chart ctx) {
        ImageIcon icon;
        PatientModel pm = ctx.getPatient();
        String pid = pm.getPatientId();
        imageOrPDFIsExist = hasImageOrPDF(pid);
        icon =  imageOrPDFIsExist ? ClientContext.getImageIconArias("icon_indicate_has_iamges_or_pdfs") : null;    
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
//s.oh^ 2013/04/19 パスが正しく表示されない
            //scan(imageBase);
            scan(getImgLocation());
//s.oh$
        }
    }
    
    private void debug(URI uri, URL url, String path, String fileName) {
        java.util.logging.Logger.getLogger(this.getClass().getName()).fine("-------------------------------------------");
        java.util.logging.Logger.getLogger(this.getClass().getName()).log(java.util.logging.Level.FINE, "URI = {0}", uri.toString());
        java.util.logging.Logger.getLogger(this.getClass().getName()).log(java.util.logging.Level.FINE, "URL = {0}", url.toString());
        java.util.logging.Logger.getLogger(this.getClass().getName()).log(java.util.logging.Level.FINE, "PATH = {0}", path);
        java.util.logging.Logger.getLogger(this.getClass().getName()).log(java.util.logging.Level.FINE, "File Name = {0}", fileName);
    }

    /**
     * 患者フォルダをスキャンする。
     * @param imgLoc
     */
    protected void scan(String imgLoc) {
        
//s.oh^ 2014/05/07 PDF・画像タブの改善
        if(scanning) {
            return;
        }
        scanning = true;
//s.oh$

        //String imgLoc = getImgLocation();
        imgCounter = 0;

        if (valueIsNullOrEmpty(imgLoc)) {
            tableModel.clear();
            this.setImageOrPDFIsExist(false);
//s.oh^ 2014/05/07 PDF・画像タブの改善
            scanning = false;
//s.oh$
            return;
        }

        Path imageDir = Paths.get(imgLoc);
        if (!Files.exists(imageDir) || !Files.isDirectory(imageDir)) {
            tableModel.clear();
            this.setImageOrPDFIsExist(false);
//s.oh^ 2014/05/07 PDF・画像タブの改善
            scanning = false;
//s.oh$
            return;
        }  

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
//s.oh^ 2014/05/07 PDF・画像タブの改善
            scanning = false;
//s.oh$
            return;
        }
        setImageOrPDFIsExist(true);       
        final int total = paths.size();
       
        SwingWorker worker = new SwingWorker<ArrayList<ImageEntry>, Void>() {

            @Override
            protected ArrayList<ImageEntry> doInBackground() throws Exception {
                
                ArrayList<ImageEntry> imageList = new ArrayList<>();

                // Sort
                if (sortIsLastModified()) {
                    // 最終更新日でソート
                    if (sortIsDescending()) {
                        Collections.sort(paths, (final Object o1, final Object o2) -> {
                            try {
                                FileTime l1 = Files.getLastModifiedTime((Path)o1);
                                FileTime l2 = Files.getLastModifiedTime((Path)o2);
                                return l2.compareTo(l1);
                            } catch (Exception e) {
                            }
                            return 0;
                        });

                    } else {
                         Collections.sort(paths, (final Object o1, final Object o2) -> {
                             try {
                                 FileTime l1 = Files.getLastModifiedTime((Path)o1);
                                 FileTime l2 = Files.getLastModifiedTime((Path)o2);
                                 return l1.compareTo(l2);
                             } catch (Exception e) {
                             }
                             return 0;
                         });
                    }
                } else {
                    // filename でソート
                    if (sortIsDescending()) {
                        Collections.sort(paths, (final Object o1, final Object o2) -> {
                            String n1 = ((Path)o1).getFileName().toString();
                            String n2 = ((Path)o2).getFileName().toString();
                            return n2.compareTo(n1);
                        });

                    } else {
                         Collections.sort(paths, (final Object o1, final Object o2) -> {
                             String n1 = ((Path)o1).getFileName().toString();
                             String n2 = ((Path)o2).getFileName().toString();
                             return n1.compareTo(n2);
                         });
                    }
                }

                int cnt = 0;

                for (Path path : paths) {
                    //for (File file : imageFiles) {
//s.oh^ 2013/11/07 複数画像表示できない不具合
                    //setProgress(100*(++cnt/total));
                    if(progressMonitor != null && progressMonitor.isCanceled()) {
                        return imageList;
                    }
                    imgCounter += 1;
                    double tmp = 100 * ((double)imgCounter / (double)total);
                    if(imgCounter >= total) {
                        setProgress(100);
                    }else{
                        setProgress((int)tmp);
                    }
//s.oh$                    
                    URI uri = path.toUri();
                    URL url = uri.toURL();
                    String pathStr = path.toAbsolutePath().toString();
                    String fileName = path.getFileName().toString();
                    if(fileName.endsWith("Thumbs.db")) {
                        continue;
                    }
                    //File f = path.toFile();
//s.oh^ 2014/05/07 PDF・画像タブの改善
                    //long last = Files.getLastModifiedTime(path).toMillis();
                    long last = 0;
                    try{
                        Files.getLastModifiedTime(path).toMillis();
                    }catch(IOException e) {
                        continue;
                    }
//s.oh$

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
                        entry.setImageIcon(ClientContext.getImageIconArias("icon_foldr"));                      
                        entry.setDirectrory(true);  // directory
                        imageList.add(entry);
                        continue;
                    }

                    // 拡張子のないファイル ToDo
                    String suffix = getSuffix(fileName);
                    if (suffix == null) {
//s.oh^ 2014/07/29 PDF・画像タブの改善
                        //continue;
                        suffix = "unnone";
//s.oh$
                    }

                    boolean found = false;

                    // 画像ファイル: Thumbnailを生成しアイコンへセットする
                    for (int i = 0; i < ACCEPT_IMAGE_TYPES.length; i++) {
                        if (ACCEPT_IMAGE_TYPES[i].equals(suffix)) {                          
//s.oh^ 2014/05/07 PDF・画像タブの改善
                            //BufferedImage image =  ImageIO.read(Files.newInputStream(path));
                            InputStream is = Files.newInputStream(path);
                            BufferedImage image =  ImageIO.read(is);
//s.oh$
//s.oh^ 2013/03/15 不具合修正(表示できない画像対応)
                            //image = ImageHelper.getFirstScaledInstance(image, MAX_IMAGE_SIZE);
                            //ImageIcon icon = new ImageIcon(image);
                            ImageIcon icon ;
                            if(image == null) {
                                icon = ClientContext.getImageIconArias("icon_default_document");                                
                            }else{
                                image = ImageHelper.getFirstScaledInstance(image, MAX_IMAGE_SIZE);
                                icon = new ImageIcon(image);
//s.oh^ 2014/02/24 PDF・画像ファイルの解放
                                image.flush();
//s.oh$
//s.oh^ 2014/05/07 PDF・画像タブの改善
                                is.close();
//s.oh$
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
                            ImageIcon icon = ClientContext.getImageIconArias(ACCEPT_DOC_ICONS[i]);                           
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
                    ImageIcon icon = ClientContext.getImageIconArias("icon_default_document");                   
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
//s.oh^ 2014/05/07 PDF・画像タブの改善
                scanning = false;
//s.oh$
            }
        };
        
        worker.addPropertyChangeListener((PropertyChangeEvent evt) -> {
            if ("progress".equals(evt.getPropertyName())) {
                int progress = (Integer)evt.getNewValue();
                progressMonitor.setProgress(progress);
                String fmt = ClientContext.getMyBundle(AbstractBrowser.class).getString("message.processing");
                String message = String.format(fmt, imgCounter, total);
                progressMonitor.setNote(message);
            }
        });
        String progressTitle = ClientContext.getMyBundle(AbstractBrowser.class).getString("title.sacaning");
        progressMonitor = new ProgressMonitor(getUI(),progressTitle, "", 0, 100);        
        progressMonitor.setProgress(0);
    
        worker.execute();
    }
    
//s.oh^ 2014/05/07 PDF・画像タブの改善
    protected boolean isScanning(Window parent, String msg) {
        if(scanning) {
            String fmt = ClientContext.getMyBundle(AbstractBrowser.class).getString("messageFormat.reading");
            MessageFormat msf = new MessageFormat(fmt);
            String message = msf.format(new Object[]{msg});
            String title = ClientContext.getMyBundle(AbstractBrowser.class).getString("title.optionPane.reading");  // ? title with ...
            JOptionPane.showMessageDialog(parent, message, title, JOptionPane.WARNING_MESSAGE);
            return true;
        }
        return false;
    }
//s.oh$

    protected abstract String getImgLocation();

    protected abstract void initComponents();

    protected void openImage(ImageEntry entry) {

        if (desktop==null) {
            return;
        }
        try {
            //desktop.open(f);
            Path path = Paths.get(entry.getPath());
            desktop.browse(path.toUri());           
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(this.getClass().getName()).fine(ex.getMessage());
        }
    }
    
    protected String getNowLocation() {
        return nowLocation;
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
        return (test==null || test.equals(""));
    }

    protected boolean valueIsNotNullNorEmpty(String test) {
        return !valueIsNullOrEmpty(test);
    }

    public JTable getTable() {
        return table;
    }
    
//s.oh^ 2014/07/29 PDF・画像タブの改善
    protected String checkDirName(String location, String name) {
        File dir = new File(location, name);
        if(dir.exists()) {
            for(int i = 2; i < 10000; i++) {
                dir = new File(location, String.format("%s（%d）", name, i));
                if(!dir.exists()) {
                    break;
                }
            }
        }
        return dir.getName();
    }
//s.oh$
}
