package com.c5durango.alertalsm.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Nullable;

import com.c5durango.alertalsm.Clases.ModelReportesLocal;

import java.util.ArrayList;

public class DBReportes extends DBHelper {

    /*
    * Aqui se encuentran los métodos relacionados a la tabla de reportes
    * como insertar y mostrar.
    * */


    Context context;
    private String TAG = "DBREPORTES";

    public DBReportes(@Nullable Context context) {
        super(context);
        this.context = context;
    }

    public long insertarReporte(ModelReportesLocal modelReportesLocal) {
        long id = 0;
        try {
            DBHelper dbHelper = new DBHelper(this.context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("id_reporte", modelReportesLocal.getId_reporte());
            values.put("fecha_hora", modelReportesLocal.getFecha_hora());
            values.put("latitud", modelReportesLocal.getLatitud());
            values.put("longitud", modelReportesLocal.getLongitud());
            values.put("lugar", modelReportesLocal.getLugar());
            values.put("incidente", modelReportesLocal.getIncidente());
            values.put("subtipo", modelReportesLocal.getSubtipo());
            values.put("victimas", modelReportesLocal.getVictimas());
            values.put("descripcion", modelReportesLocal.getDescripcion());
            values.put("imagenes", modelReportesLocal.getImagenes());
            values.put("audio", modelReportesLocal.getAudio());
            values.put("video", modelReportesLocal.getVideo());
            values.put("estatus", modelReportesLocal.getEstatus());

            id = db.insert(TABLE_REPORTES, null, values);
        } catch (Exception ex) {
            ex.toString();
        }
        return id;
    }

    public ArrayList<ModelReportesLocal> mostrarReportes() {

        DBHelper dbHelper = new DBHelper(this.context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ArrayList<ModelReportesLocal> listaReportes = new ArrayList<>();
        ModelReportesLocal model = null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_REPORTES, null);
            if (cursor.moveToFirst()) {
                do {
                    model = new ModelReportesLocal();
                    model.setId_reporte(cursor.getInt(0));
                    model.setFecha_hora(cursor.getString(1));
                    model.setLatitud(cursor.getDouble(2));
                    model.setLongitud(cursor.getDouble(3));
                    model.setLugar(cursor.getString(4));
                    model.setIncidente(cursor.getString(5));
                    model.setSubtipo(cursor.getString(6));
                    model.setVictimas(cursor.getString(7));
                    model.setDescripcion(cursor.getString(8));
                    model.setImagenes(cursor.getInt(9));
                    model.setAudio(cursor.getInt(10));
                    model.setVideo(cursor.getInt(11));
                    model.setEstatus(cursor.getString(12));

                    listaReportes.add(model);

                } while (cursor.moveToNext());
            }
            cursor.close();

        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        return listaReportes;
    }

    public ModelReportesLocal mostrarReporte(int id_reporte) {

        DBHelper dbHelper = new DBHelper(this.context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ModelReportesLocal model = null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_REPORTES + " WHERE id_reporte = " + id_reporte, null);
            if (cursor.moveToFirst()) {
                model = new ModelReportesLocal();
                model.setId_reporte(cursor.getInt(0));
                model.setFecha_hora(cursor.getString(1));
                model.setLatitud(cursor.getDouble(2));
                model.setLongitud(cursor.getDouble(3));
                model.setLugar(cursor.getString(4));
                model.setIncidente(cursor.getString(5));
                model.setSubtipo(cursor.getString(6));
                model.setVictimas(cursor.getString(7));
                model.setDescripcion(cursor.getString(8));
                model.setImagenes(cursor.getInt(9));
                model.setAudio(cursor.getInt(10));
                model.setVideo(cursor.getInt(11));
                model.setEstatus(cursor.getString(12));
            }
            cursor.close();
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        return model;
    }
}
