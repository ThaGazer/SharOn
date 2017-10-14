/*
 * Node
 * Version 1.0 created 9/27/2017
 *
 * Authors:
 * -Justin Ritter
 */

package sharon.app;

import sharon.serialization.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.*;

import static java.lang.System.exit;

/**
 * a peer to peer node
 */
public class Node {

    /*the command line parameters format*/
    private static final String cmdFormat = 
            "Parameter(s): <Server> <Port> <filePath>\n";

    /*the clients command options*/
    private static final String clientCmdFormat = "Usage: connect <address> " +
            "<port> / download <address> <port> <file ID> <file name> / " +
            "exit / <search string>\n";
    
    /*error messaging*/
    private static final String errorSocketClosed = "Connection closed early: ";
    private static final String errorIncorrectFrame = "Incorrect framing";
    private static final String errorCreation = "Could not create message: ";
    private static final String errorFNF = "File not found!";
    private static final String errorServConnectFail = "server accept failed";
    private static final String errorCode301 = "301";

    /*console output messages*/
    private static final String consoleGoodConnect = "Connection successful!\n";
    private static final String consoleCloseNode = "Shutting down Node";

    /*operations of the returned handshake/for string checking*/
    private static final String operationEXIT = "exit";
    private static final String operationConnect = "connect";
    private static final String operationDownload = "download";
    private static final String errorLocClient = "Client side: ";
    private static final String errorEOS = "end of stream";

    /*current protocol*/
    private static final String nodeProtocol = "1.0";

    /*number of executor thread to have available*/
    private static final int EXECUTETHREADCOUNT = 4;

    private static final String logFileLoc = "C:\\SharonLogs";
    private static final String logFName = "log.txt";

    /*the port number of the server for the node*/
    private static final Integer serverPortNumber = 2112;
    private static final Integer downloadPortNumber = 1968;

    /*the initial message for creating a node connection*/
    private static final String nodeInit =
            "INIT SharOn/" + nodeProtocol + "\n\n";
    private static final String nodeOK = "OK SharOn\n\n";
    private static final String nodeReject = "REJECT ";

    /*check if node was started as a server only node*/
    private static boolean servonlyStart = false;

    /*command parameters*/
    private static String nodeAddr;
    private static String docPath;
    private static int nodePort;

    private static List<Socket> socketArr;
    private static List<Thread> threadArr;
    private static List<Long> searchArr;

    /**
     * runs a P2P connection between two Nodes
     * @param args the command line parameters passed to the method
     */
    public static void main(String[] args) {
        socketArr = new ArrayList<>();
        threadArr = new ArrayList<>();
        searchArr = new ArrayList<>();

        //testing for # of args
        if(args.length == 0) {
            servonlyStart = true;
        } else if ((args.length < 3) || (args.length > 3)) {
            throw new IllegalArgumentException(cmdFormat);
        } else {
            paramCheck(args);
        }

        Logger logger = Logger.getLogger("practical");

        /*server thread pool creation*/
        Thread serverThread = new Thread(() -> {
        /*Creating server socket connection
        using command line parameters*/
            try (ServerSocket servSoc = new ServerSocket(serverPortNumber)) {
                while (true) {
                    Socket clntServer = servSoc.accept();
                    if(protocolHandShakeReceived(clntServer)) {
                        socketArr.add(clntServer);
                        Thread thread = new Thread(
                                new ServerService(clntServer, logger,
                                        docPath, downloadPortNumber));
                        threadArr.add(thread);
                        thread.start();
                    }
                }
            } catch (IOException e) {
                logger.log(Level.WARNING, errorServConnectFail, e);
                System.out.println(errorSocketClosed + e.getMessage());
            }
        });
        threadArr.add(serverThread);
        serverThread.start();

        Thread downloadThread = new Thread(() -> {
            try (ServerSocket dwnLoadSoc =
                         new ServerSocket(downloadPortNumber)) {
                dwnLoadSoc.setReuseAddress(true);
                Executor dwnloadService =
                        Executors.newFixedThreadPool(EXECUTETHREADCOUNT);
                while (true) {
                    Socket dwnLoadClient = dwnLoadSoc.accept();
                    socketArr.add(dwnLoadClient);
                    dwnloadService.execute(new downloadServiceHandler(dwnLoadClient, logger, docPath));

                }
            } catch (IOException e) {
                System.out.println(errorSocketClosed + e.getMessage());
            }
        });
        threadArr.add(downloadThread);
        downloadThread.start();

        if (servonlyStart) {
            Scanner scn = new Scanner(System.in);

            String cmdParams = scn.nextLine();
            String[] cmdParts = cmdParams.split("\\s");
            if ((args.length < 3) || (args.length > 3)) {
                throw new IllegalArgumentException(cmdFormat);
            }
            paramCheck(cmdParts);
        }

        try (Socket socket = new Socket(nodeAddr, nodePort)) {
            if (protocolHandShakeRequest(socket)) {
                socketArr.add(socket);
                clientHandler(socket, logger);
            }
        } catch (IOException e) {
            System.out.println(errorSocketClosed + e.getMessage());
        }

        try {
            for(Thread t : threadArr) {
                t.join();
            }

            for(Socket s : socketArr) {
                s.close();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            logger.log(Level.WARNING, "couldn't close socket", e);
        }
    }

    /**
     * checks the parameters passed in from consol
     * @param args parameters passed in
     */
    public static void paramCheck(String[] args) {
        nodeAddr = args[0]; //Nodes name or address
            /*Nodes port number*/
        nodePort = (args.length == 3) ? Integer.parseInt(args[1]) : 7;

            /*the documents folder path*/
        docPath = args[2];

            /*checking if the path exist*/
        try {
            File filePathing = new File(docPath);
            if (!filePathing.exists()) {
                throw new IOException(errorFNF);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            exit(0);
        }
    }

    /**
     * checks commands for client commmands
     * @param a the command broken in parts
     * @return if it is a client command
     */
    public static boolean cmdFormatCheck(String[] a) {
        switch(a[0].toLowerCase()) {
            case operationEXIT:
                return a.length == 1;
            case operationConnect:
                return a.length == 3;
            case operationDownload:
                return a.length == 5;
        }
        return false;
    }

    /**
     * handles the Node handshake for connecting to other nodes 
     * @param soc the socket of the Node's connection
     * @return protocol check
     * @throws IOException if socket read/write exception
     */
    public static boolean protocolHandShakeRequest(Socket soc) throws IOException {
        InputStream in = soc.getInputStream();
        OutputStream out = soc.getOutputStream();

        /*create the full init message*/
        String initMessage = nodeInit + nodeProtocol + "\n\n";
        
        /*writes the init message out the socket using the US_ASCII encoding*/
        System.out.println(initMessage.substring(0, initMessage.length()-2));
        out.write(initMessage.getBytes());

        int totByte = 0;
        int bytesread;
        byte[] message = nodeOK.getBytes();
        while(totByte < message.length) {
            if((bytesread = in.read
                    (message, totByte, message.length-totByte)) == -1) {
                throw new IOException(errorEOS);
            }
            totByte += bytesread;
        }

        String messageStr = new String(message);
        if(nodeOK.equals(messageStr)){
            System.out.println(consoleGoodConnect);
            return true;
        } else {
            throw new IOException(messageStr);
        }
    }

    /**
     * handles the Node handshake on the server side
     * @param soc the socket of the Node's connection
     * @return protocol check
     * @throws IOException if socket read/write exception
     */
    public static boolean protocolHandShakeReceived(Socket soc)
            throws IOException {
        InputStream in = soc.getInputStream();
        OutputStream out = soc.getOutputStream();

        String initMessage = nodeInit + nodeProtocol + "\n\n";
        int totbytes = 0;
        int bytesread;
        byte[] message = initMessage.getBytes();
        while(totbytes < initMessage.length()) {
            if((bytesread = in.read
                    (message, totbytes, message.length-totbytes)) == -1) {
                throw new IOException(errorEOS);
            }
            totbytes += bytesread;
        }

        String messageIn = new String(message);
        if(initMessage.equals(messageIn)) {
            out.write(nodeOK.getBytes());
            return true;
        } else {
            throw new IOException(nodeReject + errorCode301 + errorIncorrectFrame);
        }
    }

    /**
     * runs the client side operations of the Node
     * @param soc the socket connection to the other node
     * @throws IOException if I/o problems
     */
    public static void clientHandler(Socket soc, Logger logger)
            throws IOException {
        BufferedReader scn = new BufferedReader
                (new InputStreamReader(System.in));

        try {
            String command;
            String[] cmdParts = {""};

            /*reads next line from user*/
            while((command = scn.readLine()) != null) {
                if (!"".equals(command)) {
                    cmdParts = command.split("\\s");
                }
                if (operationEXIT.equals(cmdParts[0].toLowerCase())) {
                    if (cmdFormatCheck(cmdParts)) {
                        System.out.println(consoleCloseNode);
                        soc.close();
                        exit(0);
                    } else {
                        System.out.println(clientCmdFormat);
                    }
                } else if (operationConnect.equals(cmdParts[0].toLowerCase())) {
                    if (cmdFormatCheck(cmdParts)) {
                        connectHandler(cmdParts, logger);
                    } else {
                        System.out.println(clientCmdFormat);
                    }
                } else if (operationDownload.equals(cmdParts[0].toLowerCase())){
                    if (cmdFormatCheck(cmdParts)) {
                        downloadHandler(cmdParts, logger);
                    } else {
                        System.out.println(clientCmdFormat);
                    }
                } else {
                    searchHandler(command);
                }
            }
        } catch(IOException ioE) {
            System.out.println
                    (errorLocClient + ioE.getMessage());
        } catch(BadAttributeValueException baveE) {
            System.out.println(errorLocClient + errorCreation +
                    baveE.getMessage() + " " + baveE.getAttributeName());
        }
    }

    /**
     * handles searching
     * @param searchStr string to search for
     * @throws IOException if encoding goes wrong
     * @throws BadAttributeValueException could not create search message
     */
    public static void searchHandler(String searchStr)
            throws IOException, BadAttributeValueException {
        Long searchID = new Random().nextLong();
        byte[] id = toByteArray(searchID);

        searchArr.add(searchID);
        Message searchMessage = new Search
                (id, 1,
                        RoutingService.BREADTHFIRSTBROADCAST,
                        "00000".getBytes(), "00000".getBytes(),
                        searchStr);

        /*send search request to all nodes currently connected*/
        for(Socket s : socketArr) {
            searchMessage.encode(
                    new MessageOutput(s.getOutputStream()));
        }
    }

    /**
     * handles making new connection
     * @param params connect commands
     * @param logger logger
     * @throws IOException
     */
    public static void connectHandler(String[] params, Logger logger)
            throws IOException {
        Socket socket = new Socket(params[1], Integer.parseInt(params[2]));

        if(protocolHandShakeRequest(socket)) {
            socketArr.add(socket);

            Thread clntConnectThread = new Thread(new ServerService
                    (socket, logger, docPath, downloadPortNumber));
            threadArr.add(clntConnectThread);
            clntConnectThread.start();
        }
    }

    /**
     * handles down loading a file
     * @param params download parameters
     * @param logger logger
     * @throws IOException problems creating a socket
     */
    public static void downloadHandler(String[] params, Logger logger)
            throws IOException {
        Socket socket = new Socket(params[1], Integer.parseInt(params[2]));
        Thread dwnloadReq = new Thread(new downloadRequestHandler(socket,
                logger, docPath, params[3], params[4]));
        threadArr.add(dwnloadReq);
        dwnloadReq.start();
    }

    /**
     * turns a double into a byte array
     * @param value the double to turn into a byte array
     * @return the double converted to a byte array
     */
    public static byte[] toByteArray(long value) {
        return ByteBuffer.allocate(15).putDouble(value).array();
    }
}
