package com.ntt.userapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhoneRequestDTO {

    @NotBlank(message = "El número es requerido")
    private String numero;

    @NotBlank(message = "El código de ciudad es requerido")
    private String codigoCiudad;

    @NotBlank(message = "El código de país es requerido")
    private String codigoPais;
}