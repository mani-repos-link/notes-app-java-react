package It.RIS.DemoTest.persistence;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Owns the database connection details and one-time schema setup.
 */
public final class Database {

    private static final String SCHEMA_RESOURCE = "/schema.sql";

    private final String url;
    private final String user;
    private final String password;

    /**
     * @param url      the JDBC URL (e.g. {@code jdbc:h2:file:./data/notes})
     * @param user     the database user
     * @param password the database password
     */
    public Database(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    /**
     * @return an open JDBC connection
     * @throws SQLException if the connection cannot be established
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Init the database table, notes, if it does not already exist by executing.
     *
     * @throws IllegalStateException if the schema script is missing or fails to apply
     */
    public void initializeSchema() {
        String ddl = readSchemaScript();
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(ddl);
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to initialise database schema", e);
        }
    }

    private String readSchemaScript() {
        try (InputStream in = Database.class.getResourceAsStream(SCHEMA_RESOURCE)) {
            if (in == null) {
                throw new FileNotFoundException("Schema resource not found on path: " + SCHEMA_RESOURCE);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read " + SCHEMA_RESOURCE, e);
        }
    }
}
