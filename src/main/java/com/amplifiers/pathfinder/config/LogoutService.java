package com.amplifiers.pathfinder.config;

import com.amplifiers.pathfinder.auth.AuthenticationService;
import com.amplifiers.pathfinder.entity.token.Token;
import com.amplifiers.pathfinder.entity.token.TokenRepository;
import com.amplifiers.pathfinder.entity.user.User;
import com.amplifiers.pathfinder.entity.user.UserRepository;
import com.amplifiers.pathfinder.exception.ResourceNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidAccessTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    @Override
    @Transactional
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) {
        Cookie responseCookie = new Cookie("refresh_token", "");
        responseCookie.setPath("/");
        responseCookie.setMaxAge(0);
        responseCookie.setHttpOnly(true);
        responseCookie.setSecure(false);
        response.addCookie(responseCookie);

        final String refreshToken = AuthenticationService.getCookieRefreshToken(request);
        final String userEmail;

        if (refreshToken == null) {
            response.setStatus(404);
            return;
        }

        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = userRepository.findByEmail(userEmail).orElseThrow(
                    () -> new ResourceNotFoundException("No user found with this email. Please try again."));
            if (jwtService.isTokenValid(refreshToken, user)) {
                revokeAllUserTokens(user);
                Token token = tokenRepository.findByToken(refreshToken).get();
                token.setRevoked(true);
                tokenRepository.save(token);
            }
        }
    }
}
