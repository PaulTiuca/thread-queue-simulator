package BusinessLogic;

import DataModels.Client;

import java.util.ArrayList;
import java.util.Random;

public class Generator {
    public static ArrayList<Client> generateClients(int clientNb, int startArrivalTime, int endArrivalTime, int startServiceTime, int endServiceTime){
        ArrayList<Client> clients = new ArrayList<>();
        Random random = new Random();

        for(int id = 1; id <= clientNb; id++) {
            int randomArrivalTime = random.nextInt(endArrivalTime - startArrivalTime + 1) + startArrivalTime;
            int randomServiceTime = random.nextInt(endServiceTime - startServiceTime + 1) + startServiceTime;
            Client newClient = new Client(id, randomArrivalTime, randomServiceTime);
            clients.add(newClient);
        }
        return clients;
    }
}
