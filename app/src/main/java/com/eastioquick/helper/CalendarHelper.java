package com.eastioquick.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class CalendarHelper {
    private static String calanderURL = "";
    private static String calanderEventURL = "";
    private static String calanderRemiderURL = "";
    //为了兼容不同版本的日历,2.2以后url发生改变
    static{
        if(Build.VERSION.SDK_INT >= 8){
            calanderURL = "content://com.android.calendar/calendars";
            calanderEventURL = "content://com.android.calendar/events";
            calanderRemiderURL = "content://com.android.calendar/reminders";
        }else{
            calanderURL = "content://calendar/calendars";
            calanderEventURL = "content://calendar/events";
            calanderRemiderURL = "content://calendar/reminders";
        }
    }
    Context context;

    public CalendarHelper(Context context){
        this.context=context;
    }
    public String readAccount(){
        String rtn=null;
        Cursor cursor = context.getContentResolver()
                .query(Uri.parse(calanderURL), null,
                null, null, null);
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            rtn = cursor.getString(cursor.getColumnIndex("name"));
        }
        return rtn;
    }
    public List readEvent(Calendar startS, Calendar startE, String[]columns){
        //String where=null;//dtstart dtend
        String where=CalendarContract.Events.DTSTART +" between ? and ?";
        String []whereVal=new String[]{
                String.valueOf(startS.getTime().getTime())
                ,String.valueOf(startE.getTime().getTime())};
        String sort=null;
        Cursor cursor = context.getContentResolver().query(Uri.parse(calanderEventURL), columns,
                where, whereVal, sort);
        List rtn=DBSQLiteHelper.populate(cursor);
        return rtn;
    }
    public void writeEvent(String title,String description,Calendar start,Calendar end){
        String calId = "";
        Cursor cursor = context.getContentResolver().query(Uri.parse(calanderURL), null,
                null, null, null);
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            calId = cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars._ID));

        }
        ContentValues event = new ContentValues();
        event.put(CalendarContract.Events.TITLE, title);
        event.put(CalendarContract.Events.DESCRIPTION, description);
        event.put(CalendarContract.Events.CALENDAR_ID,calId);
        long lstart = start.getTime().getTime();
        long lend = end.getTime().getTime();
        event.put(CalendarContract.Events.DTSTART, lstart);
        event.put(CalendarContract.Events.DTEND, lend);
        event.put(CalendarContract.Events.HAS_ALARM,1);
        event.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());

        Uri newEvent = context.getContentResolver().insert(Uri.parse(calanderEventURL), event);

        long id = Long.parseLong( newEvent.getLastPathSegment() );
        ContentValues reminder = new ContentValues();
        reminder.put( "event_id", id );
        //10分鐘前提醒
        reminder.put( "minutes", 10 );
        context.getContentResolver().insert(Uri.parse(calanderRemiderURL), reminder);
    }
}
