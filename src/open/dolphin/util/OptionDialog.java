/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package open.dolphin.util;

import javax.swing.JOptionPane;

/**
 *
 * @author kazm
 */
public class OptionDialog {
    
    public static void main(String[] args) {
        
        StringBuilder sb = new StringBuilder();
        sb.append("message1");
        sb.append("\n");
        sb.append("message2");
        sb.append("\n");
        sb.append("message3");
        
        JOptionPane.showOptionDialog(
                null, 
                sb.toString(), 
                "test", 
                JOptionPane.DEFAULT_OPTION, 
                JOptionPane.WARNING_MESSAGE, 
                null, 
                new String[]{"option1", "option2"}, 
                "option1");
        
        JOptionPane.showMessageDialog(
                null, 
                sb.toString(), 
                "test", 
                JOptionPane.WARNING_MESSAGE);
        
    }
}
