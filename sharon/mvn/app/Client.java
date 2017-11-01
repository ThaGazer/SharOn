package mvn.app;


import mvn.serialization.ClientCommand;
import mvn.serialization.ErrorType;
import mvn.serialization.Packet;
import mvn.serialization.PacketType;

import java.net.*;
import java.util.Scanner;

public class Client {

    private static final int TIMEOUT = 3000; //resend timeout
    private static final int MAXTRIES = 3; //maximum retransmissions

    private static final String UNKNOWNOP = "Unknown operation";
    private static final String EXPECT1ARG =
            "Command expects at least one argument: ";
    private static final String UNEXPECTEDARG =
            "Command does not expect an argument:";
    private static final String MAUSAGE = "MA[<sp><name/address><port>]+";
    private static final String MDUSAGE = "MD[<sp><name/address><port>]+";
    private static final String NAUSAGE = "NA[<sp><name/address><port>]+";
    private static final String NDUSAGE = "ND[<sp><name/address><port>]+";

    public static void main(String[] args)
            throws UnknownHostException, SocketException {

        if(args.length != 2) {
            throw new IllegalArgumentException
                    ("Usage: <server(name or IP address)> <port>");
        }
        InetAddress servName = InetAddress.getByName(args[0]); //gets address
        int servPort = Integer.getInteger(args[1]); //gets port

        DatagramSocket sock = new DatagramSocket();
        sock.setSoTimeout(TIMEOUT);
        String command;
        boolean clientFinished = false;
        do {
            Packet sp; //sending packet
            Packet rp; //receiving packet

            Scanner scn = new Scanner(System.in);
            String clientCmd = scn.nextLine();
            String[] clientCmdSplit = clientCmd.split("\\s|:");
            ClientCommand clientOP = ClientCommand.getByCmd(clientCmdSplit[0]);

            if(clientOP == null) {
                System.err.println(UNKNOWNOP);
            } else {
                switch (clientOP) {
                    case MAVENADDITION:
                        sp = new Packet(PacketType.MAVENADDITIONS, ErrorType.NONE, )
                        break;
                    case MAVENDELETION:
                        break;
                    case NODEADDITION:
                        break;
                    case NODEDELETION:
                        break;
                    case REQUESTMAVEN:
                        break;
                    case REQUESTNODE:
                        break;
                    case EXIT:
                        break;
                    default:
                        System.err.println(UNKNOWNOP);
                }
            }
        } while(!clientFinished);
    }

    private boolean checkCommand(String cmd) {

    }
}
