package DataModels;

public class Client {
    private int ID;
    private int arrivalTime;
    private int serviceTime;

    public Client(int ID, int arrivalTime, int serviceTime){
        this.ID = ID;
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
    }

    public void decrementServiceTime() {
        if(serviceTime > 0)
            serviceTime--;
    }

    public boolean isLeaving() {
        return serviceTime <= 0;
    }

    public int getID() {
        return ID;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getServiceTime() {
        return serviceTime;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Client other = (Client) obj;
        return this.ID == other.ID;
    }
    @Override
    public int hashCode(){
        return Integer.hashCode(ID);
    }
}
