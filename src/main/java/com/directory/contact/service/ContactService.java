package com.directory.contact.service;

import com.directory.contact.dto.ContactRequest;
import com.directory.contact.entity.Contact;
import com.directory.contact.exception.ContactAlreadyExistsException;
import com.directory.contact.exception.ContactNotFoundException;
import com.directory.contact.mapper.ContactMapper;
import com.directory.contact.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactService {

    private final ContactRepository repository;
    private final ContactMapper mapper;

    @Value("${spring.contact.email-domain}")
    private String emailDomain;

    public Contact createContact(ContactRequest request) {
        // 1. Use the full email exactly as provided
        final String finalEmail = request.email();

        // 2. Check duplicate
        repository.findByEmail(finalEmail).ifPresent(c -> {
            throw new ContactAlreadyExistsException("Email " + finalEmail + " is already in the directory!");
        });

        // 3. Logic: Clean the phone number (remove all spaces before saving)
        String cleanPhone = request.phoneNumber().replaceAll("\\s+", "");

        // 4. Logic: Capitalize Names
        String capitalizedFirstName = capitalize(request.firstName());
        String capitalizedLastName = capitalize(request.lastName());

        // 5. Build and Save
        Contact contact = mapper.toEntity(request);
        contact.setFirstName(capitalizedFirstName);
        contact.setLastName(capitalizedLastName);
        contact.setEmail(finalEmail);
        contact.setPhoneNumber(cleanPhone);
        log.info("Saving new contact: {} {}", contact.getFirstName(), contact.getLastName());
        return repository.save(contact);
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    public Page<Contact> getAllContacts(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sortBy));
        return repository.findAll(pageable);
    }

    public Page<Contact> searchContacts(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "firstName"));
        return repository.searchContacts(query, pageable);
    }

    private boolean isAdmin() {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth == null)
            return false;
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("admin"));
    }

    public com.directory.contact.dto.ContactResponse toSecureDto(Contact contact) {
        com.directory.contact.dto.ContactResponse dto = mapper.toDto(contact);
        if (!isAdmin()) {
            return new com.directory.contact.dto.ContactResponse(
                    dto.id(), dto.firstName(), dto.lastName(), dto.email(),
                    null, // HIDE PHONE NUMBER
                    dto.internalPhone(), dto.department(), dto.jobTitle());
        }
        return dto;
    }

    public void deleteContact(UUID id) {
        if (!repository.existsById(id)) {
            throw new ContactNotFoundException("Contact not found!");
        }
        // This triggers the @SQLDelete (Soft Delete) in the Entity
        repository.deleteById(id);
        log.info("Contact with ID {} soft-deleted", id);
    }
}
