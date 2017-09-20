package ch.swissbytes.module.shared.utils;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class KeyUtil {

    public static String orderIdToApp(String key1, String key2, String key3) {
        String _key1 = setZero(key1, 5); // account id
        String _key2 = setZero(key2, 6); // payment id
        String _key3 = setZero(key3, 6); // user id
        String _key4 = getRandomId(999); // random id
        String orderId = _key1 + _key2 + _key3 + _key4;
        return orderId.length() > 20 ? getDefaultKey(orderId, 20) : orderId;
    }

    public static String orderIdToWeb(String key1, String key2, String key3, String key4) {
        String _key1 = setZero(key1, 5); // account id
        String _key2 = setZero(key2, 5); // payment id
        String _key3 = setZero(key3, 2); // amount devices
        String _key4 = setZero(key4, 5); // user id
        String _key5 = getRandomId(999);
        String orderId = _key1 + _key2 + _key3 + _key4 + _key5;
        return orderId.length() > 20 ? getDefaultKey(orderId, 20) : orderId;
    }

    private static String getDefaultKey(String name, int length) {
        String key = "";
        String _name = removeSpace(name);
        int lengthName = getLengthStr(name, length);
        for (int i = 0; i < lengthName; ++i) key += _name.substring(i, i + 1);
        return key;
    }

    private static int getLengthStr(String str, int length) {
        return str.length() > length ? length : str.length();
    }

    private static String getRandomId(int number) {
        return String.valueOf(new Random().nextInt(number));
    }

    public static String random(int min, int max) {
        int trackerId = ThreadLocalRandom.current().nextInt(min, max);
        return String.valueOf(trackerId);
    }

    public static String setZero(String number, int length) {
        String _key = "";
        String _number = removeSpace(number);
        int _length = getLengthStr(_number, length);
        if (_number.length() > length) return getDefaultKey(_number, length);
        for (int i = _length; i < length; ++i) _key += "0";
        _key += _number;
        return _key;
    }

    private static String removeSpace(String str) {
        return str.replaceAll("\\s+", "");
    }

    public static double decimalFormat(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
