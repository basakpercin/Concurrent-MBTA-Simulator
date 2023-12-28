import java.util.*;

public class LoadRead {
    private Map<String, List<String>> lines;
    // String -> line name, List<String> -> list of station names
    private Map<String, List<String>> trips;
    // String -> passenger name, List<String> -> List of station names for journey
    public Map<String, List<String>> getLines() {
        return lines;
    }

    public void setLines(Map<String, List<String>> lines) {
        this.lines = lines;
    }

    public Map<String, List<String>> getTrips() {
        return trips;
    }

    public void setTrips(Map<String, List<String>> trips) {
        this.trips = trips;
    }



}
