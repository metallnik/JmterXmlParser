
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;


public class SampleManager {
    private ArrayList<HttpSample> samples = new ArrayList<>();
    private HashSet<String> samplesName = new HashSet<>();
    private HashMap<String, ArrayList<HttpSample>> samplesMap = new HashMap<>();
    private int sizeAfterCutOff = 0;

    void addSample(HttpSample sample) {
        samples.add(sample);
    }

    public void displayContent() {
        System.out.println("Initial data");
        samples.forEach(System.out::println);
        sortByTime();
        System.out.println("After time sort");
        samples.forEach(System.out::println);
    }

    public void calculateQuantile(int percent, int maxLatency, int cutOffTime) {
        int countOfDelete = 0;
        ArrayList<HttpSample> worstHttp = new ArrayList<>();
        cutOffExcess(cutOffTime);
        fillInMap();
        separateSample();
        for (Map.Entry<String, ArrayList<HttpSample>> entry : samplesMap.entrySet()
        ) {
            String name = entry.getKey();
            ArrayList<HttpSample> list = entry.getValue();
            list = (ArrayList<HttpSample>) list.stream().sorted(HttpSample::compareTo).collect(Collectors.toList());
            System.out.println("Initial size " + list.size() + " for " + name);
            long remains = Math.round(list.size() * (percent / 100.0));
            System.out.println("Number of element that will be deleted " + (list.size() - remains) + " for " + name);
            countOfDelete += list.size() - remains;
            for (int i = list.size() - 1; i > remains - 1; i--) {
                worstHttp.add(list.get(i));
                list.remove(i);
            }
            System.out.println("Size after quantile " + list.size() + " for " + name);
            System.out.println("Last element after quantile " + list.get(list.size() - 1) + " for " + name);
            int lastLatency = list.get(list.size() - 1).getLatency();
            if (lastLatency <= maxLatency) {
                System.out.println("All right! different between Max latency and Last is " + (maxLatency - lastLatency) + " for " + name);
                list = (ArrayList<HttpSample>) list.stream().sorted(Comparator.comparingLong(sample -> sample.getTimestamp().getTime())).collect(Collectors.toList());
                samplesMap.put(name, list);
            } else {
                System.out.println("Last latency more than Max! different is " + (lastLatency - maxLatency) + " for " + name);
                System.out.println("PROGRAM WILL BE STOPPED ,USE ANOTHER NUMBER OF USERS");
                System.exit(0);
            }
        }
        System.out.println("Total count of element that will be deleted " + countOfDelete);
        int worstSize = worstHttp.stream().filter(sample -> (sample.getLatency() >= maxLatency)).collect(Collectors.toList()).size();
        double percentage =(double) worstSize/sizeAfterCutOff;
        percentage = percentage*100;
        System.out.println("Percent of worst samples bigger than max = "+percentage);
        System.out.println("And size "+worstSize);
    }

    private void cutOffExcess(int second) {
        System.out.println("Initial all size " + samples.size());
        sortByTime();
        long lastTime = samples.get(samples.size() - 1).getTimestamp().getTime() - (second * 1000);
        Timestamp latTimStamp = new Timestamp(lastTime);
        samples = (ArrayList<HttpSample>) samples.stream().filter(sample -> sample.getTimestamp().after(latTimStamp)).collect(Collectors.toList());
        sizeAfterCutOff = samples.size();
        System.out.println("Size after cutOff " + sizeAfterCutOff);
    }

    private BigDecimal normalizeTime(Timestamp timestamp) {
        long firstTime = samples.get(0).getTimestamp().getTime();
        double diff = (timestamp.getTime() - firstTime) / 1000.0;
        BigDecimal bigDecimal = new BigDecimal(diff);
        bigDecimal = bigDecimal.setScale(4, BigDecimal.ROUND_DOWN);
        return bigDecimal;
    }

    private void sortByTime() {
        samples = (ArrayList<HttpSample>) samples.stream().sorted(Comparator.comparingLong(sample -> sample.getTimestamp().getTime())).collect(Collectors.toList());
    }

    public void fillInSet(String name) {
        samplesName.add(name);
    }

    private void fillInMap() {
        samplesName.stream().forEach(name -> samplesMap.put(name, new ArrayList<>()));
    }

    private void separateSample() {
        for (int i = 0; i < samples.size(); i++) {
            HttpSample sample = samples.get(i);
            String name = sample.getName();
            samplesMap.get(name).add(sample);
        }
    }

    public void writeResultToFile(BufferedWriter bufferedWriter) throws IOException {
        bufferedWriter.write("SampleName;Latency;Time\n");
        StringBuilder stringBuilder;
        for (Map.Entry<String, ArrayList<HttpSample>> entry : samplesMap.entrySet()
        ) {
            bufferedWriter.write("\n");
            String name = entry.getKey();
            ArrayList<HttpSample> list = entry.getValue();
            for (int i = 0; i < list.size(); i++) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(name);
                stringBuilder.append(";");
                stringBuilder.append(list.get(i).getLatency());
                stringBuilder.append(";");
                stringBuilder.append(normalizeTime(list.get(i).getTimestamp()));
                stringBuilder.append("\n");
                bufferedWriter.write(stringBuilder.toString());
            }
        }
    }

    public void calculateMaxLatencyforEachSample(BufferedWriter bufferedWriter) throws IOException {
        bufferedWriter.write("Max latency for each Sample\n");
        ArrayList<Integer> delays = new ArrayList<>();
        for (Map.Entry<String, ArrayList<HttpSample>> entry : samplesMap.entrySet()
        ) {
            String name = entry.getKey();
            ArrayList<HttpSample> list = entry.getValue();
            int maxLatency = list.stream().max(Comparator.comparingInt(HttpSample::getLatency)).get().getLatency();
            delays.add(maxLatency);
            String result = name + ";" + maxLatency + "\n";
            bufferedWriter.write(result);
        }
        delays.sort(Integer::compareTo);
        bufferedWriter.write("Max among all is : "+delays.get(delays.size()-1)+"\n");
        bufferedWriter.write("\n");
    }
}
