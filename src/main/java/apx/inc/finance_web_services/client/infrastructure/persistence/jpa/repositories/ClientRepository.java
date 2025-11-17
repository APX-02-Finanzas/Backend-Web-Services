package apx.inc.finance_web_services.client.infrastructure.persistence.jpa.repositories;

import apx.inc.finance_web_services.client.domain.model.aggregates.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findBySalesManId(Long salesManId);
}
