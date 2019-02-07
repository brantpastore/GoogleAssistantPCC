package main.view.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import main.CommandProcessor;
import main.PushReceiver;
import main.util.FileHandler;

import java.net.URL;
import java.util.*;

public class UIController implements Initializable {
    public Button AddUserApp;
    private static FileHandler fileHandler;
    private static String api_key = "";
    private static Map<String, String> winComMap = new HashMap<>();
    private static Map<String, String> userAppMap = new HashMap<>();
    private List<Table> userTableList = new ArrayList<>();
    private List<Table> winTableList = new ArrayList<>();

    private static CommandProcessor cProcessor;
    private static PushReceiver pReceiver;

    private boolean startupIsEnabled;
    private boolean winCommandsAreEnabled;

    public UIController() {}

    @FXML
    private TabPane tabs;

    public UIController(FileHandler fh, String api, Map winCmdList, Map applicationList) {
        this.fileHandler = fh;
        this.api_key = api;
        this.winComMap.putAll(winCmdList);
        this.userAppMap.putAll(applicationList);
    }

    /**
     * LoadSettingsFile Menu item
     */
    @FXML
    private MenuItem LoadSettingsFile;

    /**
     * SaveSettings Menu item
     */
    @FXML
    private MenuItem SaveSettings;

    /**
     * Quit Menu item
     */
    @FXML
    private MenuItem QuitProgram;

    /**
     * About Menu item
     */
    @FXML
    private MenuItem AboutMenu;

    /**
     * User applications tab
     */
    @FXML
    private Tab UserTab;

    /**
     * userTable
     */
    @FXML
    private TableView<Table> userTable;
    /**
     * userTable Trigger column
     */
    @FXML
    private TableColumn userTriggerCol;
    /**
     * userTable App Directory column
     */
    @FXML
    private TableColumn userAppDirCol;

    /**
     * userData table data List
     */
    private ObservableList<Table> userData = FXCollections.observableArrayList();

    /**
     * windowsTab
     */
    @FXML
    private Tab windowsTab;

    /**
     * windowsTable
     */
    @FXML
    private TableView<Table> windowsTable;

    /**
     * windows command column
     */
    @FXML
    private TableColumn winCommandCol;

    /**
     * windows command enabled column
     */
    @FXML
    private TableColumn winEnabledCol = new TableColumn("ColumnTwo");

    /**
     * windows table data
     */
    private ObservableList<Table> winData = FXCollections.observableArrayList();

    /**
     * We setup the tabs, tables, checkboxes, buttons, and text fields of the GUI
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tabs();
        Tables();
        CheckBoxes();
        Buttons();
        APITextField();

        pushbulletKeyTextBox.setPromptText(api_key);
    }

    /**
     * TODO:
     * Reload both tables information
     */
    private void Reload() {
    }

    /**
     * Tables:
     * We setup the user app table and populate it
     * We setup the windows settings table and populate it
     */
    private void Tables() {
        try {
            userTriggerCol.setCellValueFactory(new PropertyValueFactory<String, String>("ColumnOne"));
            userAppDirCol.setCellValueFactory(new PropertyValueFactory<String, String>("ColumnTwo"));
            this.PopulateTable(userData, this.retrieveTable(userAppMap));
            userTable.getItems().addAll(userData);
            this.userTable.setItems(this.userData);

            windowsTable.setEditable(true);
            this.winEnabledCol.setEditable(true);

            winCommandCol.setCellValueFactory(new PropertyValueFactory<String, String>("ColumnOne"));
            winEnabledCol.setCellValueFactory(new PropertyValueFactory<String, String>("ColumnTwo"));
            this.PopulateTable(winData, this.retrieveTable(winComMap));
            windowsTable.getItems().addAll(winData);
            this.windowsTable.setItems(this.winData);

            for (Map.Entry entry : winComMap.entrySet()) {
                if (entry.getKey().equals("enable windows controls") && entry.getValue().equals("true")) {
                    windowsTab.setDisable(false);
                } else {
                    windowsTab.setDisable(true);
                }
            }

            // TODO: Make tables editable
//            winEnabledCol.setCellFactory(TextFieldTableCell.<Table>forTableColumn());
//            winEnabledCol.setOnEditCommit(
//                    (CellEditEvent<Table, String> t) -> {
//                        ((Table) t.getTableView().getItems().get(
//                                t.getTablePosition().getRow())
//                        ).setColumnTwo(t.getNewValue());
//                    });

        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    private ArrayList<Table> retrieveTable(Map<String, String> mList) {
        try {
            ArrayList<Table> arrays = new ArrayList<>();
            for (Map.Entry entry : mList.entrySet()) {
                arrays.add(new Table(entry.getKey().toString(), entry.getValue().toString()));
            }
            return arrays;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList();
        }
    }

    private void PopulateTable(ObservableList dataList, ArrayList<Table> entries) {
        entries.forEach((p) -> {
            dataList.add(new Table(p.getColumnOne(), p.getColumnTwo()));
        });
    }

    /**
     * Setup the tab visibility based on checkbox
     */
    private void Tabs() {
        tabs.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observableValue, Tab tab, Tab t1) {
                if (tab.equals(windowsTab)) {
                    addUserAppButton.setVisible(true);
                } else {
                    addUserAppButton.setVisible(false);
                }
            }
        });
    }

    /**
     * CheckBoxes
     */
    private void CheckBoxes() {
        startupEnabled.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                startupIsEnabled = t1;
                if(startupIsEnabled) {
                    fileHandler.ChangeWindowsValue("launch on startup", "true");
                    System.out.println("launch on startup enabled");
                } else {
                    fileHandler.ChangeWindowsValue("launch on startup", "false");
                    System.out.println("launch on startup disabled");
                }
            }
        });

        winControlsEnabled.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                winCommandsAreEnabled = t1;
                if (winCommandsAreEnabled) {
                    System.out.println("windows commands enabled");
                    fileHandler.ChangeWindowsValue("enable windows controls", "true");
                    windowsTab.setDisable(false);
                } else {
                    System.out.println("windows commands disabled");
                    fileHandler.ChangeWindowsValue("enable windows controls", "false");
                    windowsTab.setDisable(true);
                }
            }
        });

        for (Map.Entry entry : winComMap.entrySet()) {
            if (entry.getKey().equals("launch on startup") && entry.getValue().equals("true")) {
                startupEnabled.setSelected(true);
            }

            if (entry.getKey().equals("enable windows controls") && entry.getValue().equals("true")) {
                winControlsEnabled.setSelected(true);
            }
        }
    }

    private void Buttons() {
    }

    /**
     * We have a key event listener assigned to check if the user presses Enter.
     * If they do we take the characters in the text box and assign it as the API key in the FileHandler class and settings file.
     */
    private void APITextField() {
        pushbulletKeyTextBox.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    System.out.println(pushbulletKeyTextBox.getCharacters().toString());
                    fileHandler.SetAPIKey(pushbulletKeyTextBox.getCharacters().toString());
                }
            }
        });
    }


    /**
     * pushbullet API key textField
     */
    @FXML
    private TextField pushbulletKeyTextBox;

    /**
     * returns the API_Key variable
     * @return
     */
    public String getApiKey() {
        return api_key;
    }

    /**
     * startupEnabled CheckBox
     */
    @FXML
    private CheckBox startupEnabled;

    /**
     * winControlsEnabled Checkbox
     */
    @FXML
    private CheckBox winControlsEnabled;

    /**
     * Start/Stop Button
     */
    @FXML
    private Button StartButton;

    /**
     * Add User Application Button
     */
    @FXML
    private Button addUserAppButton;

    /**
     * This function is called when the user clicks the start button
     * If not already started, we:
     *  Change the text to Stop
     *  Initialize the CommandProcessor class
     *  Start the pushbullet Listener
     * If started we:
     *  Change the text to Start
     *  Stop the pushbullet listener and set it to null
     *  Set the CommandProcessor to null
     *  @param event
     */
    @FXML
    void StartApp(ActionEvent event) {
        if(StartButton.getText().equals("Start")) {
            System.out.println("Starting pushbullet listener...");
            cProcessor = new CommandProcessor(fileHandler);
            pReceiver = new PushReceiver(cProcessor, fileHandler);
            StartButton.setText("Stop");
        } else if(StartButton.getText().equals("Stop")) {
            System.out.println("Stopping pushbullet listener...");
            StartButton.setText("Start");
            pReceiver.StopListening();
            pReceiver = null;
            cProcessor = null;
        }
    }

    /**
     * Add User Application button
     * TODO:
     * Popup window with field for Trigger and application directory
     * @param event
     */
    @FXML
    void AddUserApp(ActionEvent event) {

    }

    /**
     *
     * @param event
     */
    @FXML
    void EnableWindowsControlls(ActionEvent event) {

    }

    /**
     * LaunchOnStartup
     * TODO:
     * launch app on system startup
     * @param event
     */
    @FXML
    void LaunchOnStartup(ActionEvent event) {

    }

    @FXML
    private void setOnSelectionChanged() {

    }
}
