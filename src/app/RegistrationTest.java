package app;

import service.RegistrationService;
import model.user;

public class RegistrationTest {

    public static void main(String[] args) {

        RegistrationService service = new RegistrationService();

        user registeredUser = service.registerMember(
                "Madhu Test",
                "9876543210",
                "madhu.test@example.com",
                "madhu_test_01",
                "Madhu@123"
        );

        if (registeredUser != null) {
            System.out.println("Registration test successful!");
            System.out.println("User ID: " + registeredUser.getUserId());
            System.out.println("Member ID: " + registeredUser.getMemberId());
        } else {
            System.out.println("Registration test failed!");
        }
    }
}