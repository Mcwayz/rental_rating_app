package com.example.rentalz.Backend;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;


public class SQLiteHelper extends SQLiteOpenHelper { ;
    public static final String DATABASE_NAME;
    public static final String TABLE_NAME;

    static {
        TABLE_NAME = "RECORD";
        DATABASE_NAME = "RECORDDB.sqlite";
    }

    public SQLiteHelper(Context context,
                        String name,
                        SQLiteDatabase.CursorFactory factory,
                        int version) {
        super(context, name, factory, version);
    }


    public void queryData(String sql) {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }

    //insert data
    public void insertData(String propertyType, String bedrooms, String date, String time, String monthlyRentPrice,
                           String furnitureType,  String notes, String reporterName, byte[] image)
    {
        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO "+TABLE_NAME+" VALUES(NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.bindString(1, propertyType);
        statement.bindString(2, bedrooms);
        statement.bindString(3, date);
        statement.bindString(4, time);
        statement.bindString(5, monthlyRentPrice);
        statement.bindString(6, furnitureType);
        statement.bindString(7, notes);
        statement.bindString(8, reporterName);
        statement.bindBlob(9, image);
        statement.executeInsert();
    }

    //update data
    public void updatePropertyData(String propertyType, String bedrooms, String monthlyRentPrice,
                                 String furnitureType, byte[] image, int id)
    {
        SQLiteDatabase database = getWritableDatabase();
        String sql = "UPDATE RECORD SET propType=?, rooms=?, rentPrice=?, furnType=?, image=? WHERE id=?";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.bindString(1, propertyType);
        statement.bindString(2, bedrooms);
        statement.bindString(3, monthlyRentPrice);
        statement.bindString(4, furnitureType);
        statement.bindBlob(5, image);
        statement.bindDouble(6, id);

        statement.executeInsert();
        database.close();
    }

    public void updateInfo(String trim, String date, String time, String notes, String reporterName, int id)
    {
        SQLiteDatabase database = getWritableDatabase();
        String sql = "UPDATE RECORD SET date=?, time=?, notes=?, reporter=? WHERE id=?";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1,date);
        statement.bindString(2, time);
        statement.bindString(3, notes);
        statement.bindString(4, reporterName);
        statement.bindDouble(5, id);

        statement.executeInsert();
        database.close();
    }

    //deleteData
    public void deleteData (int id) {
        SQLiteDatabase database = getWritableDatabase();
        //query to delete record using id
        String sql = "DELETE FROM RECORD WHERE id=?";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.bindDouble(1, id);

        statement.execute();
        database.close();

    }

    public void insertNote(String addNote, int id){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "UPDATE RECORD SET addNote=? WHERE id=?";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.bindString(1, addNote);
        statement.bindDouble(2, id);
        statement.execute();
        database.close();

    }


    public Cursor getData(String sql) {
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql, null);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
