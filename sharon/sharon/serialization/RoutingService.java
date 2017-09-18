package sharon.serialization;

import java.io.Serializable;

/**
 * SharOn message routing service
 */
public enum RoutingService implements Serializable, Comparable<RoutingService> {
    BREADTHFIRSTBROADCAST, DEPTHFIRSTSEARCH;

    private static final String routServ = "RoutingService";
    private static final String illParameter = "Error: no matching enum";

    /**
     * Get code for routing service
     * @return routing service code
     */
    public int getServiceCode() {
        switch(this) {
            case BREADTHFIRSTBROADCAST:
                return 0;
            case DEPTHFIRSTSEARCH:
                return 1;
            default:
                return -1;
        }
    }

    /**
     * Get routing service for given code
     * @param code code of routing service
     * @return routing service corresponding to code
     * @throws BadAttributeValueException if bad code value
     */
    public static RoutingService getRoutingService(int code)
            throws BadAttributeValueException {
        switch(code) {
            case 0:
                return RoutingService.BREADTHFIRSTBROADCAST;
            case 1:
                return RoutingService.DEPTHFIRSTSEARCH;
            default:
                throw new BadAttributeValueException(routServ, illParameter);
        }
    }
}
