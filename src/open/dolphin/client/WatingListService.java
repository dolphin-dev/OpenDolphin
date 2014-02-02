/*
 * WatingListService.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
 * Copyright (C) 2003,2004 Digital Globe, Inc. All rights reserved.
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
import javax.swing.table.*;

import open.dolphin.dao.*;
import open.dolphin.infomodel.*;
import open.dolphin.plugin.*;
import open.dolphin.plugin.event.*;
import open.dolphin.table.*;
import open.dolphin.util.*;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import java.io.*;

/**
 * 受付リスト。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class WatingListService extends AbstractFramePlugin implements IWatingList {
    
    // Placement 定数
    private final int OFFSET_RIGHT   = 10;  
    private final int OFFSET_BOTTOM  = 30;
    
    // カルテの状態
    public static final int TT_CLOSED       = 0;
    public static final int TT_OPENED       = 1;   // Visible
    public static final int TT_CLAIM_SENT   = 3;
    public static final int TT_HIDE         = 4;   // Opened but unvisible
        
    // PVT テーブル関連
    private final String[] COLUMN_NAMES = ClientContext.getStringArray("watingList.table.columnNames");
    
    private final int[] COLUMN_WIDTH = {
        80, 60, 150, 30, 30, 30, 30, 30
    };
    private final int START_NUM_ROWS = 20;
    private final int STATE_COLUMN   = 7;
        
    // PVT のチェック間隔 sec
    private final int CHECK_INTERVAL = 30;
    
    private int width   = 507;
    private int height  = 523;
    
    // Properties
    private String[] columnNames    = COLUMN_NAMES;    
    private int[] columnWidth       = COLUMN_WIDTH;    
    private int startNumRows        = START_NUM_ROWS;    
    private int checkInterval       = CHECK_INTERVAL;
    private Color closedColor       = Color.white;
    private Color openedColor       = new Color(130, 194, 127);
    private Color savedColor        = new Color(255, 64, 183);
   
    // GUI components
    private JTable pvTable;   
    private PvtTableModel myModel;    
    private JTextField countField;    
    private JLabel timerCheck;
    private JComboBox timerCombo;
    private AnimationLabel animationLabel;
           
    private PvtMessageListener pvtListener;
    
    private SqlPvtDao dao;
    private java.util.Timer checkTimer;
    private PvtChecker pvtChecker;
    private int[] intervalDictionary;
              
    /** Creates new WatingList */
    public WatingListService() {
    }
    
    public void initComponent() {
		checkInterval = ClientContext.getPreferences().getInt("watingList.checkInterval", checkInterval);
        
		// コンテンツを生成する
		JPanel ui = createContent();
		ui.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
    	this.getContentPane().add(ui);
    }
    
    public void start() {
        
        // PVT チェックタイマーを始動
        pvtChecker = new PvtChecker();
        checkTimer = new java.util.Timer(true);
        checkTimer.schedule(pvtChecker, 1000, 1000*checkInterval);
        for(int i = 0; i < intervalDictionary.length; i++) {
            if (checkInterval == intervalDictionary[i]) {
                timerCombo.setSelectedIndex(i);
                break;
            }
        }
        
        // Frame をオープンする
        Dimension screen = Toolkit.getDefaultToolkit ().getScreenSize();
        int x = screen.width - (width + OFFSET_RIGHT);
        int y = screen.height - height - OFFSET_BOTTOM;
        Dimension size = new Dimension(width, height);
        setToPreferenceBounds(x, y, width, height);
		
		super.start();
    }
    
    public void addPvtMessageListener(PvtMessageListener l) throws TooManyListenersException {
        if (pvtListener != null) {
            throw new TooManyListenersException();
            
        } else {
            pvtListener = l;
        }
    }
    
    public void removePvtMessageListener(PvtMessageListener l) {
        if (pvtListener != null && pvtListener==l) {
            pvtListener = l;
        }
    }
       
    ///////////////////////////////////////////////////////////////////////////

    public int getCheckInterval() {
        return checkInterval;
    }
    
    public void setCheckInterval(int val) {
        checkInterval = val;
    }
   
    public Color getOpenedColor() {
        return openedColor;
    }
    
    public void setOpenedColor(Color val) {
        openedColor = val;
    }
    
    public Color getSavedColor() {
        return savedColor;
    }
    
    public void setSavedColor(Color val) {
        savedColor = val;
    }
            
    public Color getUnopenedColor() {
        return closedColor;
    }
    
    public void setUnopenedColor(Color val) {
        closedColor = val;
    }    

    public void propertyChange(PropertyChangeEvent e) {
        
        if (e.getPropertyName().equals("pvtNumber")) {
            int number = ((Integer)e.getNewValue()).intValue();
            myModel.updateStatus(number);
            
        } else {
            super.propertyChange(e);
        }
    }

    //////////////////////////////////////////////////////////////////////////
    private JPanel createContent() {
      
        // PVT テーブルを生成
        myModel = new PvtTableModel(COLUMN_NAMES,startNumRows);
        TableSorter s = new TableSorter(myModel);
        pvTable = new JTable(s);
        s.addMouseListenerToHeaderInTable(pvTable);

        // SINGLE_SELECTION　に設定する
        pvTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pvTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // マウスクリック処理
        pvTable.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e) {
                
                /*
                // Event dispatch thread
                if (e.getClickCount() == 2) {
                    
                    int selectedRow = pvTable.getSelectedRow();
                    PatientVisit pvt = (PatientVisit)myModel.getPatientVisit(selectedRow);
                    
                    if (pvt != null) {
                        if (pvt.state == TT_OPENED) {
                            Toolkit.getDefaultToolkit().beep();
                            return;
                        }
                
                        if (pvtListener != null) {
                            
                            animationLabel.start();
                            PVTEvent evt = new PVTEvent(WatingListService.this);
                            evt.setPatientVisit(pvt);
                            evt.setAnimationLabel(animationLabel);

                            // イベントスレッドで通知
                            pvtListener.newPatientVisit(evt);
                            
                            animationLabel.stop();
                        }
                    }
                }*/
                
                if (e.getClickCount() == 2) {
                    final int selectedRow = pvTable.getSelectedRow();
                    final PatientVisit pvt = (PatientVisit)myModel.getPatientVisit(selectedRow);
                    
                    if ( pvt != null) {
                        
                        // Check if already opened                     
                        if (pvt.getState() == TT_OPENED) {
                            Toolkit.getDefaultToolkit().beep();
                            return;
                        }
                        Runnable r = new Runnable() {
                            
                                public void run() {
                                    
                                    SwingUtilities.invokeLater(new Runnable() {
                                        public void run() {
                                            animationLabel.start();
                                        }
                                    });
                                    
                                    
                                    if (pvtListener != null) {
                                        
                                        final PvtMessageEvent evt = new PvtMessageEvent(WatingListService.this);
                                        evt.setPatientVisit(pvt);

                                        // イベント通知
                                        pvtListener.pvtMessageEvent(evt);
                                    }
                                    
                                    SwingUtilities.invokeLater(new Runnable() {
                                        public void run() {
                                            animationLabel.stop();
                                        }
                                    });
                            }
                        };
                        Thread t = new Thread(r);
                        t.start();
                    }  
                }
            }
        });
        
        // セル幅設定
        if (columnWidth != null) {
            TableColumn column = null;
            for (int i = 0; i < columnWidth.length; i++) {
                column = pvTable.getColumnModel().getColumn(i);
                column.setPreferredWidth(columnWidth[i]);
            }
        }
        
        // State カラムのカスタムレンダラー登録
        TableColumnModel tcm = pvTable.getColumnModel();
        KarteStateRenderer renderer = new KarteStateRenderer();
        renderer.setHorizontalAlignment(JLabel.CENTER);
        tcm.getColumn(STATE_COLUMN).setCellRenderer(renderer);
      
        // Wraps table with scroller
        JScrollPane pvtScroller = new JScrollPane(pvTable);
        
        // Starts layout
        JPanel ui = new JPanel();
        ui.setLayout(new BoxLayout(ui, BoxLayout.Y_AXIS));
        
        // Explanatory notes
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));

        animationLabel = DesignFactory.createUltraSonicWave();
        p.add(animationLabel);
        p.add(Box.createHorizontalGlue());
        String text = ClientContext.getString("watingList.state.openText");
        p.add(new JLabel(text, new ColorFillIcon(openedColor,10, 10,1), SwingConstants.CENTER));
        p.add(DesignFactory.createtComponentHSpace());
        text = ClientContext.getString("watingList.state.accountText");
        p.add(new JLabel(text, new ColorFillIcon(savedColor,10, 10,1), SwingConstants.CENTER));
        ui.add(p);
        ui.add(DesignFactory.createtComponentVSpace());
        
        // PVT table
        ui.add(pvtScroller);
        ui.add(DesignFactory.createtComponentVSpace());
        
        // Count field
        p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.add(new JLabel(ClientContext.getImageIcon("kutu01.gif")));
        p.add(DesignFactory.createtComponentHSpace());
        text = ClientContext.getString("watingList.state.pvtText");
        p.add(new JLabel(text));
        p.add(DesignFactory.createtComponentHSpace());
        Dimension dim = new Dimension(50, 20);
        countField = new JTextField();
        countField.setPreferredSize(dim);
        countField.setMaximumSize(dim);
        countField.setEditable(false);
        p.add(countField);
        
        p.add(Box.createHorizontalStrut(5)); 
        
        timerCheck = new JLabel();
        dim = new Dimension(60,20);
        timerCheck.setPreferredSize(dim);
        timerCheck.setMaximumSize(dim);
        p.add(timerCheck);
        
        p.add(Box.createHorizontalGlue()); 
        
        text = ClientContext.getString("watingList.state.checkText");
        p.add(new JLabel(text));
        p.add(DesignFactory.createtComponentHSpace());
        String[] intervalString = ClientContext.getStringArray("watingList.intervalCombo");
        intervalDictionary = ClientContext.getIntArray("watingList.interval");
        timerCombo = new JComboBox(intervalString);
        timerCombo.addItemListener(new ItemListener() {
            
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    int index = timerCombo.getSelectedIndex();
                    checkInterval = intervalDictionary[index];
                    ClientContext.getPreferences().putInt("watingList.checkInterval", checkInterval);
                    checkTimer.cancel();
                    checkTimer = new java.util.Timer(true);
                    pvtChecker = new WatingListService.PvtChecker();
                    checkTimer.schedule(pvtChecker, 1000, 1000*checkInterval);         
                }
            }
        });
        dim = new Dimension(80,20);
        timerCombo.setPreferredSize(dim);
        timerCombo.setMaximumSize(dim);
        p.add(timerCombo);
        
        // Timer check field
        /*text = DolphinContext.getResourceString("watingList.state.checkText");
        p.add(new JLabel(text));
        p.add(DesignFactory.createtComponentHSpace());
        timerCheck = new JTextField(checkInterval);
        
        // チェック間隔の変更
        timerCheck.addActionListener(new ActionListener()) {
            public void actionPerformed(ActionEvent e) {
                
                String input = 
                
            }
        });
        dim = new Dimension(80,20);
        timerCheck.setPreferredSize(dim);
        timerCheck.setMaximumSize(dim);
        timerCheck.setEditable(true);
        p.add(timerCheck);
        p.add(new JLabel(" (秒)"));*/
        
        ui.add(p);
        
        return ui;
    }   
               
    ///////////////////////////////////////////////////////////////////////////
    /**
     * Class of PVT table model.
     */
    protected final class PvtTableModel extends ObjectTableModel {
        
        /** Creates new PvtTableModel */
        public PvtTableModel(String[] columnNames, int numRows) {
            
            super(columnNames, numRows);
        }

        public Object getValueAt (int row, int col) {
           
            Object ret = null;
            PatientVisit pvt = getPatientVisit(row);
             
            if (pvt != null) {

                switch (col) {

                    case 0:
                        ret = pvt.getPatient().getId();
                        break;
                        
                   case 1:
                      ret = pvt.getTime();
                      break;
                      
                   case 2:
                      ret = pvt.getPatient().getName();
                      break;
                                            
                   case 3:
                       if ( pvt.getPatient().getGender().equals("female") ) {
                           ret = "女性";
                           
                       } else if (pvt.getPatient().getGender().equals("male")) {
                           ret = "男性";
                           
                       } else {
                           ret = pvt.getPatient().getGender();
                       }
                      break;
                      
                   case 4:
                      ret = pvt.getPatient().getAge();
                      break;
                      
                   case 5:
                      ret = pvt.getDepartment();
                      break;   
                      
                   case 6:
                      ret = new Boolean(pvt.isAppointment());
                      break;
                      
                   case 7:
                      ret = new Integer(pvt.getState());
                      break;
                }
            }
            return ret;
        }

        public Class getColumnClass (int c) {
            
            switch(c) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                    return java.lang.String.class;
                case 6:
                    return java.lang.Boolean.class;
                case 7:
                    return java.lang.Integer.class;
            }
            return java.lang.String.class;
        }
       
        public void addPvt(ArrayList list) {

           int index = getObjectCount();
           int newCount = list.size();
           int tCount = index + newCount;
           Object[] o = list.toArray();

           for (int i = 0; i < newCount; i++) {
               addRow(o[i]);
           }

           countField.setText(String.valueOf(tCount));
        }
        
        public void updateStatus(int oid) {

            int size = getObjectCount();
            for (int i = 0; i < size; i++) {
                PatientVisit pv = (PatientVisit)getObject(i);
                if (pv.getNumber() == oid) {
                    fireTableCellUpdated(i, STATE_COLUMN);
                    break;
                }
            }
        }

        public PatientVisit getPatientVisit(int row) {
            return (PatientVisit)getObject(row);
        }
    }
    
    ///////////////////////////////////////////////////////////////////////////
    
    /**
     * 患者来院情報を定期的にチェックするタイマータスククラス。
     */
    protected class PvtChecker extends TimerTask  {
        
        /**
         * Creates new Task
         * @param date 検索する日
         */
        public PvtChecker() {
            dao = (SqlPvtDao)SqlDaoFactory.create(this, "dao.pvt");
        }
        
        /**
         * ＤＢの検索タスク
         */
        public void run() {
            
            String date =  MMLDate.getDate();
            String time = MMLDate.getTime();
            //System.out.println(time);
            timerCheck.setText(time);
            
            int skipCount = myModel.getObjectCount();
            //if (DEBUG) {
                //skipCount -= 7;
            //}
            //int skipCount = 0;
            ArrayList list = dao.getPatientVisit(date, skipCount);
            
            if (list != null) {
                myModel.addPvt(list);
                //myModel.setObjectList(list);
            }          
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Patient/Document state renderer for the PVT tabel
     */
    protected class KarteStateRenderer extends DefaultTableCellRenderer {

        /** Creates new IconRenderer */
        public KarteStateRenderer() {
            super();

            icon = new ColorFillIcon(Color.white, 10, 10, 1);
            setIcon(icon);
        }

        public Component getTableCellRendererComponent(
                                JTable table,
                                Object value,
                                boolean isSelected,
                                boolean isFocused,
                                int row, int col) {
            Component c = super.getTableCellRendererComponent(
                                             table, 
                                             value,
                                             isSelected,
                                             isFocused, 
                                             row, col);
            if (value != null && value instanceof Integer) {
                int i = ((Integer)value).intValue();
                Color fill = null;
                switch (i) {
                    case TT_CLOSED:
                        fill = closedColor;
                        break;
                    case TT_OPENED:
                        fill = openedColor;
                        break;
                    case TT_CLAIM_SENT:
                        fill = savedColor;
                        break;
                    default:
                        //assert false;
                        break;
                }                    
                icon.setFillColor(fill);
                ((JLabel)c).setText("");
            }
            else {
                icon.setFillColor(Color.white);
                ((JLabel)c).setText(value == null ? "" : value.toString());
            }
            return c;
        }

        protected ColorFillIcon icon;
    }
    
    private byte[] getXMLBytes(Object bean)  {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        XMLEncoder e = new XMLEncoder(new BufferedOutputStream(bo));
        e.writeObject(bean);
        e.close();
        return bo.toByteArray();
    }      
}