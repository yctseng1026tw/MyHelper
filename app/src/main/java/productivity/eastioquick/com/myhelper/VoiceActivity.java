package productivity.eastioquick.com.myhelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.eastioquick.helper.VoiceHelper;

import java.util.List;

public class VoiceActivity extends Activity {
    VoiceHelper helper;
    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);
        helper = new VoiceHelper(this);
        editText=(EditText)findViewById(R.id.speakSample);
    }

    public void doSpeak(View v) {
        helper.speak(editText.getText().toString());
    }

    @Override
    protected void onPause() {
        helper.stopSpeak();
        super.onPause();
    }

    public void doListen(View v) {
        helper.listen(VoiceHelper.VOICE_RECOGNITION_REQUEST_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VoiceHelper.VOICE_RECOGNITION_REQUEST_CODE) {
            List result = helper.listenResult(resultCode, data);

            StringBuffer sb=new StringBuffer();
            if(result!=null){
                for(int i=0;i<result.size();i++){
                    sb.append(result.get(i)+"\n");
                }
            }
            // 顯示結果
            Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
