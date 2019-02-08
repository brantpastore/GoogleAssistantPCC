package main.view.controller.addapplication;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import main.util.FileHandler;
import main.view.controller.notification.NotificationWindow;
import main.view.controller.main.UIController;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * AddAppController
 * UI controller used for the add application window
 * TODO:
 * Add an icon to the Browse button
 */
public class AddAppController implements Initializable {
    private static FileHandler fhandler;
    private static UIController ui;

    @FXML
    private Button addAppWindowButton;

    @FXML
    private Button browseForAppButton;

    @FXML
    private TextField triggerTextField;

    @FXML
    private TextField directoryTextField;

    @FXML
    private Label pathToExeLabel;


    public AddAppController() {
    }

    public AddAppController(FileHandler fhandle) {
        this.fhandler = fhandle;
    }

    public void SetUI(UIController uiC) {
        this.ui = uiC;
    }

    /**
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    /**
     * addApplication
     * @param event
     * Taking the triggerTextField value and the directoryTextField value
     * we add a new user app and refresh the app table
     * Then close the window
     */
    @FXML
    void AddApplication(ActionEvent event) {
        try {
            System.out.println("Adding app");
            if (!triggerTextField.getText().isEmpty() && !directoryTextField.getText().isEmpty()) {
                this.fhandler.CreateUserApp(triggerTextField.getText(), directoryTextField.getText());
                this.ui.ReloadUserAppTable();
                ((Node)(event.getSource())).getScene().getWindow().hide();
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
