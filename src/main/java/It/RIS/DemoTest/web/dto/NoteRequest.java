package It.RIS.DemoTest.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NoteRequest(
        @NotBlank(message = "must not be blank")
        @Size(max = 255, message = "must be at most 255 characters")
        String title,

        @Size(max = 20_000, message = "must be at most 20000 characters")
        String content) {
}
