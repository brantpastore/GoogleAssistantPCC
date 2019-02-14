package main.util;

import main.view.controller.notification.NotificationWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

/**
 * FileHandler
 * Loads settings file (which contains what windows control commands are enabled)
 * Loads user defined triggers (aswell as the path to executable)
 */
public class FileHandler {
    private static Logger fLogger = LoggerFactory.getLogger(FileHandler.class);
    private static FileInputStream io = null;
    private static File inputFile = null;

    private static DocumentBuilderFactory settingsFactory;
    private static DocumentBuilder dBuilder;
    private static Document doc;

    private static String API_KEY = "";
    // TODO:
    // Change the config location to the directory our application is located
    private static String settingsFilePath = "src//deps//settings.cfg";
    public static Map<String, String> winCommands = new HashMap<>();
    public static Map<String, String> appList = new HashMap<>();

    /**
     * FileHandler()
     * Initialize DOM
     * Read API Key
     * ReadWindowsSettings
     * ReadApplications
     */
    public FileHandler() {
        try {
            settingsFactory = DocumentBuilderFactory.newInstance();
            dBuilder = settingsFactory.newDocumentBuilder();

            inputFile = new File(settingsFilePath);

            if (inputFile.exists() == false) {
                doc = dBuilder.newDocument();
                CreateDefaultSettings();
            } else {
                doc = dBuilder.parse(inputFile);
                ReadAPIKey();
                ReadWindowsSettings();
                ReadApplications();
            }

            doc.getDocumentElement().normalize();
        } catch (ParserConfigurationException | IOException | SAXException e) {
            fLogger.info(e.getMessage());
        }

        ReadWindowsSettings();
        ReadApplications();
    }

    /**
     * LoadSettingsFile
     * @param newFile
     * Replaces the src/deps/settings.cfg with the user specified newFile
     */
    public static void LoadSettingsFile(File newFile) {
        try {
            settingsFactory = DocumentBuilderFactory.newInstance();
            dBuilder = settingsFactory.newDocumentBuilder();

            inputFile = new File(settingsFilePath);

            Files.copy(newFile.toPath(), inputFile.toPath(), StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
            doc = dBuilder.newDocument();
            CreateDefaultSettings();
            doc = dBuilder.parse(inputFile);
            ReadAPIKey();
            ReadWindowsSettings();
            ReadApplications();
            WriteToFile();
            doc.getDocumentElement().normalize();
        } catch (ParserConfigurationException | IOException | SAXException e) {
            fLogger.info(e.getMessage());
        } catch (NullPointerException e) {
        }
    }

    /**
     * ResetSettingsFile
     * Replaces the current settings.cfg file with the default_settings.cfg
     */
    public void ResetSettingsFile() {
        try {
            File inputFile = new File(settingsFilePath);
            File defaultFile = new File("src//deps//default_settings.cfg");
            Files.copy(defaultFile.toPath(), inputFile.toPath(), StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
            inputFile = new File(settingsFilePath);
            this.LoadSettingsFile(inputFile);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * ExportSettingsFile
     * @param path
     * Creates a copy of the current settings file and saves it to the specified path
     */
    public void ExportSettingsFile(String path) {
        try {
            inputFile = new File(settingsFilePath);
            File newSettingsFile = new File(path);
            if(newSettingsFile.createNewFile()) {
                Files.copy(inputFile.toPath(), newSettingsFile.toPath(), StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
                if (newSettingsFile.exists()) {
                    NotificationWindow n = new NotificationWindow("Success", "Settings file Successfully created!", "");
                } else {
                    NotificationWindow n = new NotificationWindow("Error", "There was an error creating the file...", "");
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * GetAppList
     * @return
     */
    public Map GetAppList() {
        return appList;
    }

    /**
     * GetWinCommands
     * @return
     */
    public Map GetWinCommands() {
        return winCommands;
    }

    /**
     * SetAPIKey
     * @param key
     *
     * Sets the variable API_KEY
     * Writes the key into the settings file
     */
    public static void SetAPIKey(String key) {
        try {
            fLogger.info("setting API Key...");

            API_KEY = key;
            NodeList settingsList = doc.getElementsByTagName("Setting");

            for (int index = 0; index < settingsList.getLength(); index++) {
                Node tempNode = settingsList.item(index);

                if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) tempNode;
                    if (eElement.hasAttribute("type") && eElement.getAttribute("type").equals("api_key")) {
                        eElement.getElementsByTagName("key").item(0).setTextContent(key);
                        WriteToFile();
                    }
                }
            }
            ReadAPIKey();
        } catch (Exception e) {
            fLogger.info(e.getMessage());
        }
    }

    /**
     * GetAPIKey()
     * Returns API_KEY
     * @return
     */
    public static String GetAPIKey() {
        return API_KEY;
    }

    /**
     * ReadAPIKey()
     * Gets the API Key from the settings file
     */
    public static void ReadAPIKey() {
        try {
            fLogger.info("fetching API Key...");
            NodeList settingsList = doc.getElementsByTagName("Setting");

            for (int index = 0; index < settingsList.getLength(); index++) {
                Node tempNode = settingsList.item(index);

                if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) tempNode;
                    if (eElement.hasAttribute("type") && eElement.getAttribute("type").equals("api_key")) {
                        API_KEY = eElement.getElementsByTagName("key").item(0).getTextContent();
//                        SetAPIKey(GetAPIKey());
                    }
                }
            }
        } catch (Exception e) {
            fLogger.info(e.getMessage());
        }
    }

    /**
     * Creats the default settings file
     */
    public static void CreateDefaultSettings() {
        try {
            Element root = doc.createElement("Settings");
            doc.appendChild(root);

            root.appendChild(CreateWindowsSetting("enable windows controls", "true"));
            root.appendChild(CreateWindowsSetting("launch on startup", "false"));
            root.appendChild(CreateWindowsSetting("open the start menu", "true"));
            root.appendChild(CreateWindowsSetting("go to sleep", "false"));
            root.appendChild(CreateUserApp("launch Chrome", "C:\\Program Files (x86)\\Google\\Chrome\\Application\\Chrome.exe"));

            Element type = doc.createElement("Type");
            type.setAttribute("API_KEY", "Insert API Key Here");
            ReadApplications();
            ReadWindowsSettings();
            WriteToFile();
        } catch (Exception e) {
            fLogger.info(e.getMessage());
        }
    }

    /**
     * CreateWindowsSetting
     * @param key
     * @param val
     * @return
     */
    private static Node CreateWindowsSetting(String key, String val) {
        Element newNode = doc.createElement("Setting");
        doc.appendChild(newNode);
        //set type of node to windows
        newNode.setAttribute("Type", "windows");
        // Create trigger element
        newNode.appendChild(CreateNodeElement("trigger", key));
        // create enabled element
        newNode.appendChild(CreateNodeElement("enabled", val));

        WriteToFile();

        return newNode;
    }

    /**
     * ReadWindowsSettings()
     */
    public static void ReadWindowsSettings() {
        try {
            winCommands.clear();
            NodeList settingsList = doc.getElementsByTagName("Setting");

            for (int index = 0; index < settingsList.getLength(); index++) {
                Node tempNode = settingsList.item(index);

                if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) tempNode;
                    if (eElement.hasAttribute("type") && eElement.getAttribute("type").equals("windows")) {
                        winCommands.put(eElement.getElementsByTagName("trigger").item(0).getTextContent(), eElement.getElementsByTagName("enabled").item(0).getTextContent());
                    }
                }
            }
        } catch (Exception e) {
            fLogger.info(e.getMessage());
        }
    }

    /**
     * ChangeWindowsValue
     * @param key
     * @param Val
     * Changes the enabled value in the settings file aswell as the windowsCommands Map
     */
    public static void ChangeWindowsValue(String key, String Val) {
        try {
            NodeList settingsList = doc.getElementsByTagName("Setting");
            for (int index = 0; index < settingsList.getLength(); index++) {
                Node tempNode = settingsList.item(index);

                if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) tempNode;
                    if (eElement.hasAttribute("type") && eElement.getAttribute("type").equals("windows")) {
                        if (eElement.getElementsByTagName("trigger").item(0).getTextContent().equals(key)) {
                            eElement.getElementsByTagName("enabled").item(0).setTextContent(Val);
                            WriteToFile();

                            for (Map.Entry entry : winCommands.entrySet()) {
                                if (entry.getKey().equals(key)) {
                                    entry.setValue(Val);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            fLogger.info(e.getMessage());
        }
    }

    /**
     * ReadApplications
     * Loads the application list from the settings file into the appList Map
     */
    public static void ReadApplications() {
        try {
            appList.clear();
            NodeList appNodeList = doc.getElementsByTagName("Setting");

            for (int index = 0; index < appNodeList.getLength(); index++) {
                Node nNode = appNodeList.item(index);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    if (eElement.hasAttribute("type") && eElement.getAttribute("type").equals("application")) {
                        appList.put(eElement.getElementsByTagName("trigger").item(0).getTextContent(), eElement.getElementsByTagName("directory").item(0).getTextContent());
                    }
                }
            }
        } catch (NullPointerException e) {
            fLogger.info(e.getMessage());
        }
    }

    /**
     * CreateUserApp
     * @param key
     * @param val
     * @return
     */
    public static Node CreateUserApp(String key, String val) {
        Element newNode = doc.createElement("Setting");

        NodeList settingsList = doc.getElementsByTagName("Setting");
        for (int index = 0; index < settingsList.getLength(); index++) {
            Node tempNode = settingsList.item(index);

            if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) tempNode;
                if (eElement.hasAttribute("type") && eElement.getAttribute("type").equals("application")) {
                    eElement.getParentNode().insertBefore(newNode, eElement.getNextSibling());

                    if (!appList.containsKey(key)) {
                        appList.put(key, val);
                    }
                }
            }
        }

        //set type of node to application
        newNode.setAttribute("type", "application");
        // Create trigger element
        newNode.appendChild(CreateNodeElement("trigger", key));
        // Create directory element
        newNode.appendChild(CreateNodeElement("directory", val));
        WriteToFile();


        return newNode;
    }

    /**
     * DeleteUserApp
     * @param key
     * Deletes the specified app from the settings file aswell as the appList
     */
    public static void DeleteUserApp(String key) {
        try {
            NodeList appNodeList = doc.getElementsByTagName("Setting");

            for (int index = 0; index < appNodeList.getLength(); index++) {
                Node nNode = appNodeList.item(index);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    if (eElement.hasAttribute("type") && eElement.getAttribute("type").equals("application")) {
                        if (eElement.getElementsByTagName("trigger").item(0).getTextContent().equals(key)) {
                            nNode.getParentNode().removeChild(eElement);
                            fLogger.info("Deleting user app " + key);
                            WriteToFile();
                        }
                    }
                }
            }
            if (appList.containsKey(key)) {
                appList.remove(key);
            }
            ReadApplications();
        } catch (Exception e) {
            fLogger.info(e.getMessage());
        }
    }

    /**
     * ChangeUserAppKey
     * @param key
     * Changes the specified application key (trigger/voice command)
     */
    public static void ChangeUserAppKey(String key, String newKey) {
        try {
            if (appList.containsKey(key)) {
                appList.put(key, newKey);
            }

            NodeList appNodeList = doc.getElementsByTagName("Setting");

            for (int index = 0; index < appNodeList.getLength(); index++) {
                Node nNode = appNodeList.item(index);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    if (eElement.hasAttribute("type") && eElement.getAttribute("type").equals("application")) {
                        if (eElement.getElementsByTagName("trigger").item(0).getTextContent().equals(key)) {
                            eElement.getElementsByTagName("trigger").item(0).setTextContent(newKey);
                            WriteToFile();
                            LoadSettingsFile(inputFile);
                        }
                    }
                }
            }
        } catch (Exception e) {
            fLogger.info(e.getMessage());
        }
    }

    /**
     * ChangeUserAppValue
     * @param key
     * @param Val
     * Changes the specified application value
     */
    public static void ChangeUserAppValue(String key, String Val) {
        try {
            for (Map.Entry entry : appList.entrySet()) {
                if (entry.getKey().equals(key)) {
                    entry.setValue(Val);
                }
            }
            NodeList appNodeList = doc.getElementsByTagName("Setting");

            for (int index = 0; index < appNodeList.getLength(); index++) {
                Node nNode = appNodeList.item(index);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    if (eElement.hasAttribute("type") && eElement.getAttribute("type").equals("application")) {
                        if (eElement.getElementsByTagName("trigger").item(0).getTextContent().equals(key)) {
                            eElement.getElementsByTagName("directory").item(0).setTextContent(Val);
                            WriteToFile();
                            LoadSettingsFile(inputFile);
                        }
                    }
                }
            }
        } catch (Exception e) {
            fLogger.info(e.getMessage());
        }
    }

    /**
     * CreateNode
     * @param tagName
     * @param val
     * @return
     * Simplifies the Node Creation process
     */
    public static Node CreateNode(String tagName, String val) {
        Element node = doc.createElement(tagName);
        node.appendChild(doc.createTextNode(val));

        return node;
    }

    /**
     * CreateNodeElement
     * @param tagName
     * @param val
     * @return
     * Simplifies the Node Element creation process and reduces redundancy
     */
    private static Node CreateNodeElement(String tagName, String val) {
        Element node = doc.createElement(tagName);
        node.appendChild(doc.createTextNode(val));

        return node;
    }

    /**
     * WriteToFile
     * Writes to the settings file
     */
    public static void WriteToFile() {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(settingsFilePath));
            StreamResult console = new StreamResult(System.out);
            transformer.transform(source, result);
            fLogger.info("Writing to file...");
        } catch (TransformerException e) {
            fLogger.info(e.getMessage());
        }
    }
}
