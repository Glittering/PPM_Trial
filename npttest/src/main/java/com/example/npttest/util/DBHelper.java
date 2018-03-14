package com.example.npttest.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.npttest.R;
import com.example.npttest.constant.Constant;

import java.io.ByteArrayOutputStream;

/**
 * Created by Administrator on 2017/4/27.
 */

public class DBHelper extends SQLiteOpenHelper {//数据库的辅助类
    private Context context;
    public DBHelper(Context context) {
        super(context, Constant.DB_NAME, null, Constant.VERSION);//创建数据库
        this.context=context;
    }
    //首次创建数据库的时候回调方法
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql="create table "+ Constant.TABLE_USER+"("+ Constant.RRSTRING+" varchar(200))";
        Log.e("TAG","首次创建："+sql);
        //执行sql语句
        db.execSQL(sql);

        String sql_utable="create table "+ Constant.TABLE_UNAME+"("+ Constant.UNAME+" varchar(200),"+ Constant.UTIME+" integer)";
        Log.e("TAG",sql_utable);
        //执行sql语句
        db.execSQL(sql_utable);

        String sql_insert="insert into "+  Constant.TABLE_USER+"("+ Constant.RRSTRING+") values('"+context.getString(R.string.no_note_to_release)+"')";
        db.execSQL(sql_insert);//执行sql语句

        String sql_pic="create table pic(pid integer primary key autoincrement,path blob)";
        db.execSQL(sql_pic);
        Bitmap bitmap= BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,bos);//质量压缩
        ContentValues cv=new ContentValues();
        cv.put("path","");
        long l=db.insert("pic",null,cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("TAG","更新数据库");
        if (oldVersion==1){
            /*String sql_utable="create table "+Constant.TABLE_UNAME+"("+Constant.UNAME+" varchar(200),"+Constant.UTIME+" integer)";
            Log.e("TAG",sql_utable);
            //执行sql语句
            db.execSQL(sql_utable);*/
        }
    }

}
