package open.dolphin.client;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * リソースデータから PopupMenu を生成するクラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class PopupMenuFactory {
    
    private PopupMenuFactory () {
    }
    
    /**
     * リソースとターゲットオブジェクトから PopupMenu を生成して返す。
     * @param itemLine
     * @param methodLine
     * @param target
     * @param canCopy
     * @param canPaste
     * @return 
     * @target メソッドを実行するオブジェクト
     */
    public static JPopupMenu create(String[] itemLine, String[] methodLine, Object target, boolean canCopy, boolean canPaste) {
        
        JPopupMenu popMenu = new JPopupMenu ();
        
        for (int i = 0; i < itemLine.length; i++) {
            
            String name = itemLine[i];
            String method = methodLine[i];
            
            if (name.equals("-")) {
                popMenu.addSeparator();
            }
            else {
                ReflectAction action = new ReflectAction(name, target, method);
                JMenuItem item = new JMenuItem(action);
                popMenu.add(item);
                
                if (method.equals("copy")) {
                    action.setEnabled(canCopy);
                } else if (method.equals("paste")) {
                    action.setEnabled(canPaste);
                }
            }
        }
        return popMenu;
    }
}


