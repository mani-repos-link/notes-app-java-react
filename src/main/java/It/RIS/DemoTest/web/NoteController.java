package It.RIS.DemoTest.web;

import It.RIS.DemoTest.domain.Note;
import It.RIS.DemoTest.service.NoteService;
import It.RIS.DemoTest.web.dto.NoteRequest;
import It.RIS.DemoTest.web.dto.NoteResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService service;

    public NoteController(NoteService service) {
        this.service = service;
    }

    @GetMapping
    public List<NoteResponse> list() {
        return service.list().stream().map(NoteResponse::from).toList();
    }

    @GetMapping("/{id}")
    public NoteResponse get(@PathVariable long id) {
        return NoteResponse.from(service.get(id));
    }

    @PostMapping
    public ResponseEntity<NoteResponse> create(@Valid @RequestBody NoteRequest request) {
        Note created = service.create(request.title(), request.content());
        return ResponseEntity
                .created(URI.create("/api/notes/" + created.getId()))
                .body(NoteResponse.from(created));
    }

    @PutMapping("/{id}")
    public NoteResponse update(@PathVariable long id, @Valid @RequestBody NoteRequest request) {
        return NoteResponse.from(service.update(id, request.title(), request.content()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        service.delete(id);
    }
}
