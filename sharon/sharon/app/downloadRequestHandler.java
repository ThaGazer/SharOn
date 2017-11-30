/*
 * downloadRequestHandler
 * Version 1.0 created 10/11/2017
 *
 * Authors:
 * -Justin Ritter
 */
package sharon.app;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

/**
 * ask other nodes to download a file
 */
public class downloadRequestHandler implements Runnable {

    private static final String replyOK = "OK\n\n";

    private static Socket clntSoc;
    private Logger logger;
    private static String directory;
    private static String fileID;
    private static String fileName;

    /**
     * creates a download ask handler
     * @param soc the socket connection
     * @param log logger
     * @param dir directory of files
     * @param fID id of file
     * @param fName name of file to create
     */
    public downloadRequestHandler(Socket soc, Logger log, String dir,
                                  String fID, String fName) {
        clntSoc = soc;
        logger = log;
        directory = dir;
        fileID = fID + "\n";
        fileName = fName;
    }

    /**
     * handles a request request from other nodes
     * @param soc socket connection to other nodes
     */
    protected static void requestHandler(Socket soc) {
        try {
            InputStream in = soc.getInputStream();
            OutputStream out = soc.getOutputStream();

            out.write(fileID.getBytes());

            File file = new File(directory + fileName);

            FileOutputStream fos = new FileOutputStream(file);

            ByteBuffer buff = ByteBuffer.allocate(10000);
            while(!soc.isClosed()) {
                buff.putInt(in.read());
            }
            fos.write(buff.array());

        } catch (IOException e) {
            e.printStackTrace();
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
     * overrides Thread run()
     */
    @Override
    public void run() {
        requestHandler(clntSoc);
    }
}
