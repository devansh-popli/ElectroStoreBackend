package com.lcwd.store.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Set;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
public class User implements UserDetails {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private String userId;
    private String name;
    @Column(unique = true)
    private String email;
    private String password;
    private String gender;
    @Column(length = 1000)
    private String about;
    private String imageName;
//    private String provider;
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Role> roles = new HashSet<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Cart cart;
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private Referral referral;
    private String parentReferralCode;
    private String bankAccountNumber;
    private String ifscCode;
    private String bankName;
    @OneToMany(mappedBy = "user",fetch=FetchType.LAZY)
    List<Order> orders = new ArrayList<>();
    private boolean referralRewardGiven=false;
    private Double oneTimeReferralEarning;
    private Double inActiveMoney;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<ScreenPermission> screenPermissions;
    @OneToMany(mappedBy = "referralUser",fetch = FetchType.LAZY)
    List<Order> ordersByReferralUser = new ArrayList<>();
    //must have to implement
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> simpleGrantedAuthorities = this.roles.stream().map(role -> new SimpleGrantedAuthority(role.getRoleName())).collect(Collectors.toSet());
        return simpleGrantedAuthorities;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public String getPassword() {
        return this.password;
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
