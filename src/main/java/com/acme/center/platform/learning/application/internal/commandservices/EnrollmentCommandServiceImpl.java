package com.acme.center.platform.learning.application.internal.commandservices;

import com.acme.center.platform.learning.application.commandservices.EnrollmentCommandService;
import com.acme.center.platform.learning.domain.exceptions.CourseNotFoundException;
import com.acme.center.platform.learning.domain.exceptions.EnrollmentNotFoundException;
import com.acme.center.platform.learning.domain.exceptions.EnrollmentRequestException;
import com.acme.center.platform.learning.domain.exceptions.StudentNotFoundException;
import com.acme.center.platform.learning.domain.model.aggregates.Enrollment;
import com.acme.center.platform.learning.domain.model.commands.CancelEnrollmentCommand;
import com.acme.center.platform.learning.domain.model.commands.CompleteTutorialForEnrollmentCommand;
import com.acme.center.platform.learning.domain.model.commands.ConfirmEnrollmentCommand;
import com.acme.center.platform.learning.domain.model.commands.RejectEnrollmentCommand;
import com.acme.center.platform.learning.domain.model.commands.RequestEnrollmentCommand;
import com.acme.center.platform.learning.domain.repositories.CourseRepository;
import com.acme.center.platform.learning.domain.repositories.EnrollmentRepository;
import com.acme.center.platform.learning.domain.repositories.StudentRepository;
import org.springframework.stereotype.Service;

/**
 * Implementation of the EnrollmentCommandService interface.
 */
@Service
public class EnrollmentCommandServiceImpl implements EnrollmentCommandService {
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;

    public EnrollmentCommandServiceImpl(CourseRepository courseRepository, StudentRepository studentRepository, EnrollmentRepository enrollmentRepository) {
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    public Long handle(RequestEnrollmentCommand command) {
        if (!studentRepository.existsByAcmeStudentRecordId(command.studentRecordId())) {
            throw new StudentNotFoundException(command.studentRecordId());
        }
        var course = courseRepository.findById(command.courseId()).orElseThrow(() -> new CourseNotFoundException(command.courseId()));
        try {
            var enrollment = new Enrollment(command.studentRecordId(), course);
            enrollment = enrollmentRepository.save(enrollment);
            return enrollment.getId();
        } catch (Exception e) {
            throw new EnrollmentRequestException(e.getMessage());
        }
    }

    @Override
    public Long handle(ConfirmEnrollmentCommand command) {
        return enrollmentRepository.findById(command.enrollmentId()).map(enrollment -> {
            enrollment.confirm();
            return enrollmentRepository.save(enrollment).getId();
        }).orElseThrow(() -> new EnrollmentNotFoundException(command.enrollmentId()));
    }

    @Override
    public Long handle(RejectEnrollmentCommand command) {
        return enrollmentRepository.findById(command.enrollmentId()).map(enrollment -> {
            enrollment.reject();
            return enrollmentRepository.save(enrollment).getId();
        }).orElseThrow(() -> new EnrollmentNotFoundException(command.enrollmentId()));
    }

    @Override
    public Long handle(CancelEnrollmentCommand command) {
        return enrollmentRepository.findById(command.enrollmentId()).map(enrollment -> {
            enrollment.cancel();
            return enrollmentRepository.save(enrollment).getId();
        }).orElseThrow(() -> new EnrollmentNotFoundException(command.enrollmentId()));
    }

    @Override
    public Long handle(CompleteTutorialForEnrollmentCommand command) {
        return enrollmentRepository.findById(command.enrollmentId()).map(enrollment -> {
            enrollment.completeTutorial(command.tutorialId());
            return enrollmentRepository.save(enrollment).getId();
        }).orElseThrow(() -> new EnrollmentNotFoundException(command.enrollmentId()));
    }
}
