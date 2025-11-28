package apx.inc.finance_web_services.iam.interfaces.rest;

import apx.inc.finance_web_services.iam.application.internal.commandservices.RecaptchaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/captcha")
@Tag(name = "CAPTCHA", description = "Endpoints para verificación CAPTCHA")
public class CaptchaController {

    private final RecaptchaService recaptchaService;

    @Value("${recaptcha.site-key}")
    private String recaptchaSiteKey;

    public CaptchaController(RecaptchaService recaptchaService) {
        this.recaptchaService = recaptchaService;
    }

    @Operation(summary = "Obtener configuración CAPTCHA (site-key)", description = "Retorna la site key para el frontend")
    @ApiResponse(responseCode = "200", description = "Configuración obtenida exitosamente")
    @GetMapping("/config")
    public ResponseEntity<?> getCaptchaConfig() {
        return ResponseEntity.ok()
                .body("{\"siteKey\": \"" + recaptchaSiteKey + "\"}");
    }

    @Operation(summary = "Verificar CAPTCHA", description = "Valida un token de reCAPTCHA")
    @ApiResponse(responseCode = "200", description = "CAPTCHA válido")
    @ApiResponse(responseCode = "400", description = "CAPTCHA inválido")
    @PostMapping("/verify")
    public ResponseEntity<?> verifyCaptcha(
            @RequestParam("token") String recaptchaToken) {  // ✅ Cambia el nombre del parámetro

        if (recaptchaToken == null || recaptchaToken.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("{\"status\": \"error\", \"message\": \"Token reCAPTCHA no proporcionado\"}");
        }

        boolean isValid = recaptchaService.verifyRecaptcha(recaptchaToken);

        if (isValid) {
            return ResponseEntity.ok()
                    .body("{\"status\": \"success\", \"message\": \"CAPTCHA válido\"}");
        } else {
            return ResponseEntity.badRequest()
                    .body("{\"status\": \"error\", \"message\": \"CAPTCHA inválido\"}");
        }
    }


}