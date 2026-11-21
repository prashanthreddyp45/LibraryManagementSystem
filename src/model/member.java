package model;

public class member {

    private int memberId;
    private String memberName;
    private String phone;
    private String email;

    // Constructor
    public member(int memberId, String memberName,
                  String phone, String email) {

        this.memberId = memberId;
        this.memberName = memberName;
        this.phone = phone;
        this.email = email;
    }

    // Getters
    public int getMemberId() {
        return memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    // Setters
    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}