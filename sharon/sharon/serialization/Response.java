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
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a SharOn response message
 */
public class Response extends Message {

    /*attributes of response*/
    private static final String attriAddResult = "addResult";
    private static final String attriPayLength = "Payload Length";
    private static final String attriResponseHost = "Response Host";
    private static final String attriMatch = "Matches";

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
        setMessageFrame(in);
        setResponseFrame(in);
        messageType = 2;

    }

    public void setResponseFrame(MessageInput in) throws IOException,
            BadAttributeValueException {
        InetSocketAddress socketAddress;
        String hostAddr;

        setMatches(in.nextOct_byte());

        /*reading and assigning the port variable*/
        StringBuilder portHolder = new StringBuilder();
        for(int i = 0; i < 2; i++) {
            byte a = in.nextOct_byte();
            portHolder.append(a);
        }

        /*reading and assigning the response host variable*/
        if(!"\n".contains(hostAddr = in.next4Tok())) {
            socketAddress = new InetSocketAddress
                    (hostAddr, Integer.parseUnsignedInt
                            (portHolder.substring(beginning)));

        } else {
            throw new IOException("Frame size incorrect");
        }
        setResponseHost(socketAddress);

        /*reading all of the result objects*/
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
        /*will hold encoded Message*/
        ByteBuffer encodeMessage = ByteBuffer.allocate(frameSize + 1);

        /*adds message type to string*/
        encodeMessage.put((byte)getMessageType());

        /*adds message id to string*/
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

        encodeMessage.put((byte)getMatches());
        encodeMessage.put((byte)getResponseHost().getPort());
        encodeMessage.put(getResponseHost().getAddress().getAddress());
        out.writeStr(new String(encodeMessage.array()));
        for(Result res : getResultList()) {
            res.encode(out);
        }
    }

    /**
     * Get message type
     * @return the message type
     */
    @Override
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
    public void setPayloadLength(int a) throws BadAttributeValueException {
        if(intCheck(a)) {
            messagePayloadLength = a;
        } else {
            throw new BadAttributeValueException(unknownAttri, attriPayLength);
        }
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
        if(responseHost != null) {
            messageSocket = responseHost;
        } else {
            throw new BadAttributeValueException
                    (emptyAttribute, attriResponseHost);
        }
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
    public void setMatches(int matches) throws BadAttributeValueException {
        if(intCheck(matches)) {
            messageMatches = matches;
        } else {
            throw new BadAttributeValueException(unknownAttri, attriMatch);
        }
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
        if(!(result == null)) {
            messageResList.add(result);
            setPayloadLength(getPayloadLength()+1);
        }
        else {
            throw new BadAttributeValueException
                    (emptyAttribute, attriAddResult);
        }
    }

    public String toString() {
        String ret = super.toString() + " Matches=" + messageMatches +
                " Port=" + messageSocket.getPort() +
                " Address=" + messageSocket.getAddress();
        for(Result res : messageResList) {
            ret += res.toString();
        }
        return ret;
    }
}
