package main.view.main;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import main.CommandProcessor;
import main.PushReceiver;
import main.util.FileHandler;
import main.view.EditingCell;
import main.view.Table;
import main.view.addapplication.AddAppController;
import main.view.notification.NotificationWindow;

//import javax.xml.bind.annotation.XmlAccessType;
//import javax.xml.bind.annotation.XmlAccessorType;
//import javax.xml.bind.annotation.XmlRootElement;
//import javax.xml.bind.annotation.XmlType;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import static javafx.collections.FXCollections.observableArrayList;

/**
 * UIController
 */
public class UIController implements Initializable {
    private static FileHandler fileHandler;
    private static CommandProcessor cProcessor;
    private static PushReceiver pReceiver;

    private static String api_key = "";
    private static Map<String, String> winComMap = new HashMap<>();
    private static Map<String, String> userAppMap = new HashMap<>();

    private boolean startupIsEnabled;

    private boolean winCommandsAreEnabled;

    /**
     * GetAppMap()
     * Used in AddAppController class to
     */
    public Map GetAppMap() { return userAppMap; }

    /**
     *
     */
    @FXML
    private AnchorPane pane;

    /**
     *  MenuBar
     */
    @FXML
    private MenuBar menuBar;

    /**
     * LoadSettingsFile Menu item
     */
    @FXML
    private MenuItem LoadSettingsFileItem;

    /**
     * ExportSettingsFile Menu item
     */
    @FXML
    private MenuItem exportSettingsFileItem;

    /**
     * ResetSettingsFile Menu item
     */
    @FXML
    private MenuItem ResetSettingsFileItem;

    /**
     * Quit Menu item
     */
    @FXML
    private MenuItem quitMenuItem;

    /**
     * About Menu item
     */
    @FXML
    private MenuItem uboutMenuItem;

    /**
     * User applications tab
     */
    @FXML
    private Tab userTab;

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
     * SetKey Button
     */
    @FXML
    private Button SetKeyButton;

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


    /**
     * UIController
     * @param fh
     * @param api
     * @param winCmdList
     * @param applicationList
     * we initialize filehandler, set the api string, and populate the winCmdMap and userAppMap
     */
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
    public void ReloadTables() {
        ReloadUserAppTable();
        ReloadWinCommandTable();
    }

    /**
     * ReloadUserAppTable
     */
    public void ReloadUserAppTable() {
        try {
            this.userAppMap.clear();
            this.userData.clear();
            this.fileHandler.GetAppList().clear();
            this.userTable.getItems().clear();

            this.fileHandler.ReadApplications();
            this.userAppMap.putAll(this.fileHandler.GetAppList());

            this.PopulateTable(this.userData, this.retrieveTable(this.userAppMap));
            this.userTable.getItems().addAll(this.userData);
            this.userTable.refresh();
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * ReloadWinCommandTable
     */
    public void ReloadWinCommandTable() {
        try {
            //this.winComMap.clear();
            this.winData.clear();
            this.fileHandler.GetWinCommands().clear();
            this.windowsTable.getItems().clear();

            this.fileHandler.ReadWindowsSettings();
            this.winComMap.putAll(this.fileHandler.GetWinCommands());

            this.PopulateTable(this.winData, this.retrieveTable(this.winComMap));
            this.windowsTable.getItems().addAll(this.winData);
            this.windowsTable.refresh();
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * LoadSettingFile
     * @param event
     * Open a FileChooser to select the settings *.cfg file
     */
    @FXML
    private void LoadSettingFile(ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select settings file");
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("*.cfg", "*.cfg"));
            File selectedFile = fileChooser.showOpenDialog(LoadSettingsFileItem.getParentPopup().getScene().getWindow());
            fileHandler.LoadSettingsFile(selectedFile);
            this.ReloadUserAppTable();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * ResetSettingsFile
     * @param event
     * Resets the setting file to default
     */
    @FXML
    private void ResetSettingsFile(ActionEvent event) {
        try {
            fileHandler.ResetSettingsFile();
            this.ReloadUserAppTable();
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * ExportSettingsFile
     * @param event
     * Copies settings.cfg to specified location
     */
    @FXML
    void ExportSettingsFile(ActionEvent event) {
        try {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(this.pushbulletKeyTextBox.getParent().getScene().getWindow()); // This is not the child that should be used.

            if (selectedDirectory == null) {
            } else {
                fileHandler.ExportSettingsFile(selectedDirectory.getAbsolutePath()+"\\settings.cfg");
                NotificationWindow a = new NotificationWindow("File handler", "settings.cfg has been successfully exported!", "Located: " + selectedDirectory.getAbsolutePath()+"\\settings.cfg");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
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
        NotificationWindow n = new NotificationWindow("About", "Author", "Brant.pastore@gmail.com\ngithub.com//brantpastore");
    }

    /**
     * Tabs
     * Setup the tab visibility based on checkbox
     */
    private void Tabs() {
        try {
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
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Tables
     * Setup the user app table and populate it
     * Setup the windows settings table and populate it
     */
    public void Tables() {
        try {
            this.userTable.setEditable(true);

            this.userTriggerCol.setCellValueFactory(new PropertyValueFactory<String, String>("ColumnOne"));
            this.userTriggerCol.setEditable(true);

            EventHandler CellEditEvent = new EventHandler<KeyEvent>(){
                @Override
                public void handle(KeyEvent key) {
                    if (key.getCode().equals(KeyCode.ENTER)) {
                        //fileHandler.ChangeUserAppKey(cellEditEvent.getOldValue(), cellEditEvent.getNewValue());
                        //ReloadUserAppTable();
                        System.out.println("Cell edit saved");
                    }
                }
            };

            this.userTriggerCol.setCellFactory(new Callback<TableColumn<?,?>, TableCell<?,?>>() {
                @Override
                public TableCell call(TableColumn<?,?> p) {
                    return new EditingCell();
                }
            });

            this.userTriggerCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Table, String>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<Table, String> cellEditEvent) {
                    String oldKey = cellEditEvent.getOldValue();
                    cellEditEvent.getTableView().getItems().get(cellEditEvent.getTablePosition().getRow()).setColumnOne(cellEditEvent.getNewValue());
                    fileHandler.ChangeUserAppKey(oldKey, cellEditEvent.getNewValue());
                    System.out.println(cellEditEvent.getNewValue());
                }
            });

            this.userAppDirCol.setCellValueFactory(new PropertyValueFactory<String, String>("ColumnTwo"));
            this.userAppDirCol.setEditable(true);

            this.userAppDirCol.setCellFactory(new Callback<TableColumn<?,?>, TableCell<?,?>>() {
                @Override
                public TableCell call(TableColumn<?,?> p) {
                    return new EditingCell();
                }
            });

            this.userAppDirCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Table, String>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<Table, String> cellEditEvent) {
                    String oldPath = cellEditEvent.getOldValue();
                    System.out.println(oldPath);
                    File nFile = new File(cellEditEvent.getNewValue());
                    if (nFile.exists()) {
                        cellEditEvent.getTableView().getItems().get(cellEditEvent.getTablePosition().getRow()).setColumnTwo(cellEditEvent.getNewValue());
                        fileHandler.ChangeUserAppValue(cellEditEvent.getTableView().getItems().get(cellEditEvent.getTablePosition().getRow()).getColumnOne(), cellEditEvent.getNewValue());
                    } else {
                        NotificationWindow a = new NotificationWindow("Alert", "Error", "The specified executable does not exist. Reverting to previously selected exectuable.");
                        cellEditEvent.getTableView().getItems().get(cellEditEvent.getTablePosition().getRow()).setColumnTwo(oldPath);
                        fileHandler.ChangeUserAppValue(cellEditEvent.getTableView().getItems().get(cellEditEvent.getTablePosition().getRow()).getColumnOne(), oldPath);
                    }
                }
            });


            this.PopulateTable(userData, this.retrieveTable(userAppMap));
            this.userTable.getItems().addAll(userData);
            this.userTable.refresh();


            this.winCommandCol.setCellValueFactory(new PropertyValueFactory<String, String>("ColumnOne"));
            this.winEnabledCol.setCellValueFactory(new PropertyValueFactory<String, String>("ColumnTwo"));
            this.PopulateTable(winData, this.retrieveTable(winComMap));
            this.windowsTable.getItems().addAll(winData);
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

            HandleTables();
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * retrieveTable
     * @param mList
     * @return
     */
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
     * HandleTables
     * Used to handle user interaction with user application table and windows controls table
     */
    public void HandleTables() {
        try {
            winEnabledCol.setCellFactory(TextFieldTableCell.<Table>forTableColumn());
            windowsTable.setOnMouseClicked((MouseEvent event) -> {
                if (event.getButton().equals(MouseButton.PRIMARY)) {
                    // if column two (winComEnabled) is selected
                    if (windowsTable.getFocusModel().getFocusedCell().getColumn() == 1) {
                        // for both the Enable windows controls and launch on startup, fire() can be called to (de)select the checkbox, which in turn clears the appropriate map and reloads the table
                        if (windowsTable.getSelectionModel().getTableView().getItems().get(windowsTable.getSelectionModel().getFocusedIndex()).getColumnOne().equals("enable windows controls")) {
                            winControlsEnabled.fire();
                        }
                        if (windowsTable.getSelectionModel().getTableView().getItems().get(windowsTable.getSelectionModel().getFocusedIndex()).getColumnOne().equals("launch on startup")) {
                            startupEnabled.fire();
                        } else {
                            // If the selected rows winEnabledColumn is currently set to false, change it to true
                            if (windowsTable.getSelectionModel().getTableView().getItems().get(windowsTable.getSelectionModel().getFocusedIndex()).getColumnTwo().equals("false")) {
                                fileHandler.ChangeWindowsValue(windowsTable.getSelectionModel().getTableView().getItems().get(windowsTable.getSelectionModel().getFocusedIndex()).getColumnOne(), "true");
                            } else {
                                fileHandler.ChangeWindowsValue(windowsTable.getSelectionModel().getTableView().getItems().get(windowsTable.getSelectionModel().getFocusedIndex()).getColumnOne(), "false");
                            }
                            winComMap.clear();
                            ReloadWinCommandTable();
                        }
                    }
                }
            });

            userTable.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent keyEvent) {
                    try {
                        // If the user presses the delete key, we delete the currently selected column/application
                        if (keyEvent.getCode().equals(KeyCode.DELETE)) {
                            fileHandler.DeleteUserApp(userTable.getSelectionModel().getTableView().getItems().get(userTable.getSelectionModel().getFocusedIndex()).getColumnOne());
                            userAppMap.clear();
                            ReloadUserAppTable();
                        }
                    } catch (NullPointerException e) {
                    } catch (IndexOutOfBoundsException e) {
                    }
                }
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * CheckBoxes
     */
    private void CheckBoxes() {
        try {
            startupEnabled.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                    if (t1) {
                        fileHandler.ChangeWindowsValue("launch on startup", "true");
                        startupEnabled.setSelected(true);
                        startupIsEnabled = true;
                    } else {
                        fileHandler.ChangeWindowsValue("launch on startup", "false");
                        startupEnabled.setSelected(false);
                        startupIsEnabled = false;
                    }
                    ReloadWinCommandTable();
                }
            });

            winControlsEnabled.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                    try {
                        winCommandsAreEnabled = t1;
                        if (winCommandsAreEnabled) {
                            winCommandsAreEnabled = true;
                            fileHandler.ChangeWindowsValue("enable windows controls", "true");
                            windowsTab.setDisable(false);
                        } else {
                            winCommandsAreEnabled = false;
                            fileHandler.ChangeWindowsValue("enable windows controls", "false");
                            windowsTab.setDisable(true);
                            tabs.getSelectionModel().select(userTab);
                            //userTab.getContent().requestFocus(); // userTab = null for some reason, which is why we cant get it to switch focus to userTab...
                        }
                        ReloadWinCommandTable();
                    } catch (NullPointerException e) {
                        System.out.println("FUCK");
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
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Buttons
     */
    private void Buttons() {
    }

    /**
     * APITextField
     * We have a key event listener assigned to check if the user presses Enter.
     * If they do we take the characters in the text box and assign it as the API key in the FileHandler class and settings file.
     */
    private void APITextField() {
        try {
            pushbulletKeyTextBox.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent ke) {
                    if (ke.getCode().equals(KeyCode.ENTER)) {
                        fileHandler.SetAPIKey(pushbulletKeyTextBox.getCharacters().toString());
                    }
                }
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * SetKeyButtonSelected
     * @param event
     * Called when the SetKeyButton is pressed
     * Sets the API key - NOTE: pressing Enter while in the text field does the same.
     */
    @FXML
    void SetKeyButtonSelected(ActionEvent event) {
        try {
            if (!pushbulletKeyTextBox.getText().isEmpty()) {
                fileHandler.SetAPIKey(pushbulletKeyTextBox.getCharacters().toString());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
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
        try {
            if (StartButton.getText().equals("Start")) {
                if (fileHandler.GetAPIKey().equals("Enter Pushbullet Key Here")) {
                    NotificationWindow n = new NotificationWindow("Notice", "Pushbullet", "Change your pushbullet API Key");
                } else {
                    cProcessor = new CommandProcessor(fileHandler, this);
                    pReceiver = new PushReceiver(cProcessor, fileHandler);
                    StartButton.setText("Stop");
                }

            } else if (StartButton.getText().equals("Stop")) {
                StartButton.setText("Start");
                pReceiver.StopListening();
                pReceiver = null;
                cProcessor = null;
            }
        } catch(Exception e) {
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

            Stage appStage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(UIController.class.getClassLoader().getResource("main/resources/AddApplicationUI.fxml"));
            Parent root = loader.load();

            appStage.setTitle("Add Application");
            appStage.setResizable(false);
            appStage.getIcons().add(new Image("deps/icon.png"));
            Scene myScene = new Scene(root);
            appStage.setScene(myScene);
            appStage.show();
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

    /**
     * setOnSelectionChanged
     */
    @FXML
    private void setOnSelectionChanged() {
    }
}
