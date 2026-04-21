package service;

import entity.SignatureEntity;
import enums.SignatureStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import util.BinaryWriter;
import util.HexUtils;

import java.security.MessageDigest;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BinarySignatureExportService {

    private final SigningService signingService;

    private static final String MAGIC_MANIFEST = "MF-Terenin";
    private static final String MAGIC_DATA = "DB-Terenin";
    private static final int FORMAT_VERSION = 1;

    private static final int EXPORT_FULL = 1;
    private static final int EXPORT_INCREMENT = 2;
    private static final int EXPORT_BY_IDS = 3;

    public byte[][] buildBinaryPackage(List<SignatureEntity> signatures, int exportType, long sinceEpochMillis) {
        DataBuilderResult dataResult = buildDataBin(signatures);
        byte[] dataBytes = dataResult.dataBytes;
        byte[] dataSha256 = sha256(dataBytes);

        byte[] manifestBytes = buildManifestBin(signatures, exportType, sinceEpochMillis, dataSha256, dataResult.offsets, dataResult.lengths);

        return new byte[][]{manifestBytes, dataBytes};
    }

    private DataBuilderResult buildDataBin(List<SignatureEntity> signatures) {
        BinaryWriter writer = new BinaryWriter();
        writer.writeBytes(MAGIC_DATA.getBytes(java.nio.charset.StandardCharsets.US_ASCII));
        writer.writeUint16(FORMAT_VERSION);
        writer.writeUint32(signatures.size());

        long currentOffset = 0;
        long[] offsets = new long[signatures.size()];
        long[] lengths = new long[signatures.size()];

        for (int i = 0; i < signatures.size(); i++) {
            SignatureEntity sig = signatures.get(i);
            offsets[i] = currentOffset;

            writer.writeString(sig.getThreatName());
            byte[] firstBytes = HexUtils.decodeHex(sig.getFirstBytesHex());
            writer.writeUint16(firstBytes.length);
            writer.writeBytes(firstBytes);
            byte[] remainderHash = HexUtils.decodeHex(sig.getRemainderHashHex());
            writer.writeUint16(remainderHash.length);
            writer.writeBytes(remainderHash);
            writer.writeUint32(sig.getRemainderLength());
            writer.writeString(sig.getFileType());
            writer.writeInt64(sig.getOffsetStart());
            writer.writeInt64(sig.getOffsetEnd());

            long recordLength = currentRecordLength(sig, firstBytes.length, remainderHash.length);
            lengths[i] = recordLength;
            currentOffset += recordLength;
        }

        return new DataBuilderResult(writer.toByteArray(), offsets, lengths);
    }

    private long currentRecordLength(SignatureEntity sig, int firstBytesLen, int remainderHashLen) {
        long len = 2 + sig.getThreatName().getBytes(java.nio.charset.StandardCharsets.UTF_8).length;
        len += 2 + firstBytesLen;
        len += 2 + remainderHashLen;
        len += 4;
        len += 2 + sig.getFileType().getBytes(java.nio.charset.StandardCharsets.UTF_8).length;
        len += 8;
        len += 8;
        return len;
    }

    private byte[] buildManifestBin(List<SignatureEntity> signatures, int exportType, long sinceEpochMillis,
                                    byte[] dataSha256, long[] offsets, long[] lengths) {
        BinaryWriter writer = new BinaryWriter();
        writer.writeBytes(MAGIC_MANIFEST.getBytes(java.nio.charset.StandardCharsets.US_ASCII));
        writer.writeUint16(FORMAT_VERSION);
        writer.writeUint8(exportType);
        long generatedAt = System.currentTimeMillis();
        writer.writeInt64(generatedAt);
        writer.writeInt64(sinceEpochMillis);
        writer.writeUint32(signatures.size());
        writer.writeBytes(dataSha256);

        for (int i = 0; i < signatures.size(); i++) {
            SignatureEntity sig = signatures.get(i);
            writer.writeUUID(sig.getId());
            int statusCode = (sig.getStatus() == SignatureStatus.ACTUAL) ? 1 : 2; // 1=ACTUAL,2=DELETED
            writer.writeUint8(statusCode);
            writer.writeInt64(sig.getUpdatedAt().toEpochMilli());
            writer.writeInt64(offsets[i]);
            writer.writeInt64(lengths[i]);

            byte[] recordSignature = java.util.Base64.getDecoder().decode(sig.getDigitalSignatureBase64());
            writer.writeUint32(recordSignature.length);
            writer.writeBytes(recordSignature);
        }

        byte[] unsignedManifest = writer.toByteArray();
        byte[] manifestSignature = signingService.signBytes(unsignedManifest);
        writer.writeUint32(manifestSignature.length);
        writer.writeBytes(manifestSignature);
        return writer.toByteArray();
    }

    private byte[] sha256(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(data);
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 error", e);
        }
    }

    private record DataBuilderResult(byte[] dataBytes, long[] offsets, long[] lengths) {}
}