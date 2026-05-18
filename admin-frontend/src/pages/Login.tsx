import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { login, setToken, clearToken } from '../api/client'

export default function Login() {
  const [email, setEmail] = useState('admin@admin.com')
  const [password, setPassword] = useState('admin123')
  const [error, setError] = useState('')
  const navigate = useNavigate()

  useEffect(() => { clearToken() }, [])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    try {
      const res = await login({ email, password })
      console.log(res)
      setToken(res.token)
      navigate('/admin/dashboard')
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Неизвестная ошибка')
    }
  }

  return (
    <div className="login-page">
      <div className="login-card">
        <h1>Панель администратора</h1>
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
            <label>Пароль</label>
            <input
              type="password"
              value={password}
              onChange={e => setPassword(e.target.value)}
              required
            />
          </div>
          <button type="submit" className="btn btn-primary">Войти</button>
        </form>
      </div>
    </div>
  )
}
