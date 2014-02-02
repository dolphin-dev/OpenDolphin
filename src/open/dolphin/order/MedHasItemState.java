package open.dolphin.order;

import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JTextField;

/**
 * ValidState
 *
 * @author Minagawa,Kazushi
 *
 */
public class MedHasItemState extends AbstractMedTableState {
    
    public MedHasItemState(JTable medTable, JButton deleteBtn, JButton clearBtn, JTextField stampNameField, JTextField adminField) {
        super(medTable, deleteBtn, clearBtn, stampNameField, adminField);
    }
    
    public void enter() {
        clearBtn.setEnabled(true);
        int index = medTable.getSelectedRow();
        Object obj = getTableModel().getObject(index);
        if ( obj != null && (!deleteBtn.isEnabled()) ) {
            deleteBtn.setEnabled(true);
        } else if (obj == null && deleteBtn.isEnabled()) {
            deleteBtn.setEnabled(false);
        }
    }
    
    public boolean isValidModel() {
        return (isAdminOk() && isNumberOk()) ? true : false;
    }
    
    private boolean isAdminOk() {
        return adminField.getText().trim().equals("") ? false : true;
        
        // 用法に適当なものがない場合、選択しないで送信したい
        // test 2007-4-12
        //
        //return true;
    }
    
    private boolean isNumberOk() {
        
        boolean numberOk = true;
        List list = getTableModel().getObjectList();
        
        // テーブルをイテレートする
        for (Iterator iter = list.iterator(); iter.hasNext(); ) {
            
            // マスターアイテムを取り出す
            MasterItem mItem = (MasterItem) iter.next();
            
            // 器材または医薬品の場合、数量を調べる
            if (mItem.getClassCode() != ClaimConst.SYUGI) {
                
                if ( (mItem.getNumber() != null) && (!mItem.getNumber().trim().equals("")) ) {
                    
                    String number = mItem.getNumber().trim();
                    for (int k = 0; k < number.length(); k++) {
                        
                        int ctype = Character.getType(number.charAt(k));
                        
                        // 数字 && . でない
                        if (ctype != Character.DECIMAL_DIGIT_NUMBER && number.charAt(k) != '.') {
                            numberOk = false;
                            break;
                        }
                    }
                    
                } else {
                    numberOk = false;
                }
            }
        }
        
        return numberOk;
    }
}
