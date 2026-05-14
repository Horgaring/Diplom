import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { getUsers, AdminUser, PageResponse } from '../api/client'

export default function Users() {
  const navigate = useNavigate()
  const [data, setData] = useState<PageResponse<AdminUser> | null>(null)
  const [search, setSearch] = useState('')
  const [verifiedFilter, setVerifiedFilter] = useState('All')
  const [page, setPage] = useState(0)

  const load = () => {
    const verified = verifiedFilter === 'All' ? undefined : verifiedFilter === 'Verified'
    getUsers({ search: search || undefined, verified, page, size: 20 }).then(setData)
  }

  useEffect(() => { load() }, [page, verifiedFilter])

  return (
    <div>
      <h1>Users</h1>

      <div className="filters">
        <div className="form-group" style={{ marginBottom: 0 }}>
          <label>Search</label>
          <input value={search} onChange={e => setSearch(e.target.value)} placeholder="Email or name..." />
        </div>
        <div className="form-group" style={{ marginBottom: 0 }}>
          <label>Verified</label>
          <select value={verifiedFilter} onChange={e => { setVerifiedFilter(e.target.value); setPage(0) }}>
            <option>All</option>
            <option>Verified</option>
            <option>Not Verified</option>
          </select>
        </div>
        <button className="btn btn-primary" style={{ width: 'auto' }} onClick={() => { setPage(0); load() }}>Search</button>
      </div>

      <table>
        <thead>
          <tr>
            <th>Email</th><th>Name</th><th>Role</th><th>Active</th><th>Verified</th><th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {data?.content.map(u => (
            <tr key={u.id}>
              <td>{u.email}</td>
              <td>{u.firstName} {u.lastName}</td>
              <td>{u.role}</td>
              <td>{u.active ? 'Yes' : 'No'}</td>
              <td>{u.verified ? 'Yes' : 'No'}</td>
              <td>
                <button className="btn btn-sm btn-primary" onClick={() => navigate(`/admin/users/${u.id}`)}>View</button>
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
