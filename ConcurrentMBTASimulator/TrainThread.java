import java.util.*;

public class TrainThread extends Thread {
    private final List<Station> trainLines;
    private Station currentStation;
    private final Train train;
    private final MBTA mbta;
    boolean forward = true;
    boolean running = true;

    public TrainThread(Train train, List<Station> trainLines, MBTA mbta) {
        this.trainLines = trainLines;
        this.currentStation = trainLines.get(0);
        this.train = train;
        this.mbta = mbta;
    }

    @Override
    public void run() {
        while(running){
            StationState currentStationState = mbta.stateFor(currentStation);
            Station nextStation = nextStation(currentStation);
            StationState nextStationState = mbta.stateFor(nextStation);
            while (true) {
                nextStationState.waitForNextStationToBeAvailable();
                if (nextStationState.trainArrives(train, currentStationState)) {
                    break;
                };
            }
            mbta.getLog().train_moves(train, currentStation, nextStation);
            sleeper();
            currentStation = nextStation;
            checkRunning();
        }
    }

    private void sleeper(){
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private Station nextStation(Station currentStation){
        int curIndex = trainLines.indexOf(currentStation);

        if(forward){
            if(curIndex < trainLines.size() - 1){
                return trainLines.get(curIndex + 1);
            } else {
                forward = false;
                return trainLines.get(curIndex - 1);
            }
        } else {
            if (curIndex > 0){
                return trainLines.get(curIndex - 1);
            } else {
                forward = true;
                return trainLines.get(curIndex + 1);
            }
        }
    }
    public void checkRunning(){
        running = !mbta.passengersFinishedOnTrain() || !mbta.passengersFinishedAtStations();
    }

}