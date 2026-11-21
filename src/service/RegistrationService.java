package service;

import model.user;
import util.DBConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public class RegistrationService {

    // Validate username
    private boolean isValidUsername(String username) {
        return username != null
                && username.matches("^[A-Za-z0-9_.@]+$");
    }

    // Validate password
    private boolean isValidPassword(String password) {
        return password != null && password.length() >= 8;
    }

    // Validate member details
    private boolean isValidMemberDetails(
            String name, String phone, String email) {

        return name != null && !name.trim().isEmpty()
                && phone != null
                && phone.matches("^[6-9][0-9]{9}$")
                && email != null
                && email.matches(
                    "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    // Register a new member and create their login account
    public user registerMember(
            String name,
            String phone,
            String email,
            String username,
            String password) {

        // Step 1: Validate details
        if (!isValidMemberDetails(name, phone, email)
                || !isValidUsername(username)
                || !isValidPassword(password)) {

            System.out.println("Invalid registration details!");
            return null;
        }

        try (Connection con = DBConnection.getConnection()) {

            // Step 2: Start transaction
            con.setAutoCommit(false);

            try {
                // Step 3: Check duplicate username
                String checkUsername =
                        "SELECT user_id FROM `user` WHERE username = ?";

                try (PreparedStatement ps =
                             con.prepareStatement(checkUsername)) {

                    ps.setString(1, username);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            con.rollback();
                            System.out.println(
                                    "Username already exists. Choose another.");
                            return null;
                        }
                    }
                }

                // Step 4: Insert member record
                String insertMember =
                        "INSERT INTO member " +
                        "(member_name, phone, email) VALUES (?, ?, ?)";

                int memberId;

                try (PreparedStatement ps = con.prepareStatement(
                        insertMember, Statement.RETURN_GENERATED_KEYS)) {

                    ps.setString(1, name.trim());
                    ps.setString(2, phone);
                    ps.setString(3, email.trim());

                    ps.executeUpdate();

                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException(
                                    "Member ID was not generated.");
                        }

                        memberId = keys.getInt(1);
                    }
                }

                // Step 5: Hash password using BCrypt
                String passwordHash =
                        BCrypt.hashpw(password, BCrypt.gensalt());

                // Step 6: Insert user account
                String insertUser =
                        "INSERT INTO `user` " +
                        "(username, password_hash, role, member_id) " +
                        "VALUES (?, ?, 'MEMBER', ?)";

                int userId;

                try (PreparedStatement ps = con.prepareStatement(
                        insertUser, Statement.RETURN_GENERATED_KEYS)) {

                    ps.setString(1, username);
                    ps.setString(2, passwordHash);
                    ps.setInt(3, memberId);

                    ps.executeUpdate();

                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException(
                                    "User ID was not generated.");
                        }

                        userId = keys.getInt(1);
                    }
                }

                // Step 7: Commit both records
                con.commit();

                System.out.println("Registration successful!");

                // Step 8: Return user for automatic login
                return new user(
                        userId,
                        username,
                        passwordHash,
                        "MEMBER",
                        memberId
                );

            } catch (SQLException | RuntimeException e) {

                con.rollback();

                if (e instanceof SQLException
                        && "23000".equals(
                                ((SQLException) e).getSQLState())) {

                    System.out.println(
                            "Username, phone, or email already exists.");
                } else {
                    System.out.println(
                            "Registration failed. Please try again.");
                }

                return null;
            }

        } catch (SQLException e) {
            System.out.println(
                    "Could not connect to the database.");
            return null;
        }
    }
}