package open.dolphin.impl.scheam;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

/**
 *
 * @author Kazushi Minagawa, pns
 */
public class SchemaCanvas extends JComponent {
    
    private SchemaEditorImpl context;
    private SchemaEditorProperties properties;

    // baseImage はここで管理する
    private BufferedImage baseImage;
        
    public SchemaCanvas(SchemaEditorImpl context) {
        super();
        this.context = context;
        this.properties = context.getProperties();
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // baseImage はデフォルト設定で描く
        g2d.drawImage(baseImage, 0, 0, null);

        if (context != null) {
            // 現在の色，ストロークなどを設定
            properties.setGraphicsState(g2d);
            // draw の流れ
            // baseImage → DrawingHolder#draw() → 現在のstate#draw()
            context.getStateMgr().draw(g2d);
        }
    }

    public final void setBaseImage(BufferedImage image) {
        this.baseImage = image;
        this.setPreferredSize(new Dimension(baseImage.getWidth(), baseImage.getHeight()));
    }

    public final BufferedImage getBaseImage() {
        return baseImage;
    }
}
