/*
 * StampTree.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
 * Copyright (C) 2001,2003,2004,2005 Digital Globe, Inc. All rights reserved.
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
import javax.swing.tree.*;
import javax.swing.event.*;

import open.dolphin.delegater.StampDelegater;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.ModuleModel;
import open.dolphin.infomodel.ModuleInfoBean;
import open.dolphin.infomodel.RegisteredDiagnosisModel;
import open.dolphin.infomodel.StampModel;
import open.dolphin.infomodel.TextStampModel;
import open.dolphin.project.*;
import open.dolphin.util.GUIDGenerator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.*;

/**
 * StampTree
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class StampTree extends JTree implements TreeModelListener {
    
    public static final String SELECTED_NODE_PROP = "selectedNodeProp";
    
    private static final long serialVersionUID = -4651151848166376384L;
    private static final int TOOLTIP_LENGTH = 35;
    private static final ImageIcon ASP_ICON = ClientContext.getImageIcon("move2_16.gif");
    private static final ImageIcon LOCAL_ICON = ClientContext.getImageIcon("move2_16.gif");
    private static final String NEW_FOLDER_NAME = "新規フォルダ";
    private static final String STAMP_SAVE_TASK_NAME = "スタンプ保存";
    
    /** ASP Tree かどうかのフラグ */
    private boolean asp;
    
    /** 個人用Treeかどうかのフラグ */
    private boolean userTree;
    
    /** StampBox */
    private StampBoxPlugin stampBox;
    
    /** DBから取得する時のTaskTimer */
    private javax.swing.Timer taskTimer;
    
    /**
     * StampTreeオブジェクトを生成する。
     *
     * @param model TreeModel
     */
    public StampTree(TreeModel model) {
        
        super(model);
        
        this.putClientProperty("JTree.lineStyle", "Angled"); // 水平及び垂直線を使用する
        this.setEditable(false); // ノード名を編集不可にする
        this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION); // Single Selection// にする
        
        this.setRootVisible(false);
        this.addMouseMotionListener(new MouseDragDetecter());
        
        //
        // デフォルトのセルレンダラーを置き換える
        //
        final TreeCellRenderer oldRenderer = this.getCellRenderer();
        TreeCellRenderer r = new TreeCellRenderer() {
            
            public Component getTreeCellRendererComponent(JTree tree,
                    Object value, boolean selected, boolean expanded,
                    boolean leaf, int row, boolean hasFocus) {
                
                Component c = oldRenderer.getTreeCellRendererComponent(tree,
                        value, selected, expanded, leaf, row, hasFocus);
                if (leaf && c instanceof JLabel) {
                    JLabel l = (JLabel) c;
                    Object o = ((StampTreeNode) value).getUserObject();
                    if (o instanceof ModuleInfoBean) {
                        
                        // 固有のアイコンを設定する
                        if (isAsp()) {
                            l.setIcon(ASP_ICON);
                        } else {
                            l.setIcon(LOCAL_ICON);
                        }
                        // ToolTips を設定する
                        l.setToolTipText(((ModuleInfoBean) o).getStampMemo());
                    }
                }
                return c;
            }
        };
        this.setCellRenderer(r);
        
        // Listens TreeModelEvent
        model.addTreeModelListener(this);
        
        // Enable ToolTips
        enableToolTips(true);
        
    }
    
    /**
     * このStampTreeのTreeInfoを返す。
     * @return Tree情報
     */
    public TreeInfo getTreeInfo() {
        StampTreeNode node = (StampTreeNode) this.getModel().getRoot();
        TreeInfo info = (TreeInfo)node.getUserObject();
        return info;
    }
    
    /**
     * このStampTreeのエンティティを返す。
     * @return エンティティ
     */
    public String getEntity() {
        return getTreeInfo().getEntity();
    }
    
    /**
     * このStampTreeの名前を返す。
     * @return 名前
     */
    public String getTreeName() {
        return getTreeInfo().getName();
    }
    
    /**
     * UserTreeかどうかを返す。
     * @return UserTreeの時true
     */
    public boolean isUserTree() {
        return userTree;
    }
    
    /**
     * UserTreeかどうかを設定する。
     * @param userTree UserTreeの時true
     */
    public void setUserTree(boolean userTree) {
        this.userTree = userTree;
    }
    
    /**
     * ASP提供Treeかどうかを返す。
     * @return ASP提供の時 true
     */
    public boolean isAsp() {
        return asp;
    }
    
    /**
     * ASP提供Treeかどうかを設定する。
     * @param asp ASP提供の時 true
     */
    public void setAsp(boolean asp) {
        this.asp = asp;
    }
    
    /**
     * Enable or disable tooltip
     */
    public void enableToolTips(boolean state) {
        
        ToolTipManager mgr = ToolTipManager.sharedInstance();
        if (state) {
            // Enable tooltips
            mgr.registerComponent(this);
            
        } else {
            mgr.unregisterComponent(this);
        }
    }
    
    /**
     * Set StampBox reference
     */
    public void setStampBox(StampBoxPlugin stampBox) {
        this.stampBox = stampBox;
    }
    
    /**
     * 選択されているノードを返す。
     */
    public StampTreeNode getSelectedNode() {
        return (StampTreeNode) this.getLastSelectedPathComponent();
    }
    
    /**
     * 引数のポイント位置のノードを返す。
     */
    public StampTreeNode getNode(Point p) {
        TreePath path = this.getPathForLocation(p.x, p.y);
        return (path != null)
        ? (StampTreeNode) path.getLastPathComponent()
        : null;
    }
    
    /**
     * このStampTreeにenter()する。
     */
    public void enter() {
    }
    
    /**
     * KartePaneから drop されたスタンプをツリーに加える。
     */
    public boolean addStamp(final ModuleModel droppedStamp, final StampTreeNode selected) {
        
        boolean ret = false;
        if (droppedStamp == null) {
            return ret;
        }
        
        //
        // Drop された Stamp の ModuleInfoを得る
        //
        ModuleInfoBean droppedInfo = droppedStamp.getModuleInfo();
        
        //
        // データベースへ droppedStamp のデータモデルを保存する
        //
        // Entityを生成する
        //
        StampModel stampModel = new StampModel();
        String stampId = GUIDGenerator.generate(stampModel);    // stampId
        stampModel.setId(stampId);
        stampModel.setUserId(Project.getUserModel().getId());   // userId
        stampModel.setEntity(droppedInfo.getEntity());          // entity
        stampModel.setStampBytes(getXMLBytes(droppedStamp.getModel())); // XML
        
        // Delegator を生成する
        final StampDelegater sdl = new StampDelegater();
        
        //
        // Tree に加える新しい StampInfo を生成する
        //
        final ModuleInfoBean info = new ModuleInfoBean();
        info.setStampName(droppedInfo.getStampName());      // オリジナル名
        info.setEntity(droppedInfo.getEntity());            // Entity
        info.setStampRole(droppedInfo.getStampRole());      // Role
        info.setStampMemo(constractToolTip(droppedStamp));  // Tooltip
        info.setStampId(stampId);                           // StampID
        
        // 保存タスクを生成する
        int maxEstimation = ClientContext.getInt("task.default.maxEstimation");
        int delay = ClientContext.getInt("task.default.delay");
        int taskLength = maxEstimation/delay;
        int decideToPopup = ClientContext.getInt("task.default.decideToPopup");
        int milisToPopup = ClientContext.getInt("task.default.milisToPopup");
        String saveMsg = ClientContext.getString("task.default.saveMessage");
        final StampTask worker = new StampTask(stampModel, sdl, taskLength);
        
        // ProgressMonitor を生成する
        final ProgressMonitor monitor = new ProgressMonitor(null, null, saveMsg, 0, taskLength);
        monitor.setProgress(0);
        monitor.setMillisToDecideToPopup(decideToPopup);
        monitor.setMillisToPopup(milisToPopup);
        
        // タスクタイマーを起動する
        taskTimer = new javax.swing.Timer(delay, new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                
                monitor.setProgress(worker.getCurrent());
                
                if (worker.isDone()) {
                    
                    //
                    // 終了処理を行う
                    //
                    taskTimer.stop();
                    monitor.close();
                    
                    // 保存処理が成功しているかどうかをチェックする
                    if (sdl.isNoError()) {
                        //
                        // 成功したら Tree に加える
                        //
                        addInfoToTree(info, selected);
                        
                    } else {
                        //
                        // エラーメッセージを表示する
                        //
                        warning(sdl.getErrorMessage());
                    }
                    
                } else if (worker.isTimeOver()) {
                    taskTimer.stop();
                    monitor.close();
                    String title = ClientContext.getString("stamptree.title");
                    new TimeoutWarning(null, title, null);
                }
            }
        });
        worker.start();
        taskTimer.start();
        return true;
    }
    
    /**
     * StampTree に新しいノードを加える。
     * @param info 追加するノードの情報
     * @param selected カーソルの下にあるノード(Drop 位置のノード）
     */
    public void addInfoToTree(ModuleInfoBean info, StampTreeNode selected) {
        
        //
        // StampInfo から新しい StampTreeNode を生成する
        //
        StampTreeNode node = new StampTreeNode(info);
        
        // 
        // Drop 位置のノードによって追加する位置を決める
        //
        if (selected != null && selected.isLeaf()) {
            //
            // Drop位置のノードが葉の場合、その前に挿入する
            //
            StampTreeNode newParent = (StampTreeNode) selected.getParent();
            int index = newParent.getIndex(selected);
            DefaultTreeModel model = (DefaultTreeModel) this.getModel();
            model.insertNodeInto(node, newParent, index);
            //
            // 追加したノードを選択する
            //
            TreeNode[] path = model.getPathToRoot(node);
            ((JTree)this).setSelectionPath(new TreePath(path));
            
        } else if (selected != null && (!selected.isLeaf())) {
            //
            // Drop位置のノードが子を持つ時、最後の子として挿入する
            //
            DefaultTreeModel model = (DefaultTreeModel) this.getModel();
            model.insertNodeInto(node, selected, selected.getChildCount());
            //
            // 追加したノードを選択する
            //
            TreeNode[] path = model.getPathToRoot(node);
            ((JTree)this).setSelectionPath(new TreePath(path));
            
        } else {
            //  
            // Drop 位置のノードが null でコールされるケースがある
            // 1. このtreeのスタンプではない場合、該当するTreeのルートに加える
            // 2. パス Tree など、まだノードを持たない初期状態の時
            //
            // Stamp ボックスから entity に対応する tree を得る
            StampTree another = stampBox.getStampTree(info.getEntity());
            boolean myTree = (another == this) ? true : false;
            final String treeName = another.getTreeName();
            DefaultTreeModel model = (DefaultTreeModel) another.getModel();
            StampTreeNode root = (StampTreeNode) model.getRoot();
            root.add(node);
            model.reload(root);
            //
            // 追加したノードを選択する
            //
            TreeNode[] path = model.getPathToRoot(node);
            ((JTree)this).setSelectionPath(new TreePath(path));
            
            // メッセージを表示する
            if (!myTree) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        StringBuilder buf = new StringBuilder();
                        buf.append("スタンプは個人用の ");
                        buf.append(treeName);
                        buf.append(" に保存しました。");
                        JOptionPane.showMessageDialog(
                                StampTree.this,
                                buf.toString(),
                                ClientContext.getFrameTitle(STAMP_SAVE_TASK_NAME),
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                });
            }
        }
    }
    
    /**
     * Diagnosis Table から Drag & Drop されたRegisteredDiagnosisをスタンプ化する。
     */
    public boolean addDiagnosis(RegisteredDiagnosisModel rd, final StampTreeNode selected) {
        
        if (rd == null) {
            return false;
        }
        
        // クリア
        rd.setId(0L);
        rd.setKarte(null);
        rd.setCreator(null);
        rd.setDiagnosisCategoryModel(null);
        rd.setDiagnosisOutcomeModel(null);
        rd.setFirstEncounterDate(null);
        rd.setStartDate(null);
        rd.setEndDate(null);
        rd.setRelatedHealthInsurance(null);
        rd.setFirstConfirmDate(null);
        rd.setConfirmDate(null);
        rd.setStatus(null);
        rd.setPatientLiteModel(null);
        rd.setUserLiteModel(null);
        
        RegisteredDiagnosisModel add = new RegisteredDiagnosisModel();
        add.setDiagnosis(rd.getDiagnosis());
        add.setDiagnosisCode(rd.getDiagnosisCode());
        add.setDiagnosisCodeSystem(rd.getDiagnosisCodeSystem());
        
        ModuleModel stamp = new ModuleModel();
        stamp.setModel(add);
        
        // データベースへ Stamp のデータモデルを永続化する
        StampModel addStamp = new StampModel();
        String stampId = GUIDGenerator.generate(addStamp);
        addStamp.setId(stampId);
        addStamp.setUserId(Project.getUserModel().getId());
        addStamp.setEntity(IInfoModel.ENTITY_DIAGNOSIS);
        addStamp.setStampBytes(getXMLBytes(stamp.getModel()));
        final StampDelegater sdl = new StampDelegater();
        
        // Tree に加える 新しい StampInfo を生成する
        final ModuleInfoBean info = new ModuleInfoBean();
        info.setStampId(stampId);                       // Stamp ID
        info.setStampName(add.getDiagnosis());          // 傷病名
        info.setEntity(IInfoModel.ENTITY_DIAGNOSIS);    // カテゴリ
        info.setStampRole(IInfoModel.ENTITY_DIAGNOSIS); // Role
        
        StringBuilder buf = new StringBuilder();
        buf.append(add.getDiagnosis());
        String cd = add.getDiagnosisCode();
        if (cd != null) {
            buf.append("(");
            buf.append(cd);
            buf.append(")"); // Tooltip
        }
        info.setStampMemo(buf.toString());
        
        // 保存タスクを生成し実行する
        int maxEstimation = ClientContext.getInt("task.default.maxEstimation");
        int delay = ClientContext.getInt("task.default.delay");
        int taskLength = maxEstimation/delay;
        int decideToPopup = ClientContext.getInt("task.default.decideToPopup");
        int milisToPopup = ClientContext.getInt("task.default.milisToPopup");
        String saveMsg = ClientContext.getString("task.default.saveMessage");
        final StampTask worker = new StampTask(addStamp, sdl, taskLength);
        
        final ProgressMonitor monitor = new ProgressMonitor(null, null, saveMsg, 0, taskLength);
        monitor.setProgress(0);
        monitor.setMillisToDecideToPopup(decideToPopup);
        monitor.setMillisToPopup(milisToPopup);
        
        taskTimer = new javax.swing.Timer(delay, new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                
                monitor.setProgress(worker.getCurrent());
                
                if (worker.isDone()) {
                    taskTimer.stop();
                    monitor.close();
                    
                    if (sdl.isNoError()) {
                        // 成功したら Tree に加える
                        addInfoToTree(info, selected);
                    } else {
                        // エラーメッセージを表示する
                        warning(sdl.getErrorMessage());
                    }
                    
                } else if (worker.isTimeOver()) {
                    taskTimer.stop();
                    monitor.close();
                    String title = ClientContext.getString("stamptree.title");
                    new TimeoutWarning(null, title, null);
                }
            }
        });
        worker.start();
        taskTimer.start();
        
        return true;
    }
    
    /**
     * エディタで生成した病名リストを登録する。
     */
    public void addDiagnosis(ArrayList<RegisteredDiagnosisModel> list) {
        
        if (list == null || list.size() == 0) {
            return;
        }
        
        final ArrayList<StampModel> stampList = new ArrayList<StampModel>();
        final ArrayList<ModuleInfoBean> infoList = new ArrayList<ModuleInfoBean>();
        
        for (RegisteredDiagnosisModel rd : list) {
            // クリア
            rd.setId(0L);
            rd.setKarte(null);
            rd.setCreator(null);
            rd.setDiagnosisCategoryModel(null);
            rd.setDiagnosisOutcomeModel(null);
            rd.setFirstEncounterDate(null);
            rd.setStartDate(null);
            rd.setEndDate(null);
            rd.setRelatedHealthInsurance(null);
            rd.setFirstConfirmDate(null);
            rd.setConfirmDate(null);
            rd.setStatus(null);
            rd.setPatientLiteModel(null);
            rd.setUserLiteModel(null);
            RegisteredDiagnosisModel add = new RegisteredDiagnosisModel();
            add.setDiagnosis(rd.getDiagnosis());
            add.setDiagnosisCode(rd.getDiagnosisCode());
            add.setDiagnosisCodeSystem(rd.getDiagnosisCodeSystem());
            
            ModuleModel stamp = new ModuleModel();
            stamp.setModel(add);
            
            // データベースへ Stamp のデータモデルを永続化する
            StampModel addStamp = new StampModel();
            String stampId = GUIDGenerator.generate(addStamp);
            addStamp.setId(stampId);
            addStamp.setUserId(Project.getUserModel().getId());
            addStamp.setEntity(IInfoModel.ENTITY_DIAGNOSIS);
            addStamp.setStampBytes(getXMLBytes(stamp.getModel()));
            stampList.add(addStamp);
            
            // Tree に加える 新しい StampInfo を生成する
            ModuleInfoBean info = new ModuleInfoBean();
            info.setStampId(stampId);                       // Stamp ID
            info.setStampName(add.getDiagnosis());          // 傷病名
            info.setEntity(IInfoModel.ENTITY_DIAGNOSIS);    // カテゴリ
            info.setStampRole(IInfoModel.ENTITY_DIAGNOSIS); // Role
            
            StringBuilder buf = new StringBuilder();
            buf.append(add.getDiagnosis());
            String cd = add.getDiagnosisCode();
            if (cd != null) {
                buf.append("(");
                buf.append(cd);
                buf.append(")"); // Tooltip
            }
            info.setStampMemo(buf.toString());
            infoList.add(info);
        }
        
        final StampDelegater sdl = new StampDelegater();
        
        // 保存タスクを生成し実行する
        int maxEstimation = ClientContext.getInt("task.default.maxEstimation");
        int delay = ClientContext.getInt("task.default.delay");
        int taskLength = maxEstimation/delay;
        int decideToPopup = ClientContext.getInt("task.default.decideToPopup");
        int milisToPopup = ClientContext.getInt("task.default.milisToPopup");
        String saveMsg = ClientContext.getString("task.default.saveMessage");
        final StampTask worker = new StampTask(stampList, sdl, taskLength);
        
        final ProgressMonitor monitor = new ProgressMonitor(null, null, saveMsg, 0, taskLength);
        monitor.setProgress(0);
        monitor.setMillisToDecideToPopup(decideToPopup);
        monitor.setMillisToPopup(milisToPopup);
        
        taskTimer = new javax.swing.Timer(delay, new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                
                monitor.setProgress(worker.getCurrent());
                
                if (worker.isDone()) {
                    taskTimer.stop();
                    monitor.close();
                    
                    if (sdl.isNoError()) {
                        // 成功したら Tree に加える
                        for(ModuleInfoBean info : infoList) {
                            addInfoToTree(info, null);
                        }
                    } else {
                        // エラーメッセージを表示する
                        warning(sdl.getErrorMessage());
                    }
                    
                } else if (worker.isTimeOver()) {
                    taskTimer.stop();
                    monitor.close();
                    String title = ClientContext.getString("stamptree.title");
                    new TimeoutWarning(null, title, null);
                }
            }
        });
        worker.start();
        taskTimer.start();
    }
    
    /**
     * テキストスタンプを追加する。
     */
    public boolean addTextStamp(String text, final StampTreeNode selected) {
        
        if ( (text == null) || (text.length() == 0) || text.equals("") )  {
            return false;
        }
        
        TextStampModel stamp = new TextStampModel();
        stamp.setText(text);
        
        //
        // データベースへ Stamp のデータモデルを永続化する
        //
        StampModel addStamp = new StampModel();
        String stampId = GUIDGenerator.generate(addStamp);
        addStamp.setId(stampId);
        addStamp.setUserId(Project.getUserModel().getId());
        addStamp.setEntity(IInfoModel.ENTITY_TEXT);
        addStamp.setStampBytes(getXMLBytes((IInfoModel) stamp));
        
        final StampDelegater sdl = new StampDelegater();
        
        //
        // Tree へ加える 新しい StampInfo を生成する
        //
        final ModuleInfoBean info = new ModuleInfoBean();
        int len = text.length() > 16 ? 16 : text.length();
        String name = text.substring(0, len);
        len = name.indexOf("\n");
        if (len > 0) {
            name = name.substring(0, len);
        }
        info.setStampName(name);                    //
        info.setEntity(IInfoModel.ENTITY_TEXT);     // カテゴリ
        info.setStampRole(IInfoModel.ENTITY_TEXT);  // Role
        info.setStampMemo(text);                    // Tooltip
        info.setStampId(stampId);                   // Stamp ID
        
        // 保存タスクを生成し実行する
        int maxEstimation = ClientContext.getInt("task.default.maxEstimation");
        int delay = ClientContext.getInt("task.default.delay");
        int taskLength = maxEstimation/delay;
        int decideToPopup = ClientContext.getInt("task.default.decideToPopup");
        int milisToPopup = ClientContext.getInt("task.default.milisToPopup");
        String saveMsg = ClientContext.getString("task.default.saveMessage");
        
        final StampTask worker = new StampTask(addStamp, sdl, taskLength);
        
        final ProgressMonitor monitor = new ProgressMonitor(null, null, saveMsg, 0, taskLength);
        monitor.setProgress(0);
        monitor.setMillisToDecideToPopup(decideToPopup);
        monitor.setMillisToPopup(milisToPopup);
        
        taskTimer = new javax.swing.Timer(delay, new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                
                monitor.setProgress(worker.getCurrent());
                
                if (worker.isDone()) {
                    
                    taskTimer.stop();
                    monitor.close();
                    
                    if (sdl.isNoError()) {
                        // 
                        // 成功したら Tree に加える
                        //
                        addInfoToTree(info, selected);
                        
                    } else {
                        //
                        // エラーメッセージを表示する
                        //
                        warning(sdl.getErrorMessage());
                    }
                    
                } else if (worker.isTimeOver()) {
                    taskTimer.stop();
                    monitor.close();
                    String title = ClientContext.getString("stamptree.title");
                    new TimeoutWarning(null, title, null);
                }
            }
        });
        worker.start();
        taskTimer.start();
        
        return true;
    }
    
    /**
     * スタンプの情報を表示するための文字列を生成する。
     * @param stamp 情報を生成するスタンプ
     * @return スタンプの情報文字列
     */
    protected String constractToolTip(ModuleModel stamp) {
        
        String ret = null;
        
        try {
            StringBuilder buf = new StringBuilder();
            BufferedReader reader = new BufferedReader(new StringReader(stamp.getModel().toString()));
            
            String line = null;
            while ( (line = reader.readLine()) != null ) {
                
                buf.append(line);
                
                if (buf.length() < TOOLTIP_LENGTH) {
                    buf.append(",");
                } else {
                    break;
                }
            }
            reader.close();
            if (buf.length() > TOOLTIP_LENGTH) {
                buf.setLength(TOOLTIP_LENGTH);
            }
            buf.append("...");
            ret = buf.toString();
            
        } catch (IOException e) {
            e.toString();
        }
        
        return ret;
    }
    
    /**
     * スタンプタスク共通の warning ダイアログを表示する。
     * @param title  ダイアログウインドウに表示するタイトル
     * @param message　エラーメッセージ
     */
    private void warning(String message) {
        String title = ClientContext.getString("stamptree.title");
        JOptionPane.showMessageDialog(
                StampTree.this,
                message,
                ClientContext.getFrameTitle(title),
                JOptionPane.WARNING_MESSAGE);
    }
    
    // //////////// PopupMenu サポート //////////////
    
    /**
     * ノードの名前を変更する。
     */
    public void renameNode() {
        
        if (!isUserTree()) {
            return;
        }
        
        // Root へのパスを取得する
        StampTreeNode node = getSelectedNode();
        if (node == null) {
            return;
        }
        TreeNode[] nodes = node.getPath();
        TreePath path = new TreePath(nodes);
        
        // 編集を開始する
        this.setEditable(true);
        this.startEditingAtPath(path);
        // this.setEditable (false); は TreeModelListener で行う
    }
    
//    public void cut() {
//        System.out.println("called cut");
//        if (!isUserTree()) {
//            return;
//        }
//        
//        StampTreeNode theNode = getSelectedNode();
//        if (theNode != null && theNode.isLeaf()) {
//            Action a = this.getTransferHandler().getCutAction();
//            if (a != null) {
//                a.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
//            }
//        }
//    }
//    
//    public void copy() {
//        System.out.println("called copy");
//        if (!isUserTree()) {
//            return;
//        }
//        
//        StampTreeNode theNode = getSelectedNode();
//        if (theNode != null) {
//            Action a = this.getTransferHandler().getCopyAction();
//            if (a != null) {
//                a.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
//            }
//        }
//    }
//    
//    public void paste() {
//        System.out.println("called paste");
//        if (!isUserTree()) {
//            return;
//        }
//        Action a = this.getTransferHandler().getPasteAction();
//        if (a != null) {
//            a.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
//        }
//    }
    
    /**
     * ノードを削除する。
     */
    public void deleteNode() {
        
        //System.out.println("Called delete");
        //Toolkit.getDefaultToolkit().beep();
        
        if (!isUserTree()) {
            return;
        }
        
        //
        // 削除するノードを取得する
        // 右クリックで選択されている
        //
        final StampTreeNode theNode = getSelectedNode();
        if (theNode == null) {
            return;
        }
        
        //
        // このノードをルートにするサブツリーを前順走査する列挙を生成して返します。
        // 列挙の nextElement() メソッドによって返される最初のノードは、この削除するノードです。
        //
        Enumeration e = theNode.preorderEnumeration();
        
        //
        // このリストのなかに削除するノードとその子を含める
        //
        ArrayList<String> deleteList = new ArrayList<String>();
        
        // エディタから発行があるかどうかのフラグ
        boolean hasEditor = false;
        
        // 列挙する
        while (e.hasMoreElements()) {
            //System.out.println("e.hasMore");
            StampTreeNode node = (StampTreeNode) e.nextElement();
            if (node.isLeaf()) {
                ModuleInfoBean info = (ModuleInfoBean) node.getUserObject();
                String stampId = info.getStampId();
                //
                // エディタから発行がある場合は中止する
                //
                if (info.getStampName().equals("エディタから発行...") && (! info.isSerialized()) ) {
                    hasEditor = true;
                    break;
                }
                
                //
                // IDが付いているもののみを加える
                //
                if (stampId != null) {
                    deleteList.add(stampId);
                }
            }
        }
        
        //
        // エディタから発行が有った場合はダイアログを表示し
        // リターンする
        //
        if (hasEditor) {
            String msg0 = "エディタから発行は消去できません。フォルダに含まれている";
            String msg1 = "場合は Drag & Drop で移動後、再度実行してください。";
            String taskTitle = ClientContext.getString("stamptree.title");
            JOptionPane.showMessageDialog(
                        (Component) null,
                        new Object[]{msg0, msg1},
                        ClientContext.getFrameTitle(taskTitle),
                        JOptionPane.INFORMATION_MESSAGE
                        );
            return;
        }
        
        //
        // 削除するフォルダが空の場合は削除してリターンする
        // リストのサイズがゼロかつ theNode が葉でない時
        // 
        if (deleteList.size() == 0 && (!theNode.isLeaf()) ) {
            //System.out.println("Empty Folder");
            DefaultTreeModel model = (DefaultTreeModel)(StampTree.this).getModel();
            model.removeNodeFromParent(theNode);
            return;
        }
        
        // データベースのスタンプを削除するデリゲータを生成する
        final StampDelegater sdl = new StampDelegater();
        
        // 削除タスクを生成する
        int maxEstimation = ClientContext.getInt("task.default.maxEstimation");
        int delay = ClientContext.getInt("task.default.delay");
        int taskLength = maxEstimation/delay;
        int decideToPopup = ClientContext.getInt("task.default.decideToPopup");
        int milisToPopup = ClientContext.getInt("task.default.milisToPopup");
        String deleteMessage = ClientContext.getString("task.default.deleteMessage");
        final StampTask worker = new StampTask(deleteList, sdl, taskLength);
        
        // Progress Monitor を生成する
        final ProgressMonitor monitor = new ProgressMonitor(null, null, deleteMessage, 0, taskLength);
        monitor.setProgress(0);
        monitor.setMillisToDecideToPopup(decideToPopup);
        monitor.setMillisToPopup(milisToPopup);
        
        // タスクタイマーを生成する
        taskTimer = new javax.swing.Timer(delay, new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                
                // モニタを進める
                monitor.setProgress(worker.getCurrent());
                
                // タスクが終了しているかどうかをチェックする
                if (worker.isDone()) {
                    
                    //
                    // 終了処理を行う
                    //
                    taskTimer.stop();
                    monitor.close();
                    
                    // 
                    // データベースの削除が成功しているかどうかをチェックする
                    //
                    if (sdl.isNoError()) {
                        //
                        // 成功している場合は Tree からノードを削除する
                        // TODO エディタから発行も削除される
                        //
                        DefaultTreeModel model = (DefaultTreeModel)(StampTree.this).getModel();
                        model.removeNodeFromParent(theNode);
                        
                    } else {
                        //
                        // エラーメッセージを表示する
                        // warning(sdl.getErrorMessage());
                        //
                        // 強制削除をあるバージョンまで実行する
                        // TODO エディタから発行も削除される
                        //
                        DefaultTreeModel model = (DefaultTreeModel)(StampTree.this).getModel();
                        model.removeNodeFromParent(theNode); 
                    }
                    
                } else if (worker.isTimeOver()) {
                    taskTimer.stop();
                    monitor.close();
                    String title = ClientContext.getString("stamptree.title");
                    new TimeoutWarning(null, title, null);
                }
            }
        });
        worker.start();
        taskTimer.start();
    }
    
    /**
     * 新規のフォルダを追加する
     */
    public void createNewFolder() {
        
        if (!isUserTree()) {
            return;
        }
        
        // フォルダノードを生成する
        StampTreeNode folder = new StampTreeNode(NEW_FOLDER_NAME);
        
        //
        // 生成位置となる選択されたノードを得る
        //
        StampTreeNode selected = getSelectedNode();
        
        if (selected != null && selected.isLeaf()) {
            //
            // 選択位置のノードが葉の場合、その前に挿入する
            //
            StampTreeNode newParent = (StampTreeNode) selected.getParent();
            int index = newParent.getIndex(selected);
            DefaultTreeModel model = (DefaultTreeModel) this.getModel();
            model.insertNodeInto(folder, newParent, index);
            
        } else if (selected != null && (!selected.isLeaf())) {
            //
            // 選択位置のノードが子を持つ時、最後の子として挿入する
            //
            DefaultTreeModel model = (DefaultTreeModel) this.getModel();
            model.insertNodeInto(folder, selected, selected.getChildCount());
        }
        
        //TreePath parentPath = new TreePath(parent.getPath());
        //this.expandPath(parentPath);
    }
    
    // /////////// TreeModelListener ////////////////
    
    public void treeNodesChanged(TreeModelEvent e) {
        this.setEditable(false);
    }
    
    public void treeNodesInserted(TreeModelEvent e) {
    }
    
    public void treeNodesRemoved(TreeModelEvent e) {
    }
    
    public void treeStructureChanged(TreeModelEvent e) {
    }
    
    private byte[] getXMLBytes(Object bean) {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        XMLEncoder e = new XMLEncoder(new BufferedOutputStream(bo));
        e.writeObject(bean);
        e.close();
        return bo.toByteArray();
    }
}