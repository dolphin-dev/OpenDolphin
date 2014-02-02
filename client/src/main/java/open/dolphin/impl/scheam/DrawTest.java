package open.dolphin.impl.scheam;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import open.dolphin.infomodel.ExtRefModel;
import open.dolphin.infomodel.SchemaModel;

/**
 *
 * @author pns
 */
public class DrawTest {

    public static void main (String[] args) {
        new DrawTest().startup();
    }

    protected void startup() {

        //boolean QUAQUA = true;
        boolean QUAQUA = false;

        if (QUAQUA) {
            try {
                UIManager.setLookAndFeel("ch.randelshofer.quaqua.QuaquaLookAndFeel");
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(DrawTest.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InstantiationException ex) {
                Logger.getLogger(DrawTest.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(DrawTest.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnsupportedLookAndFeelException ex) {
                Logger.getLogger(DrawTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        SchemaEditorImpl editor = new SchemaEditorImpl();
        SchemaModel schema = new SchemaModel();
        String sample1 = "/open/dolphin/impl/scheam/resources/Sample-square.JPG";
        String sample2 = "/open/dolphin/impl/scheam/resources/Sample-large.JPG";
        String sample3 = "/open/dolphin/impl/scheam/resources/Sample-landscape.JPG";
        String sample4 = "/open/dolphin/impl/scheam/resources/Sample-portrait.JPG";

        InputStream in = getClass().getResourceAsStream(sample2);

        byte[] buf = null;
        try {
            int n = in.available();
            buf = new byte[n];
            for(int i=0; i<n; i++) buf[i] = (byte) in.read();
        } catch (IOException ex) {
        }
        schema.setIcon(new ImageIcon(buf));

        ExtRefModel ref = new ExtRefModel();
        ref.setContentType("image/jpeg");
        ref.setTitle("Schema Image");
        schema.setExtRefModel(ref);
        schema.setFileName("Test");
        ref.setHref("Test");

        editor.addPropertyChangeListener(new PropertyChangeListener(){
            public void propertyChange(PropertyChangeEvent evt) {
//                System.out.println("oldValue = " + evt.getOldValue());
//                System.out.println("newValue = " + evt.getNewValue());
            }
        });

        editor.setSchema(schema);
        editor.setEditable(true);
        editor.start();
    }
}
