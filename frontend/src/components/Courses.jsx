import React, { useState, useEffect } from 'react'
import api from '../services/api'
import './Courses.css'

function Courses() {
  const [courses, setCourses] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [showForm, setShowForm] = useState(false)
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    instructor: ''
  })

  useEffect(() => {
    fetchCourses()
  }, [])

  const fetchCourses = async () => {
    try {
      setLoading(true)
      const response = await api.get('/api/courses')
      setCourses(response.data)
      setError('')
    } catch (err) {
      setError('Failed to fetch courses')
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    try {
      await api.post('/api/courses', formData)
      setSuccess('Course added successfully!')
      setFormData({ title: '', description: '', instructor: '' })
      setShowForm(false)
      fetchCourses()
    } catch (err) {
      setError('Failed to add course')
    }
  }

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this course?')) {
      try {
        await api.delete(`/api/courses/${id}`)
        setSuccess('Course deleted successfully!')
        fetchCourses()
      } catch (err) {
        setError('Failed to delete course')
      }
    }
  }

  if (loading) {
    return <div className="loading">Loading courses...</div>
  }

  return (
    <div className="courses-container">
      <div className="courses-header">
        <h1>Courses</h1>
        <button
          className="btn btn-primary"
          onClick={() => setShowForm(!showForm)}
        >
          {showForm ? 'Cancel' : 'Add New Course'}
        </button>
      </div>

      {error && <div className="error">{error}</div>}
      {success && <div className="success">{success}</div>}

      {showForm && (
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
              <input
                type="text"
                value={formData.instructor}
                onChange={(e) =>
                  setFormData({ ...formData, instructor: e.target.value })
                }
                required
              />
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
                <strong>Instructor:</strong> {course.instructor}
              </p>
              <button
                className="btn btn-danger btn-sm"
                onClick={() => handleDelete(course.id)}
              >
                Delete
              </button>
            </div>
          ))
        )}
      </div>
    </div>
  )
}

export default Courses

