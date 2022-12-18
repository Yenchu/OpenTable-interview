package com.opentable.coding.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateShortUrl {
    @NotBlank(message = "URL can not be empty")
    @Size(message = "URL must be between 10 and 255 characters", min = 10, max = 255)
    private String url;
}
