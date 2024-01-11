package contractgen.util;

/**
 * Util methods related to manipulating strings.
 */
public class StringUtils {

    /**
     * @param hexStr The value encoded in hexadecimal.
     * @return The value encoded in binary.
     */
    public static String toBinaryEncoding(String hexStr) {
        long decimal = Long.parseLong(hexStr, 16);
        return String.format("%32s", Long.toBinaryString(decimal)).replace(' ', '0');
    }

    /**
     * @param binaryStr The value encoded in binary.
     * @return The value encoded in hexadecimal.
     */
    public static String toHexEncoding(String binaryStr) {
        long decimal = Long.parseLong(binaryStr, 2);
        return String.format("%8s", Long.toHexString(decimal)).replace(' ', '0');
    }

    /**
     * @param value The value as long.
     * @return The value encoded in hexadecimal.
     */
    public static String toHexEncoding(Long value) {
        return String.format("%8s", Long.toHexString(value)).replace(' ', '0');
    }

    /**
     * @param value The value as long.
     * @return The value encoded in binary.
     */
    public static String toBinaryEncoding(Long value) {
        return String.format("%32s", Long.toBinaryString(value)).replace(' ', '0');
    }

    /**
     * @param binaryStr1 The first value encoded in binary.
     * @param binaryStr2 The second value encoded in binary.
     * @return Whether the two values represent the same value.
     */
    public static boolean equalValue(String binaryStr1, String binaryStr2) {
        return Long.parseLong(binaryStr1, 2) == Long.parseLong(binaryStr2, 2);
    }

    /**
     * @param s          The string to be expanded.
     * @param length     The desired length.
     * @param charToFill The desired character to be used as prefix.
     * @return The expanded string.
     */
    public static String expandToLength(String s, long length, Character charToFill) {
        if (length - s.length() <= 0) return s;
        return String.format("%0" + (length - s.length()) + "d", 0).replace('0', charToFill) + s;
    }
}
