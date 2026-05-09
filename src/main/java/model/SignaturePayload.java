package model;

import enums.SignatureStatus;

public record SignaturePayload(
        String threatName,
        String firstBytesHex,
        String remainderHashHex,
        long remainderLength,
        String fileType,
        long offsetStart,
        long offsetEnd,
        SignatureStatus status
) {}