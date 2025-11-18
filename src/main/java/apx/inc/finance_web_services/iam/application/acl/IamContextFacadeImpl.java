package apx.inc.finance_web_services.iam.application.acl;

import apx.inc.finance_web_services.iam.domain.model.aggregates.User;
import apx.inc.finance_web_services.iam.domain.model.queries.GetUserByIdQuery;
import apx.inc.finance_web_services.iam.domain.services.UserQueryService;
import apx.inc.finance_web_services.iam.interfaces.acl.IamContextFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IamContextFacadeImpl implements IamContextFacade {

    private final UserQueryService userQueryService;

    @Override
    public Optional<User> fetchUserById(Long userId) {
        if (userId == null || userId <= 0) {
            return Optional.empty();
        }
        try {
            var query = new GetUserByIdQuery(userId);
            var result = userQueryService.handle(query);
            return result;
        } catch (Exception e) {
            return Optional.empty();
        }
    }



}
