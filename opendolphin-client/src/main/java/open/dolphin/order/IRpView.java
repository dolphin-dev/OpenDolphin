/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.order;

/**
 *
 * @author kazushi
 */
public interface IRpView {
    
       
    /**
     * @return the clearBtn
     */
    public javax.swing.JButton getClearBtn();

    /**
     * @return the countField
     */
    public javax.swing.JTextField getCountField();

    /**
     * @return the deleteBtn
     */
    public javax.swing.JButton getDeleteBtn();

    /**
     * @return the inRadio
     */
    public javax.swing.JRadioButton getInRadio();

    /**
     * @return the infoLabel
     */
    public javax.swing.JLabel getInfoLabel();

    /**
     * @return the medicineCheck
     */
    public javax.swing.JCheckBox getMedicineCheck();

    /**
     * @return the okBtn
     */
    public javax.swing.JButton getOkBtn();

    /**
     * @return the okCntBtn
     */
    public javax.swing.JButton getOkCntBtn();

    /**
     * @return the outRadio
     */
    public javax.swing.JRadioButton getOutRadio();

    /**
     * @return the rtCheck
     */
    public javax.swing.JCheckBox getRtCheck();

    /**
     * @return the searchResultTable
     */
    public javax.swing.JTable getSearchResultTable();

    /**
     * @return the searchTextField
     */
    public javax.swing.JTextField getSearchTextField();

    /**
     * @return the setTable
     */
    public javax.swing.JTable getSetTable();

    /**
     * @return the stampNameField
     */
    public javax.swing.JTextField getStampNameField();

    /**
     * @return the usageCheck
     */
    public javax.swing.JCheckBox getUsageCheck();

    /**
     * @return the usageCombo
     */
    public javax.swing.JComboBox getUsageCombo();

    public javax.swing.JCheckBox getTonyoChk();

    /**
     * @return the partialChk
     */
    public javax.swing.JCheckBox getPartialChk();

    public javax.swing.JCheckBox getTemporalChk();
    
}
