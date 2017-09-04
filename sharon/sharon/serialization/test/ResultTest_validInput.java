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
    private long para1;

    /*holds the second parameter of a result object*/
    private long para2;

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
    public ResultTest_validInput(String a, long p1, long p2, String p3) {
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
    public static Collection<Object[]> list() {
        ArrayList<Object[]> a = new ArrayList<>();
        a.add(new Object[]{"00010001bob\r\n", 1L, 1L, "Bob"});
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

        assertEquals(xpectMsg.getBytes(), bytesOut.toByteArray());
//        assertEquals(xpectMsg, bytesOut.toString());
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
        String expected = "fileID: 1, fileSize: 1\", fileName: a";

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
     * test the getter for fileId
     * @throws BadAttributeValueException if bad attribute value
     */
    @Test
    public void getFileIdTest() throws BadAttributeValueException{
        Result res = new Result(para1, para2, para3);
        assertEquals(res.getFileId(), para1);
    }

    /**
     * test the getter for fileName
     * @throws BadAttributeValueException if bad attribute value
     */
    @Test
    public void getFileNameTest() throws BadAttributeValueException {
        Result res = new Result(para1, para2, para3);
        assertEquals(res.getFileName(), para3);
    }

    /**
     * test the getter for fileSize
     * @throws BadAttributeValueException if bad attribute value
     */
    @Test
    public void getFileSizeTest() throws BadAttributeValueException {
        Result res = new Result(para1, para2, para3);
        assertEquals(res.getFileSize(), para2);
    }

    /**
     * test the setter for fileId
     * @throws BadAttributeValueException if bad attribute value
     */
    @Test
    public void setFileIdTest() throws BadAttributeValueException {
        Result res = new Result(para1, para2, para3);
        res.setFileId(testID);
        assertEquals(res.getFileId(), testID);
    }

    /**
     * test the setter for fileName
     * @throws BadAttributeValueException if bad attribute value
     */
    @Test
    public void setFileNameTest() throws BadAttributeValueException {
        Result res = new Result(para1, para2, para3);
        res.setFileName(testName);
        assertEquals(res.getFileName(), testName);
    }

    /**
     * test the setter for fileSize
     * @throws BadAttributeValueException if bad attribute value
     */
    @Test
    public void setFileSizeTest() throws BadAttributeValueException {
        Result res = new Result(para1, para2, para3);
        res.setFileSize(testSize);
        assertEquals(res.getFileSize(), testSize);
    }
}
