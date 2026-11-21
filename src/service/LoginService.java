 package service;

import model.user;
import dao.userDAO;
import util.Session;
import org.mindrot.jbcrypt.BCrypt;

public class LoginService {

    private final userDAO dao = new userDAO();

    // Step 1: Validate login input
    private boolean isValidLoginInput(String username, String password) {

        return username != null
                && !username.trim().isEmpty()
                && password != null
                && !password.isEmpty();
    }

    // Step 2: Find user in database
    private user findUser(String username) {

        return dao.getUserByUsername(username);
    }

    // Step 3: Check password
    private boolean isPasswordCorrect(String enteredPassword, user foundUser) {

        return BCrypt.checkpw(
                enteredPassword,
                foundUser.getPasswordHash()
        );
    }

    // Step 4: Start user session
    private void startSession(user foundUser) {

        Session.setCurrentUser(foundUser);
    }

    // Step 5: Main login method
    public user login(String username, String password) {

        if (!isValidLoginInput(username, password)) {
            System.out.println("Username and password cannot be empty.");
            return null;
        }

        username = username.trim();

        user foundUser = findUser(username);

        if (foundUser == null) {
            System.out.println(
                    "Incorrect username. Please check your username."
            );
            return null;
        }

        if (!isPasswordCorrect(password, foundUser)) {
            System.out.println(
                    "Incorrect password. Please try again."
            );
            return null;
        }

        startSession(foundUser);

        System.out.println("Login successful!");

        return foundUser;
    }
}