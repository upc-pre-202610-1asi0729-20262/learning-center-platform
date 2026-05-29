package com.acme.center.platform.learning.application.internal.commandservices;

import com.acme.center.platform.learning.application.commandservices.StudentCommandService;
import com.acme.center.platform.learning.application.internal.outboundservices.acl.ExternalProfileService;
import com.acme.center.platform.learning.domain.exceptions.StudentNotFoundException;
import com.acme.center.platform.learning.domain.model.aggregates.Student;
import com.acme.center.platform.learning.domain.model.commands.CreateStudentByProfileIdCommand;
import com.acme.center.platform.learning.domain.model.commands.CreateStudentCommand;
import com.acme.center.platform.learning.domain.model.commands.UpdateStudentMetricsOnTutorialCompletedCommand;
import com.acme.center.platform.learning.domain.model.valueobjects.AcmeStudentRecordId;
import com.acme.center.platform.learning.domain.model.valueobjects.ProfileId;
import com.acme.center.platform.learning.domain.repositories.StudentRepository;
import org.springframework.stereotype.Service;

/**
 * Implementation of the StudentCommandService interface.
 */
@Service
public class StudentCommandServiceImpl implements StudentCommandService {
    private final StudentRepository studentRepository;
    private final ExternalProfileService externalProfileService;

    public StudentCommandServiceImpl(StudentRepository studentRepository, ExternalProfileService externalProfileService) {
        this.studentRepository = studentRepository;
        this.externalProfileService = externalProfileService;
    }

    @Override
    public AcmeStudentRecordId handle(CreateStudentCommand command) {
        var profileId = externalProfileService.fetchProfileByEmail(command.email());

        if (profileId.isEmpty()) {
            profileId = externalProfileService.createProfile(
                    command.firstName(), command.lastName(), command.email(),
                    command.street(), command.number(), command.city(),
                    command.postalCode(), command.country());

            if (profileId.isEmpty()) {
                throw new IllegalArgumentException("Unable to create student profile.");
            }

            return studentRepository.findByProfileId(profileId.get())
                    .orElseThrow(() -> new IllegalArgumentException("Student record not found after profile creation."))
                    .getAcmeStudentRecordId();
        }

        studentRepository.findByProfileId(profileId.get()).ifPresent(student -> {
            throw new IllegalArgumentException(
                    "Student with ID %s already exists with same profile."
                            .formatted(student.getAcmeStudentRecordId().studentRecordId()));
        });

        var student = new Student(profileId.get());
        student = studentRepository.save(student);
        return student.getAcmeStudentRecordId();
    }

    @Override
    public AcmeStudentRecordId handle(CreateStudentByProfileIdCommand command) {
        return studentRepository.findByProfileId(new ProfileId(command.profileId()))
                .orElseGet(() -> {
                    var student = new Student(command.profileId());
                    return studentRepository.save(student);
                })
                .getAcmeStudentRecordId();
    }

    @Override
    public AcmeStudentRecordId handle(UpdateStudentMetricsOnTutorialCompletedCommand command) {
        return studentRepository.findByAcmeStudentRecordId(command.studentRecordId()).map(student -> {
            student.updateMetricsOnTutorialCompleted();
            var updatedStudent = studentRepository.save(student);
            return updatedStudent.getAcmeStudentRecordId();
        }).orElseThrow(() -> new StudentNotFoundException(command.studentRecordId()));
    }
}
