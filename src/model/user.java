package model;

public class user {

    private int userId;
    private String username;
    private String passwordHash;
    private String role;
    private Integer memberId;

    // Constructor
    public user(int userId, String username, String passwordHash,
                String role, Integer memberId) {

        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.memberId = memberId;
    }

    // Getters
    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getRole() {
        return role;
    }

    public Integer getMemberId() {
        return memberId;
    }

    // Setters
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }
}