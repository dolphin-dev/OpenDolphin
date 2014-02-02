package open.dolphin.order;

import java.util.Iterator;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import open.dolphin.client.IStampModelEditor;
import open.dolphin.infomodel.RegisteredDiagnosisModel;
import open.dolphin.table.ObjectTableModel;

/**
 * 傷病名編集テーブルの State クラス。
 *
 * @author Kazushi Minagawa
 */
public class DiagnosisStateMgr {
    
    /** Event */
    public enum Event {ADDED, DELETED, CLEARED, SELECTED};
    
    /** 空の状態を表す State */
    private final EmptyState empty = new EmptyState();
    
    /** 有効な状態を表す State */
    private final ValidState valid = new ValidState();
    
    /** 無効な状態を表す State */
    private final InValidState invalid = new InValidState();
    
    /** 削除ボタン */
    private JButton delete;
    
    /** クリアボタン */
    private JButton clear;
    
    /** State label */
    private JLabel stateLabel;
    
    /** 傷病名編集テーブルの TableModel  */
    private ObjectTableModel tableModel;
    
    /** 傷病名編集テーブル */
    private JTable table;
    
    /** 現在の State */
    private DiagnosisState curState;
    
    /** コンテキスト */
    private IStampModelEditor context;
    
    /** 基本病名の個数 */
    private int baseCnt;
    
    /** 修飾語を持つかどうか */
    private boolean hasModifier;
    
    /**
     * Creates a new instance of DiagnosisStateMgr
     */    
    public DiagnosisStateMgr(JButton delete, 
                             JButton clear, 
                             JLabel stateLabel,
                             ObjectTableModel tableModel,
                             JTable table,
                             IStampModelEditor context) {
        this.delete = delete;
        this.clear = clear;
        this.stateLabel = stateLabel;
        this.tableModel = tableModel;
        this.table = table;
        this.context = context;
        curState = empty;
    }
    
    /**
     * Event を処理する。
     */
    public void processEvent(Event evt) {
        curState = curState.processEvent(evt);
    }
    
    /**
     * State へ遷移する。
     */
    public void enter() {
        curState.enter();
    }
    
    /**
     * 次の State を判定する。
     * @return 次の State
     */
    protected DiagnosisState judgeState() {
        
        List dataList = tableModel.getObjectList();
        
        if (dataList == null || dataList.size() == 0) {
            return empty;
        }
        
        hasModifier = false;
        baseCnt = 0;
        
        for (Iterator iter = dataList.iterator(); iter.hasNext(); ) {
            RegisteredDiagnosisModel rm = (RegisteredDiagnosisModel) iter.next();
            if (rm.getDiagnosisCode().startsWith("ZZZ")) {
                hasModifier = true;
            } else {
                baseCnt += 1;
            }
        }
        
        if (hasModifier) {
            //
            // 修飾語がある場合、基本病名は一つのみ
            //
            return baseCnt == 1 ? valid : invalid;
        } else {
            //
            // 
            //
            return valid;
        }
    }
    
    /**
     * 抽象編集テーブル State クラス。
     */
    protected abstract class DiagnosisState {
        
        public abstract DiagnosisState processEvent(Event evt);
        
        public abstract void enter();
    }
    
    
    /**
     * EmptyState class.
     */
    private class EmptyState extends DiagnosisState {
         
        public DiagnosisState processEvent(Event evt) {
            
            DiagnosisState next = null;
            
            switch (evt) {
                case ADDED:
                    next = judgeState();
                    next.enter();
                    break;
                    
                case SELECTED:
                    next = this;
                    next.enter();
                    break;
            }
            
            return next;
        } 
        
        public void enter() {
            delete.setEnabled(false);
            clear.setEnabled(false);
            stateLabel.setText("傷病名がありません。");
            context.setValidModel(false);
        }
    }
    
    private class ValidState extends DiagnosisState {
        
        public DiagnosisState processEvent(Event evt) {
            
            DiagnosisState next = null;
            
            switch (evt) {
                
                case ADDED:
                    next = judgeState();
                    next.enter();
                    break;
                    
                case DELETED:
                    next = judgeState();
                    next.enter();
                    break; 
                    
                case CLEARED:
                    next = empty;
                    next.enter();
                    break;
                    
                case SELECTED:
                    next = this;
                    next.enter();
                    break;                    
            }
            
            return next;
        }        
        
        public void enter() {
            int row = table.getSelectedRow();
            boolean b = tableModel.isValidRow(row);
            delete.setEnabled(b);
            clear.setEnabled(true);
            stateLabel.setText("有効なデータになっています。");
            context.setValidModel(true);
        }
        
    }
    
    private class InValidState extends DiagnosisState {
        
         public DiagnosisState processEvent(Event evt) {
            
            DiagnosisState next = null;
            
            switch (evt) {
                
                case ADDED:
                    next = judgeState();
                    next.enter();
                    break;
                    
                case DELETED:
                    next = judgeState();
                    next.enter();
                    break; 
                    
                case CLEARED:
                    next = empty;
                    next.enter();
                    break;
                    
                case SELECTED:
                    next = this;
                    next.enter();
                    break;                    
            }
            
            return next;
        }               
        
        public void enter() {
            int row = table.getSelectedRow();
            boolean b = tableModel.isValidRow(row);
            delete.setEnabled(b);
            clear.setEnabled(true);
            
            if (baseCnt == 0) {
                stateLabel.setText("基本傷病名がありません。");
            } else {
                stateLabel.setText("修飾語がある場合は、基本傷病名は一つです。");
            }
            context.setValidModel(false);
        }
    }
}
