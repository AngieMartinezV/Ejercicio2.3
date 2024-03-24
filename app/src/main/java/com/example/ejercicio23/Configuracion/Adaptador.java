package com.example.ejercicio23.Configuracion;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ejercicio23.R;

import java.util.List;

public class Adaptador extends ArrayAdapter<Foto> implements View.OnClickListener {

    private List<Foto> mData;
    private Context context;
    SQLiteConexion conexion;

    public static class ViewHolder{
        ImageView imageProfile;
        TextView txtDescription;
    }

    public Adaptador(@NonNull Context context, List<Foto> mData) {
        super(context,  R.layout.list_item, mData);
        this.mData = mData;
        this.context = context;
    }

    @Override
    public void onClick(View view) {

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Foto photograh = mData.get(position);
        ViewHolder viewHolder;
        View view = convertView;

        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.list_item,null);
        }
        ImageView imagen = view.findViewById(R.id.profile);
        TextView description = view.findViewById(R.id.itemDescription);

        imagen.setImageBitmap(obtenerImagen(photograh.getId()));
        description.setText(photograh.getDescription());

        return view;
    }

    private Bitmap obtenerImagen(String id) {
        conexion = new SQLiteConexion(context, null);
        SQLiteDatabase db = conexion.getReadableDatabase();
        Bitmap bitmap;
        String selectQuery = "SELECT " + SQLiteConexion.columphoto +" FROM " + SQLiteConexion.tableName + " WHERE id = ?";
        // Ejecuta la consulta
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(id)});
        // Verifica si se encontraron resultados
        if (cursor.moveToFirst()) {
            // Obtiene los datos de la imagen en forma de arreglo de bytes
            byte[] imageData = Base64.decode(cursor.getBlob(cursor.getColumnIndexOrThrow("photo")), Base64.DEFAULT);

            // Convierte los datos de la imagen en un objeto Bitmap
            bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        }
        else{
            bitmap = BitmapFactory.decodeResource(Resources.getSystem(),R.drawable.camara);
        }
        // Cierra el cursor y la conexión a la base de datos
        cursor.close();
        db.close();
        return bitmap;
    }


}
