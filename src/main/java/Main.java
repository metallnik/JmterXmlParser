
import java.io.*;


public class Main {
    public static void main(String[] args) throws IOException {
        //       FileInputStream fileInputStream =  new FileInputStream("C:\\Users\\vipku\\IdeaProjects\\xmlparser\\src\\main\\resources\\test.xml");
        try (FileInputStream fileInputStream = new FileInputStream("C:\\Users\\vipku\\OneDrive\\Desktop\\LoadTest\\result128.xml")) {
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("C:\\Users\\vipku\\OneDrive\\Desktop\\LoadTest\\newResult.txt"))) {
                XmlReader xmlReader = new XmlReader(fileInputStream);
                xmlReader.readXml();
                xmlReader.closeStream();
                SampleManager sampleManager = xmlReader.getSampleManager();
                sampleManager.calculateQuantile(95, 2000, 150);
                sampleManager.calculateMaxLatencyforEachSample(bufferedWriter);
                sampleManager.writeResultToFile(bufferedWriter);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
