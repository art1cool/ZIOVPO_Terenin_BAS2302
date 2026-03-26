package controller;

import model.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import service.LicenseService;

@RestController
@RequestMapping("/licenses")
@RequiredArgsConstructor
public class LicenseController {

    private final LicenseService licenseService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LicenseResponse> createLicense(@Valid @RequestBody CreateLicenseRequest request) {
        LicenseResponse response = licenseService.createLicense(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/activate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TicketResponse> activateLicense(@Valid @RequestBody ActivateLicenseRequest request) {
        TicketResponse ticket = licenseService.activateLicense(request);
        return ResponseEntity.ok(ticket);
    }

    @GetMapping("/verify")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TicketResponse> verifyLicense(
            @RequestParam String code,
            @RequestParam String deviceId) {
        TicketResponse ticket = licenseService.verifyLicense(code, deviceId);
        return ResponseEntity.ok(ticket);
    }

    @PostMapping("/renew")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TicketResponse> renewLicense(@Valid @RequestBody RenewLicenseRequest request) {
        TicketResponse ticket = licenseService.renewLicense(request);
        return ResponseEntity.ok(ticket);
    }
}