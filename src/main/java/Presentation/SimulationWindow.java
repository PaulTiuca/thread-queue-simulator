package Presentation;

import javax.swing.*;
import java.awt.*;

public class SimulationWindow extends JFrame {
    private JTextArea textArea;
    private JScrollPane scrollPane;

    public SimulationWindow() {
        setTitle("Store Simulation");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Verdana", Font.PLAIN, 14));

        scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    public void updateWindow(String uiUpdate){
        int position = scrollPane.getVerticalScrollBar().getValue();
        SwingUtilities.invokeLater(() -> {
            textArea.setText(uiUpdate);
            scrollPane.getVerticalScrollBar().setValue(position);
        });
    }

    public void signalEnd(){
        JOptionPane.showMessageDialog(this, "Simulation finished");
    }
}
