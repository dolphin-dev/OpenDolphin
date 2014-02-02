package open.dolphin.client;

import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import open.dolphin.dao.SqlOrcaSetDao;
import open.dolphin.infomodel.ModuleInfoBean;
import open.dolphin.infomodel.OrcaInputCd;
import open.dolphin.project.Project;
import open.dolphin.util.ReflectMonitor;

/**
 * ORCA StampTree クラス。
 *
 * @author Kazushi Minagawa
 */
public class OrcaTree extends StampTree {
    
    private static final String MONITOR_TITLE = "ORCAセット検索";
    
    /** ORCA 入力セットをフェッチしたかどうかのフラグ */
    private boolean fetched;
    
    /** 
     * Creates a new instance of OrcaTree 
     */
    public OrcaTree(TreeModel model) {
        super(model);
    }
    
    /**
     * ORCA 入力セットをフェッチしたかどうかを返す。
     * @return 取得済みのとき true
     */
    public boolean isFetched() {
        return fetched;
    }
    
    /**
     * ORCA 入力セットをフェッチしたかどうかを設定する。
     * @param fetched 取得済みのとき true
     */
    public void setFetched(boolean fetched) {
        this.fetched = fetched;
    }
    
    /**
     * StampBox のタブでこのTreeが選択された時コールされる。
     */
    @Override
    public void enter() {
        
        if (!fetched) {

            // CLAIM(Master) Address が設定されていない場合に警告する
            String address = Project.getClaimAddress();
            if (address == null || address.equals("")) {
//                if (SwingUtilities.isEventDispatchThread()) {
//                    String msg0 = "レセコンのIPアドレスが設定されていないため、マスターを検索できません。";
//                    String msg1 = "環境設定メニューからレセコンのIPアドレスを設定してください。";
//                    Object message = new String[]{msg0, msg1};
//                    Window parent = SwingUtilities.getWindowAncestor(OrcaTree.this);
//                    String title = ClientContext.getFrameTitle(MONITOR_TITLE);
//                    JOptionPane.showMessageDialog(parent, message, title, JOptionPane.WARNING_MESSAGE);
//                }
                return;
            }

            if (SwingUtilities.isEventDispatchThread()) {
                fetchOrcaSet();
            } else {
                fetchOrcaSet2();
            }
        }
    }
    
    /**
     * ORCA の入力セットを取得しTreeに加える。
     */
    private void fetchOrcaSet2() {
        
        try {
            SqlOrcaSetDao dao = new SqlOrcaSetDao();
            
            ArrayList<OrcaInputCd> inputSet = dao.getOrcaInputSet();
            StampTreeNode root = (StampTreeNode) this.getModel().getRoot();
            
            for (OrcaInputCd set : inputSet) {
                ModuleInfoBean stampInfo = set.getStampInfo();
                StampTreeNode node = new StampTreeNode(stampInfo);
                root.add(node);
            }
            
            DefaultTreeModel model = (DefaultTreeModel) this.getModel();
            model.reload(root);
            
            setFetched(true);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
      
    /**
     * ORCA の入力セットを取得しTreeに加える。
     */
    private void fetchOrcaSet() {

        
        // DAOを生成する
        final SqlOrcaSetDao dao = new SqlOrcaSetDao();
        
        // ReflectMonitor を生成する
        final ReflectMonitor rm = new ReflectMonitor();
        rm.setReflection(dao, 
                         "getOrcaInputSet", 
                         (Class[]) null, 
                         (Object[]) null);
        rm.setMonitor(SwingUtilities.getWindowAncestor(this), MONITOR_TITLE, "入力セットを検索しています...  ", 200, 60*1000);
        
        //
        // ReflectMonitor の結果State property の束縛リスナを生成する
        //
        PropertyChangeListener pl = new PropertyChangeListener() {
           
            public void propertyChange(PropertyChangeEvent e) {
                
                int state = ((Integer) e.getNewValue()).intValue();
                
                switch (state) {
                    
                    case ReflectMonitor.DONE:
                        processResult(dao.isNoError(), rm.getResult(), dao.getErrorMessage());
                        break;
                        
                    case ReflectMonitor.TIME_OVER:
                        Window parent = SwingUtilities.getWindowAncestor(OrcaTree.this);
                        String title = ClientContext.getString(MONITOR_TITLE);
                        new TimeoutWarning(parent, title, null).start();
                        break;
                        
                    case ReflectMonitor.CANCELED:
                        break;
                }
                
                //
                // Block を解除する
                //
                //setBusy(false);
            }
        };
        rm.addPropertyChangeListener(pl);
        
        //
        // Block し、メソッドの実行を開始する
        //
        //setBusy(true);
        rm.start();
    }
    
    /**
     * ORCAセットのStampTreeを構築する。
     */
    private void processResult(boolean noErr, Object result, String message) {
        
        if (noErr) {
            
            ArrayList<OrcaInputCd> inputSet = (ArrayList<OrcaInputCd>) result;
            StampTreeNode root = (StampTreeNode) this.getModel().getRoot();
            
            for (OrcaInputCd set : inputSet) {
                ModuleInfoBean stampInfo = set.getStampInfo();
                StampTreeNode node = new StampTreeNode(stampInfo);
                root.add(node);
            }
            
            DefaultTreeModel model = (DefaultTreeModel) this.getModel();
            model.reload(root);
            
            setFetched(true);
            
        } else {
            
            String title = ClientContext.getFrameTitle(MONITOR_TITLE);
            JOptionPane.showMessageDialog(this, message, title, JOptionPane.WARNING_MESSAGE);
        }
    }
}
