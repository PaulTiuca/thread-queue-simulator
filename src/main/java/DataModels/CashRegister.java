package DataModels;

import BusinessLogic.ClientWaitTime;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class CashRegister implements Runnable{
    private LinkedBlockingQueue<Client> clientsInLine = new LinkedBlockingQueue<>();
    private int ID;
    private volatile Client clientInFront = null;
    private final AtomicInteger totalServiceTime = new AtomicInteger(0);
    private volatile boolean running = false;
    private final ClientWaitTime clientWaitTime;

    public CashRegister(int ID, ClientWaitTime clientWaitTime) {
        this.ID = ID;
        this.clientWaitTime = clientWaitTime;
    }

    @Override
    public void run() {
        while(running) {
            try {

                if(clientInFront == null && !clientsInLine.isEmpty()) {
                    clientInFront = clientsInLine.poll();
                }

                if(clientInFront != null) {
                    clientInFront.decrementServiceTime();
                    totalServiceTime.decrementAndGet();

                    if(clientInFront.isLeaving()) {
                        clientWaitTime.getLeavingClientWaitTime(clientInFront);
                        clientInFront = null;
                    }
                }

                Thread.sleep(1000);

            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public synchronized void addClient(Client c) {
        clientsInLine.add(c);
        totalServiceTime.addAndGet(c.getServiceTime());
    }

    public void stop(){
        running = false;
        Thread.currentThread().interrupt();
    }

    public void start(){
        running = true;
    }

    public boolean isEmpty(){
        return clientsInLine.isEmpty() && (clientInFront == null);
    }

    public synchronized void getRemainingClientWaitTimes(){
        if(clientInFront != null) {
            clientWaitTime.getLeavingClientWaitTime(clientInFront);
        }
        if(!clientsInLine.isEmpty()) {
            for(Client client : clientsInLine)
                clientWaitTime.getLeavingClientWaitTime(client);
        }
    }

    public int getID() {
        return ID;
    }

    public int getTotalServiceTime(){
        return totalServiceTime.get();
    }

    public synchronized LinkedBlockingQueue<Client> getClientsInLine() {
        return clientsInLine;
    }

    public synchronized Client getClientInFront(){
        return clientInFront;
    }
}
