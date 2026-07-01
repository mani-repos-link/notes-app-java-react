package It.RIS.DemoTest.web;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class CorsFilter extends Filter {

    private final String allowedOrigin;

    public CorsFilter(String allowedOrigin) {
        this.allowedOrigin = allowedOrigin;
    }

    @Override
    public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", allowedOrigin);
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().set("Access-Control-Max-Age", "3600");

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            HttpSupport.sendStatus(exchange, 204);
            return;
        }
        chain.doFilter(exchange);
    }

    @Override
    public String description() {
        return "Adds CORS headers and handle OPTIONS requests";
    }
}
