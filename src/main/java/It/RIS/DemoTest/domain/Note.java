package It.RIS.DemoTest.domain;

import java.time.Instant;
import java.util.Objects;

/**
 * @param id            db identity
 * @param title         title of note
 * @param content       free-form body text
 * @param createdAt     note was first created
 * @param updatedAt     note was last modified
 */
public record Note(Long id, String title, String content, Instant createdAt, Instant updatedAt) {

    /**
     * @param title     note title
     * @param content   note body
     * @param now       creation instant
     * @return a new note with the id null
     */
    public static Note create(String title, String content, Instant now) {
        return new Note(null, title, content, now, now);
    }

    /**
     * @param assignedId    db-generated id
     * @return a copy identical to this note but with {@code id} set
     */
    public Note withId(long assignedId) {
        return new Note(assignedId, title, content, createdAt, updatedAt);
    }

    /**
     * @param newTitle      new title
     * @param newContent    new body
     * @param now           modification instant
     * @return the edited note
     */
    public Note edited(String newTitle, String newContent, Instant now) {
        return new Note(id, newTitle, newContent, createdAt, now);
    }
}
