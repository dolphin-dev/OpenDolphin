/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.utilities.control;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.*;

/**
 * ツールチップ拡張クラス
 * @author S.Oh@Life Sciences Computing Corporation.
 */
public class ToolTipEx extends JToolTip {
    private Color foreColor;
    private Color backColor;
    
    /**
     * コンストラクタ
     * @param fore 文字色
     * @param back 背景色
     */
    public ToolTipEx(Color fore, Color back) {
        super();
        foreColor = fore;
        backColor = back;
    }
    
    /**
     * 画像ツールチップの作成
     * @param icon アイコン
     * @return ツールチップ
     */
    public JToolTip CreateImageToolTip(ImageIcon icon) {
        final JLabel iconLabel = new JLabel(icon);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
        LookAndFeel.installColorsAndFont(iconLabel, "ToolTip.background", "ToolTip.foreground", "ToolTip.font");
        JToolTip tooltip = new JToolTip() {
            @Override
            public Dimension getPreferredSize() {
                return getLayout().preferredLayoutSize(this);
            }
            @Override
            public void setTipText(final String tipText) {
                String oldValue = iconLabel.getText();
                iconLabel.setText(tipText);
                firePropertyChange("tiptext", oldValue, tipText);
            }
        };
        tooltip.setComponent(this);
        tooltip.setLayout(new BorderLayout());
        tooltip.add(iconLabel);
        tooltip.setForeground(foreColor);
        tooltip.setBackground(backColor);
        return tooltip;
    }
    
    /**
     * 描画
     * @param g グラフィック
     */
    @Override
    public void paint(Graphics g) {
        setForeground(foreColor);
        setBackground(backColor);
        super.paint(g);
    }
    
    public static void main( String[] args ) {
        //JFrame frame = new JFrame();
        //JScrollPane pane = new JScrollPane(new JTable() {
        //    @Override
        //    public JToolTip createToolTip() {
        //        if(アイコン) {
        //            ImageIconEx icon = new ImageIconEx();
        //            ToolTipEx tool = new ToolTipEx(Color.BLACK, Color.WHITE);
        //            return tool.CreateImageToolTip(icon.getIcon());
        //        }else if(テキスト) {
        //            return new ToolTipEx(Color.BLACK, Color.WHITE);
        //        }
        //        return null;
        //    }
        //
        //    @Override
        //    public String getToolTipText(MouseEvent e) {
        //        String ret = null;
        //        if(テキスト) {
        //            Object obj = this.getModel().getValueAt(rowAtPoint(e.getPoint()), columnAtPoint(e.getPoint()));
        //            if(obj instanceof ImageIconEx) {
        //                ImageIconEx icon = (ImageIconEx)this.getModel().getValueAt(rowAtPoint(e.getPoint()), columnAtPoint(e.getPoint()));
        //                ret = icon.getText();
        //            }else{
        //                ret = (String)this.getModel().getValueAt(rowAtPoint(e.getPoint()), columnAtPoint(e.getPoint()));
        //                if(ret.isEmpty()) return null;
        //            }
        //        }else if(アイコン) {
        //            ret = "";
        //        }
        //        return  ret;
        //    }
        //});
    }
}
