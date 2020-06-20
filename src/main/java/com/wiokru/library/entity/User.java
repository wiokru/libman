package com.wiokru.library.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString(exclude = "password", callSuper = true)
@EqualsAndHashCode
@NoArgsConstructor
@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = {"email"}, name = "uniqueEmailConstraint")})
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "password")
    private String password;

    @Column(name = "city")
    private String city;

    @Column(name = "phone")
    private String phone;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    private Set<Reserved> reservedBooks;

    public User(String email, String name, String surname, String password, String city, String phone) {
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.city = city;
        this.phone = phone;
    }

    public void addRole(Role role) {
        if(roles == null){
            roles = new HashSet<Role>();
        }
        roles.add(role);
    }

    public Boolean isAdmin() {
        for(Role role : roles) {
            if(role.getName().equals("ADMINISTRATOR"))
                return true;
        }
        return false;
    }

    public Boolean isLibrarian() {
        for(Role role : roles) {
            if(role.getName().equals("LIBRARIAN"))
                return true;
        }
        return false;
    }

    public String listRoles() {
        StringBuilder rolesList = new StringBuilder();

        for (Role role : roles) {
            rolesList.append(role.getName()).append(", ");
        }
        return  rolesList.toString();
    }


}
