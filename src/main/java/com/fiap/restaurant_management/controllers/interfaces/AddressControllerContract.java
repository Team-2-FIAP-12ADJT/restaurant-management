package com.fiap.restaurant_management.controllers.interfaces;

import com.fiap.restaurant_management.controllers.ApiPaths;
import com.fiap.restaurant_management.dtos.AddressRequestDTO;
import com.fiap.restaurant_management.dtos.AddressResponseDTO;
import com.fiap.restaurant_management.dtos.ResourceErrorResponseDTO;
import com.fiap.restaurant_management.dtos.ValidationErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
