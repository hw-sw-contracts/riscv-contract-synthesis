package contractgen.util;

public class StringUtils {

    public static String toBinaryEncoding(String hexStr) {
        long decimal = Long.parseLong(hexStr,16);
        return String.format("%32s", Long.toBinaryString(decimal)).replace(' ', '0');
    }

    public static String toHexEncoding(String binaryStr) {
        long decimal = Long.parseLong(binaryStr,2);
        return String.format("%8s", Long.toHexString(decimal)).replace(' ', '0');
    }

    public static String toHexEncoding(Long value) {
        return String.format("%8s", Long.toHexString(value)).replace(' ', '0');
    }

    public static String toBinaryEncoding(Long value) {
        return String.format("%32s", Long.toBinaryString(value)).replace(' ', '0');
    }

    public static boolean equalValue(String binaryStr1, String binaryStr2) {
        return Long.parseLong(binaryStr1,2) == Long.parseLong(binaryStr2,2);
    }

    public static String expandToLength(String s, long length, Character charToFill) {
        if (length - s.length() <= 0) return s;
        return String.format("%0" + (length - s.length()) + "d", 0).replace('0', charToFill) + s;
    }
}
