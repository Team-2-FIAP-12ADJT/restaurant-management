package com.fiap.restaurant_management.controllers.interfaces;

import com.fiap.restaurant_management.controllers.ApiPaths;
import com.fiap.restaurant_management.dtos.AddressRequestDTO;
import com.fiap.restaurant_management.dtos.AddressResponseDTO;
import com.fiap.restaurant_management.dtos.AddressUpdateRequestDTO;
import com.fiap.restaurant_management.dtos.ResourceErrorResponseDTO;
import com.fiap.restaurant_management.dtos.ValidationErrorResponseDTO;
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

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.UUID;

@RequestMapping(ApiPaths.V1_ADDRESSES)
@Validated
@Tag(name = "Address", description = "Gerenciamento de endereços")
public interface AddressControllerContract {

        @Operation(summary = "Criar um novo endereço", description = "Cria um novo endereço para um usuário")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Endereço criado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AddressResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorResponseDTO.class))),
                        @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResourceErrorResponseDTO.class))),
                        @ApiResponse(responseCode = "403", description = "Proibido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResourceErrorResponseDTO.class))),
                        @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResourceErrorResponseDTO.class))),
                        @ApiResponse(responseCode = "409", description = "Endereço já cadastrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResourceErrorResponseDTO.class)))
        })
        @PostMapping("/{userId}")
        ResponseEntity<AddressResponseDTO> create(
                        @Parameter(in = ParameterIn.PATH, name = "userId", description = "UUID do usuário", schema = @Schema(type = "string", format = "uuid"), example = "e5cb7e35-0d07-4183-97d7-1e851c3ec236") @PathVariable("userId") UUID userId,
                        @Valid @RequestBody AddressRequestDTO addressRequestDTO);

        @Operation(summary = "Listar endereços de um usuário", description = "Retorna todos os endereços ativos de um usuário específico")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista de endereços", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AddressResponseDTO.class)))),
                        @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResourceErrorResponseDTO.class))),
                        @ApiResponse(responseCode = "403", description = "Proibido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResourceErrorResponseDTO.class))),
                        @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResourceErrorResponseDTO.class)))
        })
        @GetMapping("/{userId}")
        ResponseEntity<List<AddressResponseDTO>> findByUserId(
                        @Parameter(in = ParameterIn.PATH, name = "userId", description = "UUID do usuário", schema = @Schema(type = "string", format = "uuid"), example = "e5cb7e35-0d07-4183-97d7-1e851c3ec236") @PathVariable("userId") UUID userId);

        @Operation(summary = "Listar endereços do usuário autenticado")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista de endereços", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AddressResponseDTO.class)))),
                        @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResourceErrorResponseDTO.class)))
        })
        @GetMapping("/me")
        ResponseEntity<List<AddressResponseDTO>> me(@AuthenticationPrincipal Jwt jwt);

        @Operation(summary = "Atualizar um endereço", description = "Atualiza parcialmente um endereço específico de um usuário")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Endereço atualizado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AddressResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorResponseDTO.class))),
                        @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResourceErrorResponseDTO.class))),
                        @ApiResponse(responseCode = "403", description = "Proibido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResourceErrorResponseDTO.class))),
                        @ApiResponse(responseCode = "404", description = "Endereço não encontrado para o usuário informado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResourceErrorResponseDTO.class))),
                        @ApiResponse(responseCode = "409", description = "Endereço já cadastrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResourceErrorResponseDTO.class)))
        })
        @PatchMapping("/{userId}/{addressId}")
        ResponseEntity<AddressResponseDTO> update(
                        @Parameter(in = ParameterIn.PATH, name = "userId", description = "UUID do usuário", schema = @Schema(type = "string", format = "uuid"), example = "e5cb7e35-0d07-4183-97d7-1e851c3ec236") @PathVariable("userId") UUID userId,
                        @Parameter(in = ParameterIn.PATH, name = "addressId", description = "UUID do endereço", schema = @Schema(type = "string", format = "uuid"), example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890") @PathVariable("addressId") UUID addressId,
                        @Valid @RequestBody AddressUpdateRequestDTO dto);

        @Operation(summary = "Deletar um endereço", description = "Deleta um endereço específico por ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Endereço deletado com sucesso"),
                        @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResourceErrorResponseDTO.class))),
                        @ApiResponse(responseCode = "403", description = "Proibido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResourceErrorResponseDTO.class))),
                        @ApiResponse(responseCode = "404", description = "Endereço não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResourceErrorResponseDTO.class)))
        })
        @GetMapping("/{addressId}")
        ResponseEntity<Void> delete(
                        @Parameter(in = ParameterIn.PATH, name = "addressId", description = "UUID do endereço", schema = @Schema(type = "string", format = "uuid"), example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890") @PathVariable("addressId") UUID addressId);

}
