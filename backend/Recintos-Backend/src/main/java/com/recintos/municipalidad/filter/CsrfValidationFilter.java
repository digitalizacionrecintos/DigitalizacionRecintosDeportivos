package com.recintos.municipalidad.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CsrfValidationFilter extends OncePerRequestFilter {

    private static final String CSRF_HEADER = "X-CSRF-Token";
    private static final String CSRF_COOKIE = "csrfToken";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String method = request.getMethod();
        String origin = request.getHeader("Origin");
        boolean isCorsRequest = origin != null && !origin.isEmpty();

        if (isStateModifyingMethod(method)) {
            if (isCorsRequest) {
                filterChain.doFilter(request, response);
                return;
            }

            String requestToken = request.getHeader(CSRF_HEADER);
            HttpSession session = request.getSession(false);
            
            if (session == null) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"No session\"}");
                return;
            }

            String sessionToken = (String) session.getAttribute("csrfToken");

            if (requestToken == null || !requestToken.equals(sessionToken)) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Invalid CSRF token\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isStateModifyingMethod(String method) {
        return "POST".equalsIgnoreCase(method) ||
               "PUT".equalsIgnoreCase(method) ||
               "DELETE".equalsIgnoreCase(method) ||
               "PATCH".equalsIgnoreCase(method);
    }
}
