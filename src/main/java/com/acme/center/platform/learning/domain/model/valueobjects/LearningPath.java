package com.acme.center.platform.learning.domain.model.valueobjects;

import com.acme.center.platform.learning.domain.model.aggregates.Course;
import com.acme.center.platform.learning.domain.model.entities.LearningPathItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * LearningPath value object.
 */
public class LearningPath {
    private List<LearningPathItem> learningPathItems;

    public LearningPath() {
        this.learningPathItems = new ArrayList<>();
    }

    public List<LearningPathItem> getLearningPathItems() {
        return learningPathItems;
    }

    public void setLearningPathItems(List<LearningPathItem> learningPathItems) {
        this.learningPathItems = learningPathItems == null ? new ArrayList<>() : learningPathItems;
    }

    private LearningPathItem getLearningPathItemWithId(Long itemId) {
        return this.getFirstLearningPathItemWhere(item -> item.getId().equals(itemId));
    }

    public LearningPathItem getLearningPathItemWithTutorialId(TutorialId tutorialId) {
        return this.getFirstLearningPathItemWhere(item -> item.getTutorialId().equals(tutorialId));
    }

    public TutorialId getNextTutorialInLearningPath(TutorialId currentTutorialId) {
        LearningPathItem nextItem = getLearningPathItemWithTutorialId(currentTutorialId).getNextItem();
        return !Objects.isNull(nextItem) ? nextItem.getTutorialId() : null;
    }

    public boolean isLastTutorialInLearningPath(TutorialId currentTutorialId) {
        return Objects.isNull(getNextTutorialInLearningPath(currentTutorialId));
    }

    public TutorialId getFirstTutorialInLearningPath() {
        return learningPathItems.getFirst().getTutorialId();
    }

    public LearningPathItem getLastItemInLearningPath() {
        return this.getFirstLearningPathItemWhere(item -> Objects.isNull(item.getNextItem()));
    }

    private LearningPathItem getFirstLearningPathItemWhere(Predicate<LearningPathItem> predicate) {
        return learningPathItems.stream()
                .filter(predicate)
                .findFirst()
                .orElse(null);
    }

    public boolean isEmpty() {
        return learningPathItems.isEmpty();
    }

    public void addItem(Course course, TutorialId tutorialId, LearningPathItem nextItem) {
        LearningPathItem learningPathItem = new LearningPathItem(course, tutorialId, nextItem);
        learningPathItems.add(learningPathItem);
    }

    public void addItem(Course course, TutorialId tutorialId) {
        LearningPathItem learningPathItem = new LearningPathItem(course, tutorialId, null);
        LearningPathItem originalLastItem = null;
        if (!isEmpty()) originalLastItem = getLastItemInLearningPath();
        learningPathItems.add(learningPathItem);
        if (!Objects.isNull(originalLastItem)) originalLastItem.updateNextItem(learningPathItem);
    }

    public void addItem(Course course, TutorialId tutorialId, TutorialId nextTutorialId) {
        LearningPathItem nextItem = getLearningPathItemWithTutorialId(nextTutorialId);
        addItem(course, tutorialId, nextItem);
    }
}
