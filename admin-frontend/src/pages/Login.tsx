import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { login, setToken } from '../api/client'

export default function Login() {
  const [email, setEmail] = useState('admin@admin.com')
  const [password, setPassword] = useState('admin123')
  const [error, setError] = useState('')
  const navigate = useNavigate()

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    try {
      const res = await login({ email, password })
      setToken(res.token)
      navigate('/admin/dashboard')
    } catch {
      setError('Invalid email or password')
    }
  }

  return (
    <div className="login-page">
      <div className="login-card">
        <h1>Admin Panel</h1>
        {error && <div className="error-msg">{error}</div>}
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Email</label>
            <input
              type="email"
              value={email}
              onChange={e => setEmail(e.target.value)}
              required
            />
          </div>
          <div className="form-group">
            <label>Password</label>
            <input
              type="password"
              value={password}
              onChange={e => setPassword(e.target.value)}
              required
            />
          </div>
          <button type="submit" className="btn btn-primary">Sign In</button>
        </form>
      </div>
    </div>
  )
}
