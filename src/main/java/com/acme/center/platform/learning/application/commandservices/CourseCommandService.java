package com.acme.center.platform.learning.application.commandservices;

import com.acme.center.platform.learning.domain.model.aggregates.Course;
import com.acme.center.platform.learning.domain.model.commands.AddTutorialToCourseLearningPathCommand;
import com.acme.center.platform.learning.domain.model.commands.CreateCourseCommand;
import com.acme.center.platform.learning.domain.model.commands.DeleteCourseCommand;
import com.acme.center.platform.learning.domain.model.commands.UpdateCourseCommand;
import com.acme.center.platform.shared.application.result.ApplicationError;
import com.acme.center.platform.shared.application.result.Result;

/**
 * CourseCommandService
 * Service that handles course commands
 */
public interface CourseCommandService {
    /**
     * Handle a create course command
     * @param command The create course command containing the course data
     * @return Result containing the created course id or an application error
     * @see CreateCourseCommand
     */
    Result<Long, ApplicationError> handle(CreateCourseCommand command);
    /**
     * Handle an update course command
     * @param command The update course command containing the course data
     * @return Result containing the updated course or an application error
     * @see UpdateCourseCommand
     */
    Result<Course, ApplicationError> handle(UpdateCourseCommand command);
    /**
     * Handle a delete course command
     * @param command The delete course command containing the course id
     * @return Result containing the deleted course id or an application error
     * @see DeleteCourseCommand
     */
    Result<Long, ApplicationError> handle(DeleteCourseCommand command);
    /**
     * Handle an add tutorial to course learning path command
     * @param command The add tutorial to course learning path command containing the course id and tutorial id
     * @return Result containing the course id or an application error
     * @see AddTutorialToCourseLearningPathCommand
     */
    Result<Long, ApplicationError> handle(AddTutorialToCourseLearningPathCommand command);
}

