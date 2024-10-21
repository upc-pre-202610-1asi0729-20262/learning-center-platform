package com.acme.center.platform.learning.application.internal.commandservices;

import com.acme.center.platform.learning.domain.exceptions.StudentNotFoundException;
import com.acme.center.platform.learning.domain.model.commands.CreateStudentCommand;
import com.acme.center.platform.learning.domain.model.commands.UpdateStudentMetricsOnTutorialCompletedCommand;
import com.acme.center.platform.learning.domain.model.valueobjects.AcmeStudentRecordId;
import com.acme.center.platform.learning.domain.services.StudentCommandService;
import com.acme.center.platform.learning.infrastructure.persistence.jpa.repositories.StudentRepository;
import org.springframework.stereotype.Service;

/**
 * Implementation of the StudentCommandService interface.
 * <p>This class is responsible for handling the commands related to the Student aggregate. It requires a StudentRepository.</p>
 * @see StudentCommandService
 * @see StudentRepository
 */
@Service
public class StudentCommandServiceImpl implements StudentCommandService {
    private final StudentRepository studentRepository;

    // inherit javadoc
    public StudentCommandServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // inherit javadoc
    @Override
    public AcmeStudentRecordId handle(CreateStudentCommand command) {
        // Fetch profile from external service by email

        // TODO: Implement the logic to fetch the profile from the external service by email
        // If the profile is empty, create a new profile with command data.
        // If the profile is not empty, check if student exists in the repository.
        // If profile is still empty throw an exception.
        // Create a new student with the profile and save it in the repository.
        // Return the student record id. For now, return a new AcmeStudentRecordId until the logic is implemented.
        return new AcmeStudentRecordId();
    }

    // inherit javadoc
    @Override
    public AcmeStudentRecordId handle(UpdateStudentMetricsOnTutorialCompletedCommand command) {
        studentRepository.findByAcmeStudentRecordId(command.studentRecordId()).map(student -> {
            // Update the student metrics
            student.updateMetricsOnTutorialCompleted();
            studentRepository.save(student);
            return student.getAcmeStudentRecordId();
        }).orElseThrow(() -> new StudentNotFoundException(command.studentRecordId()));
        return null;
    }
}
