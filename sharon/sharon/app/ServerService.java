package sharon.app;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Justin Ritter on 10/12/2017.
 */
public class ServerService implements Runnable {

    private static final String errorInHandler = "Exception in serverHandler";

    private Socket clntSoc;
    private Logger logger;

    public ServerService(Socket soc, Logger logr) {
        clntSoc = soc;
        logger = logr;
    }

    public void serverHandler(Socket soc, Logger logger) {
        try{
            throw new IOException("nice try guy");
        } catch(IOException e) {
            logger.log(Level.WARNING, errorInHandler, e);
        } finally {
            try{
                clntSoc.close();
            } catch(IOException e) {

            }
        }
    }
    @Override
    public void run() {
        serverHandler(clntSoc, logger);
    }
}
