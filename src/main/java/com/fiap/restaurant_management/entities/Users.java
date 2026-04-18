package com.fiap.restaurant_management.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Convert;
import jakarta.persistence.UniqueConstraint;
import com.fiap.restaurant_management.enums.RoleEnumConverter;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.*;
import com.fiap.restaurant_management.enums.RoleEnum;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name = "uk_users_login", columnNames = "login"),
        @UniqueConstraint(name = "uk_users_email", columnNames = "email")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "login", nullable = false, unique = true)
    private String login;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Convert(converter = RoleEnumConverter.class)
    @Column(name = "role", nullable = false)
    private RoleEnum role;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at", nullable = true)
    private LocalDateTime deletedAt;

    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addresses;

    public void addAddress(Address address) {
        if (this.addresses == null) {
            this.addresses = new java.util.ArrayList<>();
        }
        address.linkToUser(this);
        this.addresses.add(address);
    }

    public void softDelete() {
        LocalDateTime now = LocalDateTime.now();
        this.deletedAt = now;
        if (this.addresses != null) {
            for (Address address : this.addresses) {
                address.setDeletedAt(now);
            }
        }
    }

    public boolean isEmailChanging(String email) {
        return !this.email.equalsIgnoreCase(email);
    }

    public boolean isLoginChanging(String login) {
        return !this.login.equalsIgnoreCase(login);
    }

    public boolean matchesPassword(String oldPassword){return this.password.equals(oldPassword);}

    public boolean passwordEquals(String newPassword){return this.password.equals(newPassword);}


}
