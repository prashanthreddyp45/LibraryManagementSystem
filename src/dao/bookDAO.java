package dao;

import model.book;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class bookDAO {

    // Add a book
    public boolean addBook(book book) {

        String sql = "INSERT INTO book "
                + "(book_name, author, price, total_copies, available_copies) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, book.getBookName());
            ps.setString(2, book.getAuthor());
            ps.setBigDecimal(3, book.getPrice());
            ps.setInt(4, book.getTotalCopies());
            ps.setInt(5, book.getAvailableCopies());

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get all books
    public List<book> getAllBooks() {

        List<book> books = new ArrayList<>();
        String sql = "SELECT * FROM book";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                book b = new book(
                    rs.getInt("book_id"),
                    rs.getString("book_name"),
                    rs.getString("author"),
                    rs.getBigDecimal("price"),
                    rs.getInt("total_copies"),
                    rs.getInt("available_copies")
                );

                books.add(b);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return books;
    }

    // Search books by name
    public List<book> searchBooksByName(String name) {

        List<book> books = new ArrayList<>();
        String sql = "SELECT * FROM book WHERE book_name LIKE ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + name + "%");

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    book b = new book(
                        rs.getInt("book_id"),
                        rs.getString("book_name"),
                        rs.getString("author"),
                        rs.getBigDecimal("price"),
                        rs.getInt("total_copies"),
                        rs.getInt("available_copies")
                    );

                    books.add(b);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return books;
    }

    // Search books by author
    public List<book> searchBooksByAuthor(String author) {

        List<book> books = new ArrayList<>();
        String sql = "SELECT * FROM book WHERE author LIKE ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + author + "%");

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    book b = new book(
                        rs.getInt("book_id"),
                        rs.getString("book_name"),
                        rs.getString("author"),
                        rs.getBigDecimal("price"),
                        rs.getInt("total_copies"),
                        rs.getInt("available_copies")
                    );

                    books.add(b);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return books;
    }

    // Update book details
    public boolean updateBook(book book) {

        String sql = "UPDATE book SET "
                + "book_name = ?, author = ?, price = ?, "
                + "total_copies = ?, available_copies = ? "
                + "WHERE book_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, book.getBookName());
            ps.setString(2, book.getAuthor());
            ps.setBigDecimal(3, book.getPrice());
            ps.setInt(4, book.getTotalCopies());
            ps.setInt(5, book.getAvailableCopies());
            ps.setInt(6, book.getBookId());

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete a book
    public boolean deleteBook(int bookId) {

        String sql = "DELETE FROM book WHERE book_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, bookId);

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}