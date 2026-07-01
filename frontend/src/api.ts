import type { Note, NoteInput } from './types'

const BASE_URL = '/api/notes'

async function request<T>(url: string, options?: RequestInit): Promise<T> {
  const response = await fetch(url, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  })

  if (!response.ok) {
    throw new Error(await readError(response))
  }
  return (response.status === 204 ? null : await response.json()) as T
}

async function readError(response: Response): Promise<string> {
  try {
    const body = await response.json()
    if (body.fieldErrors) {
      return Object.entries(body.fieldErrors)
        .map(([field, reason]) => `${field}: ${reason}`)
        .join('; ')
    }
    return body.message ?? `Request failed (${response.status})`
  } catch {
    return `Request failed (${response.status})`
  }
}

export const notesApi = {
  list: () => request<Note[]>(BASE_URL),
  create: (note: NoteInput) => request<Note>(
      BASE_URL,
      { method: 'POST', body: JSON.stringify(note) }
  ),
  update: (id: number, note: NoteInput) =>
    request<Note>(
        `${BASE_URL}/${id}`,
        { method: 'PUT', body: JSON.stringify(note) }
    ),
  remove: (id: number) => request<void>(
      `${BASE_URL}/${id}`,
      { method: 'DELETE' }
  ),
}
