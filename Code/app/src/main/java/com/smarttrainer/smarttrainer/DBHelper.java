package com.smarttrainer.smarttrainer;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.TextView;

/**
 * Created by ld on 3/10/16.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "FeedReader.db";

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE workout_history(timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP PRIMARY KEY,"
                + "formID Integer, reps INTEGER, score FLOAT)");
        db.execSQL("CREATE TABLE form_setting(formID Integer PRIMARY KEY, repsReq INTEGER, freq INTEGER)");
        db.execSQL("INSERT INTO form_setting(formID, repsReq, freq) values(3, 40, 0.5)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS workout_history");
        db.execSQL("DROP TABLE IF EXISTS form_setting");
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        onUpgrade(db, oldVersion, newVersion);
    }

    public static int selectReq(Context context, int id)
    {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectReqFreq = "SELECT repsReq FROM form_setting WHERE formID = ?";
        Cursor cursor = db.rawQuery(selectReqFreq, new String[]{String.valueOf(id)});
        if (cursor != null)
            cursor.moveToFirst();
        return cursor.getInt(0);
    }
}
