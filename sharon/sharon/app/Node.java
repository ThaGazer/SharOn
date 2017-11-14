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


    /*the clients command options*/
    private static final String usageConnect =
            "Usage: connect <address> <port>";
    private static final String usageDownload =
            "Usage: download <address> <port> <file ID> <file name>";
    private static final String usageExit = "Usage: exit";
    private static final String usageSearch = "Usage: <search string>";

    /*error messaging*/
    private static final String errorSocketClosed = "Connection closed early";
    private static final String errorIncorrectFrame = "Incorrect framing";
    private static final String errorFNF = "File/Folder not found!";
    private static final String errorServConnectFail = "server accept failed";
    private static final String errorCode301 = "301";
    private static final String errorLocClient = "client side ";
    private static final String errorServerFailed = "server side ";
    private static final String errorEOS = "end of stream";

    /*messages to either logger or user*/
    private static final String msgCmdFormat =
            "Parameter(s): <Server> <Port> <filePath>\n";
    private static final String msgServerStart = "Starting this nodes server";
    private static final String msgGoodConnect = "connection successful: ";
    private static final String msgBadConnect = "connection failed: ";
    private static final String msgCloseNode = "Shutting down Node";
    private static final String msgCommandConfirm =
            " is a key word, are you trying to search instead(y/n): ";
    private static final String msgNoConnection =
            "You are not connected to anyone";


    /*number of executor thread to have available*/
    private static final int EXECUTETHREADCOUNT = 4;

    /*the port number of the server for the node*/
    private static Integer serverPortNumber = 2112;
    private static final Integer downloadPortNumber = 1968;

    /*name of teh logger file*/
    private static final String LOGGERNAME = Node.class.getName();
    private static final String LOGGERFNAME = "./node.log";
    private static Logger logger = Logger.getLogger(LOGGERNAME);

    /*message packets for the node protocol*/
    private static final String nodeProtocol = "1.0";
    private static final String nodeInit =
            "INIT SharOn/" + nodeProtocol + "\n\n";
    private static final String nodeOK = "OK SharOn\n\n";
    private static final String nodeReject = "REJECT ";

    /*check if node was started as a server only node*/
    private static boolean servonlyStart = false;

    /*for determining which protocol version to use*/
    private static final int protocolRec = 1;
    private static final int protocolReq = 2;

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
    public static void main(String[] args) throws IOException {
        socketArr = new ArrayList<>();
        threadArr = new ArrayList<>();
        searchArr = new ArrayList<>();

        //testing for # of args
        if(args.length == 0) {
            servonlyStart = true;
        } else {
            if(!paramCheck(args)) {
                System.out.println(msgCmdFormat);
                exit(-1);
            }
        }

        /*set the logger information*/
        setup_logger();

        if(!servonlyStart) {
            serverPortNumber += 1;
            Socket startSoc = new Socket(nodeAddr, nodePort);
            addConnection(startSoc, protocolReq);
        }

        /*server thread pool creation*/
        serverHandler();

        /*handles users request*/
        clientHandler();

        /*Thread downloadThread = new Thread(() -> {
            try (ServerSocket dwnLoadSoc =
                         new ServerSocket(downloadPortNumber)) {
                dwnLoadSoc.setReuseAddress(true);
                Executor dwnloadService =
                        Executors.newFixedThreadPool(EXECUTETHREADCOUNT);
                while (true) {
                    Socket dwnLoadClient = dwnLoadSoc.accept();
                    socketArr.add(dwnLoadClient);
                    dwnloadService.execute(new downloadServiceHandler
                            (dwnLoadClient, logger, docPath));

                }
            } catch (IOException e) {
                System.out.println(errorSocketClosed + e.getMessage());
            }
        });
        threadArr.add(downloadThread);
        downloadThread.start();*/


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

    public static void addConnection(Socket soc, int version)
            throws IOException {
        boolean protocolRes;
        switch(version) {
            case 1:
                protocolRes = protocolHandShakeReceived(soc);
                break;
            case 2:
                protocolRes =protocolHandShakeRequest(soc);
                break;
            default:
                protocolRes = false;
        }

        if(protocolRes) {
            logger.info(msgGoodConnect + soc.getInetAddress());
            //System.out.println(msgGoodConnect);
            socketArr.add(soc);
            Thread servThread = new Thread(new ServerService
                    (soc, docPath, downloadPortNumber));
            threadArr.add(servThread);
            servThread.start();
        } else {
            logger.info(msgBadConnect + soc.getInetAddress());
            soc.close();
        }
    }

    /**
     * checks the parameters passed in from consol
     * @param args parameters passed in
     */
    public static boolean paramCheck(String[] args) {
        if (args.length != 3) {
            return false;
        }
        nodeAddr = args[0]; //Nodes name or address

        /*Nodes port number*/
        nodePort = Integer.parseInt(args[1]);

        /*the documents folder path*/
        docPath = args[2];

        /*checking if the path exist*/
        File filePathing = new File(docPath);
        if (!filePathing.exists()) {
            System.err.println(errorFNF);
            exit(-1);
        }
        return true;
    }

    /**
     * checks commands for client commmands
     * @param a the command broken in parts
     * @return if it is a client command
     */
    public static boolean cmdFormatCheck(String[] a) {
        ClientCommand cmd = ClientCommand.getByCmd(a[0].toLowerCase());
        switch(cmd) {
            case EXIT:
                return a.length == 1;
            case CONNECT:
                return a.length == 3;
            case DOWNLOAD:
                return a.length == 5;
            case SEARCH:
                return true;
            default:
                return false;
        }
    }

    /**
     * handles the Node handshake for connecting to other nodes 
     * @param soc the socket of the Node's connection
     * @return protocol check
     * @throws IOException if socket read/write exception
     */
    public static boolean protocolHandShakeRequest(Socket soc)
            throws IOException {
        InputStream in = soc.getInputStream();
        OutputStream out = soc.getOutputStream();
        
        /*writes the init message out the socket using the US_ASCII encoding*/
        out.write(nodeInit.getBytes());

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

        int totbytes = 0;
        int bytesread;
        byte[] message = nodeInit.getBytes();
        while(totbytes < nodeInit.length()) {
            if((bytesread = in.read
                    (message, totbytes, message.length-totbytes)) == -1) {
                throw new IOException(errorEOS);
            }
            totbytes += bytesread;
        }

        String messageIn = new String(message);
        if(nodeInit.equals(messageIn)) {
            out.write(nodeOK.getBytes());
            return true;
        } else {
            throw new IOException
                    (nodeReject + errorCode301 + errorIncorrectFrame);
        }
    }

    /**
     * runs the client side operations of the Node
     * @throws IOException if I/o problems
     */
    public static void clientHandler()
            throws IOException {
        BufferedReader scn = new BufferedReader
                (new InputStreamReader(System.in));

            String command;
            String[] cmdParts;

            /*reads next line from user*/
            while((command = scn.readLine()) != null) {
                try {
                    cmdParts = command.split("\\s");
                    ClientCommand cmd = ClientCommand.getByCmd(cmdParts[0]);
                    switch(cmd) {
                        case CONNECT:
                            if(cmdFormatCheck(cmdParts)) {
                               connectHandler(cmdParts);
                            } else {
                               System.out.println(cmdParts[0] +
                                       msgCommandConfirm);
                               String userResponse = scn.readLine();
                               if(userResponse.charAt(0) == 'y') {
                                   searchHandler(command);
                               } else {
                                   System.out.println(usageConnect);
                               }
                            }
                           break;
                        case DOWNLOAD:
                            if(cmdFormatCheck(cmdParts)) {
                                downloadHandler(cmdParts, logger);
                            } else {
                                System.out.println(cmdParts[0] +
                                        msgCommandConfirm);
                                String userResponse = scn.readLine();
                                if(userResponse.charAt(0) == 'y') {
                                    searchHandler(command);
                                } else {
                                    System.out.println(usageDownload);
                                }
                            }
                           break;
                        case SEARCH:
                            searchHandler(command);
                            break;
                        case EXIT:
                            System.out.println(msgCloseNode);
                            exit(0);
                    }
                } catch(IOException|BadAttributeValueException e) {
                    logger.log(Level.WARNING, errorLocClient,
                            e.fillInStackTrace());
                }
            }
    }

    /**
     * runs the server side of the node
     */
    public static void serverHandler() {
        /*server thread pool creation*/
        Thread serverThread = new Thread(() -> {
            logger.info(msgServerStart);
            /*Creating server socket connection
            using command line parameters*/
            try(ServerSocket serverSoc = new ServerSocket(serverPortNumber)) {
                while(true) {
                    try {
                        Socket clntServer = serverSoc.accept();
                        addConnection(clntServer, protocolRec);
                    } catch(IOException e) {
                        logger.log(Level.WARNING, errorServerFailed, e);
                    }
                }
            } catch(IOException e) {
                logger.log(Level.SEVERE, errorSocketClosed, e);
            }
        });
        threadArr.add(serverThread);
        serverThread.start();
    }

    /**
     * handles searching
     * @param searchStr string to search for
     * @throws IOException if encoding goes wrong
     * @throws BadAttributeValueException could not create search message
     */
    public static void searchHandler(String searchStr)
            throws IOException, BadAttributeValueException {
        Message searchMessage = new Search
                (nextID(), 1,
                        RoutingService.BREADTHFIRSTBROADCAST,
                        "00000".getBytes(), "00000".getBytes(),
                        searchStr);
        /*send search request to all nodes currently connected*/
        if(!socketArr.isEmpty()) {
            for(Socket s : socketArr) {
                searchMessage.encode(
                        new MessageOutput(s.getOutputStream()));
            }
        } else {
            System.out.println(msgNoConnection);
        }
    }

    /**
     * handles making new connection
     * @param params connect commands
     */
    public static void connectHandler(String[] params) throws IOException {
        Socket socket = new Socket(params[1], Integer.parseInt(params[2]));
        addConnection(socket, protocolReq);
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
     * constructs the logger for the server
     * @throws IOException could not find logging file
     */
    private static void setup_logger() throws IOException {
        LogManager.getLogManager().reset();

        Handler fileHandle = new FileHandler(LOGGERFNAME);
        Handler consoleHandle = new ConsoleHandler();

        fileHandle.setLevel(Level.ALL);
        consoleHandle.setLevel(Level.ALL);

        logger.addHandler(fileHandle);
        logger.addHandler(consoleHandle);
    }

    public static byte[] nextID() {
        byte[] res = new byte[15];
        for(int i = 0; i < 15; i++) {
            res[i] = (byte) (new Random().
                    nextInt() & 0x000000ff);
        }
        return res.clone();
    }
}
