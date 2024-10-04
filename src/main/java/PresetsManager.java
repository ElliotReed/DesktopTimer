import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PresetsManager {
    public void savePresetsToFile(File xmlFile, String[][] presets) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();

            Element rootElement = document.createElement("presets");
            document.appendChild(rootElement);

            for (String[] preset : presets) {
                Element presetElement = document.createElement("preset");

                Element idElement = document.createElement("id");
                idElement.appendChild(document.createTextNode(preset[0]));

                Element timeElement = document.createElement("time");
                timeElement.appendChild(document.createTextNode(preset[1]));

                presetElement.appendChild(idElement);
                presetElement.appendChild(timeElement);

                rootElement.appendChild(presetElement);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(xmlFile);

            transformer.transform(domSource, streamResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String[]> loadPresetsFromFile(File xmlFile) {
        List<String[]> presets = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);

            document.getDocumentElement().normalize();

            NodeList nodeList = document.getElementsByTagName("preset");

            for(int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    String id = element.getElementsByTagName("id").item(0).getTextContent();
                    String time = element.getElementsByTagName("time").item(0).getTextContent();

                    presets.add(new String[]{id, time});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return presets;
    }

    public static void main(String[] args) {
        PresetsManager manager = new PresetsManager();

        String[][] presets = {
                {"1", "600000"},
                {"2", "120000"},
                {"3", "3000"}
        };

        File file = new File("presets.xml");
        manager.savePresetsToFile(file, presets);

        System.out.println("Presets saved to " + file.getAbsolutePath());

        File xmlFile = new File("presets.xml");
        List<String[]> loadedPresets = manager.loadPresetsFromFile(xmlFile);

        System.out.println("Loaded presets");
        for (String[] preset : presets) {
            System.out.println("ID: " + preset[0] + ", Time: " + preset[1]);
        }
    }
}
