package open.dolphin.client;

import java.awt.*;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import open.dolphin.infomodel.IInfoModel;

import open.dolphin.infomodel.ModuleInfoBean;

/**
 * StampTreePanel
 *
 * @author  Kazushi Minagawa
 */
public class StampTreePanel extends JPanel implements TreeSelectionListener {

    private static final long serialVersionUID = -268963413379453444L;
    protected StampTree stampTree;
    protected JTextArea infoArea;

    /** Creates a new instance of StampTreePanel */
    public StampTreePanel(StampTree tree) {

        this.stampTree = tree;
        JScrollPane scroller = new JScrollPane(stampTree);
        this.setLayout(new BorderLayout());
        this.add(scroller, BorderLayout.CENTER);

        String treeEntity = stampTree.getEntity();
        if (treeEntity != null && (!treeEntity.equals(IInfoModel.ENTITY_TEXT))) {
            infoArea = new JTextArea();
            infoArea.setMargin(new Insets(3, 2, 3, 2));
            infoArea.setLineWrap(true);
            infoArea.setPreferredSize(new Dimension(250, 40));
            Font font = GUIFactory.createSmallFont();
            infoArea.setFont(font);
            this.add(infoArea, BorderLayout.SOUTH);
            tree.addTreeSelectionListener(this);
        }
    }

    /**
     * このパネルのStampTreeを返す。
     * @return StampTree
     */
    public StampTree getTree() {
        return stampTree;
    }

    /**
     * スタンプツリーで選択されたスタンプの情報を表示する。
     */
    public void valueChanged(TreeSelectionEvent e) {
        StampTree tree = (StampTree) e.getSource();
        StampTreeNode node = (StampTreeNode) tree.getLastSelectedPathComponent();
        if (node != null) {
            if (node.getUserObject() instanceof ModuleInfoBean) {
                ModuleInfoBean info = (ModuleInfoBean) node.getUserObject();
                infoArea.setText(info.getStampMemo());
            } else {
                infoArea.setText("");
            }
        } else {
            infoArea.setText("");
        }
    }
}
