import React from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import './Navbar.css'

function Navbar() {
  const { isAuthenticated, logout, isAdmin, isInstructor, isStudent, user } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <nav className="navbar">
      <div className="nav-container">
        <Link to="/courses" className="nav-logo">
          E-Learning Platform
        </Link>
        {isAuthenticated && (
          <div className="nav-menu">
            <Link to="/courses" className="nav-link">
              Courses
            </Link>
            {isStudent() && (
              <>
                <Link to="/assignments" className="nav-link">
                  Assignments
                </Link>
                <Link to="/my-exams" className="nav-link">
                  My Exams
                </Link>
              </>
            )}
            {(isAdmin() || isInstructor()) && (
              <>
                <Link to="/exams" className="nav-link">
                  Exams
                </Link>
                <Link to="/assignments" className="nav-link">
                  Assignments
                </Link>
              </>
            )}
            {isAdmin() && (
              <Link to="/users" className="nav-link">
                Users
              </Link>
            )}
            <Link to="/certificates" className="nav-link">
              Certificates
            </Link>
            <Link to="/chatbot" className="nav-link">
              Chatbot
            </Link>
            <span className="nav-user">{user?.fullName || user?.email}</span>
            <button onClick={handleLogout} className="nav-link btn-logout">
              Logout
            </button>
          </div>
        )}
      </div>
    </nav>
  )
}

export default Navbar

