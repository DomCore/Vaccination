package com.api.Poletechnika.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class DateUseUtil {

    public String getCurrentDate(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.now();
        return dtf.format(localDate);
    }


    //FOR CALCULATE DATE, LIKE LICENSE TERM
    public String changeCurrentDate(String startDate, int days){
        String dt;  // Start date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        if(startDate == null){
            c.getTime();
        }else {
            try {
                c.setTime(sdf.parse(startDate));
            } catch (ParseException e) {
                c.getTime();
            }
        }
        c.add(Calendar.DATE, days);  // number of days to add
        dt = sdf.format(c.getTime());
        return dt;
    }
}
