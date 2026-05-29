package com.acme.center.platform.learning.domain.model.entities;

import com.acme.center.platform.learning.domain.model.aggregates.Course;
import com.acme.center.platform.learning.domain.model.valueobjects.TutorialId;
import lombok.Getter;
import lombok.Setter;

/**
 * LearningPathItem domain entity.
 */
@Getter
@Setter
public class LearningPathItem {
    private Long id;
    private Course course;
    private TutorialId tutorialId;
    private LearningPathItem nextItem;

    public LearningPathItem(Course course, TutorialId tutorialId, LearningPathItem nextItem) {
        this.course = course;
        this.tutorialId = tutorialId;
        this.nextItem = nextItem;
    }

    public LearningPathItem() {
        this.tutorialId = new TutorialId(0L);
        this.nextItem = null;
    }

    public void updateNextItem(LearningPathItem nextItem) {
        this.nextItem = nextItem;
    }
}
