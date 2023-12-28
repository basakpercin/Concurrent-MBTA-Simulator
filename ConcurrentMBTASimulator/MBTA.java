import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MBTA {
  private Map<Station, StationState> stationStates;
  private final Map<Train, List<Station>> simLines;
  private final Map<Passenger, List<Station>> simPassengers;
  private Map<Train, List<Passenger>> trainToPassengerMap;

  private Log log;

  // Creates an initially empty simulation
  public MBTA() {
    this.trainToPassengerMap = new ConcurrentHashMap<>();
    this.simLines = new ConcurrentHashMap<>();
    this.simPassengers = new ConcurrentHashMap<>();
    this.stationStates = new ConcurrentHashMap<>();
  }

  public void setLog(Log log) {
    this.log = log;
  }

  public Log getLog() {
    return log;
  }

  // Adds a new transit line with given name and stations
  public void addLine(String name, List<String> stations) {
    Train trainToAdd = Train.make(name);
    List<Station> stationsToAdd = new ArrayList<Station>();

    for(String station : stations){
      stationsToAdd.add(Station.make(station));
      Train stationTrain = station.equals(stations.get(0)) ? trainToAdd : null;
      stationStates.putIfAbsent(Station.make(station), new StationState(station, stationTrain, new ArrayList<>()));
    }
    trainToPassengerMap.put(trainToAdd, new ArrayList<>());
    simLines.put(trainToAdd, stationsToAdd);
  }

  // Adds a new planned journey to the simulation
  public void addJourney(String name, List<String> stations) {
    Passenger passengerToAdd = Passenger.make(name);
    List<Station> journeyToAdd = new ArrayList<Station>();

    for(String station : stations){
      journeyToAdd.add(Station.make(station));
    }
    simPassengers.put(passengerToAdd, journeyToAdd);
    stationStates.get(journeyToAdd.get(0)).addPassenger(passengerToAdd);
  }

  // Return normally if initial simulation conditions are satisfied, otherwise
  // raises an exception
  public void checkStart() {
      // Check first stations has the train
    for (Map.Entry<Train,List<Station>> entry: simLines.entrySet()) {
      Station firstStation = entry.getValue().get(0);
      StationState firstStationState = stationStates.get(firstStation);
      firstStationState.containsTrain(entry.getKey());
    }

    // Check first station contains the passenger
    for (Map.Entry<Passenger,List<Station>> entry: simPassengers.entrySet()) {
      Station firstStation = entry.getValue().get(0);
      StationState firstStationState = stationStates.get(firstStation);
      firstStationState.containsPassenger(entry.getKey());
    }
  }

  // Return normally if final simulation conditions are satisfied, otherwise
  // raises an exception
  public void checkEnd() {
      // All stations are empty
    for (StationState state: stationStates.values()) {
      state.hasPassengers(0);
    }

    int expectedTrains = simLines.keySet().size();
    int actualTrains = 0;
    for (StationState state: stationStates.values()) {
      actualTrains += state.hasAnyTrain() ? 1 : 0;
    }
    if (actualTrains != expectedTrains) {
      throw new RuntimeException(String.format("We end up with different number of trains between " +
              "sim start and end. Start %s End %s", expectedTrains, actualTrains));
    }
  }



  // reset to an empty simulation
  public void reset() {
    this.trainToPassengerMap.clear();
    this.simLines.clear();
    this.simPassengers.clear();
    this.stationStates.clear();
  }

  // adds simulation configuration from a file
  public void loadConfig(String filename) {
    Gson gson = new Gson();
    try {
      String json = new String(Files.readAllBytes(Paths.get(filename)));

      LoadRead dataRead = gson.fromJson(json, LoadRead.class);

      Map<String, List<String>> lines = dataRead.getLines();
      Map<String, List<String>> trips = dataRead.getTrips();

      for(Map.Entry<String, List<String>> entry : lines.entrySet()){
        String lineName = entry.getKey();
        List<String> lineStations = entry.getValue();
        addLine(lineName, lineStations);
      }

      for(Map.Entry<String, List<String>> entry : trips.entrySet()){
        String passengerName = entry.getKey();
        List<String> passengerJourney = entry.getValue();
        addJourney(passengerName, passengerJourney);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public StationState stateFor(Station station) {
    return stationStates.get(station);
  }

  public boolean correctTrain(Passenger passenger, Station currentStation, Train train) {
    Station nextStation = getNextStation(passenger, currentStation);
    List<Station> trainJourney = simLines.get(train);
    return (trainJourney.contains(currentStation) && trainJourney.contains(nextStation));
  }

  public Station getNextStation(Passenger passenger, Station currentStation){
    List<Station> passengerJourney = simPassengers.get(passenger);
    int curStationIndex = passengerJourney.indexOf(currentStation);
    return passengerJourney.get(curStationIndex + 1);
  }

  public Map<Train, List<Station>> getSimLines() {
    return simLines;
  }

  public Map<Passenger, List<Station>> getSimPassengers() {
    return simPassengers;
  }

  public void trainHasPassenger(Train t, Passenger p) {
     if (!trainToPassengerMap.get(t).contains(p)) {
       throw new RuntimeException(String.format("Train %s does not contain passenger %s", t, p));
     }
  }

  public void addPassengerToTrain(Train train, Passenger passenger){
    List<Passenger> passengersOnTheTrain = trainToPassengerMap.get(train);
    passengersOnTheTrain.add(passenger);
  }
  public void removePassengerFromTrain(Train train, Passenger passenger){
    List<Passenger> passengersOnTheTrain = trainToPassengerMap.get(train);
    passengersOnTheTrain.remove(passenger);
  }

  public boolean passengersFinishedOnTrain(){
    return trainToPassengerMap.values().stream().allMatch(List::isEmpty);
  }

  public boolean passengersFinishedAtStations(){
    return stationStates.values().stream().allMatch(StationState::isStationPassengersEmpty);
  }
}

