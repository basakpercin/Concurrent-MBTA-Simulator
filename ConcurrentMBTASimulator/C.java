import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import com.google.gson.*;

public class C {
    public List<String> l;
    public Map<String, String> m;

    public static void main(String[] args) {
//      Gson gson = new Gson();
//      C c = new C();
//      c.l = List.of("a", "b", "c");
//      c.m = Map.of("k1", "v1", "k2", "v2");
//      String s = gson.toJson(c);
//      System.out.println(s);
//
//      C c2 = gson.fromJson(s, C.class);
//      System.out.println(c2.l);
//      System.out.println(c2.m);
        Gson gson = new Gson();
        try {
            String json = new String(Files.readAllBytes(Paths.get("sample.json")));

            LoadRead dataRead = gson.fromJson(json, LoadRead.class);

            Map<String, List<String>> lines = dataRead.getLines();
            Map<String, List<String>> trips = dataRead.getTrips();

            System.out.println(lines.keySet());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
