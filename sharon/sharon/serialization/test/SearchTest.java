package sharon.serialization.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import sharon.serialization.BadAttributeValueException;
import sharon.serialization.RoutingService;
import sharon.serialization.Search;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Created by Justin Ritter on 9/12/2017.
 */
@RunWith(Parameterized.class)
public class SearchTest {

    private byte[] messageId;
    private byte[] messageSrcAddr;
    private byte[] messageDestAddr;
    private int messageTtl;
    private RoutingService messageRoutServ;
    private String messagePayload;

    public SearchTest(String id, int ttl, String routServ,
                      String srcAddr, String destAddr, String payload) {
        messageId = id.getBytes();
        messageTtl = ttl;
        messageRoutServ = RoutingService.valueOf(routServ);
        messageSrcAddr = srcAddr.getBytes();
        messageDestAddr = destAddr.getBytes();
        messagePayload = payload;
    }

    /**
     * the parameter for the test to run
     * @return an arraylist object that holds all the parameters to test
     */
    @Parameterized.Parameters
    public static Collection<Object[]> list() {
        ArrayList<Object[]> a = new ArrayList<>();
        a.add(new Object[]{"0", 0, "DEPTHFIRSTSEARCH", "sourceAddr",
                "destAddr", "lookHere"});
        return a;
    }

    @Test
    public void getSetSearchString() throws BadAttributeValueException {
        Search a = new Search(messageId, messageTtl, messageRoutServ,
                messageSrcAddr, messageDestAddr, messagePayload);
        assertEquals(messagePayload, a.getSearchString());
    }
}
