package service;

public interface SigningService {
    String sign(Object payload);
    byte[] signBytes(byte[] data);
}