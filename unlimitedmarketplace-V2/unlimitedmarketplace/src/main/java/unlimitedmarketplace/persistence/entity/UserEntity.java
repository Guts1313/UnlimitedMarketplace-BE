package unlimitedmarketplace.persistence.entity;


import jakarta.persistence.*;
import unlimitedmarketplace.domain.UserRoles;


@Entity
@Table(name = "app_user") // Ensure this matches your actual table name
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true ,name = "user_name")
    private String userName;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(unique = true,name = "email")
    private String email;

    @Column(unique = false,name = "role")
    private UserRoles role;

    // Standard getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public void setUserRole(UserRoles role){this.role = role;}
    public UserRoles getUserRole(Long id){return role;}

}
