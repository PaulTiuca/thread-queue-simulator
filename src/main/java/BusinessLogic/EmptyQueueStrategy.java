package BusinessLogic;

import DataModels.CashRegister;
import DataModels.Client;

import java.util.ArrayList;

public class EmptyQueueStrategy implements Strategy {
    public void addClient(ArrayList<CashRegister> queues, Client client) {
        for (CashRegister queue : queues) {
            if (queue.isEmpty()) {
                queue.addClient(client);
                return;
            }
        }
    }
}
