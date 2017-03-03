package com.eastioquick.helper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;

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
    public void repeatWeek(String WEEK,String hm,PendingIntent pi){
        int []def=ArrayUtil.str2ArrInt(WEEK,",");
        int []ihm=ArrayUtil.str2ArrInt(hm,":");
        int hour=ihm[0];
        int minutes=ihm[1];
        for(int i=0;i<def.length;i++){
            Calendar cal= Calendar.getInstance();
            cal.set(Calendar.DAY_OF_WEEK,def[i]+1);
            cal.set(Calendar.HOUR_OF_DAY, hour);
            cal.set(Calendar.MINUTE, minutes);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            if(Calendar.getInstance().compareTo(cal)>0){
                cal.add(Calendar.DAY_OF_YEAR,7);
            }
            long firstAlarm=cal.getTimeInMillis();
            am.setRepeating(AlarmManager.RTC_WAKEUP, firstAlarm, 7* 24 * 60 * 60 * 1000 , pi);
        }
    }
}
