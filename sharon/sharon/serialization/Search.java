/*
 * Search
 * version 1.0 created 9/10/2017
 *
 * Authors:
 * -Justin Ritter
 */

package sharon.serialization;

import java.io.IOException;

/**
 * Represents a SharOn search message
 */
public class Search extends Message {

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
    }

    /**
     * Constructs a Search frame to be sent out
     * @param out serialization output destination
     * @throws IOException if frame error or IO errors
     */
    @Override
    public void encode(MessageOutput out) throws IOException {
        StringBuilder encodedSearch = new StringBuilder();

        /*adds message type to string*/
        encodedSearch.append(getMessageType());

        /*adds message id to string*/
        appendByteArr(encodedSearch, getID());

        /*adds message ttl and the Routing service to string*/
        encodedSearch.append(getTtl()).append(getRoutingService());

        /*adds message source address to string*/
        appendByteArr(encodedSearch, getSourceAddress());

        /*adds message destination address to string*/
        appendByteArr(encodedSearch, getDestinationAddress());

        /*adds the payload length and the actual payload to string*/
        encodedSearch.append(Integer.toUnsignedString(getPayloadLength())).
                append(getSearchString());

/*        if(encodedSearch.length() != (frameSize + getSearchString().length())) {
            throw new IOException(frameSizeOff);
        }*/

        /*writes out the encoded string*/
        out.writeStr(encodedSearch.substring(beginning));
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
        //add data check
        searchStr = searchString;
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
     */
    @Override
    public void setPayloadLength(int a) {
//        add data check
        messagePayloadLength = a;
    }
}
