package ch.swissbytes.module.shared.utils;


import java.math.BigDecimal;
import java.text.DecimalFormat;

public class StringUtil {

    private static final int DEFAULT_SCALE = 2;

    private StringUtil() {
    }

    public static String toInitCap(String string) {
        char[] chars = string.toLowerCase().toCharArray();
        boolean found = false;
        for (int i = 0; i < chars.length; i++) {
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                found = true;
            } else if (Character.isWhitespace(chars[i]) || chars[i] == '.' || chars[i] == '\'') { // You can add other chars here
                found = false;
            }
        }
        return String.valueOf(chars);
    }

    public static boolean isNotEmpty(String value) {
        return org.apache.commons.lang.StringUtils.isNotBlank(value);
    }

    public static boolean isEmpty(String value) {
        return !isNotEmpty(value);
    }

    public static String toCurrencyFormat(BigDecimal decimal) {
        BigDecimal bd = decimal.setScale(DEFAULT_SCALE, BigDecimal.ROUND_DOWN);

        DecimalFormat df = new DecimalFormat();

        df.setMaximumFractionDigits(DEFAULT_SCALE);

        df.setMinimumFractionDigits(DEFAULT_SCALE);

        df.setGroupingUsed(false);

        return df.format(bd);
    }
}
