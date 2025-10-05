package ru.otus.servlet;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Set;

public class AuthorizationFilter implements Filter {

    private ServletContext context;
    private Set<String> openPrefixes;

    @Override
    public void init(FilterConfig filterConfig) {
        this.context = filterConfig.getServletContext();
        this.openPrefixes = Set.of("/login", "/css/", "/js/", "/images/", "/static/", "/api/user");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String contextPath = request.getContextPath();
        String uri = request.getRequestURI();
        String path = uri.substring(contextPath.length());
        this.context.log("Requested Resource:" + path);

        boolean isOpen = openPrefixes.stream().anyMatch(path::startsWith);
        if (isOpen) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        HttpSession session = request.getSession(false);
        Object logged = (session == null) ? null : session.getAttribute("login");
        if (logged != null) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            response.sendRedirect(contextPath + "/login");
        }
    }

    @Override
    public void destroy() {
        // Not implemented
    }
}
