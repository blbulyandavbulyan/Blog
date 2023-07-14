package org.blbulyandavbulyan.blog.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "name")
    private String name;
    @Column(name = "password_hash")
    private String passwordHash;
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
}
