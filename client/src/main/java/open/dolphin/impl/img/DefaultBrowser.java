package open.dolphin.impl.img;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Enumeration;
import java.util.Properties;
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
                scan(getImgLocation());
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
                }
            }
        };
        ret.put("doSetting", doSetting);

        return ret;
    }
    
    @Override
    protected void initComponents() {

        ResourceBundle resource = ClientContext.getBundle(this.getClass());
        ActionMap map = getActionMap(resource);

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

//minagawa^ Icon Server        
        view.getDirLbl().setIcon(ClientContext.getImageIconArias("icon_info_small"));
//minagawa$        
        view.getDirLbl().setToolTipText("画像・PDFディレクトリの場所を表示してます。");
        setUI(view);
    }
}
