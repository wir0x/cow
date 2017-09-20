package ch.swissbytes.module.shared.utils;

import org.apache.commons.codec.binary.Base64;
import org.hibernate.annotations.common.util.impl.LoggerFactory;
import org.jboss.logging.Logger;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encode {

    private static Logger log = LoggerFactory.logger(Encode.class);

    public static String encode(String password) {

        Base64 encoder = new Base64();
        String encodedBytes = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update((password.getBytes("UTF-8")));
            byte[] digest = messageDigest.digest();
            encodedBytes = new String(encoder.encode(digest));

        } catch (NoSuchAlgorithmException | UnsupportedEncodingException nsa) {
            log.error(nsa.getMessage());
        }
        return encodedBytes;
    }
}
