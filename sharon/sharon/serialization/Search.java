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

    /*error message for is a frame size is not the right size*/
    private static final String frameSizeOff = "Error: frame-size is incorrect";

    /*declares the start of the StringBuilder class*/
    private static final Integer beginning = 0;

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

        frameSize += searchString.length();
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
    }

    @Override
    public void encode(MessageOutput out) throws IOException {
        StringBuilder encodedSearch = new StringBuilder();

        /*adds message type to string*/
        encodedSearch.append(messageType);

        /*adds message id to string*/
        appendByteArr(encodedSearch, messageID);

        /*adds message ttl and the Routing service to string*/
        encodedSearch.append(messageTtl).append(messageService);

        /*adds message source address to string*/
        appendByteArr(encodedSearch, messageSrcAddr);

        /*adds message destination address to string*/
        appendByteArr(encodedSearch, messageDestAddr);

        /*adds the payload length and the actual payload to string*/
        encodedSearch.append(searchStr.length()).append(searchStr);

        if(encodedSearch.length() != frameSize) {
            throw new IOException(frameSizeOff);
        }

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
}
