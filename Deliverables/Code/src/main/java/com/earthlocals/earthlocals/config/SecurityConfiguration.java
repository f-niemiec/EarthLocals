package com.earthlocals.earthlocals.config;

import com.earthlocals.earthlocals.model.UtenteRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {


    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }

    @Bean
    public UserDetailsService userDetailsService(UtenteRepository utenteRepository) {
        return utenteRepository;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);

        return new MyAuthenticationManager(authenticationProvider);
    }

    @Value("${earthlocals.rememberme.key}")
    private String rememberMeKey;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, UserDetailsService userDetailsService) throws Exception {
        http
                .logout((logout) -> {
                    logout
                            .logoutUrl("/logout")
                            .logoutSuccessUrl("/")
                            .invalidateHttpSession(true)
                            .deleteCookies("JSESSIONID")
                            .permitAll();
                })
                .formLogin((form) -> {
                    form.usernameParameter("email").loginPage("/login").permitAll();
                })
                .rememberMe(
                        rememberMe ->
                                rememberMe
                                        .key(rememberMeKey)
                                        .userDetailsService(userDetailsService)
                )
                .authorizeHttpRequests((authorize) -> {
                    authorize

                            .requestMatchers("/moderator/**").hasRole("MODERATOR")
                            .requestMatchers("/account-manager/**").hasRole("ACCOUNT_MANAGER")
                            .requestMatchers("/organizer/**").hasRole("ORGANIZER")
                            .requestMatchers("/volunteer/**").hasRole("VOLUNTEER")
                            .requestMatchers("/account/**").authenticated()
                            .anyRequest().permitAll()
                    ;
                });


        return http.build();
    }

}
