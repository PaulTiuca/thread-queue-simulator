package DataModels;

import BusinessLogic.ClientWaitTime;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class CashRegister implements Runnable{
    private LinkedBlockingQueue<Client> clientsInLine = new LinkedBlockingQueue<>();
    private int CRID;
    private volatile Client currentClient = null;
    private final AtomicInteger totalServiceTime = new AtomicInteger(0);
    private boolean running = true;
    private ClientWaitTime clientWaitTime;

    public CashRegister(int ID, ClientWaitTime clientWaitTime) {
        this.CRID = ID;
        this.clientWaitTime = clientWaitTime;
    }

    @Override
    public void run() {
        while(running) {
            try {

                if(currentClient == null && !clientsInLine.isEmpty()) {
                    currentClient = clientsInLine.poll();
                }

                if(currentClient != null) {
                    currentClient.decrementServiceTime();
                    totalServiceTime.decrementAndGet();

                    if(currentClient.isLeaving()) {
                        clientWaitTime.getLeavingClientWaitTime(currentClient);
                        currentClient = null;
                    }
                }

                Thread.sleep(1000);

            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void addClient(Client c) {
        clientsInLine.add(c);
        totalServiceTime.addAndGet(c.getServiceTime());
    }

    public void stop(){
        running = false;
        Thread.currentThread().interrupt();
    }

    public int getTotalServiceTime(){
        return totalServiceTime.get();
    }

    public boolean isEmpty(){
        return clientsInLine.isEmpty() && (currentClient == null);
    }

    public int getID() {
        return CRID;
    }

    public synchronized LinkedBlockingQueue<Client> getClientsInLine() {
        return clientsInLine;
    }

    public synchronized Client getCurrentClient(){
        return currentClient;
    }
}
