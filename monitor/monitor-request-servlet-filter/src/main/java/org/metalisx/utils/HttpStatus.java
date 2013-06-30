package org.metalisx.utils;

/**
 * Enumeration for HTTP codes.
 * 
 * @author Stefan.Oude.Nijhuis
 * 
 */
public enum HttpStatus {

    // 100 HTTP codes
    CONTINUE(100, "Continue", "RFC2616"),
    SWITCHING_PROTOCOLS(101, "Switching protocols", "RFC2616"),
    PROCESSING(102, "Processing", "RFC2616"),

    // 200 HTTP codes 
    OK(200, "Ok", "RFC2616"),
    CREATED(201, "Created", "RFC2616"),
    ACCEPTED(202, "Accepted", "RFC2616"),
    NON_AUTHORITATIVE_INFORMATION(203, "Non-Authoritative Information", "RFC2616"),
    NO_CONTENT(204, "No Content", "RFC2616"),
    RESET_CONTENT(205, "Reset Content", "RFC2616"),
    PARTIAL_CONTENT(206, "Partial Content", "RFC2616"),
    MULTI_STATUS(207, "Multi-Status", "RFC4918"),
    ALREADY_REPORTED(208, "Already Reported", "RFC5842"),
    IM_USED(226, "IM Used", "RFC3229"),

    // 300 HTTP codes
    MULTIPLE_CHOICES(300, "Multiple Choices", "RFC2616"),
    MOVED_PERMANENTLY(301, "Moved Permanently", "RFC2616"),
    FOUND(302, "Found", "RFC2616"),
    SEE_OTHER(303, "See Other", "RFC2616"),
    NOT_MODIFIED(304, "Not modified", "RFC2616"),
    USE_PROXY(305, "Use Proxy", "RFC2616"),
    RESERVED(306, "Reserved", "RFC2616"),
    TEMPORARY_REDIRECT(307, "Temporary Redirect", "RFC2616"),
    PERMANENT_REDIRECT(308, "Permanent Redirect", "RFC-reschke-http-status-308-07"),

    // 400 HTTP codes
    BAD_REQUEST(400, "Bad Request", "RFC2616"),
    UNAUTHORIZED(401, "Unauthorized", "RFC2616"),
    PAYMENT_REQUIRED(402, "Payment Required", "RFC2616"),
    FORBIDDEN(403, "Forbidden", "RFC2616"),
    NOT_FOUND(404, "Not Found", "RFC2616"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed", "RFC2616"),
    NOT_ACCEPTABLE(406, "Not Acceptable", "RFC2616"),
    PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required", "RFC2616"),
    REQUEST_TIMEOUT(408, "Request Timeout", "RFC2616"),
    CONFLICT(409, "Conflict", "RFC2616"),
    GONE(410, "Gone", "RFC2616"),
    LENGTH_REQUIRED(411, "Length Required", "RFC2616"),
    PRECONDITION_FAILED(412, "Precondition Failed", "RFC2616"),
    REQUEST_ENTITY_TOO_LARGE(413, "Request Entity Too Large", "RFC2616"),
    REQUEST_URI_TOO_LONG(414, "Request URI Too Long", "RFC2616"),
    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type", "RFC2616"),
    REQUESTED_RANGE_NOT_SATISFIABLE(416, "Requested Range Not Satisfiable", "RFC2616"),
    EXPECTATION_FAILED(417, "Expectation Failed", "RFC2616"),
    UNPROCESSABLE_ENTITY(422, "Unprocessable Entity", "RFC4918"),
    LOCKED(423, "Locked", "RFC4918"),
    FAILED_DEPENDENCY(424, "Failed Dependency", "RFC4918"),
    RESERVED_FOR_WEBDAV_ADVANCED_COLLECTIONS_EXPIRED_PROPOSAL(425, "Reserved for WebDav advanced collections expired proposal", "RFC2817"),
    UPGRADE_REQUIRED(426, "Upgrade Required", "RFC2817"),
    PRECONDITION_REQUIRED(428, "Precondition Required", "RFC6585"),
    TOO_MANY_REQUESTS(429, "Too Many Requests", "RFC6585"),
    REQUEST_HEADER_FIELDS_TOO_LARGE(431, "Request Header Fields Too Large", "RFC6585"),

    // 500 HTTP codes
    INTERNAL_SERVER_ERROR(500, "Internal Server Error", "RFC2616"),
    NOT_IMPLEMENTED(501, "Not Implemented", "RFC2616"),
    BAD_GATEWAY(502, "Bad Gateway", "RFC2616"),
    SERVICE_UNAVAILABLE(503, "Service Unavailable", "RFC2616"),
    GATEWAY_TIMEOUT(504, "Gateway Timeout", "RFC2616"),
    HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported", "RFC2616"),
    VARIANT_ALSO_NEGOTIATES(506, "Variant Also Negotiates (Experimental)", "RFC2295"),
    INSUFFICIENT_STORAGE(507, "Insufficient Storage", "RFC4918"),
    LOOP_DETECTED(508, "Loop Detected", "RFC5842"),
    NOT_EXTENDED(510, "Not Extended", "RFC2774"),
    NETWORK_AUTHENTICATION_REQUIRED(511, "Network Authentication Required", "RFC6585");

    private int code;
    private String description;
    private String rfcCode;

    private HttpStatus(int code, String description, String rfcCode) {
        this.code = code;
        this.description = description;
        this.rfcCode = rfcCode;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public String getRfcCode() {
        return rfcCode;
    }

    public static HttpStatus getValue(int code) {
        for (HttpStatus httpStatus : HttpStatus.values()) {
            if (httpStatus.getCode() == code) {
                return httpStatus;
            }
        }
        return null;
    }

}
