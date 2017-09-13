package sharon.serialization.test;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import sharon.serialization.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

    public MessageTest(String xpect, String id, int ttl, String routServ,
                       String srcAddr, String destAddr) {
        xpectMsg = xpect;
        messageId = id.getBytes();
        messageTtl = ttl;
        messageRoutServ = RoutingService.valueOf(routServ);
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
        a.add(new Object[]{"01234567891234560000010002000\n\n", "123", 0,
                "DEPTHFIRSTSEARCH", "sourceAddr", "destAddr"});
        return a;
    }

    @Test
    public void en_decodeTest() throws IOException, BadAttributeValueException {
        MessageInput in = new MessageInput(new ByteArrayInputStream(xpectMsg.getBytes()));
        Message msg = Message.decode(in);
        ByteArrayOutputStream byteOut= new ByteArrayOutputStream();
        MessageOutput out = new MessageOutput(byteOut);
        msg.encode(out);
        Assert.assertEquals(xpectMsg, byteOut.toString());
    }
}
