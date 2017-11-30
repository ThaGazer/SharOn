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
    private static String directory;

    private static String errNoFile = "could not find file";

    /**
     * creates a request handler
     * @param soc socket connection to other nodes
     * @param dir directory of files
     */
    public downloadServiceHandler(Socket soc, String dir) {
        socket = soc;
        directory = dir;
    }

    /**
     * handles a download request from other nodes
     * @param soc socket connection to other node
     */
    public static void serviceHandler(Socket soc) {
        try {
            InputStream in = soc.getInputStream();
            OutputStream out = soc.getOutputStream();

            String fIDStr = "";
            int bIn;
            boolean readDone = false;
            while(!readDone && (bIn = in.read()) != -1) {
                if(!('\n' == bIn)) {
                    fIDStr += (char)bIn;
                } else {
                    readDone = true;
                }
            }

            File outFile = fileFinder(fIDStr);
            FileInputStream readFile = new FileInputStream(outFile);

            int a;
            while((a = readFile.read()) != -1) {
                out.write(a);
            }
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
    private static File fileFinder(String idStr) throws IOException {
        int id = Integer.parseInt(idStr);
        File root = new File(directory);
        File[] filesInRoot = root.listFiles();

        if(filesInRoot != null) {
            for(File f : filesInRoot) {
                if(f.isDirectory()) {
                    fileFinder(idStr);
                } else if(f.getName().hashCode() == id){
                    return f;
                }
            }
        }
       throw new IOException(errNoFile);
    }

    /**
     * overrides Thread run()
     */
    @Override
    public void run() {
        serviceHandler(socket);
    }
}
