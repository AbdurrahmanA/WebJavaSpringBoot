package com.example.demo.filters;

import com.example.demo.data.UserEntity;
import com.example.demo.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class RememberMeFilter extends OncePerRequestFilter {

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        boolean userLoggedIn = session != null && session.getAttribute("user") != null;

        if (!userLoggedIn && request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("rememberMeUser".equals(cookie.getName())) {
                    String login = cookie.getValue();
                    Optional<UserEntity> userOpt = userService.findByLogin(login);
                    userOpt.ifPresent(user -> {
                        HttpSession newSession = request.getSession(true);
                        newSession.setAttribute("user", user);
                    });
                    break;
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
