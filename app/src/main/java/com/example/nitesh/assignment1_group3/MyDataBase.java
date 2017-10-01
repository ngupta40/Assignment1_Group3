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

/**
 * Created by Nitesh Gupta on 01-10-2017.
 */

public class MyDataBase extends SQLiteOpenHelper
{

    private static final String DATABASE_NAME = "Group2_pg_avr.db";
    private static final int DATABASE_VERSION = 1;
    private final String DATABASE_PATH;
    private Context context;

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public MyDataBase(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        DATABASE_PATH = context.getFilesDir().getPath()+"/";
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.w(this.getClass().getSimpleName(), " OnCreate");
    }

    public void createDatabase() {
        SQLiteDatabase sqLiteDatabase = null;
        String database = DATABASE_PATH + DATABASE_NAME;
        try {
            sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(database, null);
            Log.w(this.getClass().getSimpleName(), "Database exists");
        } catch (Exception e) {
            Log.w(this.getClass().getSimpleName(), "Database doesn't exist");
        }
    }

    public void createTable(String name) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("CREATE TABLE IF NOT EXISTS " + name + "(timestamp datetime, xvalue real, yvalue real, zvalue real);");
        Log.w("Table: ", "Creation successful");
        db.close();
    }



    public ArrayList<float[]> fetchLastTenValues(String name){
        try{
            SQLiteDatabase db = getWritableDatabase();
            Cursor cursor =  db.rawQuery("SELECT xvalue, yvalue , zvalue FROM " + name + " ORDER BY TIMESTAMP DESC LIMIT 10;", null);
            float[] xvalues = new float[10];
            float[] yvalues = new float[10];
            float[] zvalues = new float[10];

            cursor.moveToFirst();
            for(int i=9;i>=0;i--) {
                if(!cursor.isAfterLast()){
                    float xvalue = cursor.getFloat(cursor.getColumnIndex("xvalue"));
                    xvalues[i] = (float) xvalue;

                    float yvalue = cursor.getFloat(cursor.getColumnIndex("yvalue"));
                    yvalues[i] = (float) yvalue;

                    float zvalue = cursor.getFloat(cursor.getColumnIndex("zvalue"));
                    zvalues[i] = (float) zvalue;
                    cursor.moveToNext();
                    Log.w("here: ", "testing");
                } else {
                    xvalues[i] = 0;
                    yvalues[i]=0;
                    zvalues[i]=0;
                }
            }
            Log.w("Retrieved: ", "successful");

            ArrayList<float[]> accelArray= new ArrayList<float[]>();
            accelArray.add(xvalues);
            accelArray.add(yvalues);
            accelArray.add(zvalues);
            System.out.println(accelArray);
            return accelArray;
        }
        catch(SQLiteException ex){
            Log.w("Query Exception", "Unable to fetch last 10 records from table "+ name + ";");
            ex.printStackTrace();
        }
        return null;
    }

    public void insertAccelValues(String name, Timestamp timestamp, float x, float y, float z){
        try{
            SQLiteDatabase db = getWritableDatabase();
            Log.w("Insert statement -> ", "Timestamp -> " + timestamp.toString() + " xvalue -> " + x + " yvalue -> " + y + "zvalue -> " + z );
            try{
                ContentValues values = new ContentValues();
                values.put("timestamp", System.currentTimeMillis());
                values.put("xvalue", x);
                values.put("yvalue", y);
                values.put("zvalue", z);
                db.insertOrThrow(name, null, values);

                Cursor cursor = db.rawQuery("SELECT * FROM " + name, null);
                if(cursor.getCount() > 1){
                    Log.w("None Exist in db", "Nothing inserted");
                }
                Log.w("Added: ", "successful");
            }
            catch (SQLiteException ex){
                Log.w("Insert Exception", "Unable to insert to table "+ name + " ->  timestamp: " + timestamp + ", x: "+ x + ", y: "+ y+ ", z: "+ z);
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

