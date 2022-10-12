package com.swoqe.evote.security.model;

import com.swoqe.evote.security.AuthoritiesNames;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "voters")
public class Voter extends BaseSqlEntity implements UserDetails {

    @LastModifiedDate
    private LocalDateTime modifiedAt;

    private boolean enabled = true;

    private String username;
    private String password;
    private byte[] publicKey;

    public Voter() {}

    public Voter(String username, String password) {
        this.username = username;
        this.password = password;
        this.enabled = true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new CustomAuthority(AuthoritiesNames.COMMON_USER));
    }

    @Override
    public boolean isAccountNonExpired() {
        return enabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return enabled;
    }
}
