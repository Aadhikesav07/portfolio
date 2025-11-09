package com.yourcompany.elearningplatform.service;

import com.yourcompany.elearningplatform.entity.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatbotService {

    @Autowired
    private CourseService courseService;

    @Value("${chatbot.ai.enabled:false}")
    private boolean aiEnabled;

    @Value("${chatbot.ai.provider:openai}")
    private String aiProvider;

    @Value("${chatbot.ai.api.key:}")
    private String apiKey;

    @Value("${chatbot.ai.api.url:}")
    private String apiUrl;

    @Autowired
    private RestTemplate restTemplate;

    public String generateResponse(String question, List<Course> courses) {
        if (aiEnabled && apiKey != null && !apiKey.isEmpty()) {
            return generateAIResponse(question, courses);
        } else {
            return generateRuleBasedResponse(question);
        }
    }

    private String generateAIResponse(String question, List<Course> courses) {
        try {
            if ("openai".equalsIgnoreCase(aiProvider)) {
                return callOpenAI(question, courses);
            } else if ("gemini".equalsIgnoreCase(aiProvider)) {
                return callGemini(question, courses);
            } else {
                // Fallback to rule-based
                return generateRuleBasedResponse(question);
            }
        } catch (Exception e) {
            System.err.println("AI service error: " + e.getMessage());
            // Fallback to rule-based on error
            return generateRuleBasedResponse(question);
        }
    }

    private String callOpenAI(String question, List<Course> courses) {

        // Build context about available courses
        StringBuilder context = new StringBuilder("You are a helpful assistant for an e-learning platform. ");
        context.append("Available courses: ");
        for (Course course : courses) {
            context.append(course.getTitle()).append(" - ").append(course.getDescription()).append(". ");
        }
        context.append("Answer the user's question based on this information. ");

        // OpenAI API call
        String url = apiUrl != null && !apiUrl.isEmpty() 
            ? apiUrl 
            : "https://api.openai.com/v1/chat/completions";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", context.toString() + 
            "You can only provide information about courses, exams, assignments, and certificates. " +
            "You cannot access student grades or perform admin functions.");
        
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", question);

        requestBody.put("messages", List.of(systemMessage, userMessage));
        requestBody.put("max_tokens", 200);
        requestBody.put("temperature", 0.7);

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");

        org.springframework.http.HttpEntity<Map<String, Object>> entity = 
            new org.springframework.http.HttpEntity<>(requestBody, headers);

        try {
            Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);
            if (response != null && response.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    return (String) message.get("content");
                }
            }
        } catch (Exception e) {
            System.err.println("OpenAI API error: " + e.getMessage());
        }

        return generateRuleBasedResponse(question);
    }

    private String callGemini(String question, List<Course> courses) {
        // Similar implementation for Google Gemini
        // This is a placeholder - you would implement Gemini API calls here
        return generateRuleBasedResponse(question);
    }

    private String generateRuleBasedResponse(String question) {
        // Enhanced rule-based responses with course context
        List<Course> courses = courseService.getActiveCourses();
        String coursesList = courses.stream()
            .map(Course::getTitle)
            .reduce((a, b) -> a + ", " + b)
            .orElse("No courses available");

        if (question.contains("course") || question.contains("enroll")) {
            return "Available courses: " + coursesList + 
                ". You can view all available courses in the Courses section. To enroll, please contact your administrator.";
        } else if (question.contains("exam") || question.contains("test")) {
            return "Exams are available for courses you're enrolled in. You need to complete mandatory assignments and achieve minimum marks to be eligible for exams. " +
                "Each exam has a minimum marks requirement that you must meet from your assignment scores.";
        } else if (question.contains("assignment") || question.contains("homework")) {
            return "Assignments can be mandatory or optional. Completing all mandatory assignments is required to be eligible for exams. " +
                "Your assignment scores contribute to your overall course marks.";
        } else if (question.contains("certificate") || question.contains("cert")) {
            return "Certificates are issued automatically after successfully completing an exam. You'll receive it via email with a unique OID (Object Identifier). " +
                "You can download the PDF certificate from your Certificates page.";
        } else if (question.contains("help") || question.contains("how")) {
            return "I can help you with information about courses, exams, assignments, and certificates. " +
                "Available courses: " + coursesList + ". What would you like to know?";
        } else if (question.contains("what") && question.contains("course")) {
            return "Available courses: " + coursesList + ". You can find more details in the Courses section.";
        } else {
            return "I'm here to help! I can provide information about courses, exams, assignments, and certificates. " +
                "Available courses: " + coursesList + ". Please ask me a specific question.";
        }
    }
}

