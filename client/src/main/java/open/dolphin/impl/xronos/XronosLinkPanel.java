/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.impl.xronos;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.xml.parsers.ParserConfigurationException;
import open.dolphin.client.Chart;
import open.dolphin.project.Project;
import open.dolphin.util.Log;
import open.dolphin.utilities.control.ImageIconEx;
import open.dolphin.utilities.control.TableEx;
import open.dolphin.utilities.utility.HttpConnect;
import open.dolphin.utilities.utility.XmlReadWrite;
import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;


/**
 * Xronos連携
 * @author Life Sciences Computing Corporation.
 */
public class XronosLinkPanel extends JPanel {
    
    private Chart chart;
    private XronosSplitPane split;

    /**
     * コンストラクタ
     */
    public XronosLinkPanel(Chart chart) {
        this.chart = chart;
    }
    
    /**
     * XronosPanelの作成
     * @param host
     * @param user
     * @param patient
     * @param app 
     * @return 
     */
    public void createXronosPanel(String user, String patient, String app) {
        String modalityTitle = Project.getString(XronosLinkDocument.KEY_XRONOSBROWSER_MODALITY_TITLE);
        String modalityKey = Project.getString(XronosLinkDocument.KEY_XRONOSBROWSER_MODALITY_KEY);
        
        JPanel btnPane = new JPanel();
        btnPane.setLayout(new BoxLayout(btnPane, BoxLayout.X_AXIS));
        JButton refresh = new JButton((modalityTitle == null || modalityKey == null) ? "更新" : "全て");
        refresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                split.refresh(null);
            }
        });
        btnPane.add(refresh);
        if(modalityTitle != null && modalityKey != null) {
            String[] titles = modalityTitle.split(",");
            String[] keys = modalityKey.split(",");
            for(int i = 0; i < titles.length; i++) {
                JButton btn = new JButton(titles[i]);
                final String key = keys[i];
                btn.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        split.refresh(key);
                    }
                });
                btnPane.add(btn);
            }
        }
        
        //this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setLayout(new BorderLayout());
        split = new XronosSplitPane(chart, user, patient, app);
        
        this.add(btnPane, BorderLayout.NORTH);
        this.add(split, BorderLayout.CENTER);
    }
    
    /**
     * つまみ部分の自動計算
     */
    public void reDividerLocation(int height) {
        split.reDividerLocation(height);
    }
}
