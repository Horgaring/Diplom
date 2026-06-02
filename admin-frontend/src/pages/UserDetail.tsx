import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { getUserDetails, updateUserRole, toggleUserActive, verifyUser, deleteUser, updateUserProfile, getCities, AdminUser, City } from '../api/client'

function avatarSrc(url: string | null): string | undefined {
  if (!url) return undefined
  if (url.startsWith('http') || url.startsWith('/uploads/')) return url
  return `/uploads${url.startsWith('/') ? '' : '/'}${url}`
}

export default function UserDetail() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const [user, setUser] = useState<AdminUser | null>(null)
  const [role, setRole] = useState('')
  const [editing, setEditing] = useState(false)
  const [cities, setCities] = useState<City[]>([])

  const [form, setForm] = useState({
    firstName: '',
    lastName: '',
    bio: '',
    gender: '',
    birthDate: '',
    cityId: '',
    avatarUrl: '',
  })

  useEffect(() => {
    if (!id) return
    getUserDetails(id).then(u => { setUser(u); setRole(u.role) })
    getCities().then(setCities)
  }, [id])

  const startEdit = () => {
    if (!user) return
    setForm({
      firstName: user.firstName || '',
      lastName: user.lastName || '',
      bio: user.bio || '',
      gender: user.gender || '',
      birthDate: user.birthDate || '',
      cityId: user.cityId || '',
      avatarUrl: user.avatarUrl || '',
    })
    setEditing(true)
  }

  const cancelEdit = () => setEditing(false)

  const saveProfile = async () => {
    if (!id) return
    try {
      const data: Record<string, string | null> = {}
      if (form.firstName !== user?.firstName) data.firstName = form.firstName
      if (form.lastName !== user?.lastName) data.lastName = form.lastName
      if (form.bio !== (user?.bio ?? '')) data.bio = form.bio || null
      if (form.gender !== (user?.gender ?? '')) data.gender = form.gender || null
      if (form.birthDate !== (user?.birthDate ?? '')) data.birthDate = form.birthDate || null
      if (form.cityId !== (user?.cityId ?? '')) data.cityId = form.cityId || null
      if (form.avatarUrl !== (user?.avatarUrl ?? '')) data.avatarUrl = form.avatarUrl || null
      const updated = await updateUserProfile(id, data)
      setUser(updated)
      setRole(updated.role)
      setEditing(false)
    } catch { /* silent */ }
  }

  const handleUpdate = async (action: string) => {
    if (!id) return
    try {
      if (action === 'role') await updateUserRole(id, role)
      else if (action === 'active') await toggleUserActive(id, !user!.active)
      else if (action === 'verify') await verifyUser(id, !user!.verified)
      const updated = await getUserDetails(id)
      setUser(updated)
      setRole(updated.role)
    } catch { /* silent */ }
  }

  const handleDelete = async () => {
    if (!id || !confirm('Удалить этого пользователя?')) return
    await deleteUser(id)
    navigate('/admin/users')
  }

  if (!user) return <div className="loading">Загрузка...</div>

  if (editing) {
    return (
      <div className="detail-page">
        <div className="detail-header">
          {avatarSrc(user.avatarUrl) && <img src={avatarSrc(user.avatarUrl)} alt="Аватар" className="avatar-large" />}
          <div>
            <h1>Редактировать профиль</h1>
            <p className="detail-subtitle">{user.email}</p>
          </div>
        </div>

        <div className="card">
          <div className="edit-grid">
            <div className="form-group">
              <label>Имя</label>
              <input value={form.firstName} onChange={e => setForm(f => ({ ...f, firstName: e.target.value }))} />
            </div>
            <div className="form-group">
              <label>Фамилия</label>
              <input value={form.lastName} onChange={e => setForm(f => ({ ...f, lastName: e.target.value }))} />
            </div>
            <div className="form-group">
              <label>О себе</label>
              <textarea value={form.bio} onChange={e => setForm(f => ({ ...f, bio: e.target.value }))} rows={3} />
            </div>
            <div className="form-group">
              <label>Пол</label>
              <select value={form.gender} onChange={e => setForm(f => ({ ...f, gender: e.target.value }))}>
                <option value="">—</option>
                <option value="Male">Мужской</option>
                <option value="Female">Женский</option>
              </select>
            </div>
            <div className="form-group">
              <label>Дата рождения</label>
              <input type="date" value={form.birthDate} onChange={e => setForm(f => ({ ...f, birthDate: e.target.value }))} />
            </div>
            <div className="form-group">
              <label>Город</label>
              <select value={form.cityId} onChange={e => setForm(f => ({ ...f, cityId: e.target.value }))}>
                <option value="">—</option>
                {cities.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
              </select>
            </div>
            <div className="form-group" style={{ gridColumn: '1 / -1' }}>
              <label>URL аватара</label>
              <input value={form.avatarUrl} onChange={e => setForm(f => ({ ...f, avatarUrl: e.target.value }))} placeholder="http://..." />
            </div>
          </div>

          <div style={{ display: 'flex', gap: '0.5rem', marginTop: '1.5rem' }}>
            <button className="btn btn-primary" onClick={saveProfile}>💾 Сохранить</button>
            <button className="btn btn-secondary" onClick={cancelEdit}>Отмена</button>
          </div>
        </div>
      </div>
    )
  }

  return (
    <div className="detail-page">
      <div className="detail-header">
        {avatarSrc(user.avatarUrl) && <img src={avatarSrc(user.avatarUrl)} alt="Аватар" className="avatar-large" />}
        <div>
          <h1>{user.firstName} {user.lastName}</h1>
          <p className="detail-subtitle">{user.email}</p>
        </div>
        <div className="detail-badges">
          <span className={`badge ${user.role === 'ADMIN' ? 'badge-admin' : 'badge-user'}`}>{user.role}</span>
          <span className={`badge ${user.active ? 'badge-active' : 'badge-inactive'}`}>{user.active ? 'Активен' : 'Неактивен'}</span>
          <span className={`badge ${user.verified ? 'badge-verified' : 'badge-unverified'}`}>{user.verified ? 'Верифицирован' : 'Не верифицирован'}</span>
        </div>
      </div>

      <div className="card">
        <h3 className="card-title">Информация профиля</h3>
        <div className="info-grid">
          <div className="info-item"><span className="label">Имя</span><span className="value">{user.firstName} {user.lastName}</span></div>
          <div className="info-item"><span className="label">Email</span><span className="value">{user.email}</span></div>
          <div className="info-item"><span className="label">Пол</span><span className="value">{user.gender || '-'}</span></div>
          <div className="info-item"><span className="label">Дата рождения</span><span className="value">{user.birthDate || '-'}</span></div>
          <div className="info-item"><span className="label">Город</span><span className="value">{user.cityName || '-'}</span></div>
          <div className="info-item"><span className="label">О себе</span><span className="value">{user.bio || '-'}</span></div>
          <div className="info-item"><span className="label">Создан</span><span className="value">{new Date(user.createdAt).toLocaleString()}</span></div>
        </div>
      </div>

      <div className="card">
        <h3 className="card-title">Действия</h3>
        <div className="action-groups">
          <div className="action-group">
            <h4 className="action-group-title">Профиль</h4>
            <button className="btn btn-outline" onClick={startEdit}>✏️ Редактировать</button>
          </div>

          <div className="action-group">
            <h4 className="action-group-title">Роль</h4>
            <div className="action-inline">
              <select value={role} onChange={e => setRole(e.target.value)} className="action-select">
                <option>USER</option>
                <option>ADMIN</option>
              </select>
              <button className="btn btn-outline" onClick={() => handleUpdate('role')}>Изменить</button>
            </div>
          </div>

          <div className="action-group">
            <h4 className="action-group-title">Статус</h4>
            <div className="action-row">
              <button className={`btn ${user.active ? 'btn-warning' : 'btn-success'}`} onClick={() => handleUpdate('active')}>
                {user.active ? '🔒 Заблокировать' : '🔓 Разблокировать'}
              </button>
              <button className={`btn ${user.verified ? 'btn-warning' : 'btn-success'}`} onClick={() => handleUpdate('verify')}>
                {user.verified ? '⛔ Отменить верификацию' : '✅ Верифицировать'}
              </button>
            </div>
          </div>

          <div className="action-group action-group-danger">
            <button className="btn btn-danger" onClick={handleDelete}>🗑️ Удалить пользователя</button>
          </div>

          <div className="action-group">
            <button className="btn btn-secondary" onClick={() => navigate('/admin/users')}>← Назад к списку</button>
          </div>
        </div>
      </div>
    </div>
  )
}
