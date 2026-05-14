import { NavLink, useNavigate } from 'react-router-dom'
import { clearToken } from '../api/client'

export default function Layout({ children }: { children: React.ReactNode }) {
  const navigate = useNavigate()

  const handleLogout = () => {
    clearToken()
    navigate('/admin/login')
  }

  return (
    <div className="layout">
      <div className="sidebar">
        <h2>Admin Panel</h2>
        <nav>
          <NavLink to="/admin/dashboard" className={({ isActive }) => isActive ? 'active' : ''}>
            Dashboard
          </NavLink>
          <NavLink to="/admin/users" className={({ isActive }) => isActive ? 'active' : ''}>
            Users
          </NavLink>
          <NavLink to="/admin/messages" className={({ isActive }) => isActive ? 'active' : ''}>
            Messages
          </NavLink>
          <NavLink to="/admin/cities" className={({ isActive }) => isActive ? 'active' : ''}>
            Cities
          </NavLink>
        </nav>
        <button className="logout-btn" onClick={handleLogout}>Logout</button>
      </div>
      <div className="main-content">
        {children}
      </div>
    </div>
  )
}
