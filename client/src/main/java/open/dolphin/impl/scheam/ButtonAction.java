package open.dolphin.impl.scheam;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.JButton;
import javax.swing.JToggleButton;

/**
 * Button Action 登録
 * @author pns
 */
public class ButtonAction {
    private final SchemaCanvas canvas;
    private final StateMgr stateMgr;

    public ButtonAction(SchemaEditorImpl context) {
        this.canvas = context.getCanvas();
        this.stateMgr = context.getStateMgr();
    }

    public void register(JToggleButton button, String name, Cursor cursor) {
        button.addActionListener(new ButtonActionListener(name, cursor));
    }

    public void register(JButton button, String name, Cursor cursor) {
        button.addActionListener(new ButtonActionListener(name, cursor));
    }

    private class ButtonActionListener implements ActionListener {
        private final Cursor cursor;
        private final String name;

        public ButtonActionListener(String name, Cursor cursor) {
            super();
            this.cursor = cursor;
            this.name = name;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (cursor != null) canvas.setCursor(cursor);
                Method method = stateMgr.getClass().getMethod(name, (Class[])null);
                method.invoke(stateMgr, (Object[])null);
            } catch (NoSuchMethodException | SecurityException ex) {
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) { System.out.println(ex);
            }
        }
    }
}
