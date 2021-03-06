package main;

import main.jpushbullet2.*;
import main.util.FileHandler;
import main.view.notification.NotificationWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * PushReceiver
 * This class is used to listen and extract the body of text from a pushbullet notification
 */
public class PushReceiver implements PushbulletListener {
    private static String AccessToken = "";
    private static final Logger prLogger = LoggerFactory.getLogger(PushReceiver.class);
    private static CommandProcessor cProcessor = null;
    private static PushbulletClient client = null;
    private static PushbulletListener listener = null;

    public PushReceiver(CommandProcessor cProcess, FileHandler fHandler) {
        try {
            cProcessor = cProcess;
            AccessToken = fHandler.GetAPIKey();
            StartListening();
        } catch (InterruptedException e) {
            prLogger.info(e.getMessage());
        }
        catch (PushbulletException e) {
            prLogger.info(e.getMessage());
        }
    }

    public void StopListening() {
        client.removePushBulletListener(listener);
        cProcessor.GetUI().SetProgressIndicator(50.00d);
        client.stopWebsocket();
        cProcessor.GetUI().SetProgressIndicator(0.00d);
    }

    /**
     * Our pushBullet event listener
     * @throws PushbulletException
     * @throws InterruptedException
     */
    public void StartListening() throws PushbulletException, InterruptedException {
        try {
            client = new PushbulletClient(AccessToken);
            cProcessor.GetUI().SetProgressIndicator(35.00d);
            client.addPushbulletListener(listener = new PushbulletListener() {

                /**
                 * pushReceived
                 *
                 * @param pushEvent We pass the notification to the command processor class
                 */
                @Override
                public void pushReceived(PushbulletEvent pushEvent) {
                    prLogger.info("pushReceived PushEvent received: " + pushEvent.toString());
                    System.out.println(pushEvent.toString());
                    try {
                        List<Push> pushes = client.getPushes(0);
                        Push p = pushes.get(0);
                        cProcessor.ProcessPush(p.getBody());
                        pushes.clear();
                    } catch (PushbulletException e) {
                        System.out.println(e.getMessage());
                        NotificationWindow n = new NotificationWindow("Error", "Error", e.getMessage());
                        cProcessor.GetUI().SetProgressIndicator(0.00d);
                    }
                }

                @Override
                public void devicesChanged(PushbulletEvent pushEvent) {
                }

                @Override
                public void websocketEstablished(PushbulletEvent pushEvent) {

                }
            });

            prLogger.info("Starting websocket...");
            cProcessor.GetUI().SetProgressIndicator(55.00d);
            client.startWebsocket();
            cProcessor.GetUI().SetProgressIndicator(100.00d);
        } catch(Exception e) {
            prLogger.info(e.getMessage());
        }
    }

    public void pushReceived(PushbulletEvent pushEvent) {
    }

    public void devicesChanged(PushbulletEvent pushEvent) {
    }

    public void websocketEstablished(PushbulletEvent e) {
    }
}
