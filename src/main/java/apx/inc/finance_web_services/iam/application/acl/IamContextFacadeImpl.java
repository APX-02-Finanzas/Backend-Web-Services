package apx.inc.finance_web_services.iam.application.acl;

import apx.inc.finance_web_services.iam.domain.model.aggregates.User;
import apx.inc.finance_web_services.iam.domain.model.queries.GetUserByIdQuery;
import apx.inc.finance_web_services.iam.domain.services.UserQueryService;
import apx.inc.finance_web_services.iam.interfaces.acl.IamContextFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
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

    @Override
    public Optional<User> fetchSalesManUserById(Long salesManId) {
        if (salesManId == null || salesManId <= 0) {
            return Optional.empty();
        }

        try {
            var query = new GetUserByIdQuery(salesManId);
            Optional<User> userOpt = userQueryService.handle(query);

            if (userOpt.isPresent()) {
                User user = userOpt.get();

                // LOG TEMPORAL PARA DEBUG
                log.info("Usuario encontrado: ID={}, Username={}", user.getId(), user.getUsername());
                log.info("Roles del usuario: {}", user.getUserRoles().stream()
                        .map(role -> role.getName())
                        .collect(Collectors.toList()));

                // Validar que sea salesman - COMPARACIÓN CASE-INSENSITIVE
                boolean isSalesMan = user.getUserRoles().stream()
                        .anyMatch(role -> "ROLE_SALESMAN".equalsIgnoreCase(role.getName().name()));

                if (isSalesMan) {
                    log.debug("Salesman válido encontrado: {} - {}", salesManId, user.getUsername());
                    return userOpt;
                } else {
                    log.warn("Usuario {} no tiene rol SALESMAN. Roles actuales: {}",
                            salesManId,
                            user.getUserRoles().stream()
                                    .map(role -> role.getName())
                                    .collect(Collectors.toList()));
                }
            } else {
                log.warn("Usuario con ID {} no encontrado", salesManId);
            }

            return Optional.empty();

        } catch (Exception e) {
            log.error("Error al buscar salesman: {}", salesManId, e);
            return Optional.empty();
        }
    }


}
