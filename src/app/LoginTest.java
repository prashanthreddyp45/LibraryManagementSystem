package app;

import service.LoginService;
import model.user;

public class LoginTest {

    public static void main(String[] args) {

        LoginService service = new LoginService();

        user loggedInUser = service.login(
                "madhu_test_01",
                "Madhu@123"
        );

        if (loggedInUser != null) {
            System.out.println("User ID: " + loggedInUser.getUserId());
            System.out.println("Username: " + loggedInUser.getUsername());
            System.out.println("Role: " + loggedInUser.getRole());
        } else {
            System.out.println("Login failed!");
        }
    }
}