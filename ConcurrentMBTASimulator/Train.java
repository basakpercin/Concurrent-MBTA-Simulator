import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Train extends Entity {
  private static final ConcurrentHashMap<String, Train> trainCache = new ConcurrentHashMap<>();
  private Train(String name) { super(name); }

  public static Train make(String name) {
    return trainCache.computeIfAbsent(name, Newname -> new Train(Newname));
  }
}
