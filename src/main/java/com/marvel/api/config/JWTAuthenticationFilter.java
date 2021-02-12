package com.marvel.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marvel.api.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

import static com.marvel.api.config.SecurityConstants.EXPIRATION_TIME;
import static com.marvel.api.config.SecurityConstants.HEADER_STRING;
import static com.marvel.api.config.SecurityConstants.SECRET;
import static com.marvel.api.config.SecurityConstants.TOKEN_PREFIX;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
  private final AuthenticationManager authenticationManager;

  public JWTAuthenticationFilter(final AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }

  @Override
  public Authentication attemptAuthentication(
      final HttpServletRequest request, final HttpServletResponse response) {
    try {
      final User user = new ObjectMapper().readValue(request.getInputStream(), User.class);
      return this.authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected void successfulAuthentication(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final FilterChain chain,
      final Authentication authResult)
      throws IOException, ServletException {
    final String username =
        ((org.springframework.security.core.userdetails.User) authResult.getPrincipal())
            .getUsername();
    final String token =
        Jwts.builder()
            .setSubject(username)
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .signWith(SignatureAlgorithm.HS512, SECRET)
            .compact();
    final String bearerToken = TOKEN_PREFIX + token;
    response.getWriter().write(bearerToken); // adding token into url /login response body
    response.addHeader(HEADER_STRING, bearerToken);
  }
}
