package ch.swissbytes.module.shared.utils;

import ch.swissbytes.module.buho.service.configuration.ConfigurationKey;
import ch.swissbytes.module.buho.service.configuration.KeyAppConfiguration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    /**
     * Simple Date Format
     */
    public static final SimpleDateFormat SDF = new SimpleDateFormat(KeyAppConfiguration.getString(ConfigurationKey.DATE_FORMAT_SHORT));

    public static final SimpleDateFormat SDF_TIME_S = new SimpleDateFormat("HH:mm");

    public static final SimpleDateFormat SDF_TIME_C = new SimpleDateFormat("HH:mm:ss");

    public static final SimpleDateFormat SDF_DETAIL = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public static final SimpleDateFormat SDF_DETAIL_ST = new SimpleDateFormat("dd-MM-yyyy HH:mm");

    public static final SimpleDateFormat SDF_DETAIL_S = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public static final SimpleDateFormat SDF_DESC = new SimpleDateFormat("dd-MM-yyyy");

    public static final SimpleDateFormat SDF_ASCE = new SimpleDateFormat("yyyy-MM-dd");

    public static final SimpleDateFormat SDF_INTRA = new SimpleDateFormat("yyyyMMdd");

    public static final SimpleDateFormat SDF_INTRA_LONG = new SimpleDateFormat("yyyyMMddHHmmss");

    /**
     *
     */
    public DateUtil() {
    }

    /**
     * Returns first date of current week, but not lower than current month's first date
     *
     * @return first date of current week in current month
     */
    public static Date getFirstDayOfCurrentWeek() {
        Calendar c = Calendar.getInstance();
        int today = c.get(Calendar.DAY_OF_MONTH);

        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        if (c.get(Calendar.DAY_OF_MONTH) > today) {
            return getFirstDayOfCurrentMonth();
        } else {
            return c.getTime();
        }
    }

    /**
     * Returns last date of current week, but not greater than current month's last date
     *
     * @return last date of current week in current month
     */
    public static Date getLastDayOfCurrentWeek() {
        Calendar c = Calendar.getInstance();
        int today = c.get(Calendar.DAY_OF_MONTH);

        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

        if (c.get(Calendar.DAY_OF_MONTH) < today) {
            return getLastDayOfCurrentMonth();
        } else {
            return c.getTime();
        }
    }

    /**
     * @return first date of current month
     */
    public static Date getFirstDayOfCurrentMonth() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.setFirstDayOfWeek(Calendar.MONDAY);
        return c.getTime();
    }

    /**
     * @return last date of current month
     */
    public static Date getLastDayOfCurrentMonth() {
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        return c.getTime();
    }

    /**
     * Sets zero hour to a date. It is useful when you need to set the start of a date range.
     *
     * @param aDate
     * @return aDate with time 00:00:00-000
     */
    public static Date setZeroHour(Date aDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(aDate);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * Sets zero hour to a date. It is useful when you need to set the start of a date range.
     *
     * @param aDate
     * @return aDate with time 00:00:00-000
     */
    public static Date setZeroMinute(Date aDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(aDate);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * Sets 24 hour to a date. It is useful when you need to set the end of a date range.
     *
     * @param aDate
     * @return aDate with time 23:59:59-999
     */
    public static Date set24Hour(Date aDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(aDate);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c.getTime();
    }

    /**
     * Sets 24 hour to a date. It is useful when you need to set the end of a date range.
     *
     * @param aDate
     * @return aDate with time 23:59:59-999
     */
    public static Date set59Minnute(Date aDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(aDate);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c.getTime();
    }

    /**
     * @param date requested date, 0 based
     * @return first date of given month
     */
    public static Date getFirstDayOfAMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, 1);
        return c.getTime();
    }

    /**
     * @param year  running year of requested month
     * @param month requested month, 0 based
     * @return first date of given month
     */
    public static Date getFirstDayOfAMonth(int year, int month) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.setFirstDayOfWeek(Calendar.MONDAY);
        return c.getTime();
    }

    /**
     * @return first date of given month
     */
    public static Date getFirstDayOfMonth() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.setFirstDayOfWeek(Calendar.MONDAY);
        return c.getTime();
    }

    /**
     * @param year  running year of requested month
     * @param month requested month, 0 based
     * @return last date of given month
     */
    public static Date getLastDayOfAMonth(int year, int month) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        return c.getTime();
    }

    /**
     * @param days
     * @return date incremented
     */
    public static Date setDaysToCurrentDate(int days) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, days);
        return c.getTime();
    }

    /**
     * @param date
     * @return date incremented in base to date
     */
    public static Date setDaysToDate(Date date, int days) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();
        c.setTime(date); // use today date.
        c.add(Calendar.DATE, days); // Adding days
        return c.getTime();
    }


    /**
     * @param date date with we will work
     * @param days number of days that we want to add
     * @return date added days
     */
    public static Date getDateAddDays(Date date, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, days);
        date = c.getTime();
        return date;
    }

    /**
     * @param strDate date with we will work 'dd/MM/yyyy'
     * @return date
     */
    public static Date getDateFromString(String strDate) {
        Date date = new Date();
        try {
            date = SDF.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * @param strDate date with we will work 'dd-MM-yyyy'
     * @return date
     */
    public static Date getDateFromSimpleString(String strDate) {
        Date date = null;
        try {
            date = SDF_DESC.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * @param strDate date with we will work 'yyyy-MM-dd'
     * @return date
     */
    public static Date getDateFromAscendantString(String strDate) {
        Date date = null;
        try {
            date = SDF_ASCE.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * @param date date with we will work 'dd/MM/yyyy'
     * @return date
     */
    public static Date getDateFormatted(Date date) {
        String strDate = getStringDateFromDate(date);
        try {
            date = SDF.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * @param date date with we will work 'dd/MM/yyyy'
     * @return strDate
     */
    public static String getStringDateFromDate(Date date) {
        String strDate = SDF.format(date);
        return strDate;
    }

    /**
     * @param strDate date with we will work 'HH:MM'
     * @return date
     */
    public static Date getTimeFromString(String strDate) {
        Date date = null;
        try {
            date = SDF_TIME_S.parse(strDate);
        } catch (ParseException e) {
            return date;
        }

        return date;
    }

    /**
     * @param date date with we will work 'HH:MM'
     * @return strDate
     */
    public static String getStringTimeFromDate(Date date) {
        String strDate = "";
        strDate = SDF_TIME_S.format(date);
        return strDate;
    }

    /**
     * @param date1 date with we will compare without seconds
     * @param date2 date with we will compare without seconds
     * @return boolean
     */
    public static boolean isSameDateTime(Date date1, Date date2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(date1);
        c2.setTime(date2);
//
        if (c1.get(Calendar.YEAR) != c2.get(Calendar.YEAR))
            return false;
        if (c1.get(Calendar.MONTH) != c2.get(Calendar.MONTH))
            return false;
        if (c1.get(Calendar.DATE) != c2.get(Calendar.DATE))
            return false;
        if (c1.get(Calendar.HOUR) != c2.get(Calendar.HOUR))
            return false;
        if (c1.get(Calendar.MINUTE) != c2.get(Calendar.MINUTE))
            return false;
        return true;
    }

    /**
     * @param date date with we will compare
     * @return boolean
     */
    public static boolean isDateAfterNow(Date date) {
        return new Date().after(date);
    }

    /**
     * @param date date with we will compare
     * @return boolean
     */
    public static boolean isDateBeforeNow(Date date) {
        return new Date().before(date);
    }

    /**
     * @param date time with we will compare
     * @return boolean
     */
    public static boolean isTimeAfterNow(Date date) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(date);
        int time1 = c1.get(Calendar.HOUR_OF_DAY) * 3600 + c1.get(Calendar.MINUTE) * 60 + c1.get(Calendar.SECOND);
        int time2 = c2.get(Calendar.HOUR_OF_DAY) * 3600 + c2.get(Calendar.MINUTE) * 60 + c2.get(Calendar.SECOND);

        return (time1 <= time2);
    }

    /**
     * @param date time with we will compare
     * @return boolean
     */
    public static boolean isTimeBeforeNow(Date date) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(date);
        int time1 = c1.get(Calendar.HOUR_OF_DAY) * 3600 + c1.get(Calendar.MINUTE) * 60 + c1.get(Calendar.SECOND);
        int time2 = c2.get(Calendar.HOUR_OF_DAY) * 3600 + c2.get(Calendar.MINUTE) * 60 + c2.get(Calendar.SECOND);

        return (time1 >= time2);
    }

    /**
     * returns date string in format "dd-MM-yyyy"
     *
     * @param date
     * @return
     */
    public static String getSimpleDate(Date date) {
        String strDate = "";
        strDate = SDF.format(date);

        return strDate;
    }

    /**
     * returns date string in format "yyyy-MM-dd"
     *
     * @param date
     * @return
     */
    public static String getSimpleAscDate(Date date) {
        String strDate = "";
        strDate = SDF_ASCE.format(date);

        return strDate;
    }

    /**
     * Get simple date time format YYYY/mm/dd HH:mm
     *
     * @param date
     * @return
     */
    public static String getSimpleDateTime(Date date) {
        String strTime = "";
        strTime = SDF_DETAIL_ST.format(date);

        return strTime;
    }

    public static String getSimpleTime(Date date) {
        String strTime = "";
        strTime = SDF_TIME_C.format(date);

        return strTime;
    }

    public static boolean isValidDate(String inDate) {
        SDF_DESC.setLenient(false);
        try {
            SDF_DESC.parse(inDate);
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }
}
