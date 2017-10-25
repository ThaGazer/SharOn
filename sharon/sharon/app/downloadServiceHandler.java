/*
 * downloadServiceHandler
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
 * handles a download request from other nodes
 */
public class downloadServiceHandler implements Runnable {

    private static Socket socket;
    private static Logger logger;
    private static String directory;

    /**
     * creates a request handler
     * @param soc socket connection to other nodes
     * @param log logger
     * @param dir directory of files
     */
    public downloadServiceHandler(Socket soc, Logger log, String dir) {
        socket = soc;
        logger = log;
        directory = dir;
    }

    /**
     * handles a download request from other nodes
     * @param soc socket connection to other node
     * @param logger logger
     */
    public static void serviceHandler(Socket soc, Logger logger) {
        try {
            InputStream in = soc.getInputStream();
            OutputStream out = soc.getOutputStream();

            String fIDStr = "";
            int byteIn;
            boolean readDone = false;
            while(!readDone && (byteIn = in.read()) != -1) {
                String inStr = String.valueOf(byteIn);
                if(!"\n".equals(inStr)) {
                    fIDStr += inStr;
                } else {
                    readDone = true;
                }
            }

            File outFile = new File(fileFinder(Integer.valueOf(fIDStr)));
            FileInputStream readFile = new FileInputStream(outFile);
            ByteBuffer buff = ByteBuffer.allocate(10000);

            int a;
            while((a = readFile.read()) != -1) {
                buff.putInt(a);
            }

            out.write(buff.array());
            soc.close();


        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            try{
                System.out.println("thread closed: " +
                        Thread.currentThread().getName());
                socket.close();
            } catch(IOException ignored) {
            }
        }
    }

    /**
     * finds a file in the directory
     * @param idStr id of file to find
     * @return return the name of the file if found
     */
    private static String fileFinder(int idStr) {
        File root = new File(directory);
        File[] filesInRoot = root.listFiles();

        if(filesInRoot != null) {
            for(File f : filesInRoot) {
                if(f.isDirectory()) {
                    fileFinder(idStr);
                } else if(f.getName().hashCode() == idStr){
                    return f.getName();
                }
            }
        }
        return "";
    }

    /**
     * overrides Thread run()
     */
    @Override
    public void run() {
        serviceHandler(socket, logger);
    }
}
