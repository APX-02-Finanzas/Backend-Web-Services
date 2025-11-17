package apx.inc.finance_web_services.iam.interfaces.rest;

import apx.inc.finance_web_services.iam.domain.model.commands.DeleteUserCommand;
import apx.inc.finance_web_services.iam.domain.model.commands.UpdateUserCommand;
import apx.inc.finance_web_services.iam.domain.model.queries.*;
import apx.inc.finance_web_services.iam.domain.services.UserCommandService;
import apx.inc.finance_web_services.iam.domain.services.UserQueryService;
import apx.inc.finance_web_services.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import apx.inc.finance_web_services.iam.interfaces.rest.resources.UpdateUserResource;
import apx.inc.finance_web_services.iam.interfaces.rest.resources.UserResource;
import apx.inc.finance_web_services.iam.interfaces.rest.transform.UpdateUserCommandFromResourceAssembler;
import apx.inc.finance_web_services.iam.interfaces.rest.transform.UserResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/users", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Users", description = "Operations related to users")
public class UserController {
    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;


    public UserController(UserCommandService userCommandService, UserQueryService userQueryService) {
        this.userCommandService = userCommandService;
        this.userQueryService = userQueryService;
    }

    private Long getUserIdFromContext() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var principal = auth.getPrincipal();
        if (principal instanceof UserDetailsImpl userDetails) {
            System.out.println("🪪 User ID from context: " + userDetails.getId());
            return userDetails.getId();
        }
        throw new RuntimeException("Invalid principal type");
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update a user by ID", description = "Update a specific user (admin operation).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResource> updateUserById(
            @PathVariable Long id,
            @RequestBody UpdateUserResource updateUserResource) {

        // 1Convertir el recurso a comando
        UpdateUserCommand command = UpdateUserCommandFromResourceAssembler
                .toCommandFromResource(updateUserResource);

        // 2Ejecutar el comando pasando el ID explícito
        var updatedUser = userCommandService.handle(command, id);

        // 3Retornar respuesta
        return updatedUser.map(user ->
                ResponseEntity.ok(UserResourceFromEntityAssembler.toResourceFromEntity(user))
        ).orElse(ResponseEntity.notFound().build());
    }



    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete a user", description = "Deletes a user by its ID.")
    @ApiResponses(value = {
            @ApiResponse (responseCode = "204", description = "User deleted successfully"),
            @ApiResponse (responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResource> deleteUser(@PathVariable Long userId) {

        // Crear el comando de eliminación (No se necesita un recurso para eliminar)
        DeleteUserCommand deleteUserCommand = new DeleteUserCommand(userId);

        // Ejecutar el comando de eliminación
        userCommandService.handle(deleteUserCommand);

        // Verificar si el estudiante fue eliminado exitosamente

        return ResponseEntity.noContent().build(); // 204 No Content
    }



    @GetMapping("/{userId}")
    @Operation(summary = "Get a user by ID", description = "Retrieves a user by its ID.")
    @ApiResponses(value = {
            @ApiResponse (responseCode = "200", description = "user retrieved successfully"),
            @ApiResponse (responseCode = "404", description = "user not found")
    })
    public ResponseEntity<UserResource> getUserById(@PathVariable Long userId) {
        //Crear el query para obtener el estudiante por ID
        GetUserByIdQuery getUserByIdQuery = new GetUserByIdQuery(userId);

        // Ejecutar el query
        var userOptional = userQueryService.handle(getUserByIdQuery);

        // Verificar si el estudiante fue encontrado
        if (userOptional.isPresent()) {
            var userResource = UserResourceFromEntityAssembler.toResourceFromEntity(userOptional.get());
            return ResponseEntity.ok(userResource); // 200 OK
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieves all users.")
    @ApiResponses(value = {
            @ApiResponse (responseCode = "200", description = "users retrieved successfully"),
            @ApiResponse (responseCode = "404", description = "No users found")
    })
    public ResponseEntity<List<UserResource>> getAllUsers() {

        // Crear el query para obtener todos los estudiantes
        GetAllUsersQuery getAllUsersQuery = new GetAllUsersQuery();

        // Ejecutar el query para obtener todos los estudiantes
        var users = userQueryService.handle(getAllUsersQuery); // null para obtener todos

        // Verificar si se encontraron estudiantes
        if (users.isEmpty()) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }

        // Convertir la lista de estudiantes a recursos
        var userResources = users.stream()
                .map(UserResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(userResources); // 200 OK
    }

    @GetMapping("/email/{username}")
    @Operation(summary = "Get a user by username", description = "Retrieves a user by username.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResource> getUserByUsername(@PathVariable String username) {
        //Create the query to get the user by username
        GetUserByUsernameQuery getUserByUsernameQuery =new GetUserByUsernameQuery(username);

        // Execute the query
        var userOptional =userQueryService.handle(getUserByUsernameQuery);

        // Check if the user was found
        if (userOptional.isPresent()) {
            var userResource = UserResourceFromEntityAssembler.toResourceFromEntity(userOptional.get());
            return ResponseEntity.ok(userResource); // 200 OK
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user", description = "Retrieves the currently authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResource> getCurrentUser() {
        try {
            Long userId = getUserIdFromContext(); // obtiene el ID del usuario autenticado
            var userOptional = userQueryService.handle(new GetUserByIdQuery(userId));

            if (userOptional.isPresent()) {
                var userResource = UserResourceFromEntityAssembler.toResourceFromEntity(userOptional.get());
                return ResponseEntity.ok(userResource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).build(); // Si no hay usuario autenticado
        }
    }


}
