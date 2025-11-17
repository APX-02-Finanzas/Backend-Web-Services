package apx.inc.finance_web_services.iam.domain.model.commands;

public record SignInCommand(
        String username,
        String password
) {
    public SignInCommand {
        if (username==null || username.isBlank()){
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (password==null || password.isBlank()){
            throw new IllegalArgumentException("Password cannot be empty");
        }
    }
}
