package BusinessLogic;

import DataModels.CashRegister;
import DataModels.Client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class SimulationManager implements Runnable, ClientWaitTime {
    private Controller controller;
    private int currentTime;
    private Scheduler scheduler;
    private ArrayList<Client> clientList;
    private HashMap<Client,Integer> clientServiceTimes;
    private int simulationTime;
    private int totalWaitTime;
    private int totalServiceTime;
    private int clientsServiced;
    private int peakHour;
    private int peakHourClients;

    public SimulationManager(Controller controller) {
        this.controller = controller;
        this.currentTime = 0;
        this.totalWaitTime = 0;
        this.totalServiceTime = 0;
        this.clientsServiced = 0;
        this.peakHour = -1;
        this.clientServiceTimes = new HashMap<>();
    }

    @Override
    public void run() {
        while (!simulationFinished()) {
            try {
                assignClients();

                checkPeakHour();
                ArrayList<CashRegister> snapshot = new ArrayList<>(scheduler.getQueues());
                controller.logEvents(currentTime,clientList,snapshot);
                controller.updateUI(scheduler.getQueues());

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
                clientServiceTimes.put(client,client.getServiceTime());
                iterator.remove();
            }
        }
    }

    public void startSimulation(int clientNb, int queueNb, int startArrivalTime, int endArrivalTime, int startServiceTime, int endServiceTime, int simulationTime) {
        this.clientList = Generator.generateClients(clientNb, startArrivalTime, endArrivalTime, startServiceTime, endServiceTime);
        this.scheduler = new Scheduler(queueNb,this);
        this.simulationTime = simulationTime;
        scheduler.startQueues();
        Thread t = new Thread(this);
        t.start();
    }

    private void endSimulation() {
        controller.updateUI(scheduler.getQueues());

        scheduler.calculateRemainingClientWaitTime();

        ArrayList<CashRegister> snapshot = new ArrayList<>(scheduler.getQueues());
        controller.logEndData(currentTime,clientList,snapshot,calculateAverageWaitTime(),calculateAverageServiceTime(),peakHour,peakHourClients);
        scheduler.stopQueues();
        controller.signalEnd();
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

    public synchronized void getLeavingClientWaitTime(Client c) {
        clientsServiced++;
        int initialServiceTime = clientServiceTimes.get(c);
        totalServiceTime += initialServiceTime;
        totalWaitTime += currentTime - c.getArrivalTime() - (initialServiceTime - c.getServiceTime());
    }
}
