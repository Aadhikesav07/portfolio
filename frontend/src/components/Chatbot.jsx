import React, { useState, useEffect } from 'react'
import api from '../services/api'
import './Chatbot.css'

function Chatbot() {
  const [messages, setMessages] = useState([])
  const [input, setInput] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSend = async (e) => {
    e.preventDefault()
    if (!input.trim()) return

    const userMessage = { type: 'user', text: input }
    setMessages(prev => [...prev, userMessage])
    setInput('')
    setLoading(true)

    try {
      const response = await api.post('/api/chatbot/ask', { question: input })
      const botMessage = { type: 'bot', text: response.data.response }
      setMessages(prev => [...prev, botMessage])
    } catch (err) {
      const errorMessage = { type: 'bot', text: 'Sorry, I encountered an error. Please try again.' }
      setMessages(prev => [...prev, errorMessage])
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="chatbot-container">
      <div className="chatbot-header">
        <h1>AI Assistant</h1>
        <p className="chatbot-subtitle">Ask me anything about courses, exams, assignments, or certificates!</p>
      </div>

      <div className="chatbot-messages">
        {messages.length === 0 && (
          <div className="welcome-message">
            <p>Hello! I'm your AI assistant. I can help you with:</p>
            <ul>
              <li>Information about courses</li>
              <li>Exam guidelines and eligibility</li>
              <li>Assignment requirements</li>
              <li>Certificate information</li>
            </ul>
            <p>What would you like to know?</p>
          </div>
        )}
        {messages.map((msg, index) => (
          <div key={index} className={`message ${msg.type}`}>
            <div className="message-content">
              {msg.text}
            </div>
          </div>
        ))}
        {loading && (
          <div className="message bot">
            <div className="message-content">
              <span className="typing">Thinking...</span>
            </div>
          </div>
        )}
      </div>

      <form className="chatbot-input-form" onSubmit={handleSend}>
        <input
          type="text"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          placeholder="Ask a question..."
          className="chatbot-input"
        />
        <button type="submit" className="btn btn-primary">
          Send
        </button>
      </form>
    </div>
  )
}

export default Chatbot

