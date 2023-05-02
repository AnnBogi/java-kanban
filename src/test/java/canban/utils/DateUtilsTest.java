package canban.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DateUtilsTest {

    @Test
    void dateFromStringTest() throws ParseException {
        // Arrange.
        var dateString = "2023-04-14 07:10:00.001";
        var expectedDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").parse(dateString);

        // Act.
        var result = DateUtils.dateFromString(dateString);

        // Asserts.
        Assertions.assertEquals(expectedDate, result);
    }

    @Test
    void dateFromStringDoThrowTest() {
        // Asserts.
        Assertions.assertNull(DateUtils.dateFromString("11-12-2022"));
    }

    @Test
    void dateToStringTest() throws ParseException {
        // Arrange.
        var expectedDateString = "2023-04-14 07:10:00.001";
        var date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").parse(expectedDateString);

        // Act.
        var result = DateUtils.dateToString(date);

        // Asserts.
        Assertions.assertEquals(expectedDateString, result);
    }

}