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
 *
 * <p>
 * Represents a course within the learning platform.
 * A course is defined by a title, a description, and a learning path consisting of tutorials.
 * </p>
 */
@Getter
public class Course extends AbstractDomainAggregateRoot<Course> {
    /**
     * The unique identifier for the course.
     */
    @Setter
    private Long id;

    /**
     * The title of the course.
     */
    @Setter
    private String title;

    /**
     * The description of the course.
     */
    @Setter
    private String description;

    /**
     * The learning path of the course.
     */
    private LearningPath learningPath;

    /**
     * Default constructor for Course.
     * Initializes an empty title, description, and a new learning path.
     */
    public Course() {
        this.title = Strings.EMPTY;
        this.description = Strings.EMPTY;
        this.learningPath = new LearningPath();
    }

    /**
     * Updates the course information.
     * @param title The new title.
     * @param description The new description.
     * @return The updated Course instance.
     */
    public Course updateInformation(String title, String description) {
        this.title = title;
        this.description = description;
        return this;
    }

    /**
     * Constructor for Course with a CreateCourseCommand.
     * @param command The CreateCourseCommand.
     */
    public Course(CreateCourseCommand command) {
        this.title = command.title();
        this.description = command.description();
        this.learningPath = new LearningPath();
    }

    /**
     * Adds a tutorial to the learning path.
     * @param tutorialId The ID of the tutorial to add.
     */
    public void addTutorialToLearningPath(TutorialId tutorialId) {
        this.learningPath.addItem(this, tutorialId);
    }

    /**
     * Adds a tutorial to the learning path after a specific tutorial.
     * @param tutorialId The ID of the tutorial to add.
     * @param nextTutorialId The ID of the tutorial that should follow the added tutorial.
     */
    public void addTutorialToLearningPath(TutorialId tutorialId, TutorialId nextTutorialId) {
        this.learningPath.addItem(this, tutorialId, nextTutorialId);
    }
}
