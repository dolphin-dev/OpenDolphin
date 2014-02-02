package open.dolphin.order;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import open.dolphin.helper.ComponentMemory;
import open.dolphin.infomodel.ModuleModel;


/**
 * Stamp ï“èWópÇÃäOògÇíÒãüÇ∑ÇÈ Dialog.
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class StampEditor implements PropertyChangeListener {

    private AbstractStampEditor editor;

    private JDialog dialog;

    /**
     * Constructor. Use layered inititialization pattern.
     */
    public StampEditor(final ModuleModel stamp, final PropertyChangeListener listener)  {

        Runnable r = new Runnable() {

           @Override
           public void run() {

                String entity = stamp.getModuleInfo().getEntity();

                if (entity.equals("medOrder")) {
                    editor = new RpEditor(entity);

                } else if (entity.equals("radiologyOrder")) {
                    editor = new RadEditor(entity);

                } else {
                    editor = new BaseEditor(entity);
                }

                editor.addPropertyChangeListener(AbstractStampEditor.VALUE_PROP, listener);
                editor.addPropertyChangeListener(AbstractStampEditor.EDIT_END_PROP, StampEditor.this);
                editor.setValue(stamp);

                dialog = new JDialog(new JFrame(), true);
                dialog.setTitle(editor.getOrderName());
                dialog.getContentPane().add(editor.getView(), BorderLayout.CENTER);
                dialog.addWindowListener(new WindowAdapter() {

                    @Override
                    public void windowClosing(WindowEvent e) {
                        dialog.dispose();
                        dialog.setVisible(false);
                    }
                });

                dialog.pack();
                ComponentMemory cm = new ComponentMemory(dialog, new Point(200,100), dialog.getPreferredSize(), this);
                cm.setToPreferenceBounds();

                dialog.setVisible(true);
            }
        };

        SwingUtilities.invokeLater(r);
    }

    public StampEditor(String entity, final PropertyChangeListener listener, final Window lock) {

        Runnable r = new Runnable() {

            @Override
            public void run() {

                editor = new DiseaseEditor();
                editor.addPropertyChangeListener(AbstractStampEditor.VALUE_PROP, listener);
                editor.addPropertyChangeListener(AbstractStampEditor.EDIT_END_PROP, StampEditor.this);

                dialog = new JDialog((Frame) lock, true);
                dialog.setTitle(editor.getOrderName());
                dialog.getContentPane().add(editor.getView(), BorderLayout.CENTER);
                dialog.addWindowListener(new WindowAdapter() {

                    @Override
                    public void windowClosing(WindowEvent e) {
                        dialog.dispose();
                        dialog.setVisible(false);
                    }
                });

                dialog.pack();
                ComponentMemory cm = new ComponentMemory(dialog, new Point(200,100), dialog.getPreferredSize(), this);
                cm.setToPreferenceBounds();

                dialog.setVisible(true);
            }
        };

        SwingUtilities.invokeLater(r);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        if (evt.getPropertyName().equals(AbstractStampEditor.EDIT_END_PROP)) {
            Boolean b = (Boolean) evt.getNewValue();
            if (b.booleanValue()) {
                dialog.dispose();
                dialog.setVisible(false);
            }
        }
    }
}