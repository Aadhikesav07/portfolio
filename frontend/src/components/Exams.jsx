import React, { useState, useEffect } from 'react'
import api from '../services/api'
import './Exams.css'

function Exams() {
  const [exams, setExams] = useState([])
  const [courses, setCourses] = useState([])
  const [selectedCourse, setSelectedCourse] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [showForm, setShowForm] = useState(false)
  const [formData, setFormData] = useState({
    courseId: '',
    title: '',
    examDate: '',
    questions: ''
  })

  useEffect(() => {
    fetchCourses()
  }, [])

  useEffect(() => {
    if (selectedCourse) {
      fetchExams(selectedCourse)
    } else {
      setExams([])
    }
  }, [selectedCourse])

  const fetchCourses = async () => {
    try {
      const response = await api.get('/api/courses')
      setCourses(response.data)
      if (response.data.length > 0) {
        setSelectedCourse(response.data[0].id)
      }
    } catch (err) {
      setError('Failed to fetch courses')
    }
  }

  const fetchExams = async (courseId) => {
    try {
      setLoading(true)
      const response = await api.get(`/api/exams/course/${courseId}`)
      setExams(response.data)
      setError('')
    } catch (err) {
      setError('Failed to fetch exams')
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    try {
      const examData = {
        ...formData,
        questions: formData.questions.split('\n').filter((q) => q.trim())
      }
      await api.post('/api/exams', examData)
      setSuccess('Exam added successfully!')
      setFormData({ courseId: '', title: '', examDate: '', questions: '' })
      setShowForm(false)
      if (formData.courseId) {
        fetchExams(formData.courseId)
      }
    } catch (err) {
      setError('Failed to add exam')
    }
  }

  if (loading && exams.length === 0) {
    return <div className="loading">Loading exams...</div>
  }

  return (
    <div className="exams-container">
      <div className="exams-header">
        <h1>Exams</h1>
        <button
          className="btn btn-primary"
          onClick={() => setShowForm(!showForm)}
        >
          {showForm ? 'Cancel' : 'Add New Exam'}
        </button>
      </div>

      {error && <div className="error">{error}</div>}
      {success && <div className="success">{success}</div>}

      {showForm && (
        <div className="card">
          <h2>Add New Exam</h2>
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
              <label>Exam Date</label>
              <input
                type="datetime-local"
                value={formData.examDate}
                onChange={(e) =>
                  setFormData({ ...formData, examDate: e.target.value })
                }
                required
              />
            </div>
            <div className="form-group">
              <label>Questions (one per line)</label>
              <textarea
                value={formData.questions}
                onChange={(e) =>
                  setFormData({ ...formData, questions: e.target.value })
                }
                placeholder="Enter questions, one per line"
                required
              />
            </div>
            <button type="submit" className="btn btn-primary">
              Add Exam
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

      <div className="exams-list">
        {exams.length === 0 ? (
          <div className="no-exams">No exams available for this course</div>
        ) : (
          exams.map((exam) => (
            <div key={exam.id} className="exam-card">
              <h3>{exam.title}</h3>
              <p className="exam-date">
                <strong>Date:</strong>{' '}
                {new Date(exam.examDate).toLocaleString()}
              </p>
              <div className="exam-questions">
                <strong>Questions:</strong>
                <ul>
                  {exam.questions?.map((question, index) => (
                    <li key={index}>{question}</li>
                  ))}
                </ul>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  )
}

export default Exams

