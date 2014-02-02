/*
 * TreatmentMaster.java
 * Copyright (C) 2007 Dolphin Project. All rights reserved.
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
package open.dolphin.order;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import open.dolphin.client.AbstractInfiniteTask;
import open.dolphin.client.ClientContext;
import open.dolphin.client.MasterRenderer;
import open.dolphin.client.TimeoutWarning;
import open.dolphin.client.UltraSonicProgressLabel;
import open.dolphin.dao.SqlDaoFactory;
import open.dolphin.dao.SqlMasterDao;
import open.dolphin.infomodel.TreatmentEntry;
import open.dolphin.table.ObjectTableModel;
import open.dolphin.util.ReflectMonitor;

/**
 * 診療行為マスタ検索パネルクラス。
 *
 * @author Kazushi Minagawa
 */
public class TreatmentMaster extends MasterPanel {
    
    private static final long serialVersionUID = -4833490720433639368L;
    
    private static final String[] treatmentColumns = ClientContext.getStringArray("masterSearch.treatment.columnNames");
    private static final String[] treatmentCostFlags = ClientContext.getStringArray("masterSearch.treatment.costFlags");
    private static final String[] inOutFlags = ClientContext.getStringArray("masterSearch.treatment.inOutFlags");
    private static final String[] hospClinicFlags= ClientContext.getStringArray("masterSearch.treatment.hospitalClinicFlags");
    private static final String[] oldFlags = ClientContext.getStringArray("masterSearch.treatment.oldFlags");
    private static final String[] sortButtonNames = ClientContext.getStringArray("masterSearch.treatment.sortButtonNames");
    private static final String[] sortColumnNames = ClientContext.getStringArray("masterSearch.treatment.sortColumnNames");
    
    /** カテゴリ検索ボタン */
    private JButton categoryButton;
    
    /** 撮影部位検索ボタン */
    private JButton radLOcationButton;
    
    private boolean radiology;
    
    /**
     * TreatmentMasterオブジェクトを生成する。
     * @param master マスタ名
     * @param pulse 進捗バー
     */
    public TreatmentMaster(String master) {
        super(master);
    }
    
    /**
     * 初期化する。
     */
    protected void initialize() {
        
        //
        // ソートボタングループを生成する
        //
        ButtonGroup bg = new ButtonGroup();
        sortButtons = new JRadioButton[sortButtonNames.length];
        for (int i = 0; i < sortButtonNames.length; i++) {
            JRadioButton radio = new JRadioButton(sortButtonNames[i]);
            sortButtons[i] = radio;
            bg.add(radio);
            radio.addActionListener(new SortActionListener(this, sortColumnNames[i], i));
        }
        
        // 前回選択されていたものをリストアする
        int index = prefs.getInt("masterSearch.treatment.sort", 0);
        sortButtons[index].setSelected(true);
        setSortBy(sortColumnNames[index]);
        
        //
        // カテゴリ全検索ボタンを生成する
        //
        categoryButton = new JButton("全検索");
        categoryButton.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                getByClaimClass();
            }
        });
        
        //
        // 放射線部位検索ボタンを生成する
        //
        radLOcationButton = new JButton("放射線部位");
        radLOcationButton.setEnabled(false);
        radLOcationButton.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                getRadLocation();
            }
        });
        
        //
        // TableModel を生成する
        //
        tableModel = new ObjectTableModel(treatmentColumns, START_NUM_ROWS) {
            
            private static final long serialVersionUID = 8084360322119845887L;
            
            @SuppressWarnings("unchecked")
            public Class getColumnClass(int col) {
                return TreatmentEntry.class;
            }
        };
        
        //
        // Table を生成する
        //
        table = new JTable(tableModel);
        
        // シングル選択モードに設定する
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        //
        // 行選択が起った時に MaterItem を生成しリスナへ通知する
        //
        table.setRowSelectionAllowed(true);
        ListSelectionModel m = table.getSelectionModel();
        m.addListSelectionListener(new ListSelectionListener() {
            
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false) {
                    int row = table.getSelectedRow();
                    TreatmentEntry o = (TreatmentEntry)tableModel.getObject(row);
                    if (o != null) {
                        // Event adpter
                        MasterItem mItem = new MasterItem();
                        mItem.setClassCode(0);          // 手技
                        mItem.setCode(o.getCode());
                        mItem.setName(o.getName());
                        mItem.setClaimClassCode(o.getClaimClassCode());
                        setSelectedItem(mItem);
                    }
                }
            }
        });
        
        // 列幅を設定する
        TableColumn column = null;
        int[] width = new int[]{50, 150, 150, 30, 50, 30, 30, 30, 50};
        int len = width.length;
        for (int i = 0; i < len; i++) {
            column = table.getColumnModel().getColumn(i);
            column.setPreferredWidth(width[i]);
        }
        
        // レンダラを生成し設定する
        TreatmentMasterRenderer tr = new TreatmentMasterRenderer();
        tr.setBeforStartColor(masterColors[0]);
        tr.setInUseColor(masterColors[1]);
        tr.setAfterEndColor(masterColors[2]);
        tr.setCostFlag(treatmentCostFlags);
        tr.setInOutFlag(inOutFlags);
        tr.setOldFlag(oldFlags);
        tr.setHospitalClinicFlag(hospClinicFlags);
        table.setDefaultRenderer(TreatmentEntry.class, tr);
        
        // レイアウトする
        // Keyword
        JPanel key = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        key.add(findLabel);
        key.add(new JLabel(masterTabNames[4] + ":"));
        key.add(keywordField);
        key.setBorder(BorderFactory.createTitledBorder(keywordBorderTitle));
        
        JPanel sort = new JPanel();
        sort.setLayout(new BoxLayout(sort, BoxLayout.X_AXIS));
        for (int i = 0; i < sortButtons.length; i++) {
            if ( i != 0) {
                sort.add(Box.createHorizontalStrut(5));
            }
            sort.add(sortButtons[i]);
        }
        sort.setBorder(BorderFactory.createTitledBorder("ソート"));
        
        JPanel category = new JPanel();
        category.setLayout(new BoxLayout(category, BoxLayout.X_AXIS));
        category.add(categoryButton);
        category.add(Box.createHorizontalStrut(5));
        category.add(radLOcationButton);
        category.setBorder(BorderFactory.createTitledBorder("カテゴリ"));
        
        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));
        top.add(key);
        top.add(Box.createHorizontalStrut(7));
        top.add(category);
        //top.add(Box.createHorizontalGlue());
        top.add(sort);
        
        // Table
        JScrollPane scroller = new JScrollPane(table,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        this.setLayout(new BorderLayout(0, 11));
        this.add(top, BorderLayout.NORTH);
        this.add(scroller, BorderLayout.CENTER);
    }
    
    /**
     * 検索する診療行為コードの範囲を設定する。
     * @param searchClass
     */
    public void setSearchClass(String searchClass) {
        
        this.searchClass = searchClass;
        
        if (this.searchClass == null) {
            categoryButton.setEnabled(false);
        } else {
            categoryButton.setEnabled(true);
        }
    }
    
    /**
     * 撮影部位検索ボタンを enabled にする。
     * @param enabled
     */
    public void setRadLocationEnabled(boolean enabled) {
        radiology = enabled;
        radLOcationButton.setEnabled(enabled);
    }
    
//    /**
//     * 検索中プロパティを設定する。
//     * @param busy 検索中の時 true
//     */
//    public void setBusy(boolean busy) {
//        
//        keywordField.setEnabled(!busy);
//        
//        if (this.searchClass != null) {
//            categoryButton.setEnabled(!busy);
//        }
//        
//        if (radiology) {
//            radLOcationButton.setEnabled(!busy);
//        }
//        
//        super.setBusy(busy);
//    }
    
    /**
     * 診療行為コードで点数マスタを検索する。
     */
    private void getByClaimClass() {
        
        // DAO を生成する
        final SqlMasterDao dao = (SqlMasterDao) SqlDaoFactory.create(this, "dao.master");
        
        // ReflectMonitor を生成する
        final ReflectMonitor rm = new ReflectMonitor();
        rm.setReflection(dao, 
                         "getByClaimClass", 
                         new Class[]{String.class, String.class, String.class, String.class},
                         new Object[]{master, searchClass, sortBy, order});
        rm.setMonitor(SwingUtilities.getWindowAncestor(this), "診療行為検索", searchClass + " を検索しています...  ", 200, 30*1000);
        
        // 結果状態のリスナを生成する
        PropertyChangeListener pl = new PropertyChangeListener() {
           
            public void propertyChange(PropertyChangeEvent e) {
                
                int state = ((Integer) e.getNewValue()).intValue();
                
                switch (state) {
                    
                    case ReflectMonitor.DONE:
                        processResult(dao.isNoError(), rm.getResult(), dao.getErrorMessage());
                        break;
                        
                    case ReflectMonitor.TIME_OVER:
                        Window parent = SwingUtilities.getWindowAncestor(TreatmentMaster.this);
                        String title = ClientContext.getString(getMaster());
                        new TimeoutWarning(parent, title, null).start();
                        break;
                        
                    case ReflectMonitor.CANCELED:
                        break;
                }
                
                //
                // Block を解除する
                //
                setBusy(false);
            }
        };
        rm.addPropertyChangeListener(pl);
        
        //
        // Block し、メソッドの実行を開始する
        //
        setBusy(true);
        rm.start();
    }
    
//    /**
//     * 診療行為コードで点数マスタを検索する。
//     */
//    private void getByClaimClass() {
//        
//        // DAO を生成する
//        final SqlMasterDao dao = (SqlMasterDao) SqlDaoFactory.create(this, "dao.master");
//        
//        // Worker を生成する
//        int maxEstimation = ClientContext.getInt("task.masterSearch.maxEstimation");
//        int delay = ClientContext.getInt("task.masterSearch.delay");
//        final TreatmentTask worker = new TreatmentTask(0, dao, maxEstimation/delay);
//        
//        // タスクタイマーを生成する
//        taskTimer = new javax.swing.Timer(TIMER_DELAY, new ActionListener() {
//            
//            public void actionPerformed(ActionEvent e) {
//                
//                worker.getCurrent();
//                
//                if (worker.isDone()) {
//                    
//                    taskTimer.stop();
//                    setBusy(false);
//                    
//                    if (dao.isNoError()) {
//                        List result = worker.getResult();
//                        tableModel.setObjectList(result);
//                        setItemCount(tableModel.getObjectCount());
//                        
//                    } else {
//                        Window parent = SwingUtilities.getWindowAncestor(TreatmentMaster.this);
//                        String message = dao.getErrorMessage();
//                        String title = ClientContext.getFrameTitle(getMaster());
//                        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.WARNING_MESSAGE);
//                    }
//                } else if (worker.isTimeOver()) {
//                    taskTimer.stop();
//                    setBusy(false);
//                    Window parent = SwingUtilities.getWindowAncestor(TreatmentMaster.this);
//                    String title = ClientContext.getString(getMaster());
//                    new TimeoutWarning(parent, title, null).start();
//                }
//            }
//        });
//        setBusy(true);
//        worker.start();
//        taskTimer.start();
//    }
    
    /**
     * 撮影部位を検索する。
     */
    private void getRadLocation() {
        
        // DAO を生成する
        final SqlMasterDao dao = (SqlMasterDao)SqlDaoFactory.create(this, "dao.master");
        
          // ReflectMonitor を生成する
        final ReflectMonitor rm = new ReflectMonitor();
        rm.setReflection(dao, 
                         "getRadLocation", 
                         new Class[]{String.class, String.class, String.class},
                         new Object[]{master, sortBy, order});
        rm.setMonitor(SwingUtilities.getWindowAncestor(this), "放射線部位検索", "検索しています...  ", 200, 30*1000);
        
        // 結果状態のリスナを生成する
        PropertyChangeListener pl = new PropertyChangeListener() {
           
            public void propertyChange(PropertyChangeEvent e) {
                
                int state = ((Integer) e.getNewValue()).intValue();
                
                switch (state) {
                    
                    case ReflectMonitor.DONE:
                        processResult(dao.isNoError(), rm.getResult(), dao.getErrorMessage());
                        break;
                        
                    case ReflectMonitor.TIME_OVER:
                        Window parent = SwingUtilities.getWindowAncestor(TreatmentMaster.this);
                        String title = ClientContext.getString(getMaster());
                        new TimeoutWarning(parent, title, null).start();
                        break;
                        
                    case ReflectMonitor.CANCELED:
                        break;
                }
                
                //
                // Block を解除する
                //
                setBusy(false);
            }
        };
        rm.addPropertyChangeListener(pl);
        
        //
        // Block し、メソッドの実行を開始する
        //
        setBusy(true);
        rm.start();      
        
    }
    
//    /**
//     * 撮影部位を検索する。
//     */
//    private void getRadLocation() {
//        
//        // DAO を生成する
//        final SqlMasterDao dao = (SqlMasterDao)SqlDaoFactory.create(this, "dao.master");
//        
//        // Worker を生成する
//        int maxEstimation = ClientContext.getInt("task.masterSearch.maxEstimation");
//        int delay = ClientContext.getInt("task.masterSearch.delay");
//        final TreatmentTask worker = new TreatmentTask(1, dao, maxEstimation/delay);
//        
//        // タスクタイマーを生成する
//        taskTimer = new javax.swing.Timer(TIMER_DELAY, new ActionListener() {
//            
//            public void actionPerformed(ActionEvent e) {
//                
//                worker.getCurrent();
//                
//                if (worker.isDone()) {
//                    
//                    taskTimer.stop();
//                    setBusy(false);
//                    
//                    if (dao.isNoError()) {
//                        List result = worker.getResult();
//                        tableModel.setObjectList(result);
//                        setItemCount(tableModel.getObjectCount());
//                        
//                    } else {
//                        Window parent = SwingUtilities.getWindowAncestor(TreatmentMaster.this);
//                        String message = dao.getErrorMessage();
//                        String title = ClientContext.getFrameTitle(getMaster());
//                        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.WARNING_MESSAGE);
//                    }
//                } else if (worker.isTimeOver()) {
//                    taskTimer.stop();
//                    setBusy(false);
//                    Window parent = SwingUtilities.getWindowAncestor(TreatmentMaster.this);
//                    String title = ClientContext.getString(getMaster());
//                    new TimeoutWarning(parent, title, null).start();
//                }
//            }
//        });
//        setBusy(true);
//        worker.start();
//        taskTimer.start();
//    }
    
    /**
     * 診療行為マスタ Table のレンダラー
     */
    protected final class TreatmentMasterRenderer extends MasterRenderer {
        
        private static final long serialVersionUID = -23933027994436326L;
        
        private final int CODE_COLUMN       = 0;
        private final int NAME_COLUMN       = 1;
        private final int KANA_COLUMN       = 2;
        private final int COST_FLAG_COLUMN  = 3;
        private final int COST_COLUMN       = 4;
        private final int INOUT_COLUMN      = 5;
        private final int OLD_COLUMN        = 6;
        private final int HOSP_CLINIC_COLUMN = 7;
        private final int START_COLUMN      = 8;
        private final int END_COLUMN      = 9;
        
        private String[] costFlags;
        private String[] inOutFlags;
        private String[] oldFlags;
        private String[] hospitalClinicFlags;
        
        public TreatmentMasterRenderer() {
        }
        
        public String[] getCostFlag() {
            return costFlags;
        }
        
        public void setCostFlag(String[] val) {
            costFlags = val;
        }
        
        public String[] getInOutFlag() {
            return inOutFlags;
        }
        
        public void setInOutFlag(String[] val) {
            inOutFlags = val;
        }
        
        public String[] getOldFlag() {
            return oldFlags;
        }
        
        public void setOldFlag(String[] val) {
            oldFlags = val;
        }
        
        public String[] getHospitalClinicFlag() {
            return hospitalClinicFlags;
        }
        
        public void setHospitalClinicFlag(String[] val) {
            hospitalClinicFlags = val;
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
            if (row % 2 == 0) {
                setBackground(getEvenColor());
            } else {
                setBackground(getOddColor());
            }
            
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            }
            JLabel label = (JLabel)c;
            
            if (value != null && value instanceof TreatmentEntry) {
                
                TreatmentEntry entry = (TreatmentEntry)value;
                
                String startDate = entry.getStartDate();
                String endDate = entry.getEndDate();
                
                setColor(label,startDate, endDate);
                
                String tmp = null;
                
                switch(col) {
                    
                    case CODE_COLUMN:
                        label.setText(entry.getCode());
                        break;
                        
                    case NAME_COLUMN:
                        label.setText(entry.getName());
                        break;
                        
                    case KANA_COLUMN:
                        label.setText(entry.getKana());
                        break;
                        
                    case COST_FLAG_COLUMN:
                        tmp = entry.getCostFlag();
                        if (tmp != null) {
                            try {
                                int index = Integer.parseInt(tmp);
                                label.setText(costFlags[index]);
                            } catch (Exception e) {
                                label.setText("");
                            }
                        } else {
                            label.setText("");
                        }
                        break;
                        
                    case COST_COLUMN:
                        label.setText(entry.getCost());
                        break;
                        
                    case INOUT_COLUMN:
                        tmp = entry.getInOutFlag();
                        if (tmp != null) {
                            try {
                                int index = Integer.parseInt(tmp);
                                label.setText(inOutFlags[index]);
                            } catch (Exception e) {
                                label.setText("");
                            }
                        } else {
                            label.setText("");
                        }
                        break;
                        
                    case OLD_COLUMN:
                        tmp = entry.getOldFlag();
                        if (tmp != null) {
                            try {
                                int index = Integer.parseInt(tmp);
                                label.setText(oldFlags[index]);
                            } catch (Exception e) {
                                label.setText("");
                            }
                        } else {
                            label.setText("");
                        }
                        break;
                        
                    case HOSP_CLINIC_COLUMN:
                        tmp = entry.getHospitalClinicFlag();
                        if (tmp != null) {
                            try {
                                int index = Integer.parseInt(tmp);
                                label.setText(hospitalClinicFlags[index]);
                            } catch (Exception e) {
                                label.setText("");
                            }
                        } else {
                            label.setText("");
                        }
                        break;
                        
                    case START_COLUMN:
                        if (startDate.startsWith("0")) {
                            label.setText("");
                        } else {
                            label.setText(startDate);
                        }
                        break;
                        
                    case END_COLUMN:
                        if (endDate.startsWith("9")) {
                            label.setText("");
                        } else {
                            label.setText(endDate);
                        }
                        break;
                }
                
            } else {
                //label.setBackground(Color.white);
                label.setText(value == null ? "" : value.toString());
            }
            return c;
        }
    }
    
//    /**
//     * 検索タスククラス。
//     */
//    protected class TreatmentTask extends AbstractInfiniteTask {
//        
//        private SqlMasterDao dao;
//        private int mode;
//        private List result;
//        
//        public TreatmentTask(int mode, SqlMasterDao dao, int taskLength) {
//            this.mode = mode;
//            this.dao = dao;
//            setTaskLength(taskLength);
//        }
//        
//        protected List getResult() {
//            return result;
//        }
//        
//        protected void doTask() {
//            
//            switch (mode) {
//                case 0:
//                    result = dao.getByClaimClass(master, searchClass, sortBy, order);
//                    break;
//                case 1:
//                    result = dao.getRadLocation(master, sortBy, order);
//                    break;
//            }
//            setDone(true);
//        }
//    }
}
