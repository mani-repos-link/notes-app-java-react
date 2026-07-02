package It.RIS.DemoTest.web.dto;

import It.RIS.DemoTest.domain.Note;

import java.time.Instant;

public record NoteResponse(Long id, String title, String content, Instant createdAt, Instant updatedAt) {

    public static NoteResponse from(Note note) {
        return new NoteResponse(
                note.getId(),
                note.getTitle(),
                note.getContent(),
                note.getCreatedAt(),
                note.getUpdatedAt());
    }
}
