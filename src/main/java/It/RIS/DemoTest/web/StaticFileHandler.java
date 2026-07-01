package It.RIS.DemoTest.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;


public class StaticFileHandler implements HttpHandler {

    private static final Map<String, String> CONTENT_TYPES = Map.of(
            "html", "text/html; charset=utf-8",
            "js", "text/javascript; charset=utf-8",
            "css", "text/css; charset=utf-8",
            "json", "application/json; charset=utf-8",
            "svg", "image/svg+xml",
            "png", "image/png",
            "ico", "image/x-icon",
            "woff2", "font/woff2",
            "map", "application/json; charset=utf-8");

    private final Path root;

    public StaticFileHandler(Path root) {
        this.root = root.toAbsolutePath().normalize();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            HttpSupport.sendStatus(exchange, 405);
            return;
        }

        if (!Files.isDirectory(root)) {
            sendMissingBuildNotice(exchange); // send message
            return;
        }

        Path file = resolve(exchange.getRequestURI().getPath());
        if (file == null) {
            HttpSupport.sendStatus(exchange, 404);
            return;
        }
        serve(exchange, file);
    }


    private Path resolve(String requestPath) {
        String relative = requestPath.equals("/")
                ? "index.html"
                : requestPath.substring(1);
        Path candidate = root.resolve(relative).normalize();

        if (!candidate.startsWith(root)) {
            return null;
        }
        if (Files.isDirectory(candidate)) {
            candidate = candidate.resolve("index.html");
        }
        return Files.isRegularFile(candidate)
                ? candidate
                : null;
    }

    private void serve(HttpExchange exchange, Path file) throws IOException {
        byte[] bytes = Files.readAllBytes(file);
        exchange.getResponseHeaders().set("Content-Type", contentTypeOf(file));
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }

    private String contentTypeOf(Path file) {
        String name = file.getFileName().toString();
        int dot = name.lastIndexOf('.');
        String extension = dot < 0 ? "" : name.substring(dot + 1).toLowerCase();
        return CONTENT_TYPES.getOrDefault(extension, "application/octet-stream");
    }

    private void sendMissingBuildNotice(HttpExchange exchange) throws IOException {
        byte[] body = ("Notes API is running. Try the REST API under " + NoteController.PATH
                + ".\nFront-end build not found at " + root
                + " — run `npm run build` in ./frontend, or use the Vite dev server.")
                .getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
        exchange.sendResponseHeaders(200, body.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(body);
        }
    }
}
