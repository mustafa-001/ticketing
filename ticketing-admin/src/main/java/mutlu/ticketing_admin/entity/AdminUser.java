package mutlu.ticketing_admin.entity;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import mutlu.ticketing_admin.common.UserType;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a user. Includes both account information (login etc) and usage details.
 */
//If another entity includes a User field when serializing/deserializing refer that field with it userId.
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "userId")
@Entity
public class AdminUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;
    @Email
    private String email;
    private String firstName;
    private String lastName;
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
}
