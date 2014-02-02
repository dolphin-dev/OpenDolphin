package open.dolphin.client;

import javax.swing.*;

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
     * @param resource リソース名
     * @target メソッドを実行するオブジェクト
     */
    public static JPopupMenu create(String resource, Object target) {
        
        JPopupMenu popMenu = new JPopupMenu ();
        
        String[] itemLine = ClientContext.getStringArray(resource + ".items");
        String[] methodLine = ClientContext.getStringArray(resource + ".methods");
        
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
            }
        }
        return popMenu;
    }
}


