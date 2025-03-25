package site.easy.to.build.crm.service.api;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import site.easy.to.build.crm.entity.LoginToken;
import site.easy.to.build.crm.entity.Role;
import site.easy.to.build.crm.entity.User;
import site.easy.to.build.crm.exception.ApiException;
import site.easy.to.build.crm.repository.LoginTokenRepository;
import site.easy.to.build.crm.repository.UserRepository;
import site.easy.to.build.crm.util.ApiResponseUtil;
import site.easy.to.build.crm.util.EmailTokenUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
public class ApiLoginServiceImpl implements ApiLoginService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final LoginTokenRepository loginTokenRepository;

    public ApiLoginServiceImpl(PasswordEncoder passwordEncoder,
                               UserRepository userRepository,
                               LoginTokenRepository loginTokenRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.loginTokenRepository = loginTokenRepository;
    }

    @Override
    public void verifyToken(String token) throws Exception {
        LoginToken loginToken = loginTokenRepository.getUserToken(token.replace("Bearer ", ""));
        if (loginToken == null) {
            throw new Exception("Invalid token");
        }
        if (loginToken.getExpireAt().isAfter(LocalDateTime.now())) {
            throw new Exception("Expired token");
        }
    }
}
