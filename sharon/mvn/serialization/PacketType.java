/*
 * PacketTest
 * Version 1.1 created 10/24/2017
 *
 * Authors:
 * -Justin Ritter
 */
package mvn.serialization;

import java.io.Serializable;

/**
 * MVN message type
 */
public enum PacketType implements Serializable, Comparable<PacketType> {
    ANSWERREQUEST(2, "AR"), CLEARCACHE(7, ""),
    MAVENADDITIONS(4, "MA"), MAVENDELETIONS(6, "MD"),
    NODEADDITIONS(3, "NA"), NODEDELETIONS(5, "ND"),
    REQUESTMAVENS(1, "RM"), REQUESTNODES(0, "RN");

    public static final PacketType AnswerRequest = ANSWERREQUEST;
    public static final PacketType ClearCache = CLEARCACHE;
    public static final PacketType MavenAdditions = MAVENADDITIONS;
    public static final PacketType MavenDeletions = MAVENDELETIONS;
    public static final PacketType NodeAdditions = NODEADDITIONS;
    public static final PacketType NodeDeletions = NODEDELETIONS;
    public static final PacketType RequestMavens = REQUESTMAVENS;
    public static final PacketType RequestNodes = REQUESTNODES;

    private int code;
    private String cmd;

    /**
     * creates a new packet type
     * @param i integer to assign to enum
     */
    PacketType(int i, String s) {
        code = i;
        cmd = s;
    }

    /**
     * Get type for given cmd
     * @param cmd cmd to find type of
     * @return cmd corresponding to code or null if bad cmd
     */
    public static PacketType getByCmd(String cmd) {
        switch(cmd.toLowerCase()) {
            case "ar":
                return AnswerRequest;
            case "ma":
                return MavenAdditions;
            case "md":
                return MavenDeletions;
            case "na":
                return NodeAdditions;
            case "nd":
                return NodeDeletions;
            case "rm":
                return RequestMavens;
            case "rn":
                return RequestNodes;
            case "":
                return ClearCache;
            default:
                return null;
        }
    }

    /**
     * Get type for given code
     * @param code code of type
     * @return type corresponding to code or null if bad code
     */
    public static PacketType getByCode(int code) {
        switch(code) {
            case 2:
                return AnswerRequest;
            case 4:
                return MavenAdditions;
            case 6:
                return MavenDeletions;
            case 3:
                return NodeAdditions;
            case 5:
                return NodeDeletions;
            case 1:
                return RequestMavens;
            case 0:
                return RequestNodes;
            case 7:
                return ClearCache;
            default:
                return null;
        }
    }

    /**
     * Get cmd for type
     * @return type cmd
     */
    public String getCmd() {
        return cmd;
    }

    /**
     * Get code for type
     * @return type code
     */
    public int getCode() {
        return code;
    }
}
