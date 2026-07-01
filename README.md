# NotesTaking

A small note-taking application using Java REST backend and a React framework front-end.

A note is a title + free-form text. You can create, list, view, edit and delete notes.


## Run the backend

```bash
./mvnw exec:java  # on unix systesm
./mvnw package    # build a single runnable jar
java -jar target/NotesTaking.jar
```
Or directly from Intellij with main class NotesApplication

Test API: `GET /api/health` (http://0.0.0.0:8080/api/health)

## Setup Frontend

```bash
cd frontend
npm install
npm run dev 
```
 or just 

```bash
cd frontend && npm run build && cd ..   # outputs frontend/dist
java -jar target/NotesTaking.jar        # serves the UI at http://localhost:8080/
```
