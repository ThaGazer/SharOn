package sharon.serialization.test;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import sharon.serialization.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Created by Justin Ritter on 9/12/2017.
 */
@RunWith(Parameterized.class)
public class MessageTest {

    private byte[] messageId;
    private byte[] messageSrcAddr;
    private byte[] messageDestAddr;
    private int messageTtl;
    private RoutingService messageRoutServ;
    private String xpectMsg;

    public MessageTest(String xpect, String id, int ttl, int routServ,
                       String srcAddr, String destAddr)
            throws BadAttributeValueException {
        xpectMsg = xpect;
        messageId = id.getBytes();
        messageTtl = ttl;
        messageRoutServ = RoutingService.getRoutingService(routServ);
        messageSrcAddr = srcAddr.getBytes();
        messageDestAddr = destAddr.getBytes();
    }

    /**
     * the parameter for the test to run
     * @return an arraylist object that holds all the parameters to test
     */
    @Parameterized.Parameters
    public static Collection<Object[]> list() {
        ArrayList<Object[]> a = new ArrayList<>();
        a.add(new Object[]{"1000000000000000110000011111011\n\n",
         "000000000000000", 1, 1, "00000", "11111"});
        a.add(new Object[]{"200000000000000011000001111101180111100010001656667",
                "0000000000000000", 1, 1, "00000", "11111"});
        return a;
    }

    @Test
    public void en_decodeTest() throws IOException, BadAttributeValueException {
        MessageInput in = new MessageInput(new ByteArrayInputStream
                (xpectMsg.getBytes(StandardCharsets.US_ASCII)));
        Message msg = Message.decode(in);
        ByteArrayOutputStream byteOut= new ByteArrayOutputStream();
        MessageOutput out = new MessageOutput(byteOut);
        msg.encode(out);
        Assert.assertEquals(xpectMsg, byteOut.toString());
    }
}
