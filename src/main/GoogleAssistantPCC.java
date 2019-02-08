package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import main.util.FileHandler;
import main.view.controller.main.UIController;
import main.view.controller.addapplication.AddAppController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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

        Scene myScene = new Scene(myPane);
        primaryStage.setScene(myScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        gaLogger.info("Launching program at " + LocalDateTime.now());
        try {
            fHandler = new FileHandler();
            ui = new UIController(fHandler, fHandler.GetAPIKey(), fHandler.GetWinCommands(), fHandler.GetAppList());
            launch(args);
        } catch (Exception e) {
            gaLogger.info(e.getMessage());
        }
    }
}
