const API_BASE = '/api'

function getToken(): string | null {
  return localStorage.getItem('admin_token')
}

export function setToken(token: string) {
  localStorage.setItem('admin_token', token)
}

export function clearToken() {
  localStorage.removeItem('admin_token')
}

export function isAuthenticated(): boolean {
  return !!getToken()
}

async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  const token = getToken()
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...(options.headers as Record<string, string>),
  }
  if (token) {
    headers['Authorization'] = `Bearer ${token}`
  }

  const res = await fetch(`${API_BASE}${path}`, { ...options, headers })

  if (res.status === 403) {
    clearToken()
    window.location.href = '/admin/login'
    throw new Error('Unauthorized')
  }

  const contentType = res.headers.get('content-type')
  if (contentType?.includes('application/json')) {
    return res.json()
  }
  return res.text() as T
}

export interface AuthResponse {
  token: string
  userId: string
  email: string
  firstName: string
}

export interface LoginRequest {
  email: string
  password: string
}

export function login(data: LoginRequest): Promise<AuthResponse> {
  return request<AuthResponse>('/auth/login', {
    method: 'POST',
    body: JSON.stringify(data),
  })
}

export interface AdminStats {
  totalUsers: number
  activeUsers: number
  verifiedUsers: number
  bannedUsers: number
  totalMatches: number
  totalMessages: number
  registrationsToday: number
  registrationsThisWeek: number
  registrationsThisMonth: number
  genderEntries: { key: string; value: number }[]
  cityEntries: { key: string; value: number }[]
}

export function getStats(): Promise<AdminStats> {
  return request<AdminStats>('/admin/stats')
}

export interface AdminUser {
  id: string
  email: string
  firstName: string
  lastName: string
  role: string
  active: boolean
  verified: boolean
  gender: string | null
  birthDate: string | null
  bio: string | null
  avatarUrl: string | null
  cityName: string | null
  createdAt: string
}

export interface PageResponse<T> {
  content: T[]
  totalPages: number
  totalElements: number
  number: number
  size: number
}

export function getUsers(params: {
  search?: string
  verified?: boolean
  page?: number
  size?: number
}): Promise<PageResponse<AdminUser>> {
  const q = new URLSearchParams()
  if (params.search) q.set('search', params.search)
  if (params.verified !== undefined) q.set('verified', String(params.verified))
  q.set('page', String(params.page ?? 0))
  q.set('size', String(params.size ?? 20))
  return request<PageResponse<AdminUser>>(`/admin/users?${q}`)
}

export function getUserDetails(userId: string): Promise<AdminUser> {
  return request<AdminUser>(`/admin/users/${userId}`)
}

export function updateUserRole(userId: string, role: string): Promise<AdminUser> {
  return request<AdminUser>(`/admin/users/${userId}/role`, {
    method: 'PUT',
    body: JSON.stringify({ role }),
  })
}

export function toggleUserActive(userId: string, active: boolean): Promise<AdminUser> {
  return request<AdminUser>(`/admin/users/${userId}/active`, {
    method: 'PUT',
    body: JSON.stringify({ active }),
  })
}

export function verifyUser(userId: string, verified: boolean): Promise<AdminUser> {
  return request<AdminUser>(`/admin/users/${userId}/verify`, {
    method: 'PUT',
    body: JSON.stringify({ verified }),
  })
}

export function deleteUser(userId: string): Promise<void> {
  return request<void>(`/admin/users/${userId}`, { method: 'DELETE' })
}

export interface AdminMessage {
  id: string
  chatRoomId: string
  senderId: string
  senderFirstName: string
  content: string
  read: boolean
  createdAt: string
}

export function getMessages(page = 0, size = 20): Promise<PageResponse<AdminMessage>> {
  return request<PageResponse<AdminMessage>>(`/admin/messages?page=${page}&size=${size}`)
}

export function deleteMessage(messageId: string): Promise<void> {
  return request<void>(`/admin/messages/${messageId}`, { method: 'DELETE' })
}

export interface City {
  id: string
  name: string
}

export function getCities(): Promise<City[]> {
  return request<City[]>('/admin/cities')
}

export function createCity(name: string): Promise<City> {
  return request<City>('/admin/cities', {
    method: 'POST',
    body: JSON.stringify({ name }),
  })
}

export function updateCity(cityId: string, name: string): Promise<City> {
  return request<City>(`/admin/cities/${cityId}`, {
    method: 'PUT',
    body: JSON.stringify({ name }),
  })
}

export function deleteCity(cityId: string): Promise<void> {
  return request<void>(`/admin/cities/${cityId}`, { method: 'DELETE' })
}
