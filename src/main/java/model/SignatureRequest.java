package model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class SignatureRequest {

    @NotBlank(message = "threatName must not be blank")
    private String threatName;

    @NotBlank(message = "firstBytesHex must not be blank")
    private String firstBytesHex;

    @NotBlank(message = "remainderHashHex must not be blank")
    private String remainderHashHex;

    @PositiveOrZero(message = "remainderLength must be >= 0")
    private long remainderLength;

    @NotBlank(message = "fileType must not be blank")
    private String fileType;

    @PositiveOrZero(message = "offsetStart must be >= 0")
    private long offsetStart;

    @PositiveOrZero(message = "offsetEnd must be >= offsetStart")
    private long offsetEnd;
}