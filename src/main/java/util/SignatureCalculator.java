package util;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SignatureCalculator {
    public static SignatureData calculate(MultipartFile file, int firstBytesLimit)
            throws IOException, NoSuchAlgorithmException {
        byte[] allBytes = file.getBytes();
        long fileSize = allBytes.length;
        int firstLen = Math.min(firstBytesLimit, (int) fileSize);
        byte[] firstBytes = new byte[firstLen];
        System.arraycopy(allBytes, 0, firstBytes, 0, firstLen);
        String firstBytesHex = bytesToHex(firstBytes);

        long remainderLength = fileSize - firstLen;
        byte[] remainder = new byte[(int) remainderLength];
        System.arraycopy(allBytes, firstLen, remainder, 0, (int) remainderLength);
        String remainderHashHex = sha256Hex(remainder);

        return new SignatureData(firstBytesHex, remainderHashHex, remainderLength, 0L, fileSize - 1);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private static String sha256Hex(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(data);
        return bytesToHex(hash);
    }

    public record SignatureData(String firstBytesHex, String remainderHashHex,
                                long remainderLength, long offsetStart, long offsetEnd) {}
}