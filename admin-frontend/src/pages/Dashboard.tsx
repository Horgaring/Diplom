import { useEffect, useState } from 'react'
import { getStats, AdminStats } from '../api/client'

export default function Dashboard() {
  const [stats, setStats] = useState<AdminStats | null>(null)

  useEffect(() => {
    getStats().then(setStats)
  }, [])

  if (!stats) return <div>Loading...</div>

  return (
    <div>
      <h1>Dashboard</h1>

      <div className="stats-grid">
        <div className="stat-card" style={{ background: '#3498db' }}>
          <h3>Total Users</h3>
          <div className="value">{stats.totalUsers}</div>
        </div>
        <div className="stat-card" style={{ background: '#27ae60' }}>
          <h3>Active</h3>
          <div className="value">{stats.activeUsers}</div>
        </div>
        <div className="stat-card" style={{ background: '#2980b9' }}>
          <h3>Verified</h3>
          <div className="value">{stats.verifiedUsers}</div>
        </div>
        <div className="stat-card" style={{ background: '#e74c3c' }}>
          <h3>Banned</h3>
          <div className="value">{stats.bannedUsers}</div>
        </div>
        <div className="stat-card" style={{ background: '#f39c12' }}>
          <h3>Matches</h3>
          <div className="value">{stats.totalMatches}</div>
        </div>
        <div className="stat-card" style={{ background: '#9b59b6' }}>
          <h3>Messages</h3>
          <div className="value">{stats.totalMessages}</div>
        </div>
        <div className="stat-card" style={{ background: '#1abc9c' }}>
          <h3>Today</h3>
          <div className="value">{stats.registrationsToday}</div>
        </div>
        <div className="stat-card" style={{ background: '#34495e' }}>
          <h3>This Month</h3>
          <div className="value">{stats.registrationsThisMonth}</div>
        </div>
      </div>

      <div style={{ display: 'flex', gap: '2rem' }}>
        <div style={{ flex: 1 }}>
          <h3>Gender</h3>
          <table>
            <thead>
              <tr><th>Gender</th><th>Count</th></tr>
            </thead>
            <tbody>
              {stats.genderEntries.map(g => (
                <tr key={g.key}><td>{g.key}</td><td>{g.value}</td></tr>
              ))}
            </tbody>
          </table>
        </div>
        <div style={{ flex: 1 }}>
          <h3>Top Cities</h3>
          <table>
            <thead>
              <tr><th>City</th><th>Users</th></tr>
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
