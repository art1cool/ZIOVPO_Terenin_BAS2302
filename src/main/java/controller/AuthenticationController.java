package controller;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import configuration.JwtTokenProvider;
import entity.UserEntity;
import entity.UserSessionEntity;
import model.AuthenticationRequest;
import model.AuthenticationResponse;
import repository.UserRepository;
import model.User;
import service.UserService;
import service.SessionService;
import repository.UserSessionRepository;
import enums.SessionStatus;
import io.jsonwebtoken.Claims;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class AuthenticationController {

    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final SessionService sessionService;

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody AuthenticationRequest request) {
        try {
            String email = request.getEmail();

            UserEntity user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            email, request.getPassword())
            );

            String accessToken = jwtTokenProvider.createAccessToken(
                    email, user.getRole().getGrantedAuthorities());
            String refreshToken = jwtTokenProvider.createRefreshToken(email);

            sessionService.createSession(user, refreshToken, request.getDeviceId());

            return ResponseEntity.ok(
                    new AuthenticationResponse(
                            email, accessToken, refreshToken));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user) {
        try {
            UserEntity registeredUser = userService.registerUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody AuthenticationResponse refreshRequest) {
        try {
            String refreshToken = refreshRequest.getRefreshToken();

            if (!jwtTokenProvider.validateToken(refreshToken)) {
                String email = null;
                try {
                    Claims claims = jwtTokenProvider.getClaimsFromExpiredToken(refreshToken);
                    email = claims.getSubject();

                    if (email != null) {
                        UserEntity user = userRepository.findByEmail(email).orElse(null);
                        if (user != null) {
                            Optional<UserSessionEntity> sessionOpt = userSessionRepository.findByRefreshToken(refreshToken);
                            sessionOpt.ifPresent(session -> {
                                if (session.getStatus() == SessionStatus.ACTIVE) {
                                    session.setStatus(SessionStatus.EXPIRED);
                                    userSessionRepository.save(session);
                                }
                            });
                        }
                    }
                } catch (Exception ex) {
                }

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token is expired or invalid");
            }

            String email = jwtTokenProvider.getEmailFromToken(refreshToken);
            UserEntity user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            UserSessionEntity oldSession = sessionService.handleRefreshOperation(user.getId(), refreshToken);
            if (oldSession == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired session");
            }

            String newAccessToken = jwtTokenProvider.createAccessToken(email, user.getRole().getGrantedAuthorities());
            String newRefreshToken = jwtTokenProvider.createRefreshToken(email);

            // Создаем новую сессию и помечаем старую как LOGGED_OUT
            sessionService.createNewSessionFromRefresh(oldSession, newRefreshToken);

            return ResponseEntity.ok(new AuthenticationResponse(email, newAccessToken, newRefreshToken));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh failed");
        }
    }
}