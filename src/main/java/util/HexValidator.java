package util;

public class HexValidator {
    private static final String HEX_PATTERN = "^[0-9a-fA-F]+$";

    public static boolean isValidHex(String hex) {
        return hex != null && hex.matches(HEX_PATTERN);
    }
}