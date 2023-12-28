import java.util.List;

public class PassengerThread extends Thread{
    private final List<Station> journey;
    private Station currentStation;
    private final Passenger passenger;
    private final MBTA mbta;
    private boolean atDestination = false;


    public PassengerThread(Passenger passenger, List<Station> journey , MBTA mbta) {
        this.journey = journey;
        this.currentStation = journey.get(0);
        this.passenger = passenger;
        this.mbta = mbta;
    }

    @Override
    public void run() {
        while (!atDestination) {
            StationState state = mbta.stateFor(currentStation);
            state.waitForTrain();
            Train train = state.getTrainAtStation();
            if (mbta.correctTrain(passenger, currentStation, train)) {
                boolean boarded = boardTrain(state, passenger, train);
                if (!boarded) {
                    continue;
                } else {
                    mbta.getLog().passenger_boards(passenger, train, currentStation);
                }
            } else {
                continue;
            }
            currentStation = getPassengerNextStation(journey, currentStation);
            StationState state2 = mbta.stateFor(currentStation);
            while(state2.getTrainAtStation() != train){
                state2.waitForTrain();

            }
            if (state2.getTrainAtStation() == train) {
                if (!deboardTrain(state2, passenger, train)) {
                    continue;
                } else {
                    mbta.getLog().passenger_deboards(passenger, train, currentStation);
                    atDestination(currentStation, state2, passenger);
                    if (atDestination) {
                        return;
                    }
                }

            }
        }

    }
            // has list of stations of its journey
            // waits for train at journey[0]
            // train comes signal
            // checks if right train, checks with if curStation+1 in train line
            // board train
            // boardlamazsa nolcak tren kacti diyelim
            // boardlamazsa 26.ya geri don
            // currentStation = nextStation
            // waits for train stop, checks if station that train...
            // ...stopped is the right station i.e., currentStation
            // deboards train
            // check if the station it deboarded is the ultimate destination...
            // ... station, if so break the loop

    public boolean boardTrain(StationState state, Passenger passenger, Train train){
         boolean boarded = state.boardTrain(passenger);
         if (boarded) {
             mbta.addPassengerToTrain(train, passenger);
         }
         return boarded;
    }

    public boolean deboardTrain(StationState state, Passenger passenger, Train train){
        boolean deboarded = state.deBoardTrain(passenger, train);
        if (deboarded) {
            mbta.removePassengerFromTrain(train, passenger);
        }
        return deboarded;
    }

    public Station getPassengerNextStation(List<Station> journey, Station currentStation){
        int curIndex = journey.indexOf(currentStation);
        return journey.get(curIndex+1);
    }

    public void atDestination(Station currentStation, StationState state, Passenger passenger){
        if(currentStation.equals(journey.get(journey.size() - 1))){
            atDestination = true;
            state.removePassenger(passenger);
        }
    }
}