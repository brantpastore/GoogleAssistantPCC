package main;

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
 * Remove the hard-coded launching of apps, so were able to have users easily launch their own custom applications.
 * Enable/Disable certain commands based off of user preferences
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
    static File fileLog = new File("C:/GoogleAssistantPCC.log");

    public CommandController() {
    }

    public static void InitNircmd() throws Exception {
    }

    /**
     * ProcessPush
     * @param command
     * Proccesses the command sent to use by PushReceiver.
     */
    public static void ProcessPush(String command) {
        if (command.equals("open Chrome")) {
            try {
                nirProc = new ProcessBuilder("C:/windows/nircmd.exe", "exec", "show", "C:/Program Files (x86)/Google/Chrome/Application/chrome.exe").start();
                reader = new BufferedReader(new InputStreamReader(nirProc.getInputStream()));
                writer = new BufferedWriter(new OutputStreamWriter(nirProc.getOutputStream()));
                writer.write("exec show C:/Program Files (x86)/Google/Chrome/Application/chrome.exe\n");
                writer.flush();
                System.out.println(reader.readLine());
            } catch (IOException e) {
                ccLogger.info(e.getMessage());
                ccLogger.info(nirErrStream.toString());
            }
        } else if (command.equals("open the start menu")) {
            System.out.println("opening the start menu");
            GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            try {
                Robot robo = new Robot(gd);
                robo.setAutoDelay(100);
                robo.keyPress(KeyEvent.VK_WINDOWS);
                robo.keyRelease(KeyEvent.VK_WINDOWS);
            } catch (AWTException e) {
                System.out.println(e.getMessage());
            }
        } else if (command.equals("go to sleep")) {
            //nircmdBuild.command("standby");
        }
    }
}
