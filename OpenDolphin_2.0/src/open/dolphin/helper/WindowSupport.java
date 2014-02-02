package open.dolphin.helper;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

/**
 * Window Menu をサポートするためのクラス。
 * Factory method で WindowMenu をもつ JFrame を生成する。
 *
 * @author Minagawa,Kazushi
 */
public class WindowSupport implements MenuListener {
    
    private static ArrayList<WindowSupport> allWindows = new ArrayList<WindowSupport>(5);
    
    private static final String WINDOW_MWNU_NAME = "ウインドウ";
    
    // Window support が提供するスタッフ
    // フレーム
    private JFrame frame;
    
    // メニューバー
    private JMenuBar menuBar;
    
    // ウインドウメニュー
    private JMenu windowMenu;
    
    // Window Action
    private Action windowAction;
    
    /**
     * WindowSupportを生成する。
     * @param title フレームタイトル
     * @return WindowSupport
     */
    public static WindowSupport create(String title) {
        
        // フレームを生成する
        final JFrame frame = new JFrame(title);
        
        // メニューバーを生成する
        JMenuBar menuBar = new JMenuBar();
        
        // Window メニューを生成する
        JMenu windowMenu = new JMenu(WINDOW_MWNU_NAME);
        
        // メニューバーへWindow メニューを追加する
        menuBar.add(windowMenu);
        
        // フレームにメニューバーを設定する
        frame.setJMenuBar(menuBar);
        
        // Windowメニューのアクション
        // 選択されたらフレームを全面にする
        Action windowAction = new AbstractAction(title) {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.toFront();
            }
        };
        
        // インスタンスを生成する
        final WindowSupport ret
                = new WindowSupport(frame, menuBar, windowMenu, windowAction);
        
        // WindowEvent をこのクラスに通知しリストの管理を行う
        frame.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            
            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
                WindowSupport.windowOpened(ret);
            }
            
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                WindowSupport.windowClosed(ret);
            }
        });
        
        // windowMenu にメニューリスナを設定しこのクラスで処理をする
        windowMenu.addMenuListener(ret);
        return ret;
    }
    
    public static ArrayList getAllWindows() {
        return allWindows;
    }
    
    public static void windowOpened(WindowSupport opened) {
        // リストに追加する
        allWindows.add(opened);
    }
    
    public static void windowClosed(WindowSupport closed) {
        // リストから削除する
        allWindows.remove(closed);
        closed = null;
    }
    
    public static boolean contains(WindowSupport toCheck) {
        return allWindows.contains(toCheck);
    }
    
    // プライベートコンストラクタ
    private WindowSupport(JFrame frame, JMenuBar menuBar, JMenu windowMenu,
            Action windowAction) {
        this.frame = frame;
        this.menuBar = menuBar;
        this.windowMenu = windowMenu;
        this.windowAction = windowAction;
    }
    
    public JFrame getFrame() {
        return frame;
    }
    
    public JMenuBar getMenuBar() {
        return menuBar;
    }
    
    public JMenu getWindowMenu() {
        return windowMenu;
    }
    
    public Action getWindowAction() {
        return windowAction;
    }
    
    /**
     * ウインドウメニューが選択された場合、現在オープンしているウインドウのリストを使用し、
     * それらを選択するための MenuItem を追加する。
     */
    @Override
    public void menuSelected(MenuEvent e) {
        
        // 全てリムーブする
        JMenu wm = (JMenu) e.getSource();
        wm.removeAll();
        
        // リストから新規に生成する
        for (WindowSupport ws : allWindows) {
            Action action = ws.getWindowAction();
            wm.add(action);
        }
    }
    
    @Override
    public void menuDeselected(MenuEvent e) {
    }
    
    @Override
    public void menuCanceled(MenuEvent e) {
    }
}
