package com.acme.center.platform.iam.infrastructure.persistence.jpa.entities;

import com.acme.center.platform.iam.domain.model.valueobjects.Roles;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA persistence entity for IAM roles.
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
public class RolePersistenceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, unique = true, length = 20)
    private Roles name;
}
