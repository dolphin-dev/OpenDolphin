package open.dolphin.impl.img;

import java.awt.BorderLayout;
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
public class UniteaBrowser extends AbstractBrowser {

    private static final String TITLE = "Unitea";

    private static final String UNITEA_URL = "http://:uniteaServer/kimlinkscreen/kimlinkscreen.application?client=:uniteaClient&pid=:pid&oprt=open";
    private static final String PARAM_UNITEA_SERVER = ":uniteaServer";
    private static final String PARAM_UNITEA_CLIENT = ":uniteaClient";
    private static final String PARAM_PID = ":pid";

    private static final String PROP_UNITEA_SERVER = "uniteaServer";
    private static final String PROP_UNITEA_CLIENT = "uniteaClient";
    private static final String PROP_UNITEA_BROWSER = "uniteaBrowser";
    
    private static final String SETTING_FILE_NAME = "unitea.properties";

    private int imageSize = MAX_IMAGE_SIZE;
    private int cellWidth = MAX_IMAGE_SIZE + CELL_WIDTH_MARGIN;
    private int cellHeight = MAX_IMAGE_SIZE + CELL_HEIGHT_MARGIN;

    private ImageTableRenderer imageRenderer;
    private JScrollPane jScrollPane1;
    private JLabel dirLabel;
    private JButton settingBtn;
    private JButton refreshBtn;
    private JButton uniteaBtn;
    
    public UniteaBrowser() {
        
        setTitle(TITLE);

        properties = getProperties();
        Project.loadProperties(properties, SETTING_FILE_NAME);

        String dir = properties.getProperty(PROP_BASE_DIR);
        imageBase = valueIsNotNullNorEmpty(dir) ? dir : null;
    }
    
    @Override
    protected String getImgLocation() {
        
        if (getContext() == null) {
            dirLabel.setText("");
            return null;
        }
        
        if (valueIsNullOrEmpty(getImageBase())) {
            dirLabel.setText("画像ディレクトリが指定されていません。");
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
            dirLabel.setText(sb.toString());

        } else {
            dirLabel.setText(loc);
        }
        
        return loc;
    }

    private void openUnitea() {

        // Unitea Server
        String uniteaServer = properties.getProperty(PROP_UNITEA_SERVER);
        if (valueIsNullOrEmpty(uniteaServer)) {
            return;
        }

        String uniteaClient = properties.getProperty(PROP_UNITEA_CLIENT);
        if (valueIsNullOrEmpty(uniteaClient)) {
            return;
        }

        String url = UNITEA_URL;
        url = url.replaceFirst(PARAM_UNITEA_SERVER, uniteaServer);
        url = url.replaceFirst(PARAM_UNITEA_CLIENT, uniteaClient);
        url = url.replaceFirst(PARAM_PID, getContext().getPatient().getPatientId());

        // 既定のブラウザで
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

        String text = resource.getString("refresh.Action.text");
        AbstractAction refresh = new AbstractAction(text) {

            @Override
            public void actionPerformed(ActionEvent ae) {
                scan(getImgLocation());
            }
        };
        ret.put("refresh", refresh);

        text = resource.getString("doSetting.Action.text");
        AbstractAction doSetting = new AbstractAction(text) {

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
                UniteaSetting setting = new UniteaSetting(UniteaBrowser.this, getUI());
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
                refreshBtn.setEnabled(canRefresh);

                boolean canLaunch = ClientContext.isWin();
                canLaunch = canLaunch && valueIsNotNullNorEmpty(properties.getProperty(PROP_UNITEA_SERVER));
                canLaunch = canLaunch && valueIsNotNullNorEmpty(properties.getProperty(PROP_UNITEA_CLIENT));
                uniteaBtn.setEnabled(canLaunch);

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

        text = resource.getString("openUnitea.Action.text");
         AbstractAction openUnitea = new AbstractAction(text) {

            @Override
            public void actionPerformed(ActionEvent ae) {
                openUnitea();
            }
         };
         ret.put("openUnitea", openUnitea);

        return ret;
    }
    
    @Override
    protected void initComponents() {
        
        ResourceBundle resource = ClientContext.getBundle(this.getClass());
        ActionMap map = getActionMap(resource);

        int columnCount = columnCount();
        tableModel = new ImageTableModel(null, columnCount);
        
        table = new JTable(tableModel);
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
        
        settingBtn = new JButton();
        settingBtn.setAction(map.get("doSetting"));

        refreshBtn = new JButton();
        refreshBtn.setAction(map.get("refresh"));
        boolean enabled = true;
        enabled = enabled && valueIsNotNullNorEmpty(properties.getProperty(PROP_BASE_DIR));
        refreshBtn.setEnabled(enabled);

        dirLabel = new JLabel();
        JPanel north = new JPanel();
        
        uniteaBtn = new JButton();
        uniteaBtn.setAction(map.get("openUnitea"));
        boolean canLaunch = ClientContext.isWin();
        canLaunch = canLaunch && valueIsNotNullNorEmpty(properties.getProperty(PROP_UNITEA_SERVER));
        canLaunch = canLaunch && valueIsNotNullNorEmpty(properties.getProperty(PROP_UNITEA_CLIENT));
        canLaunch = canLaunch && valueIsNotNullNorEmpty(properties.getProperty(PROP_UNITEA_BROWSER));
        uniteaBtn.setEnabled(canLaunch);
        north.add(uniteaBtn);
        
        north.add(refreshBtn);
        north.add(dirLabel);
        north.add(Box.createHorizontalGlue());
        north.add(settingBtn);

        // AHO
        JPanel aho = new JPanel(new BorderLayout());
        aho.add(table, BorderLayout.CENTER);
        jScrollPane1 = new JScrollPane(aho);
        
        getUI().setLayout(new BorderLayout());
        getUI().add(north, BorderLayout.NORTH);
        getUI().add(jScrollPane1, BorderLayout.CENTER);
    }
}
