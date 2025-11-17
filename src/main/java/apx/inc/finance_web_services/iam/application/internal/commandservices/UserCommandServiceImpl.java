package apx.inc.finance_web_services.iam.application.internal.commandservices;

import apx.inc.finance_web_services.iam.application.internal.outboundservices.hashing.HashingService;
import apx.inc.finance_web_services.iam.application.internal.outboundservices.tokens.TokenService;
import apx.inc.finance_web_services.iam.domain.model.aggregates.User;
import apx.inc.finance_web_services.iam.domain.model.commands.*;
import apx.inc.finance_web_services.iam.domain.services.UserCommandService;
import apx.inc.finance_web_services.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import apx.inc.finance_web_services.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final HashingService hashingService;
    private final TokenService tokenService;

    public UserCommandServiceImpl(UserRepository userRepository, RoleRepository roleRepository, HashingService hashingService, TokenService tokenService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.hashingService = hashingService;
        this.tokenService = tokenService;
    }

    @Override
    public Optional<User> handle(UpdateUserCommand updateUserCommand, Long userId) {

        // Check if the user exists
        var userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User with ID " + userId + " not found");
        }

        // Check if the new username is already taken by another user
        if (userRepository.existsByUsername(updateUserCommand.username())) {
            throw new IllegalArgumentException("Username " + updateUserCommand.username() + " is already taken");
        }

        var userToUpdate = userOptional.get();

        //  Encrypt the password if it's provided
        String encodedPassword = updateUserCommand.password();
        if (encodedPassword != null && !encodedPassword.isBlank()) {
            encodedPassword = hashingService.encode(encodedPassword);
        } else {
            // Si viene vacía, mantén la actual
            encodedPassword = userToUpdate.getPassword();
        }

        // Create a new command with the encoded password
        var commandWithEncodedPassword = new UpdateUserCommand(
                updateUserCommand.username(),
                encodedPassword,
                updateUserCommand.name(),
                updateUserCommand.surname(),
                updateUserCommand.email(),
                updateUserCommand.roles()
        );

        try{
            var updatedUser= userRepository.save(userToUpdate.updateUserDetails(commandWithEncodedPassword));
            return Optional.of(updatedUser);
        }catch (Exception e) {
            // Handle exception, e.g., log it or rethrow as a custom exception
            return Optional.empty();
        }
    }

    @Override
    public void handle(DeleteUserCommand deleteUserCommand) {
        if (!userRepository.existsById(deleteUserCommand.userId())) {
            throw new IllegalArgumentException("User with ID " + deleteUserCommand.userId() + " not found");
        }

        try{
            userRepository.deleteById(deleteUserCommand.userId());
        } catch (Exception e) {
            // Handle exception, e.g., log it or rethrow as a custom exception
            throw new RuntimeException("Error deleting user: " + e.getMessage(), e);
        }
    }


    @Override
    public Optional<ImmutablePair<User, String>> handle(SignInCommand signInCommand) {
        var user = userRepository.findByUsername(signInCommand.username());

        if (user.isEmpty()) {
            throw new IllegalArgumentException("User with user name " + signInCommand.username() + " not found");
        }
        if (!hashingService.matches(signInCommand.password(), user.get().getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }
        var token = tokenService.generateToken(user.get().getUsername());
        return Optional.of(ImmutablePair.of(user.get(), token));
    }

    @Override
    public Optional<User> handle(SignUpCommand signUpCommand) {
        if (userRepository.existsByUsername(signUpCommand.userName())) {
            throw new IllegalArgumentException("User with user name " + signUpCommand.userName() + " already exists");
        }
        var roles= signUpCommand.roles().stream().map(
                role->roleRepository.findByName(role)
                        .orElseThrow(() -> new IllegalArgumentException("Role " + role + " not found"))
                ).toList();
        var user = new User(signUpCommand.userName(), hashingService.encode(signUpCommand.password()),signUpCommand.name(),signUpCommand.surname(),signUpCommand.email(), roles);
        userRepository.save(user);
        return userRepository.findByUsername(signUpCommand.userName());
    }
}
