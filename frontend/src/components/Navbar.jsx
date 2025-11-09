import React from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import './Navbar.css'

function Navbar() {
  const { isAuthenticated, logout } = useAuth()
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
            <Link to="/exams" className="nav-link">
              Exams
            </Link>
            <Link to="/certificates" className="nav-link">
              Certificates
            </Link>
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

