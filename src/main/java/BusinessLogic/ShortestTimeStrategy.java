package BusinessLogic;

import DataModels.CashRegister;
import DataModels.Client;

import java.util.ArrayList;

public class ShortestTimeStrategy implements Strategy {
    public void addClient(ArrayList<CashRegister> queues, Client client) {
        CashRegister best = null;
        int minServiceTime = Integer.MAX_VALUE;
        for(CashRegister queue : queues) {
            if(queue.getTotalServiceTime() < minServiceTime) {
                minServiceTime = queue.getTotalServiceTime();
                best = queue;
            }
        }
        if (best != null) {
            best.addClient(client);
        }
    }
}
