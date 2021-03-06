/*
 * ResultTest
 * version 0.0 created 8/29/2017
 *
 * Authors:
 * -Justin Ritter
 * -Cole Crawford
 */

package sharon.serialization.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import sharon.serialization.BadAttributeValueException;
import sharon.serialization.MessageInput;
import sharon.serialization.MessageOutput;
import sharon.serialization.Result;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

/**
 * Runs a bounds check on the Result class
 */
public class ResultBoundsCheck {
    String expctStr = "00010001656565\n\n";
    Result res;
    int id = 1;
    int size = 1;
    String name = "bob";

    public ResultBoundsCheck() {
    }

    @Test (expected = BadAttributeValueException.class)
    public void testNullConstruct() throws BadAttributeValueException {
        id = 0;
        size = 0;
        name = "";
        res = new Result(id, size, name);
    }

    @Test (expected = BadAttributeValueException.class)
    public void testNullIDSet() throws BadAttributeValueException {
        res = new Result(id, size, name);
        res.setFileID(0);
    }

    @Test (expected = BadAttributeValueException.class)
    public void testNullSizeSet() throws BadAttributeValueException {
        res = new Result(id, size, name);
        res.setFileSize(0);
    }

    @Test (expected = BadAttributeValueException.class)
    public void testNullNameSet() throws BadAttributeValueException {
        res = new Result(id, size, name);
        res.setFileName("");
    }

    @Test
    public void testDoubleDecode()
            throws IOException, BadAttributeValueException {
        String dblStr = expctStr + expctStr;
        ByteArrayInputStream bArr = new ByteArrayInputStream
                (dblStr.getBytes(StandardCharsets.US_ASCII));

        Result res1 = new Result(new MessageInput(bArr));
        Result res2 = new Result(new MessageInput(bArr));

        assertEquals(res1, res2);
    }

    @Test
    public void testDoubleEncode()
            throws IOException, BadAttributeValueException {
        byte[] dblStr = (expctStr + expctStr).getBytes
                (StandardCharsets.US_ASCII);

        ByteArrayInputStream bArr = new ByteArrayInputStream
                (expctStr.getBytes(StandardCharsets.US_ASCII));
        Result res = new Result(new MessageInput(bArr));

        ByteArrayOutputStream bOutArr = new ByteArrayOutputStream();
        MessageOutput msg = new MessageOutput(bOutArr);
        res.encode(msg);
        res.encode(msg);

        assertArrayEquals(dblStr, bOutArr.toByteArray());
    }
}