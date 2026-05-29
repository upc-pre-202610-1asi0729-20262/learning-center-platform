package com.acme.center.platform.learning.infrastructure.persistence.jpa.entities;

import com.acme.center.platform.learning.domain.model.valueobjects.AcmeStudentRecordId;
import com.acme.center.platform.learning.domain.model.valueobjects.ProfileId;
import com.acme.center.platform.learning.domain.model.valueobjects.StudentPerformanceMetricSet;
import com.acme.center.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA persistence entity for students.
 */
@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
public class StudentPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Embedded
    @AttributeOverride(name = "studentRecordId", column = @Column(name = "acme_student_id", nullable = false, unique = true))
    private AcmeStudentRecordId acmeStudentRecordId;

    @Embedded
    @AttributeOverride(name = "profileId", column = @Column(name = "profile_id", nullable = false, unique = true))
    private ProfileId profileId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "totalCompletedCourses", column = @Column(name = "total_completed_courses", nullable = false)),
            @AttributeOverride(name = "totalCompletedTutorials", column = @Column(name = "total_completed_tutorials", nullable = false))
    })
    private StudentPerformanceMetricSet performanceMetricSet;
}

