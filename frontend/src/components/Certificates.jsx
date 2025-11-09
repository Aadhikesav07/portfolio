import React, { useState, useEffect } from 'react'
import api from '../services/api'
import { useAuth } from '../context/AuthContext'
import './Certificates.css'

function Certificates() {
  const { isStudent } = useAuth()
  const [certificates, setCertificates] = useState([])
  const [courses, setCourses] = useState([])
  const [exams, setExams] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [showForm, setShowForm] = useState(false)
  const [formData, setFormData] = useState({
    courseId: '',
    examId: ''
  })

  useEffect(() => {
    fetchCertificates()
    fetchCourses()
  }, [])

  useEffect(() => {
    if (formData.courseId) {
      fetchExamsForCourse(formData.courseId)
    } else {
      setExams([])
    }
  }, [formData.courseId])

  const fetchCertificates = async () => {
    try {
      setLoading(true)
      const response = await api.get('/api/certificates')
      setCertificates(response.data)
      setError('')
    } catch (err) {
      setError('Failed to fetch certificates')
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  const fetchCourses = async () => {
    try {
      const response = await api.get('/api/courses')
      setCourses(response.data)
    } catch (err) {
      console.error(err)
    }
  }

  const fetchExamsForCourse = async (courseId) => {
    try {
      const response = await api.get(`/api/exams/course/${courseId}`)
      setExams(response.data)
    } catch (err) {
      console.error(err)
      setExams([])
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    try {
      await api.post('/api/certificates/issue', null, {
        params: {
          courseId: formData.courseId,
          examId: formData.examId
        }
      })
      setSuccess('Certificate issued successfully!')
      setFormData({ courseId: '', examId: '' })
      setShowForm(false)
      fetchCertificates()
    } catch (err) {
      setError('Failed to issue certificate')
    }
  }

  const getCourseName = (courseId) => {
    const course = courses.find((c) => c.id === courseId)
    return course ? course.title : courseId
  }

  if (loading) {
    return <div className="loading">Loading certificates...</div>
  }

  return (
    <div className="certificates-container">
      <div className="certificates-header">
        <h1>My Certificates</h1>
        <button
          className="btn btn-primary"
          onClick={() => setShowForm(!showForm)}
        >
          {showForm ? 'Cancel' : 'Request Certificate'}
        </button>
      </div>

      {error && <div className="error">{error}</div>}
      {success && <div className="success">{success}</div>}

      {showForm && (
        <div className="card">
          <h2>Request Certificate</h2>
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label>Course</label>
              <select
                value={formData.courseId}
                onChange={(e) =>
                  setFormData({ ...formData, courseId: e.target.value, examId: '' })
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
              <label>Exam</label>
              <select
                value={formData.examId}
                onChange={(e) =>
                  setFormData({ ...formData, examId: e.target.value })
                }
                required
                disabled={!formData.courseId || exams.length === 0}
              >
                <option value="">Select an exam</option>
                {exams.map((exam) => (
                  <option key={exam.id} value={exam.id}>
                    {exam.title}
                  </option>
                ))}
              </select>
            </div>
            <button type="submit" className="btn btn-primary">
              Issue Certificate
            </button>
          </form>
        </div>
      )}

      <div className="certificates-list">
        {certificates.length === 0 ? (
          <div className="no-certificates">
            You don't have any certificates yet
          </div>
        ) : (
          certificates.map((certificate) => (
            <div key={certificate.id} className="certificate-card">
              <div className="certificate-header">
                <h3>Certificate of Completion</h3>
              </div>
            <div className="certificate-body">
              <p>
                <strong>Course:</strong> {certificate.courseName || getCourseName(certificate.courseId)}
              </p>
              <p>
                <strong>Exam Score:</strong> {certificate.examMarks} marks
              </p>
              <p>
                <strong>Issued Date:</strong>{' '}
                {new Date(certificate.issueDate).toLocaleDateString()}
              </p>
              <p className="certificate-oid">
                <strong>Certificate OID:</strong> {certificate.oid || certificate.id}
              </p>
              {certificate.pdfPath && (
                <a
                  href={`/api/certificates/${certificate.id}/download`}
                  download
                  className="btn btn-primary btn-sm"
                  style={{ marginTop: '15px', display: 'inline-block' }}
                >
                  Download PDF
                </a>
              )}
            </div>
            </div>
          ))
        )}
      </div>
    </div>
  )
}

export default Certificates

