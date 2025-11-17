package apx.inc.finance_web_services.iam.interfaces.acl;

import apx.inc.finance_web_services.iam.domain.model.aggregates.User;

import java.util.List;
import java.util.Optional;

public interface IamContextFacade {

    Optional<User> fetchUserById(Long userId);


}
