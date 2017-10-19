package com.example.nitesh.assignment1_group3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.sql.Timestamp;
import java.util.ArrayList;
import android.os.Environment;

/**
 * Created by Nitesh Gupta on 01-10-2017.
 */

public class MyDataBase extends SQLiteOpenHelper
{
    private static String path = Environment.getExternalStorageDirectory().getPath();
    private static String DATABASE_FILENAME = "assignment2_group3";
    private static final String DATABASE_NAME = path+ "/Android/Data/CSE535_ASSIGNMENT2/" + DATABASE_FILENAME;
    private static final int DATABASE_VERSION = 1;
    private Context context;

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public MyDataBase(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    public void createDatabase() {
        SQLiteDatabase sqLiteDatabase = null;
        String database = DATABASE_NAME;
        try {
            sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(database, null);
        } catch (Exception e) {
            Log.w(this.getClass().getSimpleName(), "Database doesn't exist");
        }
    }

    public void createTable(String name) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("CREATE TABLE IF NOT EXISTS " + name + "(timestamp datetime, xValue real, yValue real, zValue real);");
        db.close();
    }



    public ArrayList<float[]> fetchLastTenValues(String name){
        try{
            SQLiteDatabase db = getWritableDatabase();
            Cursor cursor =  db.rawQuery("SELECT xValue, yValue , zValue FROM " + name + " ORDER BY timestamp DESC LIMIT 10;", null);
            float[] xValues = new float[10];
            float[] yValues = new float[10];
            float[] zValues = new float[10];

            cursor.moveToFirst();
            for(int i=9;i>=0;i--) {
                if(!cursor.isAfterLast()){
                    float xVal = cursor.getFloat(cursor.getColumnIndex("xValue"));
                    xValues[i] = (float) xVal;

                    float yVal = cursor.getFloat(cursor.getColumnIndex("yValue"));
                    yValues[i] = (float) yVal;

                    float zVal = cursor.getFloat(cursor.getColumnIndex("zValue"));
                    zValues[i] = (float) zVal;
                    cursor.moveToNext();
                } else {
                    xValues[i] = 0;
                    yValues[i]=0;
                    zValues[i]=0;
                }
            }

            ArrayList<float[]> accelArr = new ArrayList<float[]>();
            accelArr.add(xValues);
            accelArr.add(yValues);
            accelArr.add(zValues);
            return accelArr;
        }
        catch(SQLiteException ex){
            ex.printStackTrace();
        }
        return null;
    }

    public void insertAccelValues(String name, Timestamp timestamp, float x, float y, float z){
        try{
            SQLiteDatabase db = getWritableDatabase();
            try{
                ContentValues values = new ContentValues();
                values.put("timestamp", System.currentTimeMillis());
                values.put("xValue", x);
                values.put("yValue", y);
                values.put("zValue", z);
                db.insertOrThrow(name, null, values);

                Cursor cursor = db.rawQuery("SELECT * FROM " + name, null);
                if(cursor.getCount() > 1){
                    Log.w("None Exist in db", "Nothing inserted");
                }
            }
            catch (SQLiteException ex){
                ex.printStackTrace();
            }
            finally {
                db.close();
            }
        }
        catch (SQLiteException ex){
            Log.w("DBOpenException -> ", "Unable to open db "+ getDatabaseName());
        }
    }
}