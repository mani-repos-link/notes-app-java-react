import { useState } from 'react'
import type { Note, NoteInput } from '../types'
import NoteForm from './NoteForm'

interface NoteItemProps {
  note: Note
  onUpdate: (id: number, input: NoteInput) => Promise<void>
  onDelete: (id: number) => Promise<void>
}

export default function NoteItem({ note, onUpdate, onDelete }: NoteItemProps) {
  const [editing, setEditing] = useState(false)


  async function handleDelete() {
    if (!window.confirm('Delete this note?')) return
    try {
      await onDelete(note.id)
    } catch (e) {
      window.alert((e as Error).message)
    }
  }

  if (editing) {
    return (
      <li className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
        <NoteForm
          initialValue={note}
          submitLabel="Save"
          onCancel={() => setEditing(false)}
          onSubmit={async (input) => {
            await onUpdate(note.id, input)
            setEditing(false)
          }}
        />
      </li>
    )
  }

  return (
    <li className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
      <div className="flex items-start justify-between gap-4">
        <h3 className="text-lg font-semibold wrap-break-word">{note.title}</h3>
        <div className="flex shrink-0 gap-2">
          <button
            onClick={() => setEditing(true)}
            className="rounded-lg border border-slate-300 px-3 py-1 text-sm hover:bg-slate-100">
            Edit
          </button>
          <button
            onClick={handleDelete}
            className="rounded-lg border border-slate-300 px-3 py-1 text-sm text-red-600 hover:bg-red-50"
          >
            Delete
          </button>
        </div>
      </div>
      {note.content && <p className="mt-2 whitespace-pre-wrap wrap-break-word">{note.content}</p>}
      <p className="mt-3 text-xs text-slate-400">Updated {new Date(note.updatedAt).toLocaleString()}</p>
    </li>
  )
}
