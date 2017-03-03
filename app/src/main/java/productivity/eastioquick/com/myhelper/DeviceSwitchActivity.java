package productivity.eastioquick.com.myhelper;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

import com.eastioquick.helper.DeviceSwitchHelper;

public class DeviceSwitchActivity extends Activity {
    Switch switchWIFI,switchBlueTooth;
    DeviceSwitchHelper helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_switch);
        helper=new DeviceSwitchHelper(this);
        switchWIFI=(Switch)findViewById(R.id.switchWIFI);
        switchWIFI.setChecked(helper.isWIFIEnable());
        switchBlueTooth=(Switch)findViewById(R.id.switchBlueTooth);
        switchBlueTooth.setChecked(helper.isBlueToothEnable());
    }
    public void doWIFI(View v){
        helper.switchWIFI(switchWIFI.isChecked());
    }
    public void doBlueTooth(View v){
        helper.switchBlueTooth(switchBlueTooth.isChecked());
    }
}
