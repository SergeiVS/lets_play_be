package org.lets_play_be.entity.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.lets_play_be.entity.notification.Invite;
import org.lets_play_be.entity.notification.Notification;

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

    @OneToOne(orphanRemoval = true)
    private UserAvailability availability;

//    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user", orphanRemoval = true)
//    private List<Notification> notifications;

    public AppUser(String name, String email, String password, String avatarUrl) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.avatarUrl = avatarUrl;
        this.roles = new ArrayList<>();
//        this.notifications = new ArrayList<>();
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
