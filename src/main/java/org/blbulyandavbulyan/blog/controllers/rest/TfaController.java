package org.blbulyandavbulyan.blog.controllers.rest;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.blbulyandavbulyan.blog.dtos.authorization.TFAStatus;
import org.blbulyandavbulyan.blog.dtos.authorization.TOTPSetupResponse;
import org.blbulyandavbulyan.blog.services.TOTPSettingsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/tfa")
@RequiredArgsConstructor
@Validated
@Tags({@Tag(name="auth"), @Tag(name="tfa")})
public class TfaController {
    private final TOTPSettingsService totpSettingsService;
    @PostMapping
    public TOTPSetupResponse beginTfaSetup(Principal principal) {
        return new TOTPSetupResponse(totpSettingsService.beginSetupTFA(principal.getName()));
    }
    @PatchMapping
    public void finishSetup(Principal principal, @RequestParam @NotBlank String code) {
        totpSettingsService.finishTFASetup(principal.getName(), code);
    }

    @GetMapping("/{username}")
    public TFAStatus getTfaStatus(@PathVariable String username) {
        return new TFAStatus(totpSettingsService.isTfaEnabled(username));
    }
    @DeleteMapping
    public void disableTfa(Principal principal, @RequestParam @NotBlank String code) {
        totpSettingsService.disableTFA(principal.getName(), code);
    }
}
