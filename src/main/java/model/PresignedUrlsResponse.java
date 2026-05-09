package model;

import lombok.Data;
import java.util.Map;
import java.util.UUID;

@Data
public class PresignedUrlsResponse {
    private Map<UUID, String> presignedUrls;
}