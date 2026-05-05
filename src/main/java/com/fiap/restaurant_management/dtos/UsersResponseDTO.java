package com.fiap.restaurant_management.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UsersResponseDTO(
                @Schema(example = "e5cb7e35-0d07-4183-97d7-1e851c3ec236") UUID id,
                @Schema(example = "João Silva") String name,
                @Schema(example = "joao@example.com") String email,
                @Schema(example = "joao123") String login,
                @Schema(example = "Client") String role,
                @JsonIgnoreProperties({"user"}) List<AddressResponseDTO> addresses) {

}
