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

    public static ClientCommand getByCmd(String[] cmd) {
        switch(cmd[0].toLowerCase()) {
            case "connect":
                if(cmd.length == 3) {
                    return CONNECT;
                } else {
                    return SEARCH;
                }
            case "download":
                if(cmd.length == 5) {
                    return DOWNLOAD;
                } else {
                    return SEARCH;
                }
            case "exit":
                if(cmd.length == 1) {
                    return EXIT;
                } else {
                    return SEARCH;
                }
            default:
                return SEARCH;
        }
    }

    public String getName() {
        return name;
    }
}
