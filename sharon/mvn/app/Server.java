/*
 * Server
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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.*;

import static java.lang.System.exit;

public class Server {
    /*constatns for class*/
    private static final int MAXPACKETSIZE = 1534; //maximum size of a Packet
    private static final int MAXADDRESSES = 255; //maximum size of a Packet
    private static final String LOGGERNAME = Server.class.getName();
    private static final String LOGGERFILENAME = "./server.log";

    /*message out to the logger or console*/
    private static final String USAGE = "Usage: <port>";
    private static final String msgServerStart = "Started server on: ";
    private static final String msgPacketRecieved = "Received a packet: ";
    private static final String msgUnexpectedError = "Unexpected error: ";
    private static final String msgUnexpectedMessageType =
            "Unexpected message type: ";
    private static final String msgCommunicationProblem =
            "Communication problem: ";
    private static final String msgInvalidMessage = "Invalid message: ";
    private static final String msgServerCrash = "The server crashed: ";

    /*variables to be used by all functions in class*/
    private static DatagramSocket socket;
    private static DatagramPacket datagramPacket;
    private static Set<InetSocketAddress> nodes;
    private static Set<InetSocketAddress> mavens;
    private static Logger logger = Logger.getLogger(LOGGERNAME);

    public static void main(String[] args) throws IOException {
        if(args.length != 1) {
            System.out.println(USAGE);
            exit(-1);
        }

        /*setup for logger*/
        setup_logger();

        /*initialize node list*/
        nodes = new HashSet<>(MAXADDRESSES);

        /*initialize maven list*/
        mavens = new HashSet<>(MAXADDRESSES);

        /*read port from command line*/
        int servPort = Integer.parseInt(args[0]);

        try {
            socket = new DatagramSocket(servPort);
            Packet pack;
            logger.config(msgServerStart);
            System.out.println(msgServerStart + socket.getLocalSocketAddress());

            try {
                while (true) {
                    datagramPacket = new DatagramPacket
                            (new byte[MAXPACKETSIZE], MAXPACKETSIZE);
                    PacketType receivedPType = PacketType.ANSWERREQUEST;
                    try {
                        socket.receive(datagramPacket);
                        pack = new Packet(datagramPacket.getData());
                        logger.info(msgPacketRecieved + pack);
                        receivedPType = pack.getType();

                        if (pack.getError() == ErrorType.NONE) {
                            /*process the received packet*/
                            processPacketType(pack);
                        } else {
                            /*log unexpected error*/
                            logger.warning(msgUnexpectedError + pack);

                            /*send error packet back*/
                            pack.setError(ErrorType.INCORRECTPACKET);
                            pack.clearAddress();
                        }
                    } catch (IOException ioe) {
                        /*log communication problem*/
                        logger.severe(msgCommunicationProblem +
                                ioe.getMessage());

                        /*build system error packet*/
                        pack = new Packet(PacketType.ANSWERREQUEST,
                                        ErrorType.SYSTEM,0);
                    } catch (IllegalArgumentException iae) {
                        /*log invalid message*/
                        logger.warning(msgInvalidMessage +
                                iae.getMessage());

                        /*build incorrect packet*/
                        pack = new Packet(PacketType.ANSWERREQUEST,
                                ErrorType.INCORRECTPACKET, 0);

                    }

                    if(receivedPType.equals(PacketType.REQUESTMAVENS) ||
                            receivedPType.equals(PacketType.REQUESTNODES)) {
                        sendPacket(pack);
                    }
                }
            } catch (Exception e) {
                logger.severe(msgServerCrash + e);
            }
        } catch(IOException ioe) {
            //log communication problem
            logger.severe(msgCommunicationProblem + "socket creation");
            System.err.println("Could not create server on port: " + servPort);
        }
    }

    /**
     * operates off of the PacketType of the packet
     * @param pIn the Packet to operate off of
     */
    private static void processPacketType(Packet pIn) {
        PacketType type = pIn.getType();
        pIn.setType(PacketType.ANSWERREQUEST);
        switch (type) {
            case REQUESTNODES:
                for (InetSocketAddress a : nodes) {
                    pIn.addAddress(a);
                }
                break;
            case REQUESTMAVENS:
                for (InetSocketAddress a : mavens) {
                    pIn.addAddress(a);
                }
                break;
            case NODEADDITIONS:
                for(InetSocketAddress addr : pIn.getAddrList()) {
                    if(nodes.size() < MAXADDRESSES) {
                        nodes.add(addr);
                    }
                }
                break;
            case NODEDELETIONS:
                nodes.removeAll(pIn.getAddrList());
                break;
            case MAVENADDITIONS:
                for(InetSocketAddress addr : pIn.getAddrList()) {
                    if(nodes.size() < MAXADDRESSES) {
                        mavens.add(addr);
                    }
                }
                break;
            case MAVENDELETIONS:
                mavens.removeAll(pIn.getAddrList());
                break;
            case ANSWERREQUEST:
                /*log unexpected message*/
                logger.warning(msgUnexpectedMessageType + pIn);

                pIn.setError(ErrorType.INCORRECTPACKET);
                pIn.getAddrList().clear();
                break;
            case CLEARCACHE:
                nodes.clear();
                mavens.clear();
            default:
                //log invalid message
                logger.warning(msgInvalidMessage + pIn);
        }
    }

    /**
     * sends a packet out of the DatagramSocket for the class
     * @param pack the Packet to send out
     * @throws IOException sending issues
     */
    static void sendPacket(Packet pack) throws IOException {
        try {
            logger.info("Sending packet: " + pack);
            datagramPacket.setData(pack.encode());
            socket.send(datagramPacket);
        } catch (IllegalArgumentException iae) {
            logger.warning(msgInvalidMessage + iae.getMessage());
        }
    }

    /**
     * constructs the logger for the server
     * @throws IOException could not find logging file
     */
    private static void setup_logger() throws IOException {
        LogManager.getLogManager().reset();

        Handler fileHandle = new FileHandler(LOGGERFILENAME);
        Handler consoleHandle = new ConsoleHandler();

        fileHandle.setLevel(Level.ALL);
        consoleHandle.setLevel(Level.WARNING);

        logger.addHandler(fileHandle);
        logger.addHandler(consoleHandle);
    }
}
