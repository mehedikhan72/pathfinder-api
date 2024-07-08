package com.amplifiers.pathfinder.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService service;


  @Value("${application.security.jwt.refresh-token.expiration}")
  private long refreshExpiration;

  private void attachRefreshTokenCookie(
          AuthenticationResponse authenticationResponse,
          HttpServletResponse response
  ) {
    String refreshToken = authenticationResponse.getRefreshToken();
    Cookie cookie = new Cookie("refresh_token", refreshToken);
    cookie.setPath("/");
    cookie.setMaxAge((int) refreshExpiration);
    cookie.setHttpOnly(true);
    cookie.setSecure(false);
    response.addCookie(cookie);
  }

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/register")
  public ResponseEntity<AuthenticationResponse> register(
      @RequestBody RegisterRequest request,
      HttpServletResponse response
  ) {
    AuthenticationResponse authenticationResponse = service.register(request);
    attachRefreshTokenCookie(authenticationResponse, response);
    return ResponseEntity.ok(authenticationResponse);
  }
  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> authenticate(
      @RequestBody AuthenticationRequest request,
      HttpServletResponse response
  ) {
    AuthenticationResponse authenticationResponse = service.authenticate(request);
    attachRefreshTokenCookie(authenticationResponse, response);
    return ResponseEntity.ok(authenticationResponse);
  }

  @PostMapping("/refresh-token")
  public void refreshToken(
      HttpServletRequest request,
      HttpServletResponse response
  ) throws IOException {
    service.refreshToken(request, response);
  }


}
