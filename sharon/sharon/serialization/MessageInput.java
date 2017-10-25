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
    private static final String badFrame = "Error: incorrect frame";
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

    /**
     * Grabs the next byte in the Stream and returns it as a String
     * @return the next byte as a String
     * @throws IOException if IO problem or null stream
     */
    public String nextOct_str() throws IOException {
        String tok;
        int a;

        if (hasMore()) {
            if((a = messageIn.read()) != -1) {
                byte[] b = new byte[]{(byte)(a)};
                tok = new String(b, StandardCharsets.US_ASCII);
            } else {
                throw new IOException(badRead);
            }
        } else {
            throw new IOException(emptyMessage);
        }

        return tok;
    }

    /**
     * Grabs the next byte in the stream and returns it as a int
     * @return the next byte as a int
     * @throws IOException if IO problem or null stream
     */
    public int nextOct_int() throws IOException {
        int a;

        if (hasMore()) {
            if((a = messageIn.read()) != -1) {
                byte[] b = new byte[]{(byte)(a)};
                String tok = new String(b, StandardCharsets.US_ASCII);
                a = Integer.parseInt(tok);

                return a;
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
    public String next4Tok() throws IOException {
        String token = "";

        for(int i = 0; i < 4; i++) {
            token += nextOct_str();
        }

        return token;
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
                    if (messageIn.read() != '\n') {
                        throw new BadAttributeValueException
                                (badFrame, "MessageInput");
                    }
                    readDone = true;
                } else {
//                Checkout StringBuilder.append();
                    line += (char) a;
                }
            }
        } else {
            throw new IOException("Empty input");
        }

        if(line.isEmpty()) {
            throw new BadAttributeValueException(emptyMessage, "MessageInput");
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
