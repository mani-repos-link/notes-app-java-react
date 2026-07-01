package It.RIS.DemoTest.web;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;


final class HttpSupport {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String JSON_CONTENT_TYPE = "application/json; charset=utf-8";

    /**
     * @param exchange  HTTP exchange/Request-Resp.
     * @return the body text
     * @throws IOException if the body cannot be read
     */
    static String readBody(HttpExchange exchange) throws IOException {
        try (InputStream in = exchange.getRequestBody()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    /**
     * @param exchange  HTTP exchange/Request-Resp.
     * @param status    HTTP status code
     * @param body     object to serialise
     * @throws IOException if the response cannot be written
     */
    static void sendJson(HttpExchange exchange, int status, Object body) throws IOException {
        byte[] payload = Json.write(body).getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set(CONTENT_TYPE, JSON_CONTENT_TYPE);
        exchange.sendResponseHeaders(status, payload.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(payload);
        }
    }

    /**
     * @param exchange  HTTP exchange/Request-Resp.
     * @param status    HTTP status code
     * @throws IOException if the response cannot be written
     */
    static void sendStatus(HttpExchange exchange, int status) throws IOException {
        exchange.sendResponseHeaders(status, -1);
        exchange.close();
    }
}
