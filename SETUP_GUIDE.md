# Setup Guide for Running the E-Learning Platform

This guide will help you set up and run the E-Learning Platform on your computer.

## Prerequisites

Before you start, make sure you have the following installed:

1. **Java 17 or higher** (Java 25 works too)
   - Download from: https://adoptium.net/ or https://www.oracle.com/java/technologies/downloads/
   - Verify installation: `java -version`

2. **Node.js and npm** (for frontend)
   - Download from: https://nodejs.org/
   - Verify installation: `node --version` and `npm --version`

3. **Git** (to clone the repository)
   - Download from: https://git-scm.com/downloads
   - Verify installation: `git --version`

4. **MongoDB Atlas Account** (Cloud database - already set up)
   - The database connection is already configured
   - No local MongoDB installation needed!

## Step 1: Clone the Repository

```bash
git clone <repository-url>
cd portfolio
```

## Step 2: Set Up the Backend

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. **Set JAVA_HOME** (Important for Windows):
   - Find your Java installation path (usually `C:\Program Files\Java\jdk-XX` or `C:\java\jdk-XX`)
   - Set JAVA_HOME environment variable:
     - **Windows (CMD):**
       ```bash
       set JAVA_HOME=C:\path\to\your\jdk
       ```
     - **Windows (PowerShell):**
       ```powershell
       $env:JAVA_HOME = "C:\path\to\your\jdk"
       ```
     - **Linux/Mac:**
       ```bash
       export JAVA_HOME=/path/to/your/jdk
       ```

3. Run the backend:
   - **Windows (Easiest):**
     ```bash
     start-backend.bat
     ```
   - **Windows (Manual):**
     ```bash
     mvnw.cmd spring-boot:run
     ```
   - **Linux/Mac:**
     ```bash
     ./mvnw spring-boot:run
     ```

4. Wait for the backend to start. You should see:
   ```
   Started ElearningplatformApplication in X seconds
   ```
   The backend will run on `http://localhost:8080`

## Step 3: Set Up the Frontend

1. Open a **NEW terminal window** (keep backend running)

2. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

3. Install dependencies:
   ```bash
   npm install
   ```
   This may take a few minutes the first time.

4. Start the frontend development server:
   ```bash
   npm run dev
   ```

5. The frontend will run on `http://localhost:3000`
   - It should automatically open in your browser
   - If not, manually go to: http://localhost:3000

## Step 4: Use the Application

1. **Register a new account:**
   - Go to http://localhost:3000
   - Click "Register here" or go to `/register`
   - Fill in your details and register

2. **Login:**
   - Use your registered email and password
   - You'll be redirected to the Courses page

3. **Features:**
   - **Courses**: View, add, and delete courses
   - **Exams**: View and add exams for courses
   - **Certificates**: View and request certificates

## Troubleshooting

### Backend Issues

**Problem: "JAVA_HOME not defined"**
- Solution: Set JAVA_HOME as shown in Step 2

**Problem: "Cannot find path .mvn/wrapper"**
- Solution: The Maven wrapper files should be included. If missing, contact the repository owner.

**Problem: Backend won't start**
- Check if port 8080 is already in use
- Make sure Java 17+ is installed
- Check the error messages in the terminal

### Frontend Issues

**Problem: "npm: command not found"**
- Solution: Install Node.js from https://nodejs.org/

**Problem: Port 3000 already in use**
- Solution: Kill the process using port 3000 or change the port in `vite.config.js`

**Problem: "Cannot connect to backend"**
- Solution: Make sure the backend is running on port 8080
- Check `frontend/src/services/api.js` - it should point to `http://localhost:8080`

### Database Issues

**Problem: MongoDB connection errors**
- The database is already configured in the cloud
- If you see connection errors, check your internet connection
- The MongoDB Atlas cluster should allow connections from any IP (configured by the owner)

## Project Structure

```
portfolio/
├── backend/          # Spring Boot Java backend
│   ├── src/         # Source code
│   ├── pom.xml      # Maven dependencies
│   └── start-backend.bat  # Windows startup script
├── frontend/        # React frontend
│   ├── src/         # React source code
│   ├── package.json # Node dependencies
│   └── vite.config.js
└── README.md        # Main documentation
```

## API Endpoints

The backend provides these endpoints:

- `POST /api/users/register` - Register new user
- `POST /api/users/login` - Login user
- `GET /api/courses` - Get all courses
- `POST /api/courses` - Add new course
- `DELETE /api/courses/{id}` - Delete course
- `GET /api/exams/course/{courseId}` - Get exams for course
- `POST /api/exams` - Add new exam
- `GET /api/certificates` - Get user certificates
- `POST /api/certificates/issue` - Issue certificate

## Need Help?

If you encounter any issues:
1. Check the error messages in the terminal
2. Verify all prerequisites are installed
3. Make sure both backend and frontend are running
4. Check the README.md for more details

## Quick Start Commands Summary

**Terminal 1 (Backend):**
```bash
cd backend
set JAVA_HOME=C:\path\to\your\jdk  # Windows only
start-backend.bat  # or mvnw.cmd spring-boot:run
```

**Terminal 2 (Frontend):**
```bash
cd frontend
npm install
npm run dev
```

Then open http://localhost:3000 in your browser!

