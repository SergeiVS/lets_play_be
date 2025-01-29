package org.lets_play_be.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "accounts")
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true)
    String name;

    @Column(nullable = false, unique = true)
    @Email(message = "email is in wrong format")
    String email;

    @Column(nullable = false)
    String password;

    String avatarUrl;

    @ManyToMany(fetch = FetchType.EAGER)
    List<AppUserRole> roles;
}
