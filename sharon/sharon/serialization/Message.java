/*
 * Message
 * Version 1.0 created 9/10/2017
 *
 * Authors:
 * -Justin Ritter
 */

package sharon.serialization;

import java.io.IOException;

/**
 * Represents SharOn message
 */
public abstract class Message {

    private static final String emptyStream = "Error: empty stream";
    private static final String unknownOp = "Error: unknown message type";
    private static final String attriDecode = "decode";

    /*the size of a search frame minus the payload*/
    protected Integer frameSize = 29;

    protected byte[] messageID;
    protected int messageTtl;
    protected RoutingService messageService;
    protected byte[] messageSrcAddr;
    protected byte[] messageDestAddr;
    protected Integer messageType;

    /**
     * default constructor for compilation purposes
     */
    public Message() {

    }

    /**
     * Constructs base message with given values
     * @param id message id
     * @param ttl message TTL
     * @param routingService message routing service
     * @param sourceSharOnAddress message source address
     * @param destinationSharOnAddress message destination address
     * @throws BadAttributeValueException if bad parameter value
     */
    public Message(byte[] id, int ttl, RoutingService routingService,
                   byte[] sourceSharOnAddress, byte[] destinationSharOnAddress)
            throws BadAttributeValueException {
        setID(id);
        setTtl(ttl);
        setRoutingService(routingService);
        setSourceAddress(sourceSharOnAddress);
        setDestinationAddress(destinationSharOnAddress);
    }

    /**
     * Serialize message
     * @param out serialization output destination
     * @throws IOException if serialization fails
     */
    public abstract void encode(MessageOutput out) throws IOException;

    /**
     * Deserializes message from input source
     * @param in deserialization input source
     * @return a specific SharOn message resulting from deserialization
     * @throws IOException if deserialization fails
     * @throws BadAttributeValueException if bad attribute value
     */
    public static Message decode(MessageInput in)
            throws IOException, BadAttributeValueException {
        if(in.hasMore()) {
            String messageType = in.nextOct();
            switch(messageType) {
                case "1":
                    return new Search(in);
                case "2":
                    return new Response(in);
                default:
                    throw new BadAttributeValueException
                            (unknownOp, attriDecode);
            }
        } else {
            throw new BadAttributeValueException(emptyStream, attriDecode);
        }
    }

    /**
     * Get type of message
     * @return message type
     */
    public abstract int getMessageType();

    /**
     * Get message id
     * @return message id
     */
    public byte[] getID() {
        return messageID;
    }

    /**
     * Set message id
     * @param id new ID
     * @throws BadAttributeValueException if bad or null ID value
     */
    public void setID(byte[] id) throws BadAttributeValueException {

    }

    /**
     * Get message TTL
     * @return message TTL
     */
    public int getTtl() {
        return messageTtl;
    }

    /**
     * Set message TTL
     * @param ttl new TTL
     * @throws BadAttributeValueException if bad TTL value
     */
    public void setTtl(int ttl) throws BadAttributeValueException {

    }

    /**
     * Get message routing service
     * @return routing service
     */
    public RoutingService getRoutingService() {
        return messageService;
    }

    /**
     * Set message routing service
     * @param routServ new routing service
     * @throws BadAttributeValueException if null routing service value
     */
    public void setRoutingService(RoutingService routServ)
            throws BadAttributeValueException {

    }

    /**
     * Get source address
     * @return source address
     */
    public byte[] getSourceAddress() {
        return messageSrcAddr;
    }

    /**
     * Set source address
     * @param srcAddr source address
     * @throws BadAttributeValueException if bad or null address value
     */
    public void setSourceAddress(byte[] srcAddr)
            throws BadAttributeValueException{

    }

    /**
     * Get destination address
     * @return destination address
     */
    public byte[] getDestinationAddress() {
        return messageDestAddr;
    }

    /**
     * Set destination address
     * @param destAddr destination address
     * @throws BadAttributeValueException if bad or null address value
     */
    public void setDestinationAddress(byte[] destAddr)
            throws BadAttributeValueException{

    }

    protected void appendByteArr(StringBuilder a, byte[] bytes) {
        for (byte b: bytes) {
            a.append(b);
        }
    }
}
