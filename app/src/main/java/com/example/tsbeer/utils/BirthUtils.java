package com.example.tsbeer.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class BirthUtils {

    /**
     *  根据身份证号判断当前年龄
     * @param cardNo
     * @return
     */
    public static int getAge(String cardNo) {
        String birthday = cardNo.substring(6, 14);
        Date birthdate = null;
        try {
            birthdate = new SimpleDateFormat("yyyyMMdd").parse(birthday);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        GregorianCalendar currentDay = new GregorianCalendar();
        currentDay.setTime(birthdate);
        int birYear = currentDay.get(Calendar.YEAR);

        // 获取年龄
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
        String thisYear = simpleDateFormat.format(new Date());
        int age = Integer.parseInt(thisYear) - birYear;

        return age;
    }

    public static void main(String[] args) {
        System.out.println(getAge("530121198903119561"));
    }
}