package It.RIS.DemoTest.persistence;

import It.RIS.DemoTest.domain.Note;

import java.util.List;
import java.util.Optional;

/**
 * Storage abstraction
 */
public interface NoteRepository {

    Note create(Note note);

    Optional<Note> findById(long id);

    List<Note> findAll();

    boolean update(Note note);

    boolean deleteById(long id);
}
