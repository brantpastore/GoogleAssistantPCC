package main.view.controller.notification;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class NotificationWindowController implements Initializable {
    private String type;
    private String message;

    @FXML
    private Label messageTypeLabel;

    @FXML
    private Label alertMessage;

    public NotificationWindowController(String type, String message) {
        this.type = type;
        this.message = message;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        messageTypeLabel.setText(type);
        alertMessage.setText(message);
    }
}
