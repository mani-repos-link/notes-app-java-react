import type { Note, NoteInput } from '../types'
import NoteItem from './NoteItem'

interface NoteListProps {
  notes: Note[]
  onUpdate: (id: number, input: NoteInput) => Promise<void>
  onDelete: (id: number) => Promise<void>
}

export default function NoteList({ notes, onUpdate, onDelete }: NoteListProps) {
  if (notes.length === 0) {
    return <p className="py-4 text-center text-slate-500">No notes yet — create your first one above.</p>
  }

  return (
    <ul className="flex flex-col gap-4">
      {notes.map((note) => (
        <NoteItem key={note.id} note={note} onUpdate={onUpdate} onDelete={onDelete} />
      ))}
    </ul>
  )
}
