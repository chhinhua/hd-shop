package com.hdshop.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


import java.util.Optional;

@Configuration
//@EnableJpaAuditing(dateTimeProviderRef = "dateTimeProvider")
@EnableJpaAuditing
public class AuditingConfig implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        /*Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.of("system");
        }

        if (authentication.getPrincipal() instanceof User)
            return Optional.ofNullable(((UserDetails) authentication.getPrincipal()).getUsername());
        return Optional.ofNullable(authentication.getName());*/

        return Optional.of("system");
    }

   /* @Bean
    public DateTimeProvider dateTimeProvider() {
        return new CustomDateTimeProvider();
    }*/
}