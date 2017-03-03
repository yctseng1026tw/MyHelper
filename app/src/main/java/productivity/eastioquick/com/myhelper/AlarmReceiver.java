package productivity.eastioquick.com.myhelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.eastioquick.helper.VoiceHelper;
import com.eastioquick.helper.WebContentHelper;

import java.io.InputStream;
import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {
    VoiceHelper voiceHelper;
    WebContentHelper webContentHelper;
    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        voiceHelper =new VoiceHelper(context);
        webContentHelper =new WebContentHelper(context);
        String url = "http://opendata.cwb.gov.tw/govdownload";
        String para = "dataid=F-C0032-021&authorizationkey=rdec-key-123-45678-011121314";
        WebContentHelper.HttpRequestCallBack cb = new WebContentHelper.HttpRequestCallBack() {
            public void onNetwork(InputStream is) {
                try {

                    String stag = "parameterValue";
                    List result = webContentHelper.findByXMLPullParseXML(is, stag);

                    StringBuffer sb = new StringBuffer();
                    for (Object o : result) {
                        sb.append(o);
                    }

                    final String ss = sb.toString();
                    voiceHelper.speak(ss);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        WebContentHelper.HttpRequest task = new WebContentHelper.HttpRequest(cb);
        task.execute(url, para, "GET");

    }
}
