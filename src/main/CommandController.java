package main;

import main.util.FileHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

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
public class CommandController {
    private static final Logger ccLogger = LoggerFactory.getLogger(CommandController.class);

    private static ProcessBuilder nircmdBuild = null;
    private static Process nirProc = null;
    private static InputStream nirOutput = null;
    private static InputStream nirErrStream = null;
    private static OutputStream nirInput = null;
    private static BufferedReader reader = null;
    private static BufferedWriter writer = null;
    static File fileLog = new File("C:/GAPCC/GoogleAssistantPCC.log");

    private static FileHandler fHandler = null;

    public CommandController(FileHandler ifHandler) {
        fHandler = ifHandler;
    }

    public static void InitNircmd() throws Exception {
    }

    /**
     * ProcessPush
     *
     * @param command Proccesses the command sent to use by PushReceiver.
     */
    public static void ProcessPush(String command) {
        System.out.println(command);
        for (int index = 0; index < fHandler.getAppList().size(); index++) {
            if (fHandler.getAppList().containsKey(command)) {
                System.out.println("Found trigger: " + command);
                try {
                    nirProc = new ProcessBuilder("C:/windows/nircmd.exe", "exec", "show", fHandler.getAppList().get(command).toString()).start();
                } catch (IOException e) {
                    ccLogger.info(e.getMessage());
                    ccLogger.info(nirErrStream.toString());
                }
            }
        }

        for (int index = 0; index < fHandler.getWinCommands().size(); index++) {
            if (fHandler.getWinCommands().containsKey(command)) {
                if (command.equals("open the start menu")) {
                    System.out.println("Found trigger: " + command);
                    GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
                    try {
                        Robot robo = new Robot(gd);
                        robo.setAutoDelay(100);
                        robo.keyPress(KeyEvent.VK_WINDOWS);
                        robo.keyRelease(KeyEvent.VK_WINDOWS);
                    } catch (AWTException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        }
    }
}