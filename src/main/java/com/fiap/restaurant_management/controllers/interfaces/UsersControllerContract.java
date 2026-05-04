package com.fiap.restaurant_management.controllers.interfaces;

import com.fiap.restaurant_management.controllers.ApiPaths;
import com.fiap.restaurant_management.dtos.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping(ApiPaths.V1_USERS)
@Validated
@Tag(name = "Users", description = "Sistema de gerenciamento de usuários")
public interface UsersControllerContract {

        @Operation(description = "Cria um novo usuário no sistema", summary = "Criação de usuário")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsersResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorResponseDTO.class))),
                        @ApiResponse(responseCode = "409", description = "Login ou e-mail já cadastrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResourceErrorResponseDTO.class)))
        })
        @PostMapping
        ResponseEntity<UsersResponseDTO> create(
                        @Valid @org.springframework.web.bind.annotation.RequestBody UsersRequestDTO usersRequestDTO);

        @Operation(description = "Retorna uma lista paginada de usuários com filtro opcional por nome", summary = "Listagem de usuários", parameters = {
                        @Parameter(in = ParameterIn.QUERY, name = "name", description = "Filtrar usuários pelo nome", schema = @Schema(type = "string"), example = "João"),
                        @Parameter(in = ParameterIn.QUERY, name = "page", description = "Número da página, iniciando em 0", schema = @Schema(type = "integer", defaultValue = "0", minimum = "0")),
                        @Parameter(in = ParameterIn.QUERY, name = "size", description = "Quantidade de itens por página", schema = @Schema(type = "integer", defaultValue = "20", minimum = "1", maximum = "100")),
                        @Parameter(in = ParameterIn.QUERY, name = "sort", description = "Ordenação no formato `campo,direcao`", schema = @Schema(type = "string", defaultValue = "createdAt,asc"), example = "createdAt,asc")
        })
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UsersResponseDTO.class))))
        })
        @GetMapping
        ResponseEntity<PageResponseDTO<UsersResponseDTO>> findUsers(
                        @ParameterObject UsersFilterDTO filter,
                        @Parameter(hidden = true) @PageableDefault(sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable);

        @Operation(description = "Retorna o usuário logado", summary = "Busca do usuário logado", parameters = {
        })
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsersResponseDTO.class))),
                        @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResourceErrorResponseDTO.class))),
                        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResourceErrorResponseDTO.class)))
        })
        @GetMapping("/me")
        ResponseEntity<UsersResponseDTO> me(
                        @AuthenticationPrincipal Jwt jwt);

        @Operation(description = "Busca um usuário pelo seu UUID", summary = "Busca de usuário por ID", parameters = {
                        @Parameter(in = ParameterIn.PATH, name = "userId", description = "UUID do usuário", schema = @Schema(type = "string", format = "uuid"), example = "e5cb7e35-0d07-4183-97d7-1e851c3ec236")
        })
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsersResponseDTO.class))),
                        @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResourceErrorResponseDTO.class)))
        })
        @GetMapping("/{userId}")
        ResponseEntity<UsersResponseDTO> findById(
                        @PathVariable("userId") UUID userId);

        @Operation(description = "Realiza o soft delete do usuário", summary = "Exclusão de usuário", parameters = {
                        @Parameter(in = ParameterIn.PATH, name = "userId", description = "UUID do usuário", schema = @Schema(type = "string", format = "uuid"), example = "e5cb7e35-0d07-4183-97d7-1e851c3ec236")
        })
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso"),
                        @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResourceErrorResponseDTO.class)))
        })
        @DeleteMapping("/{userId}")
        ResponseEntity<Void> delete(
                        @PathVariable("userId") UUID userId);

        @Operation(description = "Realiza a atualização dos dados do usuário", summary = "Atualização de usuário", parameters = {
                        @Parameter(in = ParameterIn.PATH, name = "userId", description = "UUID do usuário", schema = @Schema(type = "string", format = "uuid"), example = "e5cb7e35-0d07-4183-97d7-1e851c3ec236")
        })
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsersResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorResponseDTO.class))),
                        @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResourceErrorResponseDTO.class))),
                        @ApiResponse(responseCode = "409", description = "Login ou e-mail já cadastrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResourceErrorResponseDTO.class)))
        })
        @PatchMapping("/{userId}")
        ResponseEntity<UsersResponseDTO> update(
                        @PathVariable("userId") UUID userId,
                        @Valid @org.springframework.web.bind.annotation.RequestBody UsersUpdateRequestDTO updateRequestDTO);

        @Operation(description = "Atualiza a senha do usuário", summary = "Atualização de senha", parameters = {
                        @Parameter(in = ParameterIn.PATH, name = "userId", description = "UUID do usuário", schema = @Schema(type = "string", format = "uuid"), example = "e5cb7e35-0d07-4183-97d7-1e851c3ec236")
        })
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Senha atualizada com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Senha antiga incorreta ou nova senha igual à anterior", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResourceErrorResponseDTO.class))),
                        @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResourceErrorResponseDTO.class)))
        })
        @PatchMapping("/{userId}/password")
        ResponseEntity<Void> updatePassword(
                        @PathVariable("userId") UUID userId,
                        @Valid @org.springframework.web.bind.annotation.RequestBody UsersUpdatePasswordRequestDTO usersUpdatePasswordRequestDTO);
}
