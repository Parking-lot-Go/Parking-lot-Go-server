package car.demo.global.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    JWT_TOKEN_INVALID("J001", "Invalid JWT token."),
    JWT_TOKEN_EXPIRED("J002", "Expired JWT token."),
    JWT_TOKEN_MISSING("J003", "JWT token is missing."),
    AUTHENTICATION_REQUIRED("AUTH002", "Authentication is required."),
    ACCESS_DENIED("AUTH003", "You do not have permission to access this resource."),
    NOT_FOUND_ENTITY("COM001", "Requested data was not found."),
    ALREADY_ENTITY("COM002", "Entity already exists."),
    NOT_FOUND_PATH("COM003", "Requested path was not found."),
    INVALID_REQUEST("COM004", "Invalid request."),
    SERVER_ERROR("S001", "Server error occurred."),
    INVALID_PROVIDER_TOKEN_FORMAT("AUTH001", "Provider token format is invalid."),
    INVALID_SUPPORT_TICKET_CATEGORY("SUP001", "Category does not match the ticket type."),
    CSV_PARSE_ERROR("P001", "CSV parsing failed."),
    DATA_UPLOAD_ERROR("P002", "Data upload failed.");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
