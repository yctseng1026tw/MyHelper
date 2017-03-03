package productivity.eastioquick.com.myhelper;

import android.app.Activity;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.Toast;

import com.eastioquick.helper.CalendarHelper;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class CalendarActivity extends Activity {
    CalendarHelper helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        helper=new CalendarHelper(this);
    }
    public void doReadAccount(View v){
        String msg=helper.readAccount();
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }
    public void doReadEvent(View v){
        String []columns=new String[]{CalendarContract.Events.TITLE};
        Calendar start= Calendar.getInstance(),end=Calendar.getInstance();
        start.set(Calendar.HOUR_OF_DAY,0);
        start.set(Calendar.MINUTE,0);
        end.set(Calendar.HOUR_OF_DAY,23);
        start.set(Calendar.MINUTE,59);
        List list=helper.readEvent(start,end,columns);
        StringBuffer msg=new StringBuffer();
        for(Object l:list){
            Map m=(Map)l;
            msg.append(m.get(CalendarContract.Events.TITLE)+"\n");
        }
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }
    public void doWriteEvent(View v){
        String title="測試標題",description="測試內容";
        Calendar start= Calendar.getInstance(),end=Calendar.getInstance();
        start.set(Calendar.HOUR_OF_DAY,18);
        end.set(Calendar.HOUR_OF_DAY,19);
        helper.writeEvent(title,description,start,end);
    }
}
