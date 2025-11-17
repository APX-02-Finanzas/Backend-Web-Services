package apx.inc.finance_web_services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class FinanceWebServicesApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinanceWebServicesApplication.class, args);
    }

}
