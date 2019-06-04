package com.gmail.david.baselga.soberandroidapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

        /*
        // Example of a call to a native method
        TextView tv = findViewById(R.id.sample_text);
        tv.setText(sobelFilter("/"));
        */

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

        Uri sobeluri = Uri.parse(sobelFilter(picturePath));

        img.setImageURI(sobeluri);
        filtered = true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            String mimetype = MimeTypeMap.getFileExtensionFromUrl(selectedImageUri.getPath());

            File file = new File(selectedImageUri.getPath());
            File out = new File(Environment.getExternalStorageDirectory().toString()+"/input.jpg");
            try {
                copy(file,out);
            } catch (IOException e) {
                e.printStackTrace();
            }
            picturePath = out.getAbsolutePath();

            img.setImageURI(selectedImageUri);
            filtered = false;

        }
    }

    public static void copy(File src, File dst) throws IOException {
        try (InputStream in = new FileInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }

 }
