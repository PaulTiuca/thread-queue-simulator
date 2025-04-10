package BusinessLogic;

import DataModels.CashRegister;
import DataModels.Client;
import Presentation.SetupWindow;
import Presentation.SimulationWindow;

import java.util.ArrayList;

public class Controller {
    SimulationManager simulationManager;
    SetupWindow setupWindow;
    SimulationWindow simulationWindow;
    EventLogger eventLogger;
    private StringBuilder uiUpdate;

    public Controller() {
        this.eventLogger = new EventLogger();
    }

    public void startSimulation(int clientNb, int queueNb, int startArrivalTime, int endArrivalTime, int startServiceTime, int endServiceTime, int simulationTime) {
        simulationManager.startSimulation(clientNb, queueNb, startArrivalTime, endArrivalTime, startServiceTime, endServiceTime, simulationTime);
    }

    public void logEvents(int currentTime, ArrayList<Client> clientList, ArrayList<CashRegister> queues) {
        eventLogger.logEvents(currentTime,clientList,queues);
    }

    public void logEndData(int currentTime, ArrayList<Client> clientList, ArrayList<CashRegister> queues, double averageWaitTime, double averageServiceTime, int peakHour, int peakHourClients){
        eventLogger.logEndData(currentTime,clientList,queues,averageWaitTime,averageServiceTime,peakHour,peakHourClients);
    }

    public void updateUI(ArrayList<CashRegister> queues) {
        buildUIUpdate(queues);
        simulationWindow.updateWindow(uiUpdate.toString());
    }

    private void buildUIUpdate(ArrayList<CashRegister> queues) {
        this.uiUpdate = new StringBuilder();
        for (int i = 0; i < queues.size(); i++) {
            uiUpdate.append("Queue ").append(i + 1).append(": ");
            CashRegister queue = queues.get(i);
            Client clientInFront = queue.getClientInFront();

            if (clientInFront != null && !clientInFront.isLeaving()) {
                uiUpdate.append("[").append(clientInFront.getID()).append(",").append(clientInFront.getServiceTime()).append("] ");
            }

            for (Client client : queue.getClientsInLine()) {
                uiUpdate.append("[").append(client.getID()).append(",").append(client.getServiceTime()).append("] ");
            }
            uiUpdate.append("\n\n");
        }
    }

    public void signalEnd() {
        simulationWindow.signalEnd();
    }

    public void setSimulationManager(SimulationManager simulationManager) {
        this.simulationManager = simulationManager;
    }

    public void setSetupWindow(SetupWindow setupWindow) {
        this.setupWindow = setupWindow;
    }

    public void setSimulationWindow(SimulationWindow simulationWindow) {
        this.simulationWindow = simulationWindow;
    }
}
