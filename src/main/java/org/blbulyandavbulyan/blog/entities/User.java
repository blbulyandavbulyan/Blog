package org.blbulyandavbulyan.blog.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @Column(name = "id")
    private Long id;
    @Column(name = "name")
    private String name;
    @OneToMany
    private List<Article> articles;
    @Column(name = "registration_date")
    private ZonedDateTime registrationDate;
    public User(String name, List<Article> articles, ZonedDateTime registrationDate) {
        this.name = name;
        this.articles = articles;
        this.registrationDate = registrationDate;
    }
}
