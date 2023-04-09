package ru.job4j.cinema.filter;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException,
            ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String uri = req.getRequestURI();
        if (uri.endsWith("loginPage")
                || uri.endsWith("login")
                || uri.endsWith("index")
                || uri.contains("/poster")
                || uri.endsWith("registration")
                || uri.endsWith("registerPage")) {
            chain.doFilter(req, res);
            return;
        }
        if (req.getSession()
               .getAttribute("client") == null) {
            res.sendRedirect(req.getContextPath() + "/index");
            return;
        }
        chain.doFilter(req, res);

    }

}
