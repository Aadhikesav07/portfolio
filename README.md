# E-Learning Platform

A full-stack e-learning platform with Spring Boot backend and React frontend.

## 🚀 Quick Start for New Users

**New to this project?** Check out [SETUP_GUIDE.md](./SETUP_GUIDE.md) for detailed step-by-step instructions on how to set up and run the project on your computer.

## Project Structure

```
portfolio/
├── backend/          # Spring Boot Java backend
│   ├── src/
│   ├── pom.xml
│   └── ...
└── frontend/         # React frontend
    ├── src/
    ├── package.json
    └── ...
```

## Backend Setup (Spring Boot)

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Make sure you have **Java 17** (or higher) and Maven installed
   - The project requires Java 17+ (as specified in pom.xml)
   - You can check your Java version with: `java -version`
   - **Note**: If you get "JAVA_HOME not defined" error, set JAVA_HOME environment variable to your JDK installation path

3. Update `src/main/resources/application.properties` with your MongoDB connection string

4. Run the backend:
   
   **On Windows:**
   ```bash
   # Option 1: Use the batch script (automatically sets JAVA_HOME)
   start-backend.bat
   
   # Option 2: Set JAVA_HOME manually and run
   set JAVA_HOME=C:\java\jdk-25
   mvnw.cmd spring-boot:run
   ```
   
   **On Linux/Mac:**
   ```bash
   ./mvnw spring-boot:run
   ```

   The backend will run on `http://localhost:8080`
   
   **Note**: If you get JAVA_HOME errors, set JAVA_HOME to your JDK installation path:
   - Windows: `set JAVA_HOME=C:\path\to\your\jdk`
   - Linux/Mac: `export JAVA_HOME=/path/to/your/jdk`

## Frontend Setup (React)

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm run dev
   ```

   The frontend will run on `http://localhost:3000`

## API Endpoints

### Authentication
- `POST /api/users/register` - Register a new user
- `POST /api/users/login` - Login user

### Courses
- `GET /api/courses` - Get all courses
- `POST /api/courses` - Add a new course
- `DELETE /api/courses/{id}` - Delete a course

### Exams
- `GET /api/exams/course/{courseId}` - Get exams for a course
- `POST /api/exams` - Add a new exam

### Certificates
- `GET /api/certificates` - Get user certificates
- `POST /api/certificates/issue` - Issue a certificate

## Frontend Routes

- `/login` - Login page
- `/register` - Registration page
- `/courses` - Courses listing and management
- `/exams` - Exams listing and management
- `/certificates` - User certificates

## Technologies Used

### Backend
- Spring Boot 3.0.0
- Spring Security
- Spring Data MongoDB
- JWT Authentication
- Maven

### Frontend
- React 18
- React Router DOM
- Axios
- Vite
- CSS3

## Notes

- **MongoDB Required**: Make sure MongoDB is running on `localhost:27017` before starting the backend
  - If you don't have MongoDB installed, download it from [mongodb.com](https://www.mongodb.com/try/download/community)
  - Or use MongoDB Atlas (cloud) and update the connection string in `application.properties`
- The frontend uses JWT tokens stored in localStorage for authentication
- All protected routes require authentication
- If you see JWT signature errors, clear your browser's localStorage or log out and log back in

## Troubleshooting

### MongoDB Connection Error
If you see "Connection refused" errors:
1. Make sure MongoDB is installed and running
2. Check if MongoDB service is running: `mongod --version`
3. Start MongoDB service (Windows: Services app, Linux/Mac: `sudo systemctl start mongod`)

### JWT Signature Errors
If you see JWT signature errors in the backend logs:
1. Clear browser localStorage: Open browser console and run `localStorage.clear()`
2. Or simply log out and log back in through the frontend

