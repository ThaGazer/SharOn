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
        messageIn = new InputStreamReader(in);
    }

    public String nextOct() throws IOException, BadAttributeValueException {
        String tok;
        int a;

        if (hasMore()) {
            if((a = messageIn.read()) != -1) {
                tok = String.valueOf(a);
            } else {
                throw new IOException(badRead);
            }
        } else {
            throw new IOException(emptyMessage);
        }

        return tok;
    }

    /**
     * Returns the next token in the inputStream if possible
     * @return next word in stream
     * @throws IOException if I/O problem
     * @throws BadAttributeValueException if parse or validation failure
     */
    public String next4Tok() throws IOException, BadAttributeValueException {
        String token = "";
        int a;
        int readDone = 0;
        while (readDone != 4 && (a = messageIn.read()) != -1) {
            if (a == '\n') {
                readDone = 4;
            }else {
//                Checkout StringBuilder.append();
                token += (char)a;
                readDone++;
            }
        }
        if (token.isEmpty()) {
            throw new BadAttributeValueException(emptyMessage, "MessageInput");
        }
        return token;
    }

    /**
     * Returns the entire line in the inputStream
     * @return next line in the stream
     * @throws IOException       if I/O problem
     * @throws BadAttributeValueException if parse or validation failure
     */
    public String getline() throws IOException, BadAttributeValueException {
        String line = "";
        int a = -1;
        boolean readDone = false;

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

        if (a == -1) {
            throw new BadAttributeValueException(badFrame, "MessageInput");
        }

        if (line.isEmpty()) {
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
