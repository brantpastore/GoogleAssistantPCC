package main.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * FileHandler
 * Loads settings file (which contains what windows control commands are enabled)
 * Loads user defined applications
 */
public class FileHandler {
    private static Logger fLogger = LoggerFactory.getLogger(FileHandler.class);
    private FileInputStream io = null;
    private File inputFile = null;

    private DocumentBuilderFactory settingsFactory;
    private DocumentBuilder dBuilder;
    private Document doc;

    private String API_KEY = "";
    // TODO:
    // Change the config location to the directory our application is located
    private String settings = "C:/GAPCC/settings.cfg";
    public static Map<String, String> winCommands = new HashMap<>();
    public static Map<String, String> appList = new HashMap<>();

    public FileHandler() {
        try {
            inputFile = new File(this.settings);
            settingsFactory = DocumentBuilderFactory.newInstance();
            dBuilder = settingsFactory.newDocumentBuilder();
            doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
        } catch (Exception e) {
            fLogger.info(e.getMessage());
        }

        ReadConfig();
        ReadWindowsSetting();
        ReadApplications();
    }

    public Map getAppList() {
        return appList;
    }

    public Map getWinCommands() {
        return winCommands;
    }

    public void SetAPIKey(String key) {
        this.API_KEY = key;
    }

    public String GetAPIKey() {
        return API_KEY;
    }

    public void ReadConfig() {
        try {
            fLogger.info("parsing settings file..");

            NodeList settingsList = doc.getElementsByTagName("config");

            for (int index = 0; index < settingsList.getLength(); index++) {
                Node nNode = settingsList.item(index);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    SetAPIKey(eElement.getElementsByTagName("API_KEY").item(0).getTextContent());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ReadWindowsSetting() {
        try {
            fLogger.info("parsing windows control settings..");
            NodeList settingsList = doc.getElementsByTagName("setting");

            for (int index = 0; index < settingsList.getLength(); index++) {
                Node tempNode = settingsList.item(index);

                if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) tempNode;
                    winCommands.put(eElement.getElementsByTagName("trigger").item(0).getTextContent(), eElement.getElementsByTagName("value").item(0).getTextContent());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ReadApplications() {
        try {
            fLogger.info("parsing applications settings..");
            NodeList appNodeList = doc.getElementsByTagName("application");

            for (int index = 0; index < appNodeList.getLength(); index++) {
                Node nNode = appNodeList.item(index);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    appList.put(eElement.getElementsByTagName("trigger").item(0).getTextContent(), eElement.getElementsByTagName("directory").item(0).getTextContent());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}