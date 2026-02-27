package com.directory.contact.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ContactRequest(
        @NotBlank(message = "First name is mandatory") String firstName,

        @NotBlank(message = "Last name is mandatory") String lastName,

        // Full email required (e.g. john.doe@gmail.com) — ASCII characters only
        @NotBlank(message = "Email is mandatory") @Pattern(regexp = "^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$", message = "Invalid email. Use only English characters.") String email,

        @NotBlank(message = "Phone number is mandatory") @Pattern(regexp = "^(\\+90|90|0)?\\s?5\\d{2}\\s?\\d{3}\\s?\\d{4}$", message = "Phone must start with 5. Format: 555 076 0879") String phoneNumber,

        @Pattern(regexp = "^\\d{4}$", message = "Internal phone must be exactly 4 digits") String internalPhone,

        String department,
        String jobTitle) {
}