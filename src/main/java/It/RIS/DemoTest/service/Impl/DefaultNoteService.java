package It.RIS.DemoTest.service.Impl;

import It.RIS.DemoTest.domain.Note;
import It.RIS.DemoTest.persistence.NoteRepository;
import It.RIS.DemoTest.service.NoteNotFoundException;
import It.RIS.DemoTest.service.NoteService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DefaultNoteService implements NoteService {

    private static final Sort NEWEST_FIRST = Sort.by(Sort.Direction.DESC, "updatedAt", "id");

    private final NoteRepository repository;

    public DefaultNoteService(NoteRepository repository) {
        this.repository = repository;
    }

    @Override
    public Note create(String title, String content) {
        return repository.save(new Note(title, content));
    }

    @Override
    public List<Note> list() {
        return repository.findAll(NEWEST_FIRST);
    }

    @Override
    public Note get(long id) {
        return repository.findById(id).orElseThrow(() -> new NoteNotFoundException(id));
    }

    @Override
    public Note update(long id, String title, String content) {
        Note note = get(id);
        note.setTitle(title);
        note.setContent(content);
        return repository.save(note);
    }

    @Override
    public void delete(long id) {
        if (!repository.existsById(id)) {
            throw new NoteNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
