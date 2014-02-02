package open.dolphin.client;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class TestCompletableJTextField extends JPanel
    implements ActionListener {

    CompletableJTextField completableField;
    JTextField completionField;

    public TestCompletableJTextField () {
        super();
        setLayout (new BoxLayout (this, BoxLayout.Y_AXIS));
        completableField = new CompletableJTextField (75);
        add (completableField);
        JPanel bottom = new JPanel ();
        bottom.add (new JLabel ("Completion:"));
        completionField = new JTextField (40);
        completionField.addActionListener (this);
        bottom.add (completionField);
        JButton addButton = new JButton ("Add");
        addButton.addActionListener (this);
        bottom.add (addButton);
        add (bottom);
    }

    public void actionPerformed (ActionEvent e) {
        completableField.addCompletion (completionField.getText());
        completionField.setText ("");
    }

    public static void main (String[] main) {
        JFrame f = new JFrame ("Completions...");
        f.getContentPane().add (new TestCompletableJTextField());
        f.pack();
        f.setVisible (true);
    }


}
