package Presentation;

import BusinessLogic.SimulationManager;

import javax.swing.*;
import java.awt.*;

public class SetupWindow extends JFrame {
    private JTextField numClientsField;
    private JTextField numQueuesField;
    private JTextField arrivalStartField;
    private JTextField arrivalEndField;
    private JTextField serviceStartField;
    private JTextField serviceEndField;
    private JTextField simulationTimeField;
    private JButton startButton;
    private int numClients;
    private int numQueues;
    private int arrivalStart;
    private int arrivalEnd;
    private int serviceStart;
    private int serviceEnd;
    private int simulationTime;

    public SetupWindow() {
        setTitle("Simulation Setup");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(8, 2, 5, 5));

        add(new JLabel("Number of Clients:"));
        numClientsField = new JTextField();
        add(numClientsField);

        add(new JLabel("Number of Queues:"));
        numQueuesField = new JTextField();
        add(numQueuesField);

        add(new JLabel("Arrival Time Start:"));
        arrivalStartField = new JTextField();
        add(arrivalStartField);

        add(new JLabel("Arrival Time End:"));
        arrivalEndField = new JTextField();
        add(arrivalEndField);

        add(new JLabel("Service Time Start:"));
        serviceStartField = new JTextField();
        add(serviceStartField);

        add(new JLabel("Service Time End:"));
        serviceEndField = new JTextField();
        add(serviceEndField);

        add(new JLabel("Simulation Time (seconds):"));
        simulationTimeField = new JTextField();
        add(simulationTimeField);

        startButton = new JButton("Start Simulation");
        startButton.setFocusable(false);
        startButton.addActionListener(e -> {
            if (getSetupInfo()) {
                this.dispose();

                SimulationManager simulationManager = new SimulationManager(numClients, numQueues, arrivalStart, arrivalEnd, serviceStart, serviceEnd, simulationTime);

                SimulationWindow simulationWindow = new SimulationWindow();

                simulationManager.setSimulationWindow(simulationWindow);

                new Thread(simulationManager).start();
            }
            else {
                JOptionPane.showMessageDialog(this, "Please enter valid input values.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        add(startButton);

        setVisible(true);
    }

    private boolean getSetupInfo() {
        try {
            numClients = Integer.parseInt(numClientsField.getText());
            numQueues = Integer.parseInt(numQueuesField.getText());
            arrivalStart = Integer.parseInt(arrivalStartField.getText());
            arrivalEnd = Integer.parseInt(arrivalEndField.getText());
            serviceStart = Integer.parseInt(serviceStartField.getText());
            serviceEnd = Integer.parseInt(serviceEndField.getText());
            simulationTime = Integer.parseInt(simulationTimeField.getText());
        } catch (NumberFormatException e) {
            return false;
        }
        return arrivalStart <= arrivalEnd && serviceStart <= serviceEnd;
    }
}
