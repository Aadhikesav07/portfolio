import React, { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import api from '../services/api'
import { useAuth } from '../context/AuthContext'
import './Exams.css'

function Exams() {
  const { isAdmin, isInstructor, isStudent } = useAuth()
  const navigate = useNavigate()
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
    description: '',
    examDate: '',
    endDate: '',
    durationMinutes: 60,
    minimumMarksRequired: 0,
    questions: [{ questionText: '', optionA: '', optionB: '', optionC: '', optionD: '', correctAnswer: 'A', marks: 1 }]
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
      if (response.data.length > 0 && !selectedCourse) {
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

  const handleAddQuestion = () => {
    setFormData({
      ...formData,
      questions: [...formData.questions, { questionText: '', optionA: '', optionB: '', optionC: '', optionD: '', correctAnswer: 'A', marks: 1 }]
    })
  }

  const handleQuestionChange = (index, field, value) => {
    const newQuestions = [...formData.questions]
    newQuestions[index][field] = value
    setFormData({ ...formData, questions: newQuestions })
  }

  const handleRemoveQuestion = (index) => {
    const newQuestions = formData.questions.filter((_, i) => i !== index)
    setFormData({ ...formData, questions: newQuestions })
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!isAdmin() && !isInstructor()) {
      setError('Only ADMIN and INSTRUCTOR can create exams')
      return
    }

    try {
      const examData = {
        ...formData,
        examDate: new Date(formData.examDate).toISOString(),
        endDate: new Date(formData.endDate).toISOString()
      }
      await api.post('/api/exams', examData)
      setSuccess('Exam created successfully!')
      setFormData({
        courseId: '',
        title: '',
        description: '',
        examDate: '',
        endDate: '',
        durationMinutes: 60,
        minimumMarksRequired: 0,
        questions: [{ questionText: '', optionA: '', optionB: '', optionC: '', optionD: '', correctAnswer: 'A', marks: 1 }]
      })
      setShowForm(false)
      if (examData.courseId) {
        fetchExams(examData.courseId)
      }
    } catch (err) {
      setError(err.response?.data || 'Failed to create exam')
    }
  }

  const handleTakeExam = (examId) => {
    navigate(`/exam/${examId}`)
  }

  if (loading && exams.length === 0) {
    return <div className="loading">Loading exams...</div>
  }

  return (
    <div className="exams-container">
      <div className="exams-header">
        <h1>Exams</h1>
        {(isAdmin() || isInstructor()) && (
          <button
            className="btn btn-primary"
            onClick={() => setShowForm(!showForm)}
          >
            {showForm ? 'Cancel' : 'Create New Exam'}
          </button>
        )}
      </div>

      {error && <div className="error">{error}</div>}
      {success && <div className="success">{success}</div>}

      {showForm && (isAdmin() || isInstructor()) && (
        <div className="card">
          <h2>Create New Exam</h2>
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
              />
            </div>
            <div className="form-group">
              <label>Exam Start Date & Time</label>
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
              <label>Exam End Date & Time</label>
              <input
                type="datetime-local"
                value={formData.endDate}
                onChange={(e) =>
                  setFormData({ ...formData, endDate: e.target.value })
                }
                required
              />
            </div>
            <div className="form-group">
              <label>Duration (minutes)</label>
              <input
                type="number"
                value={formData.durationMinutes}
                onChange={(e) =>
                  setFormData({ ...formData, durationMinutes: parseInt(e.target.value) })
                }
                required
                min="1"
              />
            </div>
            <div className="form-group">
              <label>Minimum Marks Required (from assignments)</label>
              <input
                type="number"
                value={formData.minimumMarksRequired}
                onChange={(e) =>
                  setFormData({ ...formData, minimumMarksRequired: parseInt(e.target.value) })
                }
                required
                min="0"
              />
            </div>

            <div className="questions-section">
              <h3>Questions</h3>
              {formData.questions.map((question, index) => (
                <div key={index} className="question-card">
                  <div className="question-header">
                    <h4>Question {index + 1}</h4>
                    {formData.questions.length > 1 && (
                      <button
                        type="button"
                        className="btn btn-danger btn-sm"
                        onClick={() => handleRemoveQuestion(index)}
                      >
                        Remove
                      </button>
                    )}
                  </div>
                  <div className="form-group">
                    <label>Question Text</label>
                    <textarea
                      value={question.questionText}
                      onChange={(e) => handleQuestionChange(index, 'questionText', e.target.value)}
                      required
                    />
                  </div>
                  <div className="form-group">
                    <label>Option A</label>
                    <input
                      type="text"
                      value={question.optionA}
                      onChange={(e) => handleQuestionChange(index, 'optionA', e.target.value)}
                      required
                    />
                  </div>
                  <div className="form-group">
                    <label>Option B</label>
                    <input
                      type="text"
                      value={question.optionB}
                      onChange={(e) => handleQuestionChange(index, 'optionB', e.target.value)}
                      required
                    />
                  </div>
                  <div className="form-group">
                    <label>Option C</label>
                    <input
                      type="text"
                      value={question.optionC}
                      onChange={(e) => handleQuestionChange(index, 'optionC', e.target.value)}
                      required
                    />
                  </div>
                  <div className="form-group">
                    <label>Option D</label>
                    <input
                      type="text"
                      value={question.optionD}
                      onChange={(e) => handleQuestionChange(index, 'optionD', e.target.value)}
                      required
                    />
                  </div>
                  <div className="form-group">
                    <label>Correct Answer</label>
                    <select
                      value={question.correctAnswer}
                      onChange={(e) => handleQuestionChange(index, 'correctAnswer', e.target.value)}
                      required
                    >
                      <option value="A">A</option>
                      <option value="B">B</option>
                      <option value="C">C</option>
                      <option value="D">D</option>
                    </select>
                  </div>
                  <div className="form-group">
                    <label>Marks</label>
                    <input
                      type="number"
                      value={question.marks}
                      onChange={(e) => handleQuestionChange(index, 'marks', parseInt(e.target.value))}
                      required
                      min="1"
                    />
                  </div>
                </div>
              ))}
              <button
                type="button"
                className="btn btn-secondary"
                onClick={handleAddQuestion}
              >
                Add Question
              </button>
            </div>

            <button type="submit" className="btn btn-primary">
              Create Exam
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
              {exam.description && <p className="exam-description">{exam.description}</p>}
              <p className="exam-date">
                <strong>Date:</strong> {new Date(exam.examDate).toLocaleString()} - {new Date(exam.endDate).toLocaleString()}
              </p>
              <p className="exam-duration">
                <strong>Duration:</strong> {exam.durationMinutes} minutes
              </p>
              <p className="exam-min-marks">
                <strong>Minimum Marks Required:</strong> {exam.minimumMarksRequired}
              </p>
              {isStudent() && (
                <button
                  className="btn btn-primary"
                  onClick={() => handleTakeExam(exam.id)}
                >
                  Take Exam
                </button>
              )}
            </div>
          ))
        )}
      </div>
    </div>
  )
}

export default Exams
