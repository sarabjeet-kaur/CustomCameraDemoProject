package com.example.customcamerademoproject;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView image_view;
    private Button add_image, delete_image;
    public static final int REQUEST_OPEN_GALLERY = 2;
    String mediaPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        clickListener();
        gettingCroppedImage();


    }

    private void initView() {

        image_view = (ImageView) findViewById(R.id.image_view);
        add_image = (Button) findViewById(R.id.add_image);
        delete_image = (Button) findViewById(R.id.delete_image);

    }

    private void clickListener() {
        add_image.setOnClickListener(this);
        delete_image.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete_image:
                Glide.with(MainActivity.this).load(R.drawable.grey).skipMemoryCache(true).into(image_view);
                break;
            case R.id.add_image:
                selectImage();
                break;

        }

    }

    public void selectImage() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.custom_chooser);
        ImageView select_camera = (ImageView) dialog.findViewById(R.id.select_camera);
        ImageView select_gallery = (ImageView) dialog.findViewById(R.id.select_gallery);
        select_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //activeCamera();
                dialog.dismiss();

            }
        });
        select_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activeGallery();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void activeGallery() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_OPEN_GALLERY);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_OPEN_GALLERY);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_OPEN_GALLERY:
                try {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = MainActivity.this.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    assert cursor != null;
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    mediaPath = cursor.getString(columnIndex);
                    Intent intent = new Intent(MainActivity.this, CropImage.class);
                    intent.putExtra("image", mediaPath);
                    startActivityForResult(intent,100);


                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Please select image", Toast.LENGTH_SHORT).show();

                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_OPEN_GALLERY:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
                    openGalleryIntent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(openGalleryIntent, REQUEST_OPEN_GALLERY);


                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_OPEN_GALLERY);
                }
                break;
        }
    }

    private void gettingCroppedImage() {
        Intent intent=getIntent();
        Bitmap bitmap=(Bitmap)intent.getParcelableExtra("croppedImage");
        if(bitmap==null){
            Glide.with(MainActivity.this).load(R.drawable.grey).skipMemoryCache(true).into(image_view);

        }
        else {
            image_view.setImageBitmap(bitmap);

        }
        Log.e("crop in main",bitmap+"");
    }
}
