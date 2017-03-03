package com.eastioquick.helper;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

public class PermissionHelper {
    Context context;
    public static int REQUEST_PERMISSION=1234;
    public PermissionHelper(Context context){
        this.context=context;
    }
    public boolean hasAllPermission() {
        List l = new ArrayList();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
                if (info.requestedPermissions != null) {
                    for (String p : info.requestedPermissions) {
                        int hasPermission = context.checkSelfPermission(p);
                        if (hasPermission == PackageManager.PERMISSION_GRANTED) {
                            continue;
                        }
                        l.add(p);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (l.size() > 0) {
                String[]permissions = new String[l.size()];
                for (int i = 0; i < l.size(); i++) {
                    permissions[i] = (String) l.get(i);
                }
                ((Activity) context).requestPermissions(permissions, REQUEST_PERMISSION);
                return false;
            }
        }
        return true;
    }
}
