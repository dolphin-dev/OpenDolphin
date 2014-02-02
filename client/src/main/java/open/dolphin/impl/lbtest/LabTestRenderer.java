package open.dolphin.impl.lbtest;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import open.dolphin.client.ClientContext;
import open.dolphin.infomodel.LabTestRowObject;
import open.dolphin.infomodel.LabTestValueObject;
import open.dolphin.table.StripeTableCellRenderer;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc
 * @author modified by masuda, Masuda Naika
 */
public class LabTestRenderer extends StripeTableCellRenderer {

    private Color penCol;
    
    private static final Color specimenColor = ClientContext.getColor("labotest.color.specimen");

    public LabTestRenderer() {
        setOpaque(true);
        setBackground(Color.white);
        setHorizontalAlignment(LEFT);
        setVerticalAlignment(CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        //-------------------------------------------------------
        if (value != null) {

            LabTestRowObject rowObj = (LabTestRowObject) value;
            
////masuda^   検体名の場合
//            String specimenName = rowObj.getSpecimenName();
//            if (specimenName != null) {
//                setText(specimenName);
//                setBackground(specimenColor);
//                setForeground(Color.BLACK);
//                return this;
//            }
////masuda$
//            
            if (column == 0) {

                // テスト項目名(単位）を表示する
                penCol = Color.black;
//masuda^   項目名は選択すると白抜きにする
                if (!isSelected) {
                    setForeground(penCol);
                }
//masuda$
                setText(rowObj.nameWithUnit());
                String toolTip = rowObj.getNormalValue() != null ? rowObj.getNormalValue() : "";
                setToolTipText(toolTip);

            } else {

                // column-1番目の値オブジェクトwp取り出す
                LabTestValueObject valueObj = rowObj.getLabTestValueObjectAt(column -1);

                String text = valueObj != null ? valueObj.getValue() : "";
                String flag = valueObj != null ? valueObj.getOut() : null;
                String toolTip = valueObj != null ? valueObj.concatComment() : "";

                if (flag != null && flag.startsWith("H")) {
                    penCol = Color.RED;
                } else if (flag != null && flag.startsWith("L")) {
                    penCol = Color.BLUE;
                } else if (toolTip!= null && (!toolTip.equals(""))) {
                    penCol = Color.MAGENTA;
                } else {
                    penCol = Color.black;
                }

                setForeground(penCol);
                setText(text);
                setToolTipText(toolTip);
            }

        } else {
            penCol = Color.black;
            setForeground(penCol);
            setText("");
            setToolTipText("");
        }
        //-------------------------------------------------------

        return this;
    }
}
