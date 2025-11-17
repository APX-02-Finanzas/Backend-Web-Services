package apx.inc.finance_web_services.iam.interfaces.rest;

import apx.inc.finance_web_services.iam.domain.model.entities.Role;
import apx.inc.finance_web_services.iam.domain.model.queries.GetAllRolesQuery;
import apx.inc.finance_web_services.iam.domain.services.RoleCommandService;
import apx.inc.finance_web_services.iam.domain.services.RoleQueryService;
import apx.inc.finance_web_services.iam.interfaces.rest.resources.RoleResource;
import apx.inc.finance_web_services.iam.interfaces.rest.transform.RoleResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/roles", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name= "Roles", description = "Operations related to roles management")
public class RolesController {
    private final RoleCommandService roleCommandService;
    private final RoleQueryService roleQueryService;

    public RolesController(RoleCommandService roleCommandService, RoleQueryService roleQueryService) {
        this.roleCommandService = roleCommandService;
        this.roleQueryService = roleQueryService;
    }

    @GetMapping
    @Operation(summary = "Get all roles", description = "Fetches all available roles in the system")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved roles"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials"),
            }
    )
    public ResponseEntity<List<RoleResource>> getAllRoles() {
        //create a query to get all roles
        GetAllRolesQuery getAllRolesQuery = new GetAllRolesQuery();

        //handle the query using the roleQueryService
        List<Role> roles = roleQueryService.handle(getAllRolesQuery);

        //verify if roles are not null or empty
        if (roles == null || roles.isEmpty()) {
            return ResponseEntity.noContent().build(); // 404 No Content
        }

        //map the roles to RoleResource
        var roleResources = roles.stream()
                .map(RoleResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(roleResources);
    }

}
