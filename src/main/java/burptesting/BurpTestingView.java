package burptesting;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class BurpTestingView extends JPanel {
    private final JTextArea textArea = new JTextArea();
    private final JButton runButton = new JButton("Run tests");

    public BurpTestingView() {
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setLayout(new BorderLayout(10, 10));

        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));

        add(new JScrollPane(textArea), BorderLayout.CENTER);
        add(runButton, BorderLayout.SOUTH);
    }

    public void clearOutput() {
        textArea.setText("");
    }

    public void append(String msg) {
        textArea.append(msg);
    }

    public JButton getRunButton() {
        return runButton;
    }

    public String caption() {
        return "Burp Testing";
    }
}
