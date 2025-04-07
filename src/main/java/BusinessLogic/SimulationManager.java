package BusinessLogic;

import DataModels.CashRegister;
import DataModels.Client;
import Presentation.SimulationWindow;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class SimulationManager implements Runnable, ClientWaitTime {
    private int currentTime;
    private Scheduler scheduler;
    private ArrayList<Client> clientList;
    private HashMap<Client,Integer> clientWaitTimes;
    private int simulationTime;
    private BufferedWriter writer;
    private int totalWaitTime;
    private int totalServiceTime;
    private int clientsServiced;
    private int peakHour;
    private int peakHourClients;
    private SimulationWindow simulationWindow;
    private StringBuilder uiUpdate;

    public SimulationManager(int clientNb, int queueNb, int startArrivalTime, int endArrivalTime, int startServiceTime, int endServiceTime, int simulationTime) {
        this.currentTime = 0;
        this.clientList = Generator.generateClients(clientNb, startArrivalTime, endArrivalTime, startServiceTime, endServiceTime);
        this.totalWaitTime = 0;
        this.totalServiceTime = 0;
        this.clientsServiced = 0;
        this.peakHour = -1;
        this.clientWaitTimes = new HashMap<>();
        this.scheduler = new Scheduler(queueNb,this);
        this.simulationTime = simulationTime;
        try {
            this.writer = new BufferedWriter(new FileWriter("log.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (!simulationFinished()) {
            try {
                assignClients();

                checkPeakHour();
                logEvents();
                buildUIUpdate();
                simulationWindow.updateWindow(uiUpdate.toString());

                currentTime++;
                Thread.sleep(1000);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        endSimulation();
    }

    private void assignClients(){
        Iterator<Client> iterator = clientList.iterator();
        while (iterator.hasNext()) {
            Client client = iterator.next();
            if (client.getArrivalTime() == currentTime) {
                scheduler.assignClient(client);
                clientWaitTimes.put(client,client.getServiceTime());
                iterator.remove();
            }
        }
    }

    private void endSimulation(){
        if(currentTime < simulationTime) {
            buildUIUpdate();
            simulationWindow.updateWindow(uiUpdate.toString());

            try {
                Thread.sleep(1000);
            }
            catch(InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        writeEndData();
        scheduler.stopQueues();
        simulationWindow.signalEnd();
    }

    private double calculateAverageWaitTime() {
        if(clientsServiced > 0)
            return (double) totalWaitTime/clientsServiced;
        else
            return 0;
    }

    private double calculateAverageServiceTime() {
        if(clientsServiced > 0)
            return (double) totalServiceTime/clientsServiced;
        else
            return 0;
    }

    private void checkPeakHour(){
        int nbClientsInStore = scheduler.getNbClientsInStore();
        if(nbClientsInStore > peakHourClients) {
            peakHour = currentTime;
            peakHourClients = nbClientsInStore;
        }
    }

    private boolean simulationFinished() {
        return (currentTime > simulationTime) || (scheduler.allQueuesEmpty() && clientList.isEmpty());
    }

    private void logEvents() {
        try {
            writer.write("Time " + currentTime + "\n");

            writer.write("Waiting Clients:");
            for (Client client : clientList) {
                writer.write(" (" + client.getID() + "," + client.getArrivalTime() + "," + client.getServiceTime() + ");");
            }

            writer.write("\n");
            for (int i = 0; i < scheduler.getQueues().size(); i++) {
                writer.write("Queue " + (i + 1) + ":");
                ArrayList<Client> snapshot = new ArrayList<>(scheduler.getQueues().get(i).getClientsInLine());
                Client currentClient = scheduler.getQueues().get(i).getCurrentClient();
                if (currentClient != null && !currentClient.isLeaving())
                    writer.write(" (" + currentClient.getID() + "," + currentClient.getArrivalTime() + "," + currentClient.getServiceTime() + ");");
                for (Client client : snapshot) {
                    writer.write(" (" + client.getID() + "," + client.getArrivalTime() + "," + client.getServiceTime() + ");");
                }
                writer.write("\n");
            }

            writer.write("\n");
            writer.write("--------------------------------------------------\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeEndData(){
        logEvents();
        try {
            writer.write("\n\nAverage Waiting Time: " + calculateAverageWaitTime());
            writer.write("\nAverage Service Time: " + calculateAverageServiceTime());
            writer.write("\nPeak hour: " + peakHour + " (" + peakHourClients + " in store)");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void getLeavingClientWaitTime(Client c) {
        clientsServiced++;
        int initialServiceTime = clientWaitTimes.get(c);
        totalServiceTime += initialServiceTime;
        totalWaitTime += currentTime - c.getArrivalTime() - initialServiceTime;
    }

    private void buildUIUpdate(){
        this.uiUpdate = new StringBuilder();
        for (int i = 0; i < scheduler.getQueues().size(); i++) {
            uiUpdate.append("Queue ").append(i + 1).append(": ");
            CashRegister queue = scheduler.getQueues().get(i);
            Client currentClient = queue.getCurrentClient();

            if (currentClient != null && !currentClient.isLeaving()) {
                uiUpdate.append("[").append(currentClient.getID()).append(",").append(currentClient.getServiceTime()).append("] ");
            }

            for (var client : queue.getClientsInLine()) {
                uiUpdate.append("[").append(client.getID()).append(",").append(client.getServiceTime()).append("] ");
            }
            uiUpdate.append("\n\n");
        }
    }

    public void setSimulationWindow(SimulationWindow simulationWindow) {
        this.simulationWindow = simulationWindow;
    }
}
