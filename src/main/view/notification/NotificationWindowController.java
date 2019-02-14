package main.view.notification;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * NotificationWindowController
 */
public class NotificationWindowController implements Initializable {
    private String type;
    private String message;

    @FXML
    private Label messageTypeLabel;

    @FXML
    private Label alertMessage;

    /**
     * NotificationWindowController
     * @param type
     * @param message
     */
    public NotificationWindowController(String type, String message) {
        this.type = type;
        this.message = message;
    }

    /**
     * initialize
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        messageTypeLabel.setText(type);
        alertMessage.setText(message);
    }
}
