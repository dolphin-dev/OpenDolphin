/*
 * CareMap.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
 * Copyright (C) 2004 Digital Globe, Inc. All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *	
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *	
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package open.dolphin.client;

import javax.swing.*;

import open.dolphin.dao.*;

import java.beans.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

/**
 * CareMap Document.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class CareMapDocument extends DefaultChartDocument {
    
    public static final String MARK_EVENT_PROP = "MARK_EVENT_PROP";
    public static final String PERIOD_PROP     = "PERIOD_PROP";
    public static final String CALENDAR_PROP   = "CALENDAR_PROP";
    public static final String SELECTED_DATE_PROP = "SELECTED_DATE_PROP";
    public static final String SELECTED_APPOINT_DATE_PROP = "SELECTED_DATE_PROP";
    public static final String APPOINT_PROP = "APPOINT_PROP";
    
    private static final String[] orderNames = ClientContext.getStringArray("orderhistory.names");  //{"来院歴", "処方歴", "処置歴", "検査歴", "画像診断"};
    private static final String[] orderCodes = ClientContext.getStringArray("orderhistory.codes"); //= {"0", "210", "600", "700"};
    private static final Color[] orderColors = ClientContext.getColorArray("orderhistory.colors"); //{
    private static final String[] appointNames = ClientContext.getStringArray("appoint.appointNames");
    private static final Color[] appointColors = ClientContext.getColorArray("appoint.appointColors");
    private final Icon[] orderIcons = {
        new ColorFillIcon(orderColors[0], 10, 10,1),
        new ColorFillIcon(orderColors[1], 10, 10,1),
        new ColorFillIcon(orderColors[2], 10, 10,1),
		new ColorFillIcon(orderColors[3], 10, 10,1)
    };
    
    private JComboBox orderCombo;
    private OrderHistoryPanel history;
    private AppointTablePanel appointTable;
    private ImageHistoryPanel imagePanel;
    private JPanel historyContainer;
    private String imageEvent = orderCodes[3];
    
    // Calendars
    private SimpleCalendarPanel c0;
    private SimpleCalendarPanel c1;
    private SimpleCalendarPanel c2;
    private Period period;
    private int origin;
    private PropertyChangeSupport boundSupport;
    private Hashtable cPool;
    private String curEvent;
    private boolean dirty;
    
    /** Creates new CareMap*/
    public CareMapDocument() {
    }
    
    public void start() {
                
        cPool = new Hashtable(12, 0.75f);
        final String pid = context.getPatient().getId();
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        // create this and next month calendar
        SimpleCalendarPanel.SimpleCalendarPool pool = SimpleCalendarPanel.SimpleCalendarPool.getInstance();
        c0 = pool.acquireSimpleCalendar(origin - 1);
        c1 = pool.acquireSimpleCalendar(origin);
        c2 = pool.acquireSimpleCalendar(origin + 1);
        c0.setChartContext(context);
        c1.setChartContext(context);
        c2.setChartContext(context);
        c0.setParent(this);
        c1.setParent(this);
        c2.setParent(this);
        cPool.put(new Integer(origin - 1), c0);
        cPool.put(new Integer(origin), c1);
        cPool.put(new Integer(origin + 1), c2);
        
        // Layout calendars
        final JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.add(Box.createHorizontalStrut(11));
        p.add(c0);
        p.add(Box.createHorizontalStrut(11));
        p.add(c1);
        p.add(Box.createHorizontalStrut(11));
        p.add(c2);
        p.add(Box.createHorizontalStrut(11));
        // 
        JButton prevBtn = new JButton(ClientContext.getImageIcon("Back16.gif"));
        
        prevBtn.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                
                SimpleCalendarPanel.SimpleCalendarPool pool = SimpleCalendarPanel.SimpleCalendarPool.getInstance();
                origin--;
                SimpleCalendarPanel save = c0;
                SimpleCalendarPanel test = (SimpleCalendarPanel)cPool.get(new Integer(origin -1));
                
                if (test != null) {
                    c0 = test;
                    
                    // カレンダが表示されていない時に curEvent が変更された可能性があるため
                    c0.setMarkEvent(curEvent);
                    
                } else {
                    // Creates a new calendar
                    c0 = pool.acquireSimpleCalendar(origin - 1);
                    c0.setChartContext(context);
                    c0.setParent(CareMapDocument.this);
                    c0.addPropertyChangeListener(SELECTED_DATE_PROP, history);
                    c0.addPropertyChangeListener(SELECTED_DATE_PROP, imagePanel);
                    c0.addPropertyChangeListener(SELECTED_APPOINT_DATE_PROP, appointTable);
                    c0.addPropertyChangeListener(APPOINT_PROP,appointTable);
                    c0.setMarkEvent(curEvent);
                    cPool.put(new Integer(origin -1), c0);
                }
                
                c2 = c1;
                c1 = save;
                p.removeAll();
                p.add(c0);
                p.add(c1);
                p.add(c2);
                p.revalidate();
                
                // Notify period
                setPeriod(c0.getFirstDate(),c2.getLastDate());
                notifyCalendar();
            }
        });
        
        JButton nextBtn = new JButton(ClientContext.getImageIcon("Forward16.gif"));
        
        nextBtn.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                
                SimpleCalendarPanel.SimpleCalendarPool pool = SimpleCalendarPanel.SimpleCalendarPool.getInstance();
                origin++;
                SimpleCalendarPanel save = c2;
                SimpleCalendarPanel test = (SimpleCalendarPanel)cPool.get(new Integer(origin + 1));
                
                if (test != null) {
                    c2 = test;
                    c2.setMarkEvent(curEvent);
                    
                } else {
                    // New calendar
                    c2 = pool.acquireSimpleCalendar(origin + 1);                   
                    c2.setChartContext(context);
                    c2.setParent(CareMapDocument.this);
                    c2.addPropertyChangeListener(SELECTED_DATE_PROP, history);
                    c2.addPropertyChangeListener(SELECTED_DATE_PROP, imagePanel);
                    c2.addPropertyChangeListener(SELECTED_APPOINT_DATE_PROP, appointTable);
                    c2.addPropertyChangeListener(APPOINT_PROP, appointTable);
                    c2.setMarkEvent(curEvent);
                    c2.markAppoint();
                    cPool.put(new Integer(origin +1), c2);
                }
                
                c0 = c1;
                c1 = save;
                p.removeAll();
                p.add(c0);
                p.add(c1);
                p.add(c2);
                p.revalidate();
                
                // Notify period
                setPeriod(c0.getFirstDate(),c2.getLastDate());
                notifyCalendar();
            }
        });        
        // Creates Appoint-Table panel
        appointTable = new AppointTablePanel();
        appointTable.setParent(this);
        appointTable.setBorder(BorderFactory.createTitledBorder("予約表"));
        appointTable.setPreferredSize(new Dimension(500, 260));

        // Creates Order-History Table
        history = new OrderHistoryPanel();
        history.setParent(this);
        history.setPid(context.getPatient().getId());
        //history.setBorder(BorderFactory.createTitledBorder("履 歴"));
        
        // Creates Image-History panel
        imagePanel = new ImageHistoryPanel();
        imagePanel.setParent(this);
        imagePanel.setPid(context.getPatient().getId());
        
        // Creates Command panel;
        JPanel cp = new JPanel();
        cp.setLayout(new BoxLayout(cp, BoxLayout.X_AXIS));
        orderCombo = new JComboBox(orderNames);
        Dimension dim = new Dimension(100,26);
        orderCombo.setPreferredSize(dim);
        orderCombo.setMaximumSize(dim);
        ComboBoxRenderer r = new ComboBoxRenderer();
        orderCombo.setRenderer(r);
        orderCombo.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    
                    String event = getMarkCode();
                    if (event.equals(imageEvent)) {
                        historyContainer.removeAll();
                        historyContainer.add(imagePanel, BorderLayout.CENTER);
                        historyContainer.revalidate();
                        CareMapDocument.this.repaint(); 
                       
                    } else if (curEvent.equals(imageEvent)) {
                        historyContainer.removeAll();
                        historyContainer.add(history, BorderLayout.CENTER);
                        historyContainer.revalidate();
                        CareMapDocument.this.repaint();
                    }
                        
                    setMarkEvent(event);
                }
            }
        });
        cp.add(Box.createHorizontalGlue());
        cp.add(prevBtn);
        cp.add(Box.createHorizontalStrut(5));
        cp.add(orderCombo);
        cp.add(Box.createHorizontalStrut(5));
        cp.add(nextBtn);      
        //cp.add(Box.createHorizontalStrut(30));
        cp.add(Box.createHorizontalGlue());
        JPanel han = new JPanel();
        han.setLayout(new BoxLayout(han, BoxLayout.X_AXIS));
        han.add(new JLabel("予約( "));
        for (int i = 0; i < appointNames.length; i++) {
            if (i != 0) {
                han.add(Box.createHorizontalStrut(7));
            }
            AppointLabel dl = new AppointLabel(appointNames[i], new ColorFillIcon(appointColors[i],10, 10,1), SwingConstants.CENTER);
            han.add(dl);
        }
        han.add(new JLabel(" )"));
        han.add(Box.createHorizontalStrut(7));
        Color birthC = ClientContext.getColor("calendar.background.birthday");
        han.add(new JLabel("誕生日", new ColorFillIcon(birthC,10, 10,1), SwingConstants.CENTER));
        han.add(Box.createHorizontalStrut(11));
        cp.add(han);
              
        this.add(p);
        this.add(Box.createVerticalStrut(7));
        this.add(cp);
        this.add(Box.createVerticalStrut(7));
        
        // 検査履歴と画像歴の切り替えコンテナ
        historyContainer = new JPanel(new BorderLayout());
        historyContainer.add(history, BorderLayout.CENTER);
        historyContainer.setBorder(BorderFactory.createTitledBorder("履 歴"));
        this.add(historyContainer);
        
        this.add(Box.createVerticalStrut(7));
        this.add(appointTable);
        
        this.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
       
        // Creates listener-relations
        addPropertyChangeListener(MARK_EVENT_PROP, history);
        addPropertyChangeListener(MARK_EVENT_PROP, imagePanel);
        
        addPropertyChangeListener(PERIOD_PROP, history);
        addPropertyChangeListener(PERIOD_PROP, imagePanel);
        
        addPropertyChangeListener(CALENDAR_PROP, appointTable);
    
        c0.addPropertyChangeListener(APPOINT_PROP, appointTable);
        c1.addPropertyChangeListener(APPOINT_PROP, appointTable);
        c2.addPropertyChangeListener(APPOINT_PROP, appointTable);
        
        c0.addPropertyChangeListener(SELECTED_DATE_PROP, history);
        c1.addPropertyChangeListener(SELECTED_DATE_PROP, history);
        c2.addPropertyChangeListener(SELECTED_DATE_PROP, history);
        c0.addPropertyChangeListener(SELECTED_DATE_PROP, imagePanel);
        c1.addPropertyChangeListener(SELECTED_DATE_PROP, imagePanel);
        c2.addPropertyChangeListener(SELECTED_DATE_PROP, imagePanel);
        
        c0.addPropertyChangeListener(SELECTED_APPOINT_DATE_PROP, appointTable);
        c1.addPropertyChangeListener(SELECTED_APPOINT_DATE_PROP, appointTable);
        c2.addPropertyChangeListener(SELECTED_APPOINT_DATE_PROP, appointTable);
        
        setMarkEvent(getMarkCode());
        c1.markAppoint();
        c2.markAppoint();
        
        setPeriod(c0.getFirstDate(),c2.getLastDate());
        notifyCalendar();
              
        enter();
        
    }
    
    public Color getOrderColor(String order) {
        Color ret = null;
        for (int i = 0; i < orderCodes.length; i++) {
            if (order.equals(orderCodes[i])) {
                ret = orderColors[i];
            }
        }
        return ret;
    }
    
    public Color getAppointColor(String appoint) {
        
        if (appoint == null) {
            return Color.white;
        }
        
        Color ret = null;
        for (int i = 0; i < appointNames.length; i++) {
            if (appoint.equals(appointNames[i])) {
                ret = appointColors[i];
            }
        }
        return ret;
    }    
    
    public void addPropertyChangeListener(String prop, PropertyChangeListener l) {
        if (boundSupport == null) {
            boundSupport = new PropertyChangeSupport(this);
        }
        boundSupport.addPropertyChangeListener(prop, l);
    }
    
    public void removePropertyChangeListener(String prop, PropertyChangeListener l) {
        if (boundSupport == null) {
            boundSupport = new PropertyChangeSupport(this);
        }
        boundSupport.removePropertyChangeListener(prop, l);
    }
    
    private void setPeriod(String start, String end) {
        Period old = period;
        if (period == null) {
            period = new Period(this);
        }
        period.setStartDate(start);
        period.setEndDate(end);
        boundSupport.firePropertyChange("PERIOD_PROP", null, period);
    }
    
    private void notifyCalendar() {
        SimpleCalendarPanel[] sc = new SimpleCalendarPanel[3];
        sc[0] = c0;
        sc[1] = c1;
        sc[2] = c2;
        boundSupport.firePropertyChange("CALENDAR_PROP", null, sc);
    }
    
    public String getMarkEvent() {
        return curEvent;
    }
    
    public void setMarkEvent(String code) {
        String old = curEvent;
        curEvent = code;
        c0.setMarkEvent(curEvent);
        c1.setMarkEvent(curEvent);
        c2.setMarkEvent(curEvent);
        boundSupport.firePropertyChange("MARK_EVENT_PROP", old, curEvent);
    }
    
    public boolean isDirty() {
        return dirty;
    }
    
    public void enter() {
        super.enter();
        controlMenu();
    }
    
    public void setDirty(boolean b) {
        if (dirty != b) {
            dirty = b;
            controlMenu();
        }
    }  
    
    protected void controlMenu() {
        super.controlMenu();
        ChartMediator med = ((ChartPlugin)context).getChartMediator();
        med.saveKarteAction.setEnabled(dirty);
    }
        
    public void save()  {
        
        ArrayList results = new ArrayList();
        Enumeration e = cPool.elements();
     
        while ( e.hasMoreElements() ) {
            SimpleCalendarPanel c = (SimpleCalendarPanel)e.nextElement();
            if (c.getRelativeMonth() >= 0) {
                
                ArrayList list = c.getUpdatedAppoints();
                int size = list.size();
                for (int i = 0; i < size; i++) {
                    results.add(list.get(i));
                }
            }
        }
        
        if (results.size() == 0) {
            results = null;
            return;
        }
        
        SqlAppointSaverDao dao = (SqlAppointSaverDao)SqlDaoFactory.create(this, "dao.appointSaver");
        dao.save(context.getPatient().getId(), results);
        
        dirty = false;
        controlMenu();
    }
    
    private String getMarkCode() {
        // 履歴名を検索コード(EntityName)に変換
        int index = orderCombo.getSelectedIndex();
        return orderCodes[index];
    }
    
    protected class ComboBoxRenderer extends JLabel
                           implements ListCellRenderer {
        //private Font uhOhFont;

        public ComboBoxRenderer() {
            setOpaque(true);
            //setHorizontalAlignment(CENTER);
            setVerticalAlignment(CENTER);
        }

        /*
         * This method finds the image and text corresponding
         * to the selected value and returns the label, set up
         * to display the text and image.
         */
        public Component getListCellRendererComponent(
                                           JList list,
                                           Object value,
                                           int index,
                                           boolean isSelected,
                                           boolean cellHasFocus) {
            //Get the selected index. (The index param isn't
            //always valid, so just use the value.)                                  

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            //Set the icon and text.  If icon was null, say so.
            Icon icon = getOrderIcon((String)value);
     
            if (icon != null) {
                setIcon(icon);
                setText((String)value);
            }

            return (Component)this;
        }
    
        private Icon getOrderIcon(String name) {
            Icon ret = null;
            for (int i = 0; i < orderNames.length; i++) {
                if (name.equals(orderNames[i])) {
                    ret = orderIcons[i];
                    break;
                }
            }

            return ret;
        }
    }
}