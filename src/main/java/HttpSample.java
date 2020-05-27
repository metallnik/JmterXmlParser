import java.sql.Timestamp;

public class HttpSample implements Comparable<HttpSample>{
    private String name;
    private int latency;
    private Timestamp timestamp ;

    public HttpSample(String name, int latency, Timestamp timestamp) {
        this.name = name;
        this.latency = latency;
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public int getLatency() {
        return latency;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "HttpSample{" +
                "name='" + name + '\'' +
                ", latency=" + latency +
                ", timestamp=" + timestamp +
                '}';
    }


    @Override
    public int compareTo(HttpSample o) {
        return Integer.compare(this.getLatency(),o.getLatency());
    }
}
