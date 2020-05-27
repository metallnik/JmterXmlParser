
import java.io.*;


public class Main {
    public static void main(String[] args) throws IOException {
 //       FileInputStream fileInputStream =  new FileInputStream("C:\\Users\\vipku\\IdeaProjects\\xmlparser\\src\\main\\resources\\test.xml");
        FileInputStream fileInputStream =  new FileInputStream("C:\\Users\\vipku\\OneDrive\\Desktop\\LoadTest\\result128.xml");
        FileWriter fileWriter =  new FileWriter ("C:\\Users\\vipku\\OneDrive\\Desktop\\LoadTest\\newResult.txt");
        BufferedWriter bufferedWriter =  new BufferedWriter(fileWriter);
        XmlReader xmlReader =  new XmlReader(fileInputStream);
        xmlReader.readXml();
        xmlReader.closeStream();
        SampleManager sampleManager = xmlReader.getSampleManager();
        sampleManager.calculateQuantile(95,2000,150);
        sampleManager.calculateMaxLatencyforEachSample(bufferedWriter);
        sampleManager.writeResultToFile(bufferedWriter);
        try {
            fileInputStream.close();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
