package apx.inc.finance_web_services.shared.infrastructure.documentation.openapi.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfiguration {

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${spring.application.name:apx-application}")
    private String applicationName;

    @Value("${spring.profiles.active:development}")
    private String activeProfile;

    @Bean
    public OpenAPI apxPlatformOpenApi() {
        // Create OpenAPI instance
        var openApi = new OpenAPI();

        // Info section - General for APX
        openApi.info(new Info()
                .title("APX Platform API")
                .description("""
                    🚀 **APX Development Platform - RESTful APIs**
                    
                    ## About APX:
                    APX is an organization dedicated to developing innovative technological 
                    solutions and software engineering education.
                    
                    ## Features:
                    - ✅ Domain-Driven Design (DDD) Architecture
                    - ✅ CQRS Pattern for responsibility separation
                    - ✅ Secure JWT Authentication
                    - ✅ OpenAPI 3.0 Documentation
                    - ✅ Multi-environment Configuration
                    
                    ## Standards:
                    - RESTful APIs
                    - Spring Boot 3.x
                    - PostgreSQL
                    - Spring Security
                    - Clean Architecture Practices
                    
                    *Developed with technical excellence by APX Development Team*
                    """)
                .version("1.0.0")
                .contact(new Contact()
                        .name("APX Development Team")
                        .email("development@apx.com")
                        .url("https://apx.com"))
                .license(new License()
                        .name("APX Internal License")
                        .url("https://apx.com/license")));

        // External documentation
        openApi.externalDocs(new ExternalDocumentation()
                .description("APX Technical Documentation")
                .url("https://docs.apx.com"));

        // Servers configuration
        openApi.servers(getServers());

        // Security scheme
        configureSecurity(openApi);

        return openApi;
    }

    private List<Server> getServers() {
        return List.of(
                // Development Server
                new Server()
                        .url("http://localhost:" + serverPort)
                        .description("🛠️ Local Development - Port " + serverPort),

                // Production Server
                new Server()
                        .url("https://" + applicationName + ".apxapps.com")
                        .description("🚀 Production - APX Apps Cloud"),

                // Staging Server
                new Server()
                        .url("https://staging-" + applicationName + ".apxapps.com")
                        .description("🧪 Staging - Testing Environment"),

                // Testing Server
                new Server()
                        .url("https://testing-" + applicationName + ".apxapps.com")
                        .description("🔬 Testing - Quality Control")
        );
    }

    private void configureSecurity(OpenAPI openApi) {
        final String securitySchemeName = "bearerAuth";

        openApi.addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("""
                                                ### JWT Authentication - APX Security Standard
                                                
                                                Include the JWT token in the Authorization header:
                                                
                                                ```http
                                                Authorization: Bearer {your_jwt_token}
                                                ```
                                                
                                                **Typical Flow:**
                                                1. Register user via `/api/v1/authentication/sign-up`
                                                2. Authenticate via `/api/v1/authentication/sign-in`
                                                3. Use the returned token in subsequent requests
                                                
                                                *Tokens expire after 7 days by default*
                                                """)));
    }
}