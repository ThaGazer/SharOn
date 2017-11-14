/*
 * Search
 * version 1.0 created 9/10/2017
 *
 * Authors:
 * -Justin Ritter
 */

package sharon.serialization;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Represents a SharOn search message
 */
public class Search extends Message {

    /*attributes of a search object*/
    protected static final String attriPayloadSize = "Payload size";
    protected static final String attriSearchString = "Search String";


    private String searchStr;

    /**
     * Constructs new search with user input
     * @param id message id
     * @param ttl message TTL
     * @param routingService message routing service
     * @param sourceAddress message source address
     * @param destinationAddress message destination address
     * @param searchString search string
     * @throws BadAttributeValueException if bad or null data value
     */
    public Search(byte[] id, int ttl, RoutingService routingService,
                  byte[] sourceAddress, byte[] destinationAddress,
                  String searchString) throws BadAttributeValueException {

        super(id, ttl, routingService, sourceAddress, destinationAddress);
        setSearchString(searchString);

        messagePayloadLength = searchString.length();
        frameSize += messagePayloadLength;
        messageType = 1;
    }

    /**
     * Constructs a new search with deserialization
     * @param in deserialization input source
     * @throws IOException if I/O problem, including null
     * @throws BadAttributeValueException if bad data value
     */
    public Search(MessageInput in)
            throws IOException, BadAttributeValueException {
        setMessageFrame(in);
        setSearchString(in.getline());
        messageType = 1;
    }

    /**
     * Constructs a Search frame to be sent out
     * @param out serialization output destination
     * @throws IOException if frame error or IO errors
     */
    @Override
    public void encode(MessageOutput out) throws IOException {
        /*will hold encoded Message*/
        ByteBuffer encodeMessage = ByteBuffer.allocate(frameSize + 1);

        /*adds message type to string*/
        encodeMessage.put((byte)getMessageType());

        /*adds message id to string*/
        System.out.println(Arrays.toString(getID()));
        encodeMessage.put(getID());

        /*adds message ttl*/
        encodeMessage.put((byte)getTtl());

        /*adds the Routing service to string*/
        encodeMessage.put((byte)getRoutingService().getServiceCode());

        /*adds message source address to string*/
        encodeMessage.put(getSourceAddress());

        /*adds message destination address to string*/
        encodeMessage.put(getDestinationAddress());

        /*adds two bytes of the payload length to the encoded message*/
        encodeMessage.put((byte)(getPayloadLength() >>> 8));
        encodeMessage.put((byte)getPayloadLength());

        /*adds the actual search string to the encoded string*/
        encodeMessage.put(getSearchString().getBytes());

        /*closes the frame with \n\n*/
        encodeMessage.put("\n".getBytes());

        System.out.println(Arrays.toString(encodeMessage.array()));
        /*writes out the encoded string*/
        out.writeStr(new String(encodeMessage.array()));
    }

    @Override
    public int getMessageType() {
        return messageType;
    }

    /**
     * Get search string
     * @return search string
     */
    public String getSearchString() {
        return searchStr;
    }

    /**
     * Set search string
     * @param searchString new search string
     * @throws BadAttributeValueException if bad search value
     */
    public void setSearchString(String searchString)
            throws BadAttributeValueException{
        if(!" ".equals(searchString)) {
            searchStr = searchString;
        } else {
            throw new BadAttributeValueException
                    (emptyAttribute, attriSearchString);
        }
    }

    /**
     * Get payload length
     * @return payload length
     */
    @Override
    public int getPayloadLength() {
        return messagePayloadLength;
    }

    /**
     * Set payload length
     * @param a payload length
     * @throws BadAttributeValueException if null or bad payload size
     */
    @Override
    public void setPayloadLength(int a) throws BadAttributeValueException {
        if(intCheck(a)) {
            messagePayloadLength = a;
        } else {
            throw new BadAttributeValueException(dataCheck, attriPayloadSize);
        }
    }

    public String toString() {
        return super.toString() + " Payload=" + getSearchString();
    }
}
