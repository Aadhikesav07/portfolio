package com.yourcompany.elearningplatform.controller;

import com.yourcompany.elearningplatform.entity.Course;
import com.yourcompany.elearningplatform.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    @Autowired
    private CourseService courseService;

    // AI Chatbot endpoint - Read-only access to course materials
    @PostMapping("/ask")
    public ResponseEntity<Map<String, String>> askQuestion(@RequestBody Map<String, String> request) {
        String question = request.get("question");
        if (question == null || question.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Question is required"));
        }

        // Simple chatbot implementation (can be enhanced with AI integration)
        String response = generateResponse(question.toLowerCase());
        
        Map<String, String> result = new HashMap<>();
        result.put("question", question);
        result.put("response", response);
        result.put("timestamp", java.time.LocalDateTime.now().toString());

        return ResponseEntity.ok(result);
    }

    // Get course information for chatbot (read-only)
    @GetMapping("/courses")
    public ResponseEntity<List<Course>> getCoursesForChatbot() {
        // Return only active courses
        return ResponseEntity.ok(courseService.getActiveCourses());
    }

    // Get course details for chatbot
    @GetMapping("/course/{courseId}")
    public ResponseEntity<Course> getCourseForChatbot(@PathVariable String courseId) {
        Course course = courseService.getCourseById(courseId);
        if (course == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(course);
    }

    private String generateResponse(String question) {
        // Simple rule-based responses (can be replaced with AI/ML model)
        if (question.contains("course") || question.contains("enroll")) {
            return "You can view all available courses in the Courses section. To enroll, please contact your administrator.";
        } else if (question.contains("exam") || question.contains("test")) {
            return "Exams are available for courses you're enrolled in. You need to complete mandatory assignments and achieve minimum marks to be eligible for exams.";
        } else if (question.contains("assignment") || question.contains("homework")) {
            return "Assignments can be mandatory or optional. Completing all mandatory assignments is required to be eligible for exams.";
        } else if (question.contains("certificate") || question.contains("cert")) {
            return "Certificates are issued automatically after successfully completing an exam. You'll receive it via email with a unique OID.";
        } else if (question.contains("help") || question.contains("how")) {
            return "I can help you with information about courses, exams, assignments, and certificates. What would you like to know?";
        } else {
            return "I'm here to help! I can provide information about courses, exams, assignments, and certificates. Please ask me a specific question.";
        }
    }
}

