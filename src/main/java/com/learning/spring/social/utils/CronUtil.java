package com.learning.spring.social.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.stereotype.Service;

@Service
public class CronUtil {

    public String dateToCronExpression(String dateTimeStr) throws ParseException {
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        Date dateTime = inputDateFormat.parse(dateTimeStr);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTime);

        int seconds = calendar.get(Calendar.SECOND);
        int minutes = calendar.get(Calendar.MINUTE);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1; // Months in Cron start from 1
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        // Convert day of week (1-7) to Cron format (0-6 with 0 and 7 both representing Sunday)
        if (dayOfWeek == Calendar.SUNDAY) {
            dayOfWeek = 0;
        } else {
            dayOfWeek -= 1;
        }

        String cronExpression = String.format("%d %d %d %d %d ?", seconds, minutes, hours, dayOfMonth, month);

        return cronExpression;
    }
}