package com.opentable.coding.challenge.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Document
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UrlInfo {

    public static final int SHORT_URL_LENGTH = 7;

    public static final String SHORT_URL_FORMAT = "^[a-zA-Z\\d]+$";

    @Id
    @NotBlank(message = "Short URL can not be empty}")
    @Size(message = "Short URL must be 7 characters", min = SHORT_URL_LENGTH, max = SHORT_URL_LENGTH)
    @Pattern(regexp = SHORT_URL_FORMAT)
    private String shortUrl;

    @NotBlank(message = "Long URL can not be empty")
    @Size(message = "Long URL must be between 10 and 255 characters", min = 10, max = 255)
    private String longUrl;
}
