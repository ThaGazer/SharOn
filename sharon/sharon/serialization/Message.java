/*
 * Message
 * Version 1.0 created 9/10/2017
 *
 * Authors:
 * -Justin Ritter
 */

package sharon.serialization;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import static sharon.serialization.Message.messageParameters.*;

/**
 * Represents SharOn message
 */
public abstract class Message {

    /**
     * a list of all the parameters of a Message object with corresponding
     * frame sizes
     */
    public enum messageParameters {
        ID(15), TTL(1), ROUTINGSERVICE(1), SRCADDR(5),
        DESTADDR(5), PAYLOADLENGTH(2);

        private int intVal = 0;

        /**
         * creates an instance of searchParameters paired with an int value
         * to denotes the frame size of the corresponding parameter
         * @param a the frame size of the parameter
         */
        messageParameters(int a) {
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
    protected static final String attriPayload = "payloadLength";

    /*the size of a message frame minus the actual payload*/
    protected Integer frameSize = 1 + ID.getVal() + TTL.getVal() +
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
     * default constructor for compiling
     */
    public Message() {}

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
        setPayloadLength(0);
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
            byte messageType = in.nextOct_byte();
            switch(messageType) {
                case 0x01:
                    return new Search(in);
                case 0x02:
                    return new Response(in);
                default:
                    throw new BadAttributeValueException
                            (unknownOp, attriDecode);
            }
        } else {
            in.getline();
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
    /*throw .clone()*/
    public byte[] getID() {
        return messageID.clone();
    }

    /**
     * Set message id
     * @param id new ID
     * @throws BadAttributeValueException if bad or null ID value
     */
    public void setID(byte[] id) throws BadAttributeValueException {
        if(byteCheck(id, attriID)) {
            messageID = id.clone();
        } else {
            throw new BadAttributeValueException(attriID,
                    String.valueOf(ByteBuffer.wrap(id).
                            order(ByteOrder.LITTLE_ENDIAN).getInt()));
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
            throw new BadAttributeValueException
                    (attriTtl, Integer.toString(ttl));
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
            messageService = routServ;
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
                messageSrcAddr = srcAddr.clone();
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
            messageDestAddr = destAddr.clone();
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
    public abstract void setPayloadLength(int a)
            throws BadAttributeValueException;

    /**
     * Does a SharOn protocol check for byte arrays
     * @param a byte array to check
     * @return if the check pass or fails
     */
    public boolean byteCheck(byte[] a, String attri)
            throws BadAttributeValueException {
        int dataSize;

        if(a != null) {
            switch(attri) {
                case attriID:
                    dataSize = messageParameters.ID.getVal();
                    break;
                case attriSrcAddr:
                    dataSize = messageParameters.SRCADDR.getVal();
                    break;
                case attriDestAddr:
                    dataSize = messageParameters.DESTADDR.getVal();
                    break;
                default:
                    dataSize = 0;
            }

            return a.length == dataSize;
        }
        return false;
    }

    /**
     * Does a SharOn protocol check for ints
     * @param a integer to check
     * @return if the check pass or fails
     */
    protected boolean intCheck(Integer a) {
        if(a != null) {
            String intData = String.valueOf(a);
            return intData.matches(numerics);
        } else {
            return false;
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

            for(messageParameters para : messageParameters.values()) {
                switch(para) {
                    case ID:
                        paraSize = messageParameters.ID.getVal();
                        byte[] idHolder = new byte[paraSize];

                        for(int i = 0; i < paraSize; i++) {
                            byte a = in.nextOct_byte();
                            if('\n' == a) {
                                throw new BadAttributeValueException
                                        (frameSizeOff, attriID);
                            }
                            idHolder[i] = a;
                        }
                        setID(idHolder);
                        break;
                    case TTL:
                        byte ttl = in.nextOct_byte();
                        setTtl(ttl);
                        break;
                    case ROUTINGSERVICE:
                        byte rout = in.nextOct_byte();
                        setRoutingService(RoutingService.getRoutingService
                                (rout));
                        break;
                    case SRCADDR:
                        paraSize = messageParameters.SRCADDR.getVal();
                        byte[] srcAddrHolder = new byte[paraSize];

                        for(int i = 0; i < paraSize; i++) {
                            byte a = in.nextOct_byte();
                            if('\n' == a) {
                                throw new BadAttributeValueException
                                        (frameSizeOff, attriSrcAddr);
                            }
                            srcAddrHolder[i] = a;
                        }
                        setSourceAddress(srcAddrHolder);
                        break;
                    case DESTADDR:
                        paraSize = messageParameters.DESTADDR.getVal();
                        byte[] destAddrHolder = new byte[paraSize];

                        for(int i = 0; i < paraSize; i++) {
                            byte a = in.nextOct_byte();
                            if('\n' == a) {
                                throw new BadAttributeValueException
                                        (frameSizeOff, attriDestAddr);
                            }
                            destAddrHolder[i] = a;
                        }
                        setDestinationAddress(destAddrHolder);
                        break;
                    case PAYLOADLENGTH:
                        paraSize = messageParameters.PAYLOADLENGTH.getVal();
                        byte[] payloadHolder = new byte[paraSize];

                        for(int i = 0; i < paraSize; i++) {
                            byte a = in.nextOct_byte();
                            if('\n' == a) {
                                throw new BadAttributeValueException
                                        (frameSizeOff, attriPayload);
                            }
                            payloadHolder[i] = a;
                        }

                        int payload = (payloadHolder[0] << 8) | payloadHolder[1];
                        setPayloadLength(payload);
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

    public String toString() {
        return "Type=" + getMessageType() + " ID=" + Arrays.toString(getID()) +
                " TTL=" + getTtl() + " Routing=" +
                getRoutingService().getServiceCode() + " Source=" +
                new String(getSourceAddress()) + " Destination=" +
                new String(getDestinationAddress()) + " Length=" +
                getPayloadLength();
    }
}
