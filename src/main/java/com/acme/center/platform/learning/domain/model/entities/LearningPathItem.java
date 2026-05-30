package com.acme.center.platform.learning.domain.model.entities;

import com.acme.center.platform.learning.domain.model.aggregates.Course;
import com.acme.center.platform.learning.domain.model.valueobjects.TutorialId;
import lombok.Getter;
import lombok.Setter;

/**
 * LearningPathItem domain entity.
 *
 * <p>
 * Represents a single item in a course's learning path.
 * Each item links a course to a tutorial and optionally points to the next item in the sequence.
 * </p>
 */
@Getter
public class LearningPathItem {
    /**
     * The unique identifier for the learning path item.
     */
    @Setter
    private Long id;

    /**
     * The course this learning path item belongs to.
     */
    @Setter
    private Course course;

    /**
     * The ID of the tutorial associated with this item.
     */
    @Setter
    private TutorialId tutorialId;

    /**
     * The next item in the learning path sequence.
     */
    @Setter
    private LearningPathItem nextItem;

    /**
     * Constructor for LearningPathItem.
     * @param course The course this item belongs to.
     * @param tutorialId The tutorial ID.
     * @param nextItem The next item in the sequence.
     */
    public LearningPathItem(Course course, TutorialId tutorialId, LearningPathItem nextItem) {
        this.course = course;
        this.tutorialId = tutorialId;
        this.nextItem = nextItem;
    }

    /**
     * Default constructor for LearningPathItem.
     * Initializes with a default TutorialId and no next item.
     */
    public LearningPathItem() {
        this.tutorialId = new TutorialId(0L);
        this.nextItem = null;
    }

    /**
     * Updates the next item in the learning path sequence.
     * @param nextItem The next LearningPathItem.
     */
    public void updateNextItem(LearningPathItem nextItem) {
        this.nextItem = nextItem;
    }
}
