package lyc.compiler.constants;

public final class Constants {
    public static final int MAX_ID       = 10;
    public static final int MIN_ID       = 1;
    public static final int MAX_INT      = 32767;
    public static final int MIN_INT      = -32768;
    public static final int MAX_STRING   = 40;
    public static final int MIN_STRING   = 0;
    public static final double MAX_FLOAT = Math.pow(3.4,38);
    public static final double MIN_FLOAT = Math.pow(1.18,-38);

    private Constants(){}

    public static boolean checkRangeId(String value) {
        return value.length() >= MIN_ID && value.length() <= MAX_ID;
    }

    public static boolean checkRangeInt(String value) {
        return Integer.valueOf(value) >= MIN_INT && Integer.valueOf(value) <= MAX_INT;
    }

    public static boolean checkRangeFlo(String value) {
        return (Float.valueOf(value) >= MIN_FLOAT && Float.valueOf(value) <= MAX_FLOAT) || (Float.valueOf(value) >= -MAX_FLOAT && Float.valueOf(value) <= -MIN_FLOAT);
    }

    public static boolean checkRangeStr(String value) {
        // Le restamos 2 a length para no contar las comillas
        return (value.length() - 2) >= MIN_STRING && (value.length() - 2) <= MAX_STRING;
    }
}
