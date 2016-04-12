package open.dolphin.impl.pvt;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.*;
import open.dolphin.client.ClientContext;

/**
 * WatingListView改
 *
 * @author masuda, Masuda Naika
 */
public class WatingListView extends JPanel {

    private final JButton kutuBtn;
    private final JLabel pvtInfoLbl;
    private final RowTipsTable pvtTable;

    public WatingListView() {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        kutuBtn = new JButton();
//minagawa^ Icon Server        
        //kutuBtn.setIcon(ClientContext.getImageIcon("kutu01.gif"));
        kutuBtn.setIcon(ClientContext.getImageIconArias("icon_akai_kutsu"));
//minagawa$        
        kutuBtn.setAlignmentY(BOTTOM_ALIGNMENT);
        panel.add(kutuBtn);

        pvtInfoLbl = new JLabel();
        pvtInfoLbl.setFont(new Font("Lucida Grande", 0, 10));
        pvtInfoLbl.setAlignmentY(BOTTOM_ALIGNMENT);
        panel.add(pvtInfoLbl);
        panel.add(Box.createHorizontalGlue());
        
	JLabel underGoLbl = new JLabel();
//minagawa^ Icon Server         
	//underGoLbl.setIcon(ClientContext.getImageIcon("apps_16.gif"));
        underGoLbl.setIcon(ClientContext.getImageIconArias("icon_under_treatment_small"));
//minagawa$
        String labelText = ClientContext.getMyBundle(WatingListView.class).getString("labelText.labTest");
	underGoLbl.setText(labelText);
	underGoLbl.setAlignmentY(BOTTOM_ALIGNMENT);
	panel.add(underGoLbl);
/*
        JLabel openLbl = new JLabel();
        openLbl.setIcon(ClientContext.getImageIcon("open_16.gif"));
        openLbl.setText("オープン");
        openLbl.setAlignmentY(BOTTOM_ALIGNMENT);
        panel.add(openLbl);
*/
        JLabel flagLbl = new JLabel();
//minagawa^ Icon Server         
        //flagLbl.setIcon(ClientContext.getImageIcon("flag_16.gif"));
        flagLbl.setIcon(ClientContext.getImageIconArias("icon_sent_claim_small"));
//minagawa$    
        labelText = ClientContext.getMyBundle(WatingListView.class).getString("labelText.done");
        flagLbl.setText(labelText);
        flagLbl.setAlignmentY(BOTTOM_ALIGNMENT);
        panel.add(flagLbl);

        pvtTable = new RowTipsTable();
        JScrollPane scroll = new JScrollPane(pvtTable);

//minagawa^ 間隔を空ける        
        //this.setLayout(new BorderLayout());
        this.setLayout(new BorderLayout(0,5));
//minagawa$        
        this.add(panel, BorderLayout.NORTH);
        this.add(scroll, BorderLayout.CENTER);
    }

    public JButton getKutuBtn() {
        return kutuBtn;
    }

    public JTable getTable() {
        return pvtTable;
    }

    public JLabel getPvtInfoLbl() {
        return pvtInfoLbl;
    }
}
