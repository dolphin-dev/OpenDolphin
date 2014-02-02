package open.dolphin.client;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.ModuleInfoBean;

/**
 * カルテペインのコードヘルパークラス。
 *
 * @author Kazyshi Minagawa
 */
public class PCodeHelper extends AbstractCodeHelper {
    
    /**
     * Creates a new instance of CodeHelper
     */
    public PCodeHelper(KartePane pPane, ChartMediator mediator) {
        super(pPane, mediator);
    }
    
    
    protected void buildPopup(String text) {
        
        String test = text.toLowerCase();
        String entity = null;
        
        //
        // StampTree のキーワードに一致しているかどうかを判定する
        //
        Preferences prefs = Preferences.userNodeForPackage(AbstractCodeHelper.class);
        
        if (prefs.get(IInfoModel.ENTITY_TEXT, "tx").startsWith(test)) {
            entity = IInfoModel.ENTITY_TEXT;
        
        } else if (prefs.get(IInfoModel.ENTITY_PATH, "pat").startsWith(test)) {
            entity = IInfoModel.ENTITY_PATH;
            
        } else if (prefs.get(IInfoModel.ENTITY_GENERAL_ORDER, "gen").startsWith(test)) {
            entity = IInfoModel.ENTITY_GENERAL_ORDER;
            
        } else if (prefs.get(IInfoModel.ENTITY_OTHER_ORDER, "oth").startsWith(test)) {
            entity = IInfoModel.ENTITY_OTHER_ORDER;
            
        } else if (prefs.get(IInfoModel.ENTITY_TREATMENT, "tr").startsWith(test)) {
            entity = IInfoModel.ENTITY_TREATMENT;
            
        } else if (prefs.get(IInfoModel.ENTITY_SURGERY_ORDER, "sur").startsWith(test)) {
            entity = IInfoModel.ENTITY_SURGERY_ORDER;
            
        } else if (prefs.get(IInfoModel.ENTITY_RADIOLOGY_ORDER, "rad").startsWith(test)) {
            entity = IInfoModel.ENTITY_RADIOLOGY_ORDER;
            
        } else if (prefs.get(IInfoModel.ENTITY_LABO_TEST, "lab").startsWith(test)) {
            entity = IInfoModel.ENTITY_LABO_TEST;
            
        } else if (prefs.get(IInfoModel.ENTITY_PHYSIOLOGY_ORDER, "phy").startsWith(test)) {
            entity = IInfoModel.ENTITY_PHYSIOLOGY_ORDER;
            
        } else if (prefs.get(IInfoModel.ENTITY_BACTERIA_ORDER, "bac").startsWith(test)) {
            entity = IInfoModel.ENTITY_BACTERIA_ORDER;
            
        } else if (prefs.get(IInfoModel.ENTITY_INJECTION_ORDER, "inj").startsWith(test)) {
            entity = IInfoModel.ENTITY_INJECTION_ORDER;
            
        } else if (prefs.get(IInfoModel.ENTITY_MED_ORDER, "rp").startsWith(test)) {
            entity = IInfoModel.ENTITY_MED_ORDER;
            
        } else if (prefs.get(IInfoModel.ENTITY_BASE_CHARGE_ORDER, "base").startsWith(test)) {
            entity = IInfoModel.ENTITY_BASE_CHARGE_ORDER;
            
        } else if (prefs.get(IInfoModel.ENTITY_INSTRACTION_CHARGE_ORDER, "ins").startsWith(test)) {
            entity = IInfoModel.ENTITY_INSTRACTION_CHARGE_ORDER;
            
        } else if (prefs.get(IInfoModel.ENTITY_ORCA, "orca").startsWith(test)) {
            entity = IInfoModel.ENTITY_ORCA;
            
        } 
        
        if (entity != null) {
            buildEntityPopup(entity);
        
        } else {
            //
            // 全てのスタンプツリーをなめる
            //
            buildMatchPopup(text);
        }
    }
    
    
    protected void buildMatchPopup(String text) {
        
        //
        // current StampBoxのP関連 StampTree を取得する
        //
        StampBoxPlugin stampBox = mediator.getStampBox();
        List<StampTree> allTree = stampBox.getAllPTrees();
        if (allTree == null || allTree.size() == 0) {
            return;
        }
        
        popup = new JPopupMenu();
        
        //
        // メニューのスタックを生成する
        //
        LinkedList menus = new LinkedList();
        menus.addFirst(popup);
        
        //
        // 親ノードのスタックを生成する
        //
        LinkedList parents = new LinkedList();
        
        //
        // Stamp の名前がキーワードで始まり、それが１個以上あるものを補完メニューに加える
        //
        pattern = Pattern.compile("^" + text + ".*");
        
        for (StampTree tree : allTree) {
            
            DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) tree.getModel().getRoot();
            
            Enumeration e = rootNode.preorderEnumeration();
            
            if (e != null) {
                
                e.nextElement(); // consume root
                
                while (e.hasMoreElements()) {
                    
                    //
                    // 調査対象のノードを得る
                    //
                    StampTreeNode node = (StampTreeNode) e.nextElement();
                    
                    // 
                    // その親を得る
                    //
                    StampTreeNode parent = (StampTreeNode) node.getParent();
                    
                    //
                    // 親がリストに含まれているかどうか
                    //
                    int index = parents.indexOf(parent);
                    if (index > -1) {
                        //
                        // 自分の親がインデックス=0になるまでポップする
                        //
                        for (int i = 0; i < index; i++) {
                            parents.removeFirst();
                            menus.removeFirst();
                        }
                        
                        if (!node.isLeaf()) {
                            //
                            // フォルダの場合
                            //
                            String folderName = node.getUserObject().toString();
                            JMenu subMenu = new JMenu(folderName);
                            if (menus.getFirst() instanceof JPopupMenu) {
                                ((JPopupMenu) menus.getFirst()).add(subMenu);
                            } else {
                                ((JMenu) menus.getFirst()).add(subMenu);
                            }
                            menus.addFirst(subMenu);
                            parents.addFirst(node);
                            JMenuItem item = new JMenuItem(folderName);
                            item.setIcon(icon);
                            subMenu.add(item);
                            addActionListner(item, node);
                            
                        } else {
                            ModuleInfoBean info = (ModuleInfoBean) node.getUserObject();
                            String completion = info.getStampName();
                            JMenuItem item = new JMenuItem(completion);
                            addActionListner(item, node);
                            if (menus.getFirst() instanceof JPopupMenu) {
                                ((JPopupMenu) menus.getFirst()).add(item);
                            } else {
                                ((JMenu) menus.getFirst()).add(item);
                            }
                        }
                    
                    } else {
                        //
                        // 含まれていないのでマッチ検査が必要
                        //
                        if (!node.isLeaf()) {
                            //
                            // フォルダの場合
                            //
                            String completion = node.getUserObject().toString();
                            Matcher matcher = pattern.matcher(completion);
                            if (matcher.matches()) {
                                //
                                // マッチした場合はカレントメニューへ加える
                                // 自分がカレントメニューになる
                                // 親リストに自分を加える
                                String folderName = node.getUserObject().toString();
                                JMenu subMenu = new JMenu(folderName);
                                if (menus.getFirst() instanceof JPopupMenu) {
                                    ((JPopupMenu) menus.getFirst()).add(subMenu);
                                } else {
                                    ((JMenu) menus.getFirst()).add(subMenu);
                                }
                                menus.addFirst(subMenu);
                                parents.addFirst(node);
                                
                                //
                                // フォルダ選択のアイテムを生成しサブメニューの要素にする
                                //
                                JMenuItem item = new JMenuItem(folderName);
                                item.setIcon(icon);
                                subMenu.add(item);
                                addActionListner(item, node);
                            }
                            
                        } else {
                            //
                            // 葉の場合
                            //
                            ModuleInfoBean info = (ModuleInfoBean) node.getUserObject();
                            String completion = info.getStampName();
                            Matcher matcher = pattern.matcher(completion);
                        
                            if (matcher.matches()) {
                                //
                                // 一致した場合
                                //
                                JMenuItem item = new JMenuItem(completion);
                                addActionListner(item, node);
                                if (menus.getFirst() instanceof JPopupMenu) {
                                    ((JPopupMenu) menus.getFirst()).add(item);
                                } else {
                                    ((JMenu) menus.getFirst()).add(item);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
