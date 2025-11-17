package apx.inc.finance_web_services.property.infrastructure.persistence.jpa.repositories;

import apx.inc.finance_web_services.client.domain.model.aggregates.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyRepository extends JpaRepository<Client, Long> {

}
