package com.gmail.david.baselga.soberandroidapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.UserDictionary;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.

    ImageView img;
    String picturePath;
    boolean filtered = true;

    public static final int PICK_IMAGE = 1;

    static {
        System.loadLibrary("sobellib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img = findViewById(R.id.image);
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String sobelFilter(String src);

    public void select(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);

    }

    public void applyFilter(View view) {
        if(img == null || picturePath == null || filtered) {
            return;
        }

        Uri sobeluri = Uri.parse("file://"+sobelFilter(picturePath));

        img.setImageURI(sobeluri);
        filtered = true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();

            File out = new File(Environment.getExternalStorageDirectory().toString()+"/sobelfilter/");
            out.mkdir();
            out = new File(Environment.getExternalStorageDirectory().toString()+"/sobelfilter/"+getNameFromURI(selectedImageUri));
            if(out.exists()) out.delete();
                try {
                    if(!out.createNewFile()) throw new IOException("couldn't create.");
                    InputStream isSource = getContentResolver().openInputStream(selectedImageUri);//new FileInputStream(file).getChannel();
                    BufferedInputStream bis = new BufferedInputStream(isSource);

                    FileOutputStream fw = new FileOutputStream(out);
                    int intchar;
                    while(bis.available() > 0){
                        byte line[] = new byte[bis.available()];
                        bis.read(line);
                        fw.write(line);
                    }
                    fw.close();
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            picturePath = out.getAbsolutePath();

            img.setImageURI(selectedImageUri);
            filtered = false;

        }
    }

    public String getNameFromURI(Uri contentUri)
    {
        //String[] proj = { MediaStore.Images.Media.DISPLAY_NAME };
        Cursor cursor = getContentResolver().query(contentUri, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

 }
