package com.yourcompany.elearningplatform.service;

import com.yourcompany.elearningplatform.entity.Assignment;
import com.yourcompany.elearningplatform.entity.Course;
import com.yourcompany.elearningplatform.repository.AssignmentRepository;
import com.yourcompany.elearningplatform.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AssignmentService {

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    public List<Assignment> getAssignmentsByCourse(String courseId) {
        return assignmentRepository.findByCourseIdAndIsActiveTrue(courseId);
    }

    public Assignment getAssignmentById(String id) {
        Optional<Assignment> assignment = assignmentRepository.findById(id);
        return assignment.orElse(null);
    }

    public Assignment addAssignment(Assignment assignment) {
        // Verify course exists
        Optional<Course> course = courseRepository.findById(assignment.getCourseId());
        if (course.isEmpty()) {
            throw new RuntimeException("Course not found");
        }
        return assignmentRepository.save(assignment);
    }

    public Assignment updateAssignment(String id, Assignment assignment) {
        Optional<Assignment> existing = assignmentRepository.findById(id);
        if (existing.isPresent()) {
            Assignment existingAssignment = existing.get();
            existingAssignment.setTitle(assignment.getTitle());
            existingAssignment.setDescription(assignment.getDescription());
            existingAssignment.setInstructions(assignment.getInstructions());
            existingAssignment.setMandatory(assignment.isMandatory());
            existingAssignment.setMaxMarks(assignment.getMaxMarks());
            existingAssignment.setDueDate(assignment.getDueDate());
            existingAssignment.setActive(assignment.isActive());
            return assignmentRepository.save(existingAssignment);
        }
        return null;
    }

    public void deleteAssignment(String id) {
        assignmentRepository.deleteById(id);
    }

    public List<Assignment> getAssignmentsByCreator(String createdBy) {
        return assignmentRepository.findByCreatedBy(createdBy);
    }

    public List<Assignment> getMandatoryAssignments(String courseId) {
        return assignmentRepository.findByCourseIdAndIsMandatoryTrue(courseId);
    }

    public boolean canInstructorAccessAssignment(String assignmentId, String instructorId) {
        Optional<Assignment> assignment = assignmentRepository.findById(assignmentId);
        if (assignment.isEmpty()) return false;
        
        Assignment a = assignment.get();
        Optional<Course> course = courseRepository.findById(a.getCourseId());
        if (course.isEmpty()) return false;
        
        return a.getCreatedBy().equals(instructorId) || 
               course.get().getInstructorId().equals(instructorId);
    }
}

