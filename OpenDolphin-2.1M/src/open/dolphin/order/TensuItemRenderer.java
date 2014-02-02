package open.dolphin.order;

import java.awt.Color;
import java.awt.Component;
import java.util.regex.Pattern;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import open.dolphin.infomodel.ClaimConst;
import open.dolphin.infomodel.TensuMaster;
import open.dolphin.table.ListTableModel;

/**
 *
 * @author Kazushi Minagawa.
 */
public final class TensuItemRenderer extends JLabel implements TableCellRenderer {
    
    private static final Color THEC_COLOR = new Color(204,255,102);
    private static final Color MEDICINE_COLOR = new Color(255,204,0);
    private static final Color MATERIAL_COLOR = new Color(153,204,255);
    private static final Color OTHER_COLOR = new Color(255,255,255);

    private Pattern passPattern;
    private Pattern shinkuPattern;
    
    public TensuItemRenderer(Pattern passPattern, Pattern shinkuPattern) {
        setOpaque(true);
        this.passPattern = passPattern;
        this.shinkuPattern = shinkuPattern;
    }
    
    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column ) {
        
        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
            
        } else {

            setForeground(table.getForeground());

            ListTableModel<TensuMaster> tm = (ListTableModel<TensuMaster>)table.getModel();
            TensuMaster item = tm.getObject(row);

            if (item!=null) {

                String slot = item.getSlot();

                if (passPattern!=null && passPattern.matcher(slot).find()) {

                    String srycd = item.getSrycd();

                    if (srycd.startsWith(ClaimConst.SYUGI_CODE_START) &&
                            shinkuPattern!=null
                            && shinkuPattern.matcher(item.getSrysyukbn()).find()) {
                        setBackground(THEC_COLOR);

                    } else if (srycd.startsWith(ClaimConst.YAKUZAI_CODE_START)) {
                        //内用1、外用6、注射薬4
                        String ykzkbn = item.getYkzkbn();

                        if (ykzkbn.equals(ClaimConst.YKZ_KBN_NAIYO)) {
                            setBackground(MEDICINE_COLOR);

                        } else if (ykzkbn.equals(ClaimConst.YKZ_KBN_INJECTION)) {
                            setBackground(MEDICINE_COLOR);

                        } else if (ykzkbn.equals(ClaimConst.YKZ_KBN_GAIYO)) {
                            setBackground(MEDICINE_COLOR);

                        } else {
                            setBackground(OTHER_COLOR);
                        }

                    } else if (srycd.startsWith(ClaimConst.ZAIRYO_CODE_START)) {
                        setBackground(MATERIAL_COLOR);

                    } else if (srycd.startsWith(ClaimConst.ADMIN_CODE_START)) {
                        setBackground(OTHER_COLOR);

                    } else if (srycd.startsWith(ClaimConst.RBUI_CODE_START)) {
                        setBackground(THEC_COLOR);

                    } else {
                        setBackground(OTHER_COLOR);
                    }

                } else {
                    setBackground(OTHER_COLOR);
                }

            } else {
                setBackground(OTHER_COLOR);
            }
        }
        
        //-------------------------------------------------------
        if (value != null) {
            
            if (value instanceof java.lang.String) {
                this.setText((String) value);
                
            } else {
                this.setText(value.toString());
            }
            
        } else {
            this.setText("");
        }
        //-------------------------------------------------------
        
        return this;
    }
}
