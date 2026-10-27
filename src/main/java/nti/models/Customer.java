package nti.models;

import java.time.LocalDateTime;

public class Customer {

    private long id;
    private long userId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private byte[] profilePic;
    private LocalDateTime createdAt;

    public Customer() {
    }

    public Customer(long userId, String name, String email,
                    String phone, String address, byte[] profilePic) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.profilePic = profilePic;
    }

    public Customer(long id, long userId, String name, String email,
                    String phone, String address, byte[] profilePic,
                    LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.profilePic = profilePic;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public byte[] getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(byte[] profilePic) {
        this.profilePic = profilePic;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean hasProfilePic() {
        return profilePic != null && profilePic.length > 0;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", profilePic=" + (hasProfilePic() ? "[" + profilePic.length + " bytes]" : "null") +
                ", createdAt=" + createdAt +
                '}';
    }
}