package com.acme.center.platform.learning.domain.model.aggregates;

import com.acme.center.platform.learning.domain.model.valueobjects.AcmeStudentRecordId;
import com.acme.center.platform.learning.domain.model.valueobjects.ProfileId;
import com.acme.center.platform.learning.domain.model.valueobjects.StudentPerformanceMetricSet;
import com.acme.center.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

/**
 * Student aggregate root.
 */
@Getter
public class Student extends AbstractDomainAggregateRoot<Student> {
    @Setter
    private Long id;
    @Setter
    private AcmeStudentRecordId acmeStudentRecordId;
    @Setter
    private ProfileId profileId;
    @Setter
    private StudentPerformanceMetricSet performanceMetricSet;

    public Student() {
        this.acmeStudentRecordId = new AcmeStudentRecordId();
        this.performanceMetricSet = new StudentPerformanceMetricSet();
    }

    public Student(Long profileId) {
        this();
        this.profileId = new ProfileId(profileId);
    }

    public Student(ProfileId profileId) {
        this();
        this.profileId = profileId;
    }

    public void updateMetricsOnCourseCompleted() {
        this.performanceMetricSet = this.performanceMetricSet.incrementTotalCompletedCourses();
    }

    public void updateMetricsOnTutorialCompleted() {
        this.performanceMetricSet = this.performanceMetricSet.incrementTotalCompletedTutorials();
    }

    public String getStudentRecordId() {
        return this.acmeStudentRecordId.studentRecordId();
    }

    public Long getProfileIdValue() {
        return this.profileId.profileId();
    }

    public int getTotalCompletedCourses() {
        return this.performanceMetricSet.totalCompletedCourses();
    }

    public int getTotalCompletedTutorials() {
        return this.performanceMetricSet.totalCompletedTutorials();
    }
}
