package apx.inc.finance_web_services.iam.infrastructure.persistence.jpa.repositories;

import apx.inc.finance_web_services.iam.domain.model.entities.Role;
import apx.inc.finance_web_services.iam.domain.model.valueobjects.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(Roles name);

    boolean existsByName(Roles name);
}
