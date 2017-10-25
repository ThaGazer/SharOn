package mvn.serialization;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

/**
 * Project: SharOn
 * Created by Justin Ritter on 10/24/2017.
 */
public class Packet {
    private static final String version = "1.1";

    /**
     * Construct new packet from byte array
     * @param buf buffer containing encoded packet
     * @throws IOException if byte array too long/short or buf is null
     * @throws IllegalAccessException if bad attribute value
     */
    Packet(byte[] buf) throws IOException, IllegalAccessException {

    }

    /**
     * Construct new packet from attributes
     * @param type type of message
     * @param error error type (if any) of message
     * @param sessionID session ID of message
     * @throws IllegalAccessException if bad attribute value given.
     * Note that only an Answer Request may have a non-zero error.
     */
    Packet(PacketType type, ErrorType error, int sessionID) throws IllegalAccessException {

    }

    /**
     * Add new address
     * @param newAddress new address to add.
     * If the Packet already contains the given address, the list of addresses remains unchanged.
     * @throws IllegalArgumentException if newAddress is null, this type of MVN packet does not have addresses,
     * or if too many addresses
     */
    public void addAddress(InetSocketAddress newAddress) throws IllegalArgumentException {

    }

    /**
     * Return encoded message in byte array
     * @return encoded message byte array
     */
    public byte[] encode() {
        return "".getBytes();
    }

    /**
     * Must override to satisfy contract
     * @param obj equals in class java.lang.Object
     * @return if the objects are equal
     */
    public boolean equals(Object obj) {
        return true;
    }

    /**
     * Get list of addresses
     * @return list of addresses
     */
    public Set<InetSocketAddress> getAddrList() {
        return new HashSet<>();
    }

    /**
     * Get error
     * @return error
     */
    public ErrorType getError() {
        return ErrorType.NONE;
    }

    /**
     * Get session ID
     * @return session ID
     */
    public int getSessionID() {
        return 0;
    }

    /**
     * Get packet type
     * @return packet type
     */
    public PacketType getType() {
        return PacketType.CLEARCACHE;
    }

    /**
     * Must override to satisfy contract
     * @return hashCode in class java.lang.Object
     */
    public int hashCode() {
        return 0;
    }

    /**
     * Set session ID
     * @param sessionID new session ID
     * @throws IllegalArgumentException if sessionID invalid
     */
    public void setSessionID(int sessionID) throws IllegalArgumentException {

    }

    /**
     * Human-readable string representation
     * @return toString in class java.lang.Object
     */
    public String toString() {
        return "";
    }
}
