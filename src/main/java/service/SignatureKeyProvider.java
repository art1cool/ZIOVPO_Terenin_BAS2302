package service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.security.PrivateKey;

@Component
@RequiredArgsConstructor
public class SignatureKeyProvider implements KeyProvider {
    private final SignatureKeyStoreService keyStoreService;

    @Override
    public PrivateKey getSigningKey() {
        return keyStoreService.getPrivateKey();
    }
}
