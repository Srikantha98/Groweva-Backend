package org.evabirds.evabirds.constant;

public final class SecurityConstants {

    private SecurityConstants() {
    }

    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    public static final String REQUEST_ID_HEADER = "X-Request-ID";
    public static final String MDC_CORRELATION_KEY = "correlationId";

    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_FARM_OWNER = "ROLE_FARM_OWNER";
    public static final String ROLE_RENT_SEEKER = "ROLE_RENT_SEEKER";
    public static final String ROLE_USER = "ROLE_USER";
}
