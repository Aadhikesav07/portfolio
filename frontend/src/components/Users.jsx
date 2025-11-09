import React, { useState, useEffect } from 'react'
import api from '../services/api'
import { useAuth } from '../context/AuthContext'
import './Users.css'

function Users() {
  const { isAdmin } = useAuth()
  const [users, setUsers] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [showForm, setShowForm] = useState(false)
  const [formData, setFormData] = useState({
    email: '',
    password: '',
    fullName: ''
  })

  useEffect(() => {
    if (isAdmin()) {
      fetchUsers()
    }
  }, [isAdmin])

  const fetchUsers = async () => {
    try {
      setLoading(true)
      const response = await api.get('/api/users')
      setUsers(response.data)
      setError('')
    } catch (err) {
      setError(err.response?.data || 'Failed to fetch users')
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  const handleCreateInstructor = async (e) => {
    e.preventDefault()
    try {
      await api.post('/api/users/create-instructor', null, {
        params: formData
      })
      setSuccess('Instructor created successfully!')
      setFormData({ email: '', password: '', fullName: '' })
      setShowForm(false)
      fetchUsers()
    } catch (err) {
      setError(err.response?.data || 'Failed to create instructor')
    }
  }

  if (!isAdmin()) {
    return <div className="error">Only ADMIN can access this page</div>
  }

  if (loading) {
    return <div className="loading">Loading users...</div>
  }

  return (
    <div className="users-container">
      <div className="users-header">
        <h1>User Management</h1>
        <button
          className="btn btn-primary"
          onClick={() => setShowForm(!showForm)}
        >
          {showForm ? 'Cancel' : 'Create Instructor'}
        </button>
      </div>

      {error && <div className="error">{error}</div>}
      {success && <div className="success">{success}</div>}

      {showForm && (
        <div className="card">
          <h2>Create New Instructor</h2>
          <form onSubmit={handleCreateInstructor}>
            <div className="form-group">
              <label>Full Name</label>
              <input
                type="text"
                value={formData.fullName}
                onChange={(e) =>
                  setFormData({ ...formData, fullName: e.target.value })
                }
                required
              />
            </div>
            <div className="form-group">
              <label>Email</label>
              <input
                type="email"
                value={formData.email}
                onChange={(e) =>
                  setFormData({ ...formData, email: e.target.value })
                }
                required
              />
            </div>
            <div className="form-group">
              <label>Password</label>
              <input
                type="password"
                value={formData.password}
                onChange={(e) =>
                  setFormData({ ...formData, password: e.target.value })
                }
                required
              />
            </div>
            <button type="submit" className="btn btn-primary">
              Create Instructor
            </button>
          </form>
        </div>
      )}

      <div className="users-list">
        <h2>All Users</h2>
        <div className="users-table">
          <div className="table-header">
            <div>Name</div>
            <div>Email</div>
            <div>Role</div>
            <div>Created</div>
          </div>
          {users.map((user) => (
            <div key={user.id} className="table-row">
              <div>{user.fullName}</div>
              <div>{user.email}</div>
              <div>
                <span className={`role-badge ${user.role?.toLowerCase().replace('role_', '')}`}>
                  {user.role?.replace('ROLE_', '')}
                </span>
              </div>
              <div>{new Date(user.createdAt).toLocaleDateString()}</div>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}

export default Users

