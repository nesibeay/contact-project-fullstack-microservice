package com.directory.contact.repository;

import com.directory.contact.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;
import java.util.Optional;

// JpaRepository gives us: save(), findById(), findAll(), delete() for free!
public interface ContactRepository extends JpaRepository<Contact, UUID> {

    // Spring magic: Just by writing this name, Spring knows how to write
    // the SQL "SELECT * FROM contacts WHERE email = ..."
    Optional<Contact> findByEmail(String email);

    // Change List to Page and add Pageable at the end
    @Query("SELECT c FROM Contact c WHERE " +
            "lower(c.firstName) LIKE lower(concat('%', :query, '%')) OR " +
            "lower(c.lastName) LIKE lower(concat('%', :query, '%')) OR " +
            "lower(c.email) LIKE lower(concat('%', :query, '%')) OR " +
            "lower(c.department) LIKE lower(concat('%', :query, '%')) OR " +
            "lower(c.jobTitle) LIKE lower(concat('%', :query, '%'))")
    Page<Contact> searchContacts(@Param("query") String query, Pageable pageable);
}