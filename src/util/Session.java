package util;

import model.user;

public class Session {

    // Store the currently logged-in user
    private static user currentUser;

    // Set the logged-in user
    public static void setCurrentUser(user user) {
        currentUser = user;
    }

    // Get the currently logged-in user
    public static user getCurrentUser() {
        return currentUser;
    }

    // Check whether a user is logged in
    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    // Logout and clear the session
    public static void logout() {
        currentUser = null;
        System.out.println("Logged out successfully!");
    }
}