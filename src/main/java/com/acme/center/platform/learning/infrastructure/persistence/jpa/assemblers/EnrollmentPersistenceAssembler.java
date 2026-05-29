package com.acme.center.platform.learning.infrastructure.persistence.jpa.assemblers;

import com.acme.center.platform.learning.domain.model.aggregates.Enrollment;
import com.acme.center.platform.learning.domain.model.entities.ProgressRecordItem;
import com.acme.center.platform.learning.domain.model.valueobjects.ProgressRecord;
import com.acme.center.platform.learning.infrastructure.persistence.jpa.entities.EnrollmentPersistenceEntity;
import com.acme.center.platform.learning.infrastructure.persistence.jpa.entities.ProgressRecordItemPersistenceEntity;

import java.util.ArrayList;

/**
 * Static assembler between enrollment domain and persistence representations.
 */
public final class EnrollmentPersistenceAssembler {

    private EnrollmentPersistenceAssembler() {
    }

    public static Enrollment toDomainFromPersistence(EnrollmentPersistenceEntity entity) {
        if (entity == null) return null;

        var enrollment = new Enrollment();
        enrollment.setId(entity.getId());
        enrollment.setAcmeStudentRecordId(entity.getAcmeStudentRecordId());
        enrollment.setCourse(CoursePersistenceAssembler.toDomainFromPersistence(entity.getCourse()));
        enrollment.setStatus(entity.getStatus());

        var progressRecord = new ProgressRecord();
        var progressItems = new ArrayList<ProgressRecordItem>();
        for (var item : entity.getProgressRecordItems()) {
            var progressRecordItem = new ProgressRecordItem();
            progressRecordItem.setId(item.getId());
            progressRecordItem.setEnrollment(enrollment);
            progressRecordItem.setTutorialId(item.getTutorialId());
            progressRecordItem.setStatus(item.getStatus());
            progressRecordItem.setStatedAt(item.getStatedAt());
            progressRecordItem.setCompletedAt(item.getCompletedAt());
            progressItems.add(progressRecordItem);
        }
        progressRecord.setProgressRecordItems(progressItems);
        enrollment.setProgressRecord(progressRecord);

        return enrollment;
    }

    public static EnrollmentPersistenceEntity toPersistenceFromDomain(Enrollment enrollment) {
        if (enrollment == null) return null;

        var entity = new EnrollmentPersistenceEntity();
        entity.setId(enrollment.getId());
        entity.setAcmeStudentRecordId(enrollment.getAcmeStudentRecordId());
        entity.setCourse(CoursePersistenceAssembler.toPersistenceFromDomain(enrollment.getCourse()));
        entity.setStatus(enrollment.getStatus());

        var progressItems = new ArrayList<ProgressRecordItemPersistenceEntity>();
        for (var item : enrollment.getProgressRecord().getProgressRecordItems()) {
            var persistenceItem = new ProgressRecordItemPersistenceEntity();
            persistenceItem.setId(item.getId());
            persistenceItem.setEnrollment(entity);
            persistenceItem.setTutorialId(item.getTutorialId());
            persistenceItem.setStatus(item.getStatus());
            persistenceItem.setStatedAt(item.getStatedAt());
            persistenceItem.setCompletedAt(item.getCompletedAt());
            progressItems.add(persistenceItem);
        }
        entity.setProgressRecordItems(progressItems);

        return entity;
    }
}

