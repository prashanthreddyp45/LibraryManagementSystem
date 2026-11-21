package app;

import java.util.Scanner;
import org.mindrot.jbcrypt.BCrypt;

public class GenerateAdminHash {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter admin password: ");
        String password = sc.nextLine();

        String hash = BCrypt.hashpw(
            password,
            BCrypt.gensalt()
        );

        System.out.println("BCrypt Hash: " + hash);
    }
}