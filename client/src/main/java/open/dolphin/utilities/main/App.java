package open.dolphin.utilities.main;

import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import open.dolphin.utilities.control.AccordionPanel;

/**
 * メインクラス
 * @author S.Oh@Life Sciences Computing Corporation.
 */
public class App 
{
    App(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        java.awt.GraphicsEnvironment env = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
        java.awt.Rectangle deskBounds = env.getMaximumWindowBounds();
        frame.setBounds((deskBounds.width - 400) / 2, (deskBounds.height - 200) / 2, 400, 200);
        frame.setTitle("lutilities-1.0.jar");

        JPanel panel = new JPanel();
        panel.setBackground(Color.white);
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("Name : lutilities").append("<br>");
        sb.append("Version : 1.0").append("<br>");
        sb.append("License : Copyright (C) Life Sciences Computing Corporation.").append("<br>");
        sb.append("[Function]").append("<br>");
        sb.append("&nbsp;+ XML read write").append("<br>");
        sb.append("&nbsp;+ HTTP connection").append("<br>");
        sb.append("&nbsp;+ Dicom library").append("<br>");
        sb.append("&nbsp;+ FCR link").append("<br>");
        sb.append("&nbsp;+ Clipboard").append("<br>");
        sb.append("</html>");
        JLabel label = new JLabel(sb.toString());
        panel.add(label);
        //frame.add(label);
        AccordionPanel panel2 = new AccordionPanel();
        panel2.addComponent("Test", new JPanel());
        frame.add(panel2);

        frame.setVisible(true);
    }

    public static void main( String[] args )
    {
        new App(args);
    }
}
