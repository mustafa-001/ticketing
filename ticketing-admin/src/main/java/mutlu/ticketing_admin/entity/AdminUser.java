package mutlu.ticketing_admin.entity;


import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * Entity representing an admin.
 */
@Where(clause = "deleted=false") //Do not return soft deleted entities in queries.
@Entity
public class AdminUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;
    @Email
    private String email;
    private String firstName;
    private String lastName;

    private boolean deleted = false;
    @NotBlank
    private String passwordHash;

    public Long getUserId() {
        return userId;
    }


    public String getEmail() {
        return email;
    }

    public AdminUser setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public AdminUser setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public AdminUser setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public AdminUser setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
        return this;
    }

    public AdminUser setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public AdminUser setDeleted(boolean deleted) {
        this.deleted = deleted;
        return this;
    }
}
