package com.acme.center.platform.iam.application.commandservices;

import com.acme.center.platform.iam.domain.model.aggregates.User;
import com.acme.center.platform.iam.domain.model.commands.SignInCommand;
import com.acme.center.platform.iam.domain.model.commands.SignUpCommand;
import com.acme.center.platform.shared.application.result.ApplicationError;
import com.acme.center.platform.shared.application.result.Result;
import org.apache.commons.lang3.tuple.ImmutablePair;

/**
 * User command service
 * <p>
 *     This interface represents the service to handle user commands.
 * </p>
 */
public interface UserCommandService {
    /**
     * Handle sign-in command
     * @param command the {@link SignInCommand} command
     * @return a {@link Result} with authenticated user and token or an {@link ApplicationError}
     */
    Result<ImmutablePair<User, String>, ApplicationError> handle(SignInCommand command);

    /**
     * Handle sign-up command
     * @param command the {@link SignUpCommand} command
     * @return a {@link Result} with created user or an {@link ApplicationError}
     */
    Result<User, ApplicationError> handle(SignUpCommand command);


}

