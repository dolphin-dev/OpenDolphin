package open.dolphin.client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import javax.swing.*;
import open.dolphin.infomodel.SimpleDate;

/**
 * CalendarCardPanel
 *
 * @author Minagawa,Kazushi
 */
public class CalendarCardPanel extends JPanel  {
    
    public static final String PICKED_DATE = "pickedDate";
    
    private final JPanel cardPanel = new JPanel();
    private final CardLayout cardLayout = new CardLayout();
    private final ImageIcon backIcon = ClientContext.getImageIconArias("icon_play_back");
    private final ImageIcon stopIcon = ClientContext.getImageIconArias("icon_stop_play");
    private final ImageIcon forwardIcon = ClientContext.getImageIconArias("icon_play");  
    private final JButton backBtn = new JButton(backIcon);
    private final JButton stopBtn = new JButton(stopIcon);
    private final JButton forwardBtn = new JButton(forwardIcon);
    private int current;
    private int[] range;
    private SimpleDate[] acceptRange;   
    private final HashMap<String, LiteCalendarPanel> calendars = new HashMap<>(12,1.0f);
    private final HashMap colorTable;
    private ArrayList markList;
    private final PropertyChangeSupport boundSupport = new PropertyChangeSupport(this);
    private final PropertyChangeListener calendarListener;
    
    private static final int TITLE_ALIGN = SwingConstants.CENTER;
    private static final int TITLE_FONT_SIZE = 14;
    private static final Font TITLE_FONT = new Font("Dialog", Font.PLAIN, TITLE_FONT_SIZE);
    
    private final JLabel titleLable;
    private final Color titleFore = ClientContext.getColor("color.calendar.title.fore");
    private final Color titleBack = ClientContext.getColor("color.calendar.title.back");
    private final int titleAlign = TITLE_ALIGN;
    private final Font titleFont = TITLE_FONT;
    
    /**
     * CalendarCardPanelを生成する。
     * 
     * @param colorTable カラーテーブル
     */
    public CalendarCardPanel(HashMap colorTable) {
        
        this.colorTable = colorTable;
        calendarListener = new CalendarListener(this);
        
        LiteCalendarPanel lc = new LiteCalendarPanel(current, false);
        lc.addPropertyChangeListener(LiteCalendarPanel.SELECTED_DATE_PROP, calendarListener);
        lc.setEventColorTable(colorTable);
        SimpleDate today = new SimpleDate(new GregorianCalendar());
        lc.setToday(today);
        String name = String.valueOf(current);
        calendars.put(name, lc);
        
        cardPanel.setLayout(cardLayout);
        cardPanel.add(lc, name);
        backBtn.setMargin(new Insets(0,0,0,0));
        stopBtn.setMargin(new Insets(0,0,0,0));
        forwardBtn.setMargin(new Insets(0,0,0,0));
        
        backBtn.addActionListener((ActionEvent e) -> {
            current -= 1;
            controlNavigation();
            showCalendar();
        });
        
        stopBtn.addActionListener((ActionEvent e) -> {
            current = 0;
            controlNavigation();
            showCalendar();
        });
        
        forwardBtn.addActionListener((ActionEvent e) -> {
            current+=1;
            controlNavigation();
            showCalendar();
        });
        
        titleLable = new JLabel();
        titleLable.setHorizontalAlignment(titleAlign);
        titleLable.setFont(titleFont);
        titleLable.setForeground(titleFore);
        titleLable.setBackground(titleBack);
        
        JPanel cmdPanel = createCommnadPanel();
        updateTitle(lc, titleLable);
        cmdPanel.add(titleLable);
        
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(cmdPanel);
        this.add(cardPanel);
        
        Dimension size = this.getPreferredSize();
        int h = size.height;
        int w = 290;    // 268
        size = new Dimension(w, h);
        this.setMinimumSize(size);
        this.setMaximumSize(size);
    }
    
    private void updateTitle(LiteCalendarPanel lc, JLabel label) {
//minagawa^ I18N        
//        StringBuilder buf = new StringBuilder();
//        buf.append(lc.getYear());
//        buf.append(ClientContext.getString("calendar.title.year"));
//        buf.append(" ");
//        String m = String.valueOf(lc.getMonth() + 1);
//        if (m.length()==1) {
//            buf.append("0");
//        }
//        buf.append(m);
//        buf.append(ClientContext.getString("calendar.title.month"));
//        label.setText(buf.toString());
        GregorianCalendar gc = new GregorianCalendar(lc.getYear(), lc.getMonth(), 1);
        String fmt = ClientContext.getString("calendar.title.yearMonth");
        SimpleDateFormat sdf = new SimpleDateFormat(fmt);
        String dateString = sdf.format(gc.getTime());
        label.setText(dateString);
//minagawa$        
    }
    
    @Override
    public void addPropertyChangeListener(String prop, PropertyChangeListener l) {
        boundSupport.addPropertyChangeListener(prop, l);
    }
    
    @Override
    public void removePropertyChangeListener(String prop, PropertyChangeListener l) {
        boundSupport.removePropertyChangeListener(prop, l);
    }
    
    public void notifyPickedDate(SimpleDate picked) {
        boundSupport.firePropertyChange(PICKED_DATE, null, picked);
    }
    
    public int[] getRange() {
        return range;
    }
    
    public void setCalendarRange(int[] range) {
        this.range = range;
        controlNavigation();
    }
    
    public void setAcceptRange(SimpleDate[] range) {
       this.acceptRange = range;
       String key = String.valueOf(current);
       LiteCalendarPanel lc = (LiteCalendarPanel)calendars.get(key);
       lc.setAcceptRange(this.acceptRange);
    }   
    
    public void setMarkList(ArrayList newMark) {
        
        if (markList != newMark) {
            markList = newMark;
        }
        LiteCalendarPanel lc = (LiteCalendarPanel)calendars.get(String.valueOf(current));
        lc.getTableModel().setMarkDates(markList);
    }
    
    private void controlNavigation() {
        if (range != null) {
            if (current == range[0]) {
                if (backBtn.isEnabled()) {
                    backBtn.setEnabled(false);
                }
                if (! forwardBtn.isEnabled()) {
                    forwardBtn.setEnabled(true);
                }
            } else if (current == range[1]) {
                if (forwardBtn.isEnabled()) {
                    forwardBtn.setEnabled(false);
                }
                if (! backBtn.isEnabled()) {
                    backBtn.setEnabled(true);
                }
            } else {
                if (! backBtn.isEnabled()) {
                    backBtn.setEnabled(true);
                }
                if (! forwardBtn.isEnabled()) {
                    forwardBtn.setEnabled(true);
                }
            }
        }
    }
    
    private void showCalendar() {
        
        String key = String.valueOf(current);
        LiteCalendarPanel lc = (LiteCalendarPanel)calendars.get(key);
        if (lc == null) {
            lc = new LiteCalendarPanel(current, false);
            lc.setAcceptRange(this.acceptRange);         
            lc.addPropertyChangeListener(LiteCalendarPanel.SELECTED_DATE_PROP, calendarListener);
            lc.setEventColorTable(colorTable);
            lc.getTableModel().setMarkDates(markList);
            calendars.put(key, lc);
            cardPanel.add(lc, key);
        } else {
            lc.getTableModel().setMarkDates(markList);
        }
        updateTitle(lc, titleLable);
        cardLayout.show(cardPanel, key);
    }
    
    private JPanel createCommnadPanel() {
        JPanel cmd = new JPanel();
        cmd.setLayout(new BoxLayout(cmd, BoxLayout.X_AXIS));
        backBtn.setMargin(new Insets(0,0,0,0));
        stopBtn.setMargin(new Insets(0,0,0,0));
        forwardBtn.setMargin(new Insets(0,0,0,0));
        cmd.add(backBtn);
        cmd.add(Box.createHorizontalStrut(2));
        cmd.add(stopBtn);
        cmd.add(Box.createHorizontalStrut(2));
        cmd.add(forwardBtn);
        cmd.add(Box.createHorizontalStrut(2));
        return cmd;
    }
    
    class CalendarListener implements PropertyChangeListener {
        
        private final CalendarCardPanel owner;
        
        public CalendarListener(CalendarCardPanel owner) {
            this.owner = owner;
        }
        
        @Override
        public void propertyChange(PropertyChangeEvent e) {
            if (e.getPropertyName().equals(LiteCalendarPanel.SELECTED_DATE_PROP)) {
                SimpleDate sd = (SimpleDate)e.getNewValue();
                owner.notifyPickedDate(sd);
            }
        }
    }
}
