package com.acme.center.platform.learning.infrastructure.persistence.jpa.adapters;

import com.acme.center.platform.learning.domain.model.aggregates.Student;
import com.acme.center.platform.learning.domain.model.valueobjects.AcmeStudentRecordId;
import com.acme.center.platform.learning.domain.model.valueobjects.ProfileId;
import com.acme.center.platform.learning.domain.repositories.StudentRepository;
import com.acme.center.platform.learning.infrastructure.persistence.jpa.assemblers.StudentPersistenceAssembler;
import com.acme.center.platform.learning.infrastructure.persistence.jpa.repositories.StudentPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository adapter that bridges the student domain repository port with Spring Data JPA.
 */
@Repository
public class StudentRepositoryImpl implements StudentRepository {

    private final StudentPersistenceRepository studentPersistenceRepository;

    public StudentRepositoryImpl(StudentPersistenceRepository studentPersistenceRepository) {
        this.studentPersistenceRepository = studentPersistenceRepository;
    }

    @Override
    public Optional<Student> findById(Long id) {
        return studentPersistenceRepository.findById(id).map(StudentPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public Optional<Student> findByAcmeStudentRecordId(AcmeStudentRecordId studentRecordId) {
        return studentPersistenceRepository.findByAcmeStudentRecordId(studentRecordId)
                .map(StudentPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public Optional<Student> findByProfileId(ProfileId profileId) {
        return studentPersistenceRepository.findByProfileId(profileId).map(StudentPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public Student save(Student student) {
        var saved = studentPersistenceRepository.save(StudentPersistenceAssembler.toPersistenceFromDomain(student));
        return StudentPersistenceAssembler.toDomainFromPersistence(saved);
    }

    @Override
    public boolean existsByAcmeStudentRecordId(AcmeStudentRecordId studentRecordId) {
        return studentPersistenceRepository.existsByAcmeStudentRecordId(studentRecordId);
    }
}

