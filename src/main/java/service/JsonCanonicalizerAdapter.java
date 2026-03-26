package service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class JsonCanonicalizerAdapter implements Canonicalizer {
    private final JsonCanon jsonCanon;

    @Override
    public byte[] canonicalize(Object payload) {
        String canonicalJson = jsonCanon.canonizeJson(payload);
        return canonicalJson.getBytes(StandardCharsets.UTF_8);
    }
}
