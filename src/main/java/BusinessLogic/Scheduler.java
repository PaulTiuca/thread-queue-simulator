package BusinessLogic;

import DataModels.CashRegister;
import DataModels.Client;

import java.util.ArrayList;

public class Scheduler {
    private ArrayList<CashRegister> queues = new ArrayList<>();
    private Strategy strategy;

    public Scheduler(int queueNb, ClientWaitTime simulationManager){
        for(int i = 1; i <= queueNb; i++) {
            CashRegister queue = new CashRegister(i,simulationManager);
            queues.add(queue);
        }
        this.strategy = new EmptyQueueStrategy();
    }

    public synchronized void assignClient(Client client) {
        if (anyQueueEmpty()) {
            strategy = new EmptyQueueStrategy();
        }
        else {
            strategy = new ShortestTimeStrategy();
        }
        strategy.addClient(queues, client);
    }

    private boolean anyQueueEmpty() {
        for (CashRegister queue : queues) {
            if (queue.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public boolean allQueuesEmpty() {
        for (CashRegister queue : queues) {
            if (!queue.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public void stopQueues() {
        for (CashRegister queue : queues) {
            queue.stop();
        }
    }

    public void startQueues() {
        for(CashRegister queue : queues) {
            queue.start();
            Thread t = new Thread(queue);
            t.start();
        }
    }

    public void calculateRemainingClientWaitTime(){
        for(CashRegister queue : queues) {
            queue.getRemainingClientWaitTimes();
        }
    }

    public synchronized int getNbClientsInStore() {
        int nbClientsInStore = 0;
        for(CashRegister queue : queues) {
            nbClientsInStore += queue.getClientsInLine().size();
            if(queue.getClientInFront() != null)
                nbClientsInStore++;
        }
        return nbClientsInStore;
    }

    public ArrayList<CashRegister> getQueues() {
        return queues;
    }
}
