package com.messes.panorama.bang;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.Override;

public class MySQLiteHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "pickupDatabase";
    private static final String LINE_TABLE = "lines";
    private static final String KEY_ID = "key";
    private static final String LINE = "line";
    private static final String[] COLUMNS = {KEY_ID, LINE};

    private Context mContext;

    public MySQLiteHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_LINE_TABLE = "CREATE TABLE " + LINE_TABLE + " (" +
                KEY_ID + " INTEGER PRIMARY KEY, " +
                LINE + " TEXT);";
        db.execSQL(CREATE_LINE_TABLE);

        initPopulate(db, mContext);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

        db.execSQL("DROP TABLE IF EXISTS puLine");
        db.setVersion(newVersion);
        this.onCreate(db);
    }

    public void addLine(PickupLine line) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, line.getKey());
        values.put(LINE, line.getLine());
        db.insert(LINE_TABLE, null, values);
        db.close();
    }

    public String getLine(long key) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(LINE_TABLE,
                COLUMNS,
                " key = ?",
                new String[] {String.valueOf(key)},
                null,
                null,
                null,
                null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        return cursor.getString(1);
    }

    public void delete(PickupLine lines){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(LINE_TABLE,KEY_ID + " = ?",new String[] {String.valueOf(lines.getKey())});
        db.close();
    }

    public void initPopulate(SQLiteDatabase db, Context context) {
        ContentValues values = new ContentValues();
        AssetManager am = context.getAssets();

        try {
            InputStream is = am.open("pick_up_lines.txt");
            String line;
            int id = 0;
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            while((line = reader.readLine()) != null) {
                if (line == "") {
                    continue;
                }

                values.put(KEY_ID, id++);
                values.put(LINE, line);
                db.insert(LINE_TABLE, null, values);
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long size() {
        SQLiteDatabase db = this.getWritableDatabase();
        return DatabaseUtils.queryNumEntries(db, LINE_TABLE);
    }
}