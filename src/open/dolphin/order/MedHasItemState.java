package open.dolphin.order;

import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;

/**
 * ValidState
 *
 * @author Minagawa,Kazushi
 */
public class MedHasItemState extends AbstractMedTableState {

    public MedHasItemState(JTable medTable, JButton deleteBtn, JButton clearBtn,
            JTextField stampNameField, JLabel stateLabel) {
        super(medTable, deleteBtn, clearBtn, stampNameField, stateLabel);
    }

    public void enter() {

        clearBtn.setEnabled(true);
        int index = medTable.getSelectedRow();
        Object obj = getTableModel().getObject(index);
        if (obj != null && (!deleteBtn.isEnabled())) {
            deleteBtn.setEnabled(true);
        } else if (obj == null && deleteBtn.isEnabled()) {
            deleteBtn.setEnabled(false);
        }

        if (!hasMedicine()) {
            stateLabel.setText("医薬品を入力してください。");
            return;
        }

        if (!hasAdmin()) {
            stateLabel.setText("用法を入力してください。");
            return;
        }

        if (!isNumberOk()) {
            stateLabel.setText("数量が正しくありません。");
            return;
        }

        stateLabel.setText("カルテに展開できます。");
    }

    public boolean isValidModel() {
        return (hasMedicine() && hasAdmin() && isNumberOk()) ? true : false;
    }

    private boolean hasMedicine() {

        boolean medicineOk = false;
        List list = getTableModel().getObjectList();

        for (Iterator iter = list.iterator(); iter.hasNext();) {
            MasterItem mItem = (MasterItem) iter.next();
            if (mItem.getClassCode() == ClaimConst.YAKUZAI) {
                medicineOk = true;
                break;
            }
        }

        return medicineOk;
    }

    private boolean hasAdmin() {

        boolean adminOk = false;
        List list = getTableModel().getObjectList();

        for (Iterator iter = list.iterator(); iter.hasNext();) {
            MasterItem mItem = (MasterItem) iter.next();
            if (mItem.getClassCode() == ClaimConst.ADMIN) {
                adminOk = true;
                break;
            }
        }

        return adminOk;
    }

    private boolean isNumber(String test) {
        
        boolean result = true;
        
        try {
            Float num = Float.parseFloat(test);
            if (num < 0F || num == 0F) {
                result = false;
            }
            
        } catch (Exception e) {
            result = false;
        }
        
        return result;
    }

    private boolean isNumberOk() {

        boolean numberOk = true;
        List list = getTableModel().getObjectList();

        // テーブルをイテレートする
        for (Iterator iter = list.iterator(); iter.hasNext();) {

            // マスターアイテムを取り出す
            MasterItem mItem = (MasterItem) iter.next();

            // 器材または医薬品の場合、数量を調べる
            if (mItem.getClassCode() == ClaimConst.YAKUZAI || mItem.getClassCode() == ClaimConst.ZAIRYO) {

                if (!isNumber(mItem.getNumber().trim())) {
                    numberOk = false;
                    break;
                }

            } else if (mItem.getClassCode() == ClaimConst.ADMIN) {
                // バンドル数を調べる
                if (!isNumber(mItem.getBundleNumber().trim())) {
                    numberOk = false;
                    break;
                }
                
            } else if (mItem.getClassCode() == ClaimConst.SYUGI) {
                
                // 手技の場合 null "" 可
                if (mItem.getNumber() == null || mItem.getNumber().equals("")) {
                    continue;
                }
                
                if (!isNumber(mItem.getNumber().trim())) {
                    numberOk = false;
                    break;
                }
            }
        }

        return numberOk;
    }
}
