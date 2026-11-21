package dao;

import model.member;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class memberDAO {

    // Add a member
    public boolean addMember(member member) {

        String sql = "INSERT INTO member "
                + "(member_name, phone, email) VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, member.getMemberName());
            ps.setString(2, member.getPhone());
            ps.setString(3, member.getEmail());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get all members
    public List<member> getAllMembers() {

        List<member> members = new ArrayList<>();

        String sql = "SELECT * FROM member";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                member m = new member(
                        rs.getInt("member_id"),
                        rs.getString("member_name"),
                        rs.getString("phone"),
                        rs.getString("email")
                );

                members.add(m);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return members;
    }

    // Get member by ID
    public member getMemberById(int memberId) {

        String sql = "SELECT * FROM member WHERE member_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, memberId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return new member(
                            rs.getInt("member_id"),
                            rs.getString("member_name"),
                            rs.getString("phone"),
                            rs.getString("email")
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Search members by name
    public List<member> searchMembersByName(String name) {

        List<member> members = new ArrayList<>();

        String sql =
                "SELECT * FROM member WHERE member_name LIKE ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + name + "%");

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    member m = new member(
                            rs.getInt("member_id"),
                            rs.getString("member_name"),
                            rs.getString("phone"),
                            rs.getString("email")
                    );

                    members.add(m);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return members;
    }

    // Update member details
    public boolean updateMember(member member) {

        String sql = "UPDATE member SET "
                + "member_name = ?, phone = ?, email = ? "
                + "WHERE member_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, member.getMemberName());
            ps.setString(2, member.getPhone());
            ps.setString(3, member.getEmail());
            ps.setInt(4, member.getMemberId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Safely delete a member and linked login account
    public boolean deleteMember(int memberId) {

        String checkIssues =
                "SELECT COUNT(*) FROM book_issue WHERE member_id = ?";

        String deleteUser =
                "DELETE FROM `user` WHERE member_id = ?";

        String deleteMember =
                "DELETE FROM member WHERE member_id = ?";

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {

                // Step 1: Check borrowing history
                try (PreparedStatement ps =
                             con.prepareStatement(checkIssues)) {

                    ps.setInt(1, memberId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (rs.next() && rs.getInt(1) > 0) {

                            con.rollback();

                            System.out.println(
                                    "Cannot delete member because "
                                    + "borrowing history exists."
                            );

                            return false;
                        }
                    }
                }

                // Step 2: Delete linked login account
                try (PreparedStatement ps =
                             con.prepareStatement(deleteUser)) {

                    ps.setInt(1, memberId);
                    ps.executeUpdate();
                }

                // Step 3: Delete member
                try (PreparedStatement ps =
                             con.prepareStatement(deleteMember)) {

                    ps.setInt(1, memberId);

                    if (ps.executeUpdate() == 0) {

                        con.rollback();
                        return false;
                    }
                }

                // Step 4: Commit transaction
                con.commit();

                return true;

            } catch (SQLException e) {

                con.rollback();
                e.printStackTrace();

                return false;
            }

        } catch (SQLException e) {

            e.printStackTrace();
            return false;
        }
    }
}