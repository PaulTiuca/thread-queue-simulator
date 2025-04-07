package BusinessLogic;

import DataModels.CashRegister;
import DataModels.Client;

import java.util.ArrayList;

public interface Strategy {
    public void addClient(ArrayList<CashRegister> queues, Client client);
}
