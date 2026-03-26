package service;

import java.security.PrivateKey;

public interface KeyProvider {
    PrivateKey getSigningKey();
}
