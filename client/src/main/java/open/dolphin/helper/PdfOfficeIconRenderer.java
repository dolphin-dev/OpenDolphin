package open.dolphin.helper;

import java.awt.Color;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import open.dolphin.client.ClientContext;
import open.dolphin.client.NameValuePair;

/**
 *
 * @author Kazushi Minagawa.
 */
public class PdfOfficeIconRenderer extends DefaultListCellRenderer {
    
//minagawa^ Icon Server    
////s.oh^ プレイン文書アイコンの追加
//    private static final ImageIcon ICON_PLAIN = ClientContext.getImageIcon("plain_icon16.png");
////s.oh$
//    private static final ImageIcon ICON_PDF = ClientContext.getImageIcon("pdf_icon16.png");
//    private static final ImageIcon ICON_OFFICE = ClientContext.getImageIcon("docs_16.png");
    //s.oh^ プレイン文書アイコンの追加
    private static final ImageIcon ICON_PLAIN = ClientContext.getImageIconArias("icon_plain_document_small");
//s.oh$
    private static final ImageIcon ICON_PDF = ClientContext.getImageIconArias("icon_pdf_small");
    private static final ImageIcon ICON_OFFICE = ClientContext.getImageIconArias("icon_plain_document_small");
//minagawa$    
    
    private static final Color DEFAULT_ODD_COLOR = ClientContext.getColor("color.odd");
    private static final Color DEFAULT_EVENN_COLOR = ClientContext.getColor("color.even");
    
    public PdfOfficeIconRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value,
                index, isSelected, cellHasFocus);

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
            
        } else {
            
            setForeground(list.getForeground());
            
            if (index % 2 == 0) {
                setBackground(DEFAULT_EVENN_COLOR);
            } else {
                setBackground(DEFAULT_ODD_COLOR);
            }
        }
        
        if (value!=null) {
            NameValuePair pair = (NameValuePair)value;
            String test = pair.getValue();
            if (test!=null) {
                if (test.endsWith(".odt")) {
                    label.setIcon(ICON_OFFICE);
                } else if (test.endsWith("KarteEditor")) {
//s.oh^ プレイン文書アイコンの追加
                    //label.setIcon(null);
                    label.setIcon(ICON_PLAIN);
//s.oh$
                } else {
                    label.setIcon(ICON_PDF);
                }
            }
        } else {
            label.setIcon(null);
        }
        return label;
    }
}