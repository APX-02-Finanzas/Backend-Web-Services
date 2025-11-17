package apx.inc.finance_web_services.iam.application.internal.queryservices;

import apx.inc.finance_web_services.iam.domain.model.entities.Role;
import apx.inc.finance_web_services.iam.domain.model.queries.GetAllRolesQuery;
//import apx.inc.design_web_services_backend.iam.domain.model.queries.GetRoleByNameQuery;
import apx.inc.finance_web_services.iam.domain.services.RoleQueryService;
import apx.inc.finance_web_services.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleQueryServiceImpl implements RoleQueryService {

    private final RoleRepository roleRepository;

    public RoleQueryServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<Role> handle(GetAllRolesQuery getAllRolesQuery) {
        return roleRepository.findAll();
    }

}
