/*
 * ResultTest
 * version 0.0 created 8/29/2017
 *
 * Authors:
 * -Justin Ritter
 * -Cole Crawford
 */

package sharon.serialization.test;

import static org.junit.Assert.*;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import sharon.serialization.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;

/**
 * test the Result class with perfect conditions
 */
@RunWith(Parameterized.class)
public class ResultTest_validInput {

    /*an Id to test*/
    private static final long testID = 2L;

    /*a size of a file to test*/
    private static final long testSize = 3L;

    /*a name of a file to test*/
    private static final String testName = "b";

    /*holds the expected message from encoding*/
    private String xpectMsg;

    /*holds the first parameter of a result object*/
    private String para1;

    /*holds the second parameter of a result object*/
    private String para2;

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
    public ResultTest_validInput(String a, String p1, String p2, String p3) {
        try{
            xpectMsg = a;
            para1 = p1;
            para2 = p2;
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
    public static Collection<Object[]> Goodlist() {
        ArrayList<Object[]> a = new ArrayList<>();
        a.add(new Object[]{"00010001Bob\n", 1L, 1L, "Bob\n"});
        a.add(new Object[]{"00010001bob\n", 1L, 1L, "Bob\n"});
        a.add(new Object[]{"00010001This Is the correct file that you should " +
                "use when trying to test this file\n", 1L, 1L,
                "This Is the correct file that you should use when trying to " +
                "test this file\n"});
        return a;
    }

    /**
     * test the encode function
     * @throws IOException if unable to serialize Result instance
     * @throws BadAttributeValueException if bad attribute value
     */
    @Test
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
    @Test
    public void testEquals() throws BadAttributeValueException{
        Result res1 = new Result(para1, para2, para3);
        assertTrue(res1.equals(xpectRes));
    }

    /**
     * test if toString prints correctly
     * @throws BadAttributeValueException if bad attribute value
     */
    @Test
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
    @Test
    public void testHash() throws BadAttributeValueException{
        Result res1 = new Result(para1, para2, para3);
        assertTrue(res1.equals(xpectRes));
    }

    /**
     * test the setters and getters of result
     * @throws BadAttributeValueException if bad attribute value
     */
    @Test
    public void getSetTester() throws BadAttributeValueException {
        Result res = new Result(para1, para2, para3);
        assertEquals(res.getFileId(), para1);
        assertEquals(res.getFileSize(), para2);
        assertEquals(res.getFileName(), para3);
    }
}
