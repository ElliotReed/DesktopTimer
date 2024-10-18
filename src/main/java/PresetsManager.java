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

    private static final String PRESETS_DIRECTORY = System.getProperty("user.home")
            + File.separator
            + "AppData"
            + File.separator
            + "Local"
            + File.separator
            + ".desktopTimer";
    private static final  String PRESETS_FILE = PRESETS_DIRECTORY + File.separator + "presets.xml";

//    macOS: Use System.getProperty("user.home") + "/Library/Application Support/YourAppName".
//    Linux: Use System.getProperty("user.home") + "/.config/YourAppName".

    public static String getAppDataPath() {
        String os = System.getProperty("os.name").toLowerCase();
        String appDataPath;
        String userHome = System.getProperty("user.home");

        if (os.contains("win")) {
            // Windows path (AppData)
            appDataPath = userHome + File.separator + "AppData" + File.separator + "Local" + File.separator + "YourAppName";
        } else if (os.contains("mac")) {
            // macOS path (~/.yourAppName)
            appDataPath = userHome + File.separator + "Library" + File.separator + "Application Support" + File.separator + "YourAppName";
        } else {
            // Linux/Unix path (~/.yourAppName)
            appDataPath = userHome + File.separator + ".YourAppName";
        }

        return appDataPath;
    }

    public PresetsManager() {
        File directory = new File(PRESETS_DIRECTORY);

        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public void savePresetsToFile(String[][] presets) {
        File file = new File(PRESETS_FILE);

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
            StreamResult streamResult = new StreamResult(file);

            transformer.transform(domSource, streamResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String[]> loadPresetsFromFile() {
        List<String[]> presets = new ArrayList<>();
        File file = new File(PRESETS_FILE);
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);

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

    public String getNextPresetId() {
        List<String[]> loadedPresets = loadPresetsFromFile();
        int maxId = 0;

        for (String[] preset : loadedPresets) {
            try {
                int currentId = Integer.parseInt(preset[0]);
                if (currentId > maxId) {
                    maxId = currentId;
                }
            } catch (NumberFormatException e) {
                System.err.println("Non-numeric preset ID found: " + preset[0]);
            }
        }

        return String.valueOf(maxId + 1);
    }



    public static void main(String[] args) {
        System.out.println(PRESETS_DIRECTORY);
//        PresetsManager manager = new PresetsManager();
//
//        String[][] presets = {
//                {"1", "300"},
//                {"2", "900"},
//                {"5", "1800"},
//                {"3", "3600"},
//                {"4", "5400"}
//        };
//
//        File file = new File("presets.xml");
//        manager.savePresetsToFile(file, presets);
//
//        System.out.println("Presets saved to " + file.getAbsolutePath());
//
//        File xmlFile = new File("presets.xml");
//        List<String[]> loadedPresets = manager.loadPresetsFromFile(xmlFile);
//
//        System.out.println("Loaded presets");
//        for (String[] preset : presets) {
//            System.out.println("ID: " + preset[0] + ", Time: " + preset[1]);
//        }
    }
}
