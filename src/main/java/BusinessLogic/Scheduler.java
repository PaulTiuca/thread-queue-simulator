package BusinessLogic;

import DataModels.CashRegister;
import DataModels.Client;

import java.util.ArrayList;

public class Scheduler {
    private ArrayList<CashRegister> queues = new ArrayList<>();
    private Strategy strategy;

    public Scheduler(int queueNb, ClientWaitTime simulationManager){
        for(int i = 1; i <= queueNb; i++) {
            CashRegister cashRegister = new CashRegister(i,simulationManager);
            queues.add(cashRegister);
            Thread t = new Thread(cashRegister);
            t.start();
        }
        this.strategy = new EmptyQueueStrategy();
    }

    public synchronized void assignClient(Client client) {
        if (anyQueueEmpty() && strategy instanceof ShortestTimeStrategy) {
            strategy = new EmptyQueueStrategy();
        } else if (strategy instanceof EmptyQueueStrategy){
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

    public synchronized int getNbClientsInStore() {
        int nbClientsInStore = 0;
        for(CashRegister queue : queues) {
            nbClientsInStore += queue.getClientsInLine().size();
            if(queue.getCurrentClient() != null)
                nbClientsInStore++;
        }
        return nbClientsInStore;
    }

    public ArrayList<CashRegister> getQueues() {
        return queues;
    }
}
