package com.eastioquick.helper;

import android.app.AlertDialog;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

//import android.support.v7.app.AlertDialog;

public class MessageHelper {
    Context context;
    public interface SingleChoice{
        public void receive(int index);
    }
    public interface MultiChoice{
        public void receive(List choice);
    }
    public  MessageHelper(Context context){
        this.context=context;
    }
    public AlertDialog dialog(String title, String messsage, LinkedHashMap clicks){
        return dialog( title, new String[]{messsage}, clicks,null,null,null);
    }

   /**
        listview one choice. for no default
     */
    public AlertDialog dialog(String title, String[] messsages,DialogInterface.OnClickListener singleClickListener){
        return dialog( title,  messsages, null,singleClickListener,null,null);
    }
    /**list radio choice. for has default */
    public AlertDialog dialog(String title, String[] messsages
            , LinkedHashMap clicks,DialogInterface.OnClickListener singleClickListener,int def){
        return dialog( title,  messsages, clicks,singleClickListener,null,new Integer(def));
    }
    /**list checkbox choice */
    public AlertDialog dialog(String title, String[] messsages
            , LinkedHashMap clicks,DialogInterface.OnMultiChoiceClickListener multiClickListener,int []def){
        boolean[]choices=getChoices(messsages,def);
        return dialog( title,  messsages, clicks,null,multiClickListener,choices);
    }
    /**list checkbox choice with callback*/
    public AlertDialog dialog(String title, String[] messsages
            , String buttonTextYES,String buttonTextNO, final MultiChoice callback,int []def){
        LinkedHashMap clicks=new LinkedHashMap();
        final List checked=new ArrayList();
        if(def!=null){
            for(int i=0;i<def.length;i++){
                checked.add(String.valueOf(def[i]));
            }
        }
        clicks.put(buttonTextYES,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Collections.sort(checked);
                callback.receive( checked);
             }
        });
        clicks.put(buttonTextNO,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        DialogInterface.OnMultiChoiceClickListener multiClickListener=new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if(isChecked){checked.add(String.valueOf(which));}else{checked.remove(String.valueOf(which));}
            }
        };
        boolean[]choices=getChoices(messsages,def);
        return dialog( title,  messsages, clicks,null,multiClickListener,choices);
    }
    private boolean[] getChoices(String []messages,int []def){
        boolean choices[]=null;
        if(def==null)return null;
        choices=new boolean[messages.length];
        for(int i=0;i<choices.length;i++){
            choices[i]=false;
            for(int d:def){
                if(d==i){
                    choices[i]=true;
                    break;
                }
            }
        }
        return choices;
    }
    /**list single Choice  with callback*/
    public AlertDialog dialog(String title, String[] messsages, String buttonTextYES,String buttonTextNO
            ,final SingleChoice callback,int def){
        final int []checked=new int[]{-1};
        LinkedHashMap clicks=new LinkedHashMap();
        clicks.put(buttonTextYES,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.receive(checked[0]);
            }
        });
        clicks.put(buttonTextNO,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        DialogInterface.OnClickListener singleClickListener=new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checked[0]=which;
            }
        };
        return dialog( title,  messsages, clicks,singleClickListener,null,new Integer(def));
    }
    private AlertDialog dialog(String title, String[] messages, LinkedHashMap clicks
            ,DialogInterface.OnClickListener singleClickListener
            ,DialogInterface.OnMultiChoiceClickListener multiClickListener
            ,Object choice){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        if(messages.length==1){
            builder.setMessage(messages[0]);
        }else{
            if(clicks==null){
                builder.setItems(messages, singleClickListener);
            }else{
                if(singleClickListener==null){
                    boolean []cho=choice==null?null:(boolean[])choice;
                    builder.setMultiChoiceItems(messages,cho,multiClickListener);
                }else{
                    int cho=choice==null?0:((Integer)choice).intValue();
                    builder.setSingleChoiceItems(messages,cho,singleClickListener);
                }
            }
        }
        if(clicks!=null){
            int i=0;
            for(Object k :clicks.keySet()){
                String buttonText=(String)k;
                DialogInterface.OnClickListener listener=(DialogInterface.OnClickListener )clicks.get(k);
                if(i==0){
                    builder.setPositiveButton(buttonText,listener);
                }
                if(i==1){
                    builder.setNegativeButton(buttonText,listener);
                }
                if(i==2){
                    builder.setNeutralButton(buttonText,listener);
                }
                i++;
            }

        }
        AlertDialog dialog = builder.create();
        return dialog;

    }
    public Toast toast(String msg){return
        Toast.makeText(context,msg,Toast.LENGTH_LONG);
    }
    public Notification.Builder notify(String title,String text,long when){
        Notification.Builder builder =new  Notification.Builder(context);
        builder.setContentTitle(title)
                .setContentText(text)
                .setWhen(when);
        int defaults=0;

        // 設定震動效果
        defaults |= Notification.DEFAULT_VIBRATE;
        /*
        long[] vibrate_effect =
                {1000, 500, 1000, 400, 1000, 300, 1000, 200, 1000, 100};
        builder.setVibrate(vibrate_effect);
        */
        // 加入音效效果
        defaults |= Notification.DEFAULT_SOUND;
        /*
        Uri sound_effect = Uri.parse("android.resource://" + getPackageName() + "/raw/zeta");
        builder.setSound(sound_effect);
        */
        // 加入閃燈效果
        defaults |= Notification.DEFAULT_LIGHTS;
        //builder.setLights(Color.GREEN, 1000, 1000);
// 設定通知效果
        builder.setDefaults(defaults);

        return builder;
    }
}
