package main.view.notification;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.view.notification.NotificationWindowController;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * NotificationWindow
 * Used to create a window with a message to the user
 */
public class NotificationWindow {
    private String type;
    private String message;

    public NotificationWindow() {
    }

    /**
     * NotificationWindow
     * @param title
     * @param type
     * @param message
     * We set the class type, message, and string to the parameters given
     */
    public NotificationWindow(String title, String type, String message) {
        this.type = type;
        this.message = message;
        SendAlert(title, type, message);
    }

    public String GetType() {
        return this.type;
    }

    public String GetMessage() {
        return this.message;
    }

    /**
     * SendAlert
     *
     * @param type
     * @param message Creates a new window where we change the labels to the respective parameters
     */
    public void SendAlert(String title, String type, String message) {
        try {
            // Since the alert windows (javaFX) are being used in outside threads (ex. pushbulletClient) this is a workaround.
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        Stage primaryStage = new Stage();
                        primaryStage.setTitle(title);
                        primaryStage.getIcons().add(new Image("deps/icon.png"));

                        FXMLLoader loader = new FXMLLoader();
                        loader.setLocation(NotificationWindow.class.getClassLoader().getResource("main/resources/NotificationWindowUI.fxml"));
                        loader.setController(new NotificationWindowController(type, message));

                        Parent root = loader.load();

                        if (title.equals("Error")) {
                            primaryStage.getIcons().add(new Image("deps/error.png"));
                        } if (title.equals("Alert")) {
                            primaryStage.getIcons().add(new Image("deps/alert.png"));
                        } else if (title.equals("Pushbullet Error")) {
                            primaryStage.getIcons().add(new Image("deps/pushbullet.png"));
                        } else {
                            primaryStage.getIcons().add(new Image("deps/original_icon.png"));
                        }

                        Scene myScene = new Scene(root);
                        primaryStage.setScene(myScene);
                        primaryStage.setResizable(false);
                        primaryStage.show();

                        // Close the notification window automatically every 4 seconds
                        Timer timer = new Timer(4000, new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent arg0) {
                                Platform.runLater(new Runnable() { // Since the Timer is a seperate thread from our javaFX thread
                                    @Override
                                    public void run() {
                                        primaryStage.close();
                                    }
                                });
                            }
                        });
                        timer.setRepeats(false);
                        timer.start(); // Go go go!
                    } catch (NullPointerException e) {
                        System.out.println(e.getMessage());
                        System.out.println(e.getSuppressed());
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            });
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}