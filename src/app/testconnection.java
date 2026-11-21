package app;

import java.sql.Connection;
import java.sql.SQLException;
import util.DBConnection;

public class testconnection {

    public static void main(String[] args) {
        try (Connection con = DBConnection.getConnection()) {
            System.out.println("Database connected and initialized successfully!");
        } catch (SQLException e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();
        }
    }
}