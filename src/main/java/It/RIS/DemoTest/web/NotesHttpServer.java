package It.RIS.DemoTest.web;

import It.RIS.DemoTest.config.ServerConfig;
import It.RIS.DemoTest.service.NoteService;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.Executors;


public class NotesHttpServer {

    private final HttpServer server;

    public NotesHttpServer(ServerConfig config, NoteService service) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(config.host(), config.port()), 0);
        CorsFilter cors = new CorsFilter(config.corsAllowedOrigin());

        HttpContext notes = server.createContext(NoteController.PATH, new NoteController(service));
        notes.getFilters().add(cors);

        HttpContext health = server.createContext(
                "/api/health",
                exchange -> HttpSupport.sendJson(exchange, 200, Map.of("status", "UP"))
        );
        health.getFilters().add(cors);
        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    public int port() {
        return server.getAddress().getPort();
    }
}
