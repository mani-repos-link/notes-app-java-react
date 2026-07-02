package It.RIS.DemoTest;

import It.RIS.DemoTest.config.ServerConfig;
import It.RIS.DemoTest.persistence.Database;
import It.RIS.DemoTest.persistence.Impl.JdbcNoteRepository;
import It.RIS.DemoTest.persistence.NoteRepository;
import It.RIS.DemoTest.service.Impl.DefaultNoteService;
import It.RIS.DemoTest.service.NoteService;
import It.RIS.DemoTest.web.NotesHttpServer;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

public final class NotesApplication {

    private static final Logger LOG = System.getLogger(NotesApplication.class.getName());

    public static void main(String[] args) throws Exception {
        ServerConfig config = ServerConfig.fromEnvironment();

        Database database = new Database(config.databaseUrl(), config.databaseUser(), config.databasePassword());
        database.initializeSchema();

        NoteRepository repository = new JdbcNoteRepository(database);
        NoteService service = new DefaultNoteService(repository);
        NotesHttpServer server = new NotesHttpServer(config, service);

        Runtime.getRuntime()
                .addShutdownHook(new Thread(server::stop, "notes-shutdown"));
        server.start();

        LOG.log(Level.INFO, "Notes API listening on http://{0}:{1,number,#} (database: {2})",
                config.host(), config.port(), config.databaseUrl());
    }
}
