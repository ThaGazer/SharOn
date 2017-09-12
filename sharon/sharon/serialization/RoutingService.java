package sharon.serialization;

import java.io.Serializable;

/**
 * SharOn message routing service
 */
public enum RoutingService implements Serializable, Comparable<RoutingService> {
    BREADTHFIRSTBROADCAST, DEPTHFIRSTSEARCH;

    private static final String routServ = "RoutingService";

    /**
     * Returns an array containing the constants of this enum type, in the order
     * they are declared. This method may be used to iterate over the constants
     * as follows:
     * for (RoutingService c : RoutingService.values()) {
     *      System.out.println(c);
     * }
     *
     * @return an array containing the constants of this enum type,
     *          in the order they are declared
     */
    public static RoutingService[] values() {

        return new RoutingService[] {BREADTHFIRSTBROADCAST, DEPTHFIRSTSEARCH};
    }

    /**
     * Returns the enum constant of this type with the specified name.
     * The string must match exactly an identifier used to declare an enum
     * constant in this type.
     * (Extraneous whitespace characters are not permitted.)
     * @param val the name of the enum constant to be returned.
     * @return the enum constant with the specified name
     * @throws IllegalArgumentException
     * @throws NullPointerException
     */
    public static RoutingService valueOf(String val) {

    }

    /**
     * Get code for routing service
     * @return routing service code
     */
    public int getServiceCode() {
        return 0;
    }

    /**
     * Get routing service for given code
     * @param code code of routing service
     * @return routing service corresponding to code
     * @throws BadAttributeValueException if bad code value
     */
    public static RoutingService getRoutingService(int code)
            throws BadAttributeValueException {
        RoutingService ret;

        switch(code) {
            case 0:
                ret = RoutingService.BREADTHFIRSTBROADCAST;
                break;
            case 1:
                ret = RoutingService.DEPTHFIRSTSEARCH;
                break;
            default:
                throw new BadAttributeValueException(routServ, "Bad field");
        }

        return ret;
    }
}
