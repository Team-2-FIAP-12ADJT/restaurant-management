package com.fiap.restaurant_management.services;

import com.fiap.restaurant_management.dtos.RegisterRequestDTO;
import com.fiap.restaurant_management.dtos.UsersLoginRequestDTO;
import com.fiap.restaurant_management.dtos.UsersLoginResponseDTO;
import com.fiap.restaurant_management.dtos.UsersResponseDTO;
import com.fiap.restaurant_management.entities.Users;
import com.fiap.restaurant_management.enums.RoleEnum;
import com.fiap.restaurant_management.mappers.UsersMapper;
import com.fiap.restaurant_management.repositories.UsersRepository;
import com.fiap.restaurant_management.security.TokenService;
import com.fiap.restaurant_management.services.interfaces.AuthServiceContract;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j
public class AuthService implements AuthServiceContract {

        private final TokenService tokenService;
        private final UsersRepository usersRepository;
        private final UsersMapper userMapper;
        private final PasswordEncoder passwordEncoder;

        public AuthService(UsersRepository usersRepository, UsersMapper userMapper, PasswordEncoder passwordEncoder,
                        TokenService tokenService) {
                this.usersRepository = usersRepository;
                this.userMapper = userMapper;
                this.passwordEncoder = passwordEncoder;
                this.tokenService = tokenService;
        }

        @Override
        public UsersLoginResponseDTO login(UsersLoginRequestDTO loginRequestDTO) {
                Users user = usersRepository.findByLoginIgnoreCaseAndDeletedAtIsNull(loginRequestDTO.login())
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.UNAUTHORIZED,
                                                "Invalid login or password"));

                if (!passwordEncoder.matches(loginRequestDTO.password(), user.getPassword())) {
                        throw new ResponseStatusException(
                                        HttpStatus.UNAUTHORIZED,
                                        "Invalid login or password");
                }

                log.info("User authenticated successfully: {}", user.getLogin());

                return tokenService.generateToken(user);
        }

        @Override
        public UsersResponseDTO register(RegisterRequestDTO registerRequestDTO) {
                if (usersRepository.existsByLoginIgnoreCaseAndDeletedAtIsNull(registerRequestDTO.login())) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Login already exists");
                }

                if (usersRepository.existsByEmailIgnoreCaseAndDeletedAtIsNull(registerRequestDTO.email())) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
                }

                Users user = this.userMapper.toEntity(registerRequestDTO);
                user.setRole(RoleEnum.CLIENT);
                user.setPassword(passwordEncoder.encode(registerRequestDTO.password()));

                return this.userMapper.toResponseDTO(this.usersRepository.save(user));
        }
}
