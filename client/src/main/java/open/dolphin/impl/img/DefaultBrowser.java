package open.dolphin.impl.img;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.swing.*;
import javax.swing.table.TableColumn;
import open.dolphin.client.ClientContext;
import open.dolphin.client.GUIConst;
import open.dolphin.client.ImageEntry;
import open.dolphin.project.Project;
import open.dolphin.util.Log;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class DefaultBrowser extends AbstractBrowser {

    private static final String TITLE = "PDF・画像";
    private static final String SETTING_FILE_NAME = "image-browser.properties";

    private ImageTableRenderer imageRenderer;
    private int cellWidth = MAX_IMAGE_SIZE + CELL_WIDTH_MARGIN;
    private int cellHeight = MAX_IMAGE_SIZE + CELL_HEIGHT_MARGIN;

    private DefaultBrowserView view;
  
    public DefaultBrowser() {
        
        setTitle(TITLE);
        
        properties = getProperties();

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
        
        String path = (imageBase != null) ? imageBase + File.separator + SETTING_FILE_NAME : SETTING_FILE_NAME;
        Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_INFORMATION, "設定の読込：", path);
        Enumeration e = properties.propertyNames();
        while (e.hasMoreElements()) {
            String key = (String)e.nextElement();
            String val = properties.getProperty(key);
            if(val == null) val = "";
            Log.outputFuncLog(Log.LOG_LEVEL_3, Log.FUNCTIONLOG_KIND_INFORMATION, key, val);
        }
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
        //if (loc.length() > 33) {
        //    sb = new StringBuilder();
        //    sb.append(loc.substring(0, 15));
        //    sb.append("...");
        //    int pos = loc.length() - 15;
        //    sb.append(loc.substring(pos));
        //    view.getDirLbl().setText(sb.toString());
        //
        //} else {
        //    view.getDirLbl().setText(loc);
        //}
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

        //String text = resource.getString("refresh.Action.text");
 //minagawa^ Icon Server       
        //ImageIcon icon = ClientContext.getImageIcon("ref_16.gif");
        ImageIcon icon = ClientContext.getImageIconArias("icon_refresh_small");
 //minagawa$       
        AbstractAction refresh = new AbstractAction("更新",icon) {

            @Override
            public void actionPerformed(ActionEvent ae) {
//s.oh^ 2014/05/07 PDF・画像タブの改善
                if(isScanning(getContext().getFrame(), "更新できません。")) return;
//s.oh$
                scan(getImgLocation());
                nowLocation = getImgLocation();
                view.getDirLbl().setText(createLocationText(nowLocation));
            }
        };
        ret.put("refresh", refresh);

        //text = resource.getString("doSetting.Action.text");
//minagawa^ Icon Server        
        //icon = ClientContext.getImageIcon("confg_16.gif");
        icon = ClientContext.getImageIconArias("icon_setting_small");
//minagawa$        
        AbstractAction doSetting = new AbstractAction("設定",icon) {

            @Override
            public void actionPerformed(ActionEvent ae) {
//s.oh^ 2014/05/07 PDF・画像タブの改善
                if(isScanning(getContext().getFrame(), "設定を変更できません。")) return;
//s.oh$

                // 現在のパラメータを保存し、Setting dialog を開始する
                int oldCount = columnCount();
                boolean oldShow = showFilename();
                boolean oldDisplayIsFilename = displayIsFilename();
                boolean oldSortIsLastModified = sortIsLastModified();
                boolean oldSortIsDescending = sortIsDescending();
                String oldBase = properties.getProperty(PROP_BASE_DIR);
                oldBase = valueIsNotNullNorEmpty(oldBase) ? oldBase : "";

                // 設定ダイアログを起動する
                DefaultSetting setting = new DefaultSetting(DefaultBrowser.this, getUI());
                setting.start();

                // 結果は properties にセットされて返ってくるので save する
                Project.storeProperties(properties, SETTING_FILE_NAME);
                Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_INFORMATION, "設定の保存：", oldBase + File.separator + SETTING_FILE_NAME);
                Enumeration e = properties.propertyNames();
                while (e.hasMoreElements()) {
                    String key = (String)e.nextElement();
                    String val = properties.getProperty(key);
                    if(val == null) val = "";
                    Log.outputFuncLog(Log.LOG_LEVEL_3, Log.FUNCTIONLOG_KIND_INFORMATION, key, val);
                }

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
        
//s.oh^ 2014/05/07 PDF・画像タブの改善
        icon = ClientContext.getImageIconArias("icon_delete");
        AbstractAction delete = new AbstractAction("削除", icon) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if(isScanning(getContext().getFrame(), "ファイル/フォルダを削除できません。")) return;
                int row = table.getSelectedRow();
                int col = table.getSelectedColumn();
                if(row < 0 || col < 0) return;
                ImageEntry entry = getEntryAt(row, col);
                if(entry == null) return;
                //String yes = "はい";
                //String no = "いいえ";
                //Object[] options = new Object[]{yes, no};
                //if (ClientContext.isMac()) {
                //    options = new Object[]{no, yes};
                //}else{
                //    options = new Object[]{yes, no};
                //}
                String msg = "「" + entry.getPath() + "」を削除しますか？";
                //int select = JOptionPane.showOptionDialog(getContext().getFrame(), msg, "削除", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, no);
                int option = JOptionPane.showConfirmDialog(getContext().getFrame(), msg, "削除", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                if(option == JOptionPane.OK_OPTION) {
                    delete(new File(entry.getPath()));
                    if(tableModel != null && tableModel.getImageList() != null) {
                        tableModel.getImageList().remove(entry);
                    }
                    ArrayList<ImageEntry> imageList = new ArrayList<ImageEntry>();
                    for(Object obj : tableModel.getImageList()) {
                        imageList.add(((ImageEntry)obj).copy());
                    }
                    tableModel.setImageList(imageList);
                    //scan(nowLocation);
                }
            }
        };
        ret.put("delete", delete);
        
        AbstractAction rename = new AbstractAction("名前の変更") {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if(isScanning(getContext().getFrame(), "名前の変更はできません。")) return;
                int row = table.getSelectedRow();
                int col = table.getSelectedColumn();
                if(row < 0 || col < 0) return;
                ImageEntry entry = getEntryAt(row, col);
                if(entry == null) return;
                String newName = JOptionPane.showInputDialog(getContext().getFrame(), "名前を入力してください。", entry.getFileName());
                ImageEntry newEntry = rename(newName, entry);
                if(newEntry != null) {
                    ArrayList<ImageEntry> imageList = new ArrayList<ImageEntry>();
                    for(Object obj : tableModel.getImageList()) {
                        if(((ImageEntry)obj).getFileName().equals(entry.getFileName())) {
                            imageList.add(newEntry);
                        }else{
                            imageList.add(((ImageEntry)obj).copy());
                        }
                    }
                    tableModel.setImageList(imageList);
                }
            }
        };
        ret.put("rename", rename);
        
        AbstractAction newdir = new AbstractAction("新規") {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if(isScanning(getContext().getFrame(), "フォルダを作成できません。")) return;
//s.oh^ 2014/07/29 PDF・画像タブの改善
                String name = JOptionPane.showInputDialog(getContext().getFrame(), "フォルダ名を入力してください。", checkDirName(nowLocation, "新規フォルダ"));
//s.oh$
                newdir(name);
            }
        };
        ret.put("newdir", newdir);
//s.oh$
        
//s.oh^ 2014/05/30 PDF・画像タブの改善
        AbstractAction backdir = new AbstractAction("戻る") {
            @Override
            public void actionPerformed(ActionEvent ae) {
                backdir();
            }
        };
        ret.put("backdir", backdir);
//s.oh$

        return ret;
    }
    
//s.oh^ 2014/05/07 PDF・画像タブの改善
    private void delete(File file) {
        if(!file.exists()) {
            return;
        }
        if(file.isFile()) {
            Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_INFORMATION, file.getPath());
            file.delete();
        }else if(file.isDirectory()) {
            File[] files = file.listFiles();
            for(int i = 0; i < files.length; i++) {
                delete(files[i]);
            }
            Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_INFORMATION, file.getPath());
            file.delete();
        }
    }
    
    private ImageEntry rename(String name, ImageEntry entry) {
        if(name != null && name.length() >= 0 && entry != null) {
            File newFile = new File(nowLocation, name);
            if(newFile.exists()) {
                String name2 = JOptionPane.showInputDialog(getContext().getFrame(), "同じ名前が既に存在します。別の名前を入力してください。", name);
                return rename(name2, entry);
            }else if(name.startsWith("__")) {
                String name2 = JOptionPane.showInputDialog(getContext().getFrame(), "__から始まる名前を使用できません。別の名前を入力してください。", name);
                return rename(name2, entry);
            }else{
                ImageEntry newEntry = entry.copy();
                try {
                    entry.setUrl(newFile.toURI().toURL().toString());
                } catch (MalformedURLException ex) {
                    Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_ERROR, "ImageEntryのURL作成失敗", ex.getMessage());
                }
                newEntry.setFileName(name);
                newEntry.setPath(newFile.getPath());
                File oldFile = new File(nowLocation, entry.getFileName());
                oldFile.renameTo(newFile);
                return newEntry;
            }
        }
        return null;
    }
    
    private void newdir(String name) {
        if(name != null && name.length() >= 0) {
            File dir = new File(nowLocation);
            if(!dir.exists()) {
                dir.mkdirs();
            }else if((dir.exists()) && (!dir.isDirectory())) {
                return;
            }
            File[] files = dir.listFiles();
            if(files == null) return;
            for(File file : files) {
                if(file.getName().equals(name)) {
                    String name2 = JOptionPane.showInputDialog(getContext().getFrame(), "同じ名前が既に存在します。別の名前を入力してください。", name);
                    newdir(name2);
                    return;
                }
            }
            name = nowLocation + File.separator + name;
            Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_INFORMATION, name);
            File folder = new File(name);
            folder.mkdir();
            
            ImageEntry entry = new ImageEntry();
            try {
                entry.setUrl(folder.toURI().toURL().toString());
            } catch (MalformedURLException ex) {
                Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_ERROR, "ImageEntryのURL作成失敗", ex.getMessage());
            }
            entry.setPath(folder.getPath());
            entry.setFileName(folder.getName());
            entry.setLastModified(folder.lastModified());
            entry.setImageIcon(ClientContext.getImageIconArias("icon_foldr"));
            entry.setDirectrory(true);
            ArrayList<ImageEntry> imageList = new ArrayList<ImageEntry>();
            imageList.add(entry);
            if(tableModel.getImageList() != null) {
                for(Object obj : tableModel.getImageList()) {
                    imageList.add(((ImageEntry)obj).copy());
                }
            }
            tableModel.setImageList(imageList);
            //scan(nowLocation);
        }
    }
//s.oh$
    
    private void backdir() {
        String path = getImageBase() + File.separator + getContext().getPatient().getPatientId();
        if(path.equals(nowLocation)) return;
        File dir = new File(nowLocation);
        nowLocation = dir.getParent();
        scan(nowLocation);
        view.getDirLbl().setText(createLocationText(nowLocation));
    }
    
    @Override
    protected void initComponents() {

        ResourceBundle resource = ClientContext.getBundle(this.getClass());
        final ActionMap map = getActionMap(resource);

        // TableModel
        int columnCount = columnCount();
        tableModel = new ImageTableModel(null, columnCount);

        // ImageTable
//        table = new JTable(tableModel) {
//            @Override
//            public String getToolTipText(MouseEvent e) {
//                int row = rowAtPoint(e.getPoint());
//                int col = columnAtPoint(e.getPoint());
//                ImageEntry entry = (ImageEntry) tableModel.getValueAt(row, col);
//                return (entry!=null)
//                        ? SDF.format(new Date(entry.getLastModified()))
//                        : null;
//            }
//        };
        view = new DefaultBrowserView();
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
                        openImage(entry);
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
                
//s.oh^ 2014/05/07 PDF・画像タブの改善
                JMenuItem midel = new JMenuItem("削除");
                midel.setAction(map.get("delete"));
                contextMenu.add(midel);
                
                JMenuItem mirename = new JMenuItem("名前の変更");
                mirename.setAction(map.get("rename"));
                contextMenu.add(mirename);
//s.oh$
                
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
        
//s.oh^ 2014/05/07 PDF・画像タブの改善
        view.getNewDirBtn().setAction(map.get("newdir"));
//s.oh$
        
//s.oh^ 2014/05/30 PDF・画像タブの改善
        view.getBackDirBtn().setAction(map.get("backdir"));
//s.oh$

//minagawa^ Icon Server        
        view.getDirLbl().setIcon(ClientContext.getImageIconArias("icon_info_small"));
//minagawa$        
        view.getDirLbl().setToolTipText("画像・PDFディレクトリの場所を表示してます。");
        setUI(view);
    }
}
