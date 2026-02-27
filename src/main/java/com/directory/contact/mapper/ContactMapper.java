package com.directory.contact.mapper;

import com.directory.contact.dto.ContactRequest;
import com.directory.contact.dto.ContactResponse;
import com.directory.contact.entity.Contact;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ContactMapper {
    Contact toEntity(ContactRequest dto);

    ContactResponse toDto(Contact entity);
}
