package controller;

import entity.SignatureEntity;
import enums.SignatureStatus;
import lombok.RequiredArgsConstructor;
import model.PresignedUrlsRequest;
import model.PresignedUrlsResponse;
import model.SignatureResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import repository.SignatureRepository;
import service.MinioService;
import service.SigningService;
import util.SignatureCalculator;
import model.SignaturePayload;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/signatures")
@RequiredArgsConstructor
public class FileSignatureController {

    private final SignatureRepository signatureRepository;
    private final MinioService minioService;
    private final SigningService signingService;

    @PostMapping("/upload-file")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SignatureResponse> uploadSignatureFromFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("threatName") String threatName,
            @RequestParam(value = "firstBytesLimit", defaultValue = "256") int firstBytesLimit) {

        try {
            var sigData = SignatureCalculator.calculate(file, firstBytesLimit);
            String fileType = extractFileType(file.getOriginalFilename());

            String minioObjectName = minioService.uploadFile(file);

            SignatureEntity entity = new SignatureEntity();
            entity.setThreatName(threatName);
            entity.setFirstBytesHex(sigData.firstBytesHex());
            entity.setRemainderHashHex(sigData.remainderHashHex());
            entity.setRemainderLength(sigData.remainderLength());
            entity.setFileType(fileType);
            entity.setOffsetStart(sigData.offsetStart());
            entity.setOffsetEnd(sigData.offsetEnd());
            entity.setUpdatedAt(Instant.now());
            entity.setStatus(SignatureStatus.ACTUAL);
            entity.setMinioObjectName(minioObjectName);

            String signature = generateSignature(entity);
            entity.setDigitalSignatureBase64(signature);

            SignatureEntity saved = signatureRepository.save(entity);

            return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
        } catch (Exception e) {
            throw new RuntimeException("Failed to process file", e);
        }
    }

    @PostMapping("/presigned-urls")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PresignedUrlsResponse> getPresignedUrls(@RequestBody PresignedUrlsRequest request) {
        List<SignatureEntity> signatures = signatureRepository.findAllById(request.getIds());
        Map<UUID, String> urlMap = new HashMap<>();
        for (SignatureEntity sig : signatures) {
            try {
                if (sig.getMinioObjectName() != null && !sig.getMinioObjectName().isBlank()) {
                    String url = minioService.getPresignedUrl(sig.getMinioObjectName());
                    urlMap.put(sig.getId(), url);
                } else {
                    urlMap.put(sig.getId(), null);
                }
            } catch (Exception e) {
                e.printStackTrace();
                urlMap.put(sig.getId(), "error: " + e.getMessage());
            }
        }
        PresignedUrlsResponse response = new PresignedUrlsResponse();
        response.setPresignedUrls(urlMap);
        return ResponseEntity.ok(response);
    }

    private String extractFileType(String filename) {
        if (filename == null) return "bin";
        int dot = filename.lastIndexOf('.');
        if (dot == -1) return "bin";
        return filename.substring(dot + 1).toLowerCase();
    }

    private String generateSignature(SignatureEntity entity) {
        var payload = new SignaturePayload(
                entity.getThreatName(),
                entity.getFirstBytesHex(),
                entity.getRemainderHashHex(),
                entity.getRemainderLength(),
                entity.getFileType(),
                entity.getOffsetStart(),
                entity.getOffsetEnd(),
                entity.getStatus()
        );
        return signingService.sign(payload);
    }

    private SignatureResponse toResponse(SignatureEntity entity) {
        SignatureResponse resp = new SignatureResponse();
        resp.setId(entity.getId());
        resp.setThreatName(entity.getThreatName());
        resp.setFirstBytesHex(entity.getFirstBytesHex());
        resp.setRemainderHashHex(entity.getRemainderHashHex());
        resp.setRemainderLength(entity.getRemainderLength());
        resp.setFileType(entity.getFileType());
        resp.setOffsetStart(entity.getOffsetStart());
        resp.setOffsetEnd(entity.getOffsetEnd());
        resp.setUpdatedAt(entity.getUpdatedAt());
        resp.setStatus(entity.getStatus());
        resp.setDigitalSignatureBase64(entity.getDigitalSignatureBase64());
        return resp;
    }
}