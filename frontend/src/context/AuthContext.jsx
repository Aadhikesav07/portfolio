import React, { createContext, useState, useContext, useEffect } from 'react'
import api from '../services/api'

const AuthContext = createContext()

export function useAuth() {
  return useContext(AuthContext)
}

export function AuthProvider({ children }) {
  const [isAuthenticated, setIsAuthenticated] = useState(false)
  const [token, setToken] = useState(localStorage.getItem('token') || null)
  const [user, setUser] = useState(null)
  const [userRole, setUserRole] = useState(null)

  useEffect(() => {
    if (token) {
      setIsAuthenticated(true)
      api.defaults.headers.common['Authorization'] = `Bearer ${token}`
      fetchUserInfo()
    }
  }, [token])

  const fetchUserInfo = async () => {
    try {
      const response = await api.get('/api/users/me')
      setUser(response.data)
      setUserRole(response.data.role)
    } catch (error) {
      console.error('Failed to fetch user info:', error)
    }
  }

  const login = async (email, password) => {
    try {
      const response = await api.post('/api/users/login', null, {
        params: { email, password }
      })
      const jwt = response.data.jwt
      localStorage.setItem('token', jwt)
      setToken(jwt)
      setIsAuthenticated(true)
      api.defaults.headers.common['Authorization'] = `Bearer ${jwt}`
      await fetchUserInfo()
      return { success: true }
    } catch (error) {
      return {
        success: false,
        message: error.response?.data?.message || 'Login failed'
      }
    }
  }

  const register = async (email, password, fullName, role = 'ROLE_STUDENT') => {
    try {
      await api.post('/api/users/register', null, {
        params: { email, password, fullName, role }
      })
      return { success: true }
    } catch (error) {
      return {
        success: false,
        message: error.response?.data || 'Registration failed'
      }
    }
  }

  const logout = () => {
    localStorage.removeItem('token')
    setToken(null)
    setIsAuthenticated(false)
    setUser(null)
    setUserRole(null)
    delete api.defaults.headers.common['Authorization']
  }

  const isAdmin = () => userRole === 'ROLE_ADMIN'
  const isInstructor = () => userRole === 'ROLE_INSTRUCTOR'
  const isStudent = () => userRole === 'ROLE_STUDENT'

  const value = {
    isAuthenticated,
    token,
    user,
    userRole,
    isAdmin,
    isInstructor,
    isStudent,
    login,
    register,
    logout
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

