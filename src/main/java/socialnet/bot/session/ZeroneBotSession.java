package socialnet.bot.session;

import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class ZeroneBotSession extends DefaultBotSession {
    public ZeroneBotSession() {
        super();
    }

    @Override
    public synchronized void start() {
        boolean isConnected = false;
        while (!isConnected) {
            try {
                super.start();
                isConnected = true;
            } catch (Exception ex) {
                System.err.println("Connection failed, retrying in 5 seconds...");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ignored) {}
            }
        }
    }
}
