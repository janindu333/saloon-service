package com.baber.saloonservice.configurations;

public class UserContext {
    private static final ThreadLocal<String> userDetailsJsonHolder = new ThreadLocal<>();
    private static final ThreadLocal<String> roleHolder = new ThreadLocal<>();
    private static final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();
    private static final ThreadLocal<String> usernameHolder = new ThreadLocal<>();
    
    public static void setUserDetailsJson(String userDetailsJson) {
        userDetailsJsonHolder.set(userDetailsJson);
    }

    public static String getUserDetailsJson() {
        return userDetailsJsonHolder.get();
    }
    
    public static void setRole(String role) {
        roleHolder.set(role);
    }
    
    public static String getRole() {
        return roleHolder.get();
    }
    
    public static void setUserId(Long userId) {
        userIdHolder.set(userId);
    }
    
    public static Long getUserId() {
        return userIdHolder.get();
    }
    
    public static void setUsername(String username) {
        usernameHolder.set(username);
    }
    
    public static String getUsername() {
        return usernameHolder.get();
    }
    
    public static boolean hasRole(String role) {
        String userRole = getRole();
        return userRole != null && userRole.equalsIgnoreCase(role);
    }
    
    public static boolean hasAnyRole(String... roles) {
        String userRole = getRole();
        if (userRole == null) {
            return false;
        }
        for (String role : roles) {
            if (userRole.equalsIgnoreCase(role)) {
                return true;
            }
        }
        return false;
    }

    public static void clear() {
        userDetailsJsonHolder.remove();
        roleHolder.remove();
        userIdHolder.remove();
        usernameHolder.remove();
    }
}

