package com.yourcompany.elearningplatform.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class RoleUtil {
    
    public static boolean isAdmin(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
    
    public static boolean isInstructor(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_INSTRUCTOR"));
    }
    
    public static boolean isStudent(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));
    }
    
    public static boolean isAdminOrInstructor(Authentication authentication) {
        return isAdmin(authentication) || isInstructor(authentication);
    }
    
    public static String getRole(Authentication authentication) {
        if (authentication == null) return null;
        return authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .findFirst()
            .map(a -> a.replace("ROLE_", ""))
            .orElse(null);
    }
    
    public static String getUserId(Authentication authentication) {
        if (authentication == null) return null;
        return authentication.getName(); // Email is used as username
    }
}

