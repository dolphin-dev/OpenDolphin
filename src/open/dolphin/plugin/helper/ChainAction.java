/*
 */
package open.dolphin.plugin.helper;

import javax.swing.*;

import java.awt.event.*;

/**
 * ChainAction
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class ChainAction extends AbstractAction {
    
    private static final long serialVersionUID = -8729508189547074832L;
    
    private MenuSupport target;
    private String method;
    
    
    public void setTarget(MenuSupport target) {
        this.target = target;
    }
    
    public void setMethod(String method) {
        this.method = method;
    }
    
    /**
     * Creates a new instance of ChainAction.
     * @param text メニューイテム名
     * @param target	メニューサポート chain の発火元
     * @pram method メソッド名
     */
    public ChainAction(String text, MenuSupport target, String method) {
        super(text);
        setTarget(target);
        setMethod(method);
    }
    
    /**
     * Creates a new instance of ChainAction.
     * @param text メニューイテム名
     * @param icon アイテムのアイコン
     * @param target メニューサポート
     * @param method メソッド名
     */
    public ChainAction(String text, Icon icon, MenuSupport target, String method) {
        super(text, icon);
        setTarget(target);
        setMethod(method);
    }
    
    /**
     * メニューサポートへメソッドを送信する。
     * メニューサポートがオブジェクトの chain を管理していて
     * 順番にリフレクションを使用してメソッドを実行する。
     * メニューサポートはメインウインドウ、チャートウインドウ等の
     * メニュバーを持つオブジェクトが保有している。
     */
    public void actionPerformed(ActionEvent e) {
        target.sendToChain(method);
    }
}