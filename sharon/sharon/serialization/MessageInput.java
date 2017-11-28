/*
 * Result
 * version 0.0 created 8/29/2017
 *
 * Authors:
 * -Justin Ritter
*/

package sharon.serialization;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Deserialization input source for messages
 */
public class MessageInput {

    /* error messages */
    private static final String badRead = "Error: nothing to read";
    private static final String emptyMessage = "Error: empty message";

    /* a buffer for the class to hold stuff from the stream */
    private InputStreamReader messageIn;

    /**
     * Constructs a new input source from an InputStream
     * @param in byte input source
     * @throws NullPointerException if in is null
     */
    public MessageInput(InputStream in) throws NullPointerException{
        messageIn = new InputStreamReader(in, StandardCharsets.US_ASCII);
    }

    public byte nextOct_byte() throws  IOException {
        int a;
        if(hasMore()) {
            if((a = messageIn.read()) != -1) {
                return (byte)(a & 0xFF);
            } else {
                throw new IOException(badRead);
            }
        } else {
            throw new IOException(emptyMessage);
        }
    }

    /**
     * Returns the next token in the inputStream if possible
     * @return next word in stream
     * @throws IOException if I/O problem
     */
    public byte[] next4Tok() throws IOException {
        byte[] a = new byte[4];
        for(int i = 0; i < 4; i++) {
             a[i] = nextOct_byte();
        }

        return a;
    }

    /**
     * Returns the entire line in the inputStream
     * @return next line in the stream
     * @throws IOException if I/O problem
     * @throws BadAttributeValueException if parse or validation failure
     */
    public String getline() throws IOException, BadAttributeValueException {
        String line = "";
        int a;
        boolean readDone = false;

        if(hasMore()) {
            while (!readDone && (a = messageIn.read()) != -1) {
                if (a == '\n') {
                    readDone = true;
                } else {
                    line += (char) a;
                }
            }
        } else {
            line = "";
        }

        return line;
    }

    /**
     * checks if the buffer is ready to be read from
     * @return a yes or no if buffer has data to read
     * @throws IOException some I/O problem
     */
    public boolean hasMore() throws IOException {
        return messageIn.ready();
    }
}
