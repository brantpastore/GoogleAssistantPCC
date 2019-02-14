package main.view.addapplication;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import main.util.FileHandler;
import main.view.notification.NotificationWindow;
import main.view.main.UIController;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * AddAppController
 * UI controller used for the add application window
 * TODO:
 * Add an icon to the Browse button
 */
public class AddAppController implements Initializable {
    private static FileHandler fHandler;
    private static UIController ui;

    /**
     * addAppWindowButton
     */
    @FXML
    private Button addAppWindowButton;

    /**
     * browseForAppButton
     */
    @FXML
    private Button browseForAppButton;

    /**
     * triggerTextField
     */
    @FXML
    private TextField triggerTextField;

    /**
     * directoryTextField
     */
    @FXML
    private TextField directoryTextField;

    /**
     * pathToExeLabel
     */
    @FXML
    private Label pathToExeLabel;

    /**
     * AddAppController
     */
    public AddAppController() {
    }

    /**
     * AddAppContrroller
     * @param fhandle
     */
    public AddAppController(FileHandler fhandle) {
        this.fHandler = fhandle;
    }

    /**
     * SetUI
     * @param uiC
     */
    public void SetUI(UIController uiC) {
        this.ui = uiC;
    }

    /**
     * initialize
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    /**
     * addApplication
     * @param event
     * Taking the triggerTextField value and the directoryTextField
     * value we add a new user app and refresh the app table
     * Then close the window
     */
    @FXML
    void AddApplication(ActionEvent event) {
        try {
            if (!ui.GetAppMap().containsKey(triggerTextField.getText())) {
                if (!triggerTextField.getText().isEmpty() && !directoryTextField.getText().isEmpty()) {
                    this.fHandler.CreateUserApp(triggerTextField.getText(), directoryTextField.getText());
                    this.ui.ReloadUserAppTable();
                    ((Node) (event.getSource())).getScene().getWindow().hide();
                }
            } else {
                NotificationWindow n = new NotificationWindow("Notice", "Warning", "You can NOT have duplicate voice commands (triggers)");
            }
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * BrowseForApp
     * @param event
     * Only executables are selectable via FileChooser
     */
    @FXML
    void BrowseForApp(ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Executable", "*.exe"));
            fileChooser.setTitle("Select Executable");
            directoryTextField.setText(fileChooser.showOpenDialog(((Node) (event.getSource())).getScene().getWindow()).toString());
        } catch (NullPointerException e) {
            NotificationWindow n = new NotificationWindow("Alert", "Warning", "Did you select a file? (" + e.getMessage() + ")");
        }
    }
}
