package com.acme.center.platform.profiles.domain.model.aggregates;

import com.acme.center.platform.profiles.domain.model.commands.CreateProfileCommand;
import com.acme.center.platform.profiles.domain.model.valueobjects.EmailAddress;
import com.acme.center.platform.profiles.domain.model.valueobjects.PersonName;
import com.acme.center.platform.profiles.domain.model.valueobjects.StreetAddress;
import java.util.Objects;

/**
 * Profile aggregate root.
 */
public class Profile {

    private Long id;
    private PersonName name;
    private EmailAddress emailAddress;
    private StreetAddress streetAddress;

    /**
     * Creates a profile from the provided domain values.
     */
    public Profile(Long id, PersonName name, EmailAddress emailAddress, StreetAddress streetAddress) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.emailAddress = Objects.requireNonNull(emailAddress, "emailAddress must not be null");
        this.streetAddress = Objects.requireNonNull(streetAddress, "streetAddress must not be null");
    }

    /**
     * Creates a profile from the provided domain values.
     */
    public Profile(PersonName name, EmailAddress emailAddress, StreetAddress streetAddress) {
        this(null, name, emailAddress, streetAddress);
    }

    /**
     * Constructor with first name, last name, email, street, number, city, postal code and country.
     */
    public Profile(String firstName, String lastName, String email, String street, String number, String city, String postalCode, String country) {
        this(
                new PersonName(firstName, lastName),
                new EmailAddress(email),
                new StreetAddress(street, number, city, postalCode, country));
    }

    /**
     * Constructor with a CreateProfileCommand.
     * @param command The {@link CreateProfileCommand} instance
     */
    public Profile(CreateProfileCommand command) {
        this(
                command.firstName(),
                command.lastName(),
                command.email(),
                command.street(),
                command.number(),
                command.city(),
                command.postalCode(),
                command.country());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PersonName getName() {
        return name;
    }

    public void setName(PersonName name) {
        this.name = Objects.requireNonNull(name, "name must not be null");
    }

    public EmailAddress getEmailAddressValue() {
        return emailAddress;
    }

    public void setEmailAddress(EmailAddress emailAddress) {
        this.emailAddress = Objects.requireNonNull(emailAddress, "emailAddress must not be null");
    }

    public StreetAddress getStreetAddressValue() {
        return streetAddress;
    }

    public void setStreetAddress(StreetAddress streetAddress) {
        this.streetAddress = Objects.requireNonNull(streetAddress, "streetAddress must not be null");
    }

    /**
     * Full name getter.
     * @return Full name
     */
    public String getFullName() {
        return name.getFullName();
    }

    /**
     * Email address getter.
     * @return Email address
     */
    public String getEmailAddress() {
        return emailAddress.address();
    }

    /**
     * Street address getter.
     * @return Street address
     */
    public String getStreetAddress() {
        return streetAddress.getStreetAddress();
    }

    /**
     * Update name.
     * @param firstName First name
     * @param lastName Last name
     */
    public void updateName(String firstName, String lastName) {
        this.name = new PersonName(firstName, lastName);
    }

    /**
     * Update email address.
     * @param email Email address
     */
    public void updateEmailAddress(String email) {
        this.emailAddress = new EmailAddress(email);
    }

}
