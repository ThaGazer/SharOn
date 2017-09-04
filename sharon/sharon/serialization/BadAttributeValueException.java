/*
 * Result
 * version 0.0 created 8/29/2017
 *
 * Authors:
 * -Justin Ritter
*/

package sharon.serialization;

/**
 * Thrown to indicate bad value
 */
public class BadAttributeValueException extends Exception{

    /* hold the attribute name*/
    private String attributeName;

    /**
     * Constructs BadAttributeException
     * @param message error message
     * @param attbuName name of attribute
     * @throws NullPointerException if message or attributeName are null
     */
    public BadAttributeValueException(String message, String attbuName)
            throws NullPointerException {
        super(message);
        attributeName = attbuName;
    }

    /**
     * Constructs BadAttributeException
     * @param message error message
     * @param attbuName name of attribute
     * @param cause exception cause
     */
    public BadAttributeValueException(String message, String attbuName,
                                      Throwable cause) {
        super(message, cause);
        attributeName = attbuName;
    }

    /**
     * Returns the attribute name of a BadAttributeValueException
     * @return the attributes name
     */
    public String getAttributeName() {
        return attributeName;
    }
}
