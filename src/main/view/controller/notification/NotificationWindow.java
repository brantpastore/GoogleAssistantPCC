package main.view.controller.notification;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


/**
 * AlertWindow
 * Used to create a window with a message to the user
 */
public class NotificationWindow {
    private String type;
    private String message;
    public NotificationWindow() {}

    public NotificationWindow(String title, String type, String message) {
        this.type = type;
        this.message = message;
        SendAlert(title, type, message);
    }

    public String GetType() { return this.type; }
    public String GetMessage() { return this.message; }

    /**
     * SendAlert
     * @param type
     * @param message
     * Creates a new window where we change the labels to the respective parameters
     */
    public void SendAlert(String title, String type, String message) {
        try {
            NotificationWindowController ac;

            Stage primaryStage = new Stage();
            primaryStage.setTitle(title);

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("NotificationWindowUI.fxml"));
            ac = new NotificationWindowController(type, message);
            loader.setController(ac);
            Parent root = loader.load();

            primaryStage.getIcons().add(new Image("deps//icon.png"));

            Scene myScene = new Scene(root);
            primaryStage.setScene(myScene);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getSuppressed());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
