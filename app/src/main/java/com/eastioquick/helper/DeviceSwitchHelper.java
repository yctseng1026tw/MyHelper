package com.eastioquick.helper;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiManager;

public class DeviceSwitchHelper {
    Context context;
    public DeviceSwitchHelper(Context context){
        this.context=context;
    }
    public boolean isWIFIEnable(){
        WifiManager m=(WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        return m.isWifiEnabled();
    }

    public void switchWIFI(boolean enable){
        WifiManager m=(WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        m.setWifiEnabled(enable);
    }

    public boolean isBlueToothEnable(){
/*        BluetoothManager m=(BluetoothManager)context.getSystemService(Context.BLUETOOTH_SERVICE);
        return m.getAdapter().isEnabled();*/
        BluetoothAdapter a=BluetoothAdapter.getDefaultAdapter();
        return a.isEnabled();
    }
    public void switchBlueTooth(boolean enable){
        BluetoothAdapter a=BluetoothAdapter.getDefaultAdapter();
        if(enable){a.enable();}else{a.disable();}
    }

}
