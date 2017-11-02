/*
 * Client
 * Version 1.0 created 10/31/2017
 *
 * Authors:
 * -Justin Ritter
 */
package mvn.app;

import mvn.serialization.ErrorType;
import mvn.serialization.Packet;
import mvn.serialization.PacketType;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.*;
import java.util.Scanner;

import static java.lang.System.exit;

/**
 * handles client side maven operations
 */
public class Client {

    //useful constant numbers
    private static final int TIMEOUT = 3000; //resend timeout
    private static final int MAXTRIES = 3; //maximum retransmissions
    private static final int OPERATION_ENDIDX = 2; //the end index of the operation for client commands
    private static int SESSIONID = 0;

    //messages to user
    private static final String errorCommunicationProblem = "Communication problem: ";
    private static final String errorInvalidMessage = "Invalid message: ";
    private static final String errorUnknownSource = "Error: received packet from unknown source";
    private static final String errorUnknownOP = "Error: unknown operation";
    private static final String messageUnexpectedType = "Unexpected message type";
    private static final String messageUnexpectedSession = "Unexpected session ID";
    private static final String messageSignOff = "Closing coonnection...\nGood bye!";
    private static final String messageTimeout = "response from server timed out";
    private static final String messageNoResponse = "no response from server -- giving up";
    private static final String EXPECTARG =
            "Command expects at least one argument: ";
    private static final String UNEXPECTEDARG =
            "Command does not expect an argument:";

    //operation usages
    private static final String USAGE = "Usage: ";
    private static final String USAGEMA = "MA[<sp><name/address>:<port>]+";
    private static final String USAGEMD = "MD[<sp><name/address>:<port>]+";
    private static final String USAGENA = "NA[<sp><name/address>:<port>]+";
    private static final String USAGEND = "ND[<sp><name/address>:<port>]+";
    private static final String USAGERM = "RM";
    private static final String USAGERN = "RN";

    //class variables
    private static DatagramSocket sock;
    private static InetAddress servName;
    private static int servPort;

    /**
     * handles client side operations of a maven
     * @param args command line parameters
     * @throws UnknownHostException could not find host address
     * @throws SocketException problem with udp socket
     */
    public static void main(String[] args)
            throws UnknownHostException, SocketException {

        if(args.length != 2) {
            throw new IllegalArgumentException
                    ("Usage: <server(name or IP address)> <port>");
        }
        servName = InetAddress.getByName(args[0]); //gets address
        servPort = Integer.parseInt(args[1]); //gets port

        sock = new DatagramSocket();
        sock.setSoTimeout(TIMEOUT);

        boolean clientFinished = false;
        do {
            SESSIONID++;

            /*read command input from user*/
            Scanner scn = new Scanner(System.in);
            String clientCmd = scn.nextLine();

            /*check if user inputted data*/
            if(!clientCmd.isEmpty()) {

                /*finds the operation out of the input*/
                int opEndIdx = clientCmd.contains(" ") ? clientCmd.indexOf(" ") : clientCmd.length();
                String op = clientCmd.substring(0, opEndIdx);

                /*converts user inputted operation into a ClientCommand*/
                ClientCommand clientOP = ClientCommand.getByCmd(op);

                /*check is command is valid*/
                if (clientOP == null) {
                    System.err.println(errorUnknownOP);
                } else {
                    try {
                        switch (clientOP) {
                            case MAVENADDITION:
                                /*builds and sends a maven addition packet to server*/
                                if (!buildPacket(PacketType.MAVENADDITIONS, clientCmd)) {
                                    System.err.println(USAGEMA);
                                }
                                break;
                            case MAVENDELETION:
                                /*builds and sends a maven deletion packet to server*/
                                if (!buildPacket(PacketType.MAVENDELETIONS, clientCmd)) {
                                    System.err.println(USAGEMD);
                                }
                                break;
                            case NODEADDITION:
                                /*builds and sends a node addition packet to server*/
                                if (!buildPacket(PacketType.NODEADDITIONS, clientCmd)) {
                                    System.err.println(USAGENA);
                                }
                                break;
                            case NODEDELETION:
                                /*builds and sends a node deletion packet to server*/
                                if (!buildPacket(PacketType.NODEDELETIONS, clientCmd)) {
                                    System.err.println(USAGEND);
                                }
                                break;
                            case REQUESTMAVEN:
                                /*a bounds check for rm command*/
                                if (clientCmd.length() > OPERATION_ENDIDX) {
                                    System.err.println(UNEXPECTEDARG + USAGERM);
                                } else {
                                    /*sends a request maven packet and processes received packet*/
                                    requestPacket(PacketType.REQUESTMAVENS);
                                }
                                break;
                            case REQUESTNODE:
                                /*a bounds check for rn command*/
                                if (clientCmd.length() > OPERATION_ENDIDX) {
                                    System.err.println(UNEXPECTEDARG + USAGERN);
                                } else {
                                    /*sends a request node packet and processes received packet*/
                                    requestPacket(PacketType.REQUESTNODES);
                                }
                                break;
                            case EXIT:
                                /*exits maven client gui*/
                                clientFinished = true;
                                break;
                            default:
                                System.err.println(errorUnknownOP);
                        }
                    } catch(IllegalArgumentException iae) {
                        System.err.println(errorInvalidMessage + iae.getMessage());
                    } catch(IOException ioe) {
                        System.err.println(errorCommunicationProblem + ioe.getMessage());
                        exit(-1);
                    }
                }
            } else {
                System.err.println(errorUnknownOP);
            }
        } while(!clientFinished);

        /*closes Datagram socket and prompts user*/
        sock.close();
        System.out.println(messageSignOff);
    }

    /**
     * checks address parameters from user for wrong format or missing syntax
     * @param arg address to check
     * @return if parameter passed check
     */
    private static boolean checkCommandParam(String arg) {
        return arg.contains(":");
    }

    /**
     * builds a udp packet to send to the server provided on start-up
     * @param type the packet type of the Packet
     * @param command the full command the user inputted
     * @return if completion of sending the Packet
     * @throws IOException errors in sending the datagram
     */
    private static boolean buildPacket(PacketType type, String command) throws IOException {
        /*creates a new packet for sending*/
        Packet pack = new Packet(type, ErrorType.NONE, SESSIONID);
        String[] cmdSplit = command.split("\\s"); //splits user command to get parameters

        if(cmdSplit.length > 1) {
            for (int i = 1; i < cmdSplit.length; i++) { //loops over all arguments found in user command
                String param = cmdSplit[i];
                /*check for address formatting*/
                if (checkCommandParam(param)) {
                    /*gets the address name*/
                    String addrName = param.substring(0, param.indexOf(":"));
                    /*gets the address port*/
                    int port = Integer.parseInt(param.substring(param.lastIndexOf(":") + 1));
                    pack.addAddress(new InetSocketAddress(addrName, port)); //adds created address to the packet
                } else {
                    System.err.print(USAGE);
                    return false;
                }
            }

            /*encodes packet for sending*/
            byte[] bytesToSend = pack.encode();
            DatagramPacket sendPacket = new DatagramPacket
                    (bytesToSend, bytesToSend.length, new InetSocketAddress(servName, servPort));
            sock.send(sendPacket); //sends Datagram packet to server
        } else {
            System.err.print(EXPECTARG);
            return false;
        }
        return true;
    }

    /**
     * sends a udp packet to the server provided on start-up and processes response packet
     * @param type type of Packet to send to server
     * @throws IOException problem with sending or receiving packets
     */
    private static void requestPacket(PacketType type) throws IOException {
        Packet sPack = new Packet(type, ErrorType.NONE, SESSIONID); //packet for sending
        Packet rPack = null; //packet for reading
        byte[] bytesToSend = sPack.encode();

        DatagramPacket sendPacket = new DatagramPacket //datagram packet for sending
                (bytesToSend, bytesToSend.length, new InetSocketAddress(servName,servPort));
        /*datagram packet for reading*/
        DatagramPacket receivePacket = new DatagramPacket(new byte[bytesToSend.length], bytesToSend.length);

        int tries = 0; //number of tries so far
        boolean receivedResponse = false; //if a response is received
        do {
            sock.send(sendPacket); //send datagram to server

            try {
                sock.receive(receivePacket); //wait for 3sec for a response

                rPack = new Packet(receivePacket.getData()); //convert datagram into a Packet

                if(!receivePacket.getAddress().equals(servName)) { //check if packet come from server
                    throw new IOException(errorUnknownSource);
                } else if(rPack.getType() != PacketType.ANSWERREQUEST) { //check if packet is of right type
                    tries += 1;
                    System.out.println(messageUnexpectedType);
                } else if (rPack.getSessionID() != 0 || rPack.getSessionID() != SESSIONID) { //check if right session id
                    tries += 1;
                    System.out.println(messageUnexpectedSession);
                } else { //if all other checks fail a correct packet was received
                    receivedResponse = true;
                }
            } catch(InterruptedIOException e) { //if timeout or some other problem increment tries and prompt user
                tries += 1;
                System.out.println(messageTimeout);
            }
        } while(!receivedResponse && tries < MAXTRIES); //loop until packet received or max of 3 loops

        /*if a packet was received from server show it to user*/
        if(receivedResponse) {
            System.out.print("Type=" + rPack.getType().name() + ", Error=" + rPack.getError().name() + ", Session ID="
                    + rPack.getSessionID() + ", Addrs=");

            for(InetSocketAddress addr : rPack.getAddrList()) {
                System.out.print(addr.toString() + ", ");
            }
            System.out.println();
        } else { //tell user there was no response from server
            System.out.println(messageNoResponse);
        }
    }
}
