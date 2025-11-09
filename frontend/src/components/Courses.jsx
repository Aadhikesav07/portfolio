import React, { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import api from '../services/api'
import { useAuth } from '../context/AuthContext'
import './Courses.css'

function Courses() {
  const { isAdmin, user } = useAuth()
  const navigate = useNavigate()
  const [courses, setCourses] = useState([])
  const [instructors, setInstructors] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [showForm, setShowForm] = useState(false)
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    instructorId: ''
  })

  useEffect(() => {
    fetchCourses()
    if (isAdmin()) {
      fetchInstructors()
    }
  }, [isAdmin])

  const fetchCourses = async () => {
    try {
      setLoading(true)
      const response = await api.get('/api/courses')
      setCourses(response.data)
      setError('')
    } catch (err) {
      setError(err.response?.data || 'Failed to fetch courses')
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  const fetchInstructors = async () => {
    try {
      const response = await api.get('/api/users')
      const instructorUsers = response.data.filter(u => u.role === 'ROLE_INSTRUCTOR')
      setInstructors(instructorUsers)
    } catch (err) {
      console.error('Failed to fetch instructors:', err)
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!isAdmin()) {
      setError('Only ADMIN can create courses')
      return
    }
    try {
      const instructor = instructors.find(i => i.id === formData.instructorId)
      const courseData = {
        ...formData,
        instructorName: instructor ? instructor.fullName : ''
      }
      await api.post('/api/courses', courseData)
      setSuccess('Course added successfully!')
      setFormData({ title: '', description: '', instructorId: '' })
      setShowForm(false)
      fetchCourses()
    } catch (err) {
      setError(err.response?.data || 'Failed to add course')
    }
  }

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this course?')) {
      try {
        await api.delete(`/api/courses/${id}`)
        setSuccess('Course deleted successfully!')
        fetchCourses()
      } catch (err) {
        setError(err.response?.data || 'Failed to delete course')
      }
    }
  }

  const handleViewCourse = (courseId) => {
    navigate(`/course/${courseId}`)
  }

  if (loading) {
    return <div className="loading">Loading courses...</div>
  }

  return (
    <div className="courses-container">
      <div className="courses-header">
        <h1>Courses</h1>
        {isAdmin() && (
          <button
            className="btn btn-primary"
            onClick={() => setShowForm(!showForm)}
          >
            {showForm ? 'Cancel' : 'Add New Course'}
          </button>
        )}
      </div>

      {error && <div className="error">{error}</div>}
      {success && <div className="success">{success}</div>}

      {showForm && isAdmin() && (
        <div className="card">
          <h2>Add New Course</h2>
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label>Title</label>
              <input
                type="text"
                value={formData.title}
                onChange={(e) =>
                  setFormData({ ...formData, title: e.target.value })
                }
                required
              />
            </div>
            <div className="form-group">
              <label>Description</label>
              <textarea
                value={formData.description}
                onChange={(e) =>
                  setFormData({ ...formData, description: e.target.value })
                }
                required
              />
            </div>
            <div className="form-group">
              <label>Instructor</label>
              <select
                value={formData.instructorId}
                onChange={(e) =>
                  setFormData({ ...formData, instructorId: e.target.value })
                }
                required
              >
                <option value="">Select an instructor</option>
                {instructors.map((instructor) => (
                  <option key={instructor.id} value={instructor.id}>
                    {instructor.fullName} ({instructor.email})
                  </option>
                ))}
              </select>
            </div>
            <button type="submit" className="btn btn-primary">
              Add Course
            </button>
          </form>
        </div>
      )}

      <div className="courses-grid">
        {courses.length === 0 ? (
          <div className="no-courses">No courses available</div>
        ) : (
          courses.map((course) => (
            <div key={course.id} className="course-card">
              <h3>{course.title}</h3>
              <p className="course-description">{course.description}</p>
              <p className="course-instructor">
                <strong>Instructor:</strong> {course.instructorName || 'Not assigned'}
              </p>
              <div className="course-actions">
                <button
                  className="btn btn-primary btn-sm"
                  onClick={() => handleViewCourse(course.id)}
                >
                  View Details
                </button>
                {isAdmin() && (
                  <button
                    className="btn btn-danger btn-sm"
                    onClick={() => handleDelete(course.id)}
                  >
                    Delete
                  </button>
                )}
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  )
}

export default Courses

