package mutlu.ticketing_admin.entity;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import mutlu.ticketing_admin.enums.UserType;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a user. Includes both account information (login etc) and usage details.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "userId")
@Where(clause = "deleted=false") //Do not return soft deleted entities in queries.
@Entity(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;
    @NotNull
    private UserType userType;
    @Email
    private String email;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    @NotBlank
    private String passwordHash;
    @OneToMany(mappedBy = "user")
    private List<Ticket> ticketList = new ArrayList<>();

    public Long getUserId() {
        return userId;
    }

    public UserType getUserType() {
        return userType;
    }

    public String getEmail() {
        return email;
    }


    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public List<Ticket> getTicketList() {
        return ticketList;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
