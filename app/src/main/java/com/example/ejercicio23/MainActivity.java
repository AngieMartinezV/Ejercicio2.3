package com.example.ejercicio23;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ejercicio23.Configuracion.SQLiteConexion;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    ImageView CapturaImagen;
    ImageButton btnTomarF;
    Button btnSalvarF,btnlistaF;
    EditText Descripcion;
    SQLiteConexion conexion;
    static final  int REQUEST_IMAGE = 100;
    static final  int PETICION_ACCESS_CAM = 200;
    String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CapturaImagen = (ImageView) findViewById(R.id.imagenCaputar);
        btnTomarF = (ImageButton) findViewById(R.id.imgBtnTomarFotografia);
        Descripcion =(EditText) findViewById(R.id.txtDescripcionCaptura);
        btnSalvarF = (Button) findViewById(R.id.btnGuardarFotografia);
        btnlistaF = (Button) findViewById(R.id.btnListaFotografias);

        btnTomarF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permisos();
            }
        });
        btnSalvarF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String datoDescription = Descripcion.getText().toString();
                if(datoDescription.isEmpty()){
                    Descripcion.setError("Ingrese la descripcion de la imagen");
                }else{
                    guardarFotos();
                }
            }

        });
        btnlistaF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityList.class);
                startActivity(intent);
            }
        });
    }

    private void permisos(){
        // METODO PARA LOS PERMISOS DE LA APLICACION
        if(ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA},PETICION_ACCESS_CAM);
        }
        else
        {
            dispatchTakePictureIntent();
            //TomarFoto();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode ==  PETICION_ACCESS_CAM){
            if (grantResults.length>0&&grantResults[0] == PackageManager.PERMISSION_GRANTED){

            }{
                Toast.makeText(getApplicationContext(),"Se necesita permiso de la camara",Toast.LENGTH_LONG);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_IMAGE){
//            Bundle extras = data.getExtras();
//            Bitmap imagen = (Bitmap) extras.get("data");
//            Objetoimagen.setImageBitmap(imagen);
            try {
                File foto = new File(currentPhotoPath);
                    CapturaImagen.setImageURI(Uri.fromFile(foto));
            }
            catch (Exception ex)
            {
                ex.toString();
            }
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.toString();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.ejercicio23_fotografia",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE);
            }
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use  with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private String convertImage64(String path){
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[] imagearray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imagearray,Base64.DEFAULT);
    }



    public void guardarFotos() {
        conexion = new SQLiteConexion(this,null);
        SQLiteDatabase db = conexion.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SQLiteConexion.columphoto, convertImage64(currentPhotoPath));
        values.put(SQLiteConexion.columdescription, Descripcion.getText().toString());
        db.insert(SQLiteConexion.tableName, null, values);
        Toast.makeText(getApplicationContext(), "Registro ingresado",Toast.LENGTH_LONG).show();
        CleanScreen();
        db.close();
    }
    private void CleanScreen() {
        CapturaImagen.setImageResource(R.drawable.camara);
        Descripcion.setText("");
    }

}