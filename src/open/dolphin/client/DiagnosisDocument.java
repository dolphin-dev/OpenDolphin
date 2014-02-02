/*
 * DiagnosisDocument.java
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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;

import open.dolphin.dao.*;
import open.dolphin.exception.*;
import open.dolphin.infomodel.DocInfo;
import open.dolphin.infomodel.ID;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.ModuleInfo;
import open.dolphin.infomodel.RegisteredDiagnosisModule;
import open.dolphin.message.*;
import open.dolphin.message.MessageBuilder;
import open.dolphin.order.*;
import open.dolphin.plugin.event.ClaimMessageEvent;
import open.dolphin.plugin.event.ClaimMessageListener;
import open.dolphin.project.*;
import open.dolphin.table.*;
import open.dolphin.util.*;

import java.beans.*;
import java.util.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;

/**
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc. 
 */
public final class DiagnosisDocument extends DefaultChartDocument 
implements DragGestureListener, DropTargetListener, DragSourceListener,PropertyChangeListener {
    
    // 傷病名テーブルのカラム名
    private static final String[] diagnosisColumnNames;
    static {
        diagnosisColumnNames = ClientContext.getStringArray("diagnosis.columnNames");
    }
    
    // 傷病名テーブルのカラム番号
    private final int DIAGNOSIS_COL         = 0;
    private final int CATEGORY_COL          = 1;
    private final int OUTCOME_COL           = 2;
    //private final int FIRST_ENCOUNTER_COL   = 3;
    private final int START_DATE_COL        = 3;
    private final int END_DATE_COL          = 4;

    // 転帰リスト
    private static final String[] outcomeList;
    static {
        outcomeList = ClientContext.getStringArray("diagnosis.outcomeList");
        outcomeList[0] = null;
    }
      
    // 抽出期間名リスト
    private static final String[] periodList = ClientContext.getStringArray("filter.combo.periodName");
    
    // 抽出期間値リスト
    private static final String[] periodValueList = ClientContext.getStringArray("filter.combo.periodValue");
    
    // GUI コンポーネント
    private static final String RESOURCE_BASE       = "/open/dolphin/resources/images/";
    private static final String DELETE_BUTTON_IMAGE = "Delete24.gif";
    private static final String NEW_BUTTON_IMAGE  = "New24.gif";
    
    private JTextField countField;              // 件数フィールド
    
    private JButton deleteNewButton;            // 削除ボタン
    
    private JButton openEditorButton;           // 新規ボタン
    
    private JButton deleteButton;               // 削除ボタン
    
    private JTable newDiagTable;                // 新規病名テーブル
    
    private ObjectTableModel newDiagTableModel;
    
    private JTable diagTable;                   // 病歴テーブル
    
    private ObjectTableModel tableModel;
    
    private JComboBox extractionCombo;          // 抽出期間コンボ
    
    private DragSource dragSource;
    
    // Properties
    private boolean dirty;
    
    private int newDiagnosisCount;
    
    private int diagnosisCount;
    
    private boolean editable = true;
    
    // 修正データコントロール
    private int modifyRow = -1;
        
    /** Creates new DiagnosisDocument */
    public DiagnosisDocument() {
    }

    public void start() {
        
        JPanel p1 = createButtonPanel();
        JPanel p2 = createNewDiagPanel();
        JPanel p3 = new JPanel(new BorderLayout(0, 7));
        p3.add(p1, BorderLayout.NORTH);
        p3.add(p2, BorderLayout.CENTER);
        p3.setBorder(BorderFactory.createTitledBorder("新規傷病名"));
        
        JPanel p4 = createButtonPanel2();
        JPanel p5 = createDignosisPanel();
        JPanel p6 = createFilterPanel();
        JPanel p7 = new JPanel(new BorderLayout(0, 7));
        p7.add(p4, BorderLayout.NORTH);
        p7.add(p5, BorderLayout.CENTER);
        p7.add(p6, BorderLayout.SOUTH);
        p7.setBorder(BorderFactory.createTitledBorder("傷病歴"));
        
        // Layouts        
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(p3);
        add(Box.createVerticalStrut(11));
        add(p7);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
               
        // 傷病歴表示
        getDiagHistory(getFilterDate(0));
        
        // State
        enter();
    }
    
    public boolean isDirty() {
        return dirty;
    }
    
    private void setDirty(boolean newDirty) {
        boolean oldDirty = dirty;
        dirty = newDirty;
        
        if (dirty != oldDirty) {
            controlMenu();
        }
    }
    
    public void enter() {
        super.enter();
        controlMenu();
    }    
    
    protected void controlMenu() {
        super.controlMenu();
        ChartMediator mediator = ((ChartPlugin)context).getChartMediator();
        mediator.saveKarteAction.setEnabled(dirty);
    }    
    
    public int getNewDiagnosisCount() {
        return newDiagnosisCount;
    }
    
    public void setNewDiagnosisCount(int n) {
        newDiagnosisCount = n;
        boolean b = n > 0 ? true : false;
        setDirty(b);
    }
    
    public int getDiagnosisCount() {
        return diagnosisCount;
    }
    
    public void setDiagnosisCount(int n) {
        diagnosisCount = n;
        try {
            String val = String.valueOf(diagnosisCount);
            countField.setText(val);
        } catch (RuntimeException e) {
            countField.setText("");
        }
    }    
    
    public boolean isEditable() {
        return editable;
    }
    
    public void setEditable(boolean b) {
        editable = b;
        
        if (! editable) {
            deleteNewButton.setEnabled(false);
            deleteButton.setEnabled(false);
            openEditorButton.setEnabled(false);
            extractionCombo.setEnabled(false);
        }
    }
    
    /**
     * 新規傷病名テーブルに使用するコントロールボタンパネルを返す
     */
    private JPanel createButtonPanel() {
        
        // 削除ボタン
        deleteNewButton = new JButton(createImageIcon(DELETE_BUTTON_IMAGE));
        deleteNewButton.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                deleteNew();
            }
        });
        deleteNewButton.setEnabled(false);
        
        // 新規登録ボタン        
        openEditorButton = new JButton(createImageIcon(NEW_BUTTON_IMAGE));
        openEditorButton.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                openEditor2();
           }
        });
        
        // Depends on readOnly prop
        openEditorButton.setEnabled(! isReadOnly());
        
        // ボタンパネル
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.add(Box.createHorizontalGlue());
        p.add(deleteNewButton);
        p.add(Box.createHorizontalStrut(5));
        p.add(openEditorButton);
        p.add(Box.createHorizontalStrut(7));
        return p;
    }
    
    /**
     * ImageIcon を返す
     */    
    private ImageIcon createImageIcon(String name) {
        String res = RESOURCE_BASE + name;
        return new ImageIcon(this.getClass().getResource(res));
    }  
    
    /**
     * 新規傷病名テーブル用のパネルを返す
     */
    private JPanel createNewDiagPanel() {
               
        // 新規登録テーブル
        newDiagTableModel = new ObjectTableModel(diagnosisColumnNames, 7) {
            
            // 編集不可
            public boolean isCellEditable(int row, int col) {
                return false;
            }
            
            // オブジェクトをテーブルに表示する
            public Object getValueAt(int row, int col) {
                
                Object o = getObject(row);
                if (o == null) {
                    return null;
                }
                
                RegisteredDiagnosisModule module = (RegisteredDiagnosisModule)o;
                String ret = null;
                
                switch (col) {
                    
                    case DIAGNOSIS_COL:
                        ret = module.getDiagnosis();
                        break;
                        
                    case CATEGORY_COL:
                        String categories = module.getCategory();
                        if (categories != null) {
                            ret = categories;
                        }
                        ret = ret != null ? (String)MMLTable.getDiagnosisCategoryDesc((String)ret) : null;
                        break;
                        
                    case OUTCOME_COL:
                        ret = module.getOutcome();
                        ret = ret != null ? (String)MMLTable.getDiagnosisOutcomeDesc((String)ret) : null;
                        break;
                        
                    /*case FIRST_ENCOUNTER_COL:
                        ret = module.getFirstEncounterDate();
                        break;*/
                        
                    case START_DATE_COL:
                        ret = module.getStartDate();
                        break;
                        
                    case END_DATE_COL:
                        ret = module.getEndDate();
                        break;
                }
                
                return ret;
            }
        };
        
        // Sort 機能を加える
        TableSorter s = new TableSorter(newDiagTableModel);
        newDiagTable = new JTable(s);
        s.addMouseListenerToHeaderInTable(newDiagTable);
        
        // Selection を設定する
        newDiagTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        newDiagTable.setRowSelectionAllowed(true);
                
        // Mouse Click 処理を登録する
        newDiagTable.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e) {
                
                if(! editable) {
                    return;
                }
                
                switch (e.getClickCount()) {
                 
                    case 1:
                        // 削除ボタンをコントロール
                        controlDeleteNewButton();
                        break;
                        
                    case 2:
                        // 選択された行をエディタで編集
                        modify();
                        break;
                    
                }
            }
        });

        // 2003-10-30 licenseCode による制御
        // RedaOnly でなければ DnD を受付
        if (! isReadOnly()) {
        
            // Table を DragTarget, thisをリスナに設定する
            dragSource = new DragSource();
            dragSource.createDefaultDragGestureRecognizer(newDiagTable, DnDConstants.ACTION_COPY_OR_MOVE, this);

            // Table を DropTarget, thisをリスナに設定する
            new DropTarget(newDiagTable, this);
        }
        
        // Layout
        JScrollPane scroller = new JScrollPane(newDiagTable, 
                                   JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                   JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        JPanel p = new JPanel(new BorderLayout());
        p.add(scroller, BorderLayout.CENTER);    
        p.setPreferredSize(new Dimension(400, 150));
        return p;      
    }
    
    
        //////////////   Drag Support //////////////////
    
    public void dragGestureRecognized(DragGestureEvent event) {
        
        if (! editable) {
            return;
        }
        
        // 選択されている行を得る
        int row = newDiagTable.getSelectedRow();
        
        // テーブルモデルからオブジェクトを得る
        RegisteredDiagnosisModule o = (RegisteredDiagnosisModule)newDiagTableModel.getObject(row);
        if (o == null) {
            return;
        }
        
        // InfoModelTransferable を生成する
        Transferable t = new InfoModelTransferable(o);
        Cursor cursor = DragSource.DefaultCopyDrop;
        int action = event.getDragAction();
        if (action == DnDConstants.ACTION_MOVE) {
            cursor = DragSource.DefaultMoveDrop;
        }

        // ドラッグを開始する
        dragSource.startDrag(event, cursor, t, this);
    }

    public void dragDropEnd(DragSourceDropEvent event) { 
        
        if (! event.getDropSuccess() || event.getDropAction() == DnDConstants.ACTION_COPY) {
            return;
        }
                
        /*int action = event.getDropAction();
        String actionSt = action == DnDConstants.ACTION_MOVE ? "MoveAction" : "CopyAction";
        String resultSt = event.getDropSuccess() ? "DnD succeeded" : "DnD failed";
        
        System.out.println("This is the drag source: " + resultSt + " " + actionSt);
        */
    }

    public void dragEnter(DragSourceDragEvent event) {
    }

    public void dragOver(DragSourceDragEvent event) {
    }
    
    public void dragExit(DragSourceEvent event) {
    }    

    public void dropActionChanged ( DragSourceDragEvent event) {
    }   
    
    //////////// Drop Support ////////////////
        
    public void drop(DropTargetDropEvent event) {
   
        
        if ( ! editable || (! isDropAcceptable(event))) {
            event.rejectDrop();
            setDropTargetBorder(false);
            event.getDropTargetContext().dropComplete(true);
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        
        // Transferableを得る
        final Transferable tr = event.getTransferable();
        
        // Drop 位置を得る
        final Point loc = event.getLocation();
        
        // 早めに DnD を完了させた方が GUI の張り付きがない
        event.acceptDrop(DnDConstants.ACTION_COPY);
        event.getDropTargetContext().dropComplete(true);
        setDropTargetBorder(false);
        
        // スレッドで実際のドロップ処理をする
        Runnable r = new Runnable() {
            public void run() {
                boolean ok = doDrop(tr, loc);
            }
        };
        Thread t = new Thread(r);
        t.start();
    }
     
    /**
     * Drop 処理を行なう
     */
    private boolean doDrop(Transferable tr, Point loc) {
        
        boolean ret = false;
        
        // StampTreeNode でなければリターン
        if (! tr.isDataFlavorSupported(StampTreeTransferable.stampTreeNodeFlavor)) {
            Toolkit.getDefaultToolkit().beep();
            return ret;
        }
        
        // 傷病名スタンプでなければリターン
        ModuleInfo stampInfo = null;
        try {
            StampTreeNode node = (StampTreeNode)tr.getTransferData(StampTreeTransferable.stampTreeNodeFlavor);
            stampInfo = (ModuleInfo)node.getStampInfo();
            if (! stampInfo.getEntity().equals("diagnosis")) {
                throw new DolphinException("Not diagnosis");
            }
            
        } catch (Exception ue) {
            Toolkit.getDefaultToolkit().beep();
            return ret;
        }

        // Drop 位置がテーブル範囲になければリターン
        int row = newDiagTable.rowAtPoint(loc);
        if (row == -1 ) {
            Toolkit.getDefaultToolkit().beep();
            return ret;
        }
        
        // エディタかスタンプに応じて分岐する
        if (stampInfo.isSerialized()) {
            applySerializedStamp(stampInfo);
        
        } else {
            openEditor();
        }
        
        ret = true;
        return ret;
    }
    
    /**
     * 傷病名スタンプを処理する
     */
    private void applySerializedStamp(ModuleInfo stampInfo) {
        
        // 病名スタンプを DB から取得してテーブルへ表示する
        startAnimation();

        String rdn = stampInfo.getStampId();
        String category = stampInfo.getEntity();
        String userId = Project.getUserId();
        
        RegisteredDiagnosisModule module = null;
        SqlStampDao dao = (SqlStampDao)SqlDaoFactory.create(DiagnosisDocument.this, "dao.stamp");
        module = (RegisteredDiagnosisModule)dao.getStamp(userId, category, rdn);

        if (module != null) {
            GregorianCalendar gc = new GregorianCalendar();
            String today = MMLDate.getDate(gc);
            module.setFirstEncounterDate(today);
            newDiagTableModel.addRow(module);
            setNewDiagnosisCount(newDiagTableModel.getObjectCount());
        }

        stopAnimation();
    }
    
    public boolean isDragAcceptable(DropTargetDragEvent evt) {
        return (evt.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) != 0;
    }
    
    public boolean isDropAcceptable(DropTargetDropEvent evt) {
        return (evt.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) != 0;
    }        

    /** DropTaregetListener interface method */
    public void dragEnter(DropTargetDragEvent e) {
        if (! isDragAcceptable(e)) {
            e.rejectDrag();
        }
    }

    /** DropTaregetListener interface method */
    public void dragExit(DropTargetEvent e) {
        setDropTargetBorder(false);
    }

    /** DropTaregetListener interface method */
    public void dragOver(DropTargetDragEvent e) { 
        if (isDragAcceptable(e)) {
            setDropTargetBorder(true);
        }
    }

    /** DropTaregetListener interface method */
    public void dropActionChanged(DropTargetDragEvent e) {
        if (! isDragAcceptable(e)) {
            e.rejectDrag();
        }
    }
    
    /**
     * Drop ターゲットの境界を表示する
     */
    private void setDropTargetBorder(final boolean b) {
        Color c = b ? DesignFactory.getDropOkColor() : this.getBackground();
        newDiagTable.setBorder(BorderFactory.createLineBorder(c, 2));
    }  
    
    private void startAnimation() {
        
        SwingUtilities.invokeLater(new Runnable() {
                    
            public void run() {
                ChartPlugin ct = (ChartPlugin)context;
                StatusPanel sp = ct.getStatusPanel();
                sp.start("スタンプを取得しています...");
            }
        });
    }
    
    private void stopAnimation() {
        
        SwingUtilities.invokeLater(new Runnable() {
                    
            public void run() {
                ChartPlugin ct = (ChartPlugin)context;
                StatusPanel sp = ct.getStatusPanel();
                sp.stop("");                   
            }
        });
    }   
    
    /**
     * 傷病名エディタを EventDispatch から開く
     */
    private void openEditor2() {
        
        StampEditorDialog stampEditor = getEditor();
        
        if (stampEditor == null) {
            return;
        }
        
        stampEditor.setOkButtonText("登 録");
        stampEditor.addPropertyChangeListener("value", this);
        stampEditor.setValue(null);
        Thread t = new Thread((Runnable)stampEditor);
        t.start();
    }
     
    /**
     * 傷病名エディタをバックグランドスレッドから開く
     */
    private void openEditor() {
                
        // StampEditor を起動する
        final StampEditorDialog stampEditor = getEditor();
        
        if (stampEditor == null) {
            return;
        }
        
        stampEditor.setOkButtonText("登 録");
        stampEditor.addPropertyChangeListener("value", this);
        stampEditor.setValue(null);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                stampEditor.run();
            }
        });
    }
    
    /**
     * 新規に追加された傷病名を変更する
     */
    private void modify() {
        
        // ダブルクリックされた行のオブジェクトを取得する
        modifyRow = newDiagTable.getSelectedRow();
        
        RegisteredDiagnosisModule o = (RegisteredDiagnosisModule)newDiagTableModel.getObject(modifyRow);
        
        if (o == null) {
            modifyRow = -1;
            return;
        }
        
        StampEditorDialog stampEditor = getEditor();
        
        if (stampEditor == null) {
            modifyRow = -1;
            return;
        }
        
        Object[] obj = new Object[1];
        obj[0] = o;
        
        stampEditor.setOkButtonText("登 録");
        stampEditor.addPropertyChangeListener("value", this);
        stampEditor.setValue(obj);
        Thread t = new Thread((Runnable)stampEditor);
        t.start();
    }
    
    /**
     * 新規追加傷病名を削除する
     */
    private void deleteNew() {
        
        int row = newDiagTable.getSelectedRow();
        newDiagTableModel.deleteRow(row);
        setNewDiagnosisCount(newDiagTableModel.getObjectCount());
    }
        
    /**
     * 削除ボタンコントロール
     */
    private void controlDeleteNewButton() {
        
        int row = newDiagTable.getSelectedRow();
        
        boolean b1 = newDiagTableModel.isValidRow(row);
        boolean b2 = deleteNewButton.isEnabled();
        
        if (b1 && !b2) {
            deleteNewButton.setEnabled(true);
            
        } else if (! b1 && b2) {
            deleteNewButton.setEnabled(false);
        }
    }
    
    /**
     * 傷病名エディタを返す
     */
    private StampEditorDialog getEditor() {
        
        StampEditorDialog stampEditor = null;
        try {
            stampEditor = new StampEditorDialog("diagnosis");
        
        } catch (DolphinException e) {
            System.out.println("DolphinException at stampInfoDropped: " + e.toString());
            stampEditor = null;
        }
        
        return stampEditor;
    }
    
    /**
     * 傷病名エディタからデータを受け取りテーブルへ追加する
     */
    public void propertyChange(PropertyChangeEvent e) {

        ArrayList list = (ArrayList)e.getNewValue();
        if (list == null) {
            return;
        }

        int len = list.size();
        
        if (modifyRow > -1) {
            RegisteredDiagnosisModule module = (RegisteredDiagnosisModule)list.get(0);
            newDiagTableModel.insertRow(modifyRow, module);
            newDiagTableModel.deleteRow(modifyRow+1);
            modifyRow = -1;
            
        } else {
        
            // エディタから受け取ったデータを追加
            for (int i = len - 1; i > -1; i--) {
                RegisteredDiagnosisModule module = (RegisteredDiagnosisModule)list.get(i);
                newDiagTableModel.insertRow(0, module);
            }
        }
        
        // 新規個数を設定する
        setNewDiagnosisCount(newDiagTableModel.getObjectCount());
    }

    /**
     * 新規傷病名を保存する
     */
    public void save() {  
        
        if (! dirty) {
            return;
        }
                
        ID masterID = Project.getMasterId(context.getPatient().getId());
        if (masterID == null) {
            // 2003-09-2
            //return;
        }
        
        final boolean sendMML = (Project.getSendMML() && masterID != null) ? true : false;
        final boolean sendDiagnosis = Project.getSendDiagnosis() && ((ChartPlugin)context).getCLAIMListener() != null ? true : false;
        
        // Show and get saving params
        SaveParams params = new SaveParams(sendMML);
        params.setTitle("病名登録");
        params.setPrintCount(-1);  // disable print
        
        // 受付から来ている診療科（PatientVisit が保持）をセットする
        String dept = ((ChartPlugin)context).getPatientVisit().getDepartment();
        params.setDepartment(dept);
        
        SaveDialog sd = (SaveDialog)Project.createSaveDialog(getParentFrame(),params);
        sd.show();
        params = sd.getValue();
        if (params == null) {
            sd.dispose();
            return;
        }
        sd.dispose();
        
        // continue to save
        // Create confirm date(YYYY-MM-DDTHH-MM-SS)
        GregorianCalendar gc = new GregorianCalendar();
        String confirmDate = MMLDate.getDateTime(gc);
        saveNewData(sendMML, sendDiagnosis, masterID, params, confirmDate);
        saveModifiedData(confirmDate);
        
        // Uneditable
        setEditable(false);
        
        // set the flag
        dirty = false;
        controlMenu();   
    }
           
    /**
     * 新規に入力された傷病名を保存する
     */
    private void saveNewData(final boolean sendMML, final boolean sendClaim, ID masterID, SaveParams params, final String confirmDate)  {
        
        if (newDiagTableModel.getObjectCount() == 0) {
            return;
        }
        
        Object[] modules = newDiagTableModel.getObjectList().toArray();
        int moduleCount = modules.length;
		final RegisteredDiagnosisModule[] rd = new RegisteredDiagnosisModule[moduleCount];
        for (int i = 0; i < moduleCount; i++) {
        	rd[i] = (RegisteredDiagnosisModule)modules[i];
        }
        
        // Allocate docinfo
        final DocInfo[] infos = new DocInfo[moduleCount];
        DocInfo docInfo = null;
                
        for (int i = 0; i < moduleCount; i++) {
            
			docInfo = new DocInfo();
			docInfo.setDocId(Project.createUUID());
			docInfo.setTitle("病名登録");
			docInfo.setFirstConfirmDate(confirmDate);
			docInfo.setConfirmDate(confirmDate);
			docInfo.setCreator(Project.getCreatorInfo());
            infos[i] = docInfo;
        }
       
        final SqlRDSaverDao dao = (SqlRDSaverDao)SqlDaoFactory.create(this, "dao.rdSaver");
        dao.setPid(context.getPatient().getId());
        dao.setDocInfo(infos);
        dao.setRegisteredDiagnosis(modules);
        
        final StatusPanel statusPanel = ((ChartPlugin)context).getStatusPanel();
        
        Runnable r = new Runnable() {
        	
        	public void run() {
        		
				SwingUtilities.invokeLater(new Runnable() {

					public void run() {  
						statusPanel.start("保存しています...");
					}
				});

				boolean ret = dao.save();
				
				if (sendClaim) {
					DiseaseHelper dhl = new DiseaseHelper();
					dhl.setPatientId(context.getPatient().getId());
					dhl.setConfirmDate(confirmDate);
					dhl.setCreator(Project.getCreatorInfo());
					dhl.setRegisteredDiagnosisModule(rd);
					dhl.setDocInfo(infos);
					dhl.setGroupId(Project.createUUID());
					DmlMessageBuilder builder = new DmlMessageBuilder();
					String dml = builder.build((IInfoModel)dhl);
					debug(dml);
					
					MessageBuilder mb = new MessageBuilder();
					mb.setTemplateFile("disease.vm");
					String claimMessage = mb.build(dml);
					debug(claimMessage);
					
					ClaimMessageEvent event = new ClaimMessageEvent(this);
					event.setPatientId(context.getPatient().getId());
					event.setPatientName(context.getPatient().getId());
					event.setPatientSex(context.getPatient().getId());
					event.setTitle("病名登録");
					event.setClaimInstance(claimMessage);
					event.setConfirmDate(confirmDate);
					ClaimMessageListener claimListener = ((ChartPlugin)context).getCLAIMListener();
					claimListener.claimMessageEvent(event);
				}
        		
				SwingUtilities.invokeLater(new Runnable() {

					public void run() {
						statusPanel.stop("");
					}
				});
        	}
        };
        
        Thread t = new Thread(r);
        t.start();
    }
    
    /**
     * 転帰と終了日が入力された傷病名を更新する
     */
    private void saveModifiedData(final String confirmDate) {
        
        final ArrayList results = getModifiedData();
        if (results == null) {
            return;
        }
        
        final SqlRDSaverDao dao = (SqlRDSaverDao)SqlDaoFactory.create(this, "dao.rdSaver");
        
		final StatusPanel statusPanel = ((ChartPlugin)context).getStatusPanel();
        
		Runnable r = new Runnable() {
        	
			public void run() {
        		
				SwingUtilities.invokeLater(new Runnable() {

					public void run() {  
						statusPanel.start("保存しています...");
					}
				});

				boolean ret = dao.update(results, confirmDate);
        		
				SwingUtilities.invokeLater(new Runnable() {

					public void run() {
						statusPanel.stop("");
					}
				});
			}
		};
        
		Thread t = new Thread(r);
		t.start();          
    }
            
    /**
     * 指定期間以降の傷病名を検索してテーブルへ表示する
     */
    private void getDiagHistory(final String past) {
    	
		final StatusPanel statusPanel = ((ChartPlugin)context).getStatusPanel();
        
		Runnable r = new Runnable() {
        	
			public void run() {
        		
				SwingUtilities.invokeLater(new Runnable() {

					public void run() {  
						statusPanel.start("検索しています...");
					}
				});

				final ArrayList results = context.getDiagnosisHistory(context.getPatient().getId(), past);
        		
				SwingUtilities.invokeLater(new Runnable() {

					public void run() {
						
						statusPanel.stop("");
						
						if (results != null) {
							tableModel.setObjectList(results);
						}
        
						setDiagnosisCount(tableModel.getObjectCount());
					}
				});
			}
		};
        
		Thread t = new Thread(r);
		t.start();
    }
    
    /**
     * 傷病歴テーブル
     */
    private JPanel createDignosisPanel() {
        
        tableModel = new ObjectTableModel(diagnosisColumnNames, 20) {
            
            // 転帰と終了日のみ編集可能とする
            public boolean isCellEditable(int row, int col) {
                
                // licenseCode && 保存後 (editable) で制御
                if ( isReadOnly() || (! editable) ) {
                    return false;
                }
                
                return ( isValidRow(row) && (col == OUTCOME_COL || col == END_DATE_COL) ) ? true : false;
            }
            
            // オブジェクトを表示する
            public Object getValueAt(int row, int col) {
                
                String ret = null;
                
                DiagnosisEntry entry = (DiagnosisEntry)getObject(row);
                
                if (entry == null) {
                    return ret;
                }
                
                switch (col) {
                    
                    case DIAGNOSIS_COL:
                        ret = entry.getDiagnosis();
                        break;
                        
                    case CATEGORY_COL:
                        ret = entry.getCategory();
                        ret = ret != null ? (String)MMLTable.getDiagnosisCategoryDesc((String)ret) : null;
                        break;
                        
                    case OUTCOME_COL:
                        ret = entry.getOutcome();
                        ret = ret != null ? (String)MMLTable.getDiagnosisOutcomeDesc((String)ret) : null;
                        break;
                        
                    /*case FIRST_ENCOUNTER_COL:
                        ret = entry.getFirstEncounterDate();
                        break;*/
                        
                    case START_DATE_COL:
                        // 2003-11
                        ret = entry.getStartDate();
                        if (ret == null) {
                            ret = entry.getFirstEncounterDate();
                        }
                        break;
                        
                    case END_DATE_COL:
                        ret = entry.getEndDate();
                        break;    
                }
                return ret;
            }
            
            public void setValueAt(Object value, int row, int col) {
                
                DiagnosisEntry entry = (DiagnosisEntry)getObject(row);
                
                if (entry == null) {
                    return;
                }
                
                switch (col) {
                    
                    case DIAGNOSIS_COL:
                        break;
                        
                    case CATEGORY_COL:
                        break;
                        
                    case OUTCOME_COL:
                        if (value == null) {
                            
                            entry.setOutcome(null);
                            entry.setEndDate(null);
                            
                        } else {
                            
                            String val = (String)MMLTable.getDiagnosisOutcomeValue((String)value);
                            entry.setOutcome(val);
                            
                            val = entry.getEndDate();
                            if (val == null) {
                                GregorianCalendar gc = new GregorianCalendar();
                                String today = MMLDate.getDate(gc);
                                entry.setEndDate(today);
                            }  
                         }
                        break;
                        
                    //case 4:
                        //entry.setStartDate((String)value);
                        //break;
                        
                    case END_DATE_COL:
                        if (value != null && ((String)value).trim().equals("") ) {
                            entry.setEndDate((String)value);
                        }
                        break; 
                }
                
                // Set the modifyed flag true
                entry.setModified(true);
                
                fireTableRowsUpdated(row, row);
                
                setDirty(true);
                
            }
        };
        
        // ソート機能をつける
        TableSorter s = new TableSorter(tableModel);
        diagTable = new JTable(s);
        s.addMouseListenerToHeaderInTable(diagTable);
        
        diagTable.setSurrendersFocusOnKeystroke(true);
        
        // 疾患終了日カラム
        TableColumn column = diagTable.getColumnModel().getColumn(END_DATE_COL);
        column.setCellEditor (new IMECellEditor (new JTextField(), 1, false));
        
        // 行選択
        diagTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        diagTable.setRowSelectionAllowed(true);
        ListSelectionModel m = diagTable.getSelectionModel();
        m.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false) {
                    // 削除ボタンをコントロールする
                    // licenseCode 制御を追加
                    if ( isReadOnly() || (! editable) ) {
                        return;
                    }
                    
                    int row = diagTable.getSelectedRow();
                    boolean b1 = tableModel.isValidRow(row);
                    boolean b2 =  deleteButton.isEnabled();
                    if (b1 && ! b2) {
                        deleteButton.setEnabled(true);
                    
                    } else if (! b1 && b2) {
                        deleteButton.setEnabled(false);
                    }
                }
            }
        });

        // Outcome comboBox 入力を設定する
        JComboBox outcomeCombo = new JComboBox(outcomeList);
        column = diagTable.getColumnModel().getColumn(OUTCOME_COL);
        column.setCellEditor(new DefaultCellEditor(outcomeCombo));
        
        // Layout
        JScrollPane scroller = new JScrollPane(diagTable, 
                                   JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                   JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        JPanel p = new JPanel(new BorderLayout());
        p.add(scroller, BorderLayout.CENTER);
        return p;
    }
    
    /**
     * 修正されたデータをリストにして返す
     */
    private ArrayList getModifiedData() {
        
        int count = tableModel.getObjectCount();
        if (count == 0) {
            return null;
        }
        
        ArrayList list = new ArrayList();
        for (int i = 0; i < count; i++) {
            DiagnosisEntry entry = (DiagnosisEntry)tableModel.getObject(i);
            if (entry.isModified()) {
                list.add(entry);
            }
        }
        
        return list.size() > 0 ? list : null;        
    }
    
    /**
     * 選択された行のデータを削除する
     */    
    private void delete() {
     
        int row = diagTable.getSelectedRow();
        
        DiagnosisEntry entry = (DiagnosisEntry)tableModel.getObject(row);
        if (entry != null) {
            SqlRDSaverDao dao = (SqlRDSaverDao)SqlDaoFactory.create(this, "dao.rdSaver");
            boolean ret = dao.delete(entry);
            if (ret) {
                tableModel.deleteRow(row);
                setDiagnosisCount(tableModel.getObjectCount());
            }
        }
    }
        
    /**
     * 削除ボタンパネルを返す
     */    
    private JPanel createButtonPanel2() {
        
        // 削除ボタン
        deleteButton = new JButton(createImageIcon(DELETE_BUTTON_IMAGE));
        //deleteButton.setMnemonic('D');
        deleteButton.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                delete();
            }
        });
        deleteButton.setEnabled(false);
        
        // ボタンパネル
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.add(Box.createHorizontalGlue());
        p.add(deleteButton);
        p.add(Box.createHorizontalStrut(7));
        return p;
    }
    
    /**
     * 抽出期間パネルを返す
     */
    private JPanel createFilterPanel() {

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.add(Box.createHorizontalStrut(7));

        // 抽出期間コンボボックス
        p.add(new JLabel("抽出期間 過去： "));        
        extractionCombo = new JComboBox(periodList);
        Dimension dim = new Dimension(80, 20);
        extractionCombo.setPreferredSize(dim);
        extractionCombo.setMaximumSize(dim);
        extractionCombo.setMinimumSize(dim);
        extractionCombo.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    int index = extractionCombo.getSelectedIndex();
                    String s = getFilterDate(index);
                    
                    getDiagHistory(s);
                }
            }
        });
        p.add(extractionCombo);

        p.add(Box.createHorizontalGlue());

        // 件数フィールド
        p.add (new JLabel("件数： "));        
        countField = new JTextField();
        dim = new Dimension(40, 20);
        countField.setPreferredSize(dim);
        countField.setMaximumSize(dim);
        countField.setMinimumSize(dim);
        countField.setEditable(false);
        p.add(countField);

        p.add(Box.createHorizontalStrut(7));
        
        p.setBorder(BorderFactory.createEmptyBorder(0, 0, 7, 0));
        
        return p;
    }
    
    private String getFilterDate(int index) {

        index *= 2;
        String flag = periodValueList[index++];
        String val = periodValueList[index];
        int n = Integer.parseInt(val);
        
        GregorianCalendar today = new GregorianCalendar();
        
        if (flag.equals("YEAR")) {
            today.add(GregorianCalendar.YEAR, n);
        
        } else if (flag.equals("MONTH")) {
            today.add(GregorianCalendar.MONTH, n);
        
        } else if (flag.equals("DATE")) {
            today.add(GregorianCalendar.DATE, n);
        
        } else {
            //assert false : "Invalid Calendar Field: " + flag;
            System.out.println("Invalid Calendar Field: " + flag);
        }
        
        return MMLDate.getDate(today);
    }  
}