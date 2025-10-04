package com.authservice.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
@Table(name="users")
@ToString

public class UserInfo {
    @Id
    @Column(name="user_id")
    private String userId;
    private  String username;
    private String password;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name="user_role_join",
            joinColumns=@JoinColumn(name="user_id"),
            inverseJoinColumns = @JoinColumn(name="role_id")
    )
    @Builder.Default
    private Set<UserRole> roles= new HashSet<>();
}
