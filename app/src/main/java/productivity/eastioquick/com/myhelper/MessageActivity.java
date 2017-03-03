package productivity.eastioquick.com.myhelper;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import com.eastioquick.helper.MessageHelper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class MessageActivity extends Activity {
    MessageHelper helper;
    RadioGroup radioGroupDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        helper=new MessageHelper(this);
        radioGroupDialog=(RadioGroup)findViewById(R.id.radioGroupDialog);
    }
    public void doDialog(View v){
        int radioButtonID = radioGroupDialog.getCheckedRadioButtonId();
        switch(radioButtonID){
            case R.id.radioButton1Message:
                do1Message();
                break;
            case R.id.radioButton1Item:
                do1Item();
                break;
            case R.id.radioButton1Choice:
                do1Choice();
                break;
            case R.id.radioButtonMultiChoice:
                doMultiChoice();
                break;
            case R.id.radioButton1ChoiceSimple:
                this.do1ChoiceSimple();
                break;
            case R.id.radioButtonMultiChoiceSimple:
                this.doMultiChoiceSimple();
                break;
        }
        //Log.d(this.getClass().getSimpleName(),"radioButtonID:"+radioButtonID);
    }
    public void doToast(View v){
        helper.toast("this is a toast").show();
    }
    public void do1Message(){
        LinkedHashMap clicks=getClicks("do1Message");
        helper.dialog("dialog title","1 message",clicks).show();
    }
    private LinkedHashMap getClicks(final String from){
        LinkedHashMap clicks=new LinkedHashMap();
        clicks.put(getResources().getString(R.string.accept),getClick(from));
        clicks.put(getResources().getString(R.string.decline),getClick(from));
        return clicks;
    }
    private DialogInterface.OnClickListener getClick(final String from){
        return new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                helper.toast(from+" click "+getResources().getString(R.string.decline)+":"+which).show();
            }
        };
    }
    public void do1Item(){
        final String[]array=getResources().getStringArray(R.array.dialog);
        helper.dialog("dialog title",array,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                helper.toast("do1Item click "+array[which]+":"+which).show();
            }
        }).show();
    }
    public void do1Choice(){
        final String[]array=getResources().getStringArray(R.array.dialog);
        LinkedHashMap clicks=getClicks("do1Choice");
        helper.dialog("do1Choice title", array, clicks, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                helper.toast("do1Choice click "+array[which]+":"+which).show();
            }
        },array.length-1).show();
    }
    public void doMultiChoice(){
        final String[]array=getResources().getStringArray(R.array.dialog);
        final List checked=new ArrayList();
        final String from ="do1Choice";
        LinkedHashMap clicks=new LinkedHashMap();
        clicks.put(getResources().getString(R.string.accept),new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StringBuffer choice=new StringBuffer("[");
                for(Object o:checked){
                    choice.append(o+",");
                }
                choice.append("]");
                helper.toast(from+" click "+getResources().getString(R.string.accept)+":"+which+",you choice "+choice.toString()).show();
            }
        });
        clicks.put(getResources().getString(R.string.decline),getClick(from));

        helper.dialog("doMultiChoice title", array, clicks, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if(isChecked){checked.add(String.valueOf(which));}else{checked.remove(String.valueOf(which));}
                helper.toast("doMultiChoice click "+array[which]+":"+which+":"+isChecked).show();
            }
        },new int[]{0,2}).show();
    }
    public void doMultiChoiceSimple(){
        final String[]array=getResources().getStringArray(R.array.dialog);
        String sOK=getResources().getString(R.string.accept);
        String sNO=getResources().getString(R.string.decline);
        helper.dialog("doMultiChoiceSimple title", array,sOK,sNO,new MessageHelper.MultiChoice(){
            @Override
            public void receive(List choice) {
                StringBuffer stringBuffer=new StringBuffer("[");
                for(Object o:choice){
                    stringBuffer.append(o+",");
                }
                stringBuffer.append("]");
                helper.toast("doMultiChoiceSimple "+",you choice is"+choice.toString()).show();
            }
        },new int[]{0,2}).show();
    }
    public void do1ChoiceSimple(){
        final String[]array=getResources().getStringArray(R.array.dialog);
        String sOK=getResources().getString(R.string.accept);
        String sNO=getResources().getString(R.string.decline);
        helper.dialog("doMultiChoiceSimple title", array,sOK,sNO,new MessageHelper.SingleChoice(){
            @Override
            public void receive(int choice) {
                helper.toast("do1ChoiceSimple "+",you choice is "+choice).show();
            }
        },array.length-1).show();
    }

    public void doNotify(View v){

    }
}
