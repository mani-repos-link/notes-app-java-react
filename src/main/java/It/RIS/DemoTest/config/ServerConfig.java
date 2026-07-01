package It.RIS.DemoTest.config;

import java.nio.file.Path;

/**
 * @param host              network interface to bind
 * @param port              TCP port to listen on
 * @param databaseUrl       JDBC URL of the H2 db
 * @param databaseUser      db user name
 * @param databasePassword  db password
 * @param corsAllowedOrigin Access-Control-Allow-Origin
 * @param staticDir         directory of front-end dist
 */
public record ServerConfig(String host, int port, String databaseUrl, String databaseUser,
                           String databasePassword, String corsAllowedOrigin, Path staticDir) {

    public static ServerConfig fromEnvironment() {
        return new ServerConfig(
                resolve("NOTES_HOST", "notes.host", "0.0.0.0"),
                Integer.parseInt(resolve("NOTES_PORT", "notes.port", "8080")),
                resolve("NOTES_DB_URL", "notes.db.url", "jdbc:h2:file:./data/notes;AUTO_SERVER=TRUE"),
                resolve("NOTES_DB_USER", "notes.db.user", "sa"),
                resolve("NOTES_DB_PASSWORD", "notes.db.password", ""),
                resolve("NOTES_CORS_ORIGIN", "notes.cors.origin", "*"),
                Path.of(resolve("NOTES_STATIC_DIR", "notes.static.dir", "frontend/dist")));
    }

    private static String resolve(String envKey, String propertyKey, String defaultValue) {
        String fromEnv = System.getenv(envKey);
        if (fromEnv != null && !fromEnv.isBlank()) {
            return fromEnv;
        }
        return System.getProperty(propertyKey, defaultValue);
    }
}
