package controller;

import entity.SignatureEntity;
import enums.SignatureStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import repository.SignatureRepository;
import service.BinarySignatureExportService;
import service.MultipartMixedResponseFactory;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/binary/signatures")
@RequiredArgsConstructor
public class BinarySignatureController {

    private final SignatureRepository signatureRepository;
    private final BinarySignatureExportService exportService;
    private final MultipartMixedResponseFactory multipartFactory;

    @GetMapping("/full")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MultiValueMap<String, Object>> getFull() {
        List<SignatureEntity> signatures = signatureRepository.findByStatus(SignatureStatus.ACTUAL);
        byte[][] packageBytes = exportService.buildBinaryPackage(signatures, 1, -1L);
        return multipartFactory.create(packageBytes[0], packageBytes[1]);
    }

    @GetMapping("/increment")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MultiValueMap<String, Object>> getIncrement(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant since) {
        if (since == null) {
            throw new IllegalArgumentException("Parameter 'since' is required");
        }
        long sinceMillis = since.toEpochMilli();
        List<SignatureEntity> signatures = signatureRepository.findByUpdatedAtAfter(since);
        byte[][] packageBytes = exportService.buildBinaryPackage(signatures, 2, sinceMillis);
        return multipartFactory.create(packageBytes[0], packageBytes[1]);
    }

    @PostMapping("/by-ids")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MultiValueMap<String, Object>> getByIds(@RequestBody List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("List of ids must not be empty");
        }
        List<SignatureEntity> signatures = signatureRepository.findAllById(ids);
        byte[][] packageBytes = exportService.buildBinaryPackage(signatures, 3, -1L);
        return multipartFactory.create(packageBytes[0], packageBytes[1]);
    }
}