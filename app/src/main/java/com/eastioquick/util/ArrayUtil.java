package com.eastioquick.util;

/**
 * Created by VGH00 on 2017/2/27.
 */

public class ArrayUtil {
    public static int[] str2ArrInt(String str,String split){
        if(str==null)return null;
        String arrStr[]=str.split(split);
        int arrInt[]=new int[arrStr.length];

        for(int i=0;i<arrInt.length;i++){
            arrInt[i]=Integer.parseInt(arrStr[i]);
        }
        return arrInt;
    }
    public static String arr2Str(Object []arr,String []desc){
        StringBuilder sb=new StringBuilder();
        for(Object o:arr){
            sb.append(","+desc[Integer.parseInt((String)o)]);
        }
        return sb.substring(1).toString();
    }
}
