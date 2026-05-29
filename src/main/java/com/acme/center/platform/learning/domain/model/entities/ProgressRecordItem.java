package com.acme.center.platform.learning.domain.model.entities;

import com.acme.center.platform.learning.domain.model.aggregates.Enrollment;
import com.acme.center.platform.learning.domain.model.valueobjects.ProgressStatus;
import com.acme.center.platform.learning.domain.model.valueobjects.TutorialId;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

/**
 * Represents a record of a student's progress on a tutorial.
 */
@Getter
@Setter
public class ProgressRecordItem {
    private Long id;
    private Enrollment enrollment;
    private TutorialId tutorialId;
    private ProgressStatus status;
    private Date statedAt;
    private Date completedAt;

    public ProgressRecordItem(Enrollment enrollment, TutorialId tutorialId) {
        this.enrollment = enrollment;
        this.tutorialId = tutorialId;
        this.status = ProgressStatus.NOT_STARTED;
    }

    public ProgressRecordItem() {
        // Required for reconstruction
    }

    public void start() {
        this.status = ProgressStatus.STARTED;
        this.statedAt = new Date();
    }

    public void complete() {
        this.status = ProgressStatus.COMPLETED;
        this.completedAt = new Date();
    }

    public boolean isCompleted() {
        return ProgressStatus.COMPLETED.equals(status);
    }

    public boolean isInProgress() {
        return ProgressStatus.STARTED.equals(status);
    }

    public boolean isNotStarted() {
        return ProgressStatus.NOT_STARTED.equals(status);
    }

    public long calculateDaysElapsed() {
        if (ProgressStatus.NOT_STARTED.equals(status)) return 0;
        var defaultTimeZone = ZoneId.systemDefault();
        var fromDate = this.statedAt.toInstant().atZone(defaultTimeZone);
        var toDate = Objects.isNull(this.completedAt)
                ? LocalDate.now().atStartOfDay(defaultTimeZone).toInstant()
                : this.completedAt.toInstant();
        return Duration.between(fromDate, toDate).toDays();
    }
}
