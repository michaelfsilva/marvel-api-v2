package com.marvel.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;

import static com.marvel.api.config.SecurityConstants.SIGN_UP_URL;

@Configuration
@Component
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(final HttpSecurity httpSecurity) throws Exception {
    httpSecurity
        .cors()
        .configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues())
        .and()
        .csrf()
        .disable()
        .authorizeRequests()
        .antMatchers(HttpMethod.GET, SIGN_UP_URL)
        .permitAll()
        .antMatchers("/api/**")
        //        .hasAnyRole()
        .hasRole("USER")
        .and()
        .addFilter(new JWTAuthenticationFilter(authenticationManager()))
        .addFilter(new JWTAuthorizationFilter(authenticationManager()));
  }
}
