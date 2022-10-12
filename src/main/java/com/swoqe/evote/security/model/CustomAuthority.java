package com.swoqe.evote.security.model;

import com.swoqe.evote.security.AuthoritiesNames;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

@NoArgsConstructor
public class CustomAuthority implements GrantedAuthority {

    @Id
    @Enumerated(value = EnumType.STRING)
    private AuthoritiesNames authority;

    public CustomAuthority(AuthoritiesNames authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return authority.name();
    }
}
