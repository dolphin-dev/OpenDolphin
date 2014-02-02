package open.dolphin.impl.img;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.TableColumn;
import open.dolphin.utilities.utility.OtherProcessLink;
import open.dolphin.client.ClientContext;
import open.dolphin.client.GUIConst;
import open.dolphin.client.ImageEntry;
import open.dolphin.infomodel.DocInfoModel;
import open.dolphin.infomodel.ModelUtils;
import open.dolphin.project.Project;

/**
 * 他プロセス連携
 * @author Life Sciences Computing Corporation.
 */
public class DefaultBrowserEx extends AbstractBrowser {

    private static final String TITLE = "PDF・画像";
    private static final String SETTING_FILE_NAME = "defaultex.properties";
    private static final String DATE_FORMAT = "yyyyMMdd";
    private static final String KEY_UPPER_FOLDER = "%UPPERFOLDER%";
    private static final String KEY_CREATE_FOLDER = "%CREATEFOLDER%";
    private static final String KEY_PATIENT_ID = "%PATIENTID%";
    private static final String KEY_STUDY_DATE = "%STUDYDATE%";
    private static final String KEY_TODAY = "%TODAY%";
    private static final String TAG_EXE = "[EXE]";
    private static final String TAG_URL = "[URL]";
    private static final String TAG_FILE = "[FILE]";
    private static final String TAG_TCP_EXE = "[TCP:EXE]";
    private static final String TAG_TCP_FILE = "[TCP:FILE]";

    private ImageTableRenderer imageRenderer;
    private int cellWidth = MAX_IMAGE_SIZE + CELL_WIDTH_MARGIN;
    private int cellHeight = MAX_IMAGE_SIZE + CELL_HEIGHT_MARGIN;

    private DefaultBrowserViewEx view;
    private String otherProcess1;
    private String otherProcess2;
    private String otherProcess3;
    private String nowLocation;
  
    public DefaultBrowserEx() {
        
        String title = Project.getString("defaultex.title.name");
        if(valueIsNullOrEmpty(title)) {
            setTitle(TITLE);
        }else{
            setTitle(title);
        }
        
        properties = getProperties();
        
        otherProcess1 = Project.getString("otherprocess1.link.name");
        otherProcess2 = Project.getString("otherprocess2.link.name");
        otherProcess3 = Project.getString("otherprocess3.link.name");

        // Convert the old properties
        Properties old = Project.loadPropertiesAsObject("imageBrowserProp2.xml");
        if (old!=null) {
            Enumeration e = old.propertyNames();
            while (e.hasMoreElements()) {
                String key = (String)e.nextElement();
                String val = old.getProperty(key);
                properties.setProperty(key, val);
            }
            Project.storeProperties(properties, SETTING_FILE_NAME);
            Project.deleteSettingFile("imageBrowserProp2.xml");
            
        } else {
            Project.loadProperties(properties, SETTING_FILE_NAME);
        }

        // Base directory
        String value = properties.getProperty(PROP_BASE_DIR);
        imageBase = valueIsNotNullNorEmpty(value) ? value : null;
    }
      
    @Override
    protected String getImgLocation() {
        
        if (getContext()==null) {
            view.getDirLbl().setText("");
            return null;
        }

        if (valueIsNullOrEmpty(getImageBase())) {
            view.getDirLbl().setText("画像ディレクトリが指定されていません。");
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
        nowLocation = loc;
        view.getDirLbl().setText(createLocationText(nowLocation));

        return loc;
    }
    
    private String createLocationText(String loc) {
        StringBuilder sb = new StringBuilder();
        if (loc.length() > 33) {
            sb.append(loc.substring(0, 15));
            sb.append("...");
            int pos = loc.length() - 15;
            sb.append(loc.substring(pos));
        } else {
            return loc;
        }
        
        return sb.toString();
    }

    private ActionMap getActionMap(ResourceBundle resource) {

        ActionMap ret = new ActionMap();

//minagawa^ Icon Server        
        //ImageIcon icon = ClientContext.getImageIcon("ref_16.gif");
        ImageIcon icon = ClientContext.getImageIconArias("icon_refresh_small");
//minagawa$        
        AbstractAction refresh = new AbstractAction("更新",icon) {

            @Override
            public void actionPerformed(ActionEvent ae) {
                scan(getImgLocation());
                nowLocation = getImgLocation();
                view.getDirLbl().setText(createLocationText(nowLocation));
            }
        };
        ret.put("refresh", refresh);

//minagawa^ IconServer        
        //icon = ClientContext.getImageIcon("confg_16.gif");
        icon = ClientContext.getImageIconArias("icon_setting_small");
//minagawa$        
        AbstractAction doSetting = new AbstractAction("設定",icon) {

            @Override
            public void actionPerformed(ActionEvent ae) {

                // 現在のパラメータを保存し、Setting dialog を開始する
                int oldCount = columnCount();
                boolean oldShow = showFilename();
                boolean oldDisplayIsFilename = displayIsFilename();
                boolean oldSortIsLastModified = sortIsLastModified();
                boolean oldSortIsDescending = sortIsDescending();
                String oldBase = properties.getProperty(PROP_BASE_DIR);
                oldBase = valueIsNotNullNorEmpty(oldBase) ? oldBase : "";

                // 設定ダイアログを起動する
                DefaultSettingEx setting = new DefaultSettingEx(DefaultBrowserEx.this, getUI());
                setting.start();

                // 結果は properties にセットされて返ってくるので save する
                Project.storeProperties(properties, SETTING_FILE_NAME);

                // 新たに設定された値を読む
                int newCount = columnCount();
                boolean newShow = showFilename();
                boolean newDisplayIsFilename = displayIsFilename();
                boolean newSortIsLastModified = sortIsLastModified();
                boolean newSortIsDescending = sortIsDescending();
                String newBase = properties.getProperty(PROP_BASE_DIR);
                newBase = valueIsNotNullNorEmpty(newBase) ? newBase : "";

                // 更新ボタンの enabled
                boolean canRefresh = true;
                canRefresh = canRefresh && (!newBase.equals(""));
                view.getRefreshBtn().setEnabled(canRefresh);

                boolean needsRefresh = false;

                // カラム数変更
                if (newCount != oldCount) {
                    needsRefresh = true;
                    tableModel = new ImageTableModel(null, newCount);
                    table.setModel(tableModel);
                    TableColumn column;
                    for (int i = 0; i < newCount; i++) {
                        column = table.getColumnModel().getColumn(i);
                        column.setPreferredWidth(cellWidth);
                    }
                    table.setRowHeight(cellHeight);
                }

                needsRefresh = (needsRefresh ||
                                (newShow!=oldShow) ||
                                (newDisplayIsFilename!=oldDisplayIsFilename) ||
                                (newSortIsLastModified!=oldSortIsLastModified) ||
                                (newSortIsDescending!=oldSortIsDescending));

                // ベースディレクトリ
                if (!newBase.equals(oldBase)) {
                    setImageBase(newBase);
                } else if (needsRefresh) {
                    scan(getImgLocation());
                    nowLocation = getImgLocation();
                    view.getDirLbl().setText(createLocationText(nowLocation));
                }
            }
        };
        ret.put("doSetting", doSetting);
         
        if(otherProcess1 != null && otherProcess1.length() > 0) {
            AbstractAction process1 = new AbstractAction(otherProcess1) {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    linkOtherProcess1();
                }
            };
            ret.put("process1", process1);
        }

        if(otherProcess2 != null && otherProcess2.length() > 0) {
            AbstractAction process2 = new AbstractAction(otherProcess2) {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    linkOtherProcess2();
                }
            };
            ret.put("process2", process2);
        }

        if(otherProcess3 != null && otherProcess3.length() > 0) {
            AbstractAction process3 = new AbstractAction(otherProcess3) {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    linkOtherProcess3();
                }
            };
            ret.put("process3", process3);
        }

        return ret;
    }
    
    private void linkOtherProcess1() {
        otherProcess(Project.getString("otherprocess1.link.path"),
                     Project.getString("otherprocess1.link.param"),
                     null);
    }
    
    private void linkOtherProcess2() {
        otherProcess(Project.getString("otherprocess2.link.path"),
                     Project.getString("otherprocess2.link.param"),
                     null);
    }
    
    private void linkOtherProcess3() {
        otherProcess(Project.getString("otherprocess3.link.path"),
                     Project.getString("otherprocess3.link.param"),
                     null);
    }
    
    private void linkOtherProcessThumbnail(ImageEntry entry) {
        String date = null;
        if(entry.getFileName().length() >= DATE_FORMAT.length()) {
            date = entry.getFileName().substring(0, DATE_FORMAT.length());
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            try {
                sdf.parse(date);
                if(!otherProcess(Project.getString("otherprocess.link.path"),
                                 Project.getString("otherprocess.link.param"),
                                 date)) {
                    openImage(entry);
                }
            } catch (ParseException ex) {
                Logger.getLogger(DefaultBrowserEx.class.getName()).log(Level.SEVERE, null, ex);
                openImage(entry);
            }
        }else{
            openImage(entry);
        }
    }
    
    private boolean otherProcess(String processPath, String processParam, String imageDate) {
        if(processPath == null || processPath.length() <= 0) return false;
        
        if(processPath.equals(KEY_UPPER_FOLDER)) {
            upperFolder();
            return false;
        }else if(processPath.equals(KEY_CREATE_FOLDER)) {
            createFolder();
            return false;
        }
        
        OtherProcessLink opl = new OtherProcessLink();
        
        if(processPath.startsWith(TAG_URL)) {
            // URL
            String url = processPath.substring(processPath.indexOf("]") + 1);
            DocInfoModel[] selectModel = getContext().getDocumentHistory().getSelectedHistories();
            String dateSelect = null;
            String dateToday = null;
            if(imageDate == null) {
                if(selectModel != null && selectModel.length == 1) {
                    dateSelect = ModelUtils.getDateAsFormatString(selectModel[0].getFirstConfirmDate(), DATE_FORMAT);
                }
            }else{
                dateSelect = imageDate;
            }
            Date today = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            dateToday = sdf.format(today);
            url = url.replaceAll(KEY_PATIENT_ID, getContext().getPatient().getPatientId());
            if(dateSelect != null) {
                url = url.replaceAll(KEY_STUDY_DATE, dateSelect);
            }else{
                url = url.replaceAll(KEY_STUDY_DATE, dateToday);
            }
            url = url.replaceAll(KEY_TODAY, dateToday);
            
            opl.linkURL(url);
        }else if(processPath.startsWith(TAG_EXE)) {
            // Exe
            String path = processPath.substring(processPath.indexOf("]") + 1);
            String param = processParam;
            String command = null;
            if(param != null && param.length() > 0) {
                DocInfoModel[] selectModel = getContext().getDocumentHistory().getSelectedHistories();
                String dateSelect = null;
                String dateToday = null;
                if(imageDate == null) {
                    if(selectModel != null && selectModel.length == 1) {
                        dateSelect = ModelUtils.getDateAsFormatString(selectModel[0].getFirstConfirmDate(), DATE_FORMAT);
                    }
                }else{
                    dateSelect = imageDate;
                }
                Date today = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
                dateToday = sdf.format(today);
                param = param.replaceAll(KEY_PATIENT_ID, getContext().getPatient().getPatientId());
                if(dateSelect != null) {
                    param = param.replaceAll(KEY_STUDY_DATE, dateSelect);
                }else{
                    param = param.replaceAll(KEY_STUDY_DATE, dateToday);
                }
                param = param.replaceAll(KEY_TODAY, dateToday);
                if(path.indexOf(" ") >= 0) {
                    command = "\"" + path + "\" " + param;
                }else{
                    command = path + " " + param;
                }
            }else{
                if(path.indexOf(" ") >= 0) {
                    command = "\"" + path + "\"";
                }else{
                    command = path;
                }
            }
            
            opl.linkFile(command);
        }else if(processPath.startsWith(TAG_FILE)) {
            // File
            String path = processPath.substring(processPath.indexOf("]") + 1);
            String param = processParam;
            if(param != null && param.length() > 0) {
                DocInfoModel[] selectModel = getContext().getDocumentHistory().getSelectedHistories();
                String dateSelect = null;
                String dateToday = null;
                if(imageDate == null) {
                    if(selectModel != null && selectModel.length == 1) {
                        dateSelect = ModelUtils.getDateAsFormatString(selectModel[0].getFirstConfirmDate(), DATE_FORMAT);
                    }
                }else{
                    dateSelect = imageDate;
                }
                Date today = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
                dateToday = sdf.format(today);
                param = param.replaceAll(KEY_PATIENT_ID, getContext().getPatient().getPatientId());
                if(dateSelect != null) {
                    param = param.replaceAll(KEY_STUDY_DATE, dateSelect);
                }else{
                    param = param.replaceAll(KEY_STUDY_DATE, dateToday);
                }
                param = param.replaceAll(KEY_TODAY, dateToday);
            }
            
            File file = new File(path);
            BufferedWriter bw;
            try {
                bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
                bw.write(param);
                bw.newLine();
                bw.close();
            } catch (IOException ex) {
                Logger.getLogger(DefaultBrowserEx.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else if(processPath.startsWith(TAG_TCP_EXE)) {
            // TCP/IP:Exe
            String tcp = processPath.substring(processPath.indexOf("]") + 1);
            String[] data = tcp.split(",");
            if(data.length < 2) return false;
            String val = processParam;
            val = val.replaceAll(KEY_PATIENT_ID, getContext().getPatient().getPatientId());
            opl.linkTCPToExe(val, data[0], Integer.valueOf(data[1]));
        }else if(processPath.startsWith(TAG_TCP_FILE)) {
            // TCP/IP:File
            String tcp = processPath.substring(processPath.indexOf("]") + 1);
            String[] data = tcp.split(",");
            if(data.length < 3) return false;
            String val = processParam;
            val = val.replaceAll(KEY_PATIENT_ID, getContext().getPatient().getPatientId());
            opl.linkTCPToFile(val, data[0], Integer.valueOf(data[1]), data[2]);
        }
        
        return true;
    }
    
    public String getNowLocation() {
        return nowLocation;
    }
    
    private void upperFolder() {
        String path = getImageBase() + File.separator + getContext().getPatient().getPatientId();
        if(path.equals(nowLocation)) return;
        File dir = new File(nowLocation);
        nowLocation = dir.getParent();
        scan(nowLocation);
        view.getDirLbl().setText(createLocationText(nowLocation));
    }
    
    private void createFolder() {
        String name = JOptionPane.showInputDialog("フォルダ名を入力してください。", "新規フォルダ");
        if(name != null && name.length() >= 0) {
            File dir = new File(nowLocation);
            if((!dir.exists()) || (!dir.isDirectory())) return;
            File[] files = dir.listFiles();
            if(files == null) return;
            for(File file : files) {
                if(file.getName().equals(name)) {
                    JOptionPane.showMessageDialog(null, "同じ名前が既に存在します。別の名前を指定してください。");
                    return;
                }
            }
            name = nowLocation + File.separator + name;
            File folder = new File(name);
            folder.mkdir();
            scan(nowLocation);
        }
    }
    
    @Override
    protected void initComponents() {

        ResourceBundle resource = ClientContext.getBundle(this.getClass());
        ActionMap map = getActionMap(resource);

        // TableModel
        int columnCount = columnCount();
        tableModel = new ImageTableModel(null, columnCount);

        view = new DefaultBrowserViewEx();
        table = view.getTable();
        table.setModel(tableModel);
        table.putClientProperty("karteCompositor", this);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setCellSelectionEnabled(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setDragEnabled(true);
        table.setTransferHandler(new ImageTableTransferHandler(this));

        TableColumn column;
        for (int i = 0; i < columnCount; i++) {
            column = view.getTable().getColumnModel().getColumn(i);
            column.setPreferredWidth(cellWidth);
        }
        table.setRowHeight(cellHeight);

        // Renderer
        imageRenderer = new ImageTableRenderer(this);
        imageRenderer.setImageSize(MAX_IMAGE_SIZE);
        table.setDefaultRenderer(java.lang.Object.class, imageRenderer);

        table.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount()==1) {
                    int row = table.getSelectedRow();
                    int col = table.getSelectedColumn();
                    ImageEntry entry = getEntryAt(row, col);
                    Action copy = getContext().getChartMediator().getAction(GUIConst.ACTION_COPY);
                    copy.setEnabled(entry!=null && (!entry.isDirectrory()));
                }
                else if(e.getClickCount()==2) {
                    int row = table.getSelectedRow();
                    int col = table.getSelectedColumn();
                    ImageEntry entry = getEntryAt(row, col);
                    if (entry!=null && (!entry.isDirectrory())) {
                        if(entry.getFileName().toUpperCase().endsWith(".JPG")) {
                            linkOtherProcessThumbnail(entry);
                        }else{
                            openImage(entry);
                        }
                    } else if (entry!=null && entry.isDirectrory()) {
                        scan(entry.getPath());
                        nowLocation = entry.getPath();
                        view.getDirLbl().setText(createLocationText(nowLocation));
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent me) {
                mabeShowPopup(me);
            }

            @Override
            public void mouseReleased(MouseEvent me) {
                mabeShowPopup(me);
            }

            private void mabeShowPopup(MouseEvent e) {

                if (!e.isPopupTrigger()) {
                    return;
                }

                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                Object entry = tableModel.getValueAt(row, col);

                if (entry==null) {
                    return;
                }

                JPopupMenu contextMenu = new JPopupMenu();
                JMenuItem micp = new JMenuItem("コピー");
                Action copy = getContext().getChartMediator().getAction(GUIConst.ACTION_COPY);
                micp.setAction(copy);
                contextMenu.add(micp);
                
                contextMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        });
        
        view.getSettingBtn().setAction(map.get("doSetting"));
        view.getSettingBtn().setToolTipText("画像ディレクトリ等の設定を行います。");
        view.getRefreshBtn().setAction(map.get("refresh"));
        view.getRefreshBtn().setToolTipText("表示を更新します。");
        boolean canRefresh = true;
        canRefresh = canRefresh && (valueIsNotNullNorEmpty(properties.getProperty(PROP_BASE_DIR)));
        view.getRefreshBtn().setEnabled(canRefresh);

        if(otherProcess1 != null && otherProcess1.length() > 0) {
            view.getOtherProcess1Btn().setAction(map.get("process1"));
        }else{
            view.getOtherProcess1Btn().setVisible(false);
        }
        if(otherProcess2 != null && otherProcess2.length() > 0) {
            view.getOtherProcess2Btn().setAction(map.get("process2"));
        }else{
            view.getOtherProcess2Btn().setVisible(false);
        }
        if(otherProcess3 != null && otherProcess3.length() > 0) {
            view.getOtherProcess3Btn().setAction(map.get("process3"));
        }else{
            view.getOtherProcess3Btn().setVisible(false);
        }

//minagawa^ Icon Server
        view.getDirLbl().setIcon(ClientContext.getImageIconArias("icon_info_small"));
        view.getDirLbl().setToolTipText("画像・PDFディレクトリの場所を表示してます。");
//minagawa$        
        setUI(view);
    }
}
