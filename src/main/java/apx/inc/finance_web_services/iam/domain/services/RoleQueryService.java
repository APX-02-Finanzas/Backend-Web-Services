package apx.inc.finance_web_services.iam.domain.services;

import apx.inc.finance_web_services.iam.domain.model.entities.Role;
import apx.inc.finance_web_services.iam.domain.model.queries.GetAllRolesQuery;

import java.util.List;

public interface RoleQueryService {

    List<Role> handle(GetAllRolesQuery getAllRolesQuery);
}
