package service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class SigningServiceImpl implements SigningService {
    private final KeyProvider keyProvider;
    private final Canonicalizer canonicalizer;

    @Override
    public String sign(Object payload) {
        try {
            byte[] canonicalBytes = canonicalizer.canonicalize(payload);
            PrivateKey privateKey = keyProvider.getSigningKey();
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(canonicalBytes);
            byte[] signatureBytes = signature.sign();
            return Base64.getEncoder().encodeToString(signatureBytes);
        } catch (Exception e) {
            log.error("Failed to sign payload", e);
            throw new RuntimeException("Failed to generate digital signature", e);
        }
    }

    @Override
    public byte[] signBytes(byte[] data) {
        try {
            PrivateKey privateKey = keyProvider.getSigningKey();
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(data);
            return signature.sign();
        } catch (Exception e) {
            throw new RuntimeException("Failed to sign byte array", e);
        }
    }
}