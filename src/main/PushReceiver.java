package main;

import main.net.iharder.jpushbullet2.*;
import main.util.FileHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

/**
 * PushReceiver
 * This class is used to listen and extract the body of text from a pushbullet notification
 *
 * TODO:
 * Make AccessToken variable load from a config file
 */
public class PushReceiver implements PushbulletListener {
    private static String AccessToken = "";
    private static final Logger prLogger = LoggerFactory.getLogger(PushReceiver.class);
    private static CommandController cController = null;

    public PushReceiver(CommandController cControl, FileHandler fHandler) {
        try {
            cController = cControl;
            AccessToken = fHandler.GetAPIKey();
            StartListening();
        } catch (InterruptedException e) {
            prLogger.info(e.getMessage());
        }
        catch (PushbulletException e) {
            prLogger.info(e.getMessage());
        }
    }

    /**
     * Our pushBullet event listener
     * @throws PushbulletException
     * @throws InterruptedException
     */
    public void StartListening() throws PushbulletException, InterruptedException {
        PushbulletClient client = new PushbulletClient( AccessToken );
        client.addPushbulletListener(new PushbulletListener(){

            /**
             * pushReceived
             * @param pushEvent
             * We pass the notification to the command controller
             */
            @Override
            public void pushReceived(PushbulletEvent pushEvent) {
                //System.out.println("pushReceived PushEvent received: " + pushEvent);
                prLogger.info("pushReceived PushEvent received: " + pushEvent.toString());
                System.out.println(pushEvent.toString());
                try {
                    List<Push> pushes = client.getPushes(0);
                    //System.out.println("Number of pushes: " + pushes.size());
                    Push p = pushes.get(0);
                    cController.ProcessPush(p.getBody());
                    pushes.clear();
                } catch (PushbulletException e) {
                    System.out.println(e.getMessage());
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
        client.startWebsocket();
    }

    public void pushReceived(PushbulletEvent pushEvent) {
    }

    public void devicesChanged(PushbulletEvent pushEvent) {
    }

    public void websocketEstablished(PushbulletEvent e) {
    }
}
