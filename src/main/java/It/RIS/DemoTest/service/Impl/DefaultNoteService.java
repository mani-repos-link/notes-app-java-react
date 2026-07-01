package It.RIS.DemoTest.service.Impl;

import It.RIS.DemoTest.domain.Note;
import It.RIS.DemoTest.persistence.NoteRepository;
import It.RIS.DemoTest.service.NoteNotFoundException;
import It.RIS.DemoTest.service.NoteService;
import It.RIS.DemoTest.service.ValidationException;

import java.time.Clock;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DefaultNoteService implements NoteService {

    static final int MAX_TITLE_LENGTH = 255;
    static final int MAX_CONTENT_LENGTH = 20_000;

    private final NoteRepository repository;
    private final Clock clock;

    public DefaultNoteService(NoteRepository repository) {
        this(repository, Clock.systemUTC());
    }

    public DefaultNoteService(NoteRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    @Override
    public Note create(String title, String content) {
        String normalizedTitle = normalizeTitle(title);
        String normalizedContent = normalizeContent(content);
        validate(normalizedTitle, normalizedContent);
        return repository.create(Note.create(normalizedTitle, normalizedContent, now()));
    }

    @Override
    public List<Note> list() {
        return repository.findAll();
    }

    @Override
    public Note get(long id) {
        return repository.findById(id).orElseThrow(() -> new NoteNotFoundException(id));
    }

    @Override
    public Note update(long id, String title, String content) {
        Note existing = get(id); // throws NoteNotFoundException if absent
        String normalizedTitle = normalizeTitle(title);
        String normalizedContent = normalizeContent(content);
        validate(normalizedTitle, normalizedContent);

        Note edited = existing.edited(normalizedTitle, normalizedContent, now());
        repository.update(edited);
        return edited;
    }

    @Override
    public void delete(long id) {
        if (!repository.deleteById(id)) {
            throw new NoteNotFoundException(id);
        }
    }

    private Instant now() {
        return clock.instant();
    }

    private String normalizeTitle(String title) {
        return title == null ? "" : title.strip();
    }

    private String normalizeContent(String content) {
        return content == null ? "" : content;
    }

    private void validate(String title, String content) {
        Map<String, String> errors = new LinkedHashMap<>();
        if (title.isEmpty()) {
            errors.put("title", "must not be blank");
        } else if (title.length() > MAX_TITLE_LENGTH) {
            errors.put("title", "must be at most " + MAX_TITLE_LENGTH + " characters");
        }
        if (content.length() > MAX_CONTENT_LENGTH) {
            errors.put("content", "must be at most " + MAX_CONTENT_LENGTH + " characters");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}
