/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package open.dolphin.table;

/**
 *
 * @author kazm
 */
public interface TableSelectionListener {
    
    public void rowSelectionChanged(Object[] selected);
    
    public void rowDoubleClicked(Object obj);

}
