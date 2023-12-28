import java.util.concurrent.ConcurrentHashMap;

public class Passenger extends Entity {
  private static final ConcurrentHashMap<String, Passenger> passengerCache = new ConcurrentHashMap<>();

  private Passenger(String name) { super(name); }

  public static Passenger make(String name) {
    return passengerCache.computeIfAbsent(name, Newname -> new Passenger(Newname));

  }
}
