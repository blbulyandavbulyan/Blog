package org.blbulyandavbulyan.blog.controllers.rest;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.blbulyandavbulyan.blog.dtos.authorization.TFAStatus;
import org.blbulyandavbulyan.blog.dtos.authorization.TOTPSetupResponse;
import org.blbulyandavbulyan.blog.services.TOTPSetupService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/tfa")
@RequiredArgsConstructor
@Validated
public class TfaController {
    private final TOTPSetupService totpSetupService;
    @PostMapping
    public TOTPSetupResponse beginTfaSetup(Principal principal) {
        return new TOTPSetupResponse(totpSetupService.beginSetupTFA(principal.getName()));
    }
    @PatchMapping
    public void finishSetup(Principal principal, @RequestParam @NotBlank String code) {
        totpSetupService.finishTFASetup(principal.getName(), code);
    }

    @GetMapping("/{username}")
    public TFAStatus getTfaStatus(@PathVariable String username) {
        return new TFAStatus(totpSetupService.isTfaEnabled(username));
    }
    @DeleteMapping
    public void disableTfa(Principal principal, @RequestParam @NotBlank String code) {
        totpSetupService.disableTFA(principal.getName(), code);
    }
}
