import { Routes, Route, Navigate } from 'react-router-dom'
import { isAuthenticated } from './api/client'
import Login from './pages/Login'
import Dashboard from './pages/Dashboard'
import Users from './pages/Users'
import UserDetail from './pages/UserDetail'
import Messages from './pages/Messages'
import Cities from './pages/Cities'
import Layout from './components/Layout'

function ProtectedRoute({ children }: { children: React.ReactNode }) {
  if (!isAuthenticated()) {
    return <Navigate to="/admin/login" replace />
  }
  return <Layout>{children}</Layout>
}

export default function App() {
  return (
    <Routes>
      <Route path="/admin/login" element={<Login />} />
      <Route path="/admin/dashboard" element={<ProtectedRoute><Dashboard /></ProtectedRoute>} />
      <Route path="/admin/users" element={<ProtectedRoute><Users /></ProtectedRoute>} />
      <Route path="/admin/users/:id" element={<ProtectedRoute><UserDetail /></ProtectedRoute>} />
      <Route path="/admin/messages" element={<ProtectedRoute><Messages /></ProtectedRoute>} />
      <Route path="/admin/cities" element={<ProtectedRoute><Cities /></ProtectedRoute>} />
      <Route path="/admin" element={<Navigate to="/admin/dashboard" replace />} />
      <Route path="*" element={<Navigate to="/admin/dashboard" replace />} />
    </Routes>
  )
}
