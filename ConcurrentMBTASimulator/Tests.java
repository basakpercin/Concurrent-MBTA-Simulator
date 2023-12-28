import static org.junit.Assert.*;
import org.junit.*;

import java.util.List;

public class Tests {
  @Test public void testPass() {
    assertTrue("true should be true", true);
  }
  @Test public void test1() {
    MBTA mbta = new MBTA();
    Log log = new Log();
    Train train1 = Train.make("red");

    Passenger passenger1 = Passenger.make("Alice");

    Station station1 = Station.make("Davis");
    Station station2 = Station.make("Park");
    Station station3 = Station.make("Harvard");
    Station station4 = Station.make("Tufts");
    Station station5 = Station.make("North Station");

    mbta.addLine("red", List.of("station1", "station2", "station3", "station4", "station5"));

    mbta.addJourney("passenger1", List.of("station1", "station4"));

    log.train_moves(train1, station1, station2);
    log.passenger_boards(passenger1, train1, station1);
    log.train_moves(train1, station2, station3);
    log.train_moves(train1, station3, station4);
    log.passenger_deboards(passenger1, train1, station4);
    log.train_moves(train1, station4, station5);

    Verify.verify(mbta, log);
  }
}
