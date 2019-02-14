package main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import main.util.FileHandler;
import main.view.controller.main.UIController;
import main.view.controller.addapplication.AddAppController;
import main.view.controller.notification.NotificationWindow;
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

    private static java.awt.PopupMenu trayMenu = null;
    private static java.awt.TrayIcon trayIcon = null;
    private static java.awt.SystemTray tray = null;
    private static final String icon = "deps//icon.png";

    @Override
    public void start(Stage primaryStage) throws Exception {
        AddAppController ap = new AddAppController(fHandler);
        primaryStage.setTitle("Google Assistant PC Controller");
        Pane myPane = (Pane) FXMLLoader.load(getClass().getResource("view/controller/main/gui.fxml"));
        primaryStage.getIcons().add(new Image(icon));
        primaryStage.setResizable(false);
        Scene myScene = new Scene(myPane);
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
