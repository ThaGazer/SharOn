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
                return ANSWERREQUEST;
            case "ma":
                return MAVENADDITIONS;
            case "md":
                return MAVENDELETIONS;
            case "na":
                return NODEADDITIONS;
            case "nd":
                return NODEDELETIONS;
            case "rm":
                return REQUESTMAVENS;
            case "rn":
                return REQUESTNODES;
            case "":
                return CLEARCACHE;
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
                return ANSWERREQUEST;
            case 4:
                return MAVENADDITIONS;
            case 6:
                return MAVENDELETIONS;
            case 3:
                return NODEADDITIONS;
            case 5:
                return NODEDELETIONS;
            case 1:
                return REQUESTMAVENS;
            case 0:
                return REQUESTNODES;
            case 7:
                return CLEARCACHE;
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
