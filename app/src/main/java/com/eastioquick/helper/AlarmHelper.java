package com.eastioquick.helper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.eastioquick.util.ArrayUtil;

import java.util.Calendar;


public class AlarmHelper {
    Context context;
    AlarmManager am;
    public AlarmHelper(Context context){
        this.context=context;
        am=(AlarmManager)context.getSystemService(context.ALARM_SERVICE);
    }
    public void cancel(PendingIntent pi){
        am.cancel(pi);
    }
    public void cancelWeek(String WEEK,String action){
        int []weekDays=ArrayUtil.str2ArrInt(WEEK,",");
        for(int i=0;i<weekDays.length;i++){
            int weekDay=weekDays[i];
            PendingIntent pi=getPendingIntent(this.context, action, weekDay);
            pi.cancel();
            am.cancel(pi);
        }
    }
    public void repeatWeek(String WEEK,String hm,String action){
        int []weekDays=ArrayUtil.str2ArrInt(WEEK,",");
        int []ihm=ArrayUtil.str2ArrInt(hm,":");
        int hour=ihm[0];
        int minutes=ihm[1];
        for(int i=0;i<weekDays.length;i++){
            int weekDay=weekDays[i];
            Calendar cal= Calendar.getInstance();
            cal.set(Calendar.DAY_OF_WEEK,weekDay+1);
            cal.set(Calendar.HOUR_OF_DAY, hour);
            cal.set(Calendar.MINUTE, minutes);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            if(Calendar.getInstance().compareTo(cal)>0){
                cal.add(Calendar.DAY_OF_YEAR,7);
            }
            long firstAlarm=cal.getTimeInMillis();

            PendingIntent pi=getPendingIntent(this.context, action, weekDay);
            am.setRepeating(AlarmManager.RTC_WAKEUP, firstAlarm, 7* 24 * 60 * 60 * 1000 , pi);
        }
    }
    public PendingIntent getPendingIntent(Context context,String action,int id){
        //Intent alarmIntent = new Intent(context, receiverClass);
        String laction=action;
        //Intent alarmIntent = new Intent(laction, Uri.parse("timer:"+id));
        Intent alarmIntent = new Intent(laction);

        //alarmIntent.setAction(laction);

        //intent.putExtra("id", item.getId());
        PendingIntent pi = PendingIntent.getBroadcast(
                context, id,
         //       alarmIntent, PendingIntent.FLAG_ONE_SHOT);
        alarmIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        return pi;
    }
    public PendingIntent getPendingIntent(Context context,Class receiverClass,int id){
        Intent alarmIntent = new Intent(context, receiverClass);

        //intent.putExtra("id", item.getId());
        PendingIntent pi = PendingIntent.getBroadcast(
                context, id,
        //        alarmIntent, PendingIntent.FLAG_ONE_SHOT);
        alarmIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        return pi;
    }
}
