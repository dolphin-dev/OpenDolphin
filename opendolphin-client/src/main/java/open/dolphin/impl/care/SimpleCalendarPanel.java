package open.dolphin.impl.care;

import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import open.dolphin.client.*;
import open.dolphin.infomodel.AppointmentModel;
import open.dolphin.infomodel.ModelUtils;
import open.dolphin.infomodel.ModuleModel;
import open.dolphin.util.MMLDate;


/**
 * SimpleCalendarPanel
 *
 * @author Kazushi Minagawa
 */
public final class SimpleCalendarPanel extends JPanel implements DragGestureListener, DropTargetListener, DragSourceListener {
    
    private String[] columnNames = ClientContext.getStringArray("calendar.day.week");
    
    private int year;
    private int month;
    private int numRows;
    private int firstCol;
    private int lastCol;
    private GregorianCalendar firstDay;
    private GregorianCalendar lastDay;
    private GregorianCalendar today;
    private String birthday;
    
    private JTable table;
    private MedicalEvent[][] days;
    private int rowHeight = ClientContext.getInt("calendar.cell.height");
    private int columnWidth = ClientContext.getInt("calendar.cell.width");
    private int horizontalAlignment = SwingConstants.CENTER;
    private int autoResizeMode = JTable.AUTO_RESIZE_ALL_COLUMNS;
    
    // Color
    private Color sundayColor = ClientContext.getColor("color.SUNDAY_FORE");
    private Color saturdayColor = ClientContext.getColor("color.SATURDAY_FORE");
    private Color todayBackground = ClientContext.getColor("color.TODAY_BACK");
    private Color calendarBackground = ClientContext.getColor("color.CALENDAR_BACK");
    private Color weekdayColor = ClientContext.getColor("color.WEEKDAY_FORE");
    private Color birthdayColor = ClientContext.getColor("color.BIRTHDAY_BACK");
    
    // Font
    private Font outOfMonthFont = new Font("Dialog", Font.PLAIN, ClientContext.getInt("calendar.font.size.outOfMonth"));
    private Font inMonthFont = new Font("Dialog", Font.PLAIN, ClientContext.getInt("calendar.font.size"));
    
    // DnD
    private DragSource dragSource;
    private int dragRow;
    private int dragCol;
    
    private int relativeMonth;
    
    private Chart context;
    private CareMapDocument parent;
    private boolean dirty;
    
    private JPopupMenu appointMenu;
    private int popedRow;
    private int popedCol;
    
    private String markEvent = "-1";
    
    private PropertyChangeSupport boundSupport;
    
    
    /** Creates new SimpleCalendarPanel*/
    private SimpleCalendarPanel() {
        //super(new BorderLayout());
        super();
    }
    
    /** Creates new SimpleCalendarPanel*/
    private SimpleCalendarPanel(int n) {
        
        this();
        
        // 今月を基点とした相対月数
        relativeMonth = n;
        
        // Get right now
        today = new GregorianCalendar();
        today.clear(Calendar.MILLISECOND);
        today.clear(Calendar.SECOND);
        today.clear(Calendar.MINUTE);
        today.clear(Calendar.HOUR_OF_DAY);
        GregorianCalendar gc = (GregorianCalendar)today.clone();
        
        // Create requested month calendar
        // Add relative number to create
        gc.add(Calendar.MONTH, n);
        this.year = gc.get(Calendar.YEAR);
        this.month = gc.get(Calendar.MONTH);
        table = createCalendarTable(gc);
        table.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mouseClicked(MouseEvent e) {
                
                if (e.getClickCount() != 1) {
                    return;
                }
                
                Point p = e.getPoint();
                int row = table.rowAtPoint(p);
                int col = table.columnAtPoint(p);
                if (row != -1 && col != -1) {
                    MedicalEvent evt = days[row][col];
                    if (evt.getMedicalCode() != null) {
                        boundSupport.firePropertyChange(CareMapDocument.SELECTED_DATE_PROP, null, evt.getDisplayDate());
                        
                    } else if (evt.getAppointmentName() != null) {
                        boundSupport.firePropertyChange(CareMapDocument.SELECTED_APPOINT_DATE_PROP, null, evt.getDisplayDate());
                    }
                }
            }
        });

        JPanel cmd = new JPanel(new FlowLayout(FlowLayout.CENTER));
        cmd.add(new JLabel(getCalendarTitle()));

        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.add(table.getTableHeader(), BorderLayout.NORTH);
        cardPanel.add(table, BorderLayout.CENTER);

        this.setLayout(new BorderLayout());
        this.add(cmd, BorderLayout.NORTH);
        this.add(cardPanel, BorderLayout.CENTER);
        //this.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        this.setBorder(BorderFactory.createEtchedBorder());
        
        // Adjust cut & try
        Dimension dim = new Dimension(columnWidth*7 + 0, rowHeight*8 + 7);  // +10
        this.setPreferredSize(dim);
        this.setMinimumSize(dim);
        this.setMaximumSize(dim);
        
        // Embed popup menu
        appointMenu = PopupMenuFactory.create("appoint.popupMenu", SimpleCalendarPanel.this);
        
        // Table を DragTarget, 自身をリスナに設定する
        dragSource = new DragSource();
        dragSource.createDefaultDragGestureRecognizer(table, DnDConstants.ACTION_COPY_OR_MOVE, SimpleCalendarPanel.this);
        
        // Table を DropTarget, 自身をリスナに設定する
        DropTarget dt = new DropTarget(table, this);
    }
    
    public void setChartContext(Chart context) {
        this.context = context;
        birthday = context.getPatient().getBirthday().substring(5);
    }
    
    public void setParent(CareMapDocument doc) {
        parent = doc;
    }
    
    public String getCalendarTitle() {
        StringBuilder buf = new StringBuilder();
        buf.append(year);
        buf.append(ClientContext.getString("calendar.title.year"));
        buf.append(month + 1);
        buf.append(ClientContext.getString("calendar.title.month"));
        return buf.toString();
    }
    
    public int getRelativeMonth() {
        return relativeMonth;
    }
    
    public boolean isThisMonth() {
        return relativeMonth == 0 ? true : false;
    }
    
    @Override
    public void addPropertyChangeListener(String prop, PropertyChangeListener l) {
        if (boundSupport == null) {
            boundSupport = new PropertyChangeSupport(this);
        }
        boundSupport.addPropertyChangeListener(prop, l);
    }
    
    @Override
    public void removePropertyChangeListener(String propName, PropertyChangeListener l) {
        if (boundSupport == null) {
            boundSupport = new PropertyChangeSupport(this);
        }
        boundSupport.removePropertyChangeListener(propName, l);
    }
    
    public String getFirstDate() {
        return MMLDate.getDate(firstDay);
    }
    
    public String getLastDate() {
        return MMLDate.getDate(lastDay);
    }
    
    /**
     * 予約のある日をリストで返す。
     * @return 予約日リスト
     */
    public ArrayList<AppointmentModel> getAppointDays() {
        
        ArrayList<AppointmentModel> results = new ArrayList<AppointmentModel>();
        MedicalEvent event = null;
        AppointmentModel appoint = null;
        
        // 1 週目を調べる
        for (int col = firstCol; col < 7; col++) {
            event = days[0][col];
            appoint = event.getAppointEntry();
            if (appoint != null && appoint.getName() != null) {
                results.add(appoint);
            }
        }
        
        // 2 週目以降を調べる
        for (int row = 1; row < numRows - 1; row++) {
            for (int col = 0; col < 7; col++) {
                event = days[row][col];
                appoint = event.getAppointEntry();
                if (appoint != null && appoint.getName() != null) {
                    results.add(appoint);
                }
            }
        }
        
        // 最後の週を調べる
        for (int col = 0; col < lastCol + 1; col++) {
            event = days[numRows - 1][col];
            appoint = event.getAppointEntry();
            if (appoint != null && appoint.getName() != null) {
                results.add(appoint);
            }
        }
        
        return results;
    }
    
    /**
     * 更新された予約のリストを返す。
     * @return 更新された予約のリスト
     */
    public ArrayList<AppointmentModel> getUpdatedAppoints() {
        
        ArrayList<AppointmentModel> results = new ArrayList<AppointmentModel>();
        MedicalEvent event = null;
        AppointmentModel appoint = null;
        
        // 1 週目を調べる
        for (int col = firstCol; col < 7; col++) {
            event = days[0][col];
            appoint = event.getAppointEntry();
            if (appoint != null && appoint.getState() != AppointmentModel.TT_NONE) {
                results.add(appoint);
            }
        }
        
        // 2週目以降を調べる
        for (int row = 1; row < numRows - 1; row++) {
            for (int col = 0; col < 7; col++) {
                event = days[row][col];
                appoint = event.getAppointEntry();
                if (appoint != null && appoint.getState() != AppointmentModel.TT_NONE) {
                    results.add(appoint);
                }
            }
        }
        
        // 最後の週を調べる
        for (int col = 0; col < lastCol + 1; col++) {
            event = days[numRows - 1][col];
            appoint = event.getAppointEntry();
            if (appoint != null && appoint.getState() != AppointmentModel.TT_NONE) {
                results.add(appoint);
            }
        }
        
        return results;
    }
    
    public void setModuleList(String event, ArrayList list) {
        
        markEvent = event;
        clearMark();
        
        if (list == null || list.isEmpty()) {
            return;
        }
        
        int size = list.size();
        String mkDate = null;
        MedicalEvent me = null;
        int index = 0;
        int[] ymd = null;
        int row = 0;
        int col = 0;
        
        ModuleModel module = null;
        
        for (int i = 0; i < size; i++) {
            
            module = (ModuleModel)list.get(i);
            mkDate = ModelUtils.getDateAsString(module.getStarted());
            index = mkDate.indexOf('T');
            if (index > 0) {
                mkDate = mkDate.substring(0, index);
            }
            ymd = MMLDate.getCalendarYMD(mkDate);
            
            int shiftDay = ymd[2] + (firstCol -1);
            row = shiftDay / 7;
            col = shiftDay % 7;
            
            me = (MedicalEvent)days[row][col];
            me.setMedicalCode(markEvent);
            
            ((AbstractTableModel)table.getModel()).fireTableCellUpdated(row, col);
        }
    }
    
    public void setImageList(String event, ArrayList list) {
        
        markEvent = event;
        clearMark();
        
        if (list == null || list.isEmpty()) {
            return;
        }
        
        int size = list.size();
        String mkDate = null;
        MedicalEvent me = null;
        int index = 0;
        int[] ymd = null;
        int row = 0;
        int col = 0;
        
        ImageEntry image = null;
        
        for (int i = 0; i < size; i++) {
            
            image = (ImageEntry)list.get(i);
            mkDate = image.getConfirmDate();
            index = mkDate.indexOf('T');
            if (index > 0) {
                mkDate = mkDate.substring(0, index);
            }
            //System.out.println("PVT date: " + pvtDate);
            ymd = MMLDate.getCalendarYMD(mkDate);
            
            int shiftDay = ymd[2] + (firstCol -1);
            row = shiftDay / 7;
            col = shiftDay % 7;
            
            me = (MedicalEvent)days[row][col];
            me.setMedicalCode(markEvent);
            
            ((AbstractTableModel)table.getModel()).fireTableCellUpdated(row, col);
        }
    }
    
    public void setAppointmentList(ArrayList list) {
        
        // 当月以降のカレンダのみ検索する
        if (relativeMonth < 0 ) {
            return;
        }
        
        // 空ならリターン
        if ( list == null || list.isEmpty()) {
            return;
        }
        
        // 当月であれば本日の３日前から検索、そうでない場合はカレンダの最初の日から検索する
        String startDate = isThisMonth() ? MMLDate.getDayFromToday(-3) : MMLDate.getDate(firstDay);
        
        // 表示する
        int size = list.size();
        for (int i = 0; i < size; i++) {
            AppointmentModel ae = (AppointmentModel)list.get(i);
            ae.setState(AppointmentModel.TT_HAS);
            String date = ModelUtils.getDateAsString(ae.getDate());
            int index = date.indexOf('T');
            if (index > 0) {
                date = date.substring(0, index);
            }
            
            // startDate 以前の場合は表示しない
            if (date.compareTo(startDate) < 0 ) {
                continue;
            }
            
            int[] ymd = MMLDate.getCalendarYMD(date);
            
            int shiftDay = ymd[2] + (firstCol -1);
            int row = shiftDay / 7;
            int col = shiftDay % 7;
            
            MedicalEvent me = days[row][col];
            me.setAppointEntry(ae);
            
            ((AbstractTableModel)table.getModel()).fireTableCellUpdated(row, col);
        }
    }
    
    /**
     * 現在の表示をクリアする
     */
    private void clearMark() {
        MedicalEvent me = null;
        boolean exit = false;
        //String val = null;
        
        for (int row = 0; row < numRows; row++) {
            
            for (int col = 0; col < 7; col++) {
                
                me = days[row][col];
                
                if (me.isToday()) {
                    exit = true;
                    break;
                    
                } else if (me.getMedicalCode() != null) {
                    
                    me.setMedicalCode(null);
                    ((AbstractTableModel)table.getModel()).fireTableCellUpdated(row, col);
                }
            }
            if (exit) {
                break;
            }
        }
    }
    
    //////////////   Drag Support //////////////////
    
    @Override
    public void dragGestureRecognized(DragGestureEvent event) {
        
        int row = table.getSelectedRow();
        int col = table.getSelectedColumn();
        if (row == -1 || col == -1) {
            return;
        }
        
        dragRow = row;
        dragCol = col;
        MedicalEvent me = days[row][col];
        AppointmentModel appo = me.getAppointEntry();
        if (appo == null) {
            //System.out.println("No Appoint");
            return;
        }
        Transferable t = new AppointEntryTransferable(appo);
        Cursor cursor = DragSource.DefaultCopyDrop;
        int action = event.getDragAction();
        if (action == DnDConstants.ACTION_MOVE) {
            cursor = DragSource.DefaultMoveDrop;
        }
        
        // Starts the drag
        dragSource.startDrag(event, cursor, t, this);
    }
    
    @Override
    public void dragDropEnd(DragSourceDropEvent event) {
        
        if (! event.getDropSuccess() || event.getDropAction() == DnDConstants.ACTION_COPY) {
            return;
        }
        
        processCancel(dragRow, dragCol);
    }
    
    @Override
    public void dragEnter(DragSourceDragEvent event) {
    }
    
    @Override
    public void dragOver(DragSourceDragEvent event) {
    }
    
    @Override
    public void dragExit(DragSourceEvent event) {
    }
    
    @Override
    public void dropActionChanged( DragSourceDragEvent event) {
    }
    
    //////////// Drop Support ////////////////
    
    @Override
    public void drop(DropTargetDropEvent e) {
        
        if (! isDropAcceptable(e)) {
            e.rejectDrop();
            setDropTargetBorder(false);
            return;
        }
        
        // Transferable を取得する
        final Transferable tr = e.getTransferable();
        
        // Drop 位置を得る
        final Point loc = e.getLocation();
        
        // accept?
        int action = e.getDropAction();
        e.acceptDrop(action);
        //e.getDropTargetContext().dropComplete(true);
        setDropTargetBorder(false);
        
        int row = table.rowAtPoint(loc);
        int col = table.columnAtPoint(loc);
        //System.out.println("row = " + droppedRow + " col = " + droppedCol);
        if (row == -1 || col == -1) {
            e.getDropTargetContext().dropComplete(false);
            return;
        }
        
        // outOfMonth ?
        MedicalEvent evt = days[row][col];
        if (evt.isOutOfMonth()) {
            e.getDropTargetContext().dropComplete(false);
            return;
        }
        
        // 本日以前
        if (evt.before(today)) {
            e.getDropTargetContext().dropComplete(false);
            return;
        }
        
        // Drop 処理
        AppointmentModel source = null;
        try {
            source = (AppointmentModel)tr.getTransferData(AppointEntryTransferable.appointFlavor);
            
        } catch (Exception ue) {
            System.out.println(ue);
            source = null;
        }
        if (source == null) {
            e.getDropTargetContext().dropComplete(false);
            return;
        }
        
        processAppoint(row, col, source.getName(), source.getMemo());
        
        e.getDropTargetContext().dropComplete(true);
    }
    
    public boolean isDragAcceptable(DropTargetDragEvent evt) {
        return (evt.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) != 0;
    }
    
    public boolean isDropAcceptable(DropTargetDropEvent evt) {
        return (evt.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) != 0;
    }
    
    /** DropTaregetListener interface method */
    @Override
    public void dragEnter(DropTargetDragEvent e) {
        if (! isDragAcceptable(e)) {
            e.rejectDrag();
        }
    }
    
    /** DropTaregetListener interface method */
    @Override
    public void dragExit(DropTargetEvent e) {
        setDropTargetBorder(false);
    }
    
    /** DropTaregetListener interface method */
    @Override
    public void dragOver(DropTargetDragEvent e) {
        if (isDragAcceptable(e)) {
            setDropTargetBorder(true);
        }
    }
    
    /** DropTaregetListener interface method */
    @Override
    public void dropActionChanged(DropTargetDragEvent e) {
        if (! isDragAcceptable(e)) {
            e.rejectDrag();
        }
    }
    
    private void setDropTargetBorder(final boolean b) {
        Color c = b ? GUIFactory.getDropOkColor() : this.getBackground();
        table.setBorder(BorderFactory.createLineBorder(c, 2));
    }
    
    
    /**
     * カレンダーテーブルを生成する
     */
    private JTable createCalendarTable(GregorianCalendar gc) {
        
        days = createDays(gc);
        
        AbstractTableModel model = new AbstractTableModel() {
            
            @Override
            public int getRowCount() {
                return days.length;
            }
            
            @Override
            public int getColumnCount() {
                return days[0].length;
            }
            
            @Override
            public Object getValueAt(int row, int col) {
                return days[row][col];
            }
            
            @Override
            public String getColumnName(int col) {
                return columnNames[col];
            }
            
            @Override
            public Class getColumnClass(int col) {
                return java.lang.String.class;
            }
            
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        
        // Basic settings
        JTable tbl = new JTable(model);
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbl.setCellSelectionEnabled(true);
        tbl.setAutoResizeMode(autoResizeMode);
        tbl.setBackground(calendarBackground);
        
        // Replace DefaultRender
        DateRenderer dateRenderer = new DateRenderer();
        dateRenderer.setHorizontalAlignment(horizontalAlignment);
        tbl.setDefaultRenderer(java.lang.Object.class, dateRenderer);

        // ヘッダーレンダラ
        ((JLabel)tbl.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(horizontalAlignment);
        
        // Set ColumnWidth
        TableColumn column = null;
        for (int i = 0; i < 7; i++) {
            column = tbl.getColumnModel().getColumn(i);
            column.setMinWidth(columnWidth);
            column.setPreferredWidth(columnWidth);
            column.setMaxWidth(columnWidth);
        }

        // Row height
        tbl.setRowHeight(rowHeight);
        
        // Embed popupMenu
        tbl.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mousePressed(MouseEvent e) {
                if (appointMenu.isPopupTrigger(e)) {
                    doPopup(e);
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (appointMenu.isPopupTrigger(e)) {
                    doPopup(e);
                }
            }
        });
        
        return tbl;
    }
    
    private void doPopup(MouseEvent e) {
        
        // ReadOnly 時の予約は不可
        if (context.isReadOnly()) {
            return;
        }
        
        popedRow = table.rowAtPoint(e.getPoint());
        popedCol = table.columnAtPoint(e.getPoint());
        if (popedRow == -1 || popedCol == -1) {
            return;
        }
        
        // クリックされた位置の MedicalEvent
        MedicalEvent me = days[popedRow][popedCol];
        
        // 予約のない日
        // popup menu がキャンセルのみなので
        if (me.getAppointmentName() == null) {
            return;
        }
        
        // 本日以前の予約は不可
        if (me.before(today)) {
            return;
        }
        
        appointMenu.show(e.getComponent(),e.getX(), e.getY());
    }
    
    public void appointInspect(ActionEvent e) {
    }
    
    public void appointTest(ActionEvent e) {
        processAppoint(popedRow, popedCol, "検体検査", null);
    }
    
    public void appointImage(ActionEvent e) {
        processAppoint(popedRow, popedCol, "画像診断", null);
    }
    
    public void appointOther(ActionEvent e) {
        processAppoint(popedRow, popedCol, "その他", null);
    }

    public void appointCancel() {
        processCancel(popedRow, popedCol);
    }
    
    private void processAppoint(int row, int col, String appointName, String memo) {
        
        MedicalEvent entry = days[row][col];
        AppointmentModel appoint = entry.getAppointEntry();
        
        if (appoint == null) {
            appoint = new AppointmentModel();
            appoint.setDate(ModelUtils.getDateAsObject(entry.getDisplayDate()));
            entry.setAppointEntry(appoint);
        }
        
        int oldState = appoint.getState();
        int next = 0;
        switch (oldState) {
            
            case AppointmentModel.TT_NONE:
                next = AppointmentModel.TT_NEW;
                break;
                
            case AppointmentModel.TT_NEW:
                next = AppointmentModel.TT_NEW;
                break;
                
            case AppointmentModel.TT_HAS:
                next = AppointmentModel.TT_REPLACE;
                break;
                
            case AppointmentModel.TT_REPLACE:
                next = AppointmentModel.TT_REPLACE;
                break;
        }
        appoint.setState(next);
        
        appoint.setName(appointName);
        appoint.setMemo(memo);
        
        ((AbstractTableModel)table.getModel()).fireTableCellUpdated(popedRow, popedCol);
        
        boundSupport.firePropertyChange(CareMapDocument.APPOINT_PROP, null, appoint);
        
        if (! dirty) {
            dirty = true;
            parent.setDirty(dirty);
        }
    }
    
    private void processCancel(int row, int col) {
        
        MedicalEvent entry = days[row][col];
        AppointmentModel appoint = entry.getAppointEntry();
        if (appoint == null) {
            return;
        }
        
        int oldState = appoint.getState();
        int nextState = 0;
        
        switch (oldState) {
            case AppointmentModel.TT_NONE:
                break;
                
            case AppointmentModel.TT_NEW:
                nextState = AppointmentModel.TT_NONE;
                break;
                
            case AppointmentModel.TT_HAS:
                nextState = AppointmentModel.TT_REPLACE;
                break;
                
            case AppointmentModel.TT_REPLACE:
                nextState = AppointmentModel.TT_REPLACE;
                break;
        }
        
        appoint.setState(nextState);
        appoint.setName(null);
        
        ((AbstractTableModel)table.getModel()).fireTableCellUpdated(popedRow, popedCol);
        
        boundSupport.firePropertyChange(CareMapDocument.APPOINT_PROP, null, appoint);
        
        if (! dirty) {
            dirty = true;
            parent.setDirty(dirty);
        }
    }
    
    /**
     * カレンダテーブルのデータを生成する
     */
    private MedicalEvent[][] createDays(GregorianCalendar gc) {
        
        MedicalEvent[][] data = null;
        
        // Ｎケ月前／先の今日と同じ日
        int dayOfMonth = gc.get(Calendar.DAY_OF_MONTH);
        
        // 作成するカレンダ月の日数
        int numDaysOfMonth = gc.getActualMaximum(Calendar.DAY_OF_MONTH);
        
        // 最後の日が月の何週目か
        gc.add(Calendar.DAY_OF_MONTH, numDaysOfMonth - dayOfMonth);  // Last day
        lastDay = (GregorianCalendar)gc.clone();                     // Save last day
        numRows = gc.get(Calendar.WEEK_OF_MONTH);                    // Week of month
        
        // それは何カラム目か
        lastCol = gc.get(Calendar.DAY_OF_WEEK);
        lastCol--;
        
        // 月の最初の日
        numDaysOfMonth--;
        gc.add(Calendar.DAY_OF_MONTH, -numDaysOfMonth);
        firstDay = (GregorianCalendar)gc.clone();
        
        // 週の何日目か
        firstCol = gc.get(Calendar.DAY_OF_WEEK);
        firstCol--;
        
        // この月のカレンダーに表示する最初の日
        gc.add(Calendar.DAY_OF_MONTH, -firstCol);
        
        // データ配列を生成
        data = new MedicalEvent[numRows][7];
        
        // 一日づつ増加させながら埋め込み
        MedicalEvent me;
        boolean b;
        for (int i = 0; i < numRows; i++) {
            
            for (int j = 0; j < 7; j++) {
                
                me = new MedicalEvent(
                        gc.get(Calendar.YEAR),
                        gc.get(Calendar.MONTH),
                        gc.get(Calendar.DAY_OF_MONTH),
                        gc.get(Calendar.DAY_OF_WEEK));
                
                // 月外の日か
                b = month == gc.get(Calendar.MONTH) ? true : false;
                me.setOutOfMonth(!b);
                
                // 今日か
                b = today.equals(gc) ? true : false;
                me.setToday(b);
                
                data[i][j] = me;
                
                // 次の日
                gc.add(Calendar.DAY_OF_MONTH, 1);
            }
        }
        return data;
    }
    
    /**
     * Custom table cell renderer for the carendar panel.
     */
    class DateRenderer extends DefaultTableCellRenderer {
        
        private static final long serialVersionUID = -5061911803358533448L;
        
        public DateRenderer() {
            super();
            setOpaque(true);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value,
                boolean isSelected,
                boolean isFocused,
                int row, int col) {
            Component compo = super.getTableCellRendererComponent(table,
                    value,
                    isSelected,
                    isFocused,
                    row, col);
            if (value != null) {
                
                MedicalEvent me = (MedicalEvent)value;
                String eventCode = me.getMedicalCode();
                
                int dayOfWeek = me.getDayOfWeek();
                
                if (dayOfWeek == 1) {
                    this.setForeground(sundayColor);
                    
                } else if (dayOfWeek == 7) {
                    this.setForeground(saturdayColor);
                    
                } else {
                    this.setForeground(weekdayColor);
                }
                
                if (me.isOutOfMonth()) {
                    this.setFont(outOfMonthFont);
                    
                } else {
                    this.setFont(inMonthFont);
                }
                
                // 誕生日
                if (me.getDisplayDate().endsWith(birthday)) {
                    this.setBackground(birthdayColor);
                    
                    // 本日
                } else if (me.isToday() && (!me.isOutOfMonth())) {
                    this.setBackground(todayBackground);
                    
                    // 実施オーダのある日
                } else if (eventCode != null) {
                    Color c = parent.getOrderColor(eventCode);
                    this.setBackground(c);
                    
                    // 予約のある日
                } else if (me.getAppointEntry() != null) {
                    
                    String appoName = me.getAppointmentName();
                    
                    if (appoName == null) {
                        // Cancel
                        this.setBackground(calendarBackground);
                        
                    } else {
                        
                        Color c = parent.getAppointColor(appoName);
                        
                        // 本日以前
                        if (me.before(today)) {
                            this.setBackground(calendarBackground);
                            this.setBorder(BorderFactory.createLineBorder(c));
                            
                        } else {
                            // 本日以降
                            this.setBackground(c);
                        }
                    }
                    
                    // 何もない日
                } else {
                    
                    this.setBackground(calendarBackground);
                }
                
                ((JLabel)compo).setText(me.toString());
                
            }
            return compo;
        }
    }
    
    /**
     * CalendarPool Class
     */
    public static class SimpleCalendarPool {
        
        private static SimpleCalendarPool instance = new SimpleCalendarPool();
        
        private HashMap<String, ArrayList> poolDictionary = new HashMap<String, ArrayList>(12,0.75f);
        
        private SimpleCalendarPool() {
        }
        
        public static SimpleCalendarPool getInstance() {
            return instance;
        }
        
        public synchronized SimpleCalendarPanel acquireSimpleCalendar(int n) {
            ArrayList pool = (ArrayList)poolDictionary.get(String.valueOf(n));
            if (pool != null) {
                int size = pool.size();
                size--;
                return (SimpleCalendarPanel)pool.remove(size);
            }
            return new SimpleCalendarPanel(n);
        }
        
        @SuppressWarnings("unchecked")
        public synchronized void releaseSimpleCalendar(SimpleCalendarPanel c) {
            int n = c.getRelativeMonth();
            String key = String.valueOf(n);
            ArrayList pool = poolDictionary.get(key);
            if (pool == null) {
                pool = new ArrayList<SimpleCalendarPanel>(5);
                poolDictionary.put(key, pool);
            }
            pool.add(c);
        }
    }
}