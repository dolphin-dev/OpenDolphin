/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.utilities.control;

import java.awt.Cursor;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

/**
 * GlassPane拡張クラス
 * @author S.Oh@Life Sciences Computing Corporation.
 */
public class GlassPaneEx extends JComponent {
    /**
     * コンストラクタ
     * @param cursor 
     */
    public GlassPaneEx(boolean cursor) {
        setOpaque(false);
        if(cursor) {
            super.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        }
    }
    
    /**
     * 有効/無効
     * @param isVisible 有効/無効
     */
    @Override
    public void setVisible(boolean isVisible) {
        boolean before = isVisible();
        super.setVisible(isVisible);
        JRootPane rootPane = SwingUtilities.getRootPane(this);
        if(rootPane != null && isVisible() != before) {
            rootPane.getLayeredPane().setVisible(!isVisible);
        }
    }
    
    /**
     * 描画
     * @param g グラフィック
     */
    @Override
    public void paintComponent(Graphics g) {
        JRootPane rootPane = SwingUtilities.getRootPane(this);
        if(rootPane != null) {
            rootPane.getLayeredPane().print(g);
        }
        super.paintComponent(g);
    }
    
    public static void main( String[] args ) {
        //frame.setGlassPane(new GlassPaneEx(true));
        //frame.setVisible(true);
    }
}
