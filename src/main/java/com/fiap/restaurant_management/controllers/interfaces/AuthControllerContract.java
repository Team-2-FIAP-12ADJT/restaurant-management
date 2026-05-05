package com.fiap.restaurant_management.controllers.interfaces;

import com.fiap.restaurant_management.controllers.ApiPaths;
import com.fiap.restaurant_management.dtos.RegisterRequestDTO;
import com.fiap.restaurant_management.dtos.ResourceErrorResponseDTO;
import com.fiap.restaurant_management.dtos.UsersLoginRequestDTO;
import com.fiap.restaurant_management.dtos.UsersLoginResponseDTO;
import com.fiap.restaurant_management.dtos.UsersResponseDTO;
import com.fiap.restaurant_management.dtos.ValidationErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(ApiPaths.V1_AUTH)
@Validated
@Tag(name = "Auth", description = "Gerenciamento de autenticação")
public interface AuthControllerContract {

        @Operation(description = "Autentica um usuário e retorna um token JWT", summary = "Login de usuário")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Autenticado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsersLoginResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorResponseDTO.class))),
                        @ApiResponse(responseCode = "401", description = "Login ou senha incorretos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResourceErrorResponseDTO.class)))
        })
        @PostMapping("/login")
        ResponseEntity<UsersLoginResponseDTO> login(
                        @Valid @org.springframework.web.bind.annotation.RequestBody UsersLoginRequestDTO usersLoginRequestDTO);

        @Operation(description = "Registra um novo usuário CLIENT e retorna os dados do usuário criado", summary = "Registro de usuário")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Usuário registrado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsersResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorResponseDTO.class))),
                        @ApiResponse(responseCode = "409", description = "Login ou e-mail já cadastrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResourceErrorResponseDTO.class)))
        })
        @PostMapping("/register")
        ResponseEntity<UsersResponseDTO> register(
                        @Valid @org.springframework.web.bind.annotation.RequestBody RegisterRequestDTO registerRequestDTO);
}
