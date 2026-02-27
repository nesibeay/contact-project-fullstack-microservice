package com.directory.contact.controller;

import com.directory.contact.dto.ApiResponse;
import com.directory.contact.dto.ContactRequest;
import com.directory.contact.dto.ContactResponse;
import com.directory.contact.mapper.ContactMapper;
import com.directory.contact.service.ContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController // Tells Spring: "This class handles JSON web requests"
@RequestMapping("/api/v1/contacts") // The base URL for everything in this class
@RequiredArgsConstructor
public class ContactController {

        private final ContactService service;
        private final ContactMapper mapper;

        @PostMapping // Handles POST requests (Creating data)
        @ResponseStatus(HttpStatus.CREATED) // Returns 201 Created instead of 200 OK
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<ApiResponse<ContactResponse>> create(@Valid @RequestBody ContactRequest request) {
                ContactResponse payload = mapper.toDto(service.createContact(request));
                ApiResponse<ContactResponse> response = new ApiResponse<>(
                                true,
                                "Contact created",
                                payload,
                                LocalDateTime.now());
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @GetMapping
        public ResponseEntity<ApiResponse<Page<ContactResponse>>> getAll(
                        @RequestParam(name = "page", defaultValue = "0") int page,
                        @RequestParam(name = "size", defaultValue = "10") int size,
                        @RequestParam(name = "sortBy", defaultValue = "firstName") String sortBy) {
                // Use logic from Service to Secure the data
                Page<ContactResponse> payload = service.getAllContacts(page, size, sortBy).map(service::toSecureDto);
                ApiResponse<Page<ContactResponse>> response = new ApiResponse<>(
                                true,
                                "Contacts fetched",
                                payload,
                                LocalDateTime.now());
                return ResponseEntity.ok(response);
        }

        @GetMapping("/search")
        public ResponseEntity<ApiResponse<Page<ContactResponse>>> search(
                        @RequestParam(name = "query") String query,
                        @RequestParam(name = "page", defaultValue = "0") int page,
                        @RequestParam(name = "size", defaultValue = "10") int size) {
                Page<ContactResponse> payload = service.searchContacts(query, page, size).map(service::toSecureDto);
                ApiResponse<Page<ContactResponse>> response = new ApiResponse<>(
                                true,
                                "Contacts search results",
                                payload,
                                LocalDateTime.now());
                return ResponseEntity.ok(response);
        }

        @DeleteMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<ApiResponse<Void>> delete(@PathVariable("id") UUID id) {
                service.deleteContact(id);
                ApiResponse<Void> response = new ApiResponse<>(
                                true,
                                "Contact deleted",
                                null,
                                LocalDateTime.now());
                return ResponseEntity.ok(response);
        }

}
