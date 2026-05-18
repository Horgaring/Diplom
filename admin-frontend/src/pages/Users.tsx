import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { getUsers, AdminUser, PageResponse } from '../api/client'

function avatarSrc(url: string | null): string | undefined {
  if (!url) return undefined
  if (url.startsWith('http') || url.startsWith('/uploads/')) return url
  return `/uploads${url.startsWith('/') ? '' : '/'}${url}`
}

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
      <h1>Пользователи</h1>

      <div className="filters">
        <div className="form-group" style={{ marginBottom: 0 }}>
          <label>Поиск</label>
          <input value={search} onChange={e => setSearch(e.target.value)} placeholder="Email или имя..." />
        </div>
        <div className="form-group" style={{ marginBottom: 0 }}>
          <label>Верификация</label>
          <select value={verifiedFilter} onChange={e => { setVerifiedFilter(e.target.value); setPage(0) }}>
            <option>Все</option>
            <option>Верифицированы</option>
            <option>Не верифицированы</option>
          </select>
        </div>
        <button className="btn btn-primary" style={{ width: 'auto' }} onClick={() => { setPage(0); load() }}>Поиск</button>
      </div>

      <table>
        <thead>
          <tr>
            <th>Аватар</th><th>Email</th><th>Имя</th><th>Роль</th><th>Активен</th><th>Верифицирован</th><th>Действия</th>
          </tr>
        </thead>
        <tbody>
          {data?.content.map(u => (
            <tr key={u.id}>
              <td>{avatarSrc(u.avatarUrl) ? <img src={avatarSrc(u.avatarUrl)} alt="" className="avatar-thumb" /> : '-'}</td>
              <td>{u.email}</td>
              <td>{u.firstName} {u.lastName}</td>
              <td>{u.role}</td>
              <td>{u.active ? 'Да' : 'Нет'}</td>
              <td>{u.verified ? 'Да' : 'Нет'}</td>
              <td>
                <button className="btn btn-sm btn-primary" onClick={() => navigate(`/admin/users/${u.id}`)}>Просмотр</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {data && (
        <div className="pagination">
          <button className="btn btn-sm" disabled={page <= 0} onClick={() => setPage(p => p - 1)}>← Назад</button>
          <span style={{ padding: '0.4rem 0.8rem' }}>Страница {data.number + 1} / {data.totalPages}</span>
          <button className="btn btn-sm" disabled={page >= data.totalPages - 1} onClick={() => setPage(p => p + 1)}>Далее →</button>
        </div>
      )}
    </div>
  )
}
