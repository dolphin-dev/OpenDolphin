/*
 * AreaDataDocument.java
 *
 * Created on 2001/11/27, 2:15
 */
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// Junzo SATO

package jp.ac.kumamoto_u.kuh.fc.jsato;

import javax.swing.*;

import open.dolphin.client.*;

import java.awt.*;

import jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.*;

/**
 *
 * @author  Junzo SATO
 * @version 
 */
public class AreaDataDocument extends DefaultChartDocument {
    /** Creates new AreaDataDocument */
    public AreaDataDocument() {
        super();
    }
    
    public void start() {
        
        JComponent compo = createComponent();
        //compo.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
        this.setLayout(new BorderLayout());
        this.add(compo); 
        //System.out.println("Created AreaDataDocument");
    }
        
    private JComponent createComponent() {
        //JPanel panel = new JPanel();
        //panel.add(new JLabel("This functionality is under construction:-)"));
        
        JPanel panel = new AreaDataBean(context);
        panel.setPreferredSize(new Dimension(680,560));

        //panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JScrollPane scroller = new JScrollPane(panel);
        return scroller;
    }
}
