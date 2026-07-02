package It.RIS.DemoTest.service;

import It.RIS.DemoTest.domain.Note;

import java.util.List;

public interface NoteService {

    Note create(String title, String content);

    List<Note> list();

    Note get(long id);

    Note update(long id, String title, String content);

    void delete(long id);
}
