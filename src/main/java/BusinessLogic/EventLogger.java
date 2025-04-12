package BusinessLogic;

import DataModels.CashRegister;
import DataModels.Client;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class EventLogger {
    private BufferedWriter writer;

    public EventLogger() {
        try {
            this.writer = new BufferedWriter(new FileWriter("log.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logEvents(int currentTime, ArrayList<Client> clientList, ArrayList<CashRegister> queues) {
        try {
            writer.write("Time " + currentTime + "\n");

            writer.write("Waiting Clients:");
            for (Client client : clientList) {
                writer.write(" (" + client.getID() + "," + client.getArrivalTime() + "," + client.getServiceTime() + ");");
            }

            writer.write("\n");
            for (int i = 0; i < queues.size(); i++) {
                writer.write("Queue " + queues.get(i).getID() + ":");
                Client clientInFront = queues.get(i).getClientInFront();
                if (clientInFront != null && !clientInFront.isLeaving()) {
                    writer.write(" (" + clientInFront.getID() + "," + clientInFront.getArrivalTime() + "," + clientInFront.getServiceTime() + ");");
                }
                for (Client client : queues.get(i).getClientsInLine()) {
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

    public void logEndData(int currentTime, int simulationTime, ArrayList<Client> clientList, ArrayList<CashRegister> queues, double averageWaitTime, double averageServiceTime, int peakHour, int peakHourClients){
        if(currentTime < simulationTime)
            logEvents(currentTime,clientList,queues);
        try {
            writer.write("\n\nAverage Waiting Time: " + averageWaitTime);
            writer.write("\nAverage Service Time: " + averageServiceTime);
            writer.write("\nPeak hour: " + peakHour + " (" + peakHourClients + " in store)");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
