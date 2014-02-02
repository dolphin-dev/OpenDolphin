package open.dolphin.client;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Kazushi Minagawa. Digital Globe, Inc.
 */
public class CutCopyPasteAdapter extends MouseAdapter {

    private static final String CUT = "カット";
    private static final String COPY = "コピー";
    private static final String PASTE = "ペースト";

    private static CutCopyPasteAdapter instance = new CutCopyPasteAdapter();
    
    private CutCopyPasteAdapter() {
    }

    public static CutCopyPasteAdapter getInstance() {
        return instance;
    }

    private void mabeShowPopup(MouseEvent e) {

        if (e.isPopupTrigger()) {

            JTextComponent tc = (JTextComponent)e.getSource();

            JPopupMenu pop = new JPopupMenu();

            JMenuItem cutItem = new JMenuItem(new DefaultEditorKit.CutAction());
            cutItem.setText(CUT);
            cutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            pop.add(cutItem);

            JMenuItem copyItem = new JMenuItem(new DefaultEditorKit.CopyAction());
            copyItem.setText(COPY);
            copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            pop.add(copyItem);

            JMenuItem pasteItem = new JMenuItem(new DefaultEditorKit.PasteAction());
            pasteItem.setText(PASTE);
            pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            pop.add(pasteItem);

            boolean hasSelection = tc.getSelectionStart()!=tc.getSelectionEnd() ? true : false;

            cutItem.setEnabled(tc.isEditable() && hasSelection);

            copyItem.setEnabled(hasSelection);

            Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            boolean canPaste = true;
            canPaste = canPaste && (t!=null);
            canPaste = canPaste && (t!=null && (t.isDataFlavorSupported(DataFlavor.stringFlavor) ||
                                                t.isDataFlavorSupported(OrderListTransferable.orderListFlavor)));
            pasteItem.setEnabled(canPaste);
            
            pop.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mabeShowPopup(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mabeShowPopup(e);
    }
}
