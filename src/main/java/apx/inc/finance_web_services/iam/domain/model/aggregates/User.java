package apx.inc.finance_web_services.iam.domain.model.aggregates;

import apx.inc.finance_web_services.iam.domain.model.commands.UpdateUserCommand;
import apx.inc.finance_web_services.iam.domain.model.entities.Role;
import apx.inc.finance_web_services.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Entity
public class User extends AuditableAbstractAggregateRoot <User>{

    private String username;

    private String password;

    private String name;

    private String surname;

    private String email;

    @ManyToMany(fetch = FetchType.EAGER,cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name="user_id"),
            inverseJoinColumns = @JoinColumn(name="role_id")
    )
    private Set<Role> userRoles;

    // jpa constructor
    protected User(){
        super();
        this.userRoles = new HashSet<>();
    }

    //constructor without roles
    public User(String username, String password,String name,String surname, String email) {
        this();
        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.email = email;
    }

    //constructor with roles using addRoles method
    public User(String username, String password,String name,String surname, String email, List<Role> roles) {
        this(username, password, name, surname, email);
        addRoles(roles);
    }

    //update
    public User updateUserDetails(UpdateUserCommand updateUserCommand){
        this.username = updateUserCommand.username();
        this.password = updateUserCommand.password();
        this.name = updateUserCommand.name();
        this.surname = updateUserCommand.surname();
        this.email = updateUserCommand.email();
        return this;
    }

    // To convert list to set and validate roles
    public void addRoles(List<Role> roles) {
        var validatedRoleSet = Role.validateRoleSet(roles);
        this.userRoles.addAll(validatedRoleSet);
    }

}
