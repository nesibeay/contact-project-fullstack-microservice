package com.directory.contact.service;

import com.directory.contact.dto.ContactRequest;
import com.directory.contact.entity.Contact;
import com.directory.contact.exception.ContactAlreadyExistsException;
import com.directory.contact.exception.ContactNotFoundException;
import com.directory.contact.mapper.ContactMapper;
import com.directory.contact.repository.ContactRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

    @Mock
    private ContactRepository repository;

    @Mock
    private ContactMapper mapper;

    @InjectMocks
    private ContactService service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "emailDomain", "turksat.com.tr");
    }

    @Test
    void createContact_ShouldSaveFullEmail_AsProvided() {
        // Arrange
        ContactRequest request = new ContactRequest("John", "Doe", "john.doe@gmail.com", "5551234567", "1234", "IT",
                "Dev");
        Contact contact = new Contact();
        contact.setFirstName("John");

        when(repository.findByEmail("john.doe@gmail.com")).thenReturn(Optional.empty());
        when(mapper.toEntity(request)).thenReturn(contact);
        when(repository.save(any(Contact.class))).thenReturn(contact);

        // Act
        service.createContact(request);

        // Assert
        verify(repository).findByEmail("john.doe@gmail.com");
        verify(repository).save(any(Contact.class));
    }

    @Test
    void createContact_ShouldThrowException_WhenEmailExists() {
        // Arrange
        ContactRequest request = new ContactRequest("John", "Doe", "john.doe", "1234567890", "1234", "IT", "Dev");
        when(repository.findByEmail(anyString())).thenReturn(Optional.of(new Contact()));

        // Act & Assert
        assertThrows(ContactAlreadyExistsException.class, () -> service.createContact(request));
        verify(repository, never()).save(any(Contact.class));
    }

    @Test
    void deleteContact_ShouldThrowException_WhenIdNotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(false);

        // Act & Assert
        assertThrows(ContactNotFoundException.class, () -> service.deleteContact(id));
        verify(repository, never()).deleteById(any());
    }
}
