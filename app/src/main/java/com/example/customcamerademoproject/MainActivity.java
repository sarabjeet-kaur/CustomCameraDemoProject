package com.example.customcamerademoproject;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.customcamerademoproject.utility.AppConstants;

import java.io.File;

import static com.example.customcamerademoproject.utility.AppConstants.REQUEST_OPEN_GALLERY;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView image_view;
    private Button add_image, delete_image;
    public String mediaPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        clickListener();

    }

    //initialise view
    private void initView() {
        image_view = (ImageView) findViewById(R.id.image_view);
        add_image = (Button) findViewById(R.id.add_image);
        delete_image = (Button) findViewById(R.id.delete_image);
    }

    //method for clickListener
    private void clickListener() {
        add_image.setOnClickListener(this);
        delete_image.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        gettingCroppedImage();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete_image:
                deleteImage();
                break;
            case R.id.add_image:
                selectImage();
                break;

        }

    }

    //Method for dialog chooser
    private void selectImage() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.custom_chooser);
        ImageView select_camera = (ImageView) dialog.findViewById(R.id.select_camera);
        ImageView select_gallery = (ImageView) dialog.findViewById(R.id.select_gallery);
        select_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CameraActivity.class));
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


    //method for open gallery
    private void activeGallery() {
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
                    intent.putExtra("tag", "Gallery");
                    startActivityForResult(intent, 100);


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

    //method for get cropped image from crop activity
    private void gettingCroppedImage() {
        Bitmap bitmap = AppConstants.cropped;
        if (bitmap == null) {
            Glide.with(MainActivity.this).load(R.drawable.grey).skipMemoryCache(true).into(image_view);

        } else {
            image_view.setImageBitmap(bitmap);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppConstants.cropped = null;

    }


    //method for delete image from device
    private void deleteImage() {
        try {
            File myFilename = new File(Environment.getExternalStorageDirectory().toString() + "/cropped images");
            if (myFilename.exists()) {
                File mypath = new File(myFilename, "profile.jpg");
                if (mypath.exists())
                    mypath.delete();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Glide.with(MainActivity.this).load(R.drawable.grey).skipMemoryCache(true).into(image_view);

        }

    }
}
