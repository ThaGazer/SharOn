package sharon.serialization;

import java.io.OutputStream;

/**
 * Created by Justin Ritter on 8/29/2017.
 */
public class MessageOutput {
    private OutputStream outputSink;

    public MessageOutput(OutputStream out) {
        outputSink = out;
    }
}
