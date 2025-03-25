package com.example.atp_back.config.filter;

import com.example.atp_back.user.model.User;
import com.example.atp_back.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = null;
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            log.info("No cookies found");
            doFilter(request, response, filterChain);
            return;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("ATOKEN")) {
                token = cookie.getValue();
                break;
            }
        }

        if (token != null) {
            User user = JwtUtil.buildUserDataFromToken(token);
            if (user != null) {
                // user.setVersion(0L);
                UsernamePasswordAuthenticationToken identityToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                identityToken.setDetails(user);
                SecurityContextHolder.getContext().setAuthentication(identityToken);
                log.info("user {} authenticated", user.getIdx());
            }
            else {
                log.info("no user");
            }
        }
        else {
            log.info("no token");
        }

        filterChain.doFilter(request, response);
    }
}
