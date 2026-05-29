package com.acme.center.platform.learning.application.internal.commandservices;

import com.acme.center.platform.learning.application.internal.outboundservices.acl.ExternalProfileService;
import com.acme.center.platform.learning.domain.exceptions.StudentNotFoundException;
import com.acme.center.platform.learning.domain.model.aggregates.Student;
import com.acme.center.platform.learning.domain.model.commands.CreateStudentByProfileIdCommand;
import com.acme.center.platform.learning.domain.model.commands.CreateStudentCommand;
import com.acme.center.platform.learning.domain.model.commands.UpdateStudentMetricsOnTutorialCompletedCommand;
import com.acme.center.platform.learning.domain.model.valueobjects.AcmeStudentRecordId;
import com.acme.center.platform.learning.domain.model.valueobjects.ProfileId;
import com.acme.center.platform.learning.application.commandservices.StudentCommandService;
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
    private final ExternalProfileService externalProfileService;

    // inherit javadoc
    public StudentCommandServiceImpl(StudentRepository studentRepository, ExternalProfileService externalProfileService) {
        this.studentRepository = studentRepository;
        this.externalProfileService = externalProfileService;
    }

    /**
     * Handles CreateStudentCommand.
     *
     * <p>Two scenarios are covered:</p>
     * <ul>
     *   <li><b>Profile does not exist:</b> creates it via ACL. The resulting
     *       {@code ProfileCreatedEvent} is dispatched synchronously by the
     *       repository adapter, so {@link com.acme.center.platform.learning.application.internal.eventhandlers.ProfileCreatedEventHandler}
     *       will have already created the {@code Student} record by the time
     *       {@code createProfile} returns. This method then queries for it.</li>
     *   <li><b>Profile already exists:</b> guards against duplicate students and
     *       creates the record directly.</li>
     * </ul>
     */
    @Override
    public AcmeStudentRecordId handle(CreateStudentCommand command) {
        var profileId = externalProfileService.fetchProfileByEmail(command.email());

        if (profileId.isEmpty()) {
            // Profile creation fires ProfileCreatedEvent → ProfileCreatedEventHandler
            // creates the Student synchronously before this call returns.
            profileId = externalProfileService.createProfile(
                    command.firstName(), command.lastName(), command.email(),
                    command.street(), command.number(), command.city(),
                    command.postalCode(), command.country());

            if (profileId.isEmpty()) {
                throw new IllegalArgumentException("Unable to create student profile.");
            }

            // Student was already created by the event handler.
            return studentRepository.findByProfileId(profileId.get())
                    .orElseThrow(() -> new IllegalArgumentException("Student record not found after profile creation."))
                    .getAcmeStudentRecordId();
        }

        // Profile already exists — guard against duplicates, then create student.
        studentRepository.findByProfileId(profileId.get()).ifPresent(student -> {
            throw new IllegalArgumentException(
                    "Student with ID %s already exists with same profile."
                            .formatted(student.getAcmeStudentRecordId().studentRecordId()));
        });

        var student = new Student(profileId.get());
        studentRepository.save(student);
        return student.getAcmeStudentRecordId();
    }

    /**
     * Handles CreateStudentByProfileIdCommand.
     *
     * <p>Creates a {@code Student} for an already-persisted profile. Idempotent:
     * if a student for the given profile already exists the existing record is
     * returned without creating a duplicate.</p>
     */
    @Override
    public AcmeStudentRecordId handle(CreateStudentByProfileIdCommand command) {
        return studentRepository.findByProfileId(new ProfileId(command.profileId()))
                .orElseGet(() -> {
                    var student = new Student(command.profileId());
                    return studentRepository.save(student);
                })
                .getAcmeStudentRecordId();
    }

    // inherit javadoc
    @Override
    public AcmeStudentRecordId handle(UpdateStudentMetricsOnTutorialCompletedCommand command) {
        studentRepository.findByAcmeStudentRecordId(command.studentRecordId()).map(student -> {
            student.updateMetricsOnTutorialCompleted();
            studentRepository.save(student);
            return student.getAcmeStudentRecordId();
        }).orElseThrow(() -> new StudentNotFoundException(command.studentRecordId()));
        return null;
    }
}


