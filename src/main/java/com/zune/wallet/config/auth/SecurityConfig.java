package com.zune.wallet.config.auth;

import com.zune.wallet.api.auth.service.JwtUserDetailsService;
import com.zune.wallet.domain.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@Configuration
public class SecurityConfig {
    private final JwtUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;

    private static final String[] PUBLIC_URIS = {
            "/api/v1/member/**",
            "/api/v1/auth/**",
    };

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //h2 ignore auth
    @Bean
    @ConditionalOnProperty(name = "spring.h2.console.enabled", havingValue = "true")
    public WebSecurityCustomizer configureH2ConsoleEnable() {
        return web -> web.ignoring().requestMatchers(PathRequest.toH2Console());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //CSRF, CORS
        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(Customizer.withDefaults());

        //no session, stateless
        http.sessionManagement(sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        //off FormLogin, BasicHttp
        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);

        //add auth filter
        http.addFilterBefore(new JwtRequestFilter(jwtUtil, customUserDetailsService), UsernamePasswordAuthenticationFilter.class);

        //add whitelist
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(PUBLIC_URIS).permitAll()
                .anyRequest().permitAll()
        );

        return http.build();
    }

}
