import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class Sim {

  public static void run_sim(MBTA mbta, Log log) {
    // requestStop(running) method, turns to false and stops trains after all passengers arrive at their destinations
    mbta.setLog(log);
    // for each passenger in MBTA
    // create and run passenger thread
    List<Thread> allThreads = new ArrayList<>();

    for(Map.Entry<Passenger, List<Station>> entry:  mbta.getSimPassengers().entrySet()) {
        PassengerThread passengerThread = new PassengerThread(entry.getKey(), entry.getValue(), mbta);
        System.out.printf("Starting passenger thread for %s %n", entry.getKey());
        passengerThread.start();
        allThreads.add(passengerThread);
    }

    for(Map.Entry<Train, List<Station>> entry:  mbta.getSimLines().entrySet()) {
      TrainThread trainThread = new TrainThread(entry.getKey(), entry.getValue(), mbta);
      System.out.printf("Starting train thread for %s %n", entry.getKey());
      trainThread.start();
      allThreads.add(trainThread);
    }

    System.out.println("Waiting for simulation to complete");
    for (Thread t: allThreads) {
      try {
        t.join();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }

    System.out.println("Simulation done");
  }

  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      System.out.println("usage: ./sim <config file>");
      System.exit(1);
    }

    MBTA mbta = new MBTA();
    mbta.loadConfig(args[0]);

    Log log = new Log();

    run_sim(mbta, log);

    String s = new LogJson(log).toJson();
    PrintWriter out = new PrintWriter("log.json");
    out.print(s);
    out.close();

    mbta.reset();
    mbta.loadConfig(args[0]);
    Verify.verify(mbta, log);
  }
}
