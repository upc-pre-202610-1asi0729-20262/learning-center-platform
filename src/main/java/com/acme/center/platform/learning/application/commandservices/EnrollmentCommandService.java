package com.acme.center.platform.learning.application.commandservices;

import com.acme.center.platform.learning.domain.model.commands.*;
import com.acme.center.platform.shared.application.result.ApplicationError;
import com.acme.center.platform.shared.application.result.Result;

/**
 * EnrollmentCommandService
 * Service that handles enrollment commands
 */
public interface EnrollmentCommandService {
    /**
     * Handle a request enrollment command
     * @param command The request enrollment command containing the enrollment data
     * @return Result containing requested enrollment id or an application error
     * @see RequestEnrollmentCommand
     */
    Result<Long, ApplicationError> handle(RequestEnrollmentCommand command);
    /**
     * Handle a confirm enrollment command
     * @param command The confirm enrollment command containing the enrollment id
     * @return Result containing confirmed enrollment id or an application error
     * @see ConfirmEnrollmentCommand
     */
    Result<Long, ApplicationError> handle(ConfirmEnrollmentCommand command);
    /**
     * Handle a reject enrollment command
     * @param command The reject enrollment command containing the enrollment id
     * @return Result containing rejected enrollment id or an application error
     * @see RejectEnrollmentCommand
     */
    Result<Long, ApplicationError> handle(RejectEnrollmentCommand command);
    /**
     * Handle a cancel enrollment command
     * @param command The cancel enrollment command containing the enrollment id
     * @return Result containing canceled enrollment id or an application error
     * @see CancelEnrollmentCommand
     */
    Result<Long, ApplicationError> handle(CancelEnrollmentCommand command);
    /**
     * Handle a complete tutorial for enrollment command
     * @param command The complete tutorial for enrollment command containing the enrollment id and tutorial id
     * @return Result containing enrollment id where the tutorial was completed or an application error
     * @see CompleteTutorialForEnrollmentCommand
     */
    Result<Long, ApplicationError> handle(CompleteTutorialForEnrollmentCommand command);
}

