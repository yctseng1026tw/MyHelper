package com.eastioquick.helper;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.R.id.list;

public class DBSQLiteHelper extends SQLiteOpenHelper {
    public static final String KEY_ID = "_id";
    public static final String COLUMN_RID_PREFIX = "_RID";
    private static SQLiteDatabase database;
    private static Map tableConstruct =new Hashtable();
    private static DBSQLiteHelper me;
    private static final List _TEXT_VIEW_CLASS=Arrays.asList(new Class[]{EditText.class,TextView.class});
    private static final List _CHECKED_VIEW_CLASS=Arrays.asList(Switch.class, CheckBox.class, RadioButton.class);
    private static final List _VIEW_CLASSES;
    static{
        _VIEW_CLASSES= new ArrayList();
        _VIEW_CLASSES.addAll(_TEXT_VIEW_CLASS);
        _VIEW_CLASSES.addAll(_CHECKED_VIEW_CLASS);
    }
    private DBSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    /**取得預設的View Classes*/
    public static List getViewClasses(){
        return new ArrayList(list);
    }
    public static void initialize(Context context) {
         initialize(context,_VIEW_CLASSES);
    }
    public static void initialize(Context context,List classes) {
        ViewGroup vg=(ViewGroup)((Activity)context).getWindow().getDecorView();
        List views=new ArrayList();
        genColumn(vg,classes,views);
        String createSQL=genCreateSQL(getTableName(context),views);
         initialize(createSQL);
    }
    public static void initialize(String createSQL) {
        String tableName= getTableName( createSQL);
        tableConstruct.put(tableName,createSQL);
    }
    public static DBSQLiteHelper build(Context context,String dbName,int version){
        if (me == null) {
            me = new DBSQLiteHelper(context, dbName,
                    null, version);
            DBSQLiteHelper.database =me.getWritableDatabase();
        }
        return me;
    }

    private static String getTableName(String createSQL){
        int eidx=createSQL.indexOf("(");
        String s=createSQL.substring(0,eidx).toUpperCase();
        int sidx=s.indexOf("TABLE");
        String tableName=s.substring(sidx).replaceFirst("TABLE","").trim();
        return tableName;
    }
    public static String getTableName(Context context){
        String tableName=context.getClass().getSimpleName();
        return tableName;
    }
    public Map insert(Context context){
        Map m=getViewValMap(context);
        String tableName=getTableName(context);
        return insert(tableName,m);
    }
    public Map insert(String table,Map m){
        //Set ks=m.keySet();
        ContentValues cv = map2ContentValues(m);
        long id = database.insert(table,null,cv);
        m.put(KEY_ID,id);
        return m;
    }
    public boolean update(Context context,int id){
        Map m=getViewValMap(context);
        String tableName=getTableName(context);
        return update(tableName,m,id);
    }
    public boolean update(String table,Map m,int id) {
        ContentValues cv = map2ContentValues(m);
        String where = KEY_ID + "=" + id;
        return database.update(table, cv, where, null) > 0;
    }
    public boolean delete(String table,long id){
        String where = KEY_ID + "=" + id;
        return database.delete(table, where , null) > 0;
    }
    /**將map 轉成 contentValues*/
    private ContentValues map2ContentValues(Map m){
        Set ks=m.keySet();
        ContentValues cv = new ContentValues();
        for(Object o:ks){
            String k=(String)o;
            cv.put(k,(String)m.get(k));
        }
        return cv;
    }
    /**透過sql 產生 list*/
    public List findBy(String sql, String[] selectionArgs){
        Cursor cursor = database.rawQuery(sql, selectionArgs);
        return populate(cursor);
    }
    /**透過 ID 產生 map*/
    public Map findBy(Context context,int id){
        return findBy(getTableName(context),id);
    }
    public Map findBy(String table,int id){
        Map rtn=new Hashtable();
        String where = KEY_ID + "=" + id;
        // 執行查詢
        Cursor cursor = database.query(
                table, null, where, null, null, null, null, null);
        List l=populate(cursor);
        if(l.size()>0){rtn= (Map)l.get(0);}
        return rtn;
    }
    /**將cursor轉為List of Map*/
    public static List populate(Cursor cursor){
        String []cns=cursor.getColumnNames();
        List rtn=new ArrayList();
        while (cursor.moveToNext()) {
            //result.add(getRecord(cursor));
            Map m=new Hashtable();
            for(int i=0;i<cns.length;i++){
                String columnName=cns[i];
                int columnIndex=cursor.getColumnIndex(columnName);
                int columnType=cursor.getType(columnIndex);
                if(cursor.getString(columnIndex)==null)continue;
                switch(columnType){
                    //case Cursor.FIELD_TYPE_NULL:
                    case Cursor.FIELD_TYPE_INTEGER:
                        m.put( columnName,cursor.getInt(columnIndex));
                        break;
                    case Cursor.FIELD_TYPE_FLOAT:
                        m.put( columnName,cursor.getFloat(columnIndex));
                        break;
                    case Cursor.FIELD_TYPE_STRING:
                        m.put( columnName,cursor.getString(columnIndex));
                        break;
                    case Cursor.FIELD_TYPE_BLOB:
                        m.put( columnName,cursor.getBlob(columnIndex));
                        break;
                }
            }
            rtn.add(m);
        }
        try{cursor.close();}catch (Exception e){}
        return rtn;
    }
    /**將DB 取出的 mVal 放到 context 的view*/
    public static void populate(Map mVal,Context context){
        Activity activity=(Activity)context;
        Set keySet=mVal.keySet();
        for(Object k:keySet){
            if(KEY_ID.equals(k)){
                continue;
            }
            String val=(String)mVal.get(k);
            if(val==null)continue;
            String key=((String) k).replaceFirst(COLUMN_RID_PREFIX,"");
            int id=Integer.parseInt(key);
            View v=activity.findViewById(id);
            boolean blnCheckDone=false;
            for(Object o:_CHECKED_VIEW_CLASS){
                Class c=(Class)o;
                if(c.isInstance(v)){
                    CompoundButton cb=(CompoundButton)v;
                    cb.setChecked("1".equals(val));
                    blnCheckDone=true;
                    break;
                }
            }
            if(blnCheckDone)continue;//避免像Switch同時具有CompoundButton 與 TextView重複執行
            for(Object o:_TEXT_VIEW_CLASS){
                Class c=(Class)o;
                if(c.isInstance(v)){
                    TextView tv=(TextView)v;
                    tv.setText(val);
                    break;
                }
            }
        }
    }
    public static String getViewValById(Map contextView,int rid){
        String rtn=null;
        String RID=COLUMN_RID_PREFIX+rid;
        rtn=(String)contextView.get(RID);
        return rtn;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        for(Object o: tableConstruct.values()){
            String sql=(String)o;
            db.execSQL(sql);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Set ks= tableConstruct.keySet();
        for(Object o:ks){
            String table=(String)o;
            db.execSQL("DROP TABLE IF EXISTS " + table);
            String sql=(String) tableConstruct.get(table);
            db.execSQL(sql);
        }
    }
    /**透過views 產生create SQL*/
    public static String genCreateSQL(String table,List views){
        StringBuffer sb =new StringBuffer();
        sb.append("Create TABLE "+table+"(");
        sb.append(KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT");
        for(Object o:views){
            String column_name=COLUMN_RID_PREFIX+((View)o).getId();
            sb.append(" , " +column_name+"  TEXT  ");
        }
        sb.append(")");
        return sb.toString();
    }
    /**透過Activity 產生 insert 要用的Map*/
    public static Map getViewValMap(Context context){
        return  getViewValMap( (ViewGroup) ((Activity)context).getWindow().getDecorView(), DBSQLiteHelper._VIEW_CLASSES);
    }
    /**透過Activity 產生 insert 要用的Map*/
    public static Map getViewValMap(ViewGroup topViewGroup){
       return  getViewValMap( topViewGroup, DBSQLiteHelper._VIEW_CLASSES);
    }
    /**透過Activity 產生 insert 要用的Map*/
    public static Map getViewValMap(ViewGroup topViewGroup, List classes){
        Map rtn=new Hashtable();
        List <View>columns=new ArrayList();
        genColumn( topViewGroup, classes,  columns);
        for(Object o:columns){
            if (o instanceof TextView){
                rtn.put(COLUMN_RID_PREFIX+((TextView) o).getId(),((TextView) o).getText().toString());
            }
            if (o instanceof Switch){
                String checked=((Switch) o).isChecked()?"1":"0";
                rtn.put(COLUMN_RID_PREFIX+((Switch) o).getId(),checked);
            }
            if (o instanceof CheckBox){
                String checked=((CheckBox) o).isChecked()?"1":"0";
                rtn.put(COLUMN_RID_PREFIX+((CheckBox) o).getId(),checked);
            }
            if (o instanceof RadioButton){
                String checked=((RadioButton) o).isChecked()?"1":"0";
                rtn.put(COLUMN_RID_PREFIX+((RadioButton) o).getId(),checked);
            }
        }
        return rtn;
    }
    /**透過Activity 產生 columns (View)*/
    public static void genColumn(ViewGroup topViewGroup, List classes, List<View> columns) {
        for (int i = 0, N = topViewGroup.getChildCount(); i < N; i++) {
            View child = topViewGroup.getChildAt(i);
            if (child instanceof ViewGroup) {
                genColumn((ViewGroup) child,classes, columns);
            } else if(child.getId()!=-1){
                for(Object o:classes){
                    Class c=(Class)o;
                    if(c.isInstance(child)){
                        columns.add(child);
                        break;
                    }
                }
            }
        }
    }
}
