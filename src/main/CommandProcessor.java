package main;

import main.util.FileHandler;
import main.view.main.UIController;
import main.view.notification.NotificationWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;

/**
 * CommandController
 * Processes the command given to us by the PushReceiver class
 */
public class CommandProcessor {
    private static final Logger cpLogger = LoggerFactory.getLogger(CommandProcessor.class);
    static File fileLog = new File("deps/logs/GoogleAssistantPCC.log");

    private static FileHandler fHandler = null;
    private static WindowsPSCommander winCommander = null;
    private static UIController ui = null;

    public CommandProcessor(FileHandler ifHandler, WindowsPSCommander wpC, UIController uiC) {
        fHandler = ifHandler;
        winCommander = wpC;
        ui = uiC;
    }

    public UIController GetUI() {
        return ui;
    }

    /**
     * ProcessPush
     *
     * @param command Proccesses the command sent to use by PushReceiver.
     */
    public static void ProcessPush(String command) {
        cpLogger.info(command);
        for (int index = 0; index < fHandler.GetAppList().size(); index++) {
            if (fHandler.GetAppList().containsKey(command)) {
                cpLogger.info("Found trigger: " + command);
                try {
                    winCommander.RunExecutable(fHandler.GetAppList().get(command).toString());
                } catch (Exception e) {
                    cpLogger.info(e.getMessage());
                }
            }
        }

        if (ui.WindowsCommandsEnabled()) {
            for (int index = 0; index < fHandler.GetWinCommands().size(); index++) {
                if (fHandler.GetWinCommands().containsKey(command) && fHandler.isEnabled(command)) {
                    if (command.equals("Shutdown")) {
                        winCommander.Shutdown();
                    } else if (command.equals("restart")) {
                        winCommander.Restart();
                    } else if (command.equals("log out")) {
                        winCommander.Logout();
                    } else if (command.equals("lock")) {
                        winCommander.LockPC();
                    } else if (command.equals("go to sleep")) {
                        winCommander.Sleep();
                    } else if (command.equals("mute")) {
                        winCommander.Mute();
                    } else if (command.equals("unmute")) {
                        winCommander.Unmute();
                    } else if (command.equals("open the start menu")) { // TODO: a better way to do this.
                        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
                        try {
                            Robot robo = new Robot(gd);
                            robo.setAutoDelay(100);
                            robo.keyPress(KeyEvent.VK_WINDOWS);
                            robo.keyRelease(KeyEvent.VK_WINDOWS);
                        } catch (AWTException e) {
                            cpLogger.info(e.getMessage());
                        }
                    }
                }
            }
        }
    }
}