package com.zune.wallet.config.auth;

import com.zune.wallet.domain.common.util.JsonUtil;
import com.zune.wallet.domain.common.util.JwtUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtRequestFilter implements Filter {
    private static final String AUTH_HEADER_NAME = "Authorization";
    private static final String AUTH_HEADER_TOKEN_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;
    private final UserDetailsService jwtUserDetailsService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String authorizationHeader = request.getHeader(AUTH_HEADER_NAME);
        String token = null;
        try {
            if (authorizationHeader != null && authorizationHeader.startsWith(AUTH_HEADER_TOKEN_PREFIX)) {
                token = authorizationHeader.substring(7);
                if (jwtUtil.validateToken(token)) {
                    Long userId = jwtUtil.getUserId(token);
                    UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(userId.toString());

                    if (userDetails != null) {
                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    }
                }
            }
        } catch (Exception e) {
            log.info("Unexpected token : {}, ({}), exception={}",
                    request.getRequestURL(),
                    token,
                    e.getClass().getName()
            );
            sendUnauthorized((HttpServletResponse) response, token);
            return;
        }
        chain.doFilter(request, response);
    }

    private static void sendUnauthorized(HttpServletResponse httpServletResponse, String token) throws IOException {
        Map<String, String> responseBody = Map.of(
                "error", "invalid_token",
                "error_description", String.format("Invalid access token: %s", token)
        );

        httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        httpServletResponse.getWriter().write(JsonUtil.convertToJson(responseBody));
    }

}
