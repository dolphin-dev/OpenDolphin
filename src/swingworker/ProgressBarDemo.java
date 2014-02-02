package swingworker;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*; 

public class ProgressBarDemo extends JFrame {
    public final static int ONE_SECOND = 1000;

    private JProgressBar progressBar;
    private Timer timer;
    private JButton startButton;
    private LongTask task;
    private JTextArea taskOutput;
    private String newline = "\n";

    public ProgressBarDemo() {
        super("ProgressBarDemo");
        task = new LongTask();

        //Create the demo's UI.
        startButton = new JButton("Start");
        startButton.setActionCommand("start");
        startButton.addActionListener(new ButtonListener());

        progressBar = new JProgressBar(0, task.getLengthOfTask());
        progressBar.setValue(0);
        progressBar.setStringPainted(true);

        taskOutput = new JTextArea(5, 20);
        taskOutput.setMargin(new Insets(5,5,5,5));
        taskOutput.setEditable(false);

        JPanel panel = new JPanel();
        panel.add(startButton);
        panel.add(progressBar);

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(panel, BorderLayout.NORTH);
        contentPane.add(new JScrollPane(taskOutput), BorderLayout.CENTER);
        contentPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(contentPane);

        //Create a timer.
        timer = new Timer(ONE_SECOND, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                progressBar.setValue(task.getCurrent());
                taskOutput.append(task.getMessage() + newline);
                taskOutput.setCaretPosition(
                        taskOutput.getDocument().getLength());
                if (task.done()) {
                    Toolkit.getDefaultToolkit().beep();
                    timer.stop();
                    startButton.setEnabled(true);
                    progressBar.setValue(progressBar.getMinimum());
                }
            }
        });
    }

    /**
     * The actionPerformed method in this class
     * is called when the user presses the start button.
     */
    class ButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            startButton.setEnabled(false);
            task.go();
            timer.start();
        }
    }
    
    public static void main(String[] args) {
        JFrame frame = new ProgressBarDemo();
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        frame.pack();
        frame.setVisible(true);
    }
}
