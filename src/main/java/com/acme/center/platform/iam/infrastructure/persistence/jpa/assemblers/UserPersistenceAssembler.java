package com.acme.center.platform.iam.infrastructure.persistence.jpa.assemblers;

import com.acme.center.platform.iam.domain.model.aggregates.User;
import com.acme.center.platform.iam.infrastructure.persistence.jpa.entities.UserPersistenceEntity;

import java.util.HashSet;

/**
 * Static assembler between IAM user domain and persistence representations.
 */
public final class UserPersistenceAssembler {

    private UserPersistenceAssembler() {
    }

    public static User toDomainFromPersistence(UserPersistenceEntity entity) {
        if (entity == null) return null;
        var domain = new User();
        domain.setId(entity.getId());
        domain.setUsername(entity.getUsername());
        domain.setPassword(entity.getPassword());
        domain.setRoles(entity.getRoles().stream()
                .map(RolePersistenceAssembler::toDomainFromPersistence)
                .collect(java.util.stream.Collectors.toSet()));
        return domain;
    }

    public static UserPersistenceEntity toPersistenceFromDomain(User user) {
        if (user == null) return null;
        var entity = new UserPersistenceEntity();
        entity.setId(user.getId());
        entity.setUsername(user.getUsername());
        entity.setPassword(user.getPassword());
        entity.setRoles(user.getRoles() == null
                ? new HashSet<>()
                : user.getRoles().stream()
                .map(RolePersistenceAssembler::toPersistenceFromDomain)
                .collect(java.util.stream.Collectors.toSet()));
        return entity;
    }
}

