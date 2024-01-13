package org.blbulyandavbulyan.blog.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.blbulyandavbulyan.blog.annotations.validation.user.ValidUsername;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;
    @ValidUsername
    @Column(name = "name")
    private String name;
    @Column(name = "password_hash")
    private String passwordHash;
    @Column(name = "tfa_enabled")
    private boolean tfaEnabled;
    @Column(name = "tfa_secret")
    private String tfaSecret;
    @ManyToMany
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;
    @OneToMany(mappedBy = "publisher", cascade = CascadeType.PERSIST)
    private List<Article> articles;
    @CreationTimestamp
    @Column(name = "registration_date")
    private ZonedDateTime registrationDate;
    public User(String name, List<Article> articles, ZonedDateTime registrationDate) {
        this.name = name;
        this.articles = articles;
        this.registrationDate = registrationDate;
    }
    public User(String name, String passwordHash) {
        this.name = name;
        this.passwordHash = passwordHash;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return name;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
