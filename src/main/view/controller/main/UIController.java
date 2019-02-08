package main.view.controller.main;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.CommandProcessor;
import main.PushReceiver;
import main.util.FileHandler;
import main.view.controller.addapplication.AddAppController;
import main.view.controller.Table;
import main.view.controller.notification.NotificationWindow;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import static javafx.collections.FXCollections.observableArrayList;

public class UIController implements Initializable {
    private static FileHandler fileHandler;
    private static CommandProcessor cProcessor;
    private static PushReceiver pReceiver;

    private static String api_key = "";
    private static Map<String, String> winComMap = new HashMap<>();
    private static Map<String, String> userAppMap = new HashMap<>();
    private List<Table> userTableList = new ArrayList<>();
    private List<Table> winTableList = new ArrayList<>();

    private boolean startupIsEnabled;
    private boolean winCommandsAreEnabled;

    /**
     * LoadSettingsFile Menu item
     */
    @FXML
    private MenuItem LoadSettingsFileItem;

    /**
     * Quit Menu item
     */
    @FXML
    private MenuItem QuitMenuItem;

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
    private ObservableList<Table> userData = observableArrayList();

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
    private ObservableList<Table> winData = observableArrayList();

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
     * addUserApp Button
     */
    @FXML
    private Button addUserAppButton;

    /**
     * tabs
     */
    @FXML
    private TabPane tabs;

    public UIController() {}


    public UIController(FileHandler fh, String api, Map winCmdList, Map applicationList) {
        this.fileHandler = fh;
        this.api_key = api;
        this.winComMap.putAll(winCmdList);
        this.userAppMap.putAll(applicationList);
    }

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
     * Reload
     * Reloads User application table
     * Reloads windows command table
     */
    public void Reload() {
        ReloadUserAppTable();
        ReloadWinCommandTable();
    }

    /**
     * ReloadUserAppTable
     */
    public void ReloadUserAppTable() {
        this.fileHandler.GetAppList().clear();
        this.fileHandler.ReadApplications();

        this.userData.clear();
        this.userAppMap.putAll(this.fileHandler.GetAppList());

        this.userTable.getItems().clear();
        this.PopulateTable(this.userData, this.retrieveTable(this.userAppMap));
        this.userTable.getItems().addAll(this.userData);
        this.userTable.refresh();
    }

    /**
     * ReloadWinCommandTable
     */
    public void ReloadWinCommandTable() {
        this.fileHandler.GetWinCommands().clear();
        this.fileHandler.ReadWindowsSettings();

        this.winData.clear();
        this.winComMap.putAll(this.fileHandler.GetWinCommands());

        this.windowsTable.getItems().clear();
        this.PopulateTable(this.winData, this.retrieveTable(this.winComMap));
        this.windowsTable.getItems().addAll(this.winData);
        this.windowsTable.refresh();
    }

    /**
     * LoadSettingFile
     * @param event
     * Open a FileChooser to select the settings *.cfg file
     */
    @FXML
    private void LoadSettingFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select settings file");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("*.cfg", "*.cfg"));
        fileHandler.LoadSettingsFile(fileChooser.showOpenDialog(LoadSettingsFileItem.getParentPopup().getScene().getWindow()));
        this.userAppMap.clear();
        this.ReloadUserAppTable();
    }

    /**
     * Quit
     * @param event
     * Exits the program
     */
    @FXML
    private void Quit(ActionEvent event) {
        this.pushbulletKeyTextBox.getParent().getScene().getWindow().hide(); // This is not the child that should be used.
        //this.QuitMenuItem.getParentPopup().getScene().getWindow().hide();
    }

    /**
     * About
     * @param event
     * Creates a window with credits
     */
    @FXML
    private void About(ActionEvent event) {
        NotificationWindow n = new NotificationWindow("About", "Creator", "Brant.pastore@gmail.com\ngithub.com//brantpastore");
    }

    /**
     * Tabs()
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
     * Tables:
     * We setup the user app table and populate it
     * We setup the windows settings table and populate it
     */
    public void Tables() {
        try {
            this.userTriggerCol.setCellValueFactory(new PropertyValueFactory<String, String>("ColumnOne"));
            this.userAppDirCol.setCellValueFactory(new PropertyValueFactory<String, String>("ColumnTwo"));
            this.PopulateTable(userData, this.retrieveTable(userAppMap));
            this.userTable.getItems().addAll(userData);
            this.userTable.setEditable(true);
            this.userTable.refresh();

            this.winCommandCol.setCellValueFactory(new PropertyValueFactory<String, String>("ColumnOne"));
            this.winEnabledCol.setCellValueFactory(new PropertyValueFactory<String, String>("ColumnTwo"));
            this.PopulateTable(winData, this.retrieveTable(winComMap));
            this.windowsTable.getItems().addAll(winData);
            this.winEnabledCol.setEditable(true);
            this.windowsTable.refresh();

            for (Map.Entry entry : winComMap.entrySet()) {
                if (entry.getKey().equals("enable windows controls") && entry.getValue().equals("true")) {
                    this.windowsTab.setDisable(false);
                    this.fileHandler.GetWinCommands().clear();
                } else {
                    this.windowsTab.setDisable(true);
                    this.ReloadWinCommandTable();
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

    /**
     * PopulateTable
     * @param dataList
     * @param entries
     */
    private void PopulateTable(ObservableList dataList, ArrayList<Table> entries) {
        entries.forEach((p) -> {
            dataList.add(new Table(p.getColumnOne(), p.getColumnTwo()));
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
                    winCommandsAreEnabled = true;
                    fileHandler.ChangeWindowsValue("enable windows controls", "true");
                    windowsTab.setDisable(false);
                } else {
                    System.out.println("windows commands disabled");
                    winCommandsAreEnabled = false;
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

    /**
     * Buttons
     */
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
     * StartApp
     * @param event
     *
     * This function is called when the user clicks the start button
     * If not already started, we:
     *  Change the text to Stop
     *  Initialize the CommandProcessor class
     *  Start the pushbullet Listener
     * If started we:
     *  Change the text to Start
     *  Stop the pushbullet listener and set it to null
     *  Set the CommandProcessor to null
     */
    @FXML
    void StartApp(ActionEvent event) {
        if(StartButton.getText().equals("Start")) {
            System.out.println("Starting pushbullet listener...");
            cProcessor = new CommandProcessor(fileHandler, this);
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
        try {
            AddAppController ap = new AddAppController(fileHandler);
            ap.SetUI(this);
            Stage primaryStage = new Stage();
            primaryStage.setTitle("Add Application");
            Pane myPane = (Pane) FXMLLoader.load(getClass().getResource("AddApplicationUI.fxml"));
            primaryStage.getIcons().add(new Image("deps//icon.png"));

            Scene myScene = new Scene(myPane);
            primaryStage.setScene(myScene);
            primaryStage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * WindowsCommandsEnabled
     * @return
     */
    public boolean WindowsCommandsEnabled() {
        return winCommandsAreEnabled;
    }

    /**
     * EnableWindowsControls
     * @param event
     */
    @FXML
    void EnableWindowsControls(ActionEvent event) {
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
