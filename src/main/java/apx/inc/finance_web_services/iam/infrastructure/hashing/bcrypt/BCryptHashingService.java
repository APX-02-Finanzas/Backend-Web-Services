package apx.inc.finance_web_services.iam.infrastructure.hashing.bcrypt;

import apx.inc.finance_web_services.iam.application.internal.outboundservices.hashing.HashingService;
import org.springframework.security.crypto.password.PasswordEncoder;

public interface BCryptHashingService extends HashingService, PasswordEncoder {




}
