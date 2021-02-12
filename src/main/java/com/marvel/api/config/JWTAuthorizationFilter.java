package com.marvel.api.config;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.marvel.api.config.SecurityConstants.HEADER_STRING;
import static com.marvel.api.config.SecurityConstants.PASSWORD;
import static com.marvel.api.config.SecurityConstants.SECRET;
import static com.marvel.api.config.SecurityConstants.TOKEN_PREFIX;
import static com.marvel.api.config.SecurityConstants.USERNAME;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
  public JWTAuthorizationFilter(final AuthenticationManager authenticationManager) {
    super(authenticationManager);
  }

  @Override
  protected void doFilterInternal(
      final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain)
      throws IOException, ServletException {
    final String header = request.getHeader(HEADER_STRING);

    if (header == null || !header.startsWith(TOKEN_PREFIX)) {
      chain.doFilter(request, response);
      return;
    }

    final UsernamePasswordAuthenticationToken authenticationToken;

    try {
      authenticationToken = getAuthenticationToken(request);
    } catch (final ExpiredJwtException | MalformedJwtException | UsernameNotFoundException e) {
      chain.doFilter(request, response);
      return;
    }

    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    chain.doFilter(request, response);
  }

  private UsernamePasswordAuthenticationToken getAuthenticationToken(
      final HttpServletRequest request) {
    final String token = request.getHeader(HEADER_STRING);

    if (token == null) {
      return null;
    }

    final String username =
        Jwts.parser()
            .setSigningKey(SECRET)
            .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
            .getBody()
            .getSubject();

    final UserDetails userDetails = getUserDetails(username);
    return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
  }

  private User getUserDetails(final String username) {
    if (USERNAME.equals(username)) {
      return new User(
          USERNAME, PASSWORD, AuthorityUtils.createAuthorityList("ROLE_USER", "ROLE_ADMIN"));
    } else {
      throw new UsernameNotFoundException("User not found");
    }
  }
}
