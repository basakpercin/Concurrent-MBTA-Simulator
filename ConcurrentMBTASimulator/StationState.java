import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class StationState {
    private final String stationName;
    private Train currentTrain;
    private final Set<Passenger> passengersAtStation;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition stationAvailable = lock.newCondition();
    private final Condition trainArrived = lock.newCondition();
    private boolean isStationOccupied = false;

    public StationState(String name, Train train, List<Passenger> passengers) {
        this.stationName = name;
        this.currentTrain = train;
        this.passengersAtStation = new HashSet<>(passengers);
    }

    //Passengers waiting for train
    public void waitForTrain(){
        lock.lock();
        try {
            while (!isStationOccupied){
                trainArrived.await();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    //Train arrives at station
    public boolean trainArrives(Train train, StationState previousState){
        lock.lock();
        try {
            if (currentTrain != null) {
                return false;
            }
            if (train.toString().equals("orange")){
                int abs = 42;
            }
            previousState.trainDeparts();
            currentTrain = train;
            isStationOccupied = true;
            trainArrived.signalAll();
            return true;
        } finally {
            lock.unlock();
        }
    }

    //Train departs from station
    public void trainDeparts() {
        lock.lock();
        try {
            currentTrain = null;
            isStationOccupied = false;
            stationAvailable.signalAll();
        } finally {
            lock.unlock();
        }
    }

    //Makes train wait if the next station is occupied
    public void waitForNextStationToBeAvailable(){
        lock.lock();
        try {
            while(isStationOccupied){
                stationAvailable.await();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public void addPassengers(Collection<Passenger> passengers) {
        passengersAtStation.addAll(passengers);
    }

    public void addPassenger(Passenger passenger) {
        passengersAtStation.add(passenger);
    }

    public void removePassengers(Collection<Passenger> passengers) {
        passengersAtStation.removeAll(passengers);
    }

    public void removePassenger(Passenger passenger) {
        passengersAtStation.remove(passenger);
    }

    public boolean boardTrain(Passenger passenger) {
        lock.lock();
        try {
            if (currentTrain == null) {
                return false;
            }
            passengersAtStation.remove(passenger);
            return true;
        } finally {
            lock.unlock();
        }
    }

    public boolean deBoardTrain(Passenger passenger, Train train) {
        lock.lock();
        try {
            if (currentTrain != train) {
                return false;
            }
            passengersAtStation.add(passenger);
            return true;
        } finally {
            lock.unlock();
        }
    }

    public void containsTrain(Train train) {
        if (!train.equals(currentTrain)) {
            throw new RuntimeException(String.format("In station %s, was expecting train %s but found %s",
                    stationName, train, currentTrain));
        }
    }

    public boolean hasTrain(Train t) {
        return currentTrain == t;
    }
    public boolean hasAnyTrain() {
        return this.currentTrain != null;
    }

    public void hasPassengers(int n) {
        if (passengersAtStation.size() != n) {
            throw new RuntimeException(String.format("Expected station %s to have %s passenger but it has %s",
                    stationName, n, passengersAtStation.size()));
        }
    }

    public void containsPassenger(Passenger passenger) {
        containsAllPassengers(Collections.singletonList(passenger));
    }

    public void containsAllPassengers(List<Passenger> passengers) {
        boolean shouldThrow = false;
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append("Station : ").append(stationName).append(" missing following passengers: ");
        for (Passenger p: passengers) {
            if (!passengersAtStation.contains(p)) {
                errorMessage.append(p.toString()).append(", ");
                shouldThrow = true;
            }
        }
        if (shouldThrow) {
            throw new RuntimeException(errorMessage.toString());
        }
    }

    public Train getTrainAtStation(){
        return currentTrain;
    }

    public boolean isStationPassengersEmpty(){
        return passengersAtStation.isEmpty();
    }
}


