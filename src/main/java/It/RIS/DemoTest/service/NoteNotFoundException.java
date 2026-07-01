package It.RIS.DemoTest.service;


public class NoteNotFoundException extends RuntimeException {

    private final long id;

    public NoteNotFoundException(long id) {
        super("Note " + id + " not found");
        this.id = id;
    }

    public long id() {
        return id;
    }
}
