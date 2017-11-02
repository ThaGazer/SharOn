/*
 * ErrorType
 * Version 1.1 created 10/25/2017
 *
 * Authors:
 * -Justin Ritter
 */
package mvn.serialization;

import java.io.Serializable;

/**
 * MVN error type
 */
public enum ErrorType implements Serializable, Comparable<ErrorType> {
    INCORRECTPACKET(20), NONE(0), SYSTEM(10);

    public static final ErrorType None = NONE;
    public static final ErrorType System = SYSTEM;
    public static final ErrorType IncorrectPacket = INCORRECTPACKET;

    private int errorType;

    /**
     * creates a new error type
     * @param i integer to assign to enum
     */
    ErrorType(int i) {
        errorType = i;
    }

    /**
     * get error for given code
     * @param code code of error
     * @return corresponding to code or null if bad code
     */
    public static ErrorType getByCode(int code) {
        switch (code) {
            case 20:
                return IncorrectPacket;
            case 0:
                return None;
            case 10:
                return System;
            default:
                return null;
        }
    }

    /**
     * get code for error
     * @return error code
     */
    public int getCode() {
        return errorType;
    }
}
