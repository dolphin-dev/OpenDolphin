package open.dolphin.client;

import java.awt.Window;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * チャートドキュメントのルートクラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public abstract class AbstractChartDocument implements ChartDocument {
        
    private static final String[] CHART_MENUS = {
//minagawa^ LSC Test        
        GUIConst.ACTION_NEW_KARTE,GUIConst.ACTION_NEW_DOCUMENT,
//minagawa$        
        GUIConst.ACTION_OPEN_KARTE, GUIConst.ACTION_SAVE, GUIConst.ACTION_DELETE, GUIConst.ACTION_PRINT, GUIConst.ACTION_MODIFY_KARTE,
        GUIConst.ACTION_CHANGE_NUM_OF_DATES_ALL,GUIConst.ACTION_CREATE_PRISCRIPTION,GUIConst.ACTION_SEND_CLAIM,GUIConst.ACTION_CHECK_INTERACTION,GUIConst.ACTION_ASCENDING, GUIConst.ACTION_DESCENDING, GUIConst.ACTION_SHOW_MODIFIED,
        GUIConst.ACTION_INSERT_TEXT, GUIConst.ACTION_INSERT_SCHEMA, GUIConst.ACTION_ATTACHMENT, GUIConst.ACTION_INSERT_STAMP,GUIConst.ACTION_SELECT_INSURANCE,
        GUIConst.ACTION_CUT, GUIConst.ACTION_COPY, GUIConst.ACTION_PASTE, GUIConst.ACTION_UNDO, GUIConst.ACTION_REDO
    }; 
    // GUIConst.ACTION_SEND_CLAIM 元町皮ふ科
    // GUIConst.ACTION_CREATE_PRISCRIPTION Hiro Clinic porting
    // GUIConst.ACTION_CHECK_INTERACTION Masuda-Naika
    
    private Chart chartContext;
    private String title;
    private JPanel ui;
    private boolean dirty;
    
    /** Creates new DefaultChartDocument */
    public AbstractChartDocument() {
        setUI(new JPanel());
    }
    
//minagawa^ 保存時に確認ダイアログを表示せず、saveAll した時の対応   
    protected PropertyChangeSupport boundSupport;
    private boolean chartDocDidSave;
    
    @Override
    public void addPropertyChangeListener(String prop, PropertyChangeListener l) {
        if (boundSupport==null) {
            boundSupport = new PropertyChangeSupport(this);
        }
        boundSupport.addPropertyChangeListener(prop, l);
    }
    
    @Override
    public void removePropertyChangeListener(String prop, PropertyChangeListener l) {
        if (boundSupport!=null) {
            boundSupport.removePropertyChangeListener(prop, l);
        }
    }
    
    @Override
    public boolean isChartDocDidSave() {
        return chartDocDidSave;
    }
    
    @Override
    public void setChartDocDidSave(boolean b) {
        chartDocDidSave = b;
        if (boundSupport!=null) {
            boundSupport.firePropertyChange(CHART_DOC_DID_SAVE, !chartDocDidSave, chartDocDidSave);
        }
    }
 //minagawa$
    
    @Override
    public String getTitle() {
        return title;
    }
    
    @Override
    public void setTitle(String title) {
        this.title = title;
    }
    
    @Override
    public ImageIcon getIconInfo(Chart chart) {
        return null;
    }
    
    @Override
    public Chart getContext() {
        return chartContext;
    }
    
    @Override
    public void setContext(Chart chart) {
        this.chartContext = chart;
    }
    
    @Override
    public abstract void start();
    
    @Override
    public abstract void stop();
    
    @Override
    public void enter() {
        // status パネルの表示内容をクリアする
        if (chartContext.getStatusPanel()!=null) {
            chartContext.getStatusPanel().setMessage("");
        }
        // responder chain の先頭になる
        getContext().getChartMediator().addChain(this);

        // 全てのメニューを disabled にする
        disableMenus();
//minagawa^ LSC Test 新規カルテはdisabled       
//        // 新規カルテと新規文書のActionを制御する
//        getContext().enabledAction(GUIConst.ACTION_NEW_KARTE, !isReadOnly());
        getContext().enabledAction(GUIConst.ACTION_NEW_DOCUMENT, !isReadOnly());
//minagawa$        
    }
    
    @Override
    public JPanel getUI() {
        return ui;
    }
    
    public final void setUI(JPanel ui) {
        this.ui = ui;
    }
    
    @Override
    public void save() {}
    
    @Override
    public void print() {}
    
    @Override
    public boolean isDirty() {
        return dirty;
    }
    
    @Override
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