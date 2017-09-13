/*
 * Message
 * Version 1.0 created 9/10/2017
 *
 * Authors:
 * -Justin Ritter
 */

package sharon.serialization;

import java.io.IOException;
import static sharon.serialization.Message.searchParameters.*;

/**
 * Represents SharOn message
 */
public abstract class Message {

    /**
     * a list of all the parameters of a Message object with corresponding
     * frame sizes
     */
    public enum searchParameters {
        ID(15), TTL(1), ROUTINGSERVICE(1), SRCADDR(5),
        DESTADDR(5), PAYLOADLENGTH(2);

        private int intVal = 0;

        /**
         * creates an instance of searchParameters paired with an int value
         * to denotes the frame size of the corresponding parameter
         * @param a the frame size of the parameter
         */
        searchParameters(int a) {
            intVal = a;
        }

        /**
         * Get value of the enum
         * @return value of the enum
         */
        public int getVal() {
            return intVal;
        }
    }

    /*error messages*/
    protected static final String emptyStream = "Error: empty stream";
    protected static final String emptyAttribute = "Error: empty attribute";
    protected static final String unknownOp = "Error: unknown message type";

    /*error message for is a frame size is not the right size*/
    protected static final String frameSizeOff =
            "Error: frame-size is incorrect";

    protected static final String attriConstruct = "constructor";
    protected static final String attriID = "ID";
    protected static final String attriSrcAddr = "Source Address";
    protected static final String attriDecode = "decode";

    /*the size of a search frame minus the payload*/
    protected Integer frameSize = ID.getVal() + TTL.getVal() +
            ROUTINGSERVICE.getVal() + SRCADDR.getVal() +
            DESTADDR.getVal() + PAYLOADLENGTH.getVal();

    protected byte[] messageID;
    protected int messageTtl;
    protected RoutingService messageService;
    protected byte[] messageSrcAddr;
    protected byte[] messageDestAddr;
    protected int messagePayloadLength;
    protected Integer messageType;

    /**
     * default constructor for compilation purposes
     * should be removed eventually
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
            String messageType = in.nextOct_str();
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
//        add data checks
        messageID = id;
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
//        add data checks
        messageTtl = ttl;
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
//        add data check
        messageService = routServ;
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
//        add data check
        messageSrcAddr = srcAddr;
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
//        adda data check
        messageDestAddr = destAddr;
    }

    /**
     * Get payload Length
     * @return payload length
     */
    public abstract int getPayloadLength();

    /**
     * Sets the payload length
     * @param a payload length
     */
    public abstract void setPayloadLength(int a);

    /**
     * Useful function that adds a byte[] to a StringBuilder
     * @param a StringBuilder to add to
     * @param bytes bytes to add
     */
    protected void appendByteArr(StringBuilder a, byte[] bytes) {
        for (byte b: bytes) {
            a.append(b);
        }
    }
}
