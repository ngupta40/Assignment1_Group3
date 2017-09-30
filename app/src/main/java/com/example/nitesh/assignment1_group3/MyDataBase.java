package com.example.nitesh.assignment1_group3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import java.util.ArrayList;

/**
 * Created by Nitesh on 24-09-2017.
 */

public class MyDataBase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "assignment1_group3";    // Database Name

    private String TABLE_NAME = "";   // Table Name

    private static final int DATABASE_Version = 1;   // Database Version
    private static final String TIMESTAMP = "timestamp";     // Column I (Primary Key)
    private static final String XVal = "XValue";    //Column II
    private static final String YVal= "YValue";    // Column III
    private static final String ZVal= "ZValue";
    private SQLiteDatabase db = null;
    private Context context;

    public MyDataBase(Context context,String dataBase) {
        super(context, dataBase, null, DATABASE_Version);
        this.context=context;
    }

    public void createDataBase(){
        SQLiteDatabase db;
        String path = Environment.getExternalStorageDirectory().getPath();
        String dataBase=path+"/Android/Data/CSE535_ASSIGNMENT2/"+DATABASE_NAME;
        try{
            db=SQLiteDatabase.openOrCreateDatabase(dataBase,null);
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void createTable(String tableName)
    {   SQLiteDatabase db;
        db=getReadableDatabase();
        this.TABLE_NAME = tableName;
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME+
                " ( "+TIMESTAMP+" VARCHAR(255), "+XVal+" float ,"+ YVal+" float , " + ZVal +" float );" ;

        db.execSQL(CREATE_TABLE);

    }
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            System.out.println("OnUpgrade");
            onCreate(db);
        }catch (Exception e) {
            System.out.println(e);
        }
    }

    public long insertData(String TableName, String TimeStamp, String XVal, String YVal, String ZVal)
    {
        SQLiteDatabase dbb = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TIMESTAMP, TimeStamp);
        contentValues.put(XVal, XVal);
        contentValues.put(YVal, YVal);
        contentValues.put(ZVal, ZVal);

        long id = dbb.insert(TableName, null , contentValues);
        return id;
    }

    public ArrayList<float[]> getData(String TableName)
    {
        SQLiteDatabase db = getWritableDatabase();
        String[] columns = {TIMESTAMP , XVal, YVal, ZVal};
        Cursor cursor =db.rawQuery("SELECT XVal,YVal,ZVal FROM "+ TableName + "ORDER BY TIMESTAMP DESC LIMIT 10;",null);
        float[] XValue = new float[10];
        float[] YValue = new float[10];
        float[] ZValue = new float[10];

        cursor.moveToFirst();
        int i =9;
        while(i>0){
            if(!cursor.isAfterLast())
            {
                float xvalue = cursor.getFloat(cursor.getColumnIndex("XValue"));
                XValue[i] = (float) xvalue;
                float yvalue = cursor.getFloat(cursor.getColumnIndex("YValue"));
                YValue[i] = (float) yvalue;
                float zvalue = cursor.getFloat(cursor.getColumnIndex("ZValue"));
                ZValue[i] = (float) zvalue;
                cursor.moveToNext();
            }
            else
            {
                XValue[i] = 0;
                YValue[i] = 0;
                ZValue[i] = 0;
            }
            i--;
        }

        cursor.close();
        ArrayList<float[]> accList = new ArrayList<float[]>();
        accList.add(XValue);
        accList.add(YValue);
        accList.add(ZValue);
        return accList;
    }

}
