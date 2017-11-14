/*
 * ServerService
 * Version 1.0 created 10/11/2017
 *
 * Authors:
 * -Justin Ritter
 */
package sharon.app;

import sharon.serialization.*;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * provides a server service for Nodes
 */
public class ServerService implements Runnable {

    private static final String errorConnectionClosed = "connection closed early";
    private static final String errorMsgCreation = "could not create message";

    private static final String infoReadingResp = "Reading search response: ";
    private static final String infoDownloadHost = "Download host: ";

    private static Socket clntSoc;
    private static Logger logger = Logger.getLogger(Node.class.getName());
    private static String directory;
    private static int searchMatch;
    private static int downloadPort;
    private static List<Result> resultList;

    /**
     * Creates a new ServerService
     * @param soc the connection to nodes
     * @param dir directory of files
     * @param dPort port where to download
     */
    public ServerService(Socket soc, String dir, int dPort) {
        clntSoc = soc;
        directory = dir;
        searchMatch = 0;
        downloadPort = dPort;
        resultList = new ArrayList<>();
    }

    /**
     * the runnable function
     * @param soc the connection to nodes
     */
    protected static void serverHandler(Socket soc) {

        try{
            MessageInput in = new MessageInput(soc.getInputStream());
            MessageOutput out = new MessageOutput(soc.getOutputStream());

            logger.info("Created and started thread " +
                    Thread.currentThread().getName());

            while(true) {
                try {
                    if(in.hasMore()) {
                        Message msg = Message.decode(in);
                        System.out.println(msg);
                        switch(msg.getMessageType()) {
                            case 1:
                                if(fileChecker(directory,
                                        ((Search) msg).getSearchString())) {
                                    responseBuilder(msg).encode(out);
                                }
                                break;
                            case 2:
                                String id = String.valueOf(ByteBuffer.wrap
                                        (msg.getID()).order
                                        (ByteOrder.LITTLE_ENDIAN).getInt());
                                Response res = (Response) msg;
                                logger.info(infoReadingResp + id + "\n" +
                                        infoDownloadHost + " /" +
                                        res.getResponseHost().getAddress() +
                                        ":" + res.getResponseHost().getPort());
                                for(Result r : res.getResultList()) {
                                    logger.info("  " + r.getFileName() +
                                            ": ID" + r.getFileID() + " (" +
                                            r.getFileSize() + " bytes)\n");
                                }
                                break;
                        }
                    }
                } catch(IOException|BadAttributeValueException e) {
                    System.out.println(in.getline());
                    logger.log(Level.WARNING, errorMsgCreation, e);
                }
            }
        } catch(Exception e) {
            logger.log(Level.SEVERE, errorConnectionClosed, e);
        } finally {
            try{
                System.out.println("thread closed: " +
                        Thread.currentThread().getName());
                clntSoc.close();
            } catch(IOException ignored) {
            }
        }
    }

    /**
     * finds a file in the directory
     * @param dir directory to search
     * @param file file to look for
     * @return is the file was forund
     * @throws BadAttributeValueException errors building result
     */
    public static boolean fileChecker(String dir, String file)
            throws BadAttributeValueException {
        File root = new File(dir);
        File[] filesInRoot = root.listFiles();

        if(filesInRoot != null) {
            for (File f : filesInRoot) {
                if(f.isDirectory()) {
                    fileChecker(f.getName(), file);
                } else if(f.getName().equals(file)) {
                    searchMatch++;
                    Integer fileId = f.getName().hashCode();
                    resultList.add(new Result(fileId.longValue(),
                            f.getTotalSpace(), f.getName()));
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * builds the response message for searches
     * @param msg the original search
     * @return the built response
     * @throws BadAttributeValueException error building response
     */
    public static Response responseBuilder(Message msg)
            throws BadAttributeValueException {
        InetSocketAddress dwnLoadSocket = new InetSocketAddress(downloadPort);
        Response  res = new Response(msg.getID(), 1,
                RoutingService.BREADTHFIRSTBROADCAST,
                msg.getDestinationAddress(),
                msg.getSourceAddress(), dwnLoadSocket);
        res.setMatches(searchMatch);
        for(Result r : resultList) {
            res.addResult(r);
        }
        return res;
    }

    /**
     * overrides Thread run()
     */
    @Override
    public void run() {
        serverHandler(clntSoc);
    }
}
