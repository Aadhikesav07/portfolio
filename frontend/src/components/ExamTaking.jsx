import React, { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import api from '../services/api'
import { useAuth } from '../context/AuthContext'
import './ExamTaking.css'

function ExamTaking() {
  const { examId } = useParams()
  const navigate = useNavigate()
  const { isStudent } = useAuth()
  const [exam, setExam] = useState(null)
  const [answers, setAnswers] = useState({})
  const [timeRemaining, setTimeRemaining] = useState(0)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [submitted, setSubmitted] = useState(false)
  const [results, setResults] = useState(null)

  useEffect(() => {
    if (!isStudent()) {
      navigate('/exams')
      return
    }
    startExam()
  }, [examId])

  useEffect(() => {
    if (exam && timeRemaining > 0) {
      const timer = setInterval(() => {
        setTimeRemaining(prev => {
          if (prev <= 1) {
            handleAutoSubmit()
            return 0
          }
          return prev - 1
        })
      }, 1000)
      return () => clearInterval(timer)
    }
  }, [exam, timeRemaining])

  const startExam = async () => {
    try {
      setLoading(true)
      // Start exam session
      await api.post(`/api/exam-responses/start/${examId}`)
      
      // Get exam details
      const response = await api.get(`/api/exams/${examId}`)
      setExam(response.data)
      
      if (response.data.questions) {
        const initialAnswers = {}
        response.data.questions.forEach((_, index) => {
          initialAnswers[index] = ''
        })
        setAnswers(initialAnswers)
      }

      // Set timer
      if (response.data.durationMinutes) {
        setTimeRemaining(response.data.durationMinutes * 60)
      }
      
      setError('')
    } catch (err) {
      setError(err.response?.data || 'Failed to start exam. You may not be eligible.')
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  const handleAnswerChange = (questionIndex, answer) => {
    setAnswers({ ...answers, [questionIndex]: answer })
  }

  const handleSubmit = async () => {
    if (window.confirm('Are you sure you want to submit? You cannot change answers after submission.')) {
      try {
        const response = await api.post(`/api/exam-responses/submit/${examId}`, answers)
        setSubmitted(true)
        fetchResults()
      } catch (err) {
        setError(err.response?.data || 'Failed to submit exam')
      }
    }
  }

  const handleAutoSubmit = async () => {
    try {
      await api.post(`/api/exam-responses/submit/${examId}`, answers)
      setSubmitted(true)
      fetchResults()
    } catch (err) {
      setError('Exam auto-submitted due to time limit')
    }
  }

  const fetchResults = async () => {
    try {
      const response = await api.get(`/api/exam-responses/results/${examId}`)
      setResults(response.data)
    } catch (err) {
      console.error('Failed to fetch results:', err)
    }
  }

  const formatTime = (seconds) => {
    const mins = Math.floor(seconds / 60)
    const secs = seconds % 60
    return `${mins}:${secs.toString().padStart(2, '0')}`
  }

  if (loading) {
    return <div className="loading">Loading exam...</div>
  }

  if (error && !exam) {
    return (
      <div className="exam-taking-container">
        <div className="error">{error}</div>
        <button className="btn btn-primary" onClick={() => navigate('/exams')}>
          Go Back
        </button>
      </div>
    )
  }

  if (submitted && results) {
    return (
      <div className="exam-taking-container">
        <div className="card">
          <h2>Exam Results</h2>
          <div className="results-summary">
            <p><strong>Total Marks:</strong> {results.totalMarks}</p>
            <p><strong>Marks Obtained:</strong> {results.marksObtained}</p>
            <p><strong>Percentage:</strong> {results.percentage}%</p>
          </div>
          <button className="btn btn-primary" onClick={() => navigate('/certificates')}>
            View Certificates
          </button>
        </div>
      </div>
    )
  }

  return (
    <div className="exam-taking-container">
      <div className="exam-header">
        <h1>{exam?.title}</h1>
        <div className="timer">
          Time Remaining: <strong>{formatTime(timeRemaining)}</strong>
        </div>
      </div>

      {error && <div className="error">{error}</div>}

      <div className="exam-questions">
        {exam?.questions?.map((question, index) => (
          <div key={index} className="question-card">
            <h3>Question {index + 1} ({question.marks} marks)</h3>
            <p className="question-text">{question.questionText}</p>
            <div className="options">
              <label className="option-label">
                <input
                  type="radio"
                  name={`question-${index}`}
                  value="A"
                  checked={answers[index] === 'A'}
                  onChange={() => handleAnswerChange(index, 'A')}
                />
                A. {question.optionA}
              </label>
              <label className="option-label">
                <input
                  type="radio"
                  name={`question-${index}`}
                  value="B"
                  checked={answers[index] === 'B'}
                  onChange={() => handleAnswerChange(index, 'B')}
                />
                B. {question.optionB}
              </label>
              <label className="option-label">
                <input
                  type="radio"
                  name={`question-${index}`}
                  value="C"
                  checked={answers[index] === 'C'}
                  onChange={() => handleAnswerChange(index, 'C')}
                />
                C. {question.optionC}
              </label>
              <label className="option-label">
                <input
                  type="radio"
                  name={`question-${index}`}
                  value="D"
                  checked={answers[index] === 'D'}
                  onChange={() => handleAnswerChange(index, 'D')}
                />
                D. {question.optionD}
              </label>
            </div>
          </div>
        ))}
      </div>

      <div className="exam-actions">
        <button className="btn btn-primary btn-large" onClick={handleSubmit}>
          Submit Exam
        </button>
      </div>
    </div>
  )
}

export default ExamTaking

