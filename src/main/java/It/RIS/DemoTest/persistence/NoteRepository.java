package It.RIS.DemoTest.persistence;

import It.RIS.DemoTest.domain.Note;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long> {
}
