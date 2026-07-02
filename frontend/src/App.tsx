import { useEffect, useState } from 'react'
import { notesApi } from './api'
import type { Note, NoteInput } from './types'
import NoteForm from './components/NoteForm'
import NoteList from './components/NoteList'

export default function App() {
  const [notes, setNotes] = useState<Note[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    notesApi
      .list()
      .then(setNotes)
      .catch((e: Error) => setError(e.message))
      .finally(() => setLoading(false))
  }, [])

  async function handleCreate(input: NoteInput) {
    const created = await notesApi.create(input)
    setNotes((current) => [created, ...current])
  }

  async function handleUpdate(id: number, input: NoteInput) {
    const updated = await notesApi.update(id, input)
    setNotes((current) => current.map((note) => (note.id === id ? updated : note)))
  }

  async function handleDelete(id: number) {
    await notesApi.remove(id)
    setNotes((current) => current.filter((note) => note.id !== id))
  }

  return (
    <div className="min-h-screen bg-slate-100 text-slate-800">
      <main className="mx-auto max-w-2xl px-4 py-10">
        <header className="mb-6">
          <h1 className="text-3xl font-bold">Notes</h1>
          <p className="text-slate-500">A note-taking app</p>
        </header>

        <section className="mb-6 rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
          <h2 className="mb-3 text-lg font-semibold">New note</h2>
          <NoteForm submitLabel="Add note" onSubmit={handleCreate} />
        </section>

        {loading && <p className="py-4 text-center text-slate-500">Loading…</p>}
        {error && <p className="py-4 text-center text-red-600">{error}</p>}
        {!loading && !error && (
          <NoteList notes={notes} onUpdate={handleUpdate} onDelete={handleDelete} />
        )}
      </main>
    </div>
  )
}
