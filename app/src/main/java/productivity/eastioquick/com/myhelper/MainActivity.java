package productivity.eastioquick.com.myhelper;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.eastioquick.helper.PermissionHelper;

public class MainActivity extends Activity {
    PermissionHelper permissionHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permissionHelper=new PermissionHelper(this);

    }

    @Override
    protected void onResume() {
        permissionHelper.hasAllPermission();
        super.onResume();
    }

    /*
        public void onRequestPermissionsResult(int requestCode,
                                               String[] permissions,
                                               int[] grantResults) {
            if (requestCode == permissionHelper.REQUEST_PERMISSION) {
                if(!permissionHelper.hasAllPermission()){return;}
                //....所有權限都取得了
            }
        }
        */
    public void doStartActivity(View v){
        Button b=(Button)v;

        String className=getPackageName()+"."+ b.getText()+"Activity";
        Log.d(getClass().getSimpleName(),className);
        Class c=null;
        try{
          c=Class.forName(className);
        }catch (Exception e){e.printStackTrace();}
        if(c==null){;
            Log.d(getClass().getSimpleName(),className+"==null");
            return;
        }
        //
        Intent intent=new Intent(this,c);
        startActivityForVersion(intent);
    }
    private void startActivityForVersion(Intent intent) {
        // 如果裝置的版本是LOLLIPOP
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 加入畫面轉換設定
            startActivity(intent,
                    ActivityOptions.makeSceneTransitionAnimation(
                            MainActivity.this).toBundle());
        }
        else {
            startActivity(intent);
        }
    }
}
