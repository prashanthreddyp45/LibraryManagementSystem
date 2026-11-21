package dao;

import model.bookissue;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class bookIssueDAO {

    // Issue a book
    public boolean issueBook(bookissue issue) {

        String memberSql =
                "SELECT member_id FROM member WHERE member_id = ? FOR UPDATE";

        String bookSql =
                "SELECT available_copies FROM book WHERE book_id = ? FOR UPDATE";

        String countSql =
                "SELECT COUNT(*) FROM book_issue "
                + "WHERE member_id = ? AND return_date IS NULL";

        String duplicateSql =
                "SELECT COUNT(*) FROM book_issue "
                + "WHERE member_id = ? AND book_id = ? "
                + "AND return_date IS NULL";

        String updateBookSql =
                "UPDATE book SET available_copies = available_copies - 1 "
                + "WHERE book_id = ? AND available_copies > 0";

        String insertSql =
                "INSERT INTO book_issue "
                + "(book_id, member_id, issue_date, due_date, return_date, fine) "
                + "VALUES (?, ?, ?, ?, NULL, ?)";

        if (issue == null
                || issue.getBookId() <= 0
                || issue.getMemberId() <= 0
                || issue.getIssueDate() == null
                || issue.getDueDate() == null
                || issue.getFine() == null
                || issue.getDueDate().isBefore(issue.getIssueDate())) {

            System.out.println("Invalid issue details!");
            return false;
        }

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {

                // Check member
                try (PreparedStatement ps =
                             con.prepareStatement(memberSql)) {

                    ps.setInt(1, issue.getMemberId());

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            System.out.println("Member not found!");
                            con.rollback();
                            return false;
                        }
                    }
                }

                // Check book availability
                try (PreparedStatement ps =
                             con.prepareStatement(bookSql)) {

                    ps.setInt(1, issue.getBookId());

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            System.out.println("Book not found!");
                            con.rollback();
                            return false;
                        }

                        if (rs.getInt("available_copies") <= 0) {
                            System.out.println(
                                    "No copies available for this book!"
                            );
                            con.rollback();
                            return false;
                        }
                    }
                }

                // Check maximum 4 active books
                try (PreparedStatement ps =
                             con.prepareStatement(countSql)) {

                    ps.setInt(1, issue.getMemberId());

                    try (ResultSet rs = ps.executeQuery()) {

                        if (rs.next() && rs.getInt(1) >= 4) {
                            System.out.println(
                                    "You cannot issue more than 4 active books!"
                            );
                            con.rollback();
                            return false;
                        }
                    }
                }

                // Prevent duplicate active book
                try (PreparedStatement ps =
                             con.prepareStatement(duplicateSql)) {

                    ps.setInt(1, issue.getMemberId());
                    ps.setInt(2, issue.getBookId());

                    try (ResultSet rs = ps.executeQuery()) {

                        if (rs.next() && rs.getInt(1) > 0) {
                            System.out.println(
                                    "You have already issued this book!"
                            );
                            con.rollback();
                            return false;
                        }
                    }
                }

                // Decrease available copies
                try (PreparedStatement ps =
                             con.prepareStatement(updateBookSql)) {

                    ps.setInt(1, issue.getBookId());

                    if (ps.executeUpdate() != 1) {
                        System.out.println(
                                "Unable to update book availability!"
                        );
                        con.rollback();
                        return false;
                    }
                }

                // Insert issue record
                try (PreparedStatement ps =
                             con.prepareStatement(insertSql)) {

                    ps.setInt(1, issue.getBookId());
                    ps.setInt(2, issue.getMemberId());
                    ps.setDate(3, Date.valueOf(issue.getIssueDate()));
                    ps.setDate(4, Date.valueOf(issue.getDueDate()));
                    ps.setBigDecimal(5, issue.getFine());

                    if (ps.executeUpdate() != 1) {
                        con.rollback();
                        return false;
                    }
                }

                con.commit();

                System.out.println("Book issued successfully!");
                return true;

            } catch (SQLException e) {

                con.rollback();
                throw e;

            } catch (RuntimeException e) {

                con.rollback();
                throw e;
            }

        } catch (SQLException e) {

            e.printStackTrace();
            return false;
        }
    }

    // View all issue records
    public List<bookissue> getAllIssues() {

        List<bookissue> issues = new ArrayList<>();

        String sql = "SELECT * FROM book_issue";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                issues.add(mapIssue(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return issues;
    }

    // View issue records for one member
    public List<bookissue> getIssuesByMemberId(int memberId) {

        List<bookissue> issues = new ArrayList<>();

        String sql =
                "SELECT * FROM book_issue WHERE member_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, memberId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    issues.add(mapIssue(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return issues;
    }

    // View issue records with book name and author
    public List<bookissue> getIssuesWithBookDetails(int memberId) {

        List<bookissue> issues = new ArrayList<>();

        String sql = "SELECT bi.*, b.book_name, b.author "
                + "FROM book_issue bi "
                + "INNER JOIN book b ON bi.book_id = b.book_id "
                + "WHERE bi.member_id = ? "
                + "ORDER BY bi.issue_date DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, memberId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    bookissue issue = mapIssue(rs);

                    issue.setBookName(rs.getString("book_name"));
                    issue.setAuthor(rs.getString("author"));

                    issues.add(issue);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return issues;
    }

    // Find one issue record
    public bookissue getIssueById(int issueId) {

        String sql =
                "SELECT * FROM book_issue WHERE issue_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, issueId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapIssue(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Return a book
    public boolean returnBook(
            int issueId,
            int memberId,
            LocalDate returnDate) {

        if (issueId <= 0 || memberId <= 0 || returnDate == null) {
            System.out.println("Invalid return details!");
            return false;
        }

        String issueSql =
                "SELECT book_id, issue_date, due_date, return_date "
                + "FROM book_issue "
                + "WHERE issue_id = ? AND member_id = ? FOR UPDATE";

        String bookSql =
                "SELECT price FROM book WHERE book_id = ? FOR UPDATE";

        String updateIssueSql =
                "UPDATE book_issue SET return_date = ?, fine = ? "
                + "WHERE issue_id = ? AND member_id = ? "
                + "AND return_date IS NULL";

        String updateBookSql =
                "UPDATE book SET available_copies = available_copies + 1 "
                + "WHERE book_id = ? "
                + "AND available_copies < total_copies";

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {

                int bookId;
                LocalDate issueDate;
                LocalDate dueDate;

                // Check issue record and member ownership
                try (PreparedStatement ps =
                             con.prepareStatement(issueSql)) {

                    ps.setInt(1, issueId);
                    ps.setInt(2, memberId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            System.out.println(
                                    "Issue record not found for this member!"
                            );
                            con.rollback();
                            return false;
                        }

                        if (rs.getDate("return_date") != null) {
                            System.out.println(
                                    "This book has already been returned!"
                            );
                            con.rollback();
                            return false;
                        }

                        bookId = rs.getInt("book_id");
                        issueDate = rs.getDate("issue_date").toLocalDate();
                        dueDate = rs.getDate("due_date").toLocalDate();
                    }
                }

                // Check return date
                if (returnDate.isBefore(issueDate)) {
                    System.out.println(
                            "Return date cannot be before issue date!"
                    );
                    con.rollback();
                    return false;
                }

                BigDecimal bookPrice;

                // Get book price
                try (PreparedStatement ps =
                             con.prepareStatement(bookSql)) {

                    ps.setInt(1, bookId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            System.out.println("Book not found!");
                            con.rollback();
                            return false;
                        }

                        bookPrice = rs.getBigDecimal("price");
                    }
                }

                // Calculate fine
                BigDecimal fine = calculateFine(
                        bookPrice,
                        dueDate,
                        returnDate
                );

                // Update issue record
                try (PreparedStatement ps =
                             con.prepareStatement(updateIssueSql)) {

                    ps.setDate(1, Date.valueOf(returnDate));
                    ps.setBigDecimal(2, fine);
                    ps.setInt(3, issueId);
                    ps.setInt(4, memberId);

                    if (ps.executeUpdate() != 1) {
                        con.rollback();
                        return false;
                    }
                }

                // Increase available copies
                try (PreparedStatement ps =
                             con.prepareStatement(updateBookSql)) {

                    ps.setInt(1, bookId);

                    if (ps.executeUpdate() != 1) {
                        System.out.println(
                                "Unable to update available copies!"
                        );
                        con.rollback();
                        return false;
                    }
                }

                con.commit();

                System.out.println("Book returned successfully!");
                System.out.println("Fine: ₹" + fine);

                return true;

            } catch (SQLException e) {

                con.rollback();
                throw e;

            } catch (RuntimeException e) {

                con.rollback();
                throw e;
            }

        } catch (SQLException e) {

            e.printStackTrace();
            return false;
        }
    }

    // Calculate fine: 5% of book price per late day
    public BigDecimal calculateFine(
            BigDecimal bookPrice,
            LocalDate dueDate,
            LocalDate returnDate) {

        if (bookPrice == null || dueDate == null || returnDate == null) {
            return BigDecimal.ZERO;
        }

        if (!returnDate.isAfter(dueDate)) {
            return BigDecimal.ZERO;
        }

        long lateDays = ChronoUnit.DAYS.between(
                dueDate, returnDate
        );

        return bookPrice
                .multiply(new BigDecimal("0.05"))
                .multiply(BigDecimal.valueOf(lateDays));
    }

    // Convert database row into bookissue object
    private bookissue mapIssue(ResultSet rs) throws SQLException {

        Date returnDate = rs.getDate("return_date");

        return new bookissue(
                rs.getInt("issue_id"),
                rs.getInt("book_id"),
                rs.getInt("member_id"),
                rs.getDate("issue_date").toLocalDate(),
                rs.getDate("due_date").toLocalDate(),
                returnDate == null ? null : returnDate.toLocalDate(),
                rs.getBigDecimal("fine")
        );
    }
}