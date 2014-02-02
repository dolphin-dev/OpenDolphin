package open.dolphin.client;

import java.awt.Window;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.TaskMonitor;
import org.jdesktop.application.TaskService;

/**
 * チャートドキュメントのルートクラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public abstract class AbstractChartDocument implements ChartDocument {
        
    private static final String[] CHART_MENUS = {
        GUIConst.ACTION_OPEN_KARTE, GUIConst.ACTION_SAVE, GUIConst.ACTION_DELETE, GUIConst.ACTION_PRINT, GUIConst.ACTION_MODIFY_KARTE,
        GUIConst.ACTION_ASCENDING, GUIConst.ACTION_DESCENDING, GUIConst.ACTION_SHOW_MODIFIED,
        GUIConst.ACTION_INSERT_TEXT, GUIConst.ACTION_INSERT_SCHEMA, GUIConst.ACTION_INSERT_STAMP, GUIConst.ACTION_SELECT_INSURANCE,
        GUIConst.ACTION_CUT, GUIConst.ACTION_COPY, GUIConst.ACTION_PASTE, GUIConst.ACTION_UNDO, GUIConst.ACTION_REDO         
    };
    
    private Chart chartContext;
    private String title;
    private JPanel ui;
    private boolean dirty;
    
    protected Logger logger;
    protected Application app;
    protected ApplicationContext appCtx;
    protected TaskMonitor taskMonitor;
    protected TaskService taskService;
    
    /** Creates new DefaultChartDocument */
    public AbstractChartDocument() {
        setUI(new JPanel());
        logger = ClientContext.getBootLogger();
        appCtx = ClientContext.getApplicationContext();
        app = appCtx.getApplication();
        taskMonitor = appCtx.getTaskMonitor();
        taskService = appCtx.getTaskService();
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public Chart getContext() {
        return chartContext;
    }
    
    public void setContext(Chart chart) {
        this.chartContext = chart;
    }
    
    public abstract void start();
    
    public abstract void stop();
    
    public void enter() {
        chartContext.getStatusPanel().setMessage("");
        getContext().getChartMediator().addChain(this);
        disableMenus();
        getContext().enabledAction(GUIConst.ACTION_NEW_KARTE, true);
        getContext().enabledAction(GUIConst.ACTION_NEW_DOCUMENT, true);
    }
    
    public JPanel getUI() {
        return ui;
    }
    
    public void setUI(JPanel ui) {
        this.ui = ui;
    }
    
    public void save() {}
    
    public void print() {}
    
    public boolean isDirty() {
        return dirty;
    }
    
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
    
    public boolean isReadOnly() {
        return chartContext.isReadOnly();
    }
    
    public void disableMenus() {
        // このウインドウに関連する全てのメニューをdisableにする
        ChartMediator mediator = getContext().getChartMediator();
        mediator.disableMenus(CHART_MENUS);
    }
    
    /**
     * 共通の警告表示を行う。
     * @param message
     */
    protected void warning(String title, String message) {
        Window parent = SwingUtilities.getWindowAncestor(getUI());
        JOptionPane.showMessageDialog(parent, message, ClientContext.getFrameTitle(title), JOptionPane.WARNING_MESSAGE);
    }
}