/*
 * Response
 * version 1.0 created 9/10/2017
 *
 * Authors:
 * -Justin Ritter
 */

package sharon.serialization;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a SharOn response message
 */
public class Response extends Message {

    /*error message for if a encoded message frame is no the right size*/
    private static final String frameSizeOff = "Error: frame size is incorrect";
    private static final String attriAddResult = "addResult";

    /*declares the start of the StringBuilder class*/
    private static final Integer beginning = 0;

    private InetSocketAddress messageSocket;
    private int messageMatches;
    private List<Result> messageResList = new ArrayList<>();

    /**
     * Constructs new response with user input
     * @param id message id
     * @param ttl message TTL
     * @param routingService message routing service
     * @param sourceAddress message source address
     * @param destinationAddress message destination address
     * @param responseHost  Address and port of responding host
     * @throws BadAttributeValueException if bad or null attribute value
     */
    public Response(byte[] id, int ttl, RoutingService routingService,
            byte[] sourceAddress, byte[] destinationAddress,
            InetSocketAddress responseHost)
            throws BadAttributeValueException {

        super(id, ttl, routingService, sourceAddress, destinationAddress);
        setResponseHost(responseHost);

        frameSize += 7; //adds the response payload minus List<Result>.length
        messageType = 2; //denotes a response message
    }

    /**
     * Constructs new response with deserialization
     * @param in deserialization input source
     * @throws IOException if I/O problem, including null
     * @throws BadAttributeValueException if bad data value
     */
    public Response(MessageInput in)
            throws IOException, BadAttributeValueException {
        InetSocketAddress socketAddress;
        String hostAddr;

        setMessageFrame(in);
        setMatches(in.nextOct_int());

        StringBuilder portHolder = new StringBuilder();
        for(int i = 0; i < 2; i++) {
            String a = in.nextOct_str();
            if(!"\n".equals(a)) {
                portHolder.append(a);
            }
            else {
                throw new BadAttributeValueException
                        (frameSizeOff, attriConstruct);
            }
        }

        if(!"\n".contains(hostAddr = in.next4Tok())) {
            socketAddress = new InetSocketAddress
                    (hostAddr, Integer.parseUnsignedInt
                            (portHolder.substring(beginning)));

        } else {
            throw new BadAttributeValueException(frameSizeOff, attriConstruct);
        }

        setResponseHost(socketAddress);

        while(in.hasMore()) {
            addResult(new Result(in));
        }
    }

    /**
     * Constructs a Response frame to be sent out
     * @param out serialization output destination
     * @throws IOException if frame error or IO errors
     */
    @Override
    public void encode(MessageOutput out) throws IOException {
        StringBuilder encodedMessage = new StringBuilder();

        encodedMessage.append(getMessageType());

        appendByteArr(encodedMessage, getID());

        encodedMessage.append(getTtl()).append(getRoutingService());

        appendByteArr(encodedMessage, getSourceAddress());
        appendByteArr(encodedMessage, getDestinationAddress());

        encodedMessage.append(getPayloadLength()).
                append(getMatches()).
                append(getResponseHost().getPort()).
                append(getResponseHost().getHostString()).
                append(getResultList());

        /*if(encodedMessage.length() != frameSize) {
            throw new IOException(frameSizeOff);
        }*/
        out.writeStr(encodedMessage.substring(beginning));
    }

    /**
     * Get message type
     * @return the message type
     */
    public int getMessageType() {
        return messageType;
    }

    /**
     * Get payloadLength
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
//        add data check?
        messagePayloadLength = a;
    }

    /**
     * Get address and port of responding host
     * @return responding host address and port
     */
    public InetSocketAddress getResponseHost() {
        return messageSocket;
    }

    /**
     * Set address and port of responding host
     * @param responseHost responding host address and port
     * @throws BadAttributeValueException if bad attribute value
     */
    public void setResponseHost(InetSocketAddress responseHost)
            throws BadAttributeValueException{
//        add data check
        messageSocket = responseHost;
    }

    /**
     * Get message matches
     * @return message matches
     */
    public int getMatches() {
        return messageMatches;
    }

    /**
     * Set message matches
     * @param matches how many search matches
     */
    public void setMatches(int matches) {
//        add data check
        messageMatches = matches;
    }

    /**
     * Get list of results
     * @return result list
     */
    public List<Result> getResultList() {
        return messageResList;
    }

    /**
     * Add result to list
     * @param result new result to add to result list
     * @throws BadAttributeValueException
     * if result is null or would make result list too long to encode
     */
    public void addResult(Result result) throws BadAttributeValueException {
//        add data checks
        if(!(result == null)) {
            messageResList.add(result);
        }
        else {
            throw new BadAttributeValueException
                    (emptyAttribute, attriAddResult);
        }
    }
}
