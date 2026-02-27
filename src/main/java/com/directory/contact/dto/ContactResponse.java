package com.directory.contact.dto;

import java.util.UUID;

public record ContactResponse(
                UUID id,
                String firstName,
                String lastName,
                String email,
                String phoneNumber,
                String internalPhone,
                String department,
                String jobTitle) {
}
