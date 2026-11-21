package service;

import dao.memberDAO;
import model.member;

import java.util.ArrayList;
import java.util.List;

public class memberService {

    private final memberDAO dao = new memberDAO();

    // Add a member
    public boolean addMember(member member) {

        if (!isValidMember(member)) {
            System.out.println("Invalid member details!");
            return false;
        }

        return dao.addMember(member);
    }

    // View all members
    public List<member> getAllMembers() {
        return dao.getAllMembers();
    }

    // Search members by name
    public List<member> searchMembersByName(String name) {

        if (name == null || name.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return dao.searchMembersByName(name.trim());
    }

    // Find a member by ID
    public member getMemberById(int memberId) {

        if (memberId <= 0) {
            return null;
        }

        return dao.getMemberById(memberId);
    }

    // Update member details
    public boolean updateMember(member member) {

        if (member == null || member.getMemberId() <= 0
                || !isValidMember(member)) {
            System.out.println("Invalid member details!");
            return false;
        }

        return dao.updateMember(member);
    }

    // Delete a member
    public boolean deleteMember(int memberId) {

        if (memberId <= 0) {
            System.out.println("Invalid member ID!");
            return false;
        }

        return dao.deleteMember(memberId);
    }

    // Validate member details
    private boolean isValidMember(member member) {

        if (member == null) {
            return false;
        }

        // Validate member name
        String name = member.getMemberName();

        if (name == null || name.trim().isEmpty()) {
            return false;
        }

        // Validate phone number
        String phone = member.getPhone();

        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }

        if (!phone.matches("^[6-9][0-9]{9}$")) {
            return false;
        }

        // Validate email
        String email = member.getEmail();

        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        if (!email.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            return false;
        }

        return true;
    }
}