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
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static java.lang.System.currentTimeMillis;
import static java.lang.System.exit;

public class Node {

    /*the command line parameters format*/
    private static final String cmdFormat = 
            "Parameter(s): <Server> <Port> <filePath>";
    
    /*error messaging*/
    private static final String errorSocketClosed = "Connection closed early: ";
    private static final String errorIncorrectFrame = "Incorrect framing";
    private static final String errorCreation = "Could not create message: ";
    private static final String errorFNF = "File not found!";

    /*console output messages*/
    private static final String consoleGoodConnect = "Connection successful!";
    private static final String consoleRejectConnect = "Connection rejected: ";
    private static final String consoleSearchRes = "Search response for: ";
    private static final String consoleDownloadHost = "Download host: ";
    
    private static final String nodeProtocol = "1.0";

    /*operations of the returned handshake/for string checking*/
    private static final String operationOK = "OK";
    private static final String operationREJECT = "REJECT";
    private static final String errorLocServer = "Server side: ";
    private static final String errorLocClient = "Client side: ";


    /*number of threads in pool*/
    private static final int THREADPOOL = 10;

    /*the initial message for creating a node connection*/
    private static final String nodeInit = "INIT SharOn/";

    /**
     * runs a P2P connection between two Nodes
     * @param args the command line parameters passed to the method
     */
    public static void main(String[] args) {
        
        if((args.length < 3) || (args.length > 3)) { //testing for # of args
            throw new IllegalArgumentException(cmdFormat);
        }
        
        String nodeAddr = args[0]; //Nodes name or address
        /*Nodes port number*/
        int nodePort = (args.length == 3) ? Integer.parseInt(args[1]) : 7;
        System.out.println("Nodes addr: " + args[0] + ":" + args[1]);

        /*the documents folder path*/
        String docPath = args[2];

        /*checking if the path exist*/
        try {
            File filePathing = new File(docPath);
            if (!filePathing.exists()) {
                throw new IOException(errorFNF);
            }
        } catch(IOException e) {
            System.out.println(e.getMessage());
            exit(0);
        }
        System.out.println("Working directory: " + args[2]);


        /*Creating socket connection using command line parameters*/
        System.out.println(nodeAddr + " " + nodePort);
        try(Socket socket = new Socket(nodeAddr, nodePort)) {
            if (protocolHandShake(socket)) {
                if(!socket.isClosed()) {
                /*continually looking for search request*/
                    serverThread(socket);

                /*handles searches from console*/
                    clientHanlder(socket);
                } else {
                    throw new IOException("Handshake");
                }

            }
        } catch(IOException e) {
            System.out.println(errorSocketClosed + e.getMessage());
        }
    }

    /**
     * handles the Node handshake for connecting to other nodes 
     * @param soc the socket of the Node's connection
     * @throws IOException if socket read/write exception
     */
    public static boolean protocolHandShake(Socket soc) throws IOException {
//        BufferedReader in = new BufferedReader(new InputStreamReader
//                (soc.getInputStream(), StandardCharsets.US_ASCII));
        OutputStreamWriter out = new OutputStreamWriter
                (soc.getOutputStream(), StandardCharsets.US_ASCII);
        InputStream in = soc.getInputStream();

        /*create the full init message*/
        String initMessage = nodeInit + nodeProtocol + "\\n\\n";
        
        /*writes the init message out the socket using the US_ASCII encoding*/
        System.out.println(initMessage);
        out.write(initMessage);

        /*reads the next message from the Node*/
        /*String messageStr;
        if((message = in.readLine()) == null) {
            throw new IOException(errorSocketClosed);
        }*/

        int totByte = 0;
        int bytesread;
        byte[] message = "OK SharOn\\n\\n".getBytes();
        while(totByte < message.length) {
            if((bytesread = in.read(message, totByte, message.length-totByte)) == -1) {

            }
            totByte += bytesread;
        }
        String messageStr = new String(message);

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
     * acts as the nodes server side handling search request
     * @param soc the socket connection
     * @throws IOException if messageInput creation fails
     */
    private static void serverThread(Socket soc) throws IOException {
        MessageInput in = new MessageInput(soc.getInputStream());

        Thread servThread = new Thread(() -> {
            try {
                while (!soc.isClosed()) {
                    if(in.hasMore()) {
                        Message messageIn = Message.decode(in);

                        if (messageIn.getMessageType() == 0x01) {
                            for (int i = 0; i < THREADPOOL; i++) {
                                Thread searchThread = new Thread(() ->
                                        searchHandler(messageIn));
                                searchThread.start();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println
                        (errorLocServer + errorSocketClosed + e.getMessage());
                exit(0);
            } catch (BadAttributeValueException e) {
                System.out.println
                        (errorLocServer + e.getMessage());
                exit(0);
            }
        });
        servThread.start();
    }

    /**
     * runs the client side operations of the Node
     * @param soc the socket connection to the other node
     * @throws IOException if I/o problems
     */
    public static void clientHanlder(Socket soc) throws IOException {
        MessageInput in = new MessageInput(soc.getInputStream());
        MessageOutput out = new MessageOutput(soc.getOutputStream());
        Scanner scn = new Scanner(System.in);

        try {
            while (scn.hasNext()) {
                        /*reads */
                String search = scn.nextLine();

                        /*creates the search message to send*/
                Message searchMessage = new Search
                        (toByteArray(Math.random()), 100,
                                RoutingService.BREADTHFIRSTBROADCAST,
                                "0001".getBytes(), "0002".getBytes(), search);

                searchMessage.encode(out); //sends search request out

                        /*reads in response if it is a response message*/
                Message responseMessage = Message.decode(in);
                if(responseMessage.getMessageType() == 0x02) {
                    System.out.println(consoleSearchRes + search);

                    String sourceAddr = new String
                            (responseMessage.getSourceAddress());
                    System.out.println
                            (consoleDownloadHost + sourceAddr);

                    String sourceID = new String
                            (responseMessage.getID());
                    System.out.println(sourceID);

                    String thisID = new String(searchMessage.getID());
                    System.out.println(thisID);
                }
            }
        } catch(BadAttributeValueException e) {
            System.out.println(errorLocClient + errorCreation + e.getMessage());
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
                if(msgParts.length == 2) {
                    return true;
                }
                return false;
            case operationREJECT:
                if(msgParts.length >= 3) {
                    return true;
                }
                return false;
            default: return false;
        }
    }

    /**
     * handles search for a file at this node
     * @param msg the search message
     */
    public static void searchHandler(Message msg) {

    }

    /**
     * turns a double into a byte array
     * @param value the double to turn into a byte array
     * @return the double converted to a byte array
     */
    public static byte[] toByteArray(double value) {
        byte[] bytes = new byte[8];
        ByteBuffer.wrap(bytes).putDouble(value);
        return bytes;
    }
}
