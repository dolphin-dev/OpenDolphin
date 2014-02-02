package open.dolphin.client;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import open.dolphin.infomodel.SimpleDate;

/**
 * CalendarCardPanel
 *
 * @author Minagawa,Kazushi
 */
public class CalendarCardPanel extends JPanel  {
    
    public static final String PICKED_DATE = "pickedDate";
    
    private JPanel cardPanel = new JPanel();
    private CardLayout cardLayout = new CardLayout();
    private ImageIcon backIcon = ClientContext.getImageIcon("control_up.png");
    private ImageIcon stopIcon = ClientContext.getImageIcon("control_stop.png");
    private ImageIcon forwardIcon = ClientContext.getImageIcon("control_down.png");
    private JButton backBtn = new JButton(backIcon);
    private JButton stopBtn = new JButton(stopIcon);
    private JButton forwardBtn = new JButton(forwardIcon);
    private int current;
    private int[] range;
    private Hashtable<String, LiteCalendarPanel> calendars = new Hashtable<String, LiteCalendarPanel>(12,1.0f);
    private HashMap colorTable;
    private ArrayList markList;
    private PropertyChangeSupport boundSupport = new PropertyChangeSupport(this);
    private PropertyChangeListener calendarListener;
    
    public CalendarCardPanel(HashMap colorTable) {
        
        this.colorTable = colorTable;
        calendarListener = new CalendarListener(this);
        
        LiteCalendarPanel lc = new LiteCalendarPanel(current, true);
        lc.addPropertyChangeListener(LiteCalendarPanel.SELECTED_DATE_PROP, calendarListener);
        lc.setEventColorTable(colorTable);
        SimpleDate today = new SimpleDate(new GregorianCalendar());
        lc.setToday(today);
        String name = String.valueOf(current);
        calendars.put(name, lc);
        
        cardPanel.setLayout(cardLayout);
        cardPanel.add(lc, name);
        backBtn.setMargin(new Insets(2,2,2,2));
        stopBtn.setMargin(new Insets(2,2,2,2));
        forwardBtn.setMargin(new Insets(2,2,2,2));
        
        backBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                current -= 1;
                controlNavigation();
                showCalendar();
            }
        });
        
        stopBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                current = 0;
                controlNavigation();
                showCalendar();
            }
        });
        
        forwardBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                current+=1;
                controlNavigation();
                showCalendar();
            }
        });
        
        //JPanel cmdPanel = createCommnadPanel();
        
        this.setLayout(new BorderLayout(0,0));
        //this.add(cmdPanel, BorderLayout.WEST);
        this.add(cardPanel, BorderLayout.CENTER);
        //this.add(cmdPanel, BorderLayout.NORTH);
        //this.add(cmdPanel, BorderLayout.EAST);
        
        controlNavigation();
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
            } else if (current == range[1]) {
                if (forwardBtn.isEnabled()) {
                    forwardBtn.setEnabled(false);
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
            lc = new LiteCalendarPanel(current, true);
            lc.addPropertyChangeListener(LiteCalendarPanel.SELECTED_DATE_PROP, calendarListener);
            lc.setEventColorTable(colorTable);
            lc.getTableModel().setMarkDates(markList);
            calendars.put(key, lc);
            cardPanel.add(lc, key);
        } else {
            lc.getTableModel().setMarkDates(markList);
        }
        cardLayout.show(cardPanel, key);
    }
    
        /*private JPanel createCommnadPanel() {
                JPanel cmd = new JPanel(new FlowLayout(FlowLayout.CENTER,5,0));
                cmd.add(backBtn);
                cmd.add(stopBtn);
                cmd.add(forwardBtn);
                return cmd;
        }*/
    
        /*private JPanel createCommnadPanel() {
                JPanel cmd = new JPanel(new BorderLayout(0,5));
                cmd.add(backBtn, BorderLayout.NORTH);
                cmd.add(stopBtn, BorderLayout.CENTER);
                cmd.add(forwardBtn, BorderLayout.SOUTH);
                return cmd;
        }*/
    
        /*private JPanel createCommnadPanel() {
                JPanel cmd = new JPanel();
                cmd.setLayout(new BoxLayout(cmd, BoxLayout.Y_AXIS));
                cmd.add(javax.swing.Box.createVerticalGlue());
                cmd.add(backBtn);
                cmd.add(stopBtn);
                cmd.add(forwardBtn);
                cmd.add(javax.swing.Box.createVerticalGlue());
                return cmd;
        }*/
    
    class CalendarListener implements PropertyChangeListener {
        
        private CalendarCardPanel owner;
        
        public CalendarListener(CalendarCardPanel owner) {
            this.owner = owner;
        }
        
        public void propertyChange(PropertyChangeEvent e) {
            if (e.getPropertyName().equals(LiteCalendarPanel.SELECTED_DATE_PROP)) {
                SimpleDate sd = (SimpleDate)e.getNewValue();
                owner.notifyPickedDate(sd);
            }
        }
    }
}
