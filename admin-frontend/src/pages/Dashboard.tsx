import { useEffect, useState } from 'react'
import { getStats, AdminStats } from '../api/client'

export default function Dashboard() {
  const [stats, setStats] = useState<AdminStats | null>(null)

  useEffect(() => {
    getStats().then(setStats)
  }, [])

  if (!stats) return <div>Загрузка...</div>

  return (
    <div>
      <h1>Панель управления</h1>

      <div className="stats-grid">
        <div className="stat-card" style={{ background: '#3498db' }}>
          <h3>Всего пользователей</h3>
          <div className="value">{stats.totalUsers}</div>
        </div>
        <div className="stat-card" style={{ background: '#27ae60' }}>
          <h3>Активны</h3>
          <div className="value">{stats.activeUsers}</div>
        </div>
        <div className="stat-card" style={{ background: '#2980b9' }}>
          <h3>Верифицированы</h3>
          <div className="value">{stats.verifiedUsers}</div>
        </div>
        <div className="stat-card" style={{ background: '#e74c3c' }}>
          <h3>Заблокированы</h3>
          <div className="value">{stats.bannedUsers}</div>
        </div>
        <div className="stat-card" style={{ background: '#f39c12' }}>
          <h3>Мэтчи</h3>
          <div className="value">{stats.totalMatches}</div>
        </div>
        <div className="stat-card" style={{ background: '#9b59b6' }}>
          <h3>Сообщения</h3>
          <div className="value">{stats.totalMessages}</div>
        </div>
        <div className="stat-card" style={{ background: '#1abc9c' }}>
          <h3>Сегодня</h3>
          <div className="value">{stats.registrationsToday}</div>
        </div>
        <div className="stat-card" style={{ background: '#34495e' }}>
          <h3>В этом месяце</h3>
          <div className="value">{stats.registrationsThisMonth}</div>
        </div>
      </div>

      <div style={{ display: 'flex', gap: '2rem' }}>
        <div style={{ flex: 1 }}>
          <h3>Пол</h3>
          <table>
            <thead>
              <tr><th>Пол</th><th>Количество</th></tr>
            </thead>
            <tbody>
              {stats.genderEntries.map(g => (
                <tr key={g.key}><td>{g.key}</td><td>{g.value}</td></tr>
              ))}
            </tbody>
          </table>
        </div>
        <div style={{ flex: 1 }}>
          <h3>Города</h3>
          <table>
            <thead>
              <tr><th>Город</th><th>Пользователи</th></tr>
            </thead>
            <tbody>
              {stats.cityEntries.map(c => (
                <tr key={c.key}><td>{c.key}</td><td>{c.value}</td></tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}
