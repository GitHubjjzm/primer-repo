package com.c5durango.alertalsm.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    /*
    * Se encarga de crear la estructura de la base de datos interna.
    * */

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "incluyente.db";
    public static final String TABLE_REPORTES = "t_reportes";

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Crear las tablas
        
        // En esta tabla se guardan todos los reportes generados
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_REPORTES +
                " (" +
                    "id_reporte INTEGER," +
                    "fecha_hora TEXT NOT NULL," +
                    "latitud REAL, " +
                    "longitud REAL," +
                    "lugar TEXT," +
                    "incidente TEXT," +
                    "subtipo TEXT, " +
                    "victimas TEXT," +
                    "descripcion TEXT, " +
                    "imagenes INTEGER DEFAULT 0, " +
                    "audio INTEGER DEFAULT 0," +
                    "video INTEGER DEFAULT 0," +
                    "estatus TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // Cuando cambia la versión se ejecuta este metodo para actualizar las tablas
        sqLiteDatabase.execSQL("DROP TABLE " + TABLE_REPORTES);
        onCreate(sqLiteDatabase);
    }
}
