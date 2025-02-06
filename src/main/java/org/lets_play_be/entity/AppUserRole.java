package org.lets_play_be.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_roles")
public class AppUserRole implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotEmpty
    @Column(name = "role_name", unique = true, nullable = false)
    private String name;

    @ManyToMany(mappedBy = "roles")
    @JsonIgnore
    private List<AppUser> users = new ArrayList<>();

    public AppUserRole(String name) {
        this.name = name;
    }

    @Override
    public String getAuthority() {
        return this.name;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || o.getClass() != this.getClass()) return false;
        AppUserRole that = (AppUserRole) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "AppUserRole{" +
                "id=" + id +
                ", name='" + name +
                '}';
    }


}
