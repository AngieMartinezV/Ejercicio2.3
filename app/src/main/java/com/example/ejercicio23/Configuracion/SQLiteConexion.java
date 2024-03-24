package com.example.ejercicio23.Configuracion;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLiteConexion extends SQLiteOpenHelper {

    public static final  String nameDataBase = "PhotoBD";
    public static final  int versionDataBase = 1;
    public static final String tableName = "photos";
    public static final String columid = "Id";
    public  static final String columphoto = "photo";
    public static final String columdescription = "description";
    public static final String CreateTable =
            "CREATE TABLE " + tableName + "("+
                    columid + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    columphoto + " BLOB," +
                    columdescription + " TEXT" +
                    ")";
    public static final String DropTablePhotos = "DROP TABLE IF EXISTS " + tableName;
    public static final String SelectTablePhotos= "SELECT * FROM " + tableName;
    public SQLiteConexion(@Nullable Context context, @Nullable SQLiteDatabase.CursorFactory factory) {
        super(context, nameDataBase, factory, versionDataBase);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CreateTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DropTablePhotos);
        onCreate(db);
    }
}
