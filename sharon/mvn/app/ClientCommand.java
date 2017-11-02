/*
 * ClientCommand
 * Version 1.0 created 10/31/2017
 *
 * Authors:
 * -Justin Ritter
 */
package mvn.app;

/**
 * commands that a user can use on the client side of a maven
 */
public enum ClientCommand {
    REQUESTNODE("rn"), REQUESTMAVEN("rm"), NODEADDITION("na"),
    MAVENADDITION("ma"), NODEDELETION("nd"), MAVENDELETION("md"), EXIT("exit");

    private String commandName;

    /**
     * creates new client command
     * @param s string to assign to enum
     */
    ClientCommand(String s) {
        commandName = s;
    }

    /**
     * Get type for given command
     * @param cmd command to find type of
     * @return command corresponding to code or null if bad command
     */
    public static ClientCommand getByCmd(String cmd) {
        switch(cmd.toLowerCase()) {
            case "ma":
                return MAVENADDITION;
            case "md":
                return MAVENDELETION;
            case "na":
                return NODEADDITION;
            case "nd":
                return NODEDELETION;
            case "rm":
                return REQUESTMAVEN;
            case "rn":
                return REQUESTNODE;
            case "exit":
                return EXIT;
            default:
                return null;
        }
    }

    /**
     * get command for type
     * @return type of command
     */
    public String getCmd() {
        return commandName;
    }
}
