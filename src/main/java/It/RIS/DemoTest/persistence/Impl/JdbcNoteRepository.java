package It.RIS.DemoTest.persistence.Impl;

import It.RIS.DemoTest.domain.Note;
import It.RIS.DemoTest.persistence.DataAccessException;
import It.RIS.DemoTest.persistence.Database;
import It.RIS.DemoTest.persistence.NoteRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcNoteRepository implements NoteRepository {

    private static final String INSERT_SQL =
            "INSERT INTO notes (title, content, created_at, updated_at) VALUES (?, ?, ?, ?)";
    private static final String SELECT_BY_ID_SQL =
            "SELECT id, title, content, created_at, updated_at FROM notes WHERE id = ?";
    private static final String SELECT_ALL_SQL =
            "SELECT id, title, content, created_at, updated_at FROM notes ORDER BY updated_at DESC, id DESC";
    private static final String UPDATE_SQL =
            "UPDATE notes SET title = ?, content = ?, updated_at = ? WHERE id = ?";
    private static final String DELETE_SQL =
            "DELETE FROM notes WHERE id = ?";

    private final Database database;

    public JdbcNoteRepository(Database database) {
        this.database = database;
    }

    @Override
    public Note create(Note note) {
        try (Connection connection = database.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, note.title());
            statement.setString(2, note.content());
            statement.setObject(3, toOffsetDateTime(note.createdAt()));
            statement.setObject(4, toOffsetDateTime(note.updatedAt()));
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return note.withId(keys.getLong(1));
                }
                throw new DataAccessException("Insert did not return a generated id", null);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to create note", e);
        }
    }

    @Override
    public Optional<Note> findById(long id) {
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID_SQL)) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load note " + id, e);
        }
    }

    @Override
    public List<Note> findAll() {
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = statement.executeQuery()) {

            List<Note> notes = new ArrayList<>();
            while (rs.next()) {
                notes.add(mapRow(rs));
            }
            return notes;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to list notes", e);
        }
    }

    @Override
    public boolean update(Note note) {
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {

            statement.setString(1, note.title());
            statement.setString(2, note.content());
            statement.setObject(3, toOffsetDateTime(note.updatedAt()));
            statement.setLong(4, note.id());
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update note " + note.id(), e);
        }
    }

    @Override
    public boolean deleteById(long id) {
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_SQL)) {

            statement.setLong(1, id);
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete note " + id, e);
        }
    }

    private Note mapRow(ResultSet rs) throws SQLException {
        return new Note(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("content"),
                rs.getObject("created_at", OffsetDateTime.class).toInstant(),
                rs.getObject("updated_at", OffsetDateTime.class).toInstant());
    }

    private OffsetDateTime toOffsetDateTime(Instant instant) {
        return instant.atOffset(ZoneOffset.UTC);
    }
}
