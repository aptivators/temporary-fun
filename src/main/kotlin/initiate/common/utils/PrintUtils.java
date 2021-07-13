package initiate.common.utils;

public class PrintUtils {
    private PrintUtils() {
    }

    public static String formatNull(String str, Object... args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                args[i] = "";
            }
        }
        return String.format(str, args);
    }

    public static String combineStringsWithSpace(String string1, String string2) {
        return combineStringsWithDelimiter(string1, string2, " ");
    }

    public static String combineStringsWithUnderscore(String string1, String string2) {
        return combineStringsWithDelimiter(string1, string2, "_");
    }

    private static String combineStringsWithDelimiter(
            String string1, String string2, String delimiter) {
        return String.format("%s%s%s", string1, delimiter, string2);
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }
}
