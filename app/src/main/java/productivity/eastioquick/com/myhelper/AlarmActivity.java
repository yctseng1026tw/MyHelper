package productivity.eastioquick.com.myhelper;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.eastioquick.helper.AlarmHelper;
import com.eastioquick.helper.DBSQLiteHelper;
import com.eastioquick.helper.MessageHelper;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import static com.eastioquick.util.ArrayUtil.str2ArrInt;
import static productivity.eastioquick.com.myhelper.R.id.TIMER;
import static productivity.eastioquick.com.myhelper.R.id.WEEK_PERIOD;


public class AlarmActivity extends Activity {
    MessageHelper messageHelper;
    DBSQLiteHelper dbHelper;
    AlarmHelper alarmHelper;
    Map mapAlarm=new Hashtable();
    EditText etDESCRIPTION;
    TextView tvWEEK,tvTIMER,tvWEEK_PERIOD;
    Switch swHAS_WEATHER,swHAS_NEWS,swHAS_CALENDAR;
    String []weekDesc;
    int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        messageHelper=new MessageHelper(this);
        alarmHelper=new AlarmHelper(this);
        DBSQLiteHelper.initialize(this);
        dbHelper=DBSQLiteHelper.build(this,"MyHelper",3);

        weekDesc=getResources().getStringArray(R.array.week);
        initView();
    }
    private void initView(){
        etDESCRIPTION=((EditText)findViewById(R.id.DESCRIPTION));
        swHAS_WEATHER=((Switch) findViewById(R.id.HAS_WEATHER));
        swHAS_NEWS=((Switch) findViewById(R.id.HAS_NEWS));
        swHAS_CALENDAR=((Switch) findViewById(R.id.HAS_CALENDAR));
        tvWEEK=(TextView)findViewById(R.id.week);
        tvWEEK_PERIOD=(TextView)findViewById(WEEK_PERIOD);
        tvTIMER=(TextView)findViewById(TIMER);
        id=getIntent().getIntExtra(DBSQLiteHelper.KEY_ID,1);
        //mapAlarm=dbHelper.findBy(TABLE_NAME,id);
        mapAlarm=dbHelper.findBy(this,id);
        dbHelper.populate(mapAlarm,this);

        if(!"".equals(tvWEEK_PERIOD.getText())){
            String WEEKs[]= tvWEEK_PERIOD.getText().toString().split(",");
            StringBuilder sb=new StringBuilder();
            for(Object o:WEEKs){
                sb.append(","+weekDesc[Integer.parseInt((String)o)]);
            }
            tvWEEK.setText(sb.substring(1));
        }
        if(dbHelper.getViewValById(mapAlarm,R.id.TIMER)==null){
            Calendar c=Calendar.getInstance();
            String def=c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE);
            tvTIMER.setText(def);
        }
    }

    public void doWeekDialog(View v){
        String title=((TextView)findViewById(R.id.weekLabel)).getText().toString();
        String buttonYes=getResources().getString(R.string.accept);
        String buttonNo=getResources().getString(R.string.decline);
        int []def=str2ArrInt((String)mapAlarm.get("WEEK_PERIOD"),",");
        messageHelper.dialog(title, weekDesc, buttonYes, buttonNo, new MessageHelper.MultiChoice() {
            @Override
            public void receive(List choice) {
                StringBuilder sb=new StringBuilder();
                StringBuilder sbCode=new StringBuilder();
                for(Object o:choice){
                    sb.append(","+weekDesc[Integer.parseInt((String)o)]);
                    sbCode.append(","+o);
                }
                String weekset=sbCode.substring(1).toString();
                //mapAlarm.put("WEEK_PERIOD",weekset);
                tvWEEK_PERIOD.setText(weekset);

                String week=sb.substring(1).toString();
                tvWEEK.setText(week);

            }
        },def).show();
    }
    public void doHourDialog(View v){
        TimePickerDialog.OnTimeSetListener timeSetListener =
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view,
                                          int hourOfDay, int minute) {
                        tvTIMER.setText(hourOfDay+":"+minute);
                    }
                };
        String timer=tvTIMER.getText().toString();
        String []timers=timer.split(":");
        int hour=Integer.parseInt(timers[0]);
        int minute=Integer.parseInt(timers[1]);
        final TimePickerDialog tpd = new TimePickerDialog(
                this, timeSetListener, hour, minute, true);
        tpd.show();
    }
    public void doInsertAlarm(View v){
        mapAlarm=dbHelper.insert(this);
        messageHelper.toast("doInsertAlarm OK").show();
    }
    public void doUpdateAlarm(View v){
        dbHelper.update( this, id);
        messageHelper.toast("doUpdateAlarm OK").show();


    }


    public void doStartAlarm(View v){
        String week=tvWEEK_PERIOD.getText().toString();
        String hm=tvTIMER.getText().toString();
        alarmHelper.repeatWeek(week,hm,AlarmReceiver.ACTION_ALARM);
        messageHelper.toast("doStartAlarm OK").show();
    }
    public void doCancelAlarm(View v){
        String week=dbHelper.getViewValById(mapAlarm, R.id.WEEK_PERIOD);
        alarmHelper.cancelWeek( week,AlarmReceiver.ACTION_ALARM);
        messageHelper.toast("doCancelAlarm OK").show();
    }

}
