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
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.System.exit;

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

    /*console output messages*/
    private static final String consoleGoodConnect = "Connection successful!\n";
    private static final String consoleCloseNode = "Shutting down Node";

    /*operations of the returned handshake/for string checking*/
    private static final String operationOK = "OK";
    private static final String operationREJECT = "REJECT";
    private static final String operationEXIT = "exit";
    private static final String operationConnect = "connect";
    private static final String operationDownload = "download";
    private static final String errorLocServer = "Server side: ";
    private static final String errorLocClient = "Client side: ";


    /*number of threads in pool*/
    private static final int THREADPOOL = 10;

    /*current protocol*/
    private static final String nodeProtocol = "1.0";

    /*the port number of the server for the node*/
    private static final Integer serverPortNumber = 2112;

    /*the initial message for creating a node connection*/
    private static final String nodeInit = "INIT SharOn/";

    /*check if node was started as a server only node*/
    private static boolean servonlyStart = false;

    /*command parameters*/
    private static String nodeAddr;
    private static String docPath;
    private static int nodePort;

    private static List<Socket> socketArr;
    private static List<Thread> threadArr;
    private static List<Integer> searchArr;

    //private static Logger logger;

    public Node() {


        /*init logger for server*/
        //logger = Logger.getLogger("practical");
    }

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

        /*Creating server socket connection using command line parameters*/
        try(ServerSocket servSoc = new ServerSocket(serverPortNumber)) {

            Logger logger = Logger.getLogger("practical");

            Executor service = Executors.newCachedThreadPool();
            /*server thread pool creation*/
            Thread serverThread = new Thread(() -> {
                while (true) {
                    try {
                        Socket clntServer = servSoc.accept();
                        socketArr.add(clntServer);
                        service.execute(new ServerService(clntServer, logger));
                    } catch (IOException e) {
                        logger.log(Level.WARNING,
                                "server accept failed", e);
                    }
                }
            });
            serverThread.start();
        } catch(IOException e) {
            System.out.println(errorSocketClosed + e.getMessage());
        }

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
            if(protocolHandShake(socket)) {
                socketArr.add(socket);
                clientHandler(socket);
            }
        } catch(IOException e) {
            System.out.println(errorSocketClosed + e.getMessage());
        }
    }

    /**
     * checks the parameters passed into function
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
     * Checks if the Node send the correct packets framing
     * @param msg the message from the Node
     * @return if the message passed the frame check
     */
    public static boolean frameCheck(String msg) {
        String[] msgParts = msg.split("\\s|\\n");
        String op = msgParts[0];

        switch(op) {
            case operationOK:
                return msgParts.length == 2;
            case operationREJECT:
                return msgParts.length >= 3;
            default: return false;
        }
    }

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
     * @throws IOException if socket read/write exception
     */
    public static boolean protocolHandShake(Socket soc) throws IOException {
        InputStream in = soc.getInputStream();
        OutputStream out = soc.getOutputStream();

        /*create the full init message*/
        String initMessage = nodeInit + nodeProtocol + "\n\n";
        
        /*writes the init message out the socket using the US_ASCII encoding*/
        System.out.println(initMessage.substring(0, initMessage.length()-2));
        out.write(initMessage.getBytes());

        int totByte = 0;
        int bytesread;
        byte[] message = "OK SharOn\n\n".getBytes();
        while(totByte < message.length) {
            if((bytesread = in.read
                    (message, totByte, message.length-totByte)) == -1) {
                throw new IOException("end of stream");
            }
            totByte += bytesread;
        }

        String messageStr = new String(message);
        System.out.println(messageStr.substring(0, messageStr.length()-2));
        String[] msgParts = messageStr.split("\\s|\\n");
        if(frameCheck(messageStr)) {
            if(operationOK.equals(msgParts[0])) {
                System.out.println(consoleGoodConnect);
            }
            if(operationREJECT.equals(msgParts[0])) {
                throw new IOException(messageStr);
            }
            return true;
        } else {
            throw new IOException(errorIncorrectFrame);
        }
    }

    /**
     * runs the client side operations of the Node
     * @param soc the socket connection to the other node
     * @throws IOException if I/o problems
     */
    public static void clientHandler(Socket soc) throws IOException {
        Scanner scn = new Scanner(System.in);

        try {
            while (scn.hasNext()) {
                /*reads next line from user*/
                String command = scn.nextLine();

                String[] cmdParts = command.split("\\s");
                if(operationEXIT.equals(cmdParts[0].toLowerCase())) {
                    if(cmdFormatCheck(cmdParts)) {
                        System.out.println(consoleCloseNode);
                        soc.close();
                        exit(0);
                    } else {
                        System.out.println(clientCmdFormat);
                    }
                } else if(operationConnect.equals(cmdParts[0].toLowerCase())) {
                    if(cmdFormatCheck(cmdParts)) {
                        connectHandler(cmdParts);
                    } else {
                        System.out.println(clientCmdFormat);
                    }
                } else if(operationDownload.equals(cmdParts[0].toLowerCase())) {
                    if(cmdFormatCheck(cmdParts)) {
                        downloadHandle(cmdParts);
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
                    baveE.getMessage() + baveE.getAttributeName());
        }
    }

    public static void searchHandler(String searchStr)
            throws IOException, BadAttributeValueException {
        int searchID = new Random().nextInt();
        searchArr.add(searchID);
        Message searchMessage = new Search
                (toByteArray(searchID), 1,
                        RoutingService.BREADTHFIRSTBROADCAST,
                        "0.0.0.0.0".getBytes(), "0.0.0.0.0".getBytes(),
                        searchStr);

        /*send search request to all nodes currently connected*/
        for(Socket s : socketArr) {
            searchMessage.encode(
                    new MessageOutput(s.getOutputStream()));
        }
    }

    public static void connectHandler(String[] params) throws IOException {
        Socket socket = new Socket(params[1], Integer.parseInt(params[2]));

        if(protocolHandShake(socket)) {
            socketArr.add(socket);
        }
    }

    public static void downloadHandle(String[] params) {

    }

    /**
     * handles the responses about a search
     * @param in the search message stream
     */
    public static void responseHandler(MessageInput in) {

    }

    /**
     * turns a double into a byte array
     * @param value the double to turn into a byte array
     * @return the double converted to a byte array
     */
    public static byte[] toByteArray(double value) {
        return ByteBuffer.allocate(8).putDouble(value).array();
    }

    /**
     * turns a int into a byte array
     * @param value the int to turn into a byte array
     * @return the int converted to a byte array
     */
    public static byte[] toByteArray(int value) {
        return ByteBuffer.allocate(4).putInt(value).array();
    }
}
