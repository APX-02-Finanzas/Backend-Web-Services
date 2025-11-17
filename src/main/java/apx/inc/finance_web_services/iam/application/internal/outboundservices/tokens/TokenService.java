package apx.inc.finance_web_services.iam.application.internal.outboundservices.tokens;

public interface TokenService {
    String generateToken(String userName);

    String getUserNameFromToken(String token);

    boolean validateToken(String token);
}
