package mvn.serialization;

/**
 * Created by Justin Ritter on 11/1/2017.
 */
public enum ClientCommand {
    REQUESTNODE("rn"), REQUESTMAVEN("rm"), NODEADDITION("na"),
    MAVENADDITION("ma"), NODEDELETION("nd"), MAVENDELETION("md"), EXIT("exit");

    private String commandName;

    ClientCommand(String s) {
        commandName = s;
    }

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

    public String getCmd() {
        return commandName;
    }
}
