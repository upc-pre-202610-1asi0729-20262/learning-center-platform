package com.acme.center.platform.learning.domain.model.aggregates;

import com.acme.center.platform.learning.domain.model.commands.CreateCourseCommand;
import com.acme.center.platform.learning.domain.model.valueobjects.LearningPath;
import com.acme.center.platform.learning.domain.model.valueobjects.TutorialId;
import com.acme.center.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.util.Strings;

/**
 * Course aggregate root.
 */
@Getter
public class Course extends AbstractDomainAggregateRoot<Course> {
    @Setter
    private Long id;
    @Setter
    private String title;
    @Setter
    private String description;
    private LearningPath learningPath;

    public Course() {
        this.title = Strings.EMPTY;
        this.description = Strings.EMPTY;
        this.learningPath = new LearningPath();
    }

    public Course updateInformation(String title, String description) {
        this.title = title;
        this.description = description;
        return this;
    }

    public Course(CreateCourseCommand command) {
        this.title = command.title();
        this.description = command.description();
        this.learningPath = new LearningPath();
    }

    public void addTutorialToLearningPath(TutorialId tutorialId) {
        this.learningPath.addItem(this, tutorialId);
    }

    public void addTutorialToLearningPath(TutorialId tutorialId, TutorialId nextTutorialId) {
        this.learningPath.addItem(this, tutorialId, nextTutorialId);
    }
}
