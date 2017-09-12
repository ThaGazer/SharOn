package sharon.serialization.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import sharon.serialization.BadAttributeValueException;
import sharon.serialization.Response;
import sharon.serialization.Result;
import sharon.serialization.RoutingService;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Created by Justin Ritter on 9/12/2017.
 */

@RunWith(Parameterized.class)
public class ResponseTest {
    private byte[] messageId;
    private byte[] messageSrcAddr;
    private byte[] messageDestAddr;
    private int messageTtl;
    private RoutingService messageRoutServ;
    private InetSocketAddress socket;

    public ResponseTest(String id, int ttl, String routServ,
                       String srcAddr, String destAddr, InetSocketAddress sock) {
        messageId = id.getBytes();
        messageTtl = ttl;
        messageRoutServ = RoutingService.valueOf(routServ);
        messageSrcAddr = srcAddr.getBytes();
        messageDestAddr = destAddr.getBytes();
        socket = sock;
    }

    /**
     * the parameter for the test to run
     * @return an arraylist object that holds all the parameters to test
     */
    @Parameterized.Parameters
    public static Collection<Object[]> list() {
        ArrayList<Object[]> a = new ArrayList<>();
        a.add(new Object[]{"0", 0, "DEPTHFIRSTSEARCH", "sourceAddr",
                "destAddr", "8080"});
        return a;
    }

    @Test
    public void getSetResponseHostTest() throws BadAttributeValueException {
        Response res = new Response(messageId, messageTtl, messageRoutServ,
                messageSrcAddr, messageDestAddr, socket);
        assertEquals(socket, res.getResponseHost());
    }

    @Test
    public void getSetListTest() throws BadAttributeValueException {
        Response b = new Response(messageId, messageTtl, messageRoutServ,
                messageSrcAddr, messageDestAddr, socket);
        List<Result> a = new ArrayList<>();
        assertEquals(a, b.getResultList());
    }
}

