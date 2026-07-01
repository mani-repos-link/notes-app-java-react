package It.RIS.DemoTest.web;

import It.RIS.DemoTest.domain.Note;
import It.RIS.DemoTest.service.NoteNotFoundException;
import It.RIS.DemoTest.service.NoteService;
import It.RIS.DemoTest.service.ValidationException;
import It.RIS.DemoTest.web.dto.ErrorResponse;
import It.RIS.DemoTest.web.dto.NoteRequest;
import It.RIS.DemoTest.web.dto.NoteResponse;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;

public class NoteController implements HttpHandler {

    public static final String PATH = "/api/notes";
    private static final Logger LOG = System.getLogger(NoteController.class.getName());
    private final NoteService noteSvc;

    public NoteController(NoteService service) {
        this.noteSvc = service;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String msg = "Request intercepted: " + exchange.getRequestURI();
        LOG.log(Level.TRACE, msg);
        System.out.println(msg);
        try {
            routeRequest(exchange);
        } catch (ValidationException e) {
            HttpSupport.sendJson(exchange, 400, ErrorResponse.validation(e.fieldErrors()));
        } catch (NoteNotFoundException e) {
            HttpSupport.sendJson(exchange, 404, ErrorResponse.of(404, "Not Found", e.getMessage()));
        } catch (JsonSyntaxException | NumberFormatException e) {
            HttpSupport.sendJson(exchange, 400,
                    ErrorResponse.of(400, "Bad Request", "Malformed request: " + e.getMessage()));
        } catch (RuntimeException e) {
            LOG.log(Level.ERROR, "Unexpected error handling " + exchange.getRequestMethod()
                    + " " + exchange.getRequestURI(), e);
            HttpSupport.sendJson(exchange, 500,
                    ErrorResponse.of(500, "Internal Server Error", "An unexpected error occurred"));
        }
    }


    private void routeRequest(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String remainder = exchange.getRequestURI().getPath().substring(PATH.length());

        // if uri: /api/notes (or /api/notes/)
        if (remainder.isEmpty() || remainder.equals("/")) {
            switch (method) {
                case "GET" -> list(exchange);
                case "POST" -> create(exchange);
                default -> methodNotAllowed(exchange, "GET, POST");
            }
            return;
        }

        // if uri: /api/notes/{id}
        if (remainder.startsWith("/")) {
            String idSegment = remainder.substring(1);
            if (!idSegment.isEmpty() && !idSegment.contains("/")) {
                long id = Long.parseLong(idSegment); // NumberFormatException -> 400 Bad Request
                switch (method) {
                    case "GET" -> getOne(exchange, id);
                    case "PUT" -> update(exchange, id);
                    case "DELETE" -> delete(exchange, id);
                    default -> methodNotAllowed(exchange, "GET, PUT, DELETE");
                }
                return;
            }
        }

        // Shares our prefix but is not a note resource.
        HttpSupport.sendJson(
                exchange,
                404,
                ErrorResponse.of(
                        404,
                        "Not Found",
                        "No resource at " + exchange.getRequestURI().getPath()
                )
        );
    }

    private void list(HttpExchange exchange) throws IOException {
        List<NoteResponse> body = noteSvc.list().stream().map(NoteResponse::from).toList();
        HttpSupport.sendJson(exchange, 200, body);
    }

    private void create(HttpExchange exchange) throws IOException {
        NoteRequest request = readRequest(exchange);
        Note created = noteSvc.create(request.title(), request.content());
        exchange.getResponseHeaders().set("Location", PATH + "/" + created.id());
        HttpSupport.sendJson(exchange, 201, NoteResponse.from(created));
    }

    private void getOne(HttpExchange exchange, long id) throws IOException {
        HttpSupport.sendJson(exchange, 200, NoteResponse.from(noteSvc.get(id)));
    }

    private void update(HttpExchange exchange, long id) throws IOException {
        NoteRequest request = readRequest(exchange);
        Note updated = noteSvc.update(id, request.title(), request.content());
        HttpSupport.sendJson(exchange, 200, NoteResponse.from(updated));
    }

    private void delete(HttpExchange exchange, long id) throws IOException {
        noteSvc.delete(id);
        HttpSupport.sendStatus(exchange, 204);
    }

    private void methodNotAllowed(HttpExchange exchange, String allowed) throws IOException {
        exchange.getResponseHeaders().set("Allow", allowed);
        HttpSupport.sendJson(
                exchange,
                405,
                ErrorResponse.of(
                        405,
                        "Method Not Allowed",
                        "Supported methods: " + allowed
                )
        );
    }


    private NoteRequest readRequest(HttpExchange exchange) throws IOException {
        NoteRequest request = Json.read(HttpSupport.readBody(exchange), NoteRequest.class);
        return request != null ? request : new NoteRequest(null, null);
    }
}
