/*
 * app:ClientCommand
 * Created on 11/10/2017
 *
 * Author(s):
 * -Justin Ritter
 */
package sharon.app;

public enum ClientCommand {
    CONNECT("connect"), DOWNLOAD("download"), EXIT("exit"), SEARCH("");

    private String name;

    ClientCommand(String s) {
        name = s;
    }

    public static ClientCommand getByCmd(String cmd) {
        switch(cmd.toLowerCase()) {
            case "connect":
                return CONNECT;
            case "download":
                return DOWNLOAD;
            case "exit":
                return EXIT;
            default:
                return SEARCH;
        }
    }

    public String getName() {
        return name;
    }
}
