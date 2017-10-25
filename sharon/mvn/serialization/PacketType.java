package mvn.serialization;

import java.io.Serializable;

/**
 * Project: SharOn
 * Created by Justin Ritter on 10/24/2017.
 */
public enum PacketType implements Serializable, Comparable<PacketType> {
    ANSERREQUEST("Answer to request"), CLEARCACHE(""), MAVENADDITIONS("Add mavens"), MAVENDELETIONS("Delete Mavens"),
    NODEADDITIONS("Add nodes"), NODEDELETIONS("Delete nodes"), REQUESTMAVENS("Request mavens"),
    REQUESTNODES("Request nodes");

    private String packettype;

    /**
     * creates a new packet type
     * @param s string to assign to enum
     */
    PacketType(String s) {
        packettype = s;
    }

    /**
     * Get type for given cmd
     * @param cmd cmd to find type of
     * @return cmd corresponding to code or null if bad cmd
     */
    public static PacketType getByCmd(String cmd) {
        switch(cmd.toLowerCase()) {
            case "ar":
                return ANSERREQUEST;
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
            default:
                return CLEARCACHE;
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
                return ANSERREQUEST;
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
            default:
                return CLEARCACHE;
        }
    }

    /**
     * Get cmd for type
     * @return type cmd
     */
    public String getCmd() {
        switch(this) {
            case ANSERREQUEST:
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
            default:
                return "";
        }
    }

    /**
     * Get code for type
     * @return type code
     */
    public int getCode() {
        switch(this) {
            case ANSERREQUEST:
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
            default:
                return 7;
        }
    }

}
