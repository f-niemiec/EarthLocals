package com.earthlocals.earthlocals.config;

import jakarta.transaction.Transactional;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class MyAuthenticationManager implements AuthenticationManager {

    private final ProviderManager providerManager;

    public MyAuthenticationManager(AuthenticationProvider... providers) {
        this.providerManager = new ProviderManager(providers);
    }

    @Override
    @Transactional
    @NullMarked
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return providerManager.authenticate(authentication);
    }
}
