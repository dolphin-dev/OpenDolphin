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
public class TFSBrowser extends AbstractBrowser {

    private static final String TITLE = "TFS";
    private static final String OPEN_LIST_URL = "http://:tfsServer/01Link/start.aspx?UserID=:userid&password=:password&ApplicationID=1600&RedirectURL=PatID%3d:pid";
    private static final String OPEN_CALENDAR_URL = "http://:tfsServer/01Link/Start.aspx?UserID=:userid&Password=:password&ApplicationID=3800&RedirectURL=PatientID%3d:pid%26PatientChange%3dno";
    private static final String CLOSE_TFS_URL = "http://:tfsServer/01Link/minimizeDV.aspx";
    private static final String PARAM_TFS_SERVER = ":tfsServer";
    private static final String PARAM_USER_ID = ":userid";
    private static final String PARAM_PASSWORD = ":password";
    private static final String PARAM_PID = ":pid";

    private static final String PROP_TFS_SERVER = "tfsServer";
    private static final String SETTING_FILE_NAME = "tfs.properties";

    private int imageSize = MAX_IMAGE_SIZE;
    private int cellWidth = MAX_IMAGE_SIZE + CELL_WIDTH_MARGIN;
    private int cellHeight = MAX_IMAGE_SIZE + CELL_HEIGHT_MARGIN;

    private ImageTableRenderer imageRenderer;
    private TFSBrowserView view;

    
    public TFSBrowser() {
        
        setTitle(TITLE);

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
    
    // 患者IDのパッディングゼロを除く ex. 001 -> 1
    private String getPidWithoutPadding() {
        String pid = getContext().getPatient().getPatientId();
        int len = pid.length();
        for (int i = 0; i < len; i++) {
            if (pid.charAt(i)!='0') {
                pid = pid.substring(i);
                break;
            }
        }
        
        // 全てゼロのケース ex. 0000 -> 0 ?
        return pid.startsWith("0") ? "0" : pid;
    }

    public void openListView() {

        // Genesys Server IP Address
        String tfserver = properties.getProperty(PROP_TFS_SERVER);

        if (valueIsNullOrEmpty(tfserver)) {
            return;
        }
        
        String url;
        
        // URL from custom
        url = Project.getString("tfs.url.list");
        if (valueIsNotNullNorEmpty(url)) {
            url = url.replaceFirst(PARAM_TFS_SERVER, tfserver);
            //url = url.replaceFirst(PARAM_PID, getContext().getPatient().getPatientId());
            url = url.replaceFirst(PARAM_PID, getPidWithoutPadding());
        } else {
            url = OPEN_LIST_URL;
            url = url.replaceFirst(PARAM_TFS_SERVER, tfserver);
            //下記は取り決め ----------------------------------------------------------
            url = url.replaceFirst(PARAM_USER_ID, "dolphin");
            url = url.replaceFirst(PARAM_PASSWORD, "tfs");
            //----------------------------------------------------------------------
            //url = url.replaceFirst(PARAM_PID, getContext().getPatient().getPatientId());
            url = url.replaceFirst(PARAM_PID, getPidWithoutPadding());
        }
       
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

    private void openCalendarView() {

        // TFS Server IP Address
        String tfserver = properties.getProperty(PROP_TFS_SERVER);

        if (valueIsNullOrEmpty(tfserver)) {
            return;
        }

        String url;
        
        // URL from custom
        url = Project.getString("tfs.url.calendar");
        if (valueIsNotNullNorEmpty(url)) {
            url = url.replaceFirst(PARAM_TFS_SERVER, tfserver);
            url = url.replaceFirst(PARAM_PID, getPidWithoutPadding());
        } else {
            url = OPEN_CALENDAR_URL;
            url = url.replaceFirst(PARAM_TFS_SERVER, tfserver);
            //下記は取り決め ----------------------------------------------------------
            url = url.replaceFirst(PARAM_USER_ID, "dolphin");
            url = url.replaceFirst(PARAM_PASSWORD, "tfs");
            //----------------------------------------------------------------------
            url = url.replaceFirst(PARAM_PID, getPidWithoutPadding());
        }

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
    
    private void closeTFSView() {
        // TFS Server IP Address
        String tfserver = properties.getProperty(PROP_TFS_SERVER);

        if (valueIsNullOrEmpty(tfserver)) {
            return;
        }

        String url;
        
        // URL from custom
        url = Project.getString("tfs.url.close");
        if (valueIsNotNullNorEmpty(url)) {
            url = url.replaceFirst(PARAM_TFS_SERVER, tfserver);
        } else {
            url = CLOSE_TFS_URL;
            url = url.replaceFirst(PARAM_TFS_SERVER, tfserver);
        }
        
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

    private ActionMap getActionMap(ResourceBundle resource) {

        ActionMap ret = new ActionMap();

        // 更新
        String text = resource.getString("refresh.Action.text");
 //minagawa^ Icon Server       
        //ImageIcon icon = ClientContext.getImageIcon("ref_16.gif");
        ImageIcon icon = ClientContext.getImageIconArias("icon_refresh_small");
 //minagawa$ 
        AbstractAction refresh = new AbstractAction(text, icon) {

            @Override
            public void actionPerformed(ActionEvent ae) {
                scan(getImgLocation());
            }
        };
        ret.put("refresh", refresh);

        // 設定
        text = resource.getString("doSetting.Action.text");
  //minagawa^ Icon Server        
        //icon = ClientContext.getImageIcon("confg_16.gif");
        icon = ClientContext.getImageIconArias("icon_setting_small");
//minagawa$ 
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
                TFSSetting setting = new TFSSetting(TFSBrowser.this, getUI());
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

                // TFS List & TFS Calendar ボタンの enabled
                boolean canLaunch = true;   //ClientContext.isWin();
                canLaunch = canLaunch && (valueIsNotNullNorEmpty(properties.getProperty("tfsServer")));
                view.getTfsListBtn().setEnabled(canLaunch);
                view.getTfsCalendarBtn().setEnabled(canLaunch);
                view.getTfsCloseBtn().setEnabled(canLaunch);

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
                }
            }
        };
        ret.put("doSetting", doSetting);

        // TFS List
//minagawa^ Icon Server        
        //icon = ClientContext.getImageIcon("web_16.gif");
        icon = ClientContext.getImageIconArias("icon_world_small");
//minagawa$        
        text = resource.getString("openListView.Action.text");

         AbstractAction listView = new AbstractAction(text, icon) {

            @Override
            public void actionPerformed(ActionEvent ae) {
                openListView();
            }
         };
         ret.put("openListView", listView);

         // TFS Calendar
//minagawa^ Icon Server        
        //icon = ClientContext.getImageIcon("web_16.gif");
        icon = ClientContext.getImageIconArias("icon_world_small");
//minagawa$  
        text = resource.getString("openCalendarView.Action.text");

         AbstractAction calView = new AbstractAction(text, icon) {

            @Override
            public void actionPerformed(ActionEvent ae) {
                openCalendarView();
            }
         };
         ret.put("openCalendarView", calView);
         
         // TFS Close
//minagawa^ Icon Server         
        //icon = ClientContext.getImageIcon("close_16.gif");
        icon = ClientContext.getImageIconArias("icon_close_small");
//minagawa$        
        text = resource.getString("closeTFSView.Action.text");

         AbstractAction closeView = new AbstractAction(text, icon) {

            @Override
            public void actionPerformed(ActionEvent ae) {
                closeTFSView();
            }
         };
         ret.put("closeTFSView", closeView);

        return ret;
    }
    
    @Override
    protected void initComponents() {
        
        ResourceBundle resource = ClientContext.getBundle(this.getClass());
        ActionMap map = getActionMap(resource);

        int columnCount = columnCount();
        tableModel = new ImageTableModel(null, columnCount);

        view = new TFSBrowserView();
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

        // 設定ボタン
        view.getSettingBtn().setAction(map.get("doSetting"));

        // 更新ボタン
        view.getRefreshBtn().setAction(map.get("refresh"));
        boolean enabled = true;
        enabled = enabled && valueIsNotNullNorEmpty(properties.getProperty(PROP_BASE_DIR));
        view.getRefreshBtn().setEnabled(enabled);

        // TFS Listボタン
        view.getTfsListBtn().setAction(map.get("openListView"));

        // TFS Calendarボタン
        view.getTfsCalendarBtn().setAction(map.get("openCalendarView"));
        
        // TFS Closeボタン
        view.getTfsCloseBtn().setAction(map.get("closeTFSView"));

        // enabled
        boolean canLaunch = true;   //ClientContext.isWin();
        canLaunch = canLaunch && (valueIsNotNullNorEmpty(properties.getProperty(PROP_TFS_SERVER)));
        view.getTfsListBtn().setEnabled(canLaunch);
        view.getTfsCalendarBtn().setEnabled(canLaunch);
        view.getTfsCloseBtn().setEnabled(canLaunch);

 //minagawa^ Icon Server        
        view.getDirLbl().setIcon(ClientContext.getImageIconArias("icon_info_small"));
//minagawa$         
        setUI(view);
    }
}
