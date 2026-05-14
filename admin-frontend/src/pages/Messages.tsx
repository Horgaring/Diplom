import { useEffect, useState } from 'react'
import { getMessages, deleteMessage, AdminMessage, PageResponse } from '../api/client'

export default function Messages() {
  const [data, setData] = useState<PageResponse<AdminMessage> | null>(null)
  const [page, setPage] = useState(0)

  useEffect(() => {
    getMessages(page, 20).then(setData)
  }, [page])

  const handleDelete = async (id: string) => {
    if (!confirm('Delete this message?')) return
    await deleteMessage(id)
    getMessages(page, 20).then(setData)
  }

  return (
    <div>
      <h1>Messages</h1>

      <table>
        <thead>
          <tr>
            <th>Sender</th><th>Content</th><th>Sent</th><th>Read</th><th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {data?.content.map(m => (
            <tr key={m.id}>
              <td>{m.senderFirstName}</td>
              <td>{m.content}</td>
              <td>{new Date(m.createdAt).toLocaleString()}</td>
              <td>{m.read ? 'Yes' : 'No'}</td>
              <td>
                <button className="btn btn-sm btn-danger" onClick={() => handleDelete(m.id)}>Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {data && (
        <div className="pagination">
          <button className="btn btn-sm" disabled={page <= 0} onClick={() => setPage(p => p - 1)}>← Prev</button>
          <span style={{ padding: '0.4rem 0.8rem' }}>Page {data.number + 1} / {data.totalPages}</span>
          <button className="btn btn-sm" disabled={page >= data.totalPages - 1} onClick={() => setPage(p => p + 1)}>Next →</button>
        </div>
      )}
    </div>
  )
}
