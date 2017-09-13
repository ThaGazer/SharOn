/*
 * Result
 * version 0.0 created 8/29/2017
 *
 * Authors:
 * -Justin Ritter
*/

package sharon.serialization;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Serialization output source for messages
 */
public class MessageOutput {

    private OutputStreamWriter messageOut;

    /**
     * Constructs a new output source from an OutputStream
     * @param out byte output sink
     */
    public MessageOutput(OutputStream out) throws NullPointerException {
        if(out != null) {
            messageOut = new OutputStreamWriter(out);
        } else {
            throw new NullPointerException();
        }
    }

    /**
     * Writes the string out to the OutputStreamWriter
     *
     * @param strOut string to write out
     * @throws IOException if I/O problem
     */
    public void writeStr(String strOut) throws IOException {
        messageOut.write(strOut, 0, strOut.length());
        messageOut.flush();
    }
}
