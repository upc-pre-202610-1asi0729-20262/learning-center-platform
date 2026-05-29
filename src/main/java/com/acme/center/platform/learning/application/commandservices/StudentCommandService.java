package com.acme.center.platform.learning.application.commandservices;

import com.acme.center.platform.learning.domain.model.commands.CreateStudentByProfileIdCommand;
import com.acme.center.platform.learning.domain.model.commands.CreateStudentCommand;
import com.acme.center.platform.learning.domain.model.commands.UpdateStudentMetricsOnTutorialCompletedCommand;
import com.acme.center.platform.learning.domain.model.valueobjects.AcmeStudentRecordId;
import com.acme.center.platform.shared.application.result.ApplicationError;
import com.acme.center.platform.shared.application.result.Result;

/**
 * StudentCommandService
 * This interface defines the contract for the StudentCommandService.
 */
public interface StudentCommandService {
    /**
     * handle
     * This method is used to handle the CreateStudentCommand.
     * @param command the CreateStudentCommand containing the student data.
     * @return Result containing AcmeStudentRecordId generated for the student, or an error.
     */
    Result<AcmeStudentRecordId, ApplicationError> handle(CreateStudentCommand command);

    /**
     * handle
     * This method is used to handle the CreateStudentByProfileIdCommand.
     * Triggered reactively when a {@code ProfileCreatedEvent} is received.
     * @param command the CreateStudentByProfileIdCommand containing the profile id.
     * @return Result containing AcmeStudentRecordId generated for the student, or an error.
     */
    Result<AcmeStudentRecordId, ApplicationError> handle(CreateStudentByProfileIdCommand command);
    /**
     * handle
     * This method is used to handle the UpdateStudentMetricsOnTutorialCompletedCommand.
     * @param command the UpdateStudentMetricsOnTutorialCompletedCommand containing the student record id.
     * @return Result containing AcmeStudentRecordId for the student whose metrics are updated, or an error.
     */
    Result<AcmeStudentRecordId, ApplicationError> handle(UpdateStudentMetricsOnTutorialCompletedCommand command);
}

