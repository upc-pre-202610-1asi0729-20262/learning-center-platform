package com.acme.center.platform.profiles.infrastructure.persistence.jpa.entities;

import com.acme.center.platform.profiles.domain.model.valueobjects.EmailAddress;
import com.acme.center.platform.profiles.infrastructure.persistence.jpa.converters.EmailAddressPersistenceConverter;
import com.acme.center.platform.profiles.infrastructure.persistence.jpa.embeddables.PersonNamePersistenceEmbeddable;
import com.acme.center.platform.profiles.infrastructure.persistence.jpa.embeddables.StreetAddressPersistenceEmbeddable;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * JPA persistence entity for profiles.
 */
@Entity
@Table(name = "profiles")
public class ProfilePersistenceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "firstName", column = @Column(name = "first_name")),
            @AttributeOverride(name = "lastName", column = @Column(name = "last_name"))})
    private PersonNamePersistenceEmbeddable name;

    @Convert(converter = EmailAddressPersistenceConverter.class)
    @Column(name = "email_address", nullable = false, unique = true)
    private EmailAddress emailAddress;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "street", column = @Column(name = "street_address_street")),
            @AttributeOverride(name = "number", column = @Column(name = "street_address_number")),
            @AttributeOverride(name = "city", column = @Column(name = "street_address_city")),
            @AttributeOverride(name = "postalCode", column = @Column(name = "street_address_postal_code")),
            @AttributeOverride(name = "country", column = @Column(name = "street_address_country"))})
    private StreetAddressPersistenceEmbeddable streetAddress;

    public ProfilePersistenceEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PersonNamePersistenceEmbeddable getName() {
        return name;
    }

    public void setName(PersonNamePersistenceEmbeddable name) {
        this.name = name;
    }

    public EmailAddress getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(EmailAddress emailAddress) {
        this.emailAddress = emailAddress;
    }

    public StreetAddressPersistenceEmbeddable getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(StreetAddressPersistenceEmbeddable streetAddress) {
        this.streetAddress = streetAddress;
    }
}


