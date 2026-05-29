package com.acme.center.platform.profiles.infrastructure.persistence.jpa.adapters;

import com.acme.center.platform.profiles.domain.model.aggregates.Profile;
import com.acme.center.platform.profiles.domain.model.valueobjects.EmailAddress;
import com.acme.center.platform.profiles.domain.repositories.ProfileRepository;
import com.acme.center.platform.profiles.infrastructure.persistence.jpa.assemblers.ProfilePersistenceAssembler;
import com.acme.center.platform.profiles.infrastructure.persistence.jpa.repositories.ProfilePersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository adapter that bridges the domain profile repository port with Spring Data JPA.
 */
@Repository
public class ProfileRepositoryImpl implements ProfileRepository {

    private final ProfilePersistenceRepository profilePersistenceRepository;

    public ProfileRepositoryImpl(ProfilePersistenceRepository profilePersistenceRepository) {
        this.profilePersistenceRepository = profilePersistenceRepository;
    }

    @Override
    public Optional<Profile> findById(Long id) {
        return profilePersistenceRepository.findById(id).map(ProfilePersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public Optional<Profile> findByEmailAddress(EmailAddress emailAddress) {
        return profilePersistenceRepository.findByEmailAddress(emailAddress).map(ProfilePersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Profile> findAll() {
        return profilePersistenceRepository.findAll().stream().map(ProfilePersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public Profile save(Profile profile) {
        var savedProfile = profilePersistenceRepository.save(ProfilePersistenceAssembler.toPersistenceFromDomain(profile));
        return ProfilePersistenceAssembler.toDomainFromPersistence(savedProfile);
    }

    @Override
    public boolean existsByEmailAddress(EmailAddress emailAddress) {
        return profilePersistenceRepository.countByEmailAddress(emailAddress) > 0;
    }
}


