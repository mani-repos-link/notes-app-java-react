package It.RIS.DemoTest.web.dto;

import java.time.Instant;
import java.util.Map;

/**
 * @param timestamp     when the error occurred
 * @param status        HTTP status code
 * @param error         HTTP status reason phrase (e.g. {@code "Not Found"})
 * @param message       a human-readable explanation
 * @param fieldErrors   per-field reasons for a validation failure, or {@code null}
 */
public record ErrorResponse(Instant timestamp, int status, String error, String message,
                            Map<String, String> fieldErrors) {

    /**
     * @param status    HTTP status code
     * @param error     status reason
     * @param message   explanation
     * @return the error body
     */
    public static ErrorResponse of(int status, String error, String message) {
        return new ErrorResponse(Instant.now(), status, error, message, null);
    }

    /**
     * @param fieldErrors map of field name to failure reason
     * @return the error body
     */
    public static ErrorResponse validation(Map<String, String> fieldErrors) {
        return new ErrorResponse(Instant.now(), 400, "Bad Request", "Validation failed", fieldErrors);
    }
}
