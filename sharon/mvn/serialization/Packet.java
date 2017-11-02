/*
 * Packet
 * Version 1.1 created 10/24/2017
 *
 * Authors:
 * -Justin Ritter
 */
package mvn.serialization;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents MVN message
 * Version: 1.1
 */
public class Packet {

    /*is the header size of the packet in bytes*/
    private static final Integer HEADERSIZE = 4;

    /*total amount of addresses allowed*/
    private static final Integer ADDRESSALLOWEDSIZE = 256;

    /*error messages*/
    private static final String errorFrameOff = "incorrect frame";
    private static final String errorSetSession = "sessionID";
    private static final String errorSetVersion = "version";
    private static final String errorSetType = "PacketType";
    private static final String errorSetError = "ErrorType";
    private static final String errorNullObj = "Null object";
    private static final String errorAddAddress = "Add address";
    private static final String errorMaxAddresses =
            "Reached max allowed addresses";


    /*the version of protocol running*/
    private static final int pVersion = 4;

    /*storing addresses for maven node*/
    private static Set<InetSocketAddress> pAddress;

    /*the type of packet*/
    private static PacketType pType;

    /*error message if any*/
    private static ErrorType pError;

    /*used to map server responses to outstanding request*/
    private static int pSession;

    /**
     * Construct new packet from byte array
     * @param buf buffer containing encoded packet
     * @throws IOException if byte array too long/short or buf is null
     * @throws IllegalArgumentException if bad attribute value
     */
    public Packet(byte[] buf) throws IOException, IllegalArgumentException {
        if(buf != null) {
            pAddress = new HashSet<>(ADDRESSALLOWEDSIZE);
            int bufLoc = 0;

            if (buf.length > 0) {
                try {
                    /*checks is versions are the same*/
                    if ((buf[bufLoc] >>> 4) != pVersion) {
                        throw new IllegalArgumentException(errorSetVersion);
                    }

                    /*assigns the packet type*/
                    if ((pType = PacketType.getByCode(buf[bufLoc] & 0x0F)) == null) {
                        throw new IllegalArgumentException(errorSetType);
                    }
                    bufLoc++;

                    /*assigns the error type*/
                    if ((pError = ErrorType.getByCode(buf[bufLoc] & 0xFF)) == null) {
                        throw new IllegalArgumentException(errorSetError);
                    }
                    bufLoc++;

                    /*assigns session number*/
                    pSession = buf[bufLoc] & 0xFF;
                    bufLoc++;

                    /*reads size of address list*/
                    int count = buf[bufLoc] & 0xFF;
                    bufLoc++;

                    if((pType == PacketType.RequestNodes || pType == PacketType.RequestMavens) && count > 0) {
                        throw new IllegalArgumentException(errorAddAddress);
                    }

                    /*reads for however many address where specified in count*/
                    for (int i = 0; i < count; i++) {

                        /*will hold address of new InetSocketAddress*/
                        byte[] addressName = new byte[4];

                        /*will hold port of new InetSocketAddress*/
                        byte[] bPort = new byte[2];


                        /*reads next address*/
                        for (int j = 0; j < 4; j++, bufLoc++) {
                            addressName[i] = buf[bufLoc];
                        }

                        /*reads next port*/
                        for (int j = 0; j < 2; j++, bufLoc++) {
                            bPort[j] = buf[bufLoc];
                        }

                        /*creates address from bytes*/
                        InetAddress iAddr = InetAddress.getByAddress(addressName);

                        /*creates port from bytes*/
                        int iPort = bPort[0] << 8 | bPort[1];

                        /*creates and adds new InetSocketAddress*/
                        addAddress(new InetSocketAddress(iAddr, (iPort & 0xFFFF)));
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    throw new IOException(errorFrameOff);
                }
            } else {
                throw new IOException(errorNullObj);
            }
        } else {
            throw new IOException(errorNullObj);
        }
    }

    /**
     * Construct new packet from attributes
     * @param type type of message
     * @param error error type (if any) of message
     * @param sessionID session ID of message
     * @throws IllegalArgumentException if bad attribute value given.
     * Note that only an Answer Request may have a non-zero error.
     */
    public Packet(PacketType type, ErrorType error, int sessionID)
            throws IllegalArgumentException {
        pAddress = new HashSet<>();
        setType(type);
        setError(error);
        setSessionID(sessionID);
    }

    /**
     * Add new address
     * @param newAddress new address to add.
     * If the Packet already contains the given address,
     * the list of addresses remains unchanged.
     * @throws IllegalArgumentException if newAddress is null,
     * this type of MVN packet does not have addresses,
     * or if too many addresses
     */
    public void addAddress(InetSocketAddress newAddress)
            throws IllegalArgumentException {
        if(newAddress != null) {
            if((pType == PacketType.RequestMavens || pType == PacketType.RequestNodes)) {
                throw new IllegalArgumentException(errorAddAddress);
            }
            if(pAddress.size() < ADDRESSALLOWEDSIZE) {
                pAddress.add(newAddress);
            } else {
                throw new IllegalArgumentException(errorMaxAddresses);
            }
        } else {
            throw new IllegalArgumentException(errorNullObj);
        }
    }

    /**
     * Return encoded message in byte array
     * @return encoded message byte array
     */
    public byte[] encode() {
        /*will hold complete encoded Packet*/
        ByteBuffer encodPacket = ByteBuffer.allocate(size());

        /*encodes packet version and type*/
        byte firstByte = (byte)(pVersion << 4 | pType.getCode());
        encodPacket.put(firstByte);

        /*encodes the error type*/
        encodPacket.put((byte)pError.getCode());

        /*encodes the session number*/
        encodPacket.put((byte)pSession);

        /*encodes the amount of addresses in the list*/
        encodPacket.put((byte)pAddress.size());

        /*encodes all address from list*/
        for(InetSocketAddress addr : pAddress) {

            /*gets host name and port. removes all "." from address*/
            encodPacket.put(addr.getAddress().getAddress());
            encodPacket.put((byte)((addr.getPort() & 0xFF00) >>> 8));
            encodPacket.put((byte) (addr.getPort() & 0x00FF));
        }

        return encodPacket.array();
    }

    /**
     * Must override to satisfy contract
     * @param obj equals in class java.lang.Object
     * @return if the objects are equal
     */
    public boolean equals(Object obj) {
        if(!(obj instanceof Packet)) {
            return false;
        }
        Packet pack = (Packet)obj;
        return pSession == pack.getSessionID() &&
                pType.equals(pack.getType()) && pError.equals(pack.getError())
                && pAddress.equals(pack.getAddrList());
    }

    /**
     * Get list of addresses
     * @return list of addresses
     */
    public Set<InetSocketAddress> getAddrList() {
        return pAddress;
    }

    /**
     * Get error
     * @return error
     */
    public ErrorType getError() {
        return pError;
    }

    /**
     * Get session ID
     * @return session ID
     */
    public int getSessionID() {
        return pSession;
    }

    /**
     * Get packet type
     * @return packet type
     */
    public PacketType getType() {
        return pType;
    }

    private void setType(PacketType p) {
        if(p != null) {
            pType = p;
        } else {
            throw new IllegalArgumentException(errorNullObj);
        }
    }

    private void setError(ErrorType e) {
        if(e != null) {
            pError = e;
        } else {
            throw new IllegalArgumentException(errorNullObj);
        }

    }

    /**
     * Set session ID
     * @param sessionID new session ID
     * @throws IllegalArgumentException if sessionID invalid
     */
    public void setSessionID(int sessionID)
            throws IllegalArgumentException {
        if(sessionID >= 0 && sessionID <= 255) {
            pSession = sessionID;
        } else {
            throw new IllegalArgumentException(errorSetSession);
        }
    }

    /**
     * Must override to satisfy contract
     * @return hashCode in class java.lang.Object
     */
    public int hashCode() {

        /* a prime number to help in the hash offset*/
        int aPrime = 17;

        /* the resulting hash*/
        int hash = 1;

        /*computes hash for every attribute. if attribute is null hash it to 0*/
        hash = aPrime * hash + ((pType == null) ? 0 : pType.hashCode());
        hash = aPrime * hash + ((pError == null) ? 0 : pError.hashCode());
        hash = aPrime * hash + Integer.hashCode(pSession);

        for(InetSocketAddress a : pAddress) {
            hash = aPrime * hash + ((a == null) ? 0 : a.hashCode());
        }
        return hash;
    }


    /**
     * the number of bytes in the packet
     * @return the size of the packet
     */
    public int size() {
        return HEADERSIZE + (pAddress.size()*6);
    }

    /**
     * Human-readable string representation
     * @return toString in class java.lang.Object
     */
    public String toString() {
        StringBuilder readable = new StringBuilder("Version: " + pVersion +
                " Type: " + pType.getCmd() + " Error: " + pError.name() +
                " SessionID: " + pSession + " Count: " + pAddress.size() +
                " Address(es): ");

        for(InetSocketAddress addr : pAddress) {
            readable.append("[").append(addr.toString()).append("] ");
        }

        return readable.toString();
    }
}
