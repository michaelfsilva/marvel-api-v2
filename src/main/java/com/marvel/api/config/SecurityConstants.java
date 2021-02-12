package com.marvel.api.config;

public class SecurityConstants {
  public static final String SECRET = "MarvelAPI";
  static final String TOKEN_PREFIX = "Bearer ";
  static final String HEADER_STRING = "Authorization";
  static final String SIGN_UP_URL = "/users/sign-up";
  public static final long EXPIRATION_TIME = 1800000L; // 30 minutes
  static final String USERNAME = "test";
  static final String PASSWORD = "marvel";
}
