package com.example.myapp.database;

import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shizhao.czc on 2014/8/25.
 */
public class DBUtil {

    private static final String TAG = "database";

    public static String dropTableSql(Class clazz) {
        StringBuilder sb = new StringBuilder();
        sb.append("DROP TABLE IF EXISTS ");
        sb.append(clazz.getSimpleName());
        sb.append(";");
        return sb.toString();
    }

    public static String createFt3TableSql(Class clz){
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE VIRTUAL TABLE ");
        sb.append(clz.getSimpleName());
        sb.append(" USING fts3 ");
        sb.append(createTableColumn(clz,false));
        return sb.toString();
    }


    public static String createTableSql(Class clazz) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ");
        sb.append(clazz.getSimpleName());
        sb.append(createTableColumn(clazz,true));
        Log.d(TAG, sb.toString());
        return sb.toString();
    }

    private static  String createTableColumn(Class clazz,boolean isNeedAdd_ID){
        Field[] fields = clazz.getFields();
        StringBuilder sb = new StringBuilder();
        sb.append(" ( ");
//		sb.append(BaseColumns._ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT ,");
        List<String> cols = new ArrayList<String>();
        if(isNeedAdd_ID)
            cols.add(BaseColumns._ID+" INTEGER PRIMARY KEY AUTOINCREMENT");
        for (int i=0,len= fields.length;i<len;i++) {
            Field field = fields[i];
            if(field.getType()==String.class&&field.isAnnotationPresent(Column.class)){
                StringBuilder cb = new StringBuilder();
                try {
                    String name= (String)field.get(null);

                    cb.append(name);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    return null;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    return null;
                }
                Column col = field.getAnnotation(Column.class);
                if(col.type()== Column.Type.INTEGER){
                    cb.append(" INTEGER");
                }else if(col.type()== Column.Type.BLOB){
                    cb.append(" BLOB");
                } else{
                    cb.append(" TEXT");
                }
                if(col.notnull()){
                    cb.append(" NOT NULL");
                }
                if(col.unique()){
                    cb.append(" UNIQUE ");
                }
                cols.add(cb.toString());
//				if(i!=len-1){
//					sb.append(",");
//				}
            }
        }
        sb.append(TextUtils.join(",", cols));
        sb.append(");");
        return sb.toString();
    }
}
