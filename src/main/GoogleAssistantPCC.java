package main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import main.util.FileHandler;
import main.view.main.UIController;
import main.view.notification.NotificationWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

public class GoogleAssistantPCC extends Application {
    private static Logger gaLogger = LoggerFactory.getLogger(GoogleAssistantPCC.class);
    private static FileHandler fHandler = null;
    private static UIController ui = null;

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("resources/gui.fxml"));
            primaryStage.setTitle("Google Assistant PC Controller");
            primaryStage.getIcons().add(new Image("deps/icon.png"));
            Scene myScene = new Scene(root);
            primaryStage.setScene(myScene);
            primaryStage.show();

            //  Are any NotificationWindows open, or the AddApplication is open? close it all!.
            primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent t) {
                    Platform.setImplicitExit(true);
                    Platform.exit();
                    System.exit(0);
                }
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        gaLogger.info("Launching program at " + LocalDateTime.now());
        try {
            //If pushbullet isn't installed, we notify the user and don't run
            Path pBullet = Paths.get("C:\\Program Files (x86)\\Pushbullet\\pushbullet.exe");
            if (!Files.exists(pBullet)) {
                NotificationWindow a = new NotificationWindow("Pushbullet Error", "Error", "You need to have Pushbullet installed to use this program!");
                Thread.sleep(5000);
            } else {
                fHandler = new FileHandler();
                ui = new UIController(fHandler, fHandler.GetAPIKey(), fHandler.GetWinCommands(), fHandler.GetAppList());
                launch(args);
            }
        } catch (Exception e) {
            gaLogger.info(e.getMessage());
        }
    }
}
