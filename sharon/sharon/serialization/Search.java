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

                        setSourceAddress(destAddrHolder);
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
            setSearchString(in.getline());
        } else {
            throw new BadAttributeValueException(emptyStream, attriConstruct);
        }
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
        encodedSearch.append(Integer.toUnsignedString(messagePayloadLength)).
                append(searchStr);

        if(encodedSearch.length() != (frameSize + searchStr.length())) {
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
