package canban.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateUtilsTest {

    @Test
    void dateFromStringTest() throws ParseException {
        var dateString = "2023-04-14 07:10:00.001";
        var expectedDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").parse(dateString);

        var result = DateUtils.dateFromString(dateString);

        Assertions.assertEquals(expectedDate, result);
    }

    @Test
    void dateFromStringDoThrowTest() {
        Assertions.assertNull(DateUtils.dateFromString("11-12-2022"));
    }

    @Test
    void dateToStringTest() throws ParseException {
        var expectedDateString = "2023-04-14 07:10:00.001";
        var date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").parse(expectedDateString);

        var result = DateUtils.dateToString(date);

        Assertions.assertEquals(expectedDateString, result);
    }

}
