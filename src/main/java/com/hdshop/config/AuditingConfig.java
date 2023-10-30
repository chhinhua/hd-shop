package com.hdshop.config;

import com.hdshop.entity.User;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class AuditingConfig implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                    authentication instanceof AnonymousAuthenticationToken
        ) {
            return Optional.empty();
        }

        if (authentication.getPrincipal() instanceof User)
            return Optional.ofNullable(((UserDetails) authentication.getPrincipal()).getUsername());
        return Optional.ofNullable(authentication.getName());
    }
}