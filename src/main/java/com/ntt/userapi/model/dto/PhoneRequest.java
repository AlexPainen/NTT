package com.ntt.userapi.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PhoneRequest {

    @NotBlank(message = "Phone number cannot be blank")
    private String number;

    @NotBlank(message = "City code cannot be blank")
    private String cityCode;

    @NotBlank(message = "Country code cannot be blank")
    private String countryCode;
}