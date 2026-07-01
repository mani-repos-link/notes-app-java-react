export interface Note {
  id: number
  title: string
  content: string
  createdAt: string
  updatedAt: string
}

export type NoteInput = Pick<Note, 'title' | 'content'>
