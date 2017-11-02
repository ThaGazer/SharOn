/*
 * PacketTest
 * Version 1.0 created 10/25/2017
 *
 * Authors:
 * -Justin Ritter
 */
package mvn.serialization.test;

import mvn.serialization.Packet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;


@RunWith(Parameterized.class)
public class PacketTest {

    /*saves buffer use to create Packet*/
    byte[] testBuf;

    /*the packet created using the buffer*/
    Packet testerP;

    /**
     * constructs a new Packet using a list of parameter buffers
     * @param bufferIn the stream of data that should decoded into a Packet
     * @throws IOException reading issues
     * @throws IllegalAccessException incorrect Packet formatting
     */
    public PacketTest(byte[] bufferIn)
            throws IOException, IllegalAccessException {
        testBuf = bufferIn;
        testerP = new Packet(bufferIn);
    }

    /**
     * the parameter for the test to run
     * @return an arraylist object that holds all the parameters to test
     */
    @Parameterized.Parameters
    public static Collection<Object[]> list() {
        ArrayList<Object[]> a = new ArrayList<>();
        a.add(new Object[]{new byte[]{(byte)0x40, (byte)0x00, (byte)0x24,
                (byte)0x01, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                (byte)0xFF, (byte)0x01}});
        a.add(new Object[]{new byte[]{(byte)0x41, (byte)0x00, (byte)0x25,
                (byte)0x02, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                (byte)0x00, (byte)0x01, (byte)0x00 , (byte)0x00, (byte)0x00,
                (byte)0x01, (byte)0x00, (byte)0x01}});
        a.add(new Object[]{null});
        return a;
    }

    /**
     * test if two Packets created from the same buffer equate to each other
     */
    @Test
    public void equalsTest() {
        Packet t1 = testerP;
        Assert.assertEquals(testerP, t1);
    }

    /**
     * test if the constructor assigns the variables
     * of the Packet class correctly
     */
    @Test
    public void bufConstructTest() {
        Assert.assertTrue(testerP.getType().getCode() ==
                (testBuf[0] & 0xF));
        Assert.assertTrue(testerP.getError().getCode() ==
                (testBuf[1] & 0xFF));
        Assert.assertTrue(testerP.getSessionID() ==
                (testBuf[2] & 0xFF));
        Assert.assertTrue(testerP.getAddrList().size() ==
                (testBuf[3] & 0xFF));
    }

    /**
     * test if you can successfully add a new address to the set
     */
    @Test
    public void addAddressTest() {
        InetSocketAddress newr =
                new InetSocketAddress("nextHost", 1234);
        int preSize = testerP.getAddrList().size();
        testerP.addAddress(newr);
        Assert.assertTrue
                ((preSize + 1) == testerP.getAddrList().size());
    }

    /**
     * test if encoding a packet will return the
     * same byte array that created it
     * @throws IOException if reading failed
     * @throws IllegalAccessException inccorect Packet parameters
     */
    @Test
    public void encoderTest() throws IOException, IllegalAccessException {
        Packet encodedPacket = new Packet(testBuf);
        Assert.assertArrayEquals(testBuf, encodedPacket.encode());
    }
}