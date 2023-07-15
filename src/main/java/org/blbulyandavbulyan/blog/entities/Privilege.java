package org.blbulyandavbulyan.blog.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

@Entity
@Table(name = "privileges")
@Getter
@Setter
@NoArgsConstructor
public class Privilege implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "privilege_name")
    private String privilegeName;
    @Override
    public String getAuthority() {
        return privilegeName;
    }
}
