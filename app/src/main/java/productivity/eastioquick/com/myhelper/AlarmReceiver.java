package productivity.eastioquick.com.myhelper;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.eastioquick.helper.MessageHelper;
import com.eastioquick.helper.VoiceHelper;
import com.eastioquick.helper.WebContentHelper;
import com.eastioquick.service.TTSService;

import java.io.InputStream;
import java.util.List;
import java.util.Locale;

public class AlarmReceiver extends BroadcastReceiver {
    VoiceHelper voiceHelper;
    WebContentHelper webContentHelper;
    MessageHelper messageHelper;
    public static String ACTION_ALARM="productivity.eastioquick.com.myhelper.ACTION_ALARM";
    public AlarmReceiver() {
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        messageHelper=new MessageHelper(context);

/*
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Ringtone ringtone = RingtoneManager.getRingtone(context, uri);
        ringtone.play();
*/

/*
    try{
        voiceHelper =new VoiceHelper(context);
    } catch (Exception e) {
        e.printStackTrace();
    }
    */
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

                    Notification.Builder builder= messageHelper.notify("title",ss,System.currentTimeMillis(),R.drawable.ic_cast_dark);
                    messageHelper.showNotify(1, builder);

                    //voiceHelper.speak(ss);
                    Intent intent=new Intent(context, TTSService.class);
                    intent.putExtra(TTSService.SPOKEN_TEXT,ss);
                    intent.putExtra(TTSService.LANGUAGE, Locale.CHINESE);
                    context.startService(intent);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        WebContentHelper.HttpRequest task = new WebContentHelper.HttpRequest(cb);
        task.execute(url, para, "GET");


    }
}
