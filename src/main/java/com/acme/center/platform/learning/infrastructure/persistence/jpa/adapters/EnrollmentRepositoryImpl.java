package com.acme.center.platform.learning.infrastructure.persistence.jpa.adapters;

import com.acme.center.platform.learning.domain.model.aggregates.Enrollment;
import com.acme.center.platform.learning.domain.model.valueobjects.AcmeStudentRecordId;
import com.acme.center.platform.learning.domain.repositories.EnrollmentRepository;
import com.acme.center.platform.learning.infrastructure.persistence.jpa.assemblers.EnrollmentPersistenceAssembler;
import com.acme.center.platform.learning.infrastructure.persistence.jpa.repositories.EnrollmentPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository adapter that bridges the enrollment domain repository port with Spring Data JPA.
 */
@Repository
public class EnrollmentRepositoryImpl implements EnrollmentRepository {

    private final EnrollmentPersistenceRepository enrollmentPersistenceRepository;

    public EnrollmentRepositoryImpl(EnrollmentPersistenceRepository enrollmentPersistenceRepository) {
        this.enrollmentPersistenceRepository = enrollmentPersistenceRepository;
    }

    @Override
    public Optional<Enrollment> findById(Long id) {
        return enrollmentPersistenceRepository.findById(id).map(EnrollmentPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Enrollment> findAll() {
        return enrollmentPersistenceRepository.findAll().stream()
                .map(EnrollmentPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public List<Enrollment> findAllByAcmeStudentRecordId(AcmeStudentRecordId studentRecordId) {
        return enrollmentPersistenceRepository.findAllByAcmeStudentRecordId(studentRecordId).stream()
                .map(EnrollmentPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public List<Enrollment> findAllByCourseId(Long courseId) {
        return enrollmentPersistenceRepository.findAllByCourseId(courseId).stream()
                .map(EnrollmentPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public Optional<Enrollment> findByAcmeStudentRecordIdAndCourseId(AcmeStudentRecordId studentRecordId, Long courseId) {
        return enrollmentPersistenceRepository.findByAcmeStudentRecordIdAndCourseId(studentRecordId, courseId)
                .map(EnrollmentPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public Enrollment save(Enrollment enrollment) {
        var saved = enrollmentPersistenceRepository.save(EnrollmentPersistenceAssembler.toPersistenceFromDomain(enrollment));
        return EnrollmentPersistenceAssembler.toDomainFromPersistence(saved);
    }
}

