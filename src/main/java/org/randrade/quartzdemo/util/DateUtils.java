package org.randrade.quartzdemo.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {


    public static Date parseDate(String sDate1) throws ParseException {

        return new SimpleDateFormat("yyyy-MM-dd").parse(sDate1);
    }

    public static java.sql.Date convert(Date date) throws ParseException {

        return new java.sql.Date(date.getTime());
    }


}
