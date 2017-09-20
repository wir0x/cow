package ch.swissbytes.module.shared.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MailUtil {

    //    private static final String EMAIL_PATTERN =
//            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
//                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String EMAIL_PATTERN =
            "^[_a-z0-9]+(\\.[_a-z0-9]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,4})$";
    private static Pattern pattern;
    private static Matcher matcher;

    public MailUtil() {
        pattern = Pattern.compile(EMAIL_PATTERN);
    }

    public static boolean isValid(final String hex) {
        matcher = pattern.matcher(hex);
        return matcher.matches();

    }

    public static <T> void validateMail(String mails, T entity) {
        if (StringUtil.isEmpty(mails) || EntityUtil.isNull(entity)) {
            return;
        }
    }

    /**
     * Validate hex with regular expression
     *
     * @param hex hex for validation
     * @return true valid hex, false invalid hex
     */
    public boolean validate(final String hex) {
        matcher = pattern.matcher(hex);
        return matcher.matches();

    }
}