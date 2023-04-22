package canban.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateUtils {

    private static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss.SSS";

    private DateUtils() {}

    public static Date dateFromString(String stringDate) {
        try {
            return new SimpleDateFormat(DATE_FORMAT).parse(stringDate);
        } catch (Exception e) {
            System.out.println("Формат даты в реестре некорректный: " + e.getMessage());
        }
        return null;
    }

    public static String dateToString(Date date) {
        return new SimpleDateFormat(DATE_FORMAT).format(date);
    }

}
