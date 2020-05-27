import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;

public class XmlReader {
    private InputStream inputStream;
    private SampleManager sampleManager = new SampleManager();
    private XMLInputFactory factory = XMLInputFactory.newInstance();
    private XMLStreamReader xmlStreamReader;

    public SampleManager getSampleManager() {
        return sampleManager;
    }

    public XmlReader(InputStream inputStream) {
        this.inputStream = inputStream;
        factory.setProperty(XMLInputFactory.IS_COALESCING, true);
        createStreamReader();
    }

    private void createStreamReader() {
        try {
            xmlStreamReader = factory.createXMLStreamReader(inputStream);
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    public void closeStream() {
        try {
            if (xmlStreamReader != null) {
                xmlStreamReader.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (XMLStreamException | IOException e) {
            e.printStackTrace();
        }
    }

    public void readXml() {
        try {
            int event = xmlStreamReader.getEventType();
            while (xmlStreamReader.hasNext()) {
                if (event == XMLStreamReader.START_ELEMENT) {
                    if (xmlStreamReader.getName().toString().equals("httpSample")) {
                        String name = null;
                        int latency = -1;
                        Timestamp timestamp = null;
                        String atributeName;
                        String atributeValue;
                        for (int i = 0; i < xmlStreamReader.getAttributeCount(); i++) {
                            atributeName = xmlStreamReader.getAttributeName(i).toString();
                            atributeValue = xmlStreamReader.getAttributeValue(i);
                            switch (atributeName) {
                                case "lb":
                                    name = atributeValue;
                                    break;
                                case "ts":
                                    timestamp = new Timestamp(Long.parseLong(atributeValue));
                                    break;
                                case "lt":
                                    latency = Integer.parseInt(atributeValue);
                                    break;
                            }
                        }
                        if (name != null && timestamp != null && latency != -1) {
                            sampleManager.addSample(new HttpSample(name, latency, timestamp));
                            sampleManager.fillInSet(name);
                        }
                    }
                }
                event = xmlStreamReader.next();
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }

    }
}
