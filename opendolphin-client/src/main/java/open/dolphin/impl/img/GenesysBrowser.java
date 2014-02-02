package open.dolphin.impl.img;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ResourceBundle;
import javax.swing.*;
import javax.swing.table.TableColumn;
import open.dolphin.client.ClientContext;
import open.dolphin.client.GUIConst;
import open.dolphin.client.ImageEntry;
import open.dolphin.project.Project;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class GenesysBrowser extends AbstractBrowser {

    private static final String TITLE = "PACS";
    private static final String GENESYS_FILE = "Genesys_";
    private static final String GENESYS_URL = "http://:genesysServer/T1Web/basis/search_Form.aspx?userid=:userid&pid=:pid";
    private static final String GENESYS_URL_WITH_SOP = "http://:genesysServer/T1Web/basis/search_Form.aspx?userid=:userid&pid=:pid&sop=:sop";
    private static final String PARAM_GENESYS_SERVER = ":genesysServer";
    private static final String PARAM_USER_ID = ":userid";
    private static final String PARAM_PID = ":pid";
    private static final String PARAM_SOP = ":sop";

    private static final String PROP_GENESYS_SERVER = "genesysServer";
    private static final String PROP_GENESYS_VIEWER = "genesysView";
    private static final String SETTING_FILE_NAME = "genesys.properties";

    private int imageSize = MAX_IMAGE_SIZE;
    private int cellWidth = MAX_IMAGE_SIZE + CELL_WIDTH_MARGIN;
    private int cellHeight = MAX_IMAGE_SIZE + CELL_HEIGHT_MARGIN;

    private ImageTableRenderer imageRenderer;
    private GenesysBrowserView view;

    
    public GenesysBrowser() {
        
        setTitle(TITLE);

//        properties = getProperties();
//
//        // Convert the old properties
//        Properties old = Project.loadPropertiesAsObject("imageBrowserProp2.xml");
//        if (old!=null) {
//            Enumeration e = old.propertyNames();
//            while (e.hasMoreElements()) {
//                String key = (String)e.nextElement();
//                String val = old.getProperty(key);
//                properties.setProperty(key, val);
//            }
//            Project.storeProperties(properties, SETTING_FILE_NAME);
//            Project.deleteSettingFile("imageBrowserProp2.xml");
//
//        } else {
//            Project.loadProperties(properties, SETTING_FILE_NAME);
//        }
//
//        // Base directory
//        String dir = properties.getProperty(PROP_BASE_DIR);
//        imageBase = valueIsNotNullNorEmpty(dir) ? dir : null;

        properties = getProperties();
        Project.loadProperties(properties, SETTING_FILE_NAME);

        String dir = properties.getProperty(PROP_BASE_DIR);
        imageBase = valueIsNotNullNorEmpty(dir) ? dir : null;
    }
    
    @Override
    protected String getImgLocation() {
        
        if (getContext() == null) {
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
        if (loc.length() > 33) {
            sb = new StringBuilder();
            sb.append(loc.substring(0, 15));
            sb.append("...");
            int pos = loc.length() - 15;
            sb.append(loc.substring(pos));
            view.getDirLbl().setText(sb.toString());

        } else {
            view.getDirLbl().setText(loc);
        }
        
        return loc;
    }

    public void viewGenesys() {

        // Genesys Server IP Address
        String genesysServer = properties.getProperty(PROP_GENESYS_SERVER);

        if (valueIsNullOrEmpty(genesysServer)) {
            return;
        }
        
        String url = GENESYS_URL;
        url = url.replaceFirst(PARAM_GENESYS_SERVER, genesysServer);
        url = url.replaceFirst(PARAM_USER_ID, Project.getUserId());
        url = url.replaceFirst(PARAM_PID, getContext().getPatient().getPatientId());

        // 既定のブラウザでオープンする
        if (desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(new URI(url));
            } catch (URISyntaxException ex) {
                ClientContext.getBootLogger().warn(ex);
            } catch (IOException ex) {
                ClientContext.getBootLogger().warn(ex);
            }
        }
    }

    private void openGenesysWithSop(String filename) {

        // Genesys Server IP Address
        String genesysServer = properties.getProperty(PROP_GENESYS_SERVER);

        if (valueIsNullOrEmpty(genesysServer)) {
            return;
        }

        int index1 = filename.lastIndexOf("_");
        int index2 = filename.lastIndexOf(".");
        String sop = filename.substring(index1+1, index2);

        String url = GENESYS_URL_WITH_SOP;
        url = url.replaceFirst(PARAM_GENESYS_SERVER, genesysServer);
        url = url.replaceFirst(PARAM_USER_ID, Project.getUserId());
        url = url.replaceFirst(PARAM_PID, getContext().getPatient().getPatientId());
        url = url.replaceFirst(PARAM_SOP, sop);

        // 既定のブラウザでオープンする
        if (desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(new URI(url));
            } catch (URISyntaxException ex) {
                ClientContext.getBootLogger().warn(ex);
            } catch (IOException ex) {
                ClientContext.getBootLogger().warn(ex);
            }
        }
    }

    @Override
    protected void openImage(ImageEntry entry) {

        // Genesys File
        String fileName = entry.getFileName();
        if (fileName.startsWith(GENESYS_FILE)) {
            openGenesysWithSop(fileName);

        } else {
            super.openImage(entry);
        }
    }

    private ActionMap getActionMap(ResourceBundle resource) {

        ActionMap ret = new ActionMap();

        String text = resource.getString("refresh.Action.text");
        ImageIcon icon = ClientContext.getImageIcon("ref_16.gif");
        AbstractAction refresh = new AbstractAction(text, icon) {

            @Override
            public void actionPerformed(ActionEvent ae) {
                scan();
            }
        };
        ret.put("refresh", refresh);

        text = resource.getString("doSetting.Action.text");
        icon = ClientContext.getImageIcon("confg_16.gif");
        AbstractAction doSetting = new AbstractAction(text, icon) {

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
                GenesysSetting setting = new GenesysSetting(GenesysBrowser.this, getUI());
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

                // Genesys ボタンの enabled
                boolean canLaunch = true;   //ClientContext.isWin();
                canLaunch = canLaunch && (valueIsNotNullNorEmpty(properties.getProperty("genesysServer")));
                view.getGenesysBtn().setEnabled(canLaunch);

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
                    scan();
                }
            }
        };
        ret.put("doSetting", doSetting);
        icon = ClientContext.getImageIcon("web_16.gif");
        text = resource.getString("viewGenesys.Action.text");

         AbstractAction viewGenesys = new AbstractAction(text, icon) {

            @Override
            public void actionPerformed(ActionEvent ae) {
                viewGenesys();
            }
         };
         ret.put("viewGenesys", viewGenesys);

        return ret;
    }
    
    @Override
    protected void initComponents() {
        
        ResourceBundle resource = ClientContext.getBundle(this.getClass());
        ActionMap map = getActionMap(resource);

        int columnCount = columnCount();
        tableModel = new ImageTableModel(null, columnCount);

        view = new GenesysBrowserView();
        table = view.getTable();
        table .setModel(tableModel);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setCellSelectionEnabled(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setDragEnabled(true);
        table.setTransferHandler(new ImageTableTransferHandler(this));

        TableColumn column;
        for (int i = 0; i < columnCount; i++) {
            column = table.getColumnModel().getColumn(i);
            column.setPreferredWidth(cellWidth);
        }
        table.setRowHeight(cellHeight);

        // Renderer
        imageRenderer = new ImageTableRenderer(this);
        imageRenderer.setImageSize(imageSize);
        table.setDefaultRenderer(java.lang.Object.class, imageRenderer);

        table.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int row = table.getSelectedRow();
                    int col = table.getSelectedColumn();
                    ImageEntry entry = getEntryAt(row, col);
                    Action copy = getContext().getChartMediator().getAction(GUIConst.ACTION_COPY);
                    copy.setEnabled((entry!=null));
                }
                else if(e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    int col = table.getSelectedColumn();
                    ImageEntry entry = getEntryAt(row, col);
                    if (entry != null) {
                        openImage(entry);
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

        view.getRefreshBtn().setAction(map.get("refresh"));
        boolean enabled = true;
        enabled = enabled && valueIsNotNullNorEmpty(properties.getProperty(PROP_BASE_DIR));
        view.getRefreshBtn().setEnabled(enabled);
        view.getGenesysBtn().setAction(map.get("viewGenesys"));
        boolean canLaunch = true;   //ClientContext.isWin();
        canLaunch = canLaunch && (valueIsNotNullNorEmpty(properties.getProperty("genesysServer")));
        view.getGenesysBtn().setEnabled(canLaunch);
        setUI(view);
    }
}
