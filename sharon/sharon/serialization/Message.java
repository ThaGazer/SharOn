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
    protected static final String unknownAttri = "Error: unknown attribute";
    protected static final String dataCheck = "Error: data check";
    protected static final String frameSizeOff =
            "Error: frame-size is incorrect";

    /*data checks points*/
    protected static final String alphaNum = "[\\w]+";
    protected static final String numerics = "[\\d]+";

    /*different attribute messages to throw*/
    protected static final String attriConstruct = "constructor";
    protected static final String attriID = "ID";
    protected static final String attriTtl = "ttl";
    protected static final String attriRoutServ = "RoutingService";
    protected static final String attriSrcAddr = "Source Address";
    protected static final String attriDestAddr = "Destination Address";
    protected static final String attriDecode = "decode";

    /*the size of a search frame minus the payload*/
    protected Integer frameSize = ID.getVal() + TTL.getVal() +
            ROUTINGSERVICE.getVal() + SRCADDR.getVal() +
            DESTADDR.getVal() + PAYLOADLENGTH.getVal();

    /*declares the starting position of the StringBuilder class*/
    protected static final Integer beginning = 0;

    protected byte[] messageID;
    protected int messageTtl;
    protected RoutingService messageService;
    protected byte[] messageSrcAddr;
    protected byte[] messageDestAddr;
    protected int messagePayloadLength;
    protected Integer messageType;

    /**
     * default constructor for compiling
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
                //these are the wrong checks for sure
                //right check?
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
        if(byteCheck(id, attriID)) {
            messageID = id;
        } else {
            throw new BadAttributeValueException(attriID, dataCheck);
        }
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
        if(intCheck(ttl)) {
            messageTtl = ttl;
        } else {
            throw new BadAttributeValueException(attriTtl, dataCheck);
        }
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
        if(routServ != null) {
            for(RoutingService a : RoutingService.values()) {
                if(a.equals(routServ)) {
                    messageService = routServ;
                    return;
                }
            }
            throw new BadAttributeValueException(attriRoutServ, unknownAttri);
        } else {
            throw new BadAttributeValueException(attriRoutServ, emptyAttribute);
        }

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
            throws BadAttributeValueException {
        if(byteCheck(srcAddr, attriSrcAddr)) {
                messageSrcAddr = srcAddr;
        } else {
            throw new BadAttributeValueException(attriSrcAddr, dataCheck);
        }
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
        if(byteCheck(destAddr, attriDestAddr)) {
            messageDestAddr = destAddr;
        }
        else {
            throw new BadAttributeValueException(dataCheck, attriDestAddr);
        }
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
    public abstract void setPayloadLength(int a) throws BadAttributeValueException;

    /**
     * Does a SharOn protocol check for byte arrays
     * @param a byte array to check
     * @return if the check pass or fails
     */
    public boolean byteCheck(byte[] a, String attri)
            throws BadAttributeValueException {
        int dataSize;

        switch(attri) {
            case attriID:
                dataSize = searchParameters.ID.getVal();
                break;
            case attriSrcAddr:
                dataSize = searchParameters.SRCADDR.getVal();
                break;
            case attriDestAddr:
                dataSize = searchParameters.DESTADDR.getVal();
                break;
            default:
                dataSize = 0;
        }

        if(a.length == dataSize) {
            String dataCheck = "";
            for(byte bytes: a) {
                dataCheck += bytes;
            }

            return dataCheck.matches(alphaNum);
        } else {
            return false;
        }
    }

    /**
     * Does a SharOn protocol check for ints
     * @param a integer to check
     * @return if the check pass or fails
     */
    public boolean intCheck(Integer a) {
        if(a != null) {
            String intData = String.valueOf(a);
            if(intData.matches(numerics)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

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

    /**
     * Constructs the Message packet frame
     * @param in intput stream
     * @throws IOException if IO problem or null
     * @throws BadAttributeValueException if bad or null attribute value
     */
    protected void setMessageFrame(MessageInput in)
            throws IOException, BadAttributeValueException {
        if(in.hasMore()) {
            int paraSize;

            for(searchParameters para : searchParameters.values()) {
                switch(para) {
                    case ID:
                        paraSize = searchParameters.ID.getVal();
                        byte[] idHolder = new byte[paraSize];

                        for(int i = 0; i < paraSize; i++) {
                            String a = in.nextOct_str();

                            if("\n".equals(a)) {
                                throw new BadAttributeValueException
                                        (frameSizeOff, attriID);
                            }
                            idHolder[i] = Byte.parseByte(a);
                        }

                        setID(idHolder);
                        break;
                    case TTL:
                        setTtl(in.nextOct_int());
                        break;
                    case ROUTINGSERVICE:
                        setRoutingService(RoutingService.getRoutingService
                                (in.nextOct_int()));
                        break;
                    case SRCADDR:
                        paraSize = searchParameters.SRCADDR.getVal();
                        byte[] srcAddrHolder = new byte[paraSize];

                        for(int i = 0; i < paraSize; i++) {
                            String a = in.nextOct_str();

                            if("\n".equals(a)) {
                                throw new BadAttributeValueException
                                        (frameSizeOff, attriSrcAddr);
                            }
                            srcAddrHolder[i] = Byte.parseByte(a);
                        }

                        setSourceAddress(srcAddrHolder);
                        break;
                    case DESTADDR:
                        paraSize = searchParameters.DESTADDR.getVal();
                        byte[] destAddrHolder = new byte[paraSize];

                        for(int i = 0; i < paraSize; i++) {
                            String a = in.nextOct_str();
                            destAddrHolder[i] = Byte.parseByte(a);
                        }

                        setDestinationAddress(destAddrHolder);
                        break;
                    case PAYLOADLENGTH:
                        paraSize = searchParameters.PAYLOADLENGTH.getVal();
                        StringBuilder payloadHolder = new StringBuilder();

                        for(int i = 0; i < paraSize; i++) {
                            String a = in.nextOct_str();
                            payloadHolder.append(a);
                        }

                        setPayloadLength(Integer.parseUnsignedInt
                                (payloadHolder.substring(beginning)));
                        break;
                    default:
                        throw new BadAttributeValueException
                                (unknownOp, attriConstruct);
                }
            }
        } else {
            throw new BadAttributeValueException(emptyStream, attriConstruct);
        }
    }
}
