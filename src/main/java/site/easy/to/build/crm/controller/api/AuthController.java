package site.easy.to.build.crm.controller.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import site.easy.to.build.crm.entity.LoginRequest;
import site.easy.to.build.crm.entity.LoginResponse;
import site.easy.to.build.crm.entity.LoginToken;
import site.easy.to.build.crm.entity.User;
import site.easy.to.build.crm.repository.LoginTokenRepository;
import site.easy.to.build.crm.service.user.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final LoginTokenRepository loginTokenRepository;

    public AuthController(UserService userService,
                          LoginTokenRepository loginTokenRepository) {
        this.userService = userService;
        this.loginTokenRepository = loginTokenRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request,
                                   PasswordEncoder passwordEncoder,
                                   Authentication authentication) {
        User user = userService.findByEmail(request.getEmail());
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Incorrect password");
        }
        if (!userService.isManager(user)) {
            return ResponseEntity.status(401).body("User not authorized");
        }
        LoginToken loginToken = getLoginToken(user);
        loginTokenRepository.save(loginToken);

        LoginResponse response = new LoginResponse();
        response.setToken(loginToken.getToken());
        response.setUsername(user.getUsername());
        response.setRoles(List.of("ROLE_MANAGER"));

        return ResponseEntity.ok(response);
    }

    private LoginToken getLoginToken(User user) {
        LoginToken loginToken = new LoginToken();
        loginToken.setToken(UUID.randomUUID().toString());
        loginToken.setCreatedAt(LocalDateTime.now());
        loginToken.setExpireAt(loginToken.getCreatedAt().plusHours(1));
        loginToken.setUser(user);
        return loginToken;
    }
}
