package main;

import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import org.slf4j.Logger;

/**
 * GoogleAssistantPCC
 * is used to launch the Event Listener for PushBullet
 *
 * TODO:
 * Create a ConfigLoader class for launching custom applications
 * Logging
 * GUI for ease of use
 */
public class GoogleAssistantPCC {
    private static Logger gaLogger = LoggerFactory.getLogger(GoogleAssistantPCC.class);
    private static CommandController cController = null;
    private static PushReceiver pReceiver = null;

    public static void main(String[] args) {
        gaLogger.info("Launching program at " + LocalDateTime.now());
        try {
            cController = new CommandController();
        } catch (Exception e) {
            gaLogger.info(e.getMessage());
        }

        try {
            pReceiver = new PushReceiver(cController);
        } catch (Exception e) {
            gaLogger.info(e.getMessage());
        }
    }
}
