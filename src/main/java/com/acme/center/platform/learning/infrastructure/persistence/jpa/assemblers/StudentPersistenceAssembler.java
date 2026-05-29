package com.acme.center.platform.learning.infrastructure.persistence.jpa.assemblers;

import com.acme.center.platform.learning.domain.model.aggregates.Student;
import com.acme.center.platform.learning.infrastructure.persistence.jpa.entities.StudentPersistenceEntity;

/**
 * Static assembler between student domain and persistence representations.
 */
public final class StudentPersistenceAssembler {

    private StudentPersistenceAssembler() {
    }

    public static Student toDomainFromPersistence(StudentPersistenceEntity entity) {
        if (entity == null) return null;
        var student = new Student();
        student.setId(entity.getId());
        student.setAcmeStudentRecordId(entity.getAcmeStudentRecordId());
        student.setProfileId(entity.getProfileId());
        student.setPerformanceMetricSet(entity.getPerformanceMetricSet());
        return student;
    }

    public static StudentPersistenceEntity toPersistenceFromDomain(Student student) {
        if (student == null) return null;
        var entity = new StudentPersistenceEntity();
        entity.setId(student.getId());
        entity.setAcmeStudentRecordId(student.getAcmeStudentRecordId());
        entity.setProfileId(student.getProfileId());
        entity.setPerformanceMetricSet(student.getPerformanceMetricSet());
        return entity;
    }
}

