package com.mindhub.homebanking.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@EnableWebSecurity
@Configuration
public class WebAuthorization {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.authorizeRequests().antMatchers("/web/index.html").permitAll()
                                        .antMatchers("/web/security.html").permitAll()
                                        .antMatchers("/web/images/**").permitAll()
                                        .antMatchers("/web/scripts/index.js").permitAll()
                                        .antMatchers("/web/scripts/general.js").permitAll()
                                        .antMatchers("/web/styles/**").permitAll()
                                        .antMatchers("/web/fonts/**").permitAll()
                                        .antMatchers("/api/login").permitAll()
                                        .antMatchers("/api/loans").permitAll()
                                        .antMatchers("/api/logout").hasAnyAuthority("CLIENT", "ADMIN")
                                        .antMatchers("/api/clients/current/**").hasAuthority("CLIENT")
                                        .antMatchers("/api/transactions").hasAnyAuthority("CLIENT", "ADMIN")
                                        .antMatchers("/api/accounts/{id}").hasAuthority("ADMIN")
                                        .antMatchers(HttpMethod.GET,"/api/**").hasAuthority("ADMIN")
                                        .antMatchers("/rest/**").hasAuthority("ADMIN")
                                        .antMatchers("/manager.html").hasAuthority("ADMIN")
                                        .antMatchers("/h2-console").hasAuthority("ADMIN")
                                        .antMatchers("/web/**").hasAuthority("CLIENT")
                                        .antMatchers(HttpMethod.POST, "/api/clients", "/cards/transactions").permitAll()
                                        .antMatchers(HttpMethod.POST, "/api/clients/current/accounts").hasAuthority("CLIENT")
                                        .antMatchers(HttpMethod.POST, "/api/clients/current/cards").hasAuthority("CLIENT")
                                        .antMatchers(HttpMethod.POST, "/api/loans").hasAuthority("CLIENT")
                                        .antMatchers(HttpMethod.POST, "/api/transactions").hasAuthority("CLIENT")
                                        .antMatchers(HttpMethod.POST, "/api/clients/current/lastLogin").hasAuthority("CLIENT");

        httpSecurity.formLogin()
                    .usernameParameter("email")
                    .passwordParameter("password")
                    .loginPage("/api/login");


        httpSecurity.logout().logoutUrl("/api/logout").deleteCookies("JSESSIONID");

        // turn off checking for CSRF tokens
        httpSecurity.csrf().disable();

        //disabling frameOptions so h2-console can be accessed
        httpSecurity.headers().frameOptions().disable();

        //redirect when a not authenticated user tries to access a forbidden /web or manager url. Otherwise, send a forbidden http response
        httpSecurity.exceptionHandling().authenticationEntryPoint((req, res, exc) -> {
            if(req.getRequestURI().contains("/web/") || req.getRequestURI().equals("/manager.html")){
                res.sendRedirect("/web/index.html");
            }
            else {
                res.sendError(HttpServletResponse.SC_FORBIDDEN);
            }
        });

        // redirect when a not authorized user tries to access a forbidden /web url. Otherwise, send a forbidden http response
        httpSecurity.exceptionHandling().accessDeniedHandler((req, res, ex) -> redirectPageToLandingPage(req, res));

        // if login is successful, just clear the flags asking for authentication
        httpSecurity.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

        // if login fails, just send an authentication failure response
        httpSecurity.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // if logout is successful, just send a success response
        httpSecurity.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());

        return httpSecurity.build();

    }

    private void redirectPageToLandingPage(HttpServletRequest request, HttpServletResponse response) throws IOException{

        if(request.getRequestURI().contains("/web/accounts.html")){
            response.sendRedirect("/manager.html");
        }
        else if(request.getRequestURI().contains("/web/")){
            response.sendRedirect("/web/index.html");
        }
        else {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }


    private void clearAuthenticationAttributes(HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if (session != null) {
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }

    }

}
