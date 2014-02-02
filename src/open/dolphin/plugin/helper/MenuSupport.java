package open.dolphin.plugin.helper;

import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.Action;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

/**
 * MenuSupport
 *
 * @author Minagawa,Kazushi
 *
 */
public class MenuSupport implements MenuListener {
    
    private Hashtable<String, Action> actions;
    private Object[] chains;
    
    public MenuSupport(Object owner) {
        Object[] chs = new Object[3];
        chs[1] = this;
        chs[2] = owner;
        setChains(chs);
    }
    
    public void menuSelected(MenuEvent e) {
    }
    
    public void menuDeselected(MenuEvent e) {
    }
    
    public void menuCanceled(MenuEvent e) {
    }
    
    public void registerActions(Hashtable<String, Action> actions) {
        this.actions = actions;
    }
    
    public Action getAction(String name) {
        return actions.get(name);
    }
    
    public Hashtable<String, Action> getActions() {
        return actions;
    }
    
    public void disableAllMenus() {
        if (actions != null) {
            Iterator<Action> iter = actions.values().iterator();
            while (iter.hasNext()) {
                iter.next().setEnabled(false);
            }
        }
    }
    
    public void disableMenus(String[] menus) {
        if (menus != null && actions != null) {
            for (String name : menus) {
                actions.get(name).setEnabled(false);
            }
        }
    }
    
    public void enableMenus(String[] menus) {
        if (menus != null && actions != null) {
            for (String name : menus) {
                actions.get(name).setEnabled(true);
            }
        }
    }
    
    public void setChains(Object[] chains) {
        this.chains = chains;
    }
    
    public Object[] getChains() {
        return chains;
    }
    
    public void addChain(Object obj) {
        // 最初のターゲットに設定する
        chains[0] = obj;
    }
    
    public Object getChain() {
        // 最初のターゲットを返す
        return chains[0];
    }
    
    /**
     * chain にそってリフレクションでメソッドを実行する。
     * メソッドを実行するオブジェクトがあればそこで終了する。
     * メソッドを実行するオブジェクトが存在しない場合もそこで終了する。
     * コマンドチェインパターンのリフレクション版。
     * @param obj
     * @return メソッドが実行された時 true
     */
    public boolean sendToChain(String method) {
        
        boolean handled = false;
        
        if (chains != null) {
            
            for (Object target : chains) {
                
                if (target != null) {
                    try {
                        Method mth = target.getClass().getMethod(method, (Class[])null);
                        mth.invoke(target, (Object[])null);
                        handled = true;
                        break;
                    } catch (Exception e) {
                        // この target では実行できない
                    }
                }
            }
        }
        
        return handled;
    }
        
//    public boolean sendToChain(String method, Class[] clss, Object[] args) {
//        
//        boolean handled = false;
//        
//        if (chains != null) {
//            
//            for (Object target : chains) {
//                
//                if (target != null) {
//                    try {
//                        Method mth = target.getClass().getMethod(method, clss);
//                        mth.invoke(target, args);
//                        handled = true;
//                        break;
//                    } catch (Exception e) {
//                        // この target では実行できない
//                    }
//                }
//            }
//        }
//        
//        return handled;
//    }
}