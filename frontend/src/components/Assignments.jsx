import React, { useState, useEffect } from 'react'
import api from '../services/api'
import { useAuth } from '../context/AuthContext'
import './Assignments.css'

function Assignments() {
  const { isAdmin, isInstructor, isStudent } = useAuth()
  const [assignments, setAssignments] = useState([])
  const [courses, setCourses] = useState([])
  const [selectedCourse, setSelectedCourse] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [showForm, setShowForm] = useState(false)
  const [formData, setFormData] = useState({
    courseId: '',
    title: '',
    description: '',
    instructions: '',
    isMandatory: true,
    maxMarks: 100,
    dueDate: ''
  })

  useEffect(() => {
    fetchCourses()
  }, [])

  useEffect(() => {
    if (selectedCourse) {
      fetchAssignments(selectedCourse)
    } else {
      setAssignments([])
    }
  }, [selectedCourse])

  const fetchCourses = async () => {
    try {
      const response = await api.get('/api/courses')
      setCourses(response.data)
      if (response.data.length > 0 && !selectedCourse) {
        setSelectedCourse(response.data[0].id)
      }
    } catch (err) {
      setError('Failed to fetch courses')
    }
  }

  const fetchAssignments = async (courseId) => {
    try {
      setLoading(true)
      const response = await api.get(`/api/assignments/course/${courseId}`)
      setAssignments(response.data)
      setError('')
    } catch (err) {
      setError('Failed to fetch assignments')
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!isAdmin() && !isInstructor()) {
      setError('Only ADMIN and INSTRUCTOR can create assignments')
      return
    }

    try {
      const assignmentData = {
        ...formData,
        dueDate: new Date(formData.dueDate).toISOString()
      }
      await api.post('/api/assignments', assignmentData)
      setSuccess('Assignment created successfully!')
      setFormData({
        courseId: '',
        title: '',
        description: '',
        instructions: '',
        isMandatory: true,
        maxMarks: 100,
        dueDate: ''
      })
      setShowForm(false)
      if (assignmentData.courseId) {
        fetchAssignments(assignmentData.courseId)
      }
    } catch (err) {
      setError(err.response?.data || 'Failed to create assignment')
    }
  }

  const handleSubmitAssignment = async (assignmentId) => {
    const submissionContent = prompt('Enter your submission (or file path):')
    if (!submissionContent) return

    try {
      await api.post('/api/assignment-submissions/submit', {
        assignmentId,
        submissionContent
      })
      setSuccess('Assignment submitted successfully!')
    } catch (err) {
      setError(err.response?.data || 'Failed to submit assignment')
    }
  }

  if (loading && assignments.length === 0) {
    return <div className="loading">Loading assignments...</div>
  }

  return (
    <div className="assignments-container">
      <div className="assignments-header">
        <h1>Assignments</h1>
        {(isAdmin() || isInstructor()) && (
          <button
            className="btn btn-primary"
            onClick={() => setShowForm(!showForm)}
          >
            {showForm ? 'Cancel' : 'Create Assignment'}
          </button>
        )}
      </div>

      {error && <div className="error">{error}</div>}
      {success && <div className="success">{success}</div>}

      {showForm && (isAdmin() || isInstructor()) && (
        <div className="card">
          <h2>Create New Assignment</h2>
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label>Course</label>
              <select
                value={formData.courseId}
                onChange={(e) =>
                  setFormData({ ...formData, courseId: e.target.value })
                }
                required
              >
                <option value="">Select a course</option>
                {courses.map((course) => (
                  <option key={course.id} value={course.id}>
                    {course.title}
                  </option>
                ))}
              </select>
            </div>
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
              <label>Instructions</label>
              <textarea
                value={formData.instructions}
                onChange={(e) =>
                  setFormData({ ...formData, instructions: e.target.value })
                }
              />
            </div>
            <div className="form-group">
              <label>
                <input
                  type="checkbox"
                  checked={formData.isMandatory}
                  onChange={(e) =>
                    setFormData({ ...formData, isMandatory: e.target.checked })
                  }
                />
                Mandatory Assignment
              </label>
            </div>
            <div className="form-group">
              <label>Maximum Marks</label>
              <input
                type="number"
                value={formData.maxMarks}
                onChange={(e) =>
                  setFormData({ ...formData, maxMarks: parseInt(e.target.value) })
                }
                required
                min="1"
              />
            </div>
            <div className="form-group">
              <label>Due Date</label>
              <input
                type="datetime-local"
                value={formData.dueDate}
                onChange={(e) =>
                  setFormData({ ...formData, dueDate: e.target.value })
                }
                required
              />
            </div>
            <button type="submit" className="btn btn-primary">
              Create Assignment
            </button>
          </form>
        </div>
      )}

      <div className="card">
        <div className="form-group">
          <label>Select Course</label>
          <select
            value={selectedCourse}
            onChange={(e) => setSelectedCourse(e.target.value)}
          >
            {courses.map((course) => (
              <option key={course.id} value={course.id}>
                {course.title}
              </option>
            ))}
          </select>
        </div>
      </div>

      <div className="assignments-list">
        {assignments.length === 0 ? (
          <div className="no-assignments">No assignments available for this course</div>
        ) : (
          assignments.map((assignment) => (
            <div key={assignment.id} className="assignment-card">
              <div className="assignment-header">
                <h3>{assignment.title}</h3>
                {assignment.isMandatory && (
                  <span className="mandatory-badge">Mandatory</span>
                )}
              </div>
              <p className="assignment-description">{assignment.description}</p>
              {assignment.instructions && (
                <p className="assignment-instructions">
                  <strong>Instructions:</strong> {assignment.instructions}
                </p>
              )}
              <p className="assignment-details">
                <strong>Max Marks:</strong> {assignment.maxMarks} | 
                <strong> Due Date:</strong> {new Date(assignment.dueDate).toLocaleString()}
              </p>
              {isStudent() && (
                <button
                  className="btn btn-primary btn-sm"
                  onClick={() => handleSubmitAssignment(assignment.id)}
                >
                  Submit Assignment
                </button>
              )}
            </div>
          ))
        )}
      </div>
    </div>
  )
}

export default Assignments

