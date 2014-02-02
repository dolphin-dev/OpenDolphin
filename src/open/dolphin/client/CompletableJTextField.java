package open.dolphin.client;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.util.*;
import java.util.regex.*;

public class CompletableJTextField extends JTextField
    implements ListSelectionListener {

    Completer completer;
    JList completionList;
    DefaultListModel completionListModel;
    JScrollPane listScroller;
    //JWindow listWindow;

    public CompletableJTextField (int col) {
        super (col);
        completer = new Completer();
        completionListModel = new DefaultListModel();
        completionList = new JList(completionListModel);
        completionList.setSelectionMode (ListSelectionModel.SINGLE_SELECTION);
        completionList.addListSelectionListener (this);
        listScroller =
            new JScrollPane (completionList, 
                             ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                             ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        listScroller.setVisible (false);
//        listWindow = new JWindow();
//        listWindow.setFocusable(true);
//        listWindow.getContentPane().add (listScroller);
    }

    public void addCompletion (String s) {
        completer.addCompletion (s); }

    public void removeCompletion (String s) {
        completer.removeCompletion (s); }

    public void clearCompletions (String s ) {
        completer.clearCompletions (); }

    public void valueChanged (ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) { return; }
        if (completionList.getModel().getSize() == 0) {return;}
        listScroller.setVisible (false);

        /*
        String completionString = 
            (String) completionList.getSelectedValue();
        // this causes an IllegalStateException:
        // "Attempt to mutate in notification"
        setText (completionString);
        */

        final String completionString =
            (String) completionList.getSelectedValue();
        Thread worker = new Thread() {
            @Override
                public void run() {
                    setText (completionString);
                }
            };
        SwingUtilities.invokeLater (worker);
    }

    /** inner class does the matching of the JTextField's
        document to completion strings kept in an ArrayList
     */
    class Completer implements DocumentListener {
        private Pattern pattern;
        private ArrayList completions;
        public Completer() {
            completions = new ArrayList();
            getDocument().addDocumentListener (this);
        }

        public void addCompletion (String s) {
            completions.add (s);
            buildAndShowPopup();
        }
        
        public void removeCompletion (String s) {
            completions.remove (s);
            buildAndShowPopup();        
        }
        
        public void clearCompletions () {
            completions.clear();
            buildPopup();
            listScroller.setVisible (false);
        }
        
        private void buildPopup() {
            completionListModel.clear();
            System.out.println ("buildPopup for " + completions.size() +
                                " completions");
            Iterator it = completions.iterator();
            pattern = Pattern.compile (getText() + ".+");
            while (it.hasNext()) {
                // check if match
                String completion = (String) it.next();
                Matcher matcher = pattern.matcher (completion);
                if (matcher.matches()) {
                    // add if match
                    System.out.println ("matched "+ completion);
                    completionListModel.add (completionListModel.getSize(),
                                             completion);
                } else {
                    System.out.println ("pattern " + 
                                        pattern.pattern() +
                                        " does not match " + 
                                        completion);
                }
            }
        }

        private void showPopup() {
            if (completionListModel.getSize() == 0) {
                listScroller.setVisible(false);
                return;
            }
 
            int pos = getCaretPosition();
            try {
                Rectangle r = modelToView(pos);
                listScroller.setBounds(r.x, r.y, 200, 300);

                listScroller.setVisible(true);
                listScroller.requestFocus();
            } catch (Exception e) {
                
            }
        }

        private void buildAndShowPopup() {
            if (getText().length() < 1)
                return;
            buildPopup();
            showPopup();
        }

        // DocumentListener implementation
        public void insertUpdate (DocumentEvent e) { buildAndShowPopup(); }
        public void removeUpdate (DocumentEvent e) { buildAndShowPopup(); }
        public void changedUpdate (DocumentEvent e) { buildAndShowPopup(); }

    }

}
