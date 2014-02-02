package open.dolphin.impl.scheam.schemastate;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import javax.swing.JOptionPane;
import open.dolphin.impl.scheam.SchemaCanvasDialog2;
import open.dolphin.impl.scheam.SchemaEditorImpl;

/**
 *
 * @author pns
 */
public class TextState extends AbstractState {

    private String inputText;
    private int[] fontSizeList = {12,16,20,24,28};

    public TextState(SchemaEditorImpl context) {
        super(context);
    }

    @Override
    public void mouseDown(Point p) {

        TextPanel textPanel = new TextPanel();
        textPanel.setFontSizeList(fontSizeList);
        textPanel.setFontSize(properties.getFontSize());

        SchemaCanvasDialog2 dialog = new SchemaCanvasDialog2(context.getCanvasView(), true);
        dialog.addContent(textPanel);
        dialog.setTitle("テキスト入力");
        dialog.setVisible(true);
        
        int result = dialog.getResult();
        inputText = textPanel.getText();
        properties.setFontSize(textPanel.getFontSize());

        if (result == JOptionPane.OK_OPTION && inputText != null && !inputText.equals("")) {
            start = p;
            end = null;
            canvas.repaint();
            
        } else {
            start = null;
            end = null;
        }
    }

    @Override
    public void mouseDragged(Point p) {
    }

    @Override
    public void mouseUp(Point p) {
    }

    @Override
    public void draw(Graphics2D g2d) {

        if (inputText != null && start != null) {
            undoMgr.storeDraw();

            FontRenderContext ctx = g2d.getFontRenderContext();
            Font f = properties.getFont();

            TextLayout layout = new TextLayout(inputText, f, ctx);
            AffineTransform trans = AffineTransform.getTranslateInstance(start.getX(), start.getY());
            Shape outLine = layout.getOutline(trans);

            g2d.setStroke(properties.getTextStroke());
            g2d.setPaint(properties.getTextColor());
            g2d.fill(outLine);

            addTextShape(outLine);
            
            inputText = null;
        }
    }
}
