package com.example.mycalendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DateManager {
    Calendar mCalendar;

    // 翌日かなどに使用
    Date nowDate;
    Calendar cal;
    SimpleDateFormat format;
    String tomorrow;


    public DateManager(){
        mCalendar = Calendar.getInstance();
    }

    //当月の要素を取得
    public List<Date> getDays(){
        //現在の状態を保持
        Date startDate = mCalendar.getTime();

        //GridViewに表示するマスの合計を計算
        int count = getWeeks() * 7 ;

        //当月のカレンダーに表示される前月分の日数を計算
        mCalendar.set(Calendar.DATE, 1);
        int dayOfWeek = mCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        mCalendar.add(Calendar.DATE, -dayOfWeek);

        List<Date> days = new ArrayList<>();

        for (int i = 0; i < count; i ++){
            days.add(mCalendar.getTime());
            mCalendar.add(Calendar.DATE, 1);
        }

        //状態を復元
        mCalendar.setTime(startDate);

        return days;
    }

    //当月かどうか確認
    public boolean isCurrentMonth(Date date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM", Locale.US);
        String currentMonth = format.format(mCalendar.getTime());
        if (currentMonth.equals(format.format(date))){
            return true;
        }else {
            return false;
        }
    }

    //週数を取得
    public int getWeeks(){
        return mCalendar.getActualMaximum(Calendar.WEEK_OF_MONTH);
    }

    //曜日を取得
    public int getDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    //翌月へ
    public void nextMonth(){
        mCalendar.add(Calendar.MONTH, 1);
    }

    //前月へ
    public void prevMonth(){
        mCalendar.add(Calendar.MONTH, -1);
    }

    public boolean isToday(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd", Locale.US);
        String today = format.format(Calendar.getInstance().getTime());
        return today.equals(format.format(date));
    }


    //翌日か
    public boolean isTomorrow(Date date) {
        nowDate = new Date();
        cal = Calendar.getInstance();

        // 翌日
        cal.setTime(nowDate);
        cal.add(Calendar.DAY_OF_MONTH, 1);

        format = new SimpleDateFormat("yyyy.MM.dd", Locale.US);
        tomorrow = format.format(cal.getTime());
        return tomorrow.equals(format.format(date));
    }
    //前日か
    public boolean isYesterday(Date date) {
        nowDate = new Date();
        cal = Calendar.getInstance();

        // 翌日
        cal.setTime(nowDate);
        cal.add(Calendar.DAY_OF_MONTH, -1);

        format = new SimpleDateFormat("yyyy.MM.dd", Locale.US);
        tomorrow = format.format(cal.getTime());
        return tomorrow.equals(format.format(date));
    }

//    public boolean isTwoDaysLater(Date date) {
//        Date nowDate = new Date();
//        Calendar cal = Calendar.getInstance();
//
//        // 翌日
//        cal.setTime(nowDate);
//        cal.add(Calendar.DAY_OF_MONTH, 2);
//
//        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd", Locale.US);
//        String tomorrow = format.format(cal.getTime());
//        return tomorrow.equals(format.format(date));
//
//
//    }
}
