package main;

import main.util.FileHandler;
import main.view.controller.main.UIController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;

/**
 * CommandController
 * Processes the command given to us by the PushReceiver class
 *
 * TODO:
 * Logging (Error, event, Process)
 * Read/Writing from the application
 * [DONE] Remove the hard-coded launching of apps, so were able to have users easily launch their own custom applications.
 * [DONE] Enable/Disable certain commands based off of user preferences
 *
 * Expand upon the list of windows commands
 */
public class CommandProcessor {
    private static final Logger cpLogger = LoggerFactory.getLogger(CommandProcessor.class);
    private static Process nirProc = null;

    static File fileLog = new File("src/deps/GoogleAssistantPCC.log");

    private static FileHandler fHandler = null;
    private static UIController ui = null;

    public CommandProcessor(FileHandler ifHandler, UIController uiC) {
        fHandler = ifHandler;
        ui = uiC;
    }

    /**
     * ProcessPush
     * @param command
     * Proccesses the command sent to use by PushReceiver.
     * TODO:
     *     Popup notifying the user windows commands arent enabled if a windows command trigger is used
     */
    public static void ProcessPush(String command) {
        cpLogger.info(command);
        for (int index = 0; index < fHandler.GetAppList().size(); index++) {
            if (fHandler.GetAppList().containsKey(command)) {
                cpLogger.info("Found trigger: " + command);
                try {
                    nirProc = new ProcessBuilder("src/deps/nircmd.exe", "exec", "show", fHandler.GetAppList().get(command).toString()).start();
                } catch (IOException e) {
                    cpLogger.info(e.getMessage());
                }
            }
        }

        for (int index = 0; index < fHandler.GetWinCommands().size(); index++) {
            if (fHandler.GetWinCommands().containsKey(command)) {
                if (ui.WindowsCommandsEnabled()) {
                    if (command.equals("open the start menu")) {
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