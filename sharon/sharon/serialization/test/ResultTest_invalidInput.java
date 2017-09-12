/*
 * ResultTest
 * version 0.0 created 8/29/2017
 *
 * Authors:
 * -Justin Ritter
 * -Cole Crawford
 */

package sharon.serialization.test;

import sharon.serialization.Result;
import static org.junit.Assert.*;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import sharon.serialization.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

@RunWith(Parameterized.class)
public class ResultTest_invalidInput {

    /*holds the expected message from encoding*/
    private String xpectMsg;

    /*holds the first parameter of a result object*/
    private Long para1;

    /*holds the second parameter of a result object*/
    private Long para2;

    /*holds teh third parameter of a result object*/
    private String para3;

    /*holds the expected result object*/
    private Result xpectRes;

    /**
     * constructor for the different test
     * @param a the correct message from encoding
     * @param p1 the file id
     * @param p2 the file size
     * @param p3 the file name
     */
    public ResultTest_invalidInput(String a, String p1, String p2, String p3) {
        try{
            xpectMsg = a;
            para1 = Long.parseLong(p1);
            para2 = Long.parseLong(p2);
            para3 = p3;
            xpectRes = new Result(para1, para2, para3);
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * the parameter for the test to run
     * @return an arraylist object that holds all the parameters to test
     */
    @Parameters
    public static Collection<Object[]> Badlist() {
        ArrayList<Object[]> a = new ArrayList<>();
        a.add(new Object[]{"", null, null, ""});
        a.add(new Object[]{"00010001bob\n\n", "001", "0001", "bob"});
        a.add(new Object[]{"00010001bob\n\n", "", "0001", "bob"});
        a.add(new Object[]{"00010001bob\n\n", "0001", "", "bob"});
        a.add(new Object[]{"00010001bob\n\n", "0001", "0001", ""});
        a.add(new Object[]{"00010001bob\n\n", "0001", "00001", "bob"});
        a.add(new Object[]{"00010001bob\n\n", "00001", "0001", "bob"});
        a.add(new Object[]{"00010001bob\n\n", "0001", "0001", "bob\n"});

        return a;
    }

    /**
     * test the encode function
     * @throws IOException if unable to serialize Result instance
     * @throws BadAttributeValueException if bad attribute value
     */
    @Test (expected = BadAttributeValueException.class)
    public void testEncode() throws IOException, BadAttributeValueException {
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        MessageOutput out = new MessageOutput(bytesOut);

        Result res = new Result(para1, para2, para3);
        res.encode(out);
        assertArrayEquals(xpectMsg.getBytes(), bytesOut.toByteArray());
    }

    /**
     * test if two objects are equal
     * @throws BadAttributeValueException if bad attribute value
     */
    @Test (expected = BadAttributeValueException.class)
    public void testEquals() throws BadAttributeValueException{
        Result res1 = new Result(para1, para2, para3);
        assertTrue(res1.equals(xpectRes));
    }

    /**
     * test if toString prints correctly
     * @throws BadAttributeValueException if bad attribute value
     */
    @Test (expected = BadAttributeValueException.class)
    public void toStringTest() throws BadAttributeValueException{
        Result res = new Result(para1, para2, para3);
        String expected = "fileID: " + para1 + ", fileSize: " + para2 +
                ", fileName: " + para3;

        assertEquals(expected, res.toString());
    }

    /**
     * test if the hashcode hashes properly
     * @throws BadAttributeValueException if bad attribute value
     */
    @Test (expected = BadAttributeValueException.class)
    public void testHash() throws BadAttributeValueException{
        Result res1 = new Result(para1, para2, para3);
        assertTrue(res1.equals(xpectRes));
    }

    /**
     * test the setters and getters of result
     * @throws BadAttributeValueException if bad attribute value
     */
    @Test (expected = BadAttributeValueException.class)
    public void getSetTester() throws BadAttributeValueException {
        Result res = new Result(para1, para2, para3);
        assertTrue(para1 == res.getFileID());
        assertTrue(para2 == res.getFileSize());
        assertEquals(res.getFileName(), para3);
    }
}