/*
 * ErrorType
 * Version 1.0 created 10/25/2017
 *
 * Authors:
 * -Justin Ritter
 */
package mvn.serialization;

import java.io.Serializable;

/**
 * Project: SharOn
 * Created by Justin Ritter on 10/24/2017.
 */
public enum ErrorType implements Serializable, Comparable<ErrorType> {
    INCORRECTPACKET("Unexpected packet type"), NONE("No error"), SYSTEM("System error");

    private String errortype = "";

    ErrorType(String s) {
        errortype = s;
    }

    /**
     * get error for given code
     * @param code code of error
     * @return corresponding to code or null if bad code
     */
    public static ErrorType getByCode(int code) {
        switch (code) {
            case 0:
                return ErrorType.INCORRECTPACKET;
            case 1:
                return ErrorType.NONE;
            case 2:
                return ErrorType.SYSTEM;
            default:
                return null;
        }
    }

    /**
     * get code for error
     * @return error code
     */
    public int getCode() {
        switch(this) {
            case INCORRECTPACKET:
                return 0;
            case NONE:
                return 1;
            case SYSTEM:
                return 2;
            default:
                return -1;
        }
    }
}
