# NPTEL-like Educational Platform - Implementation Plan

## System Architecture

### Role Hierarchy:
1. **ADMIN** - Full system access, can create courses and instructors
2. **INSTRUCTOR** - Can create exams and assignments for their courses
3. **STUDENT** - Read-only access, can take exams if qualified
4. **AI_CHATBOT** - Read-only assistance (read-only portal access)

### RBAC Matrix:

| Module | Action | ADMIN | INSTRUCTOR | STUDENT |
|--------|--------|-------|------------|---------|
| Courses | Create | ✅ | ❌ | ❌ |
| Courses | Read | ✅ | ✅ | ✅ (Active only) |
| Courses | Update | ✅ | ❌ | ❌ |
| Courses | Delete | ✅ | ❌ | ❌ |
| Exams | Create | ✅ | ✅ (Own courses) | ❌ |
| Exams | Read | ✅ | ✅ | ✅ |
| Exams | Update | ✅ | ✅ (Own exams) | ❌ |
| Exams | Delete | ✅ | ✅ (Own exams) | ❌ |
| Exams | Take/Submit | ❌ | ❌ | ✅ (If eligible) |
| Assignments | Create | ✅ | ✅ (Own courses) | ❌ |
| Assignments | Read | ✅ | ✅ | ✅ |
| Assignments | Update | ✅ | ✅ (Own assignments) | ❌ |
| Assignments | Delete | ✅ | ✅ (Own assignments) | ❌ |
| Assignments | Submit | ❌ | ❌ | ✅ |
| Certificates | Issue | ✅ | ❌ | ❌ |
| Certificates | View | ✅ | ✅ | ✅ (Own only) |
| Certificates | Download PDF | ✅ | ✅ | ✅ (Own only) |
| Users | Create Instructor | ✅ | ❌ | ❌ |
| Users | Create Student | ✅ | ❌ | ✅ (Self-register) |
| Chatbot | Access | ✅ | ✅ | ✅ |

## Database Schema Changes

### New Collections:
1. **assignments** - Course assignments (mandatory/optional)
2. **assignment_submissions** - Student assignment submissions
3. **exam_responses** - Student exam answers and scores
4. **student_progress** - Track student marks and exam eligibility

### Updated Collections:
1. **courses** - Added instructorId, createdBy, isActive
2. **exams** - Added questions with answers, minimumMarksRequired, createdBy
3. **certificates** - Added OID, student details, PDF path, email status

## Implementation Priority

1. ✅ **Database Schema** - Entities and Repositories (DONE)
2. ✅ **RBAC Utilities** - RoleUtil class (DONE)
3. ✅ **Course Controller** - RBAC enforcement (DONE)
4. ⏳ **Exam Controller** - RBAC + Answer checking
5. ⏳ **Assignment Controller** - Mandatory/Optional management
6. ⏳ **Exam Response Controller** - Student exam taking
7. ⏳ **Certificate Service** - OID generation, PDF, Email
8. ⏳ **Student Progress Service** - Mark tracking, eligibility
9. ⏳ **Chatbot Controller** - Read-only AI assistance
10. ⏳ **Frontend Updates** - All new components

## Key Features Implementation

### 1. Exam System with Answer Checking
- Questions stored with correct answers
- Students select answers (A, B, C, D)
- Auto-grading on submission
- Immediate feedback

### 2. Exam Eligibility Validation
- Check student marks in course
- Verify mandatory assignments completed
- Validate minimum marks threshold
- Return clear error messages

### 3. Certificate Generation
- Unique OID: `CERT-{timestamp}-{random}`
- PDF generation using iText or Apache PDFBox
- Email delivery with PDF attachment
- Download endpoint

### 4. Assignment Management
- Mandatory vs Optional flag
- Completion tracking
- Marks calculation
- Exam eligibility based on mandatory completion

### 5. AI Chatbot
- Read-only access to course materials
- FAQ responses
- General guidance
- No access to grades/admin functions

## Next Steps

Continue implementing:
1. ExamController with RBAC
2. AssignmentController
3. ExamResponseController (student exam taking)
4. CertificateService (PDF + Email)
5. StudentProgressService
6. ChatbotController
7. Frontend components

