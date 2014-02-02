/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.utilities.control;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.*;

/**
 * テーブルの拡張クラス
 * @author S.Oh@Life Sciences Computing Corporation.
 */
public class TableEx extends TableEvent {
    public static final int CELL_HEIGHT = 25;
    
    private JScrollPane pane;
    private JTableHeader oldTableHeader;
    private boolean cellToolTipText;
    private boolean cellToolTipIcon;
    
    /**
     * コンストラクタ
     * @param colNames カラム名
     * @param colWidths カラム幅
     * @param cellEdit  セル編集の有無
     */
    public TableEx(String[] colNames, int[] colWidths, boolean cellEdit) {
        super();
        cellToolTipText = false;
        cellToolTipIcon = false;
        
        DefaultTableModel tableModel;
        if(cellEdit) {
            tableModel = new DefaultTableModelEx(colNames, 0);
        }else{
            tableModel = new DefaultTableModelEx(colNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;   // セルの修正を禁止
                }
            };
        }
        setModel(tableModel);

        pane = new JScrollPane(this, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        // 背景を白く塗る
        this.setBackColor(Color.WHITE);
        
        if(colWidths != null) {
            DefaultTableColumnModel model = (DefaultTableColumnModel)this.getColumnModel();
            TableColumn column;
            for(int i = 0; i < model.getColumnCount(); i++) {
                column = model.getColumn(i);
                if(i > colWidths.length) break;
                column.setPreferredWidth(colWidths[i]);
                column.setMinWidth(colWidths[i]);
            }
        }

        setDefaultSetting();
    }
    
    /**
     * デフォルト設定の設定
     */
    private void setDefaultSetting() {
        // キーボードイベントからのフォーカスセット
        this.setSurrendersFocusOnKeystroke(true);
        this.setAutoResize(false);
        this.setCellHeight(CELL_HEIGHT);
        this.setRenderer();
        this.setTableCellColor(null, null, null, null, null, null, null);
        this.setTableCellMode(false);
        this.setColumnReorder(false);
        this.setSelectAllowed(true);
        this.setSort(false);
        this.setSingleSelect(true);
        this.setCellLines(true);
        this.setShowVerticalLines(false);
        this.setCellLine(false, false);
    }
    
    /**
     * JScrollPaneの取得
     * @return スクロールペイン
     */
    public JScrollPane getPane() {
        return pane;
    }
    
    /**
     * 自動リサイズの設定(横スクロール)
     * @param resize リサイズの有無
     */
    public void setAutoResize(boolean resize) {
        if(resize) this.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        else this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    }
    
    /**
     * 行の高さの設定
     * @param height 高さ
     */
    public void setCellHeight(int height) {
        this.setRowHeight(height);
    }
    
    /**
     * レンダリングの設定
     */
    public void setRenderer() {
        this.setDefaultRenderer(Object.class, new DefaultTableCellRendererEx(this));
    }

    /**
     * テーブルのセルのレンダリング色の設定
     * @param cell1 奇数行の背景色
     * @param cell2 偶数行の背景色
     * @param cellFg セルの文字色
     * @param fcsBg フォーカス行の背景色
     * @param fcsFg フォーカス行の文字色
     * @param selBg 選択セルの背景色
     * @param selFg 選択セルの文字色
     */
    public void setTableCellColor(Color cell1, Color cell2, Color cellFg, Color fcsBg, Color fcsFg, Color selBg, Color selFg) {
        DefaultTableCellRendererEx renderer = (DefaultTableCellRendererEx)this.getDefaultRenderer(Object.class);
        renderer.setTableCellColor(cell1, cell2, cellFg, fcsBg, fcsFg, selBg, selFg);
    }
    
    /**
     * テーブルのモードの設定
     * @param img 画像の有無
     */
    public void setTableCellMode(boolean img) {
        DefaultTableCellRendererEx renderer = (DefaultTableCellRendererEx)this.getDefaultRenderer(Object.class);
        renderer.setTableCellMode(img);
    }
    
    /**
     * 列の入れ替えの有無設定
     * @param reorder 入れ替えの有無
     */
    public void setColumnReorder(boolean reorder) {
        this.getTableHeader().setReorderingAllowed(reorder);
    }
    
    /**
     * 一行選択の有無設定
     * @param allowed 一行選択の有無
     */
    public void setSelectAllowed(boolean allowed) {
        this.setRowSelectionAllowed(allowed);
    }
    
    /**
     * ソートの有無設定
     * @param sort ソートの有無
     */
    public void setSort(boolean sort) {
        this.setAutoCreateRowSorter(sort);
    }

    /**
     * 一行の選択の有無設定
     * @param single 一行の選択
     */
    public void setSingleSelect(boolean single) {
        if(single) this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        else this.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    }
    
    /**
     * セルの線の描画の有無設定
     * @param line 線描画の有無
     */
    public void setCellLines(boolean line) {
        this.setShowHorizontalLines(line);
        this.setShowVerticalLines(line);
    }

    /**
     * データ(行)の追加
     * @param data データ
     */
    public void addData(Object[] data) {
        DefaultTableModel model = (DefaultTableModel)this.getModel();
        model.addRow(data);
    }
    
    /**
     * カラムの非表示の有無設定
     * @param hide 表示/非表示
     */
    public void setHideColumn(boolean hide) {
        if(hide) {
            oldTableHeader = this.getTableHeader();
            this.setTableHeader(null);
            //getTableHeader().setVisible(false);
        }else if(oldTableHeader != null) {
            this.setTableHeader(oldTableHeader);
        }
    }
    
    /**
     * 枠線の描画の設定
     * @param vertical 垂直線
     * @param horizontal 水平線
     */
    public void setCellLine(boolean vertical, boolean horizontal) {
        this.setShowVerticalLines(vertical);
        this.setShowHorizontalLines(horizontal);
    }
    
    /**
     * データの設定
     * @param val データ
     * @param row 縦
     * @param col 横
     */
    public void setData(Object val, int row, int col) {
        DefaultTableModel model = (DefaultTableModel)this.getModel();
        model.setValueAt(val, row, col);
    }
    
    /**
     * スクロール幅の設定
     * @param increment スクロール幅
     */
    public void setScrollIncrement(int increment) {
        getPane().getVerticalScrollBar().setUnitIncrement(increment);
    }
    
    /**
     * ツールチップのセ設定
     * @param tooltipText ツールチップ(テキスト)
     */
    public void setToolTipText(boolean tooltipText) {
        cellToolTipText = tooltipText;
    }
    
    /**
     * ツールチップの設定(アイコン含む)
     * @param tooltipIcon ツールチップ(アイコン)
     */
    public void setToolTipIcon(boolean tooltipIcon) {
        cellToolTipIcon = tooltipIcon;
    }
    
    /**
     * 背景色の設定
     * @param color 色
     */
    public void setBackColor(Color color) {
        getPane().getViewport().setBackground(color);
    }
    
    /**
     * カラムのリセット
     * @param colNum カラム数
     * @param colWidths カラム幅
     */
    public void resetColumnCount(int colNum, int[] colWidths) {
        DefaultTableModel model = (DefaultTableModel)this.getModel();
        model.setRowCount(0);
        model.setColumnCount(colNum);
        DefaultTableColumnModel colModel = (DefaultTableColumnModel)this.getColumnModel();
        TableColumn column;
        for(int i = 0; i < colModel.getColumnCount(); i++) {
            column = colModel.getColumn(i);
            if(i >= colWidths.length) break;
            column.setPreferredWidth(colWidths[i]);
            column.setMinWidth(colWidths[i]);
        }
    }
    
    /**
     * データのクリア
     */
    public void clearAllData() {
        DefaultTableModel model = (DefaultTableModel)this.getModel();
        model.setRowCount(0);
    }
    
    /**
     * ツールチップの作成
     * @return ツールチップ
     */
    @Override
    public JToolTip createToolTip() {
        if(cellToolTipIcon) {
            int row = getSelectedRow();
            int col = getSelectedColumn();
            if(row >= 0 && col >= 0) {
                ImageIconEx icon = (ImageIconEx)this.getModel().getValueAt(row, col);
                ToolTipEx tool = new ToolTipEx(Color.BLACK, Color.WHITE);
                return tool.CreateImageToolTip(icon.getIcon());
            }else{
                return null;
            }
        }else if(cellToolTipText) {
            return new ToolTipEx(Color.BLACK, Color.WHITE);
        }
        return null;
    }

    /**
     * 表示するツールチップの文字列を返す
     * @param e マウスイベント
     * @return 文字列
     */
    @Override
    public String getToolTipText(MouseEvent e) {
        String ret = null;
        if(cellToolTipText) {
            Object obj = this.getModel().getValueAt(rowAtPoint(e.getPoint()), columnAtPoint(e.getPoint()));
            if(obj instanceof ImageIconEx) {
                ImageIconEx icon = (ImageIconEx)this.getModel().getValueAt(rowAtPoint(e.getPoint()), columnAtPoint(e.getPoint()));
                ret = icon.getText();
            }else{
                ret = (String)this.getModel().getValueAt(rowAtPoint(e.getPoint()), columnAtPoint(e.getPoint()));
                if(ret.isEmpty()) return null;
            }
        }else if(cellToolTipIcon) {
            ret = "";
        }
        return  ret;
    }
    
    public static void main( String[] args ) {
        //TableEx list = new TableEx(new String[] = {"column1","column2"}, new int[] = {100,100}, false);
        //TableEx icon = new TableEx(null, null, false);
    }
}

/**
 * JTableのイベント処理
 * @author S.Oh@Life Sciences Computing Corporation.
 */
class TableEvent extends JTable {
    protected Object parent;
    protected Method mouseLSingleClickMethod;
    protected Method mouseLDoubleClickMethod;
    protected Method mouseLUpMethod;
    protected Method mouseLDownMethod;
    protected Method mouseRSingleClickMethod;
    protected Method mouseRDoubleClickMethod;
    protected Method mouseRUpMethod;
    protected Method mouseRDownMethod;
    protected Method keyUpMethod;
    protected Method keyDownMethod;
    
    /**
     * コンストラクタ
     */
    public TableEvent() {
        super();
        parent = null;
        mouseLSingleClickMethod = null;
        mouseLDoubleClickMethod = null;
        mouseLUpMethod = null;
        mouseLDownMethod = null;
        mouseRSingleClickMethod = null;
        mouseRDoubleClickMethod = null;
        mouseRUpMethod = null;
        mouseRDownMethod = null;
        keyUpMethod = null;
        keyDownMethod = null;
        
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = getSelectedRow();
                int col = getSelectedColumn();
                if(e.getClickCount() == 1) {
                    if(row != -1 && col != -1) {
                        if(javax.swing.SwingUtilities.isLeftMouseButton(e)) {
                            mouseLSingleClick(row, col, e);
                        }else if(javax.swing.SwingUtilities.isRightMouseButton(e)) {
                            mouseRSingleClick(row, col, e);
                        }
                    }
                }else if(e.getClickCount() == 2) {
                    if(row != -1 && col != -1) {
                        if(javax.swing.SwingUtilities.isLeftMouseButton(e)) {
                            mouseLDoubleClick(row, col, e);
                        }else if(javax.swing.SwingUtilities.isRightMouseButton(e)) {
                            mouseRDoubleClick(row, col, e);
                        }
                    }
                }
            }
            @Override
            public void mousePressed(MouseEvent e) {
                int row = getSelectedRow();
                int col = getSelectedColumn();
                if(row != -1 && col != -1) {
                    if(javax.swing.SwingUtilities.isLeftMouseButton(e)) {
                        mouseLDown(row, col, e);
                    }else if(javax.swing.SwingUtilities.isRightMouseButton(e)) {
                        mouseRDown(row, col, e);
                    }
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                int row = getSelectedRow();
                int col = getSelectedColumn();
                if(row != -1 && col != -1) {
                    if(javax.swing.SwingUtilities.isLeftMouseButton(e)) {
                        mouseLUp(row, col, e);
                    }else if(javax.swing.SwingUtilities.isRightMouseButton(e)) {
                        mouseRUp(row, col, e);
                    }
                }
            }
        });
        
        this.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                char key = e.getKeyChar();
            }
            @Override
            public void keyPressed(KeyEvent e) {
                int row = getSelectedRow();
                int col = getSelectedColumn();
                keyDown(row, col, e);
            }
            @Override
            public void keyReleased(KeyEvent e) {
                int row = getSelectedRow();
                int col = getSelectedColumn();
                keyUp(row, col, e);
            }
        });
    }
    
    /**
     * シングルクリック
     * @param row 列
     * @param col 行
     * @param e マウスイベント
     */
    public void mouseLSingleClick(int row, int col, MouseEvent e) {
        if(mouseLSingleClickMethod != null) {
            try {
                mouseLSingleClickMethod.invoke(parent, row, col);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(TableEx.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(TableEx.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(TableEx.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * ダブルクリック
     * @param row 列
     * @param col 行
     * @param e マウスイベント
     */
    public void mouseLDoubleClick(int row, int col, MouseEvent e) {
        if(mouseLDoubleClickMethod != null) {
            try {
                mouseLDoubleClickMethod.invoke(parent, row, col, e);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(TableEx.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(TableEx.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(TableEx.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            DefaultTableModel model = (DefaultTableModel)this.getModel();
            DefaultTableCellRendererEx renderer = (DefaultTableCellRendererEx)this.getDefaultRenderer(Object.class);
            if(renderer.isImgMode()) {
                ImageIconEx icon = (ImageIconEx)model.getValueAt(row, col);
            }
        }
    }
    
    /**
     * マウスダウン
     * @param row 列
     * @param col 行
     * @param e マウスイベント
     */
    public void mouseLDown(int row, int col, MouseEvent e) {
        if(mouseLDownMethod != null) {
            try {
                mouseLDownMethod.invoke(parent, row, col, e);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(TableEx.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(TableEx.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(TableEx.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * マウスアップ
     * @param row 列
     * @param col 行
     * @param e マウスイベント
     */
    public void mouseLUp(int row, int col, MouseEvent e) {
        if(mouseLUpMethod != null) {
            try {
                mouseLUpMethod.invoke(parent, row, col, e);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(TableEx.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(TableEx.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(TableEx.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * シングルクリック
     * @param row 列
     * @param col 行
     * @param e マウスイベント
     */
    public void mouseRSingleClick(int row, int col, MouseEvent e) {
        if(mouseRSingleClickMethod != null) {
            try {
                mouseRSingleClickMethod.invoke(parent, row, col);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(TableEx.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(TableEx.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(TableEx.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * ダブルクリック
     * @param row 列
     * @param col 行
     * @param e マウスイベント
     */
    public void mouseRDoubleClick(int row, int col, MouseEvent e) {
        if(mouseRDoubleClickMethod != null) {
            try {
                mouseRDoubleClickMethod.invoke(parent, row, col, e);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(TableEx.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(TableEx.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(TableEx.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            DefaultTableModel model = (DefaultTableModel)this.getModel();
            DefaultTableCellRendererEx renderer = (DefaultTableCellRendererEx)this.getDefaultRenderer(Object.class);
            if(renderer.isImgMode()) {
                ImageIconEx icon = (ImageIconEx)model.getValueAt(row, col);
            }
        }
    }
    
    /**
     * マウスダウン
     * @param row 列
     * @param col 行
     * @param e マウスイベント
     */
    public void mouseRDown(int row, int col, MouseEvent e) {
        if(mouseRDownMethod != null) {
            try {
                mouseRDownMethod.invoke(parent, row, col, e);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(TableEx.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(TableEx.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(TableEx.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * マウスアップ
     * @param row 列
     * @param col 行
     * @param e マウスイベント
     */
    public void mouseRUp(int row, int col, MouseEvent e) {
        if(mouseRUpMethod != null) {
            try {
                mouseRUpMethod.invoke(parent, row, col, e);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(TableEx.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(TableEx.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(TableEx.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * キーアップ
     * @param row 列
     * @param col 行
     * @param e マウスイベント
     */
    public void keyUp(int row, int col, KeyEvent e) {
        if(keyUpMethod != null) {
            try {
                keyUpMethod.invoke(parent, row, col, e);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(TableEx.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(TableEx.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(TableEx.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * キーダウン
     * @param row 列
     * @param col 行
     * @param e マウスイベント
     */
    public void keyDown(int row, int col, KeyEvent e) {
        if(keyDownMethod != null) {
            try {
                keyDownMethod.invoke(parent, row, col, e);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(TableEx.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(TableEx.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(TableEx.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * シングルクリックの呼び出す関数の設定
     * @param obj オブジェクト
     * @param methodName メソッド名
     */
    public void setLMouseSingleClickMethod(Object obj, String methodName) throws NoSuchMethodException {
        parent = obj;
        mouseLSingleClickMethod = parent.getClass().getMethod(methodName, int.class, int.class, MouseEvent.class);
    }
    
    /**
     * ダブルクリックの呼び出す関数の設定
     * @param obj オブジェクト
     * @param methodName メソッド名
     */
    public void setLMouseDoubleClickMethod(Object obj, String methodName) throws NoSuchMethodException {
        parent = obj;
        mouseLDoubleClickMethod = parent.getClass().getMethod(methodName, int.class, int.class, MouseEvent.class);
    }
    
    /**
     * マウスダウンの呼び出す関数の設定
     * @param obj オブジェクト
     * @param methodName メソッド名
     */
    public void setLMouseDownMethod(Object obj, String methodName) throws NoSuchMethodException {
        parent = obj;
        mouseLDownMethod = parent.getClass().getMethod(methodName, int.class, int.class, MouseEvent.class);
    }
    
    /**
     * マウスアップの呼び出す関数の設定
     * @param obj オブジェクト
     * @param methodName メソッド名
     */
    public void setLMouseUpMethod(Object obj, String methodName) throws NoSuchMethodException {
        parent = obj;
        mouseLUpMethod = parent.getClass().getMethod(methodName, int.class, int.class, MouseEvent.class);
    }
    
    /**
     * シングルクリックの呼び出す関数の設定
     * @param obj オブジェクト
     * @param methodName メソッド名
     */
    public void setRMouseSingleClickMethod(Object obj, String methodName) throws NoSuchMethodException {
        parent = obj;
        mouseRSingleClickMethod = parent.getClass().getMethod(methodName, int.class, int.class, MouseEvent.class);
    }
    
    /**
     * ダブルクリックの呼び出す関数の設定
     * @param obj オブジェクト
     * @param methodName メソッド名
     */
    public void setRMouseDoubleClickMethod(Object obj, String methodName) throws NoSuchMethodException {
        parent = obj;
        mouseRDoubleClickMethod = parent.getClass().getMethod(methodName, int.class, int.class, MouseEvent.class);
    }
    
    /**
     * マウスダウンの呼び出す関数の設定
     * @param obj オブジェクト
     * @param methodName メソッド名
     */
    public void setRMouseDownMethod(Object obj, String methodName) throws NoSuchMethodException {
        parent = obj;
        mouseRDownMethod = parent.getClass().getMethod(methodName, int.class, int.class, MouseEvent.class);
    }
    
    /**
     * マウスアップの呼び出す関数の設定
     * @param obj オブジェクト
     * @param methodName メソッド名
     */
    public void setRMouseUpMethod(Object obj, String methodName) throws NoSuchMethodException {
        parent = obj;
        mouseRUpMethod = parent.getClass().getMethod(methodName, int.class, int.class, MouseEvent.class);
    }
    
    /**
     * キーダウンの呼び出す関数の設定
     * @param obj オブジェクト
     * @param methodName メソッド名
     */
    public void setKeyDownMethod(Object obj, String methodName) throws NoSuchMethodException {
        parent = obj;
        keyDownMethod = parent.getClass().getMethod(methodName, int.class, int.class, KeyEvent.class);
    }
    
    /**
     * キーアップの呼び出す関数の設定
     * @param obj オブジェクト
     * @param methodName メソッド名
     */
    public void setKeyUpMethod(Object obj, String methodName) throws NoSuchMethodException {
        parent = obj;
        keyUpMethod = parent.getClass().getMethod(methodName, int.class, int.class, KeyEvent.class);
    }
}

/**
 * 画像をセル内に表示するために継承
 * @author S.Oh@Life Sciences Computing Corporation.
 */
class DefaultTableModelEx extends DefaultTableModel {
    /**
     * コンストラクタ
     * @param columnNames カラム名
     * @param rowNum 行数
     */
    DefaultTableModelEx(String[] columnNames, int rowNum){
        super(columnNames, rowNum);
    }

    /**
     * コンストラクタ
     * @param rowCount 行数
     * @param columnCount カラム数
     */
    DefaultTableModelEx(int rowCount, int columnCount){
        super(rowCount, columnCount);
    }

    /**
     * カラムのクラスの取得
     * @param col 列
     * @return クラス
     */
    @Override
    public Class getColumnClass(int col){
        return getValueAt(0, col).getClass();
    }
}

/**
 * テーブルのレンダリングクラス
 * @author S.Oh@Life Sciences Computing Corporation.
 */
class DefaultTableCellRendererEx extends DefaultTableCellRenderer {
    private Color cellColor1;
    private Color cellColor2;
    private Color cellFgColor;
    private Color fcsBgColor;
    private Color fcsFgColor;
    private Color selBgColor;
    private Color selFgColor;
    private boolean modeImg;

    /** Creates new IconRenderer */
    /**
     * コンストラクタ
     * @param table テーブル
     */
    public DefaultTableCellRendererEx(TableEx table) {
        super();
        setTableCellColor(null, null, null, null, null, null, null);
        setTableCellMode(false);
    }
    
    /**
     * セルの色のセット
     * @param cell1 奇数行の背景色
     * @param cell2 偶数行の背景色
     * @param cellFg セルの文字色
     * @param fcsBg フォーカス行の背景色
     * @param fcsFg フォーカス行の文字色
     * @param selBg 選択セルの背景色
     * @param selFg 選択セルの文字色
     */
    public void setTableCellColor(Color cell1, Color cell2, Color cellFg, Color fcsBg, Color fcsFg, Color selBg, Color selFg) {
        cellColor1 = cell1;
        cellColor2 = cell2;
        cellFgColor = cellFg;
        fcsBgColor = fcsBg;
        fcsFgColor = fcsFg;
        selBgColor = selBg;
        selFgColor = selFg;
    }
    
    /**
     * セルのモードの設定
     * @param img 画像の有無
     */
    public void setTableCellMode(boolean img) {
        modeImg = img;
        if(img) {
            setHorizontalAlignment(SwingConstants.LEFT);
            setVerticalAlignment(SwingConstants.CENTER);
            setVerticalTextPosition(JLabel.TOP);
            //setHorizontalTextPosition(JLabel.CENTER);
        }
    }
    
    /**
     * 画像表示モードかどうかの取得
     * @return 画像モードの結果
     */
    public boolean isImgMode() {
        return modeImg;
    }

    /**
     * getTableCellRendererComponentのオーバーライド
     * @param table テーブル
     * @param value オブジェクト
     * @param isSelected 選択の有無
     * @param isFocused フォーカスの有無
     * @param row 列
     * @param col 行
     * @return コンポーネント
     */
    @Override
    public Component getTableCellRendererComponent(JTable table,
            Object value,
            boolean isSelected,
            boolean isFocused,
            int row, int col) {
        if(value instanceof ImageIcon || value instanceof ImageIconEx || modeImg) {
            return getImageRendererComponent(table, value, isSelected, isFocused, row, col);
        }
        return getColorRendererComponent(table, value, isSelected, isFocused, row, col);
    }
    
    /**
     * 色のレンダリング
     * @param table テーブル
     * @param value オブジェクト
     * @param isSelected 選択の有無
     * @param isFocused フォーカスの有無
     * @param row 列
     * @param col 行
     * @return コンポーネント
     */
    private Component getColorRendererComponent(JTable table, Object value, boolean isSelected, boolean isFocused, int row, int col) {
        if(isFocused) {
            // フォーカスのセルのカラー
            if(fcsBgColor == null) setBackground(table.getSelectionBackground());
            else setBackground(fcsBgColor);
            if(fcsFgColor == null) setForeground(table.getSelectionForeground());
            else setForeground(fcsFgColor);
        }else if(isSelected) {
            // 選択されている行のカラー
            if(selBgColor == null) setBackground(table.getSelectionBackground());
            else setBackground(selBgColor);
            if(selFgColor == null) setForeground(table.getSelectionForeground());
            else setForeground(selFgColor);
        }else{
            if((row & (1)) == 0) {
                setBackground(cellColor1);
            }else{
                setBackground(cellColor2);
            }
            if(cellFgColor == null) setForeground(table.getSelectionForeground());
            else setForeground(cellFgColor);
        }

        if(value != null) {
            if(value instanceof String) {
                this.setText((String) value);
            }else{
                this.setText(value.toString());
            }
        }else{
            this.setText("");
        }
        
        // コンポーネントの処理
        if(value instanceof JComponent){
            return (JComponent)value;
        }

        return this;
    }
    
    /**
     * 画像のレンダリング
     * @param table テーブル
     * @param value オブジェクト
     * @param isSelected 選択の有無
     * @param isFocused フォーカスの有無
     * @param row 列
     * @param col 行
     * @return コンポーネント
     */
    private Component getImageRendererComponent(JTable table, Object value, boolean isSelected, boolean isFocused, int row, int col) {
        Component compo = super.getTableCellRendererComponent(table,
                value,
                isSelected,
                isFocused,
                row, col);

        JLabel label = (JLabel)compo;
        
        if(value instanceof ImageIconEx) {
            ImageIconEx imgIcon = (ImageIconEx)value;
            label.setIcon(imgIcon.getIcon());
            label.setText(imgIcon.getText());
        }

        if(isFocused) {
            // フォーカスのセルのカラー
            if(fcsBgColor == null) setBackground(table.getSelectionBackground());
            else setBackground(fcsBgColor);
            if(fcsFgColor != null) setForeground(table.getSelectionForeground());
            else setForeground(fcsFgColor);
        }else{
            if((row & (1)) == 0) {
                setBackground(cellColor1);
            }else{
                setBackground(cellColor2);
            }
            if(cellFgColor == null) setForeground(table.getSelectionForeground());
            else setForeground(cellFgColor);
        }

        return (Component)label;
    }
}
