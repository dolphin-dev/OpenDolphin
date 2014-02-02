/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.utilities.common;

/**
 * 共通メソッド
 * @author S.Oh@Life Sciences Computing Corporation.
 */
public abstract class AbstractCommonFunc implements CommonDefImpl {
    protected boolean debugInfo;
    
    /**
     * コンストラクタ
     */
    public AbstractCommonFunc() {
        debugInfo = false;
    }
    
    /**
     * 初期化
     */
    abstract void Init();
    
    /**
     * デバッグ情報の有無設定
     * @param dbg デバッグ情報の有無
     */
    protected void setDebug(boolean dbg) {
        debugInfo = dbg;
    }
    
    /**
     * デバッグ情報の取得
     * @return デバッグ情報の有無
     */
    protected boolean getDebug() {
        return debugInfo;
    }
}
