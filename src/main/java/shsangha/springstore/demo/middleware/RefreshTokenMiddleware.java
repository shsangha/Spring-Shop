package shsangha.springstore.demo.middleware;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;
import shsangha.springstore.demo.services.JWTUtilsService;
import shsangha.springstore.demo.services.TwoCookieAuthApproachHelperService;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(1)
public class RefreshTokenMiddleware extends OncePerRequestFilter {

    private JWTUtilsService jwtUtilsService;
    private TwoCookieAuthApproachHelperService twoCookieAuthApproachHelperService;



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        System.out.println(WebUtils.getCookie(request, "payload").getValue());


        // TODO SEND THE RESPONSE REFRESH TOKEN


        filterChain.doFilter(request,response);
    }
}