package open.dolphin.client;

import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import open.dolphin.infomodel.ModuleInfoBean;

/**
 * KartePane の抽象コードヘルパークラス。
 *
 * @author Kazyshi Minagawa
 */
public abstract class AbstractCodeHelper {
    
    /** キーワードの境界となる文字 */
    static final String[] WORD_SEPARATOR = {" ", " ", "、", "。", "\n", "\t"};
    
    static final String LISTENER_METHOD = "importStamp";
    
    static final Icon icon = ClientContext.getImageIcon("foldr_16.gif");
    
    /** 対象の KartePane */
    KartePane kartePane;
    
    /** KartePane の JTextPane */
    JTextPane textPane;
    
    /** 補完リストメニュー */
    JPopupMenu popup;
    
    /** キーワードパターン */
    Pattern pattern;
    
    /** キーワードの開始位置 */
    int start;
    
    /** キーワードの終了位置 */
    int end;
    
    /** ChartMediator */
    ChartMediator mediator;
    
    /** 修飾キー */
    int MODIFIER;
    
    
    /** 
     * Creates a new instance of CodeHelper 
     */
    public AbstractCodeHelper(KartePane kartePane, ChartMediator mediator) {
        
        this.kartePane = kartePane;
        this.mediator = mediator;
        this.textPane = kartePane.getTextPane();
        
        Preferences prefs = Preferences.userNodeForPackage(AbstractCodeHelper.class);
        String modifier = prefs.get("modifier", "ctrl");
        
        if (modifier.equals("ctrl")) {
            MODIFIER =  KeyEvent.CTRL_DOWN_MASK;
        } else if (modifier.equals("meta")) {
            MODIFIER =  KeyEvent.META_DOWN_MASK;
        }

        this.textPane.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if ((e.getModifiersEx() == MODIFIER) && e.getKeyCode() == KeyEvent.VK_SPACE) {
                    buildAndShowPopup();
                }
            }
        });
    }
    
    protected abstract void buildPopup(String text);
    
    protected void buildEntityPopup(String entity) {
        
        //
        // 引数の entityに対応する StampTree を取得する
        //
        StampBoxPlugin stampBox = mediator.getStampBox();
        StampTree tree = stampBox.getStampTree(entity);
        if (tree == null) {
            return;
        }
        
        popup = new JPopupMenu();
        
        Hashtable<Object, Object> ht = new Hashtable<Object, Object>(5, 0.75f);
        
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) tree.getModel().getRoot();
        ht.put(rootNode, popup);
        
        Enumeration e = rootNode.preorderEnumeration();
        
        if (e != null) {
            
            e.nextElement(); // consume root
            
            while (e.hasMoreElements()) {
                
                StampTreeNode node = (StampTreeNode) e.nextElement();
                
                if (!node.isLeaf()) {
                    
                    JMenu subMenu = new JMenu(node.getUserObject().toString());
                    if (node.getParent() == rootNode) {
                        JPopupMenu parent = (JPopupMenu) ht.get(node.getParent());
                        parent.add(subMenu);
                        ht.put(node, subMenu);
                    } else {
                        JMenu parent = (JMenu) ht.get(node.getParent());
                        parent.add(subMenu);
                        ht.put(node, subMenu);   
                    }
                    
            
                    // 配下の子を全て列挙しJmenuItemにまとめる
                    JMenuItem item = new JMenuItem(node.getUserObject().toString());
                    item.setIcon(icon);
                    subMenu.add(item);
                    
                    addActionListner(item, node);
                
                } else if (node.isLeaf()) {
                    
                    ModuleInfoBean info = (ModuleInfoBean) node.getUserObject();
                    String stampName = info.getStampName();
                     
                    JMenuItem item = new JMenuItem(stampName);
                    addActionListner(item, node);
                    
                    if (node.getParent() == rootNode) {
                        JPopupMenu parent = (JPopupMenu) ht.get(node.getParent());
                        parent.add(item);
                    } else {
                        JMenu parent = (JMenu) ht.get(node.getParent());
                        parent.add(item);
                    }
                }
            }
        }
    }
    
    protected void addActionListner(JMenuItem item, StampTreeNode node) {
        
        ReflectActionListener ral = new ReflectActionListener(this, LISTENER_METHOD, 
                            new Class[]{JComponent.class, TransferHandler.class, LocalStampTreeNodeTransferable.class},
                            new Object[]{textPane, textPane.getTransferHandler(), new LocalStampTreeNodeTransferable(node)});
        
        item.addActionListener(ral);
    }

    protected void showPopup() {
        
        if (popup == null || popup.getComponentCount() < 1) {
            return;
        }
        
        try {
            int pos = textPane.getCaretPosition();
            Rectangle r = textPane.modelToView(pos);
            popup.show (textPane, r.x, r.y);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void importStamp(JComponent comp, TransferHandler handler, LocalStampTreeNodeTransferable tr) {
        textPane.setSelectionStart(start);
        textPane.setSelectionEnd(end);
        textPane.replaceSelection("");
        handler.importData(comp, tr);
        closePopup();
    }
    
    protected void closePopup() {
        if (popup != null) {
            popup.removeAll();
            popup = null;
        }
    }

    /**
     * 単語の境界からキャレットの位置までのテキストを取得し、
     * 長さがゼロ以上でれば補完メニューをポップアップする。
     */
    protected void buildAndShowPopup() {

        end = textPane.getCaretPosition();
        start = end;
        boolean found = false;

        while (start > 0) {
            
            start--;
  
            try {
                String text = textPane.getText(start, 1);
                for (String test : WORD_SEPARATOR) {
                    if (test.equals(text)) {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    start++;
                    break;
                }
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            
            String str = textPane.getText(start, end - start);
            
            if (str.length() > 0) {
                buildPopup(str);
                showPopup();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
