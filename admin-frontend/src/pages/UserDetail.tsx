import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { getUserDetails, updateUserRole, toggleUserActive, verifyUser, deleteUser, AdminUser } from '../api/client'

export default function UserDetail() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const [user, setUser] = useState<AdminUser | null>(null)
  const [role, setRole] = useState('')

  useEffect(() => {
    if (!id) return
    getUserDetails(id).then(u => { setUser(u); setRole(u.role) })
  }, [id])

  if (!user) return <div>Loading...</div>

  const handleUpdate = async (action: string) => {
    if (!id) return
    try {
      if (action === 'role') await updateUserRole(id, role)
      else if (action === 'active') await toggleUserActive(id, !user.active)
      else if (action === 'verify') await verifyUser(id, !user.verified)
      const updated = await getUserDetails(id)
      setUser(updated)
      setRole(updated.role)
    } catch { /* silent */ }
  }

  const handleDelete = async () => {
    if (!id || !confirm('Delete this user?')) return
    await deleteUser(id)
    navigate('/admin/users')
  }

  return (
    <div>
      <h1>User: {user.email}</h1>

      <div className="info-grid">
        <div><span className="label">Name:</span> {user.firstName} {user.lastName}</div>
        <div><span className="label">Email:</span> {user.email}</div>
        <div><span className="label">Role:</span> {user.role}</div>
        <div><span className="label">Active:</span> {user.active ? 'Yes' : 'No'}</div>
        <div><span className="label">Verified:</span> {user.verified ? 'Yes' : 'No'}</div>
        <div><span className="label">Gender:</span> {user.gender || '-'}</div>
        <div><span className="label">Birth Date:</span> {user.birthDate || '-'}</div>
        <div><span className="label">City:</span> {user.cityName || '-'}</div>
        <div><span className="label">Bio:</span> {user.bio || '-'}</div>
        <div><span className="label">Created:</span> {new Date(user.createdAt).toLocaleString()}</div>
      </div>

      <div className="actions-bar">
        <div className="form-group" style={{ marginBottom: 0 }}>
          <label>Role</label>
          <select value={role} onChange={e => setRole(e.target.value)}>
            <option>USER</option>
            <option>ADMIN</option>
          </select>
        </div>
        <button className="btn btn-sm btn-primary" onClick={() => handleUpdate('role')} style={{ marginTop: '1.4rem' }}>Change Role</button>
        <button
          className={`btn btn-sm ${user.active ? 'btn-danger' : 'btn-success'}`}
          onClick={() => handleUpdate('active')}
          style={{ marginTop: '1.4rem' }}
        >
          {user.active ? 'Ban' : 'Unban'}
        </button>
        <button className="btn btn-sm btn-primary" onClick={() => handleUpdate('verify')} style={{ marginTop: '1.4rem' }}>
          {user.verified ? 'Unverify' : 'Verify'}
        </button>
        <button className="btn btn-sm btn-danger" onClick={handleDelete} style={{ marginTop: '1.4rem' }}>Delete User</button>
        <button className="btn btn-sm" onClick={() => navigate('/admin/users')} style={{ marginTop: '1.4rem' }}>← Back</button>
      </div>
    </div>
  )
}
