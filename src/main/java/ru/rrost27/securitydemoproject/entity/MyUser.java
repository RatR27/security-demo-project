package ru.rrost27.securitydemoproject.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table
@Data
@NoArgsConstructor
public class MyUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    private String firstName;

    private String lastName;

    private String phone;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "myUsers_roles",
            joinColumns = @JoinColumn(name = "myUser_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Collection<Role> roles;

}
