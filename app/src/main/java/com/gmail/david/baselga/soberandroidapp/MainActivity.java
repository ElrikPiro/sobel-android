package com.gmail.david.baselga.soberandroidapp;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
        Button input = findViewById(R.id.SelectImg);
        Button output = findViewById(R.id.ProcessImage);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            String[] projection = {MediaStore.Images.Media.DATA};

            try {
                assert selectedImageUri != null;
                Cursor cursor = getContentResolver().query(selectedImageUri, projection, null, null, null);
                assert cursor != null;
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(projection[0]);
                picturePath = cursor.getString(columnIndex);
                cursor.close();
            }
            catch(Exception e) {
                Log.e("Path Error", e.toString());
            }

            img.setImageURI(selectedImageUri);
            filtered = false;

        }
    }

}
