package apx.inc.finance_web_services.property.infrastructure.persistence.jpa.repositories;

import apx.inc.finance_web_services.client.domain.model.aggregates.Client;
import apx.inc.finance_web_services.property.domain.model.aggregates.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    List<Property> findBySalesManId(Long salesManId);
}
