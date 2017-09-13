/*
 * Result
 * version 0.0 created 8/29/2017
 *
 * Authors:
 * -Justin Ritter
*/

package sharon.serialization;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 *
 */
public class MessageOutput {

    private OutputStreamWriter messageOut;

    public MessageOutput(OutputStream out) {
        messageOut = new OutputStreamWriter(out);
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

/*    *//**
     * Writes the byte[] out to the OutputStreamWriter
     *
     * @param byteOut byte[] to write out
     * @throws IOException if I/O problem
     *//*
    public void writeStr(ByteArrayOutputStream byteOut) throws IOException {

    }*/
}
