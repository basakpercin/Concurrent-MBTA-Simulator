import java.util.concurrent.ConcurrentHashMap;

public class Station extends Entity {
  private static final ConcurrentHashMap<String, Station> stationCache = new ConcurrentHashMap<>();
  private Station(String name) { super(name); }

  public static Station make(String name) {
    return stationCache.computeIfAbsent(name, Newname -> new Station(Newname));
  }
}
