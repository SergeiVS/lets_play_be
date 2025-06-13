package org.lets_play_be.entity.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.lets_play_be.entity.Invite.Invite;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "accounts")
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    @Email(message = "email is in wrong format")
    private String email;

    @Column(nullable = false)
    private String password;

    private String avatarUrl;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<AppUserRole> roles;

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    private UserAvailability availability;

    public AppUser(String name, String email, String password, String avatarUrl) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.avatarUrl = avatarUrl;
        this.roles = new ArrayList<>();
    }

    public String[] getRolesStrings(){
        String[] rolesStringsArray = new String[roles.size()];
        for(int i = 0; i < roles.size(); i++){
            rolesStringsArray[i] = roles.get(i).getName();
        }
        return rolesStringsArray;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || o.getClass() != this.getClass()) return false;
        AppUser that = (AppUser) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
