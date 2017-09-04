package sharon.serialization;

/**
 * Created by Justin Ritter on 8/29/2017.
 */
public class BadAttributeValueException extends Exception{

    /** hold the attribute name*/
    private String attributeName;

    public BadAttributeValueException(String message, String attbuName) {
        super(message);
        attributeName = attbuName;
    }

    public BadAttributeValueException(String message, String attbuName,
                                      Throwable cause) {
        super(message, cause);
        attributeName = attbuName;
    }

    public String getAttributeName() {
        return attributeName;
    }
}
