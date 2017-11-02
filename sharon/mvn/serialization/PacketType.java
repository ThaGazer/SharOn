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
    ANSWERREQUEST("Answer to request"), CLEARCACHE(""),
    MAVENADDITIONS("Add mavens"), MAVENDELETIONS("Delete Mavens"),
    NODEADDITIONS("Add nodes"), NODEDELETIONS("Delete nodes"),
    REQUESTMAVENS("Request mavens"), REQUESTNODES("Request nodes");

    /**
     * creates a new packet type
     * @param s string to assign to enum
     */
    PacketType(String s) {
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
            case 0:
                return ANSWERREQUEST;
            case 1:
                return MAVENADDITIONS;
            case 2:
                return MAVENDELETIONS;
            case 3:
                return NODEADDITIONS;
            case 4:
                return NODEDELETIONS;
            case 5:
                return REQUESTMAVENS;
            case 6:
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
        switch(this) {
            case ANSWERREQUEST:
                return "AR";
            case MAVENADDITIONS:
                return "MA";
            case MAVENDELETIONS:
                return "MD";
            case NODEADDITIONS:
                return "NA";
            case NODEDELETIONS:
                return "ND";
            case REQUESTMAVENS:
                return "RM";
            case REQUESTNODES:
                return "RN";
            case CLEARCACHE:
                return "";
            default:
                return null;
        }
    }

    /**
     * Get code for type
     * @return type code
     */
    public int getCode() {
        switch(this) {
            case ANSWERREQUEST:
                return 0;
            case MAVENADDITIONS:
                return 1;
            case MAVENDELETIONS:
                return 2;
            case NODEADDITIONS:
                return 3;
            case NODEDELETIONS:
                return 4;
            case REQUESTMAVENS:
                return 5;
            case REQUESTNODES:
                return 6;
            case CLEARCACHE:
                return 7;
            default:
                return -1;
        }
    }
}
