import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.sql.Timestamp;

@AllArgsConstructor
@ToString
public class HttpSample implements Comparable<HttpSample>{
    private @Getter String name;
    private @Getter int latency;
    private @Getter Timestamp timestamp ;

    @Override
    public int compareTo(HttpSample o) {
        return Integer.compare(this.getLatency(),o.getLatency());
    }
}
