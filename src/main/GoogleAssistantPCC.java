package main;

import main.util.FileHandler;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import org.slf4j.Logger;

/**
 * GoogleAssistantPCC
 * is used to launch the Event Listener for PushBullet
 *
 * TODO:
 * GUI for ease of use
 */
public class GoogleAssistantPCC {
    private static Logger gaLogger = LoggerFactory.getLogger(GoogleAssistantPCC.class);
    private static CommandController cController = null;
    private static PushReceiver pReceiver = null;
    private static FileHandler fHandler = null;

    public static void main(String[] args) {
        gaLogger.info("Launching program at " + LocalDateTime.now());
        try {
            fHandler = new FileHandler();
            cController = new CommandController(fHandler);
            pReceiver = new PushReceiver(cController, fHandler);
        } catch (Exception e) {
            gaLogger.info(e.getMessage());
        }
    }
}
